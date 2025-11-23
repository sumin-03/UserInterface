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
import androidx.lifecycle.ViewModelProvider;

import com.example.userinterface.databinding.FragmentCommunityWriteBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class WriteCommunityFragment extends Fragment {

    private FirebaseFirestore db;
    private User currentUser;
    private FragmentCommunityWriteBinding binding;


    // 수정 모드를 위한 변수들
    private String editPostId = null; // 수정할 글 ID (null이면 새 글 작성)
    private Post editPostData = null; // 수정할 글 데이터

    private String TAG = "WriteCommunityFragment";

    //기본 생성자(글쓰기용)
    public static WriteCommunityFragment newInstance(User user) {
        WriteCommunityFragment fragment = new WriteCommunityFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_PROFILE", user);
        fragment.setArguments(args);
        return fragment;
    }
    //수정용 생성자 (수정 모드용)
    public static WriteCommunityFragment newInstanceForEdit(User user, String postId, Post postData) {
        WriteCommunityFragment fragment = new WriteCommunityFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_PROFILE", user);
        args.putString("EDIT_POST_ID", postId); // 수정할 ID 전달
        args.putSerializable("EDIT_POST_DATA", postData); // 기존 데이터 전달
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            currentUser = getArguments().getSerializable("USER_PROFILE", User.class);

            //수정모드이면 이거 받음
            editPostId = getArguments().getString("EDIT_POST_ID");
            editPostData = getArguments().getSerializable("EDIT_POST_DATA", Post.class);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityWriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 수정 모드라면 UI 초기화 (기존 내용 채우기)
        if (editPostId != null && editPostData != null) {
            initEditMode();
        }

        WritePostClickListener(); //등록+수정 버튼 클릭 리스너
        BackButtonClickListener(); //돌아가기 버튼 클릭 리스너
    }

    // 수정 모드일 때 화면 세팅
    private void initEditMode() {
        binding.textCount.setText("글 수정 중"); // 상단 타이틀 변경 (필요 시)
        binding.btnPostSubmit.setText("수정"); // 버튼 글자 변경

        // 기존 내용 채워넣기
        binding.setPostTitle.setText(editPostData.getTitle());
        binding.setPostContent.setText(editPostData.getContent());

        // 기존 카테고리 선택
        String category = editPostData.getCategory();
        if (category != null) {
            switch (category) {
                case "일반": binding.toggleGroup.check(R.id.normal_btn); break;
                case "조립": binding.toggleGroup.check(R.id.pc_building_btn); break;
                case "질문/토론": binding.toggleGroup.check(R.id.question_btn); break;
                case "견적": binding.toggleGroup.check(R.id.pc_quote_btn); break;
                case "정보": binding.toggleGroup.check(R.id.information); break;
            }
        }
    }
    private void WritePostClickListener(){
        binding.btnPostSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                //입력 데이터 가져오기
                String title = binding.setPostTitle.getText().toString().trim();
                String content = binding.setPostContent.getText().toString().trim();

                String category = getSelectedCategory(); //카테고리 선택 확인

                //유효성 검사 (빈 칸 방지)
                if(title.isEmpty() || content.isEmpty()){
                    Toast.makeText(getContext(),"제목 혹은 본문을 채워주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                //카테고리 선택 확인
                if(category == null){
                    Toast.makeText(getContext(),"카테고리를 설정해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                // [분기점] 수정 모드 vs 새 글 작성 모드
                if (editPostId != null) {
                    // === 수정 로직 ===
                    updatePost(title, content, category);
                } else {
                    // === 새 글 작성 로직 ===
                    createNewPost(title, content, category);
                }


            }
        });
    }

    private void updatePost(String title, String content, String category){
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("content", content);
        updates.put("category", category);
        updates.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("posts").document(editPostId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack(); // 뒤로 가기
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "수정 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createNewPost(String title, String content, String category){
        Post newPost = new Post(title, content, category, currentUser.getUid() ,currentUser.getNickname());

        // 레벨업 및 경험치 로직 계산
        // 예시: 글 작성 시 경험치 +10, (현재 레벨 * 100) 경험치 도달 시 레벨업
        long xpReward = 10;
        long currentXp = currentUser.getExperience() + xpReward;
        long currentLevel = currentUser.getLevel();
        long requiredXp = 100; // 레벨 1->2 필요경험치: 100 설정

        boolean isLevelup = false;
        if (currentXp >= requiredXp){
            currentLevel++;
            currentXp = currentXp - requiredXp; //레벨업 후 남은 경험치
            isLevelup = true;
        }

        // Firestore Batch(일괄 처리) 시작
        WriteBatch batch = db.batch();

        // 게시글 저장 경로 설정 (자동 ID 생성)
        DocumentReference postRef = db.collection("posts").document();
        batch.set(postRef, newPost);

        // 유저 정보 업데이트 경로 설정
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        batch.update(userRef, "experience", currentXp);
        batch.update(userRef, "level", currentLevel);

        // 값 final 설정
        boolean finalIsLevelUp = isLevelup;
        final long finalXp = currentXp;
        final long final_level = currentLevel;

        //DB에 user정보랑 Post 업데이트!!
        batch.commit().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "User and Post update Success!!");
            // 성공 시 처리
            if (finalIsLevelUp) {
                // 레벨업 축하 메시지
                Toast.makeText(getContext(), "레벨업! 현재 레벨: " + finalIsLevelUp, Toast.LENGTH_LONG).show();
            }

            //currentUser 정보 업데이트해서 넘겨주기
            currentUser.setExperience(finalXp);
            currentUser.setLevel(final_level);

            // ViewModel에 변경된 정보 쏘기
            // requireActivity()를 쓰는 이유는 '액티비티'를 통해 공유하기 위함입니다.
            new ViewModelProvider(requireActivity()).get(UserViewModel.class).setUser(currentUser);

            // 화면 종료 (뒤로 가기)
            getParentFragmentManager().popBackStack();

        }).addOnFailureListener(e -> {
            Log.d(TAG, "User and Post update failed >: ");
            // 실패 시 처리
            Toast.makeText(getContext(), "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void BackButtonClickListener(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
    
    private String getSelectedCategory(){
        int checkId = binding.toggleGroup.getCheckedButtonId();
        if (checkId == binding.normalBtn.getId()) return "일반";
        if (checkId == binding.pcBuildingBtn.getId()) return "조립";
        if (checkId == binding.questionBtn.getId()) return "질문/토론";
        if (checkId == binding.pcQuoteBtn.getId()) return "견적";
        if (checkId == binding.information.getId()) return "정보";
        return null;
    }
}
