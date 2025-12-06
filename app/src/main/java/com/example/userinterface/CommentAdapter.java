package com.example.userinterface;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {
    private String currentUserId;

    public interface onCommentDeleteListener{
        void onDeleteClick(DocumentSnapshot snapshot);
    }

    public onCommentDeleteListener deleteListener;

    public void setOnCommentDeleteListener(onCommentDeleteListener listener){
        this.deleteListener = listener;
    }
    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
        // 어댑터 생성 시 현재 내 ID 가져오기
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }


    //ViewHolder class
    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView userName, commentContent, commentDate;
        TextView userLevel;
        ImageView commentDelete;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            // list_item_post.xml에 있는 TextView ID 연결
            userName = itemView.findViewById(R.id.comment_user);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentDate = itemView.findViewById(R.id.comment_date);
            userLevel = itemView.findViewById(R.id.user_level);
            commentDelete = itemView.findViewById(R.id.comment_delete);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder (CommentViewHolder viewHolder, int position, @NonNull Comment model) {
        viewHolder.commentContent.setText(model.getCommentText());
        viewHolder.userName.setText(model.getUserName());


        // 댓글에 저장된(작성 당시) 레벨로 즉시 표시
        setLevelEmoji(viewHolder.userLevel, model.getUserLevel());


        // Date 타입을 String으로
        if (model.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM.dd HH:mm", Locale.KOREA);
            viewHolder.commentDate.setText(sdf.format(model.getTimestamp()));
        } else {
            viewHolder.commentDate.setText(".. ..:..");
        }

        // 작성자 본인 확인
        if(currentUserId != null && currentUserId.equals(model.getUserId())){
            // 내 댓글이면 삭제 버튼 보이기
            viewHolder.commentDelete.setVisibility(View.VISIBLE);

            //삭제 버튼 이벤트 설정
            viewHolder.commentDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(deleteListener != null){
                        // 현재 위치의 문서 스냅샷(ID 포함)을 보냄
                        deleteListener.onDeleteClick(getSnapshots().getSnapshot(position));
                    }
                    else{
                        // 남의 댓글이면 숨기기
                        viewHolder.commentDelete.setVisibility(View.GONE);
                        viewHolder.commentDelete.setOnClickListener(null); // 리스너 해제
                    }
                }
            });
        }
    }

    private void setLevelEmoji(TextView textView, int level) {
        String levelEmoji;
        if (level >= 30) {
            levelEmoji = "🍗"; // 30레벨 이상
        } else if (level >= 20) {
            levelEmoji = "🐓"; // 20 ~ 29레벨
        } else if (level >= 10) {
            levelEmoji = "🐥"; // 10 ~ 19레벨
        } else {
            levelEmoji = "🥚"; // 1 ~ 9레벨 (그 외)
        }
        textView.setText(levelEmoji);
    }
}
