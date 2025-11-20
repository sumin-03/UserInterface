package com.example.userinterface;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private String uid;
    private String nickname; //닉네임
    private String email; // '아이디'를 이메일로 사용
    private long level; // 커뮤니티 레벨
    private long experience; // 커뮤니티 경험치
    private @ServerTimestamp Date joinDate; // 가입 날짜 (서버 시간 자동 입력)

    //Firestore가 사용할 빈 생성자
    public User() {
    }

    // 회원가입 시 사용할 생성자
    public User(String uid, String nickname, String email) {
        this.uid = uid;
        this.nickname = nickname;
        this.email = email;
        this.level = 1;         // 가입 시 레벨 1
        this.experience = 0;    // 가입 시 경험치 0
    }

    // Firebase 용 Getter 추가
    public String getUid() { return uid; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public long getLevel() { return level; }
    public long getExperience() { return experience; }
    public Date getJoinDate() { return joinDate; }



    // Setter 추가
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setLevel(long level) {
        this.level = level;
    }
    public void setExperience(long experience) {
        this.experience = experience;
    }
}