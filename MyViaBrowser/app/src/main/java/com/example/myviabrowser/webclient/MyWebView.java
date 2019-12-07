package com.example.myviabrowser.webclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class MyWebView extends WebView {

    private Context mContext;
    private ProgressBar mProgressBar;
    private MyWebClient mWebClient;
    private MyWebChromeClient mChromeClient;
    private PageMessage pageMessage;
    private boolean isLoading;

    public MyWebView(Context context) {
        this(context, null);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        //阻止父View获取touch事件
        //阻止父View获取touch事件
        requestDisallowInterceptTouchEvent(true);
        mWebClient = new MyWebClient(mContext);
        mChromeClient = new MyWebChromeClient(mContext);
        setWebViewClient(mWebClient);
        setWebChromeClient(mChromeClient);
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.mProgressBar = progressBar;
        mChromeClient.setProgressBar(progressBar);
        mWebClient.setProgressBar(mProgressBar);
        initWebSettings(getSettings());
    }

    public void setPageMessage(PageMessage pageMessage) {
        this.pageMessage = pageMessage;
    }

    public PageMessage getPageMessage() {
        return pageMessage;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public ProgressBar getProgressBar() {
        return this.mProgressBar;
    }
    @SuppressLint("SetJavaScriptEnabled")
    private synchronized void initWebSettings(WebSettings webSettings) {
        // 在WebView中启用内容URL访问
        webSettings.setAllowContentAccess(true);
        //启用文件访问
        webSettings.setAllowFileAccess(true);
        //启用应用程序缓存
        webSettings.setAppCacheEnabled(true);
        //设置缓存路径
//        webSettings.setAppCachePath(path);
        //设置webView从网络加载图片资源（http和https协议的访问资源）
        webSettings.setBlockNetworkImage(false);
        //设置加载网络资源
        webSettings.setBlockNetworkLoads(false);
        //启用内置缩放机制
        webSettings.setBuiltInZoomControls(true);
        //设置缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //使用缩放机制时启用缩放控件显示
        webSettings.setDisplayZoomControls(false);
        //启用DOM存储
        webSettings.setDomStorageEnabled(true);
        //启用无里定位
        webSettings.setGeolocationEnabled(true);
        //javascript可自动打开窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //启用javascript
        webSettings.setJavaScriptEnabled(true);
        //适合屏幕加载内容
        webSettings.setLoadWithOverviewMode(true);
        //加载图像资源
        webSettings.setLoadsImagesAutomatically(true);
        //设置用户需要手势播放视频
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(true);
        }
        //当安全来源尝试从不安全来源加载资源时，配置WebView的行为
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //启用安全浏览
        if (Build.VERSION.SDK_INT >= 26) {
            webSettings.setSafeBrowsingEnabled(true);
        }
        //设置支持多个窗口
        webSettings.setSupportMultipleWindows(true);
        //设置支持手势缩放
        webSettings.setSupportZoom(true);
        //设置WebView是应启用对“视口” HTML元标记的支持还是应使用宽视口。
        webSettings.setUseWideViewPort(true);

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//设置渲染的优先级
    }



}
