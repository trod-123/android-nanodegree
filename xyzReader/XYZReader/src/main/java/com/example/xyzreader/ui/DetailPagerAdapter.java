package com.example.xyzreader.ui;


import android.database.Cursor;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.xyzreader.data.ArticleLoader;

public class DetailPagerAdapter extends FragmentStatePagerAdapter {
    private ArticleDetailFragment mCurrentFragment;
    private Cursor mCursor;
    private int mLaunchedPosition;

    DetailPagerAdapter(Fragment fragment, Cursor cursor, int launchedPosition) {
        // Initialize with the child fragment manager for shared elements. Allows detail
        // fragment to recognize the pager fragment as its "parent" for starting
        // postponed enter transition
        super(fragment.getChildFragmentManager());
        mCursor = cursor;
        mLaunchedPosition = launchedPosition;
    }

    public ArticleDetailFragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentFragment = (ArticleDetailFragment) object;
    }

    // This method is only called when the fragment at the given position does not already exist
    // It is called after instantiateItem(), when instantiateItem() notices the fragment
    // at the given position does not exist. getItem() creates that fragment
    @Override
    public Fragment getItem(int position) {
        mCursor.moveToPosition(position);
        // NOTE: This is where paged items are created
        return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID),
                position == mLaunchedPosition);
    }

    @Override
    public int getCount() {
        // This seems to rely on the cursor for returning number of pages, rather than looking
        // at number of pages
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    // TODO: BUG, more info here: https://stackoverflow.com/questions/18642890/fragmentstatepageradapter-with-childfragmentmanager-fragmentmanagerimpl-getfra
    // Loading details fragment and then quickly pressing back before the detail fragment is
    // rendered causes issues when trying to load the fragment again. Overriding restoreState()
    // without calling super seems to help avoid NPEs when loading the detail fragment, but
    // another issue arises when pressing the back button to return to the list fragment:
    // the user is greeted with a blank white screen. no list fragment. and pressing back again
    // exits the app
    // HOWEVER, implementing this will cause adapter to lose all its fragments upon rotation,
    // which can screw up shared elements transitions. Best not to remove this
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }
}