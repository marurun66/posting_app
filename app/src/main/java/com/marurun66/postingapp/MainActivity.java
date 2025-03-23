package com.marurun66.postingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marurun66.postingapp.adapter.PostAdapter;
import com.marurun66.postingapp.api.NetworkClient;
import com.marurun66.postingapp.api.PostAPI;
import com.marurun66.postingapp.config.Config;
import com.marurun66.postingapp.model.Post;
import com.marurun66.postingapp.model.PostListResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    Button btnAdd;

    String token ="";
    int page=1;
    int size=10;
    int count;

    PostAdapter adapter;
    ArrayList<Post> postArrayList=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate called");
        noJwt();
        init();
        setUpActionBar();
        addListPost();
        addPost();

    }

    //UI 초기화
    public void init(){
        btnAdd=findViewById(R.id.btnAdd);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter=new PostAdapter(MainActivity.this,postArrayList);
        recyclerView.setAdapter(adapter);
        Log.d("MainActivity", "RecyclerView initialized.");
    }

    //btnAdd 클릭시 PostAddActivity로 이동
    public void addPost(){
        btnAdd.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,AddActivity.class);
            startActivity(intent);
        });
    }

    //jwt 토큰이 없으면 로그인 액티비티 이동
    void noJwt() {
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString(Config.TOKEN, ""); //토큰이 없으면 빈문자열이 반환됨
        Log.i("MainActivity", "token: " + token);
        if (token.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //포스트 리스트 가져오기
    public void getPostList(){
        Retrofit retrofit= NetworkClient.getRetrofitClient(this);
        PostAPI postAPI=retrofit.create(PostAPI.class);
        Call<PostListResponse> call=postAPI.getPostingList("Bearer "+token,page,size);
        call.enqueue(new Callback<PostListResponse>() {
            @Override
            public void onResponse(Call<PostListResponse> call, Response<PostListResponse> response) {
                if(response.isSuccessful()){
                    if(page == 1){
                        postArrayList.clear();
                    }
                    postArrayList.addAll(response.body().postsList);
                    adapter.notifyDataSetChanged();
                    count=response.body().count;
                    page++;
                    Log.d("MainActivity", "Data added. Total posts: " + postArrayList.size());
                } else {
                    // HTTP 상태 코드 및 응답 메시지 로그
                    Log.e("MainActivity", "포스트 리스트 가져오기 실패. HTTP 코드: " + response.code());
                    Log.e("MainActivity", "Error Message: " + response.message());
                }
            }


            @Override
            public void onFailure(Call<PostListResponse> call, Throwable throwable) {
                Log.e("MainActivity","네트워크 요청 실패");
                Toast.makeText(MainActivity.this,"네트워크 요청에 실패했습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //스크롤 내리면 추가데이터를 가져오는 함수
    public void addListPost() {
        Log.i("MainActivity", "Scroll listener added.");
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.i("MainActivity", "Scrolled.");
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();
                Log.i("MainActivity", "Last position: " + lastPosition + ", Total count: " + totalCount);
                // 마지막 아이템에 도달했을 때 추가 데이터를 요청
                if (lastPosition + 1 == totalCount) {
                    if (count < size) {
                        Toast.makeText(MainActivity.this, "마지막 데이터입니다. 총 개수: " + totalCount, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("MainActivity", "Fetching more posts...");
                        getPostList();  // 데이터를 가져오는 함수 호출
                    }

                }
            }
        });
    }

    //액션바 설정
    public void setUpActionBar(){
        getSupportActionBar().setTitle("포스팅 앱");
    }
    //액션바 버튼 불러오기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //액션바 로그아웃 버튼 클릭시 로그아웃
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btnLogout) {
            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(Config.TOKEN);
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // 액티비티가 다시 시작될 때 호출되는 함수
    @Override
    protected void onResume() {
        super.onResume();
        // 페이지 초기화: 첫 번째 페이지부터 데이터를 다시 불러옴
        page = 1;  // 페이지를 1로 초기화
        postArrayList.clear();  // 이전 데이터 초기화
        getPostList();  // 데이터를 다시 불러오기
        addListPost();  // 추가 데이터 로드 리스너
    }
}