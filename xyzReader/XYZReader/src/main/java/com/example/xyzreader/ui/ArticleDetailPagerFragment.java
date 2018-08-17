package com.example.xyzreader.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
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
 * <p>
 * TODO: Make this fragment extend ArticleDetailFragment, for the interface methods?
 */
public class ArticleDetailPagerFragment extends Fragment {

    private static final String KEY_PAGER_LOADED = "com.example.xyzreader.key.pager_loaded";

    @BindView(R.id.pager)
    WebViewViewPager mPager;
    private DetailPagerAdapter mPagerAdapter;

    private Activity mHostActivity;

    // This is for ensuring that only the launched article has the temp container visible
    // for transition animations. Temp container be hidden for any other fragment created at this
    // time
    private int mLaunchedPosition;
    private boolean mLoaded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(ArticleDetailPagerFragment.class.getSimpleName());

        mHostActivity = getActivity();
        mLaunchedPosition = MainActivity.sCurrentPosition;
        mLoaded = savedInstanceState != null && savedInstanceState.getBoolean(KEY_PAGER_LOADED, false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail_pager, container, false);
        ButterKnife.bind(this, rootView);

        setupViewPager();

        prepareEnterReturnTransitions();

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
        //getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_PAGER_LOADED, mLoaded);
    }

    private void setupViewPager() {
        mPager.setAdapter(mPagerAdapter = new DetailPagerAdapter(this,
                MainActivity.sCursor, mLaunchedPosition));

        // If I leave this here and it works when I click on any item,
        // that means the fragments are being recycled as proper
        mPager.setCurrentItem(MainActivity.sCurrentPosition, false);

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (MainActivity.sCursor != null && !MainActivity.sCursor.isClosed()) {
                    // While updating position doesn't require cursor when paginating, the cursor
                    // is necessary for updating the id
                    MainActivity.sCursor.moveToPosition(MainActivity.sCurrentPosition = position);
                    MainActivity.sCurrentId = MainActivity.sCursor.getLong(ArticleLoader.Query._ID);
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

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // Find the view for the current fragment
                Fragment currentFragment = mPagerAdapter.getCurrentFragment();
                View view = currentFragment.getView();
                if (view == null) return;

                // Map the first shared element name to the child image view
                if (!mLoaded) {
                    sharedElements.put(names.get(0), view.findViewById(R.id.detail_temp_photo));
                } else {
                    // Map to the app bar detail image view once shared element transition is done
                    sharedElements.put(names.get(0), view.findViewById(R.id.detail_photo));
                }
                mLoaded = true;
            }
        };
        setEnterSharedElementCallback(callback);
    }

//    @NonNull
//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return ArticleLoader.newAllArticlesInstance(mHostActivity);
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
//        MainActivity.sCursor = cursor;
//        // TODO: Calling this seems to reset the pager adapter into thinking it should load from
//        // the 0 position, even though we may have setCurrentItem() earlier
//        // Potentially this may also be clearing out the child fragment manager of any fragments
//        // it contains... but this happens even without calling the below...
//        mPagerAdapter.notifyDataSetChanged();
//
//        // Select the start ID
////        if (MainActivity.sCurrentId > 0) {
////            MainActivity.sCursor.moveToFirst();
////            // TODO: this code can be deleted, it seems OK to just set current item to sCurrentPosition
////            while (!MainActivity.sCursor.isAfterLast()) {
////                if (MainActivity.sCursor.getLong(ArticleLoader.Query._ID) == MainActivity.sCurrentId) {
////                    mPager.setCurrentItem(MainActivity.sCursor.getPosition(), false);
////                    return;
////                }
////                MainActivity.sCursor.moveToNext();
////            }
////        }
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader<Cursor> cursorLoader) {
//        MainActivity.sCursor = null;
//        mPagerAdapter.notifyDataSetChanged();
//    }
}
