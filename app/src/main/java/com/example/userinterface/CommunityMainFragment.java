package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.userinterface.databinding.FragmentCommunityMainBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommunityMainFragment extends Fragment {

    private FragmentCommunityMainBinding binding;

    private FirebaseFirestore db;
    private PostAdapter adapter;
    private User currentUser;
    private String TAG = "CommunityMain";

    //생성자
    public static CommunityMainFragment newInstance(User user) {
        CommunityMainFragment fragment = new CommunityMainFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_PROFILE", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase instance 초기화
        db = FirebaseFirestore.getInstance();

        //전달받은 User 객체
        if(getArguments() != null){
            currentUser = getArguments().getSerializable("USER_PROFILE", User.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //방송 듣기 설정
        new ViewModelProvider(requireActivity()).get(UserViewModel.class).getUser().observe(getViewLifecycleOwner(), updatedUser -> {
            //user 정보가 바뀔 때 실행
            if (updatedUser != null) {
                //현재 프래그먼트의 변수 업데이트
                currentUser = updatedUser;
                Log.d(TAG, "UserUpdate");
            }
        });

        // RecyclerView 설정
        setupRecyclerView();

        //탭 클릭 리스너 설정
        setupTabClickListeners();

        //초기 데이터 로드
        updateRecyclerViewQuery(0);

        //post detail 클릭 리스너
        setupPostDetailClickListeners();

        //글쓰기 버튼 클릭 리스너 설정
        binding.communityWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment writeCommunityFragment = WriteCommunityFragment.newInstance(currentUser);

                //글쓰기로 이동
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, writeCommunityFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setupRecyclerView(){
        binding.communityRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.communityRecyclerview.setItemAnimator(null);
    }

    private void setupTabClickListeners(){ // 탭 클릭 리스너
        binding.communityTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {//tab 선택시 recyclerview 업데이트
                updateRecyclerViewQuery(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { //tab 재선택시 맨 위로
                binding.communityRecyclerview.smoothScrollToPosition(0);
            }
        });
    }

    //탭 위치에 따라 쿼리 변경하고 어댑터 갱신하는 메소드
    private void updateRecyclerViewQuery(int tabPosition){
        Query query;

        // 탭 순서: 0:최신글, 1:공지, 2:조립/견적, 3:질문/토론, 4:정보, 5:인기글, 6:일반, 7: 내 글, 8: 북마크
        switch(tabPosition){
            case 1: // 공지
                query = db.collection("posts")
                        .whereEqualTo("category", "공지")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            case 2: // 조립
                query = db.collection("posts")
                        .whereEqualTo("category", "조립")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            case 3: // 견적
                query = db.collection("posts")
                        .whereEqualTo("category", "견적")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            case 4: // 질문/토론
                query = db.collection("posts")
                        .whereEqualTo("category", "질문/토론")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            case 5: // 정보
                query = db.collection("posts")
                        .whereEqualTo("category", "정보")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            case 6: // 인기글(추천 수 10 이상 + 최신 순)
                query = db.collection("posts")
                        .whereGreaterThanOrEqualTo("likes", 10)
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            case 7: // 일반
                query = db.collection("posts")
                        .whereEqualTo("category", "일반")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            case 8: //내 글
                if (currentUser != null) {
                    query = db.collection("posts")
                            .whereEqualTo("userId", currentUser.getUid())
                            .orderBy("timestamp", Query.Direction.DESCENDING);
                } else {
                    // 유저 정보 없으면 빈 리스트 표시
                    query = db.collection("posts").limit(0);
                }
                break;
            case 9: //북마크
                if(currentUser != null){
                    query = db.collection("users")
                            .document(currentUser.getUid())
                            .collection("bookmarks")
                            .orderBy("timestamp", Query.Direction.DESCENDING);
                } else {
                    // 유저 정보 없으면 빈 리스트 표시
                    query = db.collection("posts").limit(0);
                }
                break;
            case 0: //최신글
            default:
                query = db.collection("posts")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
        }

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        if (adapter != null){
            // 어댑터가 있으면 옵션만 갈아끼움 (리스너 유지됨)
            adapter.updateOptions(options);
        }
        else {
            // 어댑터가 없어서 새로 만들 경우
            adapter = new PostAdapter(options);
            // [중요] 어댑터를 새로 만들었으면 리스너도 다시 달아줘야 함
            setupPostDetailClickListeners();
        }

        if (tabPosition == 9) {
            adapter.setBookmarkMode(true);
        } else {
            adapter.setBookmarkMode(false);
        }

        binding.communityRecyclerview.setAdapter(adapter);
    }

    private void setupPostDetailClickListeners(){
        // 어댑터가 생성되지 않았을 경우 방지
        if (adapter == null) return;
        adapter.setOnItemClickListener(((document, position) -> {
            // 클릭한 아이템의 Document ID 가져오기
            String postId = document.getId();

            // 상세 화면 Fragment 생성 (ID 전달)
            PostDetailFragment detailFragment = PostDetailFragment.newInstance(postId, currentUser);

            //화면 전환
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }));
    }

    // Fragment 생명주기 관리: 화면에 보일 때 리스너 시작
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    // Fragment 생명주기 관리: 화면에서 사라질 때 리스너 중지
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}
