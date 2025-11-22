package com.example.userinterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Locale;

    // Post 모델과 PostViewHolder를 사용하는 FirestoreRecyclerAdapter
public class PostAdapter extends FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder> {
        //클릭 리스너 인터페이스 정의
        // (클릭 시 문서 정보(ID 포함)와 위치를 넘겨줍니다)
        public interface OnItemClickListener {
            void onItemClick(DocumentSnapshot document, int position);
        }
        private OnItemClickListener listener;
        //외부에서 리스너를 설정하는 메서드
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
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
        holder.comments.setText(String.format(Locale.KOREA, "%d", model.getCountComments()));

        // Date 타입을 String으로
        if (model.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            holder.date.setText(sdf.format(model.getTimestamp()));
        } else {
            holder.date.setText("----.--.--");
        }

        // 아이템 클릭 이벤트 연결
        holder.itemView.setOnClickListener(v -> {
            // 리스너가 설정되어 있고, 위치가 유효하다면 실행
            if(listener != null && position != RecyclerView.NO_POSITION){
                // getSnapshots().getSnapshot(position)을 통해 문서 ID가 포함된 스냅샷을 보냅니다.
                listener.onItemClick(getSnapshots().getSnapshot(position), position);
            }
        });
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
        TextView title, category, user, date, views, likes, comments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // list_item_post.xml에 있는 TextView ID 연결
            title = itemView.findViewById(R.id.community_post_title);
            category = itemView.findViewById(R.id.community_post_category);
            user = itemView.findViewById(R.id.community_post_user);
            date = itemView.findViewById(R.id.community_post_date);
            views = itemView.findViewById(R.id.community_post_watch);
            likes = itemView.findViewById(R.id.community_post_thumb);
            comments = itemView.findViewById(R.id.community_post_comment_count);
        }
    }
}