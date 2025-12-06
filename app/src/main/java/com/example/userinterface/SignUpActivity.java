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
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private FirebaseAuth mAuth;

    private static final String TAG = "SignUp";

    private FirebaseFirestore db;


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
        db = FirebaseFirestore.getInstance();

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
            String nickname = binding.nickname.getText().toString().trim(); //nickname 초기값

            // ---  유효성 검사 (Validation) ---

            // 기본 항목 비어있는지 검사
            if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || nickname.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            //이메일 형식인지 체크
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.signUpEmail.setError("올바른 이메일 형식이 아닙니다.");
                binding.signUpEmail.requestFocus();
                return;
            }

            if(nickname.length() > 6 || nickname.length() < 2){
                binding.nickname.setError("닉네임은 2글자 이상 6글자 이하이어야 합니다.");
                binding.nickname.requestFocus();
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

            db.collection("users").whereEqualTo("nickname", nickname).get() //닉네임이 같은 지 확인
                            .addOnCompleteListener(nicknameTask -> {
                                if(nicknameTask.isSuccessful()){
                                    if(nicknameTask.getResult().isEmpty()){
                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // 회원가입 성공
                                                            Log.d(TAG, "createUserWithEmail:success");
                                                            FirebaseUser user = mAuth.getCurrentUser();

                                                            if (user != null) {
                                                                String uid = user.getUid();
                                                                MyPc myPc = new MyPc("나만의 cpu", "나만의 gpu", "나만의 메인보드", "나만의 ram", "나만의 파워", "나만의 케이스", "나만의 쿨러", "나만의 저장소", uid);
                                                                db.collection("mypcs")
                                                                        .document(uid)
                                                                        .set(myPc)
                                                                        .addOnSuccessListener(unused -> {
                                                                            Log.d(TAG, "mypcs created successfully");
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            Log.e(TAG, "mypcs create failed", e);
                                                                        });

                                                                User newUser = new User(uid, nickname, email);
                                                                //유저 정보 저장
                                                                db.collection("users").document(uid).set(newUser)
                                                                        .addOnSuccessListener(  aVoid -> {
                                                                            // (저장 성공 시) 이메일 인증 발송
                                                                            Log.d(TAG, "User profile saved to Firestore.");
                                                                            Toast.makeText(SignUpActivity.this, "회원가입 성공! 이메일 인증을 해주세요", Toast.LENGTH_LONG).show();
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
                                                                                            finish(); // 회원가입 화면 종료
                                                                                        }
                                                                                    });

                                                                        }).addOnFailureListener(e -> {
                                                                            // (저장 실패 시)
                                                                            Log.w(TAG, "Error saving user profile", e);
                                                                            Toast.makeText(SignUpActivity.this, "오류: 프로필 저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                                                            user.delete();
                                                                        });
                                                            }
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
                                    } else{
                                        //닉네입 이미 존재
                                        Log.w(TAG, "Nickname already Exist");
                                        binding.nickname.setError("이미 존재하는 닉네임입니다.");
                                        binding.nickname.requestFocus();
                                    }
                                }else {
                                    //닉네임 확인 실패
                                    Log.w(TAG, "Error checking nickname", nicknameTask.getException());
                                    Toast.makeText(this, "닉네임 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });

        });

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
    }
}