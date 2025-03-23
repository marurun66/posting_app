package com.marurun66.postingapp.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marurun66.postingapp.R;
import com.marurun66.postingapp.api.LikesAPI;
import com.marurun66.postingapp.api.NetworkClient;
import com.marurun66.postingapp.config.Config;
import com.marurun66.postingapp.model.Post;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    //5. 이 어댑터 클래스의 멤버 변수를 만든다.
    Context context;
    ArrayList<Post> postArrayList;


    //6. 생성자를 만든다.
    public PostAdapter(Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post=postArrayList.get(position);
        //데이터를 꺼내고, 화면(홀더)에 표시
        holder.txtContent.setText(post.content);
        holder.txtEmail.setText(post.email);
        holder.txtCreatedAt.setText(post.createdAt);
        Glide.with(context).load(post.imageUrl).into(holder.imgPost);

        // 좋아요 상태에 따라 아이콘 변경
        if (post.isLiked) {
            // 좋아요가 눌린 상태 (까만색 엄지척 아이콘)
            holder.imgBtnLike.setImageResource(R.drawable.thumb_up_black);  // 아이콘 변경
        } else {
            // 좋아요가 안 눌린 상태 (흰색 엄지척 아이콘)
            holder.imgBtnLike.setImageResource(R.drawable.thumb_up_white);  // 아이콘 변경
        }
        // 좋아요 버튼 클릭 시 동작 추가
        holder.imgBtnLike.setOnClickListener(v -> {
            String token = context.getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE)
                    .getString(Config.TOKEN, null); // SharedPreferences에서 토큰 가져오기

            if (post.isLiked) {
                // 좋아요 취소
                post.isLiked = false;
                holder.imgBtnLike.setImageResource(R.drawable.thumb_up_white); // 아이콘 변경
                // 서버에 좋아요 취소 요청 보내는 로직 추가
                Retrofit retrofit = NetworkClient.getRetrofitClient(context);
                LikesAPI likesAPI = retrofit.create(LikesAPI.class);
                Call<Void> call = likesAPI.unlikePost("Bearer " + token, post.id);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // 좋아요 취소 성공
                            post.isLiked = false;
                            notifyDataSetChanged();
                        } else {
                            // 좋아요 취소 실패
                            post.isLiked = true;
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable throwable) {
                        Log.e("PostAdapter", "Error: " +  throwable.getMessage());
                    }
                });
            } else {
                // 좋아요
                post.isLiked = true;
                holder.imgBtnLike.setImageResource(R.drawable.thumb_up_black); // 아이콘 변경
                // 서버에 좋아요 추가 요청 보내는 로직 추가
                Retrofit retrofit = NetworkClient.getRetrofitClient(context);
                LikesAPI likesAPI = retrofit.create(LikesAPI.class);
                Call<Void> call = likesAPI.likePost("Bearer " + token, post.id);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // 좋아요 성공
                            Log.d("PostAdapter", "Liked post successfully.");
                        } else {
                            // 좋아요 실패
                            Log.d("PostAdapter", "Failed to like post.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable throwable) {
                        Log.e("PostAdapter", "Error: " +  throwable.getMessage());
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }
    //1.어뎁터 상속 받는다. //4. 만든 뷰 홀더 적어준다.

    //2. 뷰홀더 클래스만든다.
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPost;
        TextView txtContent;
        TextView txtEmail;
        TextView txtCreatedAt;
        ImageButton imgBtnLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPost=itemView.findViewById(R.id.imgPost);
            txtContent=itemView.findViewById(R.id.txtContent);
            txtEmail=itemView.findViewById(R.id.txtEmail);
            txtCreatedAt=itemView.findViewById(R.id.txtCreatedAt);
            imgBtnLike=itemView.findViewById(R.id.imgBtnLike);
        }
    }

}
