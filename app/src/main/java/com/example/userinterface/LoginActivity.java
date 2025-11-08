package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth; //firebase
    private static final String TAG = "Login"; //login tag

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //FirebaseAuth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        //로그인 버튼 클릭리스너
        binding.loginBtn.setOnClickListener(v -> {
            // 사용자가 입력한 아이디와 비밀번호 가져오기
            String email = binding.loginEmail.getText().toString().trim();
            String password = binding.loginPassword.getText().toString().trim();

            // 아이디 또는 비밀번호가 비어있는지 확인
            if (email.isEmpty() || password.isEmpty()) {
                binding.failedLogin.setVisibility(View.VISIBLE); // 실패 메시지 보이기
                binding.failedLogin.setText("아이디 또는 비밀번호를 잘못 입력하셨습니다."); // 오류 메시지 변경
                return; //이전으로
            }

            // [Firebase] 이메일과 비밀번호로 로그인
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // 로그인 성공
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user != null){
                                    if (user.isEmailVerified()) {
                                        // ✅ 2-1. 인증 완료
                                        Log.d(TAG, "Email is verified.");
                                        // TODO: 홈 회면 만들고 Toast 삭제
                                        Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                                        binding.failedLogin.setVisibility(View.INVISIBLE);

                                        // TODO: 로그인 성공 시 메인 화면(예: HomeActivity)으로 이동
                                        // Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        // startActivity(intent);
                                        // finish();

                                    } else {
                                        // ✅ 2-2. 인증 미완료
                                        Log.w(TAG, "Email is not verified.");
                                        binding.failedLogin.setVisibility(View.VISIBLE);
                                        binding.failedLogin.setText("이메일 인증이 필요합니다. 인증메일을 보냈습니다"); // 오류 메시지 변경
                                        user.sendEmailVerification();
                                    }
                                }
                            } else {
                                // 로그인 실패
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                binding.failedLogin.setVisibility(View.VISIBLE); // 실패 메시지 보이기
                                binding.failedLogin.setText("아이디 또는 비밀번호를 잘못 입력하셨습니다."); // 오류 메시지 변경
                            }
                        }
                    });
        });


        // 회원가입 텍스트 클릭 리스너
        binding.signUpText.setOnClickListener(v -> {
            // 회원가입 화면으로 이동하는 로직
            Log.d("MOVE", "MoveSignUpAcitivity");
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // 비밀번호 찾기 텍스트 클릭 리스너
        binding.findPasswordText.setOnClickListener(v -> {
            Log.d("MOVE", "MoveFindPasswordAcitivity");
            Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
            startActivity(intent);
        });
    }
}