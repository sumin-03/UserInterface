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

public class RecommendQuotationByappsGameFragment extends Fragment {

    private RecyclerView rvGameList;
    private AppAdapter adapter;
    private List<AppItem> gameItemList;
    private FirebaseFirestore db;

    // [1] 액티비티와 통신할 인터페이스 정의
    public interface OnGameSelectedListener {
        void onGameSelectionComplete(ArrayList<String> selectedGames);
    }

    private OnGameSelectedListener mListener;

    public RecommendQuotationByappsGameFragment() {
        // Required empty public constructor
    }

    public static RecommendQuotationByappsGameFragment newInstance(String param1, String param2) {
        RecommendQuotationByappsGameFragment fragment = new RecommendQuotationByappsGameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // [2] 프래그먼트가 액티비티에 붙을 때 인터페이스 연결
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnGameSelectedListener) {
            mListener = (OnGameSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnGameSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend_quotation_byapps_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvGameList = view.findViewById(R.id.rvGameList);
        ImageView ivBack = view.findViewById(R.id.ivBack);
        Button btnCheckResult = view.findViewById(R.id.btnCheckResult);

        gameItemList = new ArrayList<>();
        adapter = new AppAdapter(gameItemList);
        rvGameList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGameList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadGameData();

        ivBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        // [3] 결과 확인 버튼 클릭 시 데이터 전송
        btnCheckResult.setOnClickListener(v -> {
            ArrayList<String> selectedGames = adapter.getSelectedAppNames();

            if (selectedGames.isEmpty()) {
                Toast.makeText(getContext(), "게임을 하나 이상 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 인터페이스를 통해 액티비티로 데이터 전달
                if (mListener != null) {
                    mListener.onGameSelectionComplete(selectedGames);
                }
            }
        });
    }

    private void loadGameData() {
        // Game은 requirements_min 컬렉션에서 category == "game"
        db.collection("requirements_min")
                .whereEqualTo("category", "game")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        gameItemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String gameName = document.getString("name");
                            if (gameName != null) {
                                gameItemList.add(new AppItem(gameName));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting games: ", task.getException());
                        Toast.makeText(getContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}