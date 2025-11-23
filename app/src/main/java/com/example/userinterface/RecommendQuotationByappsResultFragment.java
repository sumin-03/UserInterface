package com.example.userinterface;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecommendQuotationByappsResultFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView tvMinSpecContent;
    private TextView tvRecSpecContent;

    // 1. 계산된 최종 사양을 저장할 내부 클래스 (제조사별 분리)
    private static class FinalSpec {
        // CPU - AMD
        int bestCpuAmdGrade = 999;
        String bestCpuAmdName = "정보 없음";

        // CPU - Intel
        int bestCpuIntelGrade = 999;
        String bestCpuIntelName = "정보 없음";

        // GPU - AMD
        int bestGpuAmdGrade = 999;
        String bestGpuAmdName = "정보 없음";

        // GPU - NVIDIA
        int bestGpuNvidiaGrade = 999;
        String bestGpuNvidiaName = "정보 없음";

        int maxRam = 0;
        int maxStorage = 0;
        String os = "Windows 10 64-bit";
    }

    public RecommendQuotationByappsResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend_quotation_byapps_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        tvMinSpecContent = view.findViewById(R.id.tvMinSpecContent);
        tvRecSpecContent = view.findViewById(R.id.tvRecSpecContent);
        ImageView ivBack = view.findViewById(R.id.ivBack);
        AppCompatButton btnReselect = view.findViewById(R.id.btnReselect);

        if (ivBack != null) {
            ivBack.setOnClickListener(v -> {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        btnReselect.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .remove(RecommendQuotationByappsResultFragment.this)
                    .commit();
        });

        if (getArguments() != null) {
            ArrayList<String> games = getArguments().getStringArrayList("gameList");
            ArrayList<String> apps = getArguments().getStringArrayList("appList");

            ArrayList<String> allPrograms = new ArrayList<>();
            if (games != null) allPrograms.addAll(games);
            if (apps != null) allPrograms.addAll(apps);

            if (!allPrograms.isEmpty()) {
                // 최소 사양 계산
                calculateSpecs(allPrograms, "requirements_min", tvMinSpecContent);

                // 권장 사양 계산
                if (tvRecSpecContent != null) {
                    calculateSpecs(allPrograms, "requirements_rec", tvRecSpecContent);
                }
            }
        }
    }

    // =========================================================================================
    //  메인 로직: 요구사항 수집
    // =========================================================================================
    private void calculateSpecs(ArrayList<String> programNames, String collectionName, TextView targetView) {
        FinalSpec finalSpec = new FinalSpec();
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        for (String name : programNames) {
            tasks.add(db.collection(collectionName).whereEqualTo("name", name).get());
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            List<Task<Void>> hardwareTasks = new ArrayList<>();

            for (Object obj : results) {
                QuerySnapshot snapshot = (QuerySnapshot) obj;
                if (!snapshot.isEmpty()) {
                    DocumentSnapshot doc = snapshot.getDocuments().get(0);

                    updateMaxMemory(finalSpec, doc.getString("ram"), true);
                    updateMaxMemory(finalSpec, doc.getString("storage"), false);

                    // CPU 조회 요청 (AMD / Intel 분리 호출)
                    String cpuAmd = doc.getString("cpu_AMD");
                    String cpuIntel = doc.getString("cpu_Intel");
                    if (cpuAmd != null) hardwareTasks.add(checkCpuGrade(finalSpec, cpuAmd, true));   // true: AMD
                    if (cpuIntel != null) hardwareTasks.add(checkCpuGrade(finalSpec, cpuIntel, false)); // false: Intel

                    // GPU 조회 요청 (AMD / NVIDIA 분리 호출)
                    String gpuAmd = doc.getString("gpu_AMD");
                    String gpuNvidia = doc.getString("gpu_NVIDIA");
                    if (gpuAmd != null) hardwareTasks.add(checkGpuGrade(finalSpec, gpuAmd, true));      // true: AMD
                    if (gpuNvidia != null) hardwareTasks.add(checkGpuGrade(finalSpec, gpuNvidia, false)); // false: NVIDIA
                }
            }

            Tasks.whenAllSuccess(hardwareTasks).addOnSuccessListener(v -> {
                updateUI(targetView, finalSpec);
            }).addOnFailureListener(e -> {
                Log.e("SpecCalc", "하드웨어 조회 실패", e);
                targetView.setText("하드웨어 정보를 불러오지 못했습니다.");
            });

        }).addOnFailureListener(e -> {
            Log.e("SpecCalc", "요구사항 조회 실패", e);
            targetView.setText("요구사항 정보를 불러오지 못했습니다.");
        });
    }

    // =========================================================================================
    //  2. CPU Grade 확인 함수 (문자열 파싱 처리)
    //  Firestore 컬렉션: "cpus" / grade 타입: String
    // =========================================================================================
    private Task<Void> checkCpuGrade(FinalSpec spec, String modelName, boolean isAmd) {
        return db.collection("cpus")
                .whereEqualTo("name", modelName)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);

                        // 문자열로 grade 가져오기
                        String gradeStr = doc.getString("grade");
                        int grade = 999;

                        // 문자열 파싱 ("단종" 등 처리)
                        if (gradeStr != null) {
                            try {
                                grade = Integer.parseInt(gradeStr);
                            } catch (NumberFormatException e) {
                                grade = 999; // 숫자가 아니면 가장 낮은 성능 처리
                            }
                        }

                        // AMD/Intel 구분하여 저장
                        if (isAmd) {
                            if (grade < spec.bestCpuAmdGrade) {
                                spec.bestCpuAmdGrade = grade;
                                spec.bestCpuAmdName = modelName;
                            }
                        } else { // Intel
                            if (grade < spec.bestCpuIntelGrade) {
                                spec.bestCpuIntelGrade = grade;
                                spec.bestCpuIntelName = modelName;
                            }
                        }
                    }
                    return null;
                });
    }

    // =========================================================================================
    //  3. GPU Grade 확인 함수 (정수형 처리)
    //  Firestore 컬렉션: "gpu_info" / grade 타입: Number (Long/Integer)
    // =========================================================================================
    // =========================================================================================
    //  3. GPU Grade 확인 함수 (제조사 이름 제거 전처리 추가)
    // =========================================================================================
    // =========================================================================================
    //  3. GPU Grade 확인 함수 (제조사 제거 + 메모리 용량 유무 동시 검색)
    // =========================================================================================
    // =========================================================================================
    //  3. GPU Grade 확인 함수 (최종 수정: 대소문자 보정 및 컬렉션 확인)
    // =========================================================================================
    private Task<Void> checkGpuGrade(FinalSpec spec, String rawModelName, boolean isAmd) {

        if (rawModelName == null || rawModelName.equalsIgnoreCase("None")) {
            return Tasks.forResult(null);
        }

        // 1. 제조사 이름 제거 및 기본 정리
        String cleanName = rawModelName;
        if (cleanName.toUpperCase().startsWith("NVIDIA ")) {
            cleanName = cleanName.substring(7).trim();
        } else if (cleanName.toUpperCase().startsWith("AMD ")) {
            cleanName = cleanName.substring(4).trim();
        }

        // 2. 검색 후보군 생성
        List<String> searchCandidates = new ArrayList<>();

        // (1) 원본 (제조사만 뗀 것)
        searchCandidates.add(cleanName);

        // (2) GB 제거 버전
        String nameWithoutGB = cleanName.replaceAll("(?i)\\s+\\d+GB$", "").trim(); // (?i)는 대소문자 무시
        if (!cleanName.equals(nameWithoutGB)) {
            searchCandidates.add(nameWithoutGB);
        }

        // (3) [핵심] 대소문자 보정 (Super -> SUPER, Vega -> VEGA, Ti -> Ti)
        // DB에는 "SUPER", "VEGA"로 저장되어 있으나 요구사항에는 "Super", "Vega"로 올 수 있음
        String upperFixed = cleanName
                .replaceAll("(?i)Super", "SUPER")
                .replaceAll("(?i)Vega", "VEGA");

        if (!searchCandidates.contains(upperFixed)) {
            searchCandidates.add(upperFixed);
        }

        // GB 뗀 버전에도 대소문자 보정 적용
        String upperFixedNoGB = nameWithoutGB
                .replaceAll("(?i)Super", "SUPER")
                .replaceAll("(?i)Vega", "VEGA");

        if (!searchCandidates.contains(upperFixedNoGB)) {
            searchCandidates.add(upperFixedNoGB);
        }

        // [중요] 컬렉션 이름을 본인의 DB 상황에 맞게 수정하세요 ("gpus" 인지 "gpu_info" 인지)
        String collectionName = "gpus_info";

        return db.collection(collectionName)
                .whereIn("name", searchCandidates)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        int bestGradeInResult = 999;
                        String bestNameInResult = "";

                        for (DocumentSnapshot doc : task.getResult()) {
                            Long gradeLong = doc.getLong("grade");
                            int currentGrade = 999;

                            if (gradeLong != null) {
                                currentGrade = gradeLong.intValue();
                            } else {
                                try {
                                    String gStr = doc.getString("grade");
                                    if(gStr != null) currentGrade = Integer.parseInt(gStr);
                                } catch(Exception e) {}
                            }

                            if (currentGrade < bestGradeInResult) {
                                bestGradeInResult = currentGrade;
                                bestNameInResult = doc.getString("name");
                            }
                        }

                        if (isAmd) {
                            if (bestGradeInResult < spec.bestGpuAmdGrade) {
                                spec.bestGpuAmdGrade = bestGradeInResult;
                                spec.bestGpuAmdName = bestNameInResult;
                            }
                        } else {
                            if (bestGradeInResult < spec.bestGpuNvidiaGrade) {
                                spec.bestGpuNvidiaGrade = bestGradeInResult;
                                spec.bestGpuNvidiaName = bestNameInResult;
                            }
                        }
                    } else {
                        // 여전히 못 찾음 -> 데이터가 없거나 컬렉션 이름 틀림
                        Log.e("GPU_DEBUG", "못 찾음: " + searchCandidates.toString() + " (대상 컬렉션: " + collectionName + ")");
                    }
                    return null;
                });
    }

    private void updateMaxMemory(FinalSpec spec, String rawString, boolean isRam) {
        if (rawString == null) return;
        String numberOnly = rawString.replaceAll("[^0-9]", "");
        if (numberOnly.isEmpty()) return;

        try {
            int val = Integer.parseInt(numberOnly);
            if (isRam) {
                if (val > spec.maxRam) spec.maxRam = val;
            } else {
                if (val > spec.maxStorage) spec.maxStorage = val;
            }
        } catch (NumberFormatException e) {}
    }

    // =========================================================================================
    //  UI 출력 (4종류 모두 표시)
    // =========================================================================================
    private void updateUI(TextView textView, FinalSpec spec) {
        if (textView == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("[CPU]\n");
        sb.append(" AMD: ").append(formatGrade(spec.bestCpuAmdGrade, spec.bestCpuAmdName)).append("\n");
        sb.append(" Intel: ").append(formatGrade(spec.bestCpuIntelGrade, spec.bestCpuIntelName)).append("\n\n");

        sb.append("[GPU]\n");
        sb.append(" NVIDIA: ").append(formatGrade(spec.bestGpuNvidiaGrade, spec.bestGpuNvidiaName)).append("\n");
        sb.append(" AMD: ").append(formatGrade(spec.bestGpuAmdGrade, spec.bestGpuAmdName)).append("\n\n");

        sb.append("[Memory/OS]\n");
        sb.append(" RAM: ").append(spec.maxRam).append("GB\n");
        sb.append(" Storage: ").append(spec.maxStorage).append("GB\n");
        sb.append(" OS: ").append(spec.os);

        textView.setText(sb.toString());
    }

    // UI 출력용 헬퍼 함수
    private String formatGrade(int grade, String name) {
        if (grade == 999) return "정보 없음";
        return name;
    }
}