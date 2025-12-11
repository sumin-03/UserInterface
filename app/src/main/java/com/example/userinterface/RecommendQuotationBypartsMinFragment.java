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

import com.example.userinterface.databinding.FragmentRecommendQuotationBypartsMinBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecommendQuotationBypartsMinFragment extends Fragment {

    private FragmentRecommendQuotationBypartsMinBinding binding;
    private FirebaseFirestore db;
    private AppAdapter adapter;

    // 로그 필터링을 위한 태그 정의
    private final String TAG = "SpecCheck";

    private static final String ARG_CPU = "selected_cpu";
    private static final String ARG_GPU = "selected_gpu";
    private static final String ARG_RAM = "selected_ram";

    private int userCpuGrade = -1;
    private int userGpuGrade = -1;
    private int userRamSize = 0;
    private boolean isUserCpuAmd = false;
    private boolean isUserGpuAmd = false;

    private ArrayList<String> playableGameList = new ArrayList<>();

    public static RecommendQuotationBypartsMinFragment newInstance(String cpu, String gpu, String ram) {
        RecommendQuotationBypartsMinFragment fragment = new RecommendQuotationBypartsMinFragment();
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
        binding = FragmentRecommendQuotationBypartsMinBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 닫기 버튼
        view.findViewById(R.id.btnCheckResult).setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .remove(RecommendQuotationBypartsMinFragment.this)
                    .commit();
        });

        db = FirebaseFirestore.getInstance();

        // RecyclerView 설정
        if (binding != null) {
            binding.rvPlayableGames.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        // Arguments 수신 및 로그 확인
        if (getArguments() != null) {
            String cpuName = getArguments().getString(ARG_CPU);
            String gpuName = getArguments().getString(ARG_GPU);
            String ramString = getArguments().getString(ARG_RAM);

            // [로그 1] Fragment에 도착한 원본 데이터 확인
            Log.d(TAG, "========================================");
            Log.d(TAG, "[1] 데이터 수신 (From Activity/Fragment)");
            Log.d(TAG, "  > Raw CPU: " + cpuName);
            Log.d(TAG, "  > Raw GPU: " + gpuName);
            Log.d(TAG, "  > Raw RAM: " + ramString);
            Log.d(TAG, "========================================");

            calculateUserSpecs(cpuName, gpuName, ramString);
        } else {
            Log.e(TAG, "[Error] getArguments() is null. 데이터를 받지 못했습니다.");
        }
    }

    private void calculateUserSpecs(String cpuName, String gpuName, String ramString) {
        userRamSize = parseMemory(ramString);

        // 제조사 파악 로직
        isUserCpuAmd = (cpuName != null) && (cpuName.toUpperCase().contains("RYZEN") || cpuName.toUpperCase().contains("AMD"));
        isUserGpuAmd = (gpuName != null) && (gpuName.toUpperCase().contains("RADEON") || gpuName.toUpperCase().contains("AMD"));

        // [로그 2] 파싱된 스펙 정보 확인
        Log.d(TAG, "[2] 스펙 파싱 결과");
        Log.d(TAG, "  > RAM Size : " + userRamSize + " GB");
        Log.d(TAG, "  > CPU Type : " + (isUserCpuAmd ? "AMD" : "Intel"));
        Log.d(TAG, "  > GPU Type : " + (isUserGpuAmd ? "AMD" : "NVIDIA"));

        List<Task<Integer>> tasks = new ArrayList<>();
        // DB 조회 요청
        tasks.add(getHardwareGrade("cpus", cpuName));
        tasks.add(getHardwareGrade("gpus_info", gpuName));

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            userCpuGrade = (int) results.get(0);
            userGpuGrade = (int) results.get(1);

            // [로그 3] DB 조회 후 최종 등급 확인
            Log.d(TAG, "[3] 최종 하드웨어 등급 (낮을수록 좋음)");
            Log.d(TAG, "  > 내 CPU 등급: " + userCpuGrade);
            Log.d(TAG, "  > 내 GPU 등급: " + userGpuGrade);

            if (userCpuGrade != 999 && userGpuGrade != 999) {
                Log.d(TAG, "  > 등급 조회 성공. 게임 탐색 시작...");
                findPlayableGames();
            } else {
                Log.e(TAG, "  > !! 실패: 부품 등급을 가져오지 못했습니다. (999 반환됨)");
                if (getContext() != null) {
                    Toast.makeText(getContext(), "일부 부품 정보를 DB에서 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findPlayableGames() {
        db.collection("requirements_min").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Log.w(TAG, "[Warn] requirements_min 컬렉션이 비어있습니다.");
                return;
            }

            Log.d(TAG, "[4] 게임 요구사양 데이터 로드됨: " + queryDocumentSnapshots.size() + "개");

            List<Task<String>> checkTasks = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                checkTasks.add(checkSingleRequirement(doc));
            }

            Tasks.whenAllSuccess(checkTasks).addOnSuccessListener(results -> {
                playableGameList.clear();
                for (Object result : results) {
                    if (result != null) playableGameList.add((String) result);
                }
                updateUI();
            });
        });
    }

    private Task<String> checkSingleRequirement(DocumentSnapshot doc) {
        String gameName = doc.getString("name");
        String reqRamStr = doc.getString("ram");
        int reqRam = parseMemory(reqRamStr);

        // 램 부족하면 즉시 탈락
        if (userRamSize < reqRam) return Tasks.forResult(null);

        // CPU/GPU 요구사항 이름 가져오기
        String targetCpuName = isUserCpuAmd ? doc.getString("cpu_AMD") : doc.getString("cpu_Intel");
        String targetGpuName = isUserGpuAmd ? doc.getString("gpu_AMD") : doc.getString("gpu_NVIDIA");

        List<Task<Integer>> tasks = new ArrayList<>();
        tasks.add(getHardwareGrade("cpus", targetCpuName));
        tasks.add(getHardwareGrade("gpus_info", targetGpuName));

        return Tasks.whenAllSuccess(tasks).continueWith(task -> {
            List<Object> grades = task.getResult();
            int reqCpuGrade = (int) grades.get(0);
            int reqGpuGrade = (int) grades.get(1);

            // 내 등급(숫자)이 요구 등급(숫자)보다 작거나 같아야 성능이 더 좋은 것 (1등급 > 5등급)
            if (userCpuGrade <= reqCpuGrade && userGpuGrade <= reqGpuGrade) {
                return gameName; // 구동 가능
            }
            return null; // 불가능
        });
    }

    private Task<Integer> getHardwareGrade(String collectionName, String rawName) {
        if (rawName == null || rawName.equalsIgnoreCase("None")) {
            Log.w(TAG, "  > [Query Skip] 이름이 Null이거나 None입니다: " + collectionName);
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

        // 로그 확인: 이제 후보군에 원본 이름도 포함되어야 합니다.
        Log.d(TAG, "  > [DB Query] 컬렉션: " + collectionName);
        Log.d(TAG, "  > [DB Query] 원본이름: '" + rawName + "' -> 검색후보: " + candidates.toString());

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
                Log.d(TAG, "    -> [Match Found] '" + rawName + "' 찾음! 등급: " + bestGrade);
                return bestGrade;
            }
            Log.w(TAG, "    -> [No Match] '" + rawName + "' DB 매칭 실패. (검색 후보군 확인 필요)");
            return 999;
        });
    }

    private int parseMemory(String raw) {
        if (raw == null) return 0;
        String num = raw.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            Log.w(TAG, "[Warn] 메모리 파싱 에러: " + raw);
            return 0;
        }
    }

    private void updateUI() {
        if (binding == null) return;

        Log.d(TAG, "[5] 최종 결과: 구동 가능한 게임 " + playableGameList.size() + "개 발견");

        if (playableGameList.isEmpty()) {
            Toast.makeText(getContext(), "조건을 만족하는 게임이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), playableGameList.size() + "개의 게임을 찾았습니다!", Toast.LENGTH_SHORT).show();
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