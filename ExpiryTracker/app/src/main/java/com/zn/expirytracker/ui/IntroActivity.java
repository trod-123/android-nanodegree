package com.zn.expirytracker.ui;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.ParallaxPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.zn.expirytracker.R;

public class IntroActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)

                .page(new BasicPage(R.drawable.web_hi_res_512,
                        getString(R.string.wel_page_first_title),
                        getString(R.string.wel_page_first_description))
                        .background(R.color.wel_page_first)
                )
                .page(new ParallaxPage(R.layout.wel_page_capture,
                        getString(R.string.wel_page_capture_title),
                        getString(R.string.wel_page_capture_description))
                        .background(R.color.wel_page_capture)
                )
                .page(new ParallaxPage(R.layout.wel_page_dates,
                        getString(R.string.wel_page_dates_title),
                        getString(R.string.wel_page_dates_description))
                        .background(R.color.wel_page_dates)
                )
                .page(new ParallaxPage(R.layout.wel_page_view,
                        getString(R.string.wel_page_view_title),
                        getString(R.string.wel_page_view_description))
                        .background(R.color.wel_page_view)
                )
                .page(new ParallaxPage(R.layout.wel_page_notifications,
                        getString(R.string.wel_page_notifications_title),
                        getString(R.string.wel_page_notifications_description))
                        .background(R.color.wel_page_notifications)
                )
                .page(new ParallaxPage(R.layout.wel_page_save,
                        getString(R.string.wel_page_save_title),
                        getString(R.string.wel_page_save_description))
                        .background(R.color.wel_page_save)
                )
                .page(new BasicPage(R.drawable.ic_check_white_24dp,
                        getString(R.string.wel_page_final_title),
                        getString(R.string.wel_page_final_description))
                        .background(R.color.wel_page_final)
                )
                .bottomLayout(WelcomeConfiguration.BottomLayout.STANDARD_DONE_IMAGE)

                .showPrevButton(true)
                .swipeToDismiss(false)
                .canSkip(false)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }
}
