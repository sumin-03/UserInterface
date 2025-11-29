package com.example.userinterface;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuideMenu4Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuideMenu4Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FrameLayout container;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GuideMenu4Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GuideMenu4Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuideMenu4Fragment newInstance(String param1, String param2) {
        GuideMenu4Fragment fragment = new GuideMenu4Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guide_menu4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.menu4_btn_back).setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        container = view.findViewById(R.id.container_guide_content); // XML에 추가한 FrameLayout ID

        // 1. 초기 화면 설정 (0번 탭 내용)
        loadLayout(0);

        // 2. 탭 리스너
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 탭을 누르면 해당 인덱스의 레이아웃을 불러옴
                loadLayout(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // 레이아웃 교체 함수
    private void loadLayout(int tabIndex) {
        // 1. 기존에 떠있던 뷰들을 싹 지운다.
        container.removeAllViews();

        // 2. 탭 번호에 따라 보여줄 XML 레이아웃 ID를 고른다.
        int layoutResId = 0;
        switch (tabIndex) {
            case 0: layoutResId = R.layout.view_guide_cpu; break; // 별도로 만든 XML 파일들
            case 1: layoutResId = R.layout.view_guide_ssd; break;
            case 2: layoutResId = R.layout.view_guide_cooler; break;
            case 3: layoutResId = R.layout.view_guide_ram; break;
            case 4: layoutResId = R.layout.view_guide_power; break;
            case 5: layoutResId = R.layout.view_guide_mainboard; break;
            case 6: layoutResId = R.layout.view_guide_gpu; break;
        }

        // 3. 선택된 레이아웃이 있다면 인플레이터로 뷰를 생성해서 컨테이너에 붙인다.
        if (layoutResId != 0) {
            // attachToRoot를 true로 설정하면 자동으로 container에 addView가 됩니다.
            getLayoutInflater().inflate(layoutResId, container, true);
        }
    }
}