package com.example.userinterface;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

// ◀️ 1. 바인딩 클래스 import 변경 (Rec용 바인딩)
import com.example.userinterface.databinding.FragmentRecommendQuotationBypartsRecBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

// ◀️ 2. 클래스 이름 변경
public class RecommendQuotationBypartsRecFragment extends Fragment {

    // ◀️ 3. 바인딩 변수 타입 변경
    private FragmentRecommendQuotationBypartsRecBinding binding;
    private FirebaseFirestore db;
    private AppAdapter adapter;

    private static final String ARG_CPU = "selected_cpu";
    private static final String ARG_GPU = "selected_gpu";
    private static final String ARG_RAM = "selected_ram";

    private int userCpuGrade = -1;
    private int userGpuGrade = -1;
    private int userRamSize = 0;
    private boolean isUserCpuAmd = false;
    private boolean isUserGpuAmd = false;

    private ArrayList<String> playableGameList = new ArrayList<>();

    // ◀️ 4. 생성자 이름 및 반환 타입 변경
    public static RecommendQuotationBypartsRecFragment newInstance(String cpu, String gpu, String ram) {
        RecommendQuotationBypartsRecFragment fragment = new RecommendQuotationBypartsRecFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CPU, cpu);
        args.putString(ARG_GPU, gpu);
        args.putString(ARG_RAM, ram);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ◀️ 5. Rec용 레이아웃 inflate
        binding = FragmentRecommendQuotationBypartsRecBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnCheckResult).setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .remove(RecommendQuotationBypartsRecFragment.this)
                    .commit();
        });

        // [디버깅 1] 프래그먼트가 생성되었는지 확인
        Log.e("DEBUG_ENTRY", ">>> RecommendQuotationBypartsRecFragment: onViewCreated 진입 성공! <<<");
        db = FirebaseFirestore.getInstance();

        if (binding != null) {
            binding.rvPlayableGames.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        if (getArguments() != null) {
            String cpuName = getArguments().getString(ARG_CPU);
            String gpuName = getArguments().getString(ARG_GPU);
            String ramString = getArguments().getString(ARG_RAM);

            if (cpuName == null || gpuName == null) {
                Log.e("DEBUG_ENTRY", "⚠️ 경고: CPU 또는 GPU 이름이 NULL입니다! 로직을 실행하지 않습니다.");
            } else {
                // 정상적으로 데이터가 있을 때만 실행
                calculateUserSpecs(cpuName, gpuName, ramString);
            }
        } else {
            // [디버깅 3] arguments가 아예 없는 경우
            Log.e("DEBUG_ENTRY", "❌ 에러: getArguments()가 NULL입니다. Activity에서 데이터를 보내지 않았습니다.");
        }
    }

    private void calculateUserSpecs(String cpuName, String gpuName, String ramString) {
        Log.d("DEBUG_REC", "1. 사용자 스펙 계산 시작: " + cpuName + " / " + gpuName);

        userRamSize = parseMemory(ramString);
        isUserCpuAmd = (cpuName != null) && (cpuName.toUpperCase().contains("RYZEN") || cpuName.toUpperCase().contains("AMD"));
        isUserGpuAmd = (gpuName != null) && (gpuName.toUpperCase().contains("RADEON") || gpuName.toUpperCase().contains("AMD"));

        List<Task<Integer>> tasks = new ArrayList<>();
        tasks.add(getHardwareGrade("cpus", cpuName));
        tasks.add(getHardwareGrade("gpus_info", gpuName));

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            userCpuGrade = (int) results.get(0);
            userGpuGrade = (int) results.get(1);

            Log.d("DEBUG_REC", "2. 사용자 등급 계산 완료 -> CPU: " + userCpuGrade + ", GPU: " + userGpuGrade);

            if (userCpuGrade != 999 && userGpuGrade != 999) {
                findPlayableGames();
            } else {
                Log.e("DEBUG_REC", "사용자 부품 정보를 DB에서 찾을 수 없음");
            }
        }).addOnFailureListener(e -> {
            Log.e("DEBUG_REC", "사용자 스펙 조회 중 에러 발생", e);
        });
    }

    private void findPlayableGames() {
        Log.d("DEBUG_REC", "3. requirements_rec 컬렉션 조회 시작");

        // [체크포인트 1] 컬렉션 이름 오타 확인 필수! (requirements_rec)
        db.collection("requirements_rec").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // [수정] 데이터가 비어있으면 Toast 띄우고 종료
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.e("DEBUG_REC", "데이터 없음: requirements_rec 컬렉션이 비어있거나 존재하지 않음.");
                        return;
                    }

                    Log.d("DEBUG_REC", "4. 데이터 가져옴. 문서 개수: " + queryDocumentSnapshots.size());

                    List<Task<String>> checkTasks = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        checkTasks.add(checkSingleRequirement(doc));
                    }

                    Tasks.whenAllSuccess(checkTasks).addOnSuccessListener(results -> {
                        playableGameList.clear();
                        for (Object result : results) {
                            if (result != null) playableGameList.add((String) result);
                        }

                        Log.d("DEBUG_REC", "5. 최종 결과 개수: " + playableGameList.size());
                        updateUI();
                    }).addOnFailureListener(e -> {
                        Log.e("DEBUG_REC", "개별 게임 조건 비교 중 에러", e);
                    });
                })
                .addOnFailureListener(e -> {
                    // [수정] DB 연결 실패 시 에러 로그 및 Toast
                    Log.e("DEBUG_REC", "Firestore 조회 실패: requirements_rec", e);
                });
    }

    private Task<String> checkSingleRequirement(DocumentSnapshot doc) {
        String gameName = doc.getString("name");
        String reqRamStr = doc.getString("ram");
        int reqRam = parseMemory(reqRamStr);
        if (userRamSize < reqRam) return Tasks.forResult(null);

        String targetCpuName = isUserCpuAmd ? doc.getString("cpu_AMD") : doc.getString("cpu_Intel");
        String targetGpuName = isUserGpuAmd ? doc.getString("gpu_AMD") : doc.getString("gpu_NVIDIA");

        List<Task<Integer>> tasks = new ArrayList<>();
        tasks.add(getHardwareGrade("cpus", targetCpuName));
        tasks.add(getHardwareGrade("gpus_info", targetGpuName));

        return Tasks.whenAllSuccess(tasks).continueWith(task -> {
            List<Object> grades = task.getResult();
            int reqCpuGrade = (int) grades.get(0);
            int reqGpuGrade = (int) grades.get(1);

            // 로직 동일: 내 등급(낮을수록 좋음) <= 권장 요구 등급
            if (userCpuGrade <= reqCpuGrade && userGpuGrade <= reqGpuGrade) {
                return gameName;
            }
            return null;
        });
    }

    private Task<Integer> getHardwareGrade(String collectionName, String rawName) {
        if (rawName == null || rawName.equalsIgnoreCase("None")) {
            return Tasks.forResult(999);
        }

        List<String> candidates = new ArrayList<>();

        // [핵심 수정 1] 원본 이름(앞뒤 공백 제거)을 1순위 후보로 넣습니다.
        // DB에 "AMD Ryzen 5 3600"이라고 풀네임으로 저장된 경우 이걸로 잡힙니다.
        String originalTrimmed = rawName.trim();
        candidates.add(originalTrimmed);

        // [기존 로직] 제조사 제거 버전 생성
        String cleanName = originalTrimmed;
        String upperName = cleanName.toUpperCase();

        if (upperName.startsWith("NVIDIA ")) {
            cleanName = cleanName.substring(7).trim();
        } else if (upperName.startsWith("INTEL ")) {
            cleanName = cleanName.substring(6).trim();
        } else if (upperName.startsWith("AMD ")) {
            cleanName = cleanName.substring(4).trim();
        }

        // 제조사를 뗀 이름이 원본과 다르면 후보에 추가 (예: "Ryzen 5 3600")
        if (!cleanName.equals(originalTrimmed)) {
            candidates.add(cleanName);
        }

        // 브랜드(Ryzen, Core) 제거 버전 생성 (예: "5 3600")
        String noBrandName = cleanName.replaceAll("(?i)^Core\\s+", "").replaceAll("(?i)^Ryzen\\s+", "");
        if (!cleanName.equals(noBrandName)) {
            candidates.add(noBrandName.trim());
        }

        // "GB" 제거 버전 추가
        String noGB = cleanName.replaceAll("(?i)\\s+\\d+GB$", "").trim();
        if(!candidates.contains(noGB)) candidates.add(noGB);

        // Super, Ti 등의 변형 추가
        List<String> tempCandidates = new ArrayList<>(candidates);
        for (String s : tempCandidates) {
            String fixed = s.replaceAll("(?i)Super", "SUPER").replaceAll("(?i)Vega", "VEGA").replaceAll("(?i)Ti", "Ti");
            if (!candidates.contains(fixed)) candidates.add(fixed);
        }

        return db.collection(collectionName).whereIn("name", candidates).get().continueWith(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                int bestGrade = 999;
                for (DocumentSnapshot doc : task.getResult()) {
                    int g = 999;
                    Object gObj = doc.get("grade");
                    if (gObj instanceof Number) g = ((Number) gObj).intValue();
                    else if (gObj instanceof String) { try { g = Integer.parseInt((String) gObj); } catch(Exception e){} }

                    if (g < bestGrade) bestGrade = g;
                }
                return bestGrade;
            }
            return 999;
        });
    }

    private int parseMemory(String raw) {
        if (raw == null) return 0;
        String num = raw.replaceAll("[^0-9]", "");
        try { return Integer.parseInt(num); } catch (Exception e) { return 0; }
    }

    private void updateUI() {
        if (binding == null) return;

        if (playableGameList.isEmpty()) {
            Toast.makeText(getContext(), "권장 사양을 만족하는 게임이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), playableGameList.size() + "개의 권장 사양 만족 게임을 찾았습니다!", Toast.LENGTH_SHORT).show();
        }

        ArrayList<AppItem> appItems = new ArrayList<>();
        for (String gameName : playableGameList) {
            appItems.add(new AppItem(gameName));
        }

        adapter = new AppAdapter(appItems);
        binding.rvPlayableGames.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}