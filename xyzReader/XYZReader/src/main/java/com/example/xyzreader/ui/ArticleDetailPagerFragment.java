package com.example.xyzreader.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment representing a single Article detail screen, letting you swipe between articles.
 * This class is instantiated from intent actions, and is not created directly from within list
 */
public class ArticleDetailPagerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.pager)
    ViewPager mPager;
    private DetailPagerAdapter mPagerAdapter;

    Activity mHostActivity;

    private Cursor mCursor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail_pager, container, false);
        Timber.tag(ArticleDetailPagerFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);
        mHostActivity = getActivity();

        setupViewPager();

        prepareSharedElementTransition();

        if (savedInstanceState == null) {
            // Even the shared element is within the fragment, this is still needed to be called
            // in the activity, NOT in the fragment since this activity is created first.
            // Also, calling it here when savedInstanceState is null avoids this from being
            // called on orientation change
            postponeEnterTransition();
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    private void setupViewPager() {
        mPagerAdapter = new DetailPagerAdapter(this);
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    MainActivity.sCurrentId = mCursor.getLong(ArticleLoader.Query._ID);
                    MainActivity.sCurrentPosition = position;
                } else {
                    Timber.e("Cursor is null when paging");
                }
            }
        });
    }

    private void prepareSharedElementTransition() {
        Transition transition = TransitionInflater.from(mHostActivity)
                .inflateTransition(R.transition.image_shared_element_transition);
        setSharedElementEnterTransition(transition);

        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Find the view for the current fragment
                        Fragment currentFragment = (Fragment) mPagerAdapter
                                .instantiateItem(mPager, MainActivity.sCurrentPosition);
                        View view = currentFragment.getView();
                        if (view == null) return;

                        // Map the first shared element name to the child image view
                        sharedElements.put(names.get(0), view.findViewById(R.id.iv_photo_details));
                    }
                });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(mHostActivity);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (MainActivity.sCurrentId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == MainActivity.sCurrentId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            // mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private class DetailPagerAdapter extends FragmentStatePagerAdapter {
        DetailPagerAdapter(Fragment fragment) {
            // Initialize with the child fragment manager for shared elements. Allows detail
            // fragment to recognize the pager fragment as its "parent" for starting
            // postponed enter transition
            super(fragment.getChildFragmentManager());
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
            if (fragment != null) {
//                mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
//                updateUpButtonPosition();
            }
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
        }

        @Override
        public int getCount() {
            // This seems to rely on the cursor for returning number of pages, rather than looking
            // at number of pages
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
