package com.marurun66.postingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.marurun66.postingapp.api.NetworkClient;
import com.marurun66.postingapp.api.PostAPI;
import com.marurun66.postingapp.config.Config;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class AddActivity extends AppCompatActivity {

    private ImageView img;
    private EditText editContent;
    private Button btnSave;

    private File photoFile; // 사진 파일

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 500;

    // ActivityResultLauncher 선언
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> albumLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init();
        imgOnClick();
        setUpActionBar();
        savePost();

        // 카메라 이미지 선택 후 결과 처리
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        photo = rotateBitmap(photo, getPhotoOrientation(photoFile.getAbsolutePath()));
                        img.setImageBitmap(photo);
                    }
                });

        // 앨범 이미지 선택 후 결과 처리
        albumLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri albumUri = result.getData().getData(); // 앨범 이미지 Uri 가져오기
                        String fileName = getFileName(albumUri); // 파일 이름 가져오기

                        try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(albumUri, "r");
                             FileInputStream inputStream = new FileInputStream(pfd.getFileDescriptor());
                             FileOutputStream outputStream = new FileOutputStream(new File(getCacheDir(), fileName))) {

                            IOUtils.copy(inputStream, outputStream);
                            Bitmap photo = BitmapFactory.decodeFile(new File(getCacheDir(), fileName).getAbsolutePath());
                            img.setImageBitmap(photo);
                            photoFile = new File(getCacheDir(), fileName); // photoFile에 경로 설정

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // UI 초기화
    private void init() {
        img = findViewById(R.id.img);
        editContent = findViewById(R.id.editContent);
        btnSave = findViewById(R.id.btnSave);
    }

    // 이미지 클릭 시 카메라 또는 갤러리 선택
    private void imgOnClick() {
        img.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
            builder.setTitle("사진 선택")
                    .setItems(new String[]{"카메라로 찍기", "앨범에서 선택"}, (dialogInterface, i) -> {
                        if (i == 0) {
                            camera(); // 카메라 실행
                        } else if (i == 1) {
                            album(); // 앨범 실행
                        }
                    });
            builder.show();
        });
    }

    // 카메라 실행
    private void camera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            photoFile = getPhotoFile(fileName);

            if (photoFile != null) {
                Uri fileProvider = FileProvider.getUriForFile(this, "com.marurun66.cameraapp.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                cameraLauncher.launch(intent); // startActivityForResult 대신 cameraLauncher 사용
            }
        } else {
            showInstallCameraAppDialog();
        }
    }

    private void showInstallCameraAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle("카메라 앱 설치")
                .setMessage("카메라 앱이 설치되어 있지 않습니다. 설치하시겠습니까?")
                .setPositiveButton("설치", (dialogInterface, i) -> {
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=카메라 앱"));
                    startActivity(marketIntent);
                })
                .setNegativeButton("취소", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    // 앨범 실행
    private void album() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11 이상
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION);
                Toast.makeText(this, "앨범 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else { // Android 10 이하
            if (!checkPermission()) {
                requestPermission();
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // 앨범 앱 실행
        intent.setType("image/*"); // 이미지 파일만 선택 가능
        if (intent.resolveActivity(getPackageManager()) != null) {
            albumLauncher.launch(intent); // startActivityForResult 대신 albumLauncher 사용
        } else {
            Toast.makeText(this, "앨범 앱을 설치해 주세요.", Toast.LENGTH_SHORT).show();
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=갤러리 앱"));
            startActivity(marketIntent);
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
    }

    // 사진 파일 생성
    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 사진의 회전 각도 가져오기
    private int getPhotoOrientation(String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
            return ExifInterface.ORIENTATION_NORMAL;
        }
    }

    // 사진 회전
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;

            default:
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // Uri에서 파일 이름 가져오기
    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            cursor.close();
            return fileName;
        }
        return null;
    }

    // btnSave 클릭 시, 사진과 내용을 서버로 전송
    public void savePost() {
        btnSave.setOnClickListener(view -> {
            String token = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE).getString(Config.TOKEN, "");
            String content = editContent.getText().toString().trim();

            // 사진이 선택되지 않았거나 내용이 비어있을 경우 경고 메시지 출력
            if (photoFile == null || content.isEmpty()) {
                Toast.makeText(this, "사진과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // content를 RequestBody로 변환
            RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);

            // photoFile을 MultipartBody.Part로 변환
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", photoFile.getName(), imageRequestBody);

            // API 호출
            Retrofit retrofit = NetworkClient.getRetrofitClient(this);
            PostAPI postAPI = retrofit.create(PostAPI.class);
            Call<Void> call = postAPI.createPost("Bearer " + token, contentBody, imagePart);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddActivity.this, "포스팅이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddActivity.this, "포스팅 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    Toast.makeText(AddActivity.this, "네트워크 요청에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // ActionBar 설정
    public void setUpActionBar() {
        getSupportActionBar().setTitle("포스팅");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
