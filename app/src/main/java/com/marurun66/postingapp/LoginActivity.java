package com.marurun66.postingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.marurun66.postingapp.api.NetworkClient;
import com.marurun66.postingapp.api.UserAPI;
import com.marurun66.postingapp.config.Config;
import com.marurun66.postingapp.model.LoginRequest;
import com.marurun66.postingapp.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    TextView txtSignUp;
    EditText editEmail;
    EditText editPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        loginOnClick();
        signUpOnClick();
        setUpActionBar();
    }

    //UI 초기화
    public void init(){
        txtSignUp=findViewById(R.id.txtLogin);
        editEmail=findViewById(R.id.editEmail);
        editPassword=findViewById(R.id.editPassword);
        btnLogin=findViewById(R.id.btnSignUp);
    }

    //로그인 버튼 누르면 로그인 처리
    public void loginOnClick() {
        btnLogin.setOnClickListener(view -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            //이메일과 비밀번호가 입력되지 않았을 때
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            //로그인 API 호출
            Retrofit retrofit = NetworkClient.getRetrofitClient(this);
            UserAPI userAPI = retrofit.create(UserAPI.class);
            Call<LoginResponse> call = userAPI.login(new LoginRequest(email, password));
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        //로그인 성공시 jwt 토큰 저장
                        getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE)
                                .edit()
                                .putString(Config.TOKEN, loginResponse.getToken())
                                .apply();
                        Log.i("LoginActivity", "onResponse: " + loginResponse.getToken());
                        //메인 액티비티로 이동
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        //로그인 실패시 토스트메세지 출력
                        Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                    @Override
                    public void onFailure (Call < LoginResponse > call, Throwable throwable){
                    Toast.makeText(LoginActivity.this,"서버와 통신 중 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                    }

            });
        });
    }


    //액션바 설정
    public void setUpActionBar(){
        getSupportActionBar().setTitle("포스팅 앱");
    }
    //회원가입 텍스트 누르면 회원가입 액티비티로 이동
    public void signUpOnClick(){
        txtSignUp.setOnClickListener(view -> {
            Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(intent);
        });
    }

}