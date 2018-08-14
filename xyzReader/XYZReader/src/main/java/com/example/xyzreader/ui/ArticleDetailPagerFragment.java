package com.example.xyzreader.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.xyzreader.util.BasicTouchEnablerTransitionListener;

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
    WebViewViewPager mPager;
    private DetailPagerAdapter mPagerAdapter;

    Activity mHostActivity;

    private Cursor mCursor;

    // This is for ensuring that only the launched article has the temp container visible
    // for transition animations. Temp container be hidden for any other fragment created at this
    // time
    private int mLaunchedPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(ArticleDetailPagerFragment.class.getSimpleName());

        mHostActivity = getActivity();
        mLaunchedPosition = MainActivity.sCurrentPosition;

        prepareEnterReturnTransitions();

        if (savedInstanceState == null) {
            // Even the shared element is within the fragment, this is still needed to be called
            // in the activity, NOT in the fragment since this activity is created first.
            // Also, calling it here when savedInstanceState is null avoids this from being
            // called on orientation change
            postponeEnterTransition();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail_pager, container, false);
        ButterKnife.bind(this, rootView);
        setupViewPager();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    private void setupViewPager() {
        mPager.setAdapter(mPagerAdapter = new DetailPagerAdapter(this));

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mCursor != null && !mCursor.isClosed()) {
                    mCursor.moveToPosition(MainActivity.sCurrentPosition = position);
                    MainActivity.sCurrentId = mCursor.getLong(ArticleLoader.Query._ID);
                } else {
                    Timber.e("Cursor is null when paging");
                }
            }
        });
    }

    private void prepareEnterReturnTransitions() {
        TransitionInflater inflater = TransitionInflater.from(mHostActivity);

        Transition enterTransition =
                inflater.inflateTransition(R.transition.detail_enter_transition);
        enterTransition.addListener(new BasicTouchEnablerTransitionListener(mHostActivity.getWindow()) {
            @Override
            public void onTransitionStart(Transition transition) {
                super.onTransitionStart(transition);
                // NOTE: By the time this method is called, all the views participating in the
                // transition MUST be visible, or else they will not be animated
                ArticleDetailFragment fragment = mPagerAdapter.getCurrentFragment();
                if (fragment != null) {
                    fragment.onEnterTransitionStarted();
                }
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                ArticleDetailFragment fragment = mPagerAdapter.getCurrentFragment();
                if (fragment != null) {
                    fragment.onEnterTransitionFinished();
                }
            }
        });
        setEnterTransition(enterTransition);

        Transition sharedElementEnterTransition = inflater
                .inflateTransition(R.transition.image_shared_element_enter_transition);
        // https://stackoverflow.com/questions/33641752/how-to-know-when-shared-element-transition-ends/33859633
        // https://stackoverflow.com/questions/32256168/android-shared-view-transition-combined-with-fade-transition/32356093#32356093
        sharedElementEnterTransition.addListener(
                new BasicTouchEnablerTransitionListener(mHostActivity.getWindow()) {
                    @Override
                    public void onTransitionStart(Transition transition) {
                        super.onTransitionStart(transition);
                        ArticleDetailFragment fragment = mPagerAdapter.getCurrentFragment();
                        if (fragment != null) {
                            fragment.onSharedElementEnterTransitionStarted();
                        }
                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        ArticleDetailFragment fragment = mPagerAdapter.getCurrentFragment();
                        if (fragment != null) {
                            fragment.onSharedElementEnterTransitionFinished();
                        }
                    }
                });
        setSharedElementEnterTransition(sharedElementEnterTransition);

        // When returning to list, only block touch response from the re-enter transition in the
        // list, not here
        Transition returnTransition =
                inflater.inflateTransition(R.transition.detail_return_transition);
        returnTransition.addListener(new BasicTouchEnablerTransitionListener(mHostActivity.getWindow()) {
            @Override
            public void onTransitionStart(Transition transition) {
                ArticleDetailFragment fragment = mPagerAdapter.getCurrentFragment();
                if (fragment != null) {
                    fragment.onReturnTransitionStarted();
                }
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
        setReturnTransition(returnTransition);

        Transition sharedElementReturnTransition = inflater
                .inflateTransition(R.transition.image_shared_element_return_transition);
        setSharedElementReturnTransition(sharedElementReturnTransition);

        SharedElementCallback callback = new SharedElementCallback() {
            boolean loaded = false;

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // Find the view for the current fragment
                Fragment currentFragment = (Fragment) mPagerAdapter
                        .instantiateItem(mPager, MainActivity.sCurrentPosition);
                View view = currentFragment.getView();
                if (view == null) return;

                // Map the first shared element name to the child image view
                if (!loaded) {
                    sharedElements.put(names.get(0), view.findViewById(R.id.detail_temp_photo));
                } else {
                    // Map to the app bar detail image view once shared element transition is done
                    sharedElements.put(names.get(0), view.findViewById(R.id.detail_photo));
                }
                loaded = true;
            }
        };
        setEnterSharedElementCallback(callback);
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
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private class DetailPagerAdapter extends FragmentStatePagerAdapter {
        ArticleDetailFragment mCurrentFragment;

        DetailPagerAdapter(Fragment fragment) {
            // Initialize with the child fragment manager for shared elements. Allows detail
            // fragment to recognize the pager fragment as its "parent" for starting
            // postponed enter transition
            super(fragment.getChildFragmentManager());
        }

        public ArticleDetailFragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentFragment = (ArticleDetailFragment) object;
        }

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
        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            //super.restoreState(state, loader);
        }
    }
}
