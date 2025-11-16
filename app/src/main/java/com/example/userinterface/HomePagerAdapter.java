package com.example.userinterface;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 탭 위치에 따라 다른 Fragment를 반환
        switch (position) {
            case 1:
                // "내가 쓴 게시글" 탭
                return PostListFragment.newInstance("my_posts");
            case 2:
                // "북마크" 탭
                return PostListFragment.newInstance("bookmarks");
            case 0:
            default:
                // "최신 게시글" 탭
                return PostListFragment.newInstance("latest");
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 탭 개수 (최신, 내 글, 북마크)
    }
}