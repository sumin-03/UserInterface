package com.example.userinterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
    private User currentUser;

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

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ViewModel을 통해 최신 User 정보 가져오기 (HomeFragment와 데이터 공유)
        new ViewModelProvider(requireActivity()).get(UserViewModel.class).getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.currentUser = user;
                setupRecyclerView();
            }
        });
    }

    private void setupRecyclerView() {
        if (!"latest".equals(queryType) && currentUser == null) {
            return;
        }
        Query query;

        // 탭(queryType)에 따라 다른 쿼리 실행
        switch (queryType) {
            case "my_posts":
                query = db.collection("posts")
                        .whereEqualTo("userId", currentUser.getUid())
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(5);
                break;
            case "bookmarks":
                query = db.collection("users")
                        .document(currentUser.getUid())
                        .collection("bookmarks")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(5);
                break;
            case "latest":
            default:
                // "최신 게시글"
                query = db.collection("posts")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(5);
                break;
        }

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class) // 쿼리와 Post 모델 클래스 연결
                .build();

        if (adapter != null) {
            adapter.updateOptions(options);
        } else {
            adapter = new PostAdapter(options);
            }

        if ("bookmarks".equals(queryType)) {
            adapter.setBookmarkMode(true); // 북마크면 숨김 모드 켜기
        } else {
            adapter.setBookmarkMode(false); // 아니면 일반 모드
        }

        // 아이템 클릭 리스너 설정
        adapter.setOnItemClickListener(((document, position) -> {
            String postId = document.getId();


            //상세 화면 생성 (클릭된 글 ID + 내 유저 정보 전달)
            PostDetailFragment detailFragment = PostDetailFragment.newInstance(postId, currentUser);

            //화면 전환
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }));
        // RecyclerView에 어댑터 및 레이아웃 매니저 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
    }

    // Fragment가 화면에 보일 때 리스너 시작
    @Override
    public void onResume() { // onStart() -> onResume()
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    // Fragment가 화면에서 사라질 때 리스너 중지
    @Override
    public void onPause() { // onStop() -> onPause()
        super.onPause();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}