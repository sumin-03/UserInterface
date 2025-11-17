// AddPowerFragment.java
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

import com.example.userinterface.databinding.FragmentAddPowerBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddPowerFragment extends Fragment {

    // 뷰 바인딩 및 Firestore 변수
    private FragmentAddPowerBinding binding;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddPowerBinding.inflate(inflater, container, false);

        // Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // "부품 추가 완료" 버튼에 클릭 리스너 설정
        binding.addPartButton.setOnClickListener(v -> {
            uploadPowerDataToFirestore();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
        });
    }

    /**
     * XML 뷰에서 텍스트를 읽어 Power 객체를 Firestore에 업로드하는 함수
     */
    private void uploadPowerDataToFirestore() {
        // 1. 뷰에서 텍스트 읽어오기 (모두 String)
        String name = binding.valuePowerModelName.getText().toString().trim(); // XML의 value_power_model_name
        String manufacturer = binding.valueManufacturer.getText().toString().trim();
        String power = binding.valuePower.getText().toString().trim();
        String plus80 = binding.valuePlus80.getText().toString().trim();
        String size = binding.valueSize.getText().toString().trim();

        // 2. 간단한 유효성 검사
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(manufacturer)) {
            Toast.makeText(getContext(), "모델명과 제조사는 필수입니다.", Toast.LENGTH_SHORT).show();
            binding.valuePowerModelName.requestFocus();
            return; // 업로드 중단
        }

        // 3. Power 객체 생성 (Power.java의 생성자 순서에 맞게)
        Power newPower = new Power(
                name, manufacturer, power, plus80, size
        );

        // 4. Firestore "powers" 컬렉션에 객체 추가하기
        db.collection("powers")
                .add(newPower)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(getContext(), "파워 정보가 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show();

                    // (선택) 업로드 성공 시 프래그먼트 닫기
                    // if (getActivity() != null) {
                    //     getActivity().getSupportFragmentManager().popBackStack();
                    // }
                })
                .addOnFailureListener(e -> {
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