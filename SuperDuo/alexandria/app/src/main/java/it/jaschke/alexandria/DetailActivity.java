package it.jaschke.alexandria;

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

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            // Start the fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container_fragment, fragment)
                    .commit();
        }
    }
}
