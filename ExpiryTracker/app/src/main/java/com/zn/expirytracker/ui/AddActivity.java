package com.zn.expirytracker.ui;

import android.os.Bundle;
import android.view.MenuItem;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.InputType;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Timber.tag(AddActivity.class.getSimpleName());
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            // To retain current fragment it is necessary to return here to prevent additional
            // detail fragments when changing orientation
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_edit_fragment, EditFragment.newInstance(
                        EditFragment.POSITION_ADD_MODE, "", InputType.TEXT_ONLY),
                        EditFragment.class.getSimpleName())
                .commit();
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

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 0) {
            super.onBackPressed();
        } else {
            for (Fragment fragment : fragments) {
                String tag = fragment.getTag();
                if (tag != null && tag.equals(EditFragment.class.getSimpleName()) &&
                        ((EditFragment) fragment).haveFieldsChanged()) {
                    return;
                }
            }
        }
        super.onBackPressed();
    }
}
