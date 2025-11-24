package com.example.userinterface;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.userinterface.databinding.FragmentMypageBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyPageFragment extends Fragment {
    private User currentUser;
    private FragmentMypageBinding binding;
    private String TAG = "MYPAGE";
    public static MyPageFragment newInstance(User user) {
        MyPageFragment fragment = new MyPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_PROFILE", user);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            currentUser = getArguments().getSerializable("USER_PROFILE", User.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMypageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayUserProfile();
        //방송 듣기 설정
        new ViewModelProvider(requireActivity()).get(UserViewModel.class).getUser().observe(getViewLifecycleOwner(), updatedUser -> {
            //user 정보가 바뀔 때 실행
            if (updatedUser != null) {
                //현재 프래그먼트의 변수 업데이트
                currentUser = updatedUser;
                Log.d(TAG, "UserUpdate");

                //User 프로필 재설정
                displayUserProfile();
            }
        });
    }


    private void displayUserProfile() {
        if (currentUser != null) {
            binding.homeUsername.setText(currentUser.getNickname());
            binding.homeLevel.setText("Lv." + currentUser.getLevel());
            binding.homeExperienceBar.setMax(100);
            binding.homeExperienceBar.setProgress((int) currentUser.getExperience());
            binding.homeExperiencePoints.setText(currentUser.getExperience() + "/100");
            binding.homeWelcome.setText("안녕하세요, " + currentUser.getNickname() + "님!");

            if (currentUser.getJoinDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 가입", Locale.KOREA);
                String formattedDate = sdf.format(currentUser.getJoinDate());
                binding.homeJoinDate.setText(formattedDate);
            }
            Log.d(TAG, "load user profile");

        } else {
            Toast.makeText(getContext(), "프로필 로드 실패", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "failed load profile");
        }
    }
}
