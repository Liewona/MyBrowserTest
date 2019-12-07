package com.example.myviabrowser.utils;

import android.content.Context;
import android.os.Build;

import com.example.myviabrowser.R;
import com.example.myviabrowser.fragments.WebViewFragment;
import com.example.myviabrowser.webclient.MyWebClient;
import com.example.myviabrowser.webclient.MyWebView;
import com.example.myviabrowser.webclient.PageMessage;

public class WebViewHelper {

    public static String defaultUrl = "http://m.bbaidu.com/s/wd=";

    public static void openNewURL(String url, Context context, MyWebView view) {
        ThreadUtils.setInUIThread(() -> {
            view.onPause();
            PageMessage pageMessage = view.getPageMessage();
            WebViewFragment currentFragment = new WebViewFragment();
            WebViewFragment oldCurrentFragment = pageMessage.parentFragment.getCurrentFragment();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PageMessage.reloadOrGoImg.setImageDrawable(context.getDrawable(R.drawable.ic_close));
            }
            pageMessage.backWebViewFragments.push(oldCurrentFragment);
            pageMessage.clearNext();
            pageMessage.parentFragment.changedCurrentFragment(currentFragment);
            currentFragment.setPageMessage(pageMessage);
            currentFragment.setUrl(url);
            FragmentShowUtil.addFragment(pageMessage.fragmentManager, R.id.web_view_fragment_pager, oldCurrentFragment, currentFragment);
        });


    }

    public static void searchWord(String word, Context context, MyWebView view) {
        openNewURL(defaultUrl + word, context, view);
    }
}
