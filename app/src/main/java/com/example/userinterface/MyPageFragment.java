package com.example.userinterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyPageFragment extends Fragment {
    private User currentUser;
    private FragmentMypageBinding binding;
    private FirebaseFirestore db;
    private MyPc myPc;
    private AlertDialog dialog;

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
        MyPc.listen(currentUser.getUid(), new MyPc.OnMyPcLoadedListener() {
            @Override
            public void onLoaded(@Nullable MyPc pc) {
                if (pc == null) return;

                myPc = pc;

                binding.myCpu.setText("CPU : " + myPc.getCpu());
                binding.myGpu.setText("GPU : " + myPc.getGpu());
                binding.myMainboard.setText("메인보드 : " + myPc.getMainboard());
                binding.myRam.setText("RAM : " + myPc.getRam());
                binding.myPower.setText("파워 : " + myPc.getPower());
                binding.myCase.setText("케이스 : " + myPc.getBox());
                binding.myCooler.setText("쿨러 : " + myPc.getCooler());
                binding.myStorage.setText("저장장치 : " + myPc.getStorage());
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "실시간 MyPc 오류", e);
            }
        });
        binding.gotoMyPc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyPcActivity.class);
                intent.putExtra("USER_PROFILE", currentUser);
                startActivity(intent);
            }
        });

        binding.myPageLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(getContext())
                        .setTitle("로그아웃")
                        .setMessage("정말 로그아웃하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("아니요", null)
                        .create();

                dialog.show();
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
