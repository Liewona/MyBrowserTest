package com.example.myviabrowser;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myviabrowser.fragments.GrayViewFragment;
import com.example.myviabrowser.fragments.OnceViewAllFragment;
import com.example.myviabrowser.fragments.WebViewFragment;
import com.example.myviabrowser.utils.FragmentShowUtil;
import com.example.myviabrowser.utils.ThreadUtils;
import com.example.myviabrowser.utils.URLUtils;
import com.example.myviabrowser.utils.WebViewHelper;
import com.example.myviabrowser.webclient.MyWebView;
import com.example.myviabrowser.webclient.PageMessage;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private OnceViewAllFragment currentFragment;
    private List<Fragment> fragmentList;
    private FragmentManager fragmentManager;
    private PageMessage currentPageMessage;

    private View titleBar;  // 头部导航栏
    private View bottomBar;  //底部导航拉
    private View searchBtnView;  // 搜索按键
    private View reloadOrGoView; //刷新和加载按键
    private ImageView reloadOrGoImage;  // 刷新和加载图标
    private View urlBox;  // 输入框父空间
    private EditText urlEditBox;  //url输入框
    private View goBackView;  // 返回按钮
    private View goNextView; //前进按钮
    private View goHomeView; //回主页按钮
    private View showAllOnceWebViewFragmentView;  //查看打开的所有窗口
    private TextView showAllFragmentCount;  // 打开的窗口数量
    private View showMoreSettingsView;  // 更多设置
    private LinearLayout navBefore;
    private LinearLayout navAfter;
    private View goDownView;//下拉按钮
    private PopupWindow popupWindow;
    private Fragment graySet;
    private String inputText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
        initClickListener();
    }

    private void initFragment() {
        graySet = new GrayViewFragment();
        fragmentList = new ArrayList<>();
        currentFragment = new OnceViewAllFragment();
        currentPageMessage = new PageMessage();
        PageMessage.reloadOrGoImg = reloadOrGoImage;
        currentFragment.setPageMessage(currentPageMessage);
        fragmentList.add(currentFragment);
        fragmentManager = getSupportFragmentManager();
        FragmentShowUtil.replaceFragmentShow(fragmentManager, R.id.web_view_show_fragment, currentFragment);
    }



    private void initView() {
        titleBar = findViewById(R.id.title_nav_bar);
        searchBtnView = findViewById(R.id.search_btn_view);
        reloadOrGoImage = findViewById(R.id.to_url_or_refresh_img);
        reloadOrGoView = findViewById(R.id.reload_or_go_btn_view);
        urlBox = findViewById(R.id.url_edit_text_container);
        urlEditBox = findViewById(R.id.url_edit_text);
        bottomBar = findViewById(R.id.bottom_nav_bar);
        goBackView = findViewById(R.id.go_back_btn);
        goNextView = findViewById(R.id.go_next_btn);
        goHomeView = findViewById(R.id.go_home_btn);
        showAllOnceWebViewFragmentView = findViewById(R.id.go_other_web_view_btn);
        showAllFragmentCount = findViewById(R.id.web_view_once_fragment_count);
        showMoreSettingsView = findViewById(R.id.go_more_btn);
        navBefore = findViewById(R.id.nav_before);
        navAfter = findViewById(R.id.nav_after);
        goDownView = findViewById(R.id.go_down_btn);
    }


    public void initClickListener() {


        goBackView.setOnClickListener(e -> {
            if(currentFragment.getCurrentWebView().canGoBack()) {
                currentFragment.getCurrentWebView().goBack();
            } else {
                if(! currentPageMessage.backWebViewFragments.empty()) {
                    Fragment fragment = currentPageMessage.backWebViewFragments.pop();
                    currentFragment.getCurrentFragment().getWebView().onPause();
                    currentPageMessage.nextWebViewFragments.push(currentFragment.getCurrentFragment());
                    FragmentShowUtil.showFragment(fragmentManager, currentFragment.getCurrentFragment(), fragment);
                    currentPageMessage.parentFragment.changedCurrentFragment((WebViewFragment) fragment);
                    currentFragment.changedCurrentFragment((WebViewFragment) fragment);
                    ((WebViewFragment) fragment).getWebView().onResume();
                    urlEditBox.setText(currentPageMessage.titles.get(fragment));
                }
            }
        });

        goNextView.setOnClickListener(e -> {
            if(currentFragment.getCurrentWebView().canGoForward()) {
                currentFragment.getCurrentWebView().goForward();
            } else {
                if(! currentPageMessage.nextWebViewFragments.empty()) {
                    Fragment fragment = currentPageMessage.nextWebViewFragments.pop();
                    currentFragment.getCurrentFragment().getWebView().onPause();
                    currentPageMessage.backWebViewFragments.push(currentFragment.getCurrentFragment());
                    FragmentShowUtil.showFragment(fragmentManager, currentFragment.getCurrentFragment(), fragment);
                    currentPageMessage.parentFragment.changedCurrentFragment((WebViewFragment) fragment);
                    ((WebViewFragment) fragment).getWebView().onResume();
                    currentFragment.changedCurrentFragment((WebViewFragment) fragment);
                    urlEditBox.setText(currentPageMessage.titles.get(fragment));
                }
            }
        });

        goHomeView.setOnClickListener(e -> {
            String url = getResources().getString(R.string.start_page_html);
            if(currentFragment.getCurrentFragment().getUrl().equals(url)) {
                return;
            } else {
                WebViewFragment fragment = new WebViewFragment();
                fragment.setUrl(url);
                fragment.setPageMessage(currentPageMessage);
                currentFragment.getCurrentFragment().getWebView().onPause();
                currentPageMessage.backWebViewFragments.push(currentFragment.getCurrentFragment());
                FragmentShowUtil.addFragment(fragmentManager, currentFragment.getCurrentFragment().getId(), currentFragment.getCurrentFragment(), fragment);
                currentFragment.changedCurrentFragment(fragment);
                currentPageMessage.clearNext();
            }
        });

        showAllOnceWebViewFragmentView.setOnClickListener(e -> {
            ThreadUtils.setInUIThread(() -> Toast.makeText(this, fragmentList.size() + "", Toast.LENGTH_SHORT).show());
//            Log.d("mylog", "initClickListener: ");
        });

        showMoreSettingsView.setOnClickListener(e -> {
            View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow_view, null);
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);// 取得焦点
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.setOutsideTouchable(false);
            popupWindow.setTouchable(false);
            popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
            popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, navBefore.getMeasuredHeight());//弹出
            navBefore.setVisibility(View.GONE);
            ThreadUtils.setPostDelayed(() -> {
                navAfter.setVisibility(View.VISIBLE);
            }, 400);
        });
        goDownView.setOnClickListener(e->{
            popupWindow.dismiss();//消失
            navAfter.setVisibility(View.GONE);
            ThreadUtils.setPostDelayed(() -> {
                navBefore.setVisibility(View.VISIBLE);
            }, 300);
        });

        urlEditBox.setOnFocusChangeListener((v, hasFocus) -> {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(hasFocus) {
                ((EditText) v).setText(inputText);
                showGraySet(transaction);
                reloadOrGoImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_icon));
            } else {
                inputText = ((EditText) v).getText().toString();
                ((EditText) v).setText("");
                ((EditText) v).setHint(currentPageMessage.titles.get(currentFragment.getCurrentFragment()));
                removeGraySet(transaction);
                reloadOrGoImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_reload));
            }
        });

        reloadOrGoView.setOnClickListener(e -> {
            if (urlEditBox.isFocused()) {
                inputText = urlEditBox.getText().toString();
                Log.d("mylog", "setOnClickListener: " + inputText);
                ThreadUtils.setInThread(() -> {
//                    Log.d("mylog", "initClickListener: " + URLUtils.canConnectionByHttp(inputText));
                    if(URLUtils.canConnectionByHttp(inputText)) {
                        WebViewHelper.openNewURL(inputText, this, (MyWebView) currentFragment.getCurrentWebView());
                    } else {
                        WebViewHelper.searchWord(inputText, this, (MyWebView) currentFragment.getCurrentWebView());
                    }
                });
            }

            MyWebView view = (MyWebView)currentFragment.getCurrentWebView();
            if(view.isLoading()) {
                view.stopLoading();
                view.getProgressBar().setVisibility(View.GONE);
                reloadOrGoImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_reload));
            } else {
                view.reload();
                reloadOrGoImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
            }

        });

    }

    private void showGraySet(FragmentTransaction transaction) {
        transaction.add(currentFragment.getCurrentFragment().getId(), graySet).show(graySet).commit();
    }

    private void removeGraySet(FragmentTransaction transaction) {
        transaction.remove(graySet).commit();
    }





}
