package com.marurun66.postingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.marurun66.postingapp.api.NetworkClient;
import com.marurun66.postingapp.api.UserAPI;
import com.marurun66.postingapp.model.LoginRequest;
import com.marurun66.postingapp.model.LoginResponse;
import com.marurun66.postingapp.util.EmailValidator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    EditText editPassword2;
    Button btnSignup;
    TextView txtLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

    }
    //UI 초기화
    public void init(){
        editEmail=findViewById(R.id.editEmail);
        editPassword=findViewById(R.id.editPassword);
        editPassword2=findViewById(R.id.editPassword2);
        btnSignup=findViewById(R.id.btnSignUp);
        txtLogin=findViewById(R.id.txtLogin);
    }

    //회원가입 버튼 누르면 회원가입 처리
    public void signUpOnClick() {
        btnSignup.setOnClickListener(view -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            String password2 = editPassword2.getText().toString();

            //이메일과 비밀번호가 입력되지 않았을 때
            if (email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            //이메일 양식 확인
            if (!EmailValidator.isValidEmail(email)) {
                Toast.makeText(SignUpActivity.this, "이메일 형식이 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                return;
            }
            //비밀번호 일치 확인
            if (!password.equals(password2)) {
                Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                return;
            }
            //회원가입 API 호출
            Retrofit retrofit = NetworkClient.getRetrofitClient(this);
            UserAPI userAPI = retrofit.create(UserAPI.class);
            LoginRequest signUpRequest = new LoginRequest(email, password);
            Call<Void> call = userAPI.signUp(signUpRequest);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    Toast.makeText(SignUpActivity.this, "서버와 통신 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }






}