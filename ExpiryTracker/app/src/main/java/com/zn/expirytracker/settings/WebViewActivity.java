package com.zn.expirytracker.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.zn.expirytracker.R;
import com.zn.expirytracker.utils.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A convenience class for displaying a {@link WebView}. Launch Intents must contain the title of
 * the activity passed via {@link WebViewActivity#KEY_WEBVIEW_TITLE_STRING}, and the url passed via
 * {@link WebViewActivity#KEY_WEBVIEW_URL}. This Activity should NOT be started without setting the
 * proper Intent extras.
 * <p>
 * Note: Activity handles orientation changes, etc. to preserve WebView layout - as declared
 * in Manifest via {@code android:configChanges}. This is totally fine for our purposes since we do
 * not have any alternate layouts declared for this Activity
 */
public class WebViewActivity extends AppCompatActivity {

    /**
     * Used for setting the App Bar Title
     */
    public static final String KEY_WEBVIEW_TITLE_STRING =
            Toolbox.createStaticKeyString(WebViewActivity.class, "webview_title");

    /**
     * Used for setting the url for the WebView
     */
    public static final String KEY_WEBVIEW_URL =
            Toolbox.createStaticKeyString(WebViewActivity.class, "webview_url");

    @BindView(R.id.wbv)
    WebView mWebView;
    @BindView(R.id.pb_webview)
    ProgressBar mPb;

    String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(WebViewActivity.class.getSimpleName());
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        // Set the up button. No parent activity needed if we're overriding the up button anyway
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(KEY_WEBVIEW_TITLE_STRING);
            setTitle(title != null ? title : "WebView");
            mUrl = intent.getStringExtra(KEY_WEBVIEW_URL);
        }

        // Set up the client to show a loading spinner
        WebViewClient wvClient = new WebViewClient() {
            boolean loadingFinished = true;
            boolean redirect = false;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String host = Uri.parse(url).getHost();
                if (host != null && host.equals(mUrl)) {
                    if (!loadingFinished) redirect = true;
                    loadingFinished = false;
                    view.loadUrl(url);
                    return true;
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingFinished = false;
                Toolbox.showView(view, false, false);
                Toolbox.showView(mPb, true, false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) loadingFinished = true;
                if (loadingFinished && !redirect) {
                    Toolbox.showView(view, true, false);
                    Toolbox.showView(mPb, false, false);
                } else {
                    redirect = false;
                }
            }
        };
        mWebView.setWebViewClient(wvClient);
        // Get the download url from the Firebase uri
        FirebaseStorage.getInstance().getReferenceFromUrl(mUrl).getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            mWebView.loadUrl(downloadUri.toString());
                        } else {
                            Timber.e(task.getException(), "WebViewActivity/Error getting HTTP link");
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
