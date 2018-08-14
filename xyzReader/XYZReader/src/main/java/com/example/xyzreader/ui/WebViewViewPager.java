package com.example.xyzreader.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

/**
 * View pager that allows swiping from webviews. When implementing, make sure to
 * <p>
 * 1) Nest webview within a scrollview
 * <p>
 * 2) Disable scrolling within the webview
 * <p>
 * From: https://stackoverflow.com/questions/42526469/viewpager-with-webview-allowing-swiping-and-disallowing-scrolling-of-webview
 */
public class WebViewViewPager extends ViewPager {
    public WebViewViewPager(@NonNull Context context) {
        super(context);
    }

    public WebViewViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return !(v instanceof WebView) && super.canScroll(v, checkV, dx, x, y);
    }
}
