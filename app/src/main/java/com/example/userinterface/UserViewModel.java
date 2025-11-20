package com.example.userinterface;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    // 유저 정보를 담는 '살아있는 데이터' (값이 바뀌면 다 알려줌)
    private final MutableLiveData<User> user = new MutableLiveData<>();

    // 정보 세팅하기 (글쓰기 화면 등에서 호출)
    public void setUser(User userConfig) {
        user.setValue(userConfig);
    }

    // 정보 가져오기 (홈, 마이페이지 등에서 관찰용)
    public LiveData<User> getUser() {
        return user;
    }
}