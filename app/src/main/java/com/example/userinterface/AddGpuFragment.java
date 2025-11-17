// AddGpuFragment.java
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

import com.example.userinterface.databinding.FragmentAddGpuBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddGpuFragment extends Fragment {

    // 뷰 바인딩 및 Firestore 변수
    private FragmentAddGpuBinding binding;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddGpuBinding.inflate(inflater, container, false);

        // Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // "부품 추가 완료" 버튼에 클릭 리스너 설정
        binding.addPartButton.setOnClickListener(v -> {
            uploadGpuDataToFirestore();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
        });
    }

    /**
     * XML 뷰에서 텍스트를 읽어 Gpu 객체를 Firestore에 업로드하는 함수
     */
    private void uploadGpuDataToFirestore() {
        // 1. 뷰에서 텍스트 읽어오기 (String)
        String name = binding.valueGpuModelName.getText().toString().trim();
        String manufacturer = binding.valueManufacturer.getText().toString().trim();
        String chipSet = binding.valueChipset.getText().toString().trim();
        String outPut = binding.valueOutput.getText().toString().trim();

        // 숫자 변환이 필요한 문자열
        String fanStr = binding.valueFan.getText().toString().trim();
        String vramStr = binding.valueVram.getText().toString().trim();
        String sizeStr = binding.valueLength.getText().toString().trim(); // XML ID는 value_length
        String baseStr = binding.valueBaseClock.getText().toString().trim();
        String boostStr = binding.valueBoostClock.getText().toString().trim();
        String powerStr = binding.valuePower.getText().toString().trim();
        String pcieStr = binding.valuePcie.getText().toString().trim();

        // 2. 간단한 유효성 검사 (필수 항목)
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(manufacturer) || TextUtils.isEmpty(chipSet)) {
            Toast.makeText(getContext(), "모델명, 제조사, 칩셋은 필수입니다.", Toast.LENGTH_SHORT).show();
            binding.valueGpuModelName.requestFocus();
            return; // 업로드 중단
        }

        // 3. String을 숫자(int, double)로 변환 (안전하게)
        //    (값이 비어있거나 잘못된 경우 0을 기본값으로 사용)
        int fan = parseIntWithDefault(fanStr, 0);
        int vram = parseIntWithDefault(vramStr, 0);
        double size = parseDoubleWithDefault(sizeStr, 0.0);
        int base = parseIntWithDefault(baseStr, 0);
        int boost = parseIntWithDefault(boostStr, 0);
        int power = parseIntWithDefault(powerStr, 0);
        int pcie = parseIntWithDefault(pcieStr, 0);

        // 4. (선택 사항) Gpu.java 모델에 있는 'gpuVer' 필드 추론하기
        String gpuVer = null;
        if (chipSet.toLowerCase().contains("rtx") || chipSet.toLowerCase().contains("geforce")) {
            gpuVer = "NVIDIA";
        } else if (chipSet.toLowerCase().contains("radeon") || chipSet.toLowerCase().contains("rx")) {
            gpuVer = "AMD";
        } else if (chipSet.toLowerCase().contains("arc")) {
            gpuVer = "Intel";
        }

        // 5. Gpu 객체 생성
        Gpu newGpu = new Gpu(
                name, manufacturer, fan, vram, size, base,
                boost, outPut, power, pcie, chipSet, gpuVer
        );

        // 6. Firestore "gpus" 컬렉션에 객체 추가하기
        db.collection("gpus")
                .add(newGpu)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(getContext(), "GPU 정보가 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show();
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

    // --- 문자열을 숫자로 바꾸는 헬퍼(Helper) 함수 ---

    /**
     * 문자열을 int로 변환합니다. 비어있거나 숫자 형식이 아니면 기본값을 반환합니다.
     * @param s 변환할 문자열
     * @param defaultValue 기본값
     * @return 변환된 int 또는 기본값
     */
    private int parseIntWithDefault(String s, int defaultValue) {
        if (s.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 문자열을 double로 변환합니다. 비어있거나 숫자 형식이 아니면 기본값을 반환합니다.
     * @param s 변환할 문자열
     * @param defaultValue 기본값
     * @return 변환된 double 또는 기본값
     */
    private double parseDoubleWithDefault(String s, double defaultValue) {
        if (s.isEmpty()) return defaultValue;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // 프래그먼트가 destroy될 때 바인딩을 null로 만들어 메모리 누수 방지
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}