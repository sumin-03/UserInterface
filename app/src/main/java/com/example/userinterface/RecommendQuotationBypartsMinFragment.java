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
import androidx.recyclerview.widget.LinearLayoutManager; // ◀️ 필수 import

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

    // ◀️ AppAdapter 사용 선언
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

        view.findViewById(R.id.btnCheckResult).setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .remove(RecommendQuotationBypartsMinFragment.this)
                    .commit();
        });

        db = FirebaseFirestore.getInstance();

        // ◀️ 리사이클러뷰 기본 설정 (LayoutManager만 먼저 설정)
        if (binding != null) {
            binding.rvPlayableGames.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        if (getArguments() != null) {
            String cpuName = getArguments().getString(ARG_CPU);
            String gpuName = getArguments().getString(ARG_GPU);
            String ramString = getArguments().getString(ARG_RAM);

            calculateUserSpecs(cpuName, gpuName, ramString);
        }
    }

    // ... (calculateUserSpecs, findPlayableGames, checkSingleRequirement, getHardwareGrade, parseMemory 함수들은 이전과 동일하므로 생략하지 않고 그대로 둡니다) ...

    // [참고] 로직 함수들은 그대로 유지하시면 됩니다. 변경된 부분은 아래 updateUI() 입니다.

    private void calculateUserSpecs(String cpuName, String gpuName, String ramString) {
        // ... (이전 코드와 동일) ...
        userRamSize = parseMemory(ramString);
        isUserCpuAmd = (cpuName != null) && (cpuName.toUpperCase().contains("RYZEN") || cpuName.toUpperCase().contains("AMD"));
        isUserGpuAmd = (gpuName != null) && (gpuName.toUpperCase().contains("RADEON") || gpuName.toUpperCase().contains("AMD"));

        List<Task<Integer>> tasks = new ArrayList<>();
        tasks.add(getHardwareGrade("cpus", cpuName));
        tasks.add(getHardwareGrade("gpus_info", gpuName));

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            userCpuGrade = (int) results.get(0);
            userGpuGrade = (int) results.get(1);

            if (userCpuGrade != 999 && userGpuGrade != 999) {
                findPlayableGames();
            } else {
                Toast.makeText(getContext(), "부품 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findPlayableGames() {
        db.collection("requirements_min").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) return;

            List<Task<String>> checkTasks = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                checkTasks.add(checkSingleRequirement(doc));
            }

            Tasks.whenAllSuccess(checkTasks).addOnSuccessListener(results -> {
                playableGameList.clear();
                for (Object result : results) {
                    if (result != null) playableGameList.add((String) result);
                }
                updateUI(); // ◀️ 데이터 다 모았으니 UI 업데이트 호출
            });
        });
    }

    private Task<String> checkSingleRequirement(DocumentSnapshot doc) {
        // ... (이전 코드와 동일) ...
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
            if (userCpuGrade <= reqCpuGrade && userGpuGrade <= reqGpuGrade) {
                return gameName;
            }
            return null;
        });
    }

    private Task<Integer> getHardwareGrade(String collectionName, String rawName) {
        // ... (이전 코드와 동일, 아까 작성해드린 강화된 버전 그대로 사용) ...
        if (rawName == null || rawName.equalsIgnoreCase("None")) return Tasks.forResult(999);

        String cleanName = rawName;
        String upperName = cleanName.toUpperCase();
        if (upperName.startsWith("NVIDIA ")) cleanName = cleanName.substring(7).trim();
        else if (upperName.startsWith("AMD ")) cleanName = cleanName.substring(4).trim();
        if (upperName.startsWith("INTEL CORE ")) cleanName = cleanName.substring(11).trim();
        else if (upperName.startsWith("INTEL ")) cleanName = cleanName.substring(6).trim();
        else if (upperName.startsWith("AMD RYZEN ")) cleanName = cleanName.substring(10).trim();

        List<String> candidates = new ArrayList<>();
        candidates.add(cleanName);
        String noBrandName = cleanName.replaceAll("(?i)^Core\\s+", "").replaceAll("(?i)^Ryzen\\s+", "");
        if (!cleanName.equals(noBrandName)) candidates.add(noBrandName.trim());
        String noGB = cleanName.replaceAll("(?i)\\s+\\d+GB$", "").trim();
        if(!candidates.contains(noGB)) candidates.add(noGB);
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

    // =========================================================================================
    // ◀️ [수정됨] 여기가 핵심입니다. String 리스트 -> AppItem 리스트 변환 후 어댑터 연결
    // =========================================================================================
    private void updateUI() {
        if (binding == null) return;

        if (playableGameList.isEmpty()) {
            Toast.makeText(getContext(), "조건을 만족하는 게임이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), playableGameList.size() + "개의 게임을 찾았습니다!", Toast.LENGTH_SHORT).show();
        }

        // 1. AppItem 리스트 생성
        ArrayList<AppItem> appItems = new ArrayList<>();
        for (String gameName : playableGameList) {
            // AppAdapter는 AppItem 객체를 원하므로 변환해서 넣어줍니다.
            appItems.add(new AppItem(gameName));
        }

        // 2. 어댑터 생성 및 연결 (기존 AppAdapter 재활용)
        adapter = new AppAdapter(appItems);
        binding.rvPlayableGames.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}