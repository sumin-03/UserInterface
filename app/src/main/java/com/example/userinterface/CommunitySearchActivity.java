package com.example.userinterface;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommunitySearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private FirebaseFirestore db;
    private User currentUser;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_search);

        db = FirebaseFirestore.getInstance();

        // CommunityMainFragment에서 넘겨준 User 객체 받기
        if (getIntent() != null && getIntent().hasExtra("USER_PROFILE")) {
            currentUser = getIntent().getSerializableExtra("USER_PROFILE", User.class);
        }

        recyclerView = findViewById(R.id.search_recyclerview);
        searchView = findViewById(R.id.search_view);
        ImageButton btnBack = findViewById(R.id.btn_back);

        setupRecyclerView();

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());

        // 검색 리스너 설정
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 엔터 or 돋보기 버튼을 눌렀을 때만 실행
                Log.d("SEARCH_DEBUG", "검색 버튼 눌림: " + query);
                searchPosts(query);
                searchView.clearFocus(); // 키보드 내리기
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 텍스트가 비어있으면 초기 화면(빈 화면)으로 되돌리기
                if (newText.isEmpty()) {
                    setupRecyclerView();
                }
                return false;
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 빈 쿼리로 초기화해서 화면을 비워둠
        Query query = db.collection("posts")
                .whereEqualTo("userId", "none_user");

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new PostAdapter(options);

        // 상세 화면 이동 리스너 연결
        adapter.setOnItemClickListener((document, position) -> {
            String postId = document.getId();

            // 상세 Fragment 열기
            PostDetailFragment detailFragment = PostDetailFragment.newInstance(postId, currentUser);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.search_fragment_container, detailFragment) // 전체 화면 덮기
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);
    }

    private void searchPosts(String searchText) {
        if (searchText.isEmpty()) return;

        Log.d("SEARCH_DEBUG", "쿼리 시작: " + searchText);

        // Firebase 범위 검색 (Prefix Search)
        Query query = db.collection("posts")
                .orderBy("title") // 제목 기준 정렬 필수
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        // 어댑터 옵션 업데이트 (새로운 쿼리 적용)
        adapter.updateOptions(options);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}