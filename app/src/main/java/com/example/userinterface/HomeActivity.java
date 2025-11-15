package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityHomeBinding;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //PagerAdapter와 탭 제목 배열 추가
    private HomePagerAdapter pagerAdapter;
    private String[] tabTitles = new String[]{"최신 게시글", "내가 쓴 게시글", "북마크"};


    private static final String TAG = "HomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        displayUserProfile();// LoadingActivity 에서 User가져옴
        setupCardClickListeners(); //홈 화면 가운데 4개 리스너

        //TODO: 내비게이션 바 사용 로직 필요시 가져다 쓰세요
        setupNavigationListeners(); //밑에 내비게이션 바 리스너 -> 이 부분 복사해서 자신의 액티비ㅜ티에 맞게 수정 후 사용

        setupCommunityViewPager(); //커뮤니티 페이지 설정
    }

    //밑에 커뮤니티 페이지
    private void setupCommunityViewPager() {
        // PagerAdapter 초기화
        pagerAdapter = new HomePagerAdapter(HomeActivity.this);
        binding.communityViewPager.setAdapter(pagerAdapter);

        // TabLayout과 ViewPager2 연결 (탭 제목 설정)
        new TabLayoutMediator(binding.communityLayout, binding.communityViewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
    }

    //TODO: 로그아웃 마이페이지에 추가하기
    //TODO: 닉네임변경 마이페이지에 추가하기
    //TODO: 홈으로 올때는 User 정보를 항상 넘겨줘야함 (LoadingActivity참조)
    private void displayUserProfile() {
        //Intent에서 "USER_PROFILE" 이름으로 User 객체를 꺼냄
        User user = getIntent().getSerializableExtra("USER_PROFILE", User.class);

        // 객체가 정상적으로 전달되었는지 확인
        if (user != null) {
            // UI 요소에 데이터 즉시 적용
            binding.homeUsername.setText(user.getNickname());
            binding.homeLevel.setText("Lv." + user.getLevel());
            binding.homeExperienceBar.setMax(100);
            binding.homeExperienceBar.setProgress((int) user.getExperience());
            binding.homeExperiencePoints.setText(user.getExperience() + "/100");

            binding.homeWelcome.setText("안녕하세요, " + user.getNickname() + "님!");

            if (user.getJoinDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 가입", Locale.KOREA);
                String formattedDate = sdf.format(user.getJoinDate());
                binding.homeJoinDate.setText(formattedDate);
            }
        } else {
            //  데이터가 전달되지 않음
            Log.w(TAG, "User profile data was not passed to HomeActivity.");
            Toast.makeText(this, "프로필 표시에 실패했습니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void setupCardClickListeners() { //실질적으로 내비게이션 바가 처리

        //부품 검색 클릭 리스너
        binding.findComponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNavigation.setSelectedItemId(R.id.navigation_search);
            }
        });

        //가이드 클릭 리스너
        binding.guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNavigation.setSelectedItemId(R.id.navigation_guide);
            }
        });

        //내 PC 클릭 리스너
        binding.community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNavigation.setSelectedItemId(R.id.navigation_community);
            }
        });

        //견적 추천 클릭 리스너
        binding.recommendedBuilds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNavigation.setSelectedItemId(R.id.navigation_recommended_builds);
            }
        });
    }

    private void setupNavigationListeners() { //밑에 내비게이션 바 리스너
        binding.bottomNavigation.setOnItemSelectedListener(Item -> {
            int itemId = Item.getItemId();

            if(itemId == R.id.navigation_home){
                Log.d("MOVE", "MoveHome");
                Toast.makeText(this, "홈으로 이동!", Toast.LENGTH_SHORT).show(); //이거 지우기
                return true;
            }

            else if (itemId == R.id.navigation_search) {
                Log.d("MOVE", "MoveFindComponent");
                Toast.makeText(this, "부품 검색으로 이동!", Toast.LENGTH_SHORT).show(); //이거 지우기
                // TODO: 부품 검색 액티비티로 이동(User정보 포함해서 넘겨주기)
                // Intent intent = new Intent(this, SearchActivity.class);
                // startActivity(intent);
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
}