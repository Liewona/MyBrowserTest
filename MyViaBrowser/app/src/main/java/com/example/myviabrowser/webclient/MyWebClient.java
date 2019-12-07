package com.example.myviabrowser.webclient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.myviabrowser.R;
import com.example.myviabrowser.fragments.WebViewFragment;
import com.example.myviabrowser.utils.FragmentShowUtil;

public class MyWebClient extends WebViewClient {

    private Context mContext;
    private boolean isRedirect = false;  //是否重定向
    private boolean isPageOk = false;  //目的地址是否加载完成
    private PageMessage pageMessage;
    private boolean realLoadFinish = false;  //确定最后一次，可以设置title了

    private ProgressBar mProgressBar;
    private WebView webView;

    public MyWebClient(Context context) {
        mContext = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.d("mylog", "shouldOverrideUrlLoading: ");
        String url;
        if(pageMessage == null) {
            pageMessage = ((MyWebView) view).getPageMessage();
        }
        loadOk = false;
        isRedirect = false;
        if (isPageOk) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
                WebClientHelper helper = new WebClientHelper(mContext);
                if (helper.shouldOverrideUrlLoadingByApp(view, url)) {
                    Log.d("mylog", "shouldOverrideUrlLoading: " + url);
                    return true;
                }
                view.onPause();
                WebViewFragment currentFragment = new WebViewFragment();
                WebViewFragment oldCurrentFragment = pageMessage.parentFragment.getCurrentFragment();

                PageMessage.reloadOrGoImg.setImageDrawable(mContext.getDrawable(R.drawable.ic_close));
                pageMessage.backWebViewFragments.push(oldCurrentFragment);
                pageMessage.clearNext();
                pageMessage.parentFragment.changedCurrentFragment(currentFragment);
                currentFragment.setPageMessage(pageMessage);
                currentFragment.setUrl(url);
                FragmentShowUtil.addFragment(pageMessage.fragmentManager, R.id.web_view_fragment_pager, oldCurrentFragment, currentFragment);

                return true;
            }
        }
//
        return false;
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }

    private boolean loadOk = false;
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
//        Log.d("mylog", "onPageStarted: " + view);
        isRedirect = true;
        isPageOk = false;
        loadOk = true;
        ((MyWebView) view).setLoading(true);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        isPageOk = isRedirect;
//        Log.d("mylog", "onPageFinished: " + view);
        if(loadOk) {
            PageMessage.reloadOrGoImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_reload));
            ((MyWebView) view).setLoading(false);
        }
    }


    public void setProgressBar(ProgressBar progressBar) {
        this.mProgressBar = progressBar;
    }


    public boolean getIsPageOk() {
        return isPageOk;
    }

    public boolean getIsRedirect() {
        return isRedirect;
    }

    public boolean getRealLoadFinish() {
        return realLoadFinish;
    }

    public void setRealLoadFinish(boolean realLoadFinish) {
        this.realLoadFinish = realLoadFinish;
    }
}
