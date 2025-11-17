// AddCaseFragment.java
package com.example.userinterface;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.userinterface.databinding.FragmentAddCaseBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCaseFragment extends Fragment {

    // 뷰 바인딩 및 Firestore 변수
    private FragmentAddCaseBinding binding;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddCaseBinding.inflate(inflater, container, false);

        // Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // "부품 추가 완료" 버튼에 클릭 리스너 설정
        binding.addPartButton.setOnClickListener(v -> {
            uploadCaseDataToFirestore();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
        });
    }

    /**
     * XML 뷰에서 텍스트를 읽어 Case 객체를 Firestore에 업로드하는 함수
     */
    private void uploadCaseDataToFirestore() {
        // 1. 뷰에서 텍스트 읽어오기 (trim()으로 앞뒤 공백 제거)
        String name = binding.valueCaseModelName.getText().toString().trim();
        String manufacturer = binding.valueManufacturer.getText().toString().trim();
        String boardSize = binding.valueBoardSize.getText().toString().trim();
        String coolerSize = binding.valueCoolerSize.getText().toString().trim();
        String gpuSize = binding.valueGpuSize.getText().toString().trim();
        String powerSize = binding.valuePowerSize.getText().toString().trim();
        String size = binding.valueSize.getText().toString().trim();
        String atxPower = binding.valueAtxPower.getText().toString().trim();

        // 2. 간단한 유효성 검사 (필수 항목이 비었는지 확인)
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(manufacturer)) {
            Toast.makeText(getContext(), "모델명과 제조사는 필수입니다.", Toast.LENGTH_SHORT).show();
            binding.valueCaseModelName.requestFocus(); // 모델명 입력창에 포커스
            return; // 업로드 중단
        }

        // 3. Case 객체 생성
        Case newCase = new Case(
                name, manufacturer, boardSize, coolerSize,
                gpuSize, powerSize, size, atxPower
        );

        // 4. Firestore "cases" 컬렉션에 객체 추가하기
        // "cases"라는 이름의 컬렉션이 없으면 자동으로 생성됩니다.
        db.collection("cases")
                .add(newCase) // .add()는 자동 생성된 ID로 문서를 추가합니다.
                .addOnSuccessListener(documentReference -> {
                    // 성공 리스너
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(getContext(), "케이스 정보가 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show();

                    // (선택) 업로드 성공 시 프래그먼트 닫기
                    // if (getActivity() != null) {
                    //     getActivity().getSupportFragmentManager().popBackStack();
                    // }
                })
                .addOnFailureListener(e -> {
                    // 실패 리스너
                    Log.w("Firestore", "Error adding document", e);
                    Toast.makeText(getContext(), "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // 프래그먼트가 destroy될 때 바인딩을 null로 만들어 메모리 누수 방지
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}