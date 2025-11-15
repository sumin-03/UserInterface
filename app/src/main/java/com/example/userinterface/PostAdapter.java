package com.example.userinterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import java.text.SimpleDateFormat;
import java.util.Locale;

    // Post 모델과 PostViewHolder를 사용하는 FirestoreRecyclerAdapter
public class PostAdapter extends FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder> {

    public PostAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
        super(options);
    }

    // list_item_post.xml의 뷰들에 데이터 연결
    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
        // Post 객체에서 데이터를 가져와 PostViewHolder의 TextView에 설정
        holder.title.setText(model.getTitle());
        holder.category.setText(model.getCategory());
        holder.user.setText(model.getUserName());

        // long 타입을 String으로
        holder.views.setText(String.format(Locale.KOREA, "조회 : %d", model.getViews()));
        holder.likes.setText(String.format(Locale.KOREA, "추천 : %d", model.getLikes()));

        // Date 타입을 String으로
        if (model.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            holder.date.setText(sdf.format(model.getTimestamp()));
        } else {
            holder.date.setText("----.--.--");
        }
    }

    // ViewHolder 생성
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_post, parent, false);
        return new PostViewHolder(view);
    }

    // ViewHolder 클래스
    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, category, user, date, views, likes;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // list_item_post.xml에 있는 TextView ID 연결
            title = itemView.findViewById(R.id.community_post_title);
            category = itemView.findViewById(R.id.community_post_category);
            user = itemView.findViewById(R.id.community_post_user);
            date = itemView.findViewById(R.id.community_post_date);
            views = itemView.findViewById(R.id.community_post_watch);
            likes = itemView.findViewById(R.id.community_post_thumb);

            // TODO: 아이템 클릭 리스너 (게시글 보기)
            //itemView.setOnClickListener(v -> {});
        }
    }
}