package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.userinterface.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private User currentUser; // 현재 User 확인

    private String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // BottomPadding은 내비게이션바를 위해 0으로
            return insets;
        });

        // Intent에서 User 데이터 수신
        currentUser = getIntent().getSerializableExtra("USER_PROFILE", User.class);
        if (currentUser == null) {
            Log.w(TAG, "cannot get User");
            Toast.makeText(this, "유저 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
        }

        // 초기 프래그먼트 설정 (HomeFragment)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance(currentUser));
        }

        // 내비게이션 바 리스너 설정
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) { //Fragment로 이동
                    selectedFragment = HomeFragment.newInstance(currentUser);

                } else if (itemId == R.id.navigation_search) { //Activity로 이동
                    Intent intent = new Intent(HomeActivity.this, PartsSearchMain.class);
                    intent.putExtra("USER_PROFILE", currentUser); // 유저 정보 넘겨주기
                    startActivity(intent);

                    // 액티비티로 떠나므로 현재 탭 선택 상태를 바꾸지 않으려면 false 반환
                    return false;

                } else if (itemId == R.id.navigation_guide) {
                    //selectedFragment = GuideFragment.newInstance(currentUser);
                } else if (itemId == R.id.navigation_recommended_builds) {
                    //selectedFragment = RecommendFragment.newInstance(currentUser);
                } else if (itemId == R.id.navigation_community) {
                    //selectedFragment = CommunityFragment.newInstance(currentUser);
                } else if (itemId == R.id.navigation_profile) {
                    //selectedFragment = ProfileFragment.newInstance(currentUser);
                }

                if (selectedFragment != null) { // null 이 아니면 탭 선택 변경
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    // 프래그먼트 교체 함수
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // 프래그먼트 내부에서 탭을 변경하고 싶을 때 호출할 public 메서드 (HomeFragment의 카드 클릭 시 사용)
    public void selectBottomNavigationItem(int itemId) {
        binding.bottomNavigation.setSelectedItemId(itemId);
    }
}