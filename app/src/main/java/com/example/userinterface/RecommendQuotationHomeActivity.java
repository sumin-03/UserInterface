package com.example.userinterface;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.userinterface.databinding.ActivityRecommendQuotationHomeBinding;

public class RecommendQuotationHomeActivity extends AppCompatActivity {
    ActivityRecommendQuotationHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRecommendQuotationHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.buttonApps.setOnClickListener(v -> {
            Intent intent = new Intent(RecommendQuotationHomeActivity.this, RecommendQuotationByappsActivity.class);
            startActivity(intent);
        });

        binding.buttonParts.setOnClickListener(v -> {
            Intent intent = new Intent(RecommendQuotationHomeActivity.this, RecommendQuotationBypartsActivity.class);
            startActivity(intent);
        });

        binding.toolbar.setOnClickListener(v -> { // 위에 toolbar 클릭시 홈으로
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
        });

        binding.bottomNavigation.setSelectedItemId(R.id.navigation_search); //내비게이션 부품 검색으로 설정
        setupNavigationListeners(); //내비게이션 바
    }

    private void setupNavigationListeners() { //밑에 내비게이션 바 리스너
        binding.bottomNavigation.setOnItemSelectedListener(Item -> {
            int itemId = Item.getItemId();
            User user = getIntent().getSerializableExtra("USER_PROFILE", User.class);

            if(itemId == R.id.navigation_home){
                Log.d("MOVE", "MoveHome");
                Intent intent = new Intent(RecommendQuotationHomeActivity.this, HomeActivity.class);
                intent.putExtra("USER_PROFILE", user);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(this, 0, 0);
                startActivity(intent, options.toBundle());
                return true;
            }

            else if (itemId == R.id.navigation_search) {
                Log.d("STAY", "RecommendQuotationHomeActivity");
                return true;
            }

            else if (itemId == R.id.navigation_guide) {
                Log.d("MOVE", "MoveGuide");
                Toast.makeText(this, "가이드로 이동!", Toast.LENGTH_SHORT).show(); //이거 지우기
                // TODO: 가이드 액티비티로 이동(User정보 포함해서 넘겨주기)
                // Intent intent = new Intent(this, GuideActivity.class);
                // startActivity(intent);
                return true;
            }

            else if (itemId == R.id.navigation_recommended_builds) {
                Log.d("MOVE", "MoveRecommend");
                Toast.makeText(this, "견적 추천으로 이동!!", Toast.LENGTH_SHORT).show(); //이거 지우기
                // TODO: 견적 추천 액티비티로 이동(User정보 포함해서 넘겨주기)
                // Intent intent = new Intent(this, RecommendActivity.class);
                // startActivity(intent);
                return true;
            }

            else if (itemId == R.id.navigation_community) {
                Log.d("MOVE", "MoveCommunity");
                Toast.makeText(this, "커뮤니티", Toast.LENGTH_SHORT).show(); //이거 지우기
                // TODO: 커뮤니티 액티비티로 이동(User정보 포함해서 넘겨주기)
                // Intent intent = new Intent(this, CommunityActivity.class);
                // startActivity(intent);
                return true;
            }

            else if (itemId == R.id.navigation_profile) {
                Log.d("MOVE", "MoveProfile");
                Toast.makeText(this, "내 프로필로 이동!", Toast.LENGTH_SHORT).show(); //이거 지우기
                // TODO: 내 프로필로 액티비티로 이동(User정보 포함해서 넘겨주기)
                // Intent intent = new Intent(this, MyPCActivity.class);
                // startActivity(intent);
                return true;
            }

            return false;
        });
    }

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