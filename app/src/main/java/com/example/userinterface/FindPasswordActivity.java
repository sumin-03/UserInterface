package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivityFindPasswordBinding; // 1. 바인딩 임포트
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindPasswordActivity extends AppCompatActivity {

    private ActivityFindPasswordBinding binding; //  바인딩 변수
    private FirebaseAuth mAuth;
    private static final String TAG = "FindPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindPasswordBinding.inflate(getLayoutInflater()); // 3. 바인딩 초기화
        setContentView(binding.getRoot());

        // FirebaseAuth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        // 뒤로가기 버튼 리스너
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        // 재설정 메일 발송 버튼 리스너
        binding.sendResetEmailButton.setOnClickListener(v -> {
            sendPasswordResetEmail();
        });
    }

    private void sendPasswordResetEmail() {
        String email = binding.emailEditText.getText().toString().trim();

        // 이메일 유효성 검사
        if (email.isEmpty()) {
            binding.emailEditText.setError("이메일을 입력해주세요.");
            binding.emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("유효한 이메일 형식이 아닙니다.");
            binding.emailEditText.requestFocus();
            return;
        }

        // 유효성 검사 통과 시 에러 메시지 제거
        binding.emailEditText.setError(null);

        // 2. Firebase로 비밀번호 재설정 이메일 발송
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //실제 있는 이메일이 아니어도 보냈다고 처리
                        if (task.isSuccessful()) {
                            Log.d(TAG, "재설정 이메일 발송 성공");
                            binding.failedSend.setVisibility(View.GONE);
                            // 3. 성공 UI 처리
                            binding.successMessage.setVisibility(View.VISIBLE);
                        } else {
                            Log.w(TAG, "재설정 이메일 발송 실패", task.getException());
                            // 4. 실패 UI 처리 (가입되지 않은 이메일 등)
                            binding.failedSend.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}