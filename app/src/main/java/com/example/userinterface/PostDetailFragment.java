package com.example.userinterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.userinterface.databinding.FragmentPostDetailBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class PostDetailFragment extends Fragment {

    private FirebaseFirestore db;
    private CommentAdapter commentAdapter;
    private FragmentPostDetailBinding binding;
    private String postId;
    private User currentUser;
    private Post currentPostData; // 현재 보고 있는 글 데이터 (수정 시 넘겨주기 위해 저장)
    private UserViewModel userViewModel;

    private boolean isLiked = false; // 현재 좋아요 상태
    private boolean isLikeProcess = false; // 중복 클릭 방지용

    private boolean isBookmarked = false; //현재 북마크 상태
    private boolean isBookmarkProcess = false; //중복 클릭 방지용

    // [추가] 로딩 상태 확인용 변수
    private boolean isPostLoaded = false;
    private boolean isCommentsLoaded = false;

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
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
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

        // 처음에는 로딩바 보이고, 내용은 숨김
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.GONE);

        // 로딩 상태 초기화
        isPostLoaded = false;
        isCommentsLoaded = false;

        //comment내용 불러오기 + 삭제
        setupCommentPlusDelete();
        //post내용 불러오기
        setupPostDetail();

        //user의 추천 상태 불러오기
        setupLikesStatus();

        //user의 bookmark 상태 불러오기
        setupBookmarkStatus();
        
        //뒤로가기 클릭 리스너
        setupBackButton();

        //댓글 등록 클릭 리스너
        setupCommentButton();


        //추천 클릭 리스너
        setupLikesButton();

        //북마크 클릭 리스너
        setupBookmarkButton();
    }

    private void checkLoadingState() {
        // 게시글과 댓글이 모두 로드되었을 때만 화면을 보여줌
        if (isPostLoaded && isCommentsLoaded) {
            binding.progressBar.setVisibility(View.GONE);
            binding.nestedScrollView.setVisibility(View.VISIBLE);
        }
    }
    private void setupCommentPlusDelete(){
        if(postId == null) return;
        // 쿼리 만들기: 해당 게시글(postId)의 "comments" 서브컬렉션을 날짜순으로 가져옴
        Query query = db.collection("posts").document(postId).collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING); // 최신순

        // 옵션 설정
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        // 어댑터 생성 및 연결
        commentAdapter = new CommentAdapter(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                // 데이터가 로드되면(변경되면) 로딩 완료 처리
                if (!isCommentsLoaded) {
                    isCommentsLoaded = true;
                    checkLoadingState(); // 상태 체크
                }
            }
            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                // 에러가 나더라도 무한 로딩에 걸리지 않게 완료 처리 해버림
                isCommentsLoaded = true;
                checkLoadingState();
            }
        };
        binding.postCommentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.postCommentRecycler.setAdapter(commentAdapter);

        // 댓글 삭제 리스너
        commentAdapter.setOnCommentDeleteListener(documentSnapshot -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("댓글 삭제")
                    .setMessage("댓글을 삭제하시겠습니까?\n(경험치 100이 차감됩니다)")
                    .setNegativeButton("삭제", (dialog, which) -> {
                        String commentId = documentSnapshot.getId();
                        String commentAuthorId = documentSnapshot.getString("userId"); // 댓글 작성자 ID

                        // 트랜잭션 시작
                        db.runTransaction(transaction -> {
                            DocumentReference postRef = db.collection("posts").document(postId);
                            DocumentReference commentRef = postRef.collection("comments").document(commentId);

                            // 댓글 작성자 정보 가져오기
                            DocumentReference authorRef = null;
                            long currentXp = 0;
                            long currentLevel = 1;

                            if (commentAuthorId != null) {
                                authorRef = db.collection("users").document(commentAuthorId);
                                DocumentSnapshot authorSnapshot = transaction.get(authorRef);
                                if (authorSnapshot.exists()) {
                                    Long xpVal = authorSnapshot.getLong("experience");
                                    if (xpVal != null) currentXp = xpVal;
                                    Long lvVal = authorSnapshot.getLong("level");
                                    if (lvVal != null) currentLevel = lvVal;
                                }
                            }

                            // 경험치 -100 차감 및 레벨 다운 계산
                            long lostXp = 100;
                            long finalXp = currentXp - lostXp;
                            long requiredXp = 100;

                            // 경험치가 음수가 되면 레벨 다운
                            while (finalXp < 0) {
                                if (currentLevel > 1) {
                                    currentLevel--;
                                    finalXp += requiredXp; // 이전 레벨의 경험치로 환산
                                } else {
                                    finalXp = 0; // 레벨 1이면 0에서 멈춤
                                    break;
                                }
                            }

                            // DB 업데이트
                            transaction.delete(commentRef); // 댓글 삭제
                            transaction.update(postRef, "countComments", FieldValue.increment(-1)); // 게시글 댓글 수 -1

                            if (authorRef != null) {
                                transaction.update(authorRef, "experience", finalXp);
                                transaction.update(authorRef, "level", currentLevel);
                            }
                            if (currentUser.getUid().equals(commentAuthorId)) {
                                Map<String, Object> result = new HashMap<>();
                                result.put("level", currentLevel);
                                result.put("experience", finalXp);
                                return result;
                            }

                            return null;

                        }).addOnSuccessListener(result -> {
                            Toast.makeText(getContext(), "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            if (result != null) {
                                long newLevel = (long) result.get("level");
                                long newXp = (long) result.get("experience");

                                currentUser.setLevel(newLevel);
                                currentUser.setExperience(newXp);
                                userViewModel.setUser(currentUser);
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    })
                    .setPositiveButton("취소", null)
                    .show();
        });

        binding.postCommentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.postCommentRecycler.setAdapter(commentAdapter);
    }
    private void setupPostDetail(){
        if(postId == null) return;
        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                currentPostData = documentSnapshot.toObject(Post.class);

                                if (currentPostData != null) {
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
                                    if (currentUser != null && currentUser.getUid().equals(currentPostData.getUserId())) {
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

                                    // [수정] 로딩바 끄는 코드 삭제 -> 상태 변경 함수 호출
                                    isPostLoaded = true;
                                    checkLoadingState();
                                }
                            } else{
                                isPostLoaded = true; // 로딩은 끝난 것임
                                // 북마크 상태인지 확인 후 처리
                                checkLoadingState();
                                checkIfBookmarkedAndPromptDelete();
                            }
                }).addOnFailureListener(e -> {
                    // 데이터 로드 실패 시 처리
                    Toast.makeText(getContext(), "게시글을 불러오지 못했습니다.", android.widget.Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                });
    }

    // 삭제된 글일 때 처리
    private void checkIfBookmarkedAndPromptDelete() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        // 내 북마크에 있는지 확인
        db.collection("users").document(currentUser.getUid())
                .collection("bookmarks").document(postId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        // 북마크에 있는데 원본이 지워진 상황 -> 삭제 권유 다이얼로그
                        showDeadBookmarkDialog();
                    } else {
                        // 북마크도 아니고 원본도 없음 -> 그냥 뒤로가기
                        Toast.makeText(getContext(), "삭제된 게시글입니다.", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    }
                });
    }

    // 죽은 북마크 삭제 다이얼로그
    private void showDeadBookmarkDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("알림")
                .setMessage("원본 게시글이 삭제되었습니다.\n내 북마크 목록에서 삭제하시겠습니까?")
                .setCancelable(false) // 뒤로가기 막기 (선택해야 함)
                .setNegativeButton("삭제하기", (dialog, which) -> {
                    // 북마크 삭제 수행
                    db.collection("users").document(currentUser.getUid())
                            .collection("bookmarks").document(postId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "북마크가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack(); // 목록으로 복귀
                            });
                })
                .setPositiveButton("그냥 나가기", (dialog, which) -> {
                    getParentFragmentManager().popBackStack();
                })
                .show();
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
            //  먼저 해당 게시글의 모든 댓글을 가져옴
            db.collection("posts").document(postId).collection("comments")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        // Batch 생성 (한 번에 여러 삭제 작업을 묶어서 처리)
                        com.google.firebase.firestore.WriteBatch batch = db.batch();

                        // 모든 댓글을 삭제 목록에 추가
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            batch.delete(document.getReference());
                        }

                        // 게시글 자체도 삭제 목록에 추가
                        batch.delete(db.collection("posts").document(postId));

                        // 최종 실행 (Commit)
                        batch.commit()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    getParentFragmentManager().popBackStack(); // 뒤로 가기
                                })
                                .addOnFailureListener(e -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    })
                    .addOnFailureListener(e -> {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "댓글 정보를 불러오지 못해 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    //추천 상태 확인
    private void setupLikesStatus(){
        if(currentUser == null || postId == null) return;

        db.collection("posts").document(postId)
                .collection("likes").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot ->{
                    if (documentSnapshot.exists()){
                        isLiked = true;
                        binding.iconLikes.setImageResource(R.drawable.ic_thumb_up);
                    }
                    else{
                        isLiked = false;
                        binding.iconLikes.setImageResource(R.drawable.ic_thumb_up_border);
                    }
                });
    }

    //북마크 상태 표시
    private void setupBookmarkStatus(){
        if(currentUser == null || postId == null) return;

        db.collection("users").document(currentUser.getUid())
                .collection("bookmarks").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        isBookmarked = true;
                        binding.btnDetailBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    }
                    else{
                        isBookmarked = false;
                        binding.btnDetailBookmark.setImageResource(R.drawable.ic_bookmark_border);
                    }
                });
    }
    private void setupBackButton(){
        binding.btnDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    //댓글 클릭 리스너
    private void setupCommentButton(){
        binding.btnCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = binding.setCommentContent.getText().toString().trim();
                if(content.isEmpty()){
                    Toast.makeText(getContext(), "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(currentUser == null) return;

                // 트랜잭션 시작
                db.runTransaction(transaction -> {
                    DocumentReference postRef = db.collection("posts").document(postId);
                    DocumentReference userRef = db.collection("users").document(currentUser.getUid());
                    DocumentReference newCommentRef = postRef.collection("comments").document();

                    // 유저 정보 최신 상태로 읽어오기 (경험치 계산용)
                    DocumentSnapshot userSnapshot = transaction.get(userRef);
                    long currentXp = 0;
                    long currentLevel = 1;

                    if (userSnapshot.exists()) {
                        Long xpVal = userSnapshot.getLong("experience");
                        if (xpVal != null) currentXp = xpVal;

                        Long lvVal = userSnapshot.getLong("level");
                        if (lvVal != null) currentLevel = lvVal;
                    }

                    // 경험치 +100 증가 및 레벨업 계산
                    long gainedXp = 100;
                    long finalXp = currentXp + gainedXp;
                    long requiredXp = 100; // 레벨업 필요 경험치 (예: 100)

                    // 경험치가 100 이상이면 레벨업 (여러 번 레벨업 가능하도록 while 사용)
                    while (finalXp >= requiredXp) {
                        currentLevel++;
                        finalXp -= requiredXp;
                    }

                    // 댓글 객체 생성
                    Comment newComment = new Comment(
                            currentUser.getNickname(),
                            currentUser.getUid(),
                            (int) currentLevel, // 갱신된 레벨 저장
                            content
                    );

                    transaction.set(newCommentRef, newComment); // 댓글 저장
                    transaction.update(postRef, "countComments", com.google.firebase.firestore.FieldValue.increment(1)); // 게시글 댓글 수 +1
                    transaction.update(userRef, "experience", finalXp); // 유저 경험치 갱신
                    transaction.update(userRef, "level", currentLevel); // 유저 레벨 갱신

                    // 레벨 + 경험치 같이 리턴
                    Map<String, Object> result = new HashMap<>();
                    result.put("level", currentLevel);
                    result.put("experience", finalXp);
                    return result;

                }).addOnSuccessListener(result -> {
                    long newLevel = (long) result.get("level");
                    long newXp = (long) result.get("experience");
                    // 트랜잭션 성공 후 UI 업데이트
                    Toast.makeText(getContext(), "댓글 등록 완료!", Toast.LENGTH_SHORT).show();
                    binding.setCommentContent.setText(""); // 입력창 비우기
                    // 내 로컬 정보(currentUser) 업데이트 후 ViewModel에 전파
                    currentUser.setLevel(newLevel);
                    currentUser.setExperience(newXp);
                    userViewModel.setUser(currentUser); // -> HomeFragment가 이걸 감지하고 UI를 바꿈!
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "전송 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    //추천 버튼 클릭리스너
    private void setupLikesButton() {
        binding.btnPostLike.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            if (isLikeProcess) return;

            isLikeProcess = true; // 처리 시작

            db.runTransaction(transaction -> {
                DocumentReference postRef = db.collection("posts").document(postId);
                DocumentReference likeRef = postRef.collection("likes").document(currentUser.getUid());

                DocumentSnapshot postSnapshot = transaction.get(postRef);
                DocumentSnapshot likeSnapshot = transaction.get(likeRef);

                // 게시글 정보 및 작성자 ID 확인
                long currentLikes = 0;
                String authorId = null;
                if (postSnapshot.exists()) {
                    if (postSnapshot.contains("likes")) {
                        Double val = postSnapshot.getDouble("likes");
                        currentLikes = (val != null) ? val.longValue() : 0;
                    }
                    authorId = postSnapshot.getString("userId");
                }

                // 작성자 정보 가져오기
                DocumentReference authorRef = null;
                DocumentSnapshot authorSnapshot = null;
                if (authorId != null) {
                    authorRef = db.collection("users").document(authorId);
                    authorSnapshot = transaction.get(authorRef);
                }

                long newLikes = currentLikes;

                // [반환할 결과 맵 생성]
                Map<String, Object> result = new HashMap<>();

                // === [분기] 좋아요 취소 vs 추가 ===
                if (likeSnapshot.exists()) {
                    // [취소] 좋아요 삭제
                    transaction.delete(likeRef);
                    newLikes = (currentLikes > 0) ? currentLikes - 1 : 0;
                    transaction.update(postRef, "likes", newLikes);

                    // 경험치 차감
                    if (authorSnapshot != null && authorSnapshot.exists()) {
                        long currentXp = 0;
                        long currentLevel = 1;
                        Long xpVal = authorSnapshot.getLong("experience");
                        Long lvVal = authorSnapshot.getLong("level");

                        if (xpVal != null) currentXp = xpVal;
                        if (lvVal != null) currentLevel = lvVal;

                        long penalty = 50;
                        // 추천수 10 -> 9로 떨어질 때 대량 차감
                        if (currentLikes == 10) {
                            penalty += 5000;
                        }

                        long finalXp = currentXp - penalty;
                        long requiredXp = 100;

                        // 레벨 다운 로직
                        while (finalXp < 0) {
                            if (currentLevel > 1) {
                                currentLevel--;
                                finalXp += requiredXp;
                            } else {
                                finalXp = 0;
                                break;
                            }
                        }
                        transaction.update(authorRef, "experience", finalXp);
                        transaction.update(authorRef, "level", currentLevel);

                        // 작성자가 본인이라면 갱신된 정보 담기
                        if (currentUser.getUid().equals(authorId)) {
                            result.put("myNewXp", finalXp);
                            result.put("myNewLevel", currentLevel);
                        }
                    }
                } else {
                    // [추가] 좋아요 생성
                    Map<String, Object> data = new HashMap<>();
                    data.put("timestamp", FieldValue.serverTimestamp());
                    transaction.set(likeRef, data);

                    newLikes = currentLikes + 1;
                    transaction.update(postRef, "likes", newLikes);

                    // 경험치 지급
                    if (authorSnapshot != null && authorSnapshot.exists()) {
                        long currentXp = 0;
                        long currentLevel = 1;
                        Long xpVal = authorSnapshot.getLong("experience");
                        Long lvVal = authorSnapshot.getLong("level");

                        if (xpVal != null) currentXp = xpVal;
                        if (lvVal != null) currentLevel = lvVal;

                        long gainedXp = 50;
                        // 10번째 추천 달성 시 보너스
                        if (newLikes == 10) {
                            gainedXp += 5000;
                        }

                        long finalXp = currentXp + gainedXp;
                        long requiredXp = 100;

                        // 레벨업 로직
                        while (finalXp >= requiredXp) {
                            currentLevel++;
                            finalXp -= requiredXp;
                        }
                        transaction.update(authorRef, "experience", finalXp);
                        transaction.update(authorRef, "level", currentLevel);

                        // 작성자가 본인이라면 갱신된 정보 담기
                        if (currentUser.getUid().equals(authorId)) {
                            result.put("myNewXp", finalXp);
                            result.put("myNewLevel", currentLevel);
                        }
                    }
                }

                // 최종 좋아요 수 담아서 리턴
                result.put("likes", newLikes);
                return result;

            }).addOnSuccessListener(result -> {

                isLikeProcess = false;

                // Map에서 좋아요 수 꺼내기 (형변환 주의)
                long likes = 0;
                Long ll = (Long) result.get("likes");
                if(ll != null) {
                    likes = ll;
                }


                // 아이콘 및 텍스트 갱신
                if (isLiked) {
                    isLiked = false;
                    binding.iconLikes.setImageResource(R.drawable.ic_thumb_up_border);
                } else {
                    isLiked = true;
                    binding.iconLikes.setImageResource(R.drawable.ic_thumb_up);
                }
                binding.postLikeCount.setText(String.valueOf(likes));

                // 내 경험치/레벨이 변했다면 ViewModel 업데이트 (내 글에 내가 좋아요 누른 경우)
                if (result.containsKey("myNewXp") && result.containsKey("myNewLevel")) {
                    Long xpObj = (Long) result.get("myNewXp");
                    Long levelObj = (Long) result.get("myNewLevel");

                    if (xpObj != null && levelObj != null) {
                        long newXp = xpObj;
                        long newLevel = levelObj;

                        currentUser.setExperience(newXp);
                        currentUser.setLevel(newLevel);

                        if (userViewModel != null) {
                            userViewModel.setUser(currentUser);
                        }
                    }
                }

            }).addOnFailureListener(e -> {
                isLikeProcess = false;
                Toast.makeText(getContext(), "오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void setupBookmarkButton(){
        binding.btnDetailBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }

                if (isBookmarkProcess) return;
                isBookmarkProcess = true;

                DocumentReference bookmarkRef = db.collection("users").document(currentUser.getUid())
                        .collection("bookmarks").document(postId);

                if(isBookmarked){
                    //[삭제] 이미 북마크 상태
                    bookmarkRef.delete().addOnSuccessListener(aVoid -> {
                        isBookmarkProcess = false;
                        isBookmarked = false;
                        binding.btnDetailBookmark.setImageResource(R.drawable.ic_bookmark_border);
                    });
                }
                else{
                    // [추가] 북마크 안 된 상태 -> 저장 (조회수, 추천수는 저장 x)
                    Map<String, Object> data = new HashMap<>();
                    data.put("title", currentPostData.getTitle());
                    data.put("category", currentPostData.getCategory());
                    data.put("userName", currentPostData.getUserName());
                    data.put("timestamp", FieldValue.serverTimestamp());
                    data.put("userId", currentPostData.getUserId());
                    bookmarkRef.set(data).addOnSuccessListener(aVoid -> {
                        isBookmarkProcess = false;
                        isBookmarked = true;
                        binding.btnDetailBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    });
                }
            }
        });
    }

    // [중요] 어댑터 생명주기 관리 (Fragment가 보일 때만 리스닝)
    @Override
    public void onStart() {
        super.onStart();
        if (commentAdapter != null) {
            commentAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (commentAdapter != null) {
            commentAdapter.stopListening();
        }
    }
}
