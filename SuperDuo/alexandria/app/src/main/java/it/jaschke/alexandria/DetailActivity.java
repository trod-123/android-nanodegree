package it.jaschke.alexandria;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by TROD on 20160106.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            // Put the book data in the fragment
            Bundle arguments = new Bundle();
            if (getIntent().hasExtra(DetailFragment.BOOK_ID))
                arguments.putString(DetailFragment.BOOK_ID,
                        getIntent().getStringExtra(DetailFragment.BOOK_ID));
            else if (getIntent().hasExtra(DetailFragment.VOLUME_OBJECT))
                arguments.putParcelable(DetailFragment.VOLUME_OBJECT,
                        getIntent().getParcelableExtra(DetailFragment.VOLUME_OBJECT));
            if (getIntent().hasExtra(DetailFragment.UPDATE_BOOK))
                arguments.putBoolean(DetailFragment.UPDATE_BOOK,
                        getIntent().getBooleanExtra(DetailFragment.UPDATE_BOOK, false));

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            // Start the fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container_fragment, fragment)
                    .commit();
        }
    }

    /**
     * System calls this method to get parent activity intent for up button behavior. By adding
     *  the Intent.FLAG_ACTIVITY_CLEAR_TOP flag, allows system to check if main activity is already
     *  running in task, and to use that main activity instead of creating a new instance.
     * The annotation TargetApi is displayed because this method did not exist before Jelly Bean.
     * @return Intent directing to the main activity
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
