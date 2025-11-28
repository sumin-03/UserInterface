package com.example.userinterface;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {
    private String userName;

    private String userId;

    private int userLevel;

    private String commentText;

    private @ServerTimestamp Date timestamp; // 서버 시간 기준 작성 시간

    public Comment() {//파이어베이스가 쓸 생성자
    }

    public Comment(String userName,  String userId, int userLevel, String commentText){
        this.userName = userName;
        this.userId = userId;
        this.userLevel = userLevel;
        this.commentText = commentText;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getUserId() {
        return userId;
    }
    public Date getTimestamp() {
        return timestamp;
    }
}
