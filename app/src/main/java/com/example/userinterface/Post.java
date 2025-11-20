package com.example.userinterface;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Post {

    // 게시글 내용
    private String title;       // 제목
    private String content;     // 본문 내용
    private String category;    // 카테고리

    // 작성자 정보
    private String userId;      // 작성자의 고유 ID
    private String userName;    // 작성자 닉네임

    private @ServerTimestamp Date timestamp; // 서버 시간 기준 작성 시간
    private long views;         // 조회수
    private long likes;         // 추천수

    // Firestore가 쓸 빈 생성자
    public Post() {
    }

    public Post(String title, String content, String category, String userId, String userName) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.userId = userId;
        this.userName = userName;
        this.views = 0;
        this.likes = 0;
    }


    // Firebase 용 Getter 추가
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public Date getTimestamp() { return timestamp; }
    public long getViews() { return views; }
    public long getLikes() { return likes; }
}