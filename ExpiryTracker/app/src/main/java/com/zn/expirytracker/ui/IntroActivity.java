package com.zn.expirytracker.ui;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.FragmentWelcomePage;
import com.stephentuso.welcome.ParallaxPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.zn.expirytracker.R;

import androidx.fragment.app.Fragment;

public class IntroActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .page(new BasicPage(R.drawable.saved_full_logo_colored,
                        getString(R.string.wel_page_first_title),
                        getString(R.string.wel_page_first_description))
                )
                .page(new ParallaxPage(R.layout.wel_page_capture,
                        getString(R.string.wel_page_capture_title),
                        getString(R.string.wel_page_capture_description))
                )
                .page(new ParallaxPage(R.layout.wel_page_dates,
                        getString(R.string.wel_page_dates_title),
                        getString(R.string.wel_page_dates_description))
                )
                .page(new ParallaxPage(R.layout.wel_page_view,
                        getString(R.string.wel_page_view_title),
                        getString(R.string.wel_page_view_description))
                )
                .page(new ParallaxPage(R.layout.wel_page_notifications,
                        getString(R.string.wel_page_notifications_title),
                        getString(R.string.wel_page_notifications_description))
                )
                .page(new ParallaxPage(R.layout.wel_page_save,
                        getString(R.string.wel_page_save_title),
                        getString(R.string.wel_page_save_description))
                )
                .page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return new IntroFinalFragment();
                    }
                })

                .bottomLayout(WelcomeConfiguration.BottomLayout.STANDARD_DONE_IMAGE)
                .showPrevButton(true)
                .swipeToDismiss(true)
                .canSkip(false)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }
}
