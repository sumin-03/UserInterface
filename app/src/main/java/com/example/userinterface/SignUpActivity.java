package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;



public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private FirebaseAuth mAuth;

    private static final String TAG = "SignUp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //FirebaseAuth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        binding.signUpEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String email = binding.signUpEmail.getText().toString().trim();

                    // 유효한 이메일 형식인지 검사
                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        binding.failedEmail.setVisibility(View.VISIBLE);
                    } else{
                        binding.failedEmail.setVisibility(View.GONE);
                    }
                }
            }
        });

        binding.signUpBtn.setOnClickListener(v ->{

            // --- 입력값 가져오기 ---
            String email = binding.signUpEmail.getText().toString().trim();
            String password = binding.signUpPassword.getText().toString().trim();
            String passwordConfirm = binding.checkPassword.getText().toString().trim(); // 비밀번호 확인

            // ---  유효성 검사 (Validation) ---

            // 기본 항목 비어있는지 검사
            if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            //이메일 형식인지 체크
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                return;
            }

            //  비밀번호 일치 여부 검사
            if (!password.equals(passwordConfirm)) {
                binding.checkPassword.setError("비밀번호가 일치하지 않습니다.");
                binding.checkPassword.requestFocus();
                return;
            }

            // 비밀번호 조건 (예: 8자리 이상, 숫자 1개 이상, 문자 1개 이상)
            if (password.length() < 8) {
                binding.signUpPassword.setError("비밀번호는 8자리 이상이어야 합니다.");
                binding.signUpPassword.requestFocus();
                return;
            }
            if (!password.matches(".*[A-Za-z].*")) {
                binding.signUpPassword.setError("비밀번호에 문자가 1개 이상 포함되어야 합니다.");
                binding.signUpPassword.requestFocus();
                return;
            }
            if (!password.matches(".*[0-9].*")) {
                binding.signUpPassword.setError("비밀번호에 숫자가 1개 이상 포함되어야 합니다.");
                binding.signUpPassword.requestFocus();
                return;
            }

            binding.signUpPassword.setError(null);
            binding.checkPassword.setError(null);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // 회원가입 성공
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) {
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Email verification sent.");
                                                        // (메일 발송 성공)
                                                    } else {
                                                        Log.e(TAG, "sendEmailVerification", task.getException());
                                                        // (메일 발송 실패)
                                                    }
                                                }
                                            });
                                }
                                finish(); // 회원가입 화면 종료
                            } else {
                                // 회원가입 실패
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                // 이메일이 이미 존재하여 충돌이 발생한 경우
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    binding.signUpEmail.setError("이미 사용 중인 이메일입니다.");
                                    binding.signUpEmail.requestFocus();
                                }
                            }
                        }
                    });
        });

        binding.backButton.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }
}