package com.example.myviabrowser.webclient;

import android.widget.ImageView;

import com.example.myviabrowser.fragments.OnceViewAllFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class PageMessage {
    public Stack<Fragment> backWebViewFragments;
    public Stack<Fragment> nextWebViewFragments;
    public Map<Fragment, String> titles;
    public FragmentManager fragmentManager;
    public OnceViewAllFragment parentFragment;
    public static ImageView reloadOrGoImg;

    public PageMessage() {
        backWebViewFragments = new Stack<>();
        titles = new HashMap<>();
        nextWebViewFragments = new Stack<>();
    }

    public void clearNext() {
        Fragment fragment;
        while(! nextWebViewFragments.empty()) {
            fragment = nextWebViewFragments.pop();
            fragment.onDestroy();
        }
    }

}
