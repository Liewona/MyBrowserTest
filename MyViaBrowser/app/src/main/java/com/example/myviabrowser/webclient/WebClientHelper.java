package com.example.myviabrowser.webclient;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebClientHelper {

    private Context mContext;

    public WebClientHelper(Context context) {
        mContext = context;
    }

    private boolean handleUri(WebView webView, final Uri uri) {

        Log.i(TAG, "Uri =" + uri);
        final String url = uri.toString();
        // Based on some condition you need to determine if you are going to load the url
        // in your web view itself or in a browser.
        // You can use `host` or `scheme` or any part of the `uri` to decide.
        // open web links as usual

        if (url.startsWith("http")) {
            webView.loadUrl(url);
            return true;
        }

        //try to find browse activity to handle uri
        Uri parsedUri = Uri.parse(url);
        PackageManager packageManager = mContext.getPackageManager();
        Intent browseIntent = new Intent(Intent.ACTION_VIEW).setData(parsedUri);
        if (browseIntent.resolveActivity(packageManager) != null) {
            mContext.startActivity(browseIntent);
            return true;
        }
        //if not activity found, try to parse intent://
        if (url.startsWith("intent:")) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    try {
                        mContext.startActivity(intent);
                    } catch (Exception e) {
//                        NinjaToast.show(context, R.string.toast_load_error);
                    }
                    return true;
                }
                //try to find fallback url
                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                if (fallbackUrl != null) {
                    webView.loadUrl(fallbackUrl);
                    return true;
                }
                //invite to install
                Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + intent.getPackage()));
                if (marketIntent.resolveActivity(packageManager) != null) {
                    mContext.startActivity(marketIntent);
                    return true;
                }
            } catch (URISyntaxException e) {
                //not an intent uri
                return false;
            }
        }
//        white = adBlock.isWhite(url);
        return true;//do nothing in other cases
    }


    private static final String TAG = "IntentUtils";
    /**
     * 系统可以处理的url正则
     */
    private static final Pattern ACCEPTED_URI_SCHEME = Pattern.compile("(?i)"
            + // switch on case insensitive matching
            '('
            + // begin group for scheme
            "(?:http|https|ftp|file)://" + "|(?:inline|data|about|javascript):" + "|(?:.*:.*@)"
            + ')' + "(.*)");


    public boolean shouldOverrideUrlLoadingByApp(WebView view, String url) {
        return shouldOverrideUrlLoadingByAppInternal(view, url, true);
    }

    private boolean shouldOverrideUrlLoadingByAppInternal(WebView view, String url, boolean interceptExternalProtocol) {
        if (isAcceptedScheme(url)) return false;
        Intent intent;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            Log.e(TAG, "URISyntaxException: " + e.getLocalizedMessage());
            return interceptExternalProtocol;
        }

        intent.setComponent(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            intent.setSelector(null);
        }

        //intent://dangdang://product//pid=23248697#Intent;scheme=dangdang://product//pid=23248697;package=com.dangdang.buy2;end
        //该Intent无法被设备上的应用程序处理
        if (mContext.getPackageManager().resolveActivity(intent, 0) == null) {
            return tryHandleByMarket(intent) || interceptExternalProtocol;
        }

        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "ActivityNotFoundException: " + e.getLocalizedMessage());
            return interceptExternalProtocol;
        }
        return true;
    }

    private boolean tryHandleByMarket(Intent intent) {
        String packagename = intent.getPackage();
        if (packagename != null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                    + packagename));
            try{
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "tryHandleByMarket ActivityNotFoundException: " + e.getLocalizedMessage());
                return false;
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * 该url是否属于浏览器能处理的内部协议
     */

    private boolean isAcceptedScheme(String url) {
        //正则中忽略了大小写，保险起见，还是转成小写
        String lowerCaseUrl = url.toLowerCase();
        Matcher acceptedUrlSchemeMatcher = ACCEPTED_URI_SCHEME.matcher(lowerCaseUrl);
        if (acceptedUrlSchemeMatcher.matches()) {
            return true;
        }
        return false;
    }
}
