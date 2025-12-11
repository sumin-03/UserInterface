package com.example.userinterface;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.userinterface.databinding.ActivityRecommendQuotationByappsBinding;

import java.util.ArrayList;

public class RecommendQuotationByappsActivity extends AppCompatActivity
        implements RecommendQuotationByappsGameFragment.OnGameSelectedListener,
        RecommendQuotationByappsCreatorFragment.OnAppSelectedListener {

    // 1. 데이터를 저장할 멤버 변수 선언 (리스트 초기화)
    private boolean isBasicWorkSelected = false;
    private User currentUser;
    private ArrayList<String> finalGameList = new ArrayList<>();
    private ArrayList<String> finalAppList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityRecommendQuotationByappsBinding binding = ActivityRecommendQuotationByappsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CheckBox checkBox = findViewById(R.id.checkBox);
        View btnResult = findViewById(R.id.btnResult);

        // 서칭/문서작업 선택 리스너
        binding.checkBox.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                isBasicWorkSelected = !isBasicWorkSelected;
                checkBox.setChecked(isBasicWorkSelected);
            }
        });

        // 게임 선택 화면으로 이동
        binding.ivArrowGame.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, RecommendQuotationByappsGameFragment.newInstance("", ""))
                    .addToBackStack(null)
                    .commit();
        });

        // 크리에이터(앱) 선택 화면으로 이동
        binding.ivArrowCreator.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, RecommendQuotationByappsCreatorFragment.newInstance("", ""))
                    .addToBackStack(null)
                    .commit();
        });
        currentUser=getIntent().getSerializableExtra("USER_PROFILE", User.class);

        // [수정됨] 결과 확인 버튼 로직: 데이터를 모아서 결과 프래그먼트로 이동
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 선택된 항목이 하나라도 있는지 확인 (선택 사항)
                if (!isBasicWorkSelected && finalGameList.isEmpty() && finalAppList.isEmpty()) {
                    Toast.makeText(RecommendQuotationByappsActivity.this, "최소 한 가지 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. 데이터 꾸러미(Bundle) 생성
                Bundle bundle = new Bundle();
                bundle.putBoolean("isBasicWork", isBasicWorkSelected); // 문서작업 여부
                bundle.putStringArrayList("gameList", finalGameList);  // 게임 리스트
                bundle.putStringArrayList("appList", finalAppList);    // 앱 리스트
                bundle.putSerializable("USER_PROFILE",currentUser);

                // 2. 결과 프래그먼트 생성 및 데이터 전달
                RecommendQuotationByappsResultFragment resultFragment = new RecommendQuotationByappsResultFragment();
                resultFragment.setArguments(bundle);

                // 3. 화면 전환
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main, resultFragment) // R.id.main 영역을 교체
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    // [2] GameFragment에서 데이터를 전달받아 멤버 변수에 저장
    @Override
    public void onGameSelectionComplete(ArrayList<String> selectedGames) {
        // 프래그먼트 닫기
        getSupportFragmentManager().popBackStack();

        // 멤버 변수에 저장
        this.finalGameList = selectedGames;

        // 확인용 토스트
        Toast.makeText(this, "게임 " + selectedGames.size() + "개 선택됨", Toast.LENGTH_SHORT).show();
    }

    // [3] CreatorFragment에서 데이터를 전달받아 멤버 변수에 저장
    @Override
    public void onAppSelectionComplete(ArrayList<String> selectedApps) {
        // 프래그먼트 닫기
        getSupportFragmentManager().popBackStack();

        // 멤버 변수에 저장
        this.finalAppList = selectedApps;

        // 확인용 토스트
        Toast.makeText(this, "앱 " + selectedApps.size() + "개 선택됨", Toast.LENGTH_SHORT).show();
    }
}