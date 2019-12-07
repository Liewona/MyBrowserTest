package com.example.myviabrowser.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.example.myviabrowser.R;
import com.example.myviabrowser.webclient.MyWebChromeClient;
import com.example.myviabrowser.webclient.MyWebClient;
import com.example.myviabrowser.webclient.MyWebView;
import com.example.myviabrowser.webclient.PageMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {


    private Context mContext;
    private MyWebView mWebView;
    private ProgressBar mProgressBar;
    private MyWebChromeClient mChromeClient;
    private MyWebClient mWebClient;
    private String url;
    private PageMessage pageMessage;



    public WebViewFragment() {
//        mContext = getContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext(); // 测试
        mWebView = getView().findViewById(R.id.in_fragment_web_view);
        mProgressBar = getView().findViewById(R.id.web_progress_bar);
        mWebView.setProgressBar(mProgressBar);
        mWebView.setPageMessage(pageMessage);
        mWebView.loadUrl(url);
    }

    public void setPageMessage(PageMessage pageMessage) {
        this.pageMessage = pageMessage;
    }


    public MyWebView getWebView() {
        return mWebView;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
