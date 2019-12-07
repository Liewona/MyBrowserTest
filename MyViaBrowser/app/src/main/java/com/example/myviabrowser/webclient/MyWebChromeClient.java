package com.example.myviabrowser.webclient;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.myviabrowser.R;
import com.example.myviabrowser.viewpager.MyFragmentPagerAdapter;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MyWebChromeClient extends WebChromeClient {

    private Context mContext;
    private ProgressBar mProgressBar;
    private MyWebClient client;
    //false 表示progressbar没有显示 true表示显示
    private static boolean progressFlag = false;
    private PageMessage pageMessage;

    public MyWebChromeClient(Context context) {
        this(context, null);
    }

    public MyWebChromeClient(Context context, ProgressBar progressBar) {
        mContext = context;
        this.mProgressBar = progressBar;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
//        if(client == null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                client = (MyWebClient) view.getWebViewClient();
//            }
//        }
        if(pageMessage == null) {
            pageMessage = ((MyWebView) view).getPageMessage();
        }
        if(! TextUtils.isEmpty(title)) {
            EditText edit = ((Activity) mContext).findViewById(R.id.url_edit_text);
            edit.setText(view.getTitle());

            pageMessage.titles.put(pageMessage.parentFragment.getCurrentFragment(), title);
        }



    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (mProgressBar != null) {
            if(! progressFlag) {
                mProgressBar.setVisibility(View.VISIBLE);
                progressFlag = true;
            }
//            PageMessage.reloadOrGoImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_close));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mProgressBar.setProgress(newProgress, true);
            } else {
                mProgressBar.setProgress(newProgress);
            }

            if(newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
                progressFlag = false;
            }
//            Log.d("mylog", "onProgressChanged: " + newProgress + " : " + view);
        }
    }

    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

}
