package com.zn.expirytracker.ui;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.FragmentWelcomePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.zn.expirytracker.R;

import androidx.fragment.app.Fragment;

public class IntroActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return new IntroFinalFragment();
                    }
                })
                .page(new BasicPage(R.drawable.ic_check_accent_24dp,
                        getString(R.string.wel_page_save_title),
                        getString(R.string.wel_page_save_description)))


                .bottomLayout(WelcomeConfiguration.BottomLayout.STANDARD)
                .canSkip(false)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }
}
