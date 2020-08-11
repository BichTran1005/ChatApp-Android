package com.tranthibich.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {


    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 1:
                FindFriendsFragment findFriendsFragment = new FindFriendsFragment();
                return findFriendsFragment;


            case 2:
                SettingFragment settingFragment = new SettingFragment();
                return settingFragment;


            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Tin Nhắn";

            case 1:
                return "Liên Hệ";

            case 2:
                return "Cài Đặt";

            default:
                return null;
        }
    }
}
