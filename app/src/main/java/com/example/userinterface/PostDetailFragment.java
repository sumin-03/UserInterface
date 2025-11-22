package com.example.userinterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.userinterface.databinding.FragmentPostDetailBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostDetailFragment extends Fragment {

    private FirebaseFirestore db;
    private FragmentPostDetailBinding binding;
    private String postId;
    private User currentUser;
    private Post currentPostData; // 현재 보고 있는 글 데이터 (수정 시 넘겨주기 위해 저장)

    public static PostDetailFragment newInstance(String postId, User user) {
        PostDetailFragment fragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putString("POST_ID", postId);
        args.putSerializable("USER_PROFILE", user);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        if(getArguments() != null){
            postId = getArguments().getString("POST_ID");
            currentUser = getArguments().getSerializable("USER_PROFILE", User.class);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //post내용 불러오기
        setupPostDetail();

        //뒤로가기 클릭 리스너
        setupBackButton();
    }

    private void setupPostDetail(){
        if(postId == null) return;
        
        //처음 시작시 로딩 중
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.GONE);

        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentPostData = documentSnapshot.toObject(Post.class);

                    if (currentPostData != null){
                        //조회수 1 증가
                        db.collection("posts").document(postId).update("views", com.google.firebase.firestore.FieldValue.increment(1));
                        binding.detailTitle.setText(currentPostData.getTitle());
                        binding.postContent.setText(currentPostData.getContent());
                        binding.detailCategory.setText(currentPostData.getCategory());
                        binding.postUser.setText(currentPostData.getUserName());
                        binding.detailWatch.setText("조회 " + (currentPostData.getViews() + 1));
                        binding.postLikeCount.setText(String.valueOf(currentPostData.getLikes()));


                        // 날짜 포맷팅
                        if (currentPostData.getTimestamp() != null) {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.KOREA);
                            binding.detailDate.setText(sdf.format(currentPostData.getTimestamp()));
                        }

                        // 본인 글 확인 로직 (수정/삭제 버튼 표시)
                        if (currentUser != null && currentUser.getUid().equals(currentPostData.getUserId())){
                            binding.postChangeBtn.setVisibility(View.VISIBLE);
                            binding.postDeleteBtn.setVisibility(View.VISIBLE);

                            // === 수정 버튼 클릭 리스너 ===
                            binding.postChangeBtn.setOnClickListener(v -> {
                                // 수정용 프래그먼트 생성 (기존 데이터 전달)
                                WriteCommunityFragment editFragment = WriteCommunityFragment.newInstanceForEdit(currentUser, postId, currentPostData);

                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, editFragment) // 컨테이너 ID 확인 필요
                                        .addToBackStack(null)
                                        .commit();
                            });
                            // === 삭제 버튼 클릭 리스너 ===
                            binding.postDeleteBtn.setOnClickListener(v -> showDeleteDialog());
                        }

                        // 남이 쓴 글이면 숨기기
                        else {
                            binding.postChangeBtn.setVisibility(View.GONE);
                            binding.postDeleteBtn.setVisibility(View.GONE);
                        }

                        //이제 글이 보이기 시작함 ㅇㅇ
                        binding.progressBar.setVisibility(View.GONE);
                        binding.nestedScrollView.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(e -> {
                    // 데이터 로드 실패 시 처리
                    Toast.makeText(getContext(), "게시글을 불러오지 못했습니다.", android.widget.Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                });
    }

    // 삭제 확인 다이얼로그
    private void showDeleteDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("게시글 삭제")
                .setMessage("정말 삭제하시겠습니까?\n삭제된 글은 복구할 수 없습니다.")
                .setNegativeButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost();
                    }
                })
                .setPositiveButton("아니오", null)
                .show();
    }

    // 실제 삭제 로직
    private void deletePost() {
        if (postId != null) {
            db.collection("posts").document(postId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack(); // 목록으로 돌아가기
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void setupBackButton(){
        binding.btnDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
