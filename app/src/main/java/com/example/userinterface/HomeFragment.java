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

import com.example.userinterface.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private User currentUser;
    private HomePagerAdapter pagerAdapter;
    private String[] tabTitles = new String[]{"최신 게시글", "내가 쓴 게시글", "북마크"};

    private String TAG = "HomeFragment";
    // 생성자 패턴
    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_PROFILE", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getSerializable("USER_PROFILE", User.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayUserProfile();
        setupCardClickListeners();
        setupCommunityViewPager();

        //방송 듣기 설정
        new ViewModelProvider(requireActivity()).get(UserViewModel.class).getUser().observe(getViewLifecycleOwner(), updatedUser -> {
            //user 정보가 바뀔 때 실행
            if (updatedUser != null) {
                //현재 프래그먼트의 변수 업데이트
                currentUser = updatedUser;
                Log.d(TAG, "UserUpdate");

                //User 프로필 재설정
                displayUserProfile();
            }
        });
    }

    private void displayUserProfile() {
        if (currentUser != null) {
            binding.homeUsername.setText(currentUser.getNickname());
            binding.homeLevel.setText("Lv." + currentUser.getLevel());
            binding.homeExperienceBar.setMax(100);
            binding.homeExperienceBar.setProgress((int) currentUser.getExperience());
            binding.homeExperiencePoints.setText(currentUser.getExperience() + "/100");
            binding.homeWelcome.setText("안녕하세요, " + currentUser.getNickname() + "님!");

            if (currentUser.getJoinDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 가입", Locale.KOREA);
                String formattedDate = sdf.format(currentUser.getJoinDate());
                binding.homeJoinDate.setText(formattedDate);
            }
            Log.d(TAG, "load user profile");

        } else {
            Toast.makeText(getContext(), "프로필 로드 실패", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "failed load profile");
        }
    }

    private void setupCardClickListeners() {
        // 클릭 시 HomeActivity의 BottomNavigation을 조작하여 화면 전환
        binding.findComponent.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).selectBottomNavigationItem(R.id.navigation_search);
            }
        });

        binding.guide.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).selectBottomNavigationItem(R.id.navigation_guide);
            }
        });

        binding.community.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).selectBottomNavigationItem(R.id.navigation_community);
            }
        });

        binding.recommendedBuilds.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).selectBottomNavigationItem(R.id.navigation_recommended_builds);
            }
        });
    }

    private void setupCommunityViewPager() {
        // Fragment 내부에서는 getChildFragmentManager()를 사용하는 것이 안전하지 않을 수 있지만
        // ViewPager2 + FragmentStateAdapter에서는 getActivity() 또는 this를 사용
        pagerAdapter = new HomePagerAdapter(getActivity());
        binding.communityViewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.communityLayout, binding.communityViewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}