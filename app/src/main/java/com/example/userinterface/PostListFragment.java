package com.example.userinterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostListFragment extends Fragment {

    private FirebaseFirestore db;
    private PostAdapter adapter;
    private RecyclerView recyclerView;
    private String queryType = "latest"; // 첫 시작 latest
    private String currentUserId;

    // Fragment가 어떤 데이터를 로드해야 하는지 알려주는 '생성자' 역할
    public static PostListFragment newInstance(String queryType) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putString("QUERY_TYPE", queryType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            queryType = getArguments().getString("QUERY_TYPE", "latest");
        }
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_post_list.xml을 inflate
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        recyclerView = view.findViewById(R.id.post_recycler_view);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        Query query;

        // 탭(queryType)에 따라 다른 쿼리 실행
        switch (queryType) {
            case "my_posts":
                // "내가 쓴 게시글"
                query = db.collection("posts")
                        .whereEqualTo("userId", currentUserId)
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
//            case "bookmarks":
//                // TODO: 북마크 기능 구현 필요
//                break;
            case "latest":
            default:
                // "최신 게시글"
                query = db.collection("posts")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
        }

        //  쿼리 결과 중 5개만 가져오도록 추가
        query = query.limit(5);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class) // 쿼리와 Post 모델 클래스 연결
                .build();

        // 어댑터 생성
        adapter = new PostAdapter(options);

        // RecyclerView에 어댑터 및 레이아웃 매니저 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    // Fragment가 화면에 보일 때 리스너 시작
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    // Fragment가 화면에서 사라질 때 리스너 중지
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}