package com.example.myviabrowser.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentShowUtil {
    public static void replaceFragmentShow(FragmentManager fragmentManager, int replaceViewId, Fragment targetFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(replaceViewId, targetFragment);
        transaction.commit();
    }

    public static void showFragment(FragmentManager fragmentManager,  Fragment nowFragment, Fragment targetFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        nowFragment.onPause();
        targetFragment.onResume();
        transaction.hide(nowFragment).show(targetFragment).commit();
    }

    public static void addFragment(FragmentManager fragmentManager, int replaceViewId, Fragment nowFragment,Fragment targetFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        nowFragment.onPause();
        targetFragment.onResume();
        if(targetFragment.isAdded()) {
            transaction.hide(nowFragment).show(targetFragment).commit();
        } else {
            transaction.hide(nowFragment).add(replaceViewId, targetFragment).show(targetFragment).commit();
        }
    }
}
