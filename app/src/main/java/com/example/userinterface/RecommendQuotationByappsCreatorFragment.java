package com.example.userinterface;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecommendQuotationByappsCreatorFragment extends Fragment {

    private RecyclerView rvAppList;
    private AppAdapter adapter;
    private List<AppItem> appItemList;
    private FirebaseFirestore db;

    // [1] 액티비티와 통신할 인터페이스 정의
    public interface OnAppSelectedListener {
        void onAppSelectionComplete(ArrayList<String> selectedApps);
    }

    private OnAppSelectedListener mListener;

    public RecommendQuotationByappsCreatorFragment() {
        // Required empty public constructor
    }

    public static RecommendQuotationByappsCreatorFragment newInstance(String param1, String param2) {
        RecommendQuotationByappsCreatorFragment fragment = new RecommendQuotationByappsCreatorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // [2] 프래그먼트가 액티비티에 붙을 때 인터페이스 연결
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAppSelectedListener) {
            mListener = (OnAppSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAppSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend_quotation_byapps_creator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvAppList = view.findViewById(R.id.rvAppList);
        ImageView ivBack = view.findViewById(R.id.ivBack);
        Button btnCheckResult = view.findViewById(R.id.btnCheckResult);

        appItemList = new ArrayList<>();
        adapter = new AppAdapter(appItemList);
        rvAppList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAppList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadAppData();

        ivBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        // [3] 결과 확인 버튼 클릭 시 데이터 전송
        btnCheckResult.setOnClickListener(v -> {
            ArrayList<String> selectedApps = adapter.getSelectedAppNames();

            if (selectedApps.isEmpty()) {
                Toast.makeText(getContext(), "앱을 하나 이상 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 인터페이스를 통해 액티비티로 데이터 전달
                if (mListener != null) {
                    mListener.onAppSelectionComplete(selectedApps);
                }
            }
        });
    }

    private void loadAppData() {
        // Creator는 requirements_min 컬렉션에서 category == "software" (올려주신 파일 기준)
        db.collection("requirements_min")
                .whereEqualTo("category", "software")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appItemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String appName = document.getString("name");
                            if (appName != null) {
                                appItemList.add(new AppItem(appName));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting apps: ", task.getException());
                        Toast.makeText(getContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}