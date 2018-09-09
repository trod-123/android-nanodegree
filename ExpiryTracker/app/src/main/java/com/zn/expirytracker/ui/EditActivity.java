package com.zn.expirytracker.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.zn.expirytracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    @BindView(R.id.layout_edit_root)
    View mRootLayout;
    @BindView(R.id.fab_edit_voice)
    FloatingActionButton mFabVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            int position = intent.getIntExtra(DetailActivity.ARG_ITEM_POSITION_INT, 0);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_edit_fragment, EditFragment.newInstance(position),
                            EditFragment.class.getSimpleName())
                    .commit();
        }

        // Perform action when keyboard shows and hides
        // https://stackoverflow.com/questions/4745988/how-do-i-detect-if-software-keyboard-is-visible-on-android-device
        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mRootLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = mRootLayout.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    mFabVoice.hide();
                }
                else {
                    // keyboard is closed
                    mFabVoice.show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Emulate the back button when pressing the up button, to prevent parent activity from
            // getting recreated
            // https://stackoverflow.com/questions/22947713/make-the-up-button-behave-like-the-back-button-on-android
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
