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

import com.example.userinterface.databinding.FragmentAddCpuBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCpuFragment extends Fragment {

    private FragmentAddCpuBinding binding;

    // Firestore 인스턴스 변수 추가
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddCpuBinding.inflate(inflater, container, false);

        // Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // "부품 추가 완료" 버튼에 클릭 리스너 설정
        binding.addPartButton.setOnClickListener(v -> {
            // Firestore에 업로드하는 함수 호출
            uploadCpuDataToFirestore();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
        });
    }

    // Firestore에 데이터를 업로드하는 함수
    private void uploadCpuDataToFirestore() {
        // 1. 뷰에서 텍스트 읽어오기 (trim()으로 앞뒤 공백 제거)
        String modelName = binding.valueCpuModelName.getText().toString().trim(); //
        String manufacturer = binding.valueManufacturer.getText().toString().trim();
        String socket = binding.valueSocket.getText().toString().trim();
        String cores = binding.valueCores.getText().toString().trim();
        String threads = binding.valueThreads.getText().toString().trim();
        String memoryVersion = binding.valueMemoryVersion.getText().toString().trim();
        String memoryClock = binding.valueMemoryClock.getText().toString().trim();
        String baseClock = binding.valueBaseClock.getText().toString().trim();
        String boostClock = binding.valueBoostClock.getText().toString().trim();
        String l2cache = binding.valueL2cache.getText().toString().trim();
        String l3cache = binding.valueL3cache.getText().toString().trim();
        String graphics = binding.valueGraphics.getText().toString().trim();
        String tdp = binding.valueTdp.getText().toString().trim();

        // 2. (★수정된 부분★) 유효성 검사 (필수 항목을 각각 검사)
        if (TextUtils.isEmpty(modelName)) {
            Toast.makeText(getContext(), "모델명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            binding.valueCpuModelName.requestFocus(); // 모델명 입력창에 포커스
            return; // 업로드 중단
        }

        if (TextUtils.isEmpty(manufacturer)) {
            Toast.makeText(getContext(), "제조사를 입력해주세요.", Toast.LENGTH_SHORT).show();
            binding.valueManufacturer.requestFocus(); // 제조사 입력창에 포커스
            return; // 업로드 중단
        }

        if (TextUtils.isEmpty(socket)) {
            Toast.makeText(getContext(), "소켓을 입력해주세요.", Toast.LENGTH_SHORT).show();
            binding.valueSocket.requestFocus(); // 소켓 입력창에 포커스
            return; // 업로드 중단
        }

        // 3. Cpu 객체 생성
        Cpu newCpu = new Cpu(
                modelName, manufacturer, socket, cores, threads,
                memoryVersion, memoryClock, baseClock, boostClock,
                l2cache, l3cache, graphics, tdp
        );

        // 4. Firestore "cpus" 컬렉션에 객체 추가하기
        // "cpus"라는 이름의 컬렉션이 없으면 자동으로 생성됩니다.
        db.collection("cpus")
                .add(newCpu)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(getContext(), "CPU 정보가 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    // getActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                    Toast.makeText(getContext(), "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

