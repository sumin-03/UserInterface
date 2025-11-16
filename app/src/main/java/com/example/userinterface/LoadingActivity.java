package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityLoadingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoadingActivity extends AppCompatActivity {


    private ActivityLoadingBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        loadUserProfileAndStartNextActivity();
    }

    private void loadUserProfileAndStartNextActivity(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        //로그인 상태인지 확인
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            // "users" 컬렉션에서 "uid"를 문서 ID로 하는 문서를 가져옴
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // 문서 가져오기 성공
                        if (documentSnapshot.exists()) {
                            // 문서를 User.java 객체로 자동 변환
                            User user = documentSnapshot.toObject(User.class);

                            if (user != null) {
                                //홈 액티비티로 이동할 intent
                                Intent intent = new Intent(LoadingActivity.this, HomeActivity.class);

                                //intent에 user 추가
                                intent.putExtra("USER_PROFILE", user);

                                startActivity(intent);
                                finish(); //Home으로 이동
                            }
                        } else {
                            // 문서가 존재하지 않음
                            Log.w(TAG, "User document does not exist for UID: " + uid);
                            goToLogin("프로필 정보를 찾을 수 없습니다.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // DB 접근 실패
                        Log.w(TAG, "Error getting user document", e);
                        goToLogin("프로필 로드에 실패했습니다.");
                    });
        } else {
            // 로그아웃 상태
            Log.w(TAG, "No user is logged in.");
            goToLogin("로그인이 필요합니다.");
        }
    }

    private void goToLogin(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        mAuth.signOut();

        startActivity(new Intent(LoadingActivity.this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }


}