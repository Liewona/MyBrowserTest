package com.example.myviabrowser.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.myviabrowser.R;
import com.example.myviabrowser.utils.FragmentShowUtil;
import com.example.myviabrowser.webclient.PageMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnceViewAllFragment extends Fragment {

    private WebViewFragment currentFragment;
    private PageMessage pageMessage;
    private String startUrl;

    public OnceViewAllFragment() {
        // Required empty public constructor
    }

    public OnceViewAllFragment(String startUrl) {
        this.startUrl = startUrl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initPager();
    }

    private void initPager() {
        if(startUrl == null) {
            startUrl = getResources().getString(R.string.start_page_html);
        }
        currentFragment = new WebViewFragment();
        pageMessage.parentFragment = this;
        currentFragment.setPageMessage(pageMessage);
        currentFragment.setUrl(startUrl);
        FragmentManager manager = getFragmentManager();
        pageMessage.fragmentManager = manager;
        FragmentShowUtil.addFragment(manager, R.id.web_view_fragment_pager, currentFragment, currentFragment);
    }

    public WebView getCurrentWebView() {
        return currentFragment.getWebView();
    }

    public PageMessage getPageMessage() {
        return pageMessage;
    }

    public void setPageMessage(PageMessage pageMessage) {
        this.pageMessage = pageMessage;
    }


    public void changedCurrentFragment(WebViewFragment nowCurrentFragment) {
        this.currentFragment = nowCurrentFragment;
    }

    public WebViewFragment getCurrentFragment() {
        return this.currentFragment;
    }

}
