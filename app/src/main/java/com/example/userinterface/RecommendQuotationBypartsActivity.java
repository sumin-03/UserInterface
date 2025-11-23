package com.example.userinterface;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivityRecommendQuotationBypartsBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecommendQuotationBypartsActivity extends AppCompatActivity {

    private static final String TAG = "RecommendByParts";
    private FirebaseFirestore db;
    private ActivityRecommendQuotationBypartsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 뷰 바인딩 설정
        binding = ActivityRecommendQuotationBypartsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();

        // 3. RAM 설정 (Dropdown)
        String[] rams = {"8GB", "16GB", "32GB", "64GB"};
        ArrayAdapter<String> ramAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                rams
        );
        binding.autoCompleteRam.setAdapter(ramAdapter);

        // 4. CPU 설정 (Firestore에서 가져오기)
        setupDropdownFromFirestore(binding.autoCompleteCpu, "cpus", "name");

        // 5. GPU 설정 (Firestore에서 가져오기)
        setupDropdownFromFirestore(binding.autoCompleteGpu, "gpus_info", "name");

        // ====================================================================
        // [버튼 1] 최소 사양 결과 보기 버튼
        // ====================================================================
        binding.buttonMinSpec.setOnClickListener(v -> {
            // 1. 선택된 값 가져오기
            String selectedCpu = binding.autoCompleteCpu.getText().toString();
            String selectedGpu = binding.autoCompleteGpu.getText().toString();
            String selectedRam = binding.autoCompleteRam.getText().toString();

            // 2. 유효성 검사
            if (TextUtils.isEmpty(selectedCpu) || TextUtils.isEmpty(selectedGpu) || TextUtils.isEmpty(selectedRam)) {
                Toast.makeText(this, "모든 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. newInstance로 Fragment 생성 (데이터 전달)
            RecommendQuotationBypartsMinFragment minFragment =
                    RecommendQuotationBypartsMinFragment.newInstance(selectedCpu, selectedGpu, selectedRam);

            // 4. 화면 전환
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, minFragment) // 레이아웃 ID(R.id.main) 확인 필요
                    .addToBackStack("min_spec")
                    .commit();
        });

        // ====================================================================
        // [버튼 2] 권장 사양 결과 보기 버튼 (수정된 부분)
        // ====================================================================
        binding.buttonRecSpec.setOnClickListener(v -> {
            // 1. 선택된 값 가져오기
            String selectedCpu = binding.autoCompleteCpu.getText().toString();
            String selectedGpu = binding.autoCompleteGpu.getText().toString();
            String selectedRam = binding.autoCompleteRam.getText().toString();

            // 2. 유효성 검사
            if (TextUtils.isEmpty(selectedCpu) || TextUtils.isEmpty(selectedGpu) || TextUtils.isEmpty(selectedRam)) {
                Toast.makeText(this, "모든 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. [중요] newInstance로 Fragment 생성하여 데이터 전달!
            RecommendQuotationBypartsRecFragment recFragment =
                    RecommendQuotationBypartsRecFragment.newInstance(selectedCpu, selectedGpu, selectedRam);

            // 4. 화면 전환
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, recFragment) // 레이아웃 ID(R.id.main) 확인 필요
                    .addToBackStack("rec_spec")
                    .commit();
        });
    }

    /**
     * Firestore에서 데이터를 가져와 드롭다운 메뉴(AutoCompleteTextView)를 설정하는 공통 메서드
     */
    private void setupDropdownFromFirestore(AutoCompleteTextView view, String collectionName, String fieldName) {
        List<String> itemList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                itemList
        );
        view.setAdapter(adapter);

        db.collection(collectionName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String item = document.getString(fieldName);
                        if (item != null) {
                            itemList.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, collectionName + " 목록 로드 성공");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, collectionName + " 목록 로드 실패.", e);
                    Toast.makeText(this, collectionName + " 로드 실패", Toast.LENGTH_SHORT).show();
                });
    }
}