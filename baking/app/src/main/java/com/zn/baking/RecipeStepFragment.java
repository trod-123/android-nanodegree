package com.zn.baking;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.zn.baking.model.Step;
import com.zn.baking.util.OrientationHelper;
import com.zn.baking.util.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeStepFragment extends Fragment {

    public static final String BUNDLE_STEP_INTENT_EXTRA_KEY =
            "com.zn.baking.bundle_step_intent_extra_key";
    public static final String STEP_PARCELABLE_EXTRA_KEY =
            "com.zn.baking.step_parcelable_extra_key";
    public static final String STEP_POSITION_EXTRA_KEY =
            "com.zn.baking.step_position_extra_key";
    public static final String RECIPE_NAME_EXTRA_KEY =
            "com.zn.baking.recipe_name_extra_key";
    public static final String NUM_STEPS_EXTRA_KEY =
            "com.zn.baking.num_steps_extra_key";

    public static final long DEFAULT_STEP_VIDEO_POSITION = 0;

    StepActivity mHostActivity;
    ActionBar mActionBar;

    @BindView(R.id.player_recipe_step_container)
    FrameLayout mFl_player_container;
    @BindView(R.id.image_recipe_step)
    ImageView mIv_step;
    @BindView(R.id.image_step_loading_spinner)
    ProgressBar mPb_image;
    SimpleExoPlayer mExoPlayer;
    PlayerView mPlayerView;
    @BindView(R.id.text_step_broad_instruction)
    @Nullable
    TextView mTv_broad_instruction;
    @Nullable
    @BindView(R.id.text_step_detailed_instruction)
    TextView mTv_instruction;
    @Nullable
    @BindView(R.id.exo_fullscreen)
    ImageButton mBtn_exoFullscreen;
    @BindView(R.id.exo_play)
    ImageButton mBtn_exoPlay;
    @BindView(R.id.exo_pause)
    ImageButton mBtn_exoPause;

    private Step mStep;
    private View mDecorView;

    // for controlling orientation changes
    OrientationEventListener mOrientationEventListener;
    private int mCurrentOrientation;
    // used when clicking on exoplayer fullscreen button. if true, prevents system rotation
    private boolean mOrientationJitter = false;

    // keeps track of whether user presses play or pause button, to preserve state across config changes
    private boolean mVideoPlaying = true;

    private boolean mTabletLayout;
    private boolean mVideoLoaded = false;
    private Uri mVideoUri;
    private long mVideoPosition = DEFAULT_STEP_VIDEO_POSITION;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        ButterKnife.bind(this, view);
        Timber.tag(StepActivity.class.getSimpleName());

        // save a convenience reference to the hosting activity
        mHostActivity = (StepActivity) getActivity();

        // TODO: For now, full screen is disabled for tablet devices, regardless of orientation
        mTabletLayout = mHostActivity.isInTabletLayout();

        // Track the current orientation of the device. Be mindful of tablet devices where
        // orientation is different
        mCurrentOrientation = OrientationHelper.getCurrentOrientation(mHostActivity);

        // Up navigation handled in hosting Activity
        mActionBar = mHostActivity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // load up the saved video position for restoring state
        // TODO: None of this is even called since we are handing config changes ourselves. Delete
        if (savedInstanceState != null) {
            mVideoPosition = savedInstanceState
                    .getLong(STEP_VIDEO_POSITION_EXTRA_KEY, DEFAULT_STEP_VIDEO_POSITION);
            mVideoPlaying = savedInstanceState
                    .getBoolean(VIDEO_CURRENTLY_PLAYING_EXTRA_KEY, true);
            mOrientationJitter = savedInstanceState
                    .getBoolean(ORIENTATION_JITTER_EXTRA_KEY, false);
        }

        // TODO: Provide default constructor for step fragment to ensure all necessary arguments are
        // provided to set up fragment properly

        // populate the UI
        mStep = getArguments().getParcelable(STEP_PARCELABLE_EXTRA_KEY);
        int stepPosition = getArguments().getInt(STEP_POSITION_EXTRA_KEY);
        String recipeName = getArguments().getString(RECIPE_NAME_EXTRA_KEY);
        int numSteps = getArguments().getInt(NUM_STEPS_EXTRA_KEY);
        if (mStep != null && recipeName != null && !recipeName.isEmpty()) {
            if (stepPosition > 0) {
                mHostActivity.setTitle(
                        getString(R.string.title_step_detailed,
                                recipeName, stepPosition, numSteps - 1));
            } else {
                mHostActivity.setTitle(getString(R.string.title_step_intro, recipeName));
            }
        }
        if (mTv_broad_instruction != null)
            mTv_broad_instruction.setText(mStep.getShortDescription());
        if (mTv_instruction != null)
            mTv_instruction
                    .setText(Toolbox.generateReadableDetailedStepInstruction(mStep.getDescription()));

        mPlayerView = view.findViewById(R.id.player_recipe_step);

        String thumbnailUrl = mStep.getThumbnailURL();
        String videoUrl = mStep.getVideoURL();
        if ((!thumbnailUrl.isEmpty() && Toolbox.isVideoFile(thumbnailUrl)) ||
                (!videoUrl.isEmpty() && Toolbox.isVideoFile(videoUrl))) {
            mVideoLoaded = true;
            // set up the exoplayer if we have a video url. Prioritize the videoUrl
            // if both are not empty
            String url = !videoUrl.isEmpty() && !thumbnailUrl.isEmpty() ? videoUrl :
                    !videoUrl.isEmpty() ? videoUrl : thumbnailUrl;
            mVideoUri = Uri.parse(url);

            // set up fullscreen mode
            mDecorView = mHostActivity.getWindow().getDecorView();
            setFullscreen(mHostActivity.getResources().getConfiguration()); // if loaded in landscape, start in fullscreen
        } else if ((!thumbnailUrl.isEmpty() && Toolbox.isImageFile(thumbnailUrl)) ||
                (!videoUrl.isEmpty() && Toolbox.isImageFile(videoUrl))) {
            // if there is no video url, but we have an image url, then load the image without
            // prepping the ExoPlayer. Prioritize the thumbnailUrl if both are not empty
            mPlayerView.setVisibility(View.GONE);
            String url = !thumbnailUrl.isEmpty() && !videoUrl.isEmpty() ? thumbnailUrl :
                    !thumbnailUrl.isEmpty() ? thumbnailUrl : videoUrl;
            mPb_image.setVisibility(View.VISIBLE);
            RequestListener<Bitmap> requestListener = new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    Timber.e(e != null ? e.getMessage() : "Exception message returned null",
                            "There was an issue loading the detail step thumbnail: %s");
                    mPb_image.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    mPb_image.setVisibility(View.GONE);
                    return false;
                }
            };
            Toolbox.loadThumbnailFromUrl(mHostActivity, url, mIv_step, requestListener);
        } else {
            // No image url is provided, so just hide the player and image views
            mFl_player_container.setVisibility(View.GONE);
        }

        // handle orientation changes ourselves here
        // TODO: Video fullscreen is only available for phone devices - implement for tablet devices
        mOrientationEventListener =
                new OrientationEventListener(mHostActivity, SensorManager.SENSOR_DELAY_NORMAL) {
                    // i = [0,360) where 0 = default and 90 = left side on top
                    @Override
                    public void onOrientationChanged(int i) {
                        // only purpose for this is to note which orientation we're coming from
                        int displayOrientation = OrientationHelper.getCurrentOrientation(mHostActivity);
                        // manually update screen rotations, similar to how OS does it
                        int targetOrientation =
                                OrientationHelper.getTargetOrientation(displayOrientation, i); // don't do anything if this stays -1

                        // this is where "overriding" OS rotations happens.
                        // only rotate if there is no jitter
                        if (!mOrientationJitter && targetOrientation !=
                                OrientationHelper.ROTATION_NO_CHANGE) {
                            if (!mTabletLayout && mVideoLoaded) {
                                setFullscreen(targetOrientation != Surface.ROTATION_0,
                                        targetOrientation);
                            } else if (!mVideoLoaded) {
                                setLandscape(targetOrientation != Surface.ROTATION_0,
                                        targetOrientation);
                            }
                        } else if (mOrientationJitter) {
                            // otherwise, without rotating, get the actual device orientation
                            int deviceOrientation =
                                    OrientationHelper.getDeviceOrientation(i);

                            // when device orientation matches or is opposite of the display
                            // orientation, turn off the jitter so user can freely rotate again
                            if (deviceOrientation == displayOrientation ||
                                    deviceOrientation == OrientationHelper.getOppositeOrientation(displayOrientation)) {
                                mOrientationJitter = false;
                            }
                        }
                    }
                };

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > 23) {
            setupOrientations();
            if (mVideoLoaded) {
                // Only load the player if we have a video link available
                initializePlayer(mVideoUri);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT <= 23) {
            setupOrientations();
            if (mVideoLoaded) {
                // Only load the player if we have a video link available
                initializePlayer(mVideoUri);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= 23) {
            // Don't wait until onDestroy() to release player resources as navigating away from app
            // does not call onDestroy(), so it is likely player may still be running and using
            // resources even while user is away from app
            // Although onStop() is called after onPause(), before API 24 there is no guarantee
            // onStop() is called, so release the player here
            releasePlayer();
            restoreOrientations();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT > 23) {
            // Safe to release player in onStop() for API 24 and above
            releasePlayer();
            restoreOrientations();
        }
    }

    /**
     * Sets up the exo player with the Step video
     *
     * @param videoUri
     */
    private void initializePlayer(Uri videoUri) {
        if (mExoPlayer == null) {
            // generate exoPlayer
            RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    renderersFactory, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            // resize video player container appropriately once video is loaded. setting video
            // height to "WRAP_CONTENT" in the XML requires calculation before determining dimens
            // The time it takes is illustrated by the video player loading in fullscreen to start
            // with if "wrap_content" is provided in the resource file.
            // so best to do it dynamically through here, and put an initial fixed height in the xml
            mExoPlayer.addVideoListener(new VideoListener() {
                @Override
                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

                }

                @Override
                public void onRenderedFirstFrame() {
                    if (mTabletLayout || !isLandscape()) {
                        mFl_player_container.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                    } else if (isLandscape()) {
                        mFl_player_container.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                }
            });

            // handle full screen button clicks
            if (mBtn_exoFullscreen != null)
                mBtn_exoFullscreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        forceChangeFullscreen();
                    }
                });

            // keep track of when user presses play or pause so when user rotates device, the
            // media player either continues playing, or remains paused
            mBtn_exoPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExoPlayer.setPlayWhenReady(false);
                    mVideoPlaying = false;
                }
            });
            mBtn_exoPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExoPlayer.setPlayWhenReady(true);
                    mVideoPlaying = true;
                }
            });

            // set step video url as media source
            String userAgent = Util.getUserAgent(getContext(), "test");
            MediaSource mediaSource = new ExtractorMediaSource.Factory(
                    new DefaultDataSourceFactory(getContext(), userAgent))
                    .createMediaSource(videoUri);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.seekTo(mVideoPosition);
            // only loop by default if step is not intro
            if (mStep.getId() != 0)
                mExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            mExoPlayer.setPlayWhenReady(mVideoPlaying);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            // Since we're handling config changes ourselves, save the current video position here
            // as savedInstanceState is not used, before releasing player
            mVideoPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void setupOrientations() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.enable();
        }
        // If coming out of the app from fullscreen mode, restore system bars here
        if (!mTabletLayout && !isLandscape() && !mActionBar.isShowing()) {
            mActionBar.show();
            mBtn_exoFullscreen.setImageResource(R.drawable.ic_fullscreen_white_24dp);
            // TODO <small issue>: Upon showing the action bar, a top portion of the video gets cut
            // off. fix it here. Might need to set the style of this activity to NoActionBar and
            // then handle all action bar stuff programmatically?
            if (mDecorView != null) {
                //mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }

    private void restoreOrientations() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
        // allow system to handle rotations again after leaving the fragment
        mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**
     * Helper method for forcing exolayer in and out of fullscreen mode, while enabling jitter
     */
    public void forceChangeFullscreen() {
        setFullscreen(!isLandscape(), mCurrentOrientation); // negate boolean to change fullscreen mode
        mOrientationJitter = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mExoPlayer != null) {

        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Helper method to set fullscreen mode for the exo player if current screen orientation is
     * not Configuration.ORIENTATION_PORTRAIT. Otherwise, don't do anything
     *
     * @param config
     */
    private void setFullscreen(Configuration config) {
        if (config.orientation != Configuration.ORIENTATION_PORTRAIT)
            setFullscreen(true, mCurrentOrientation);
    }

    /**
     * Helper method to set fullscreen mode for the exo player
     * TODO: Fullscreen is only available for phone devices - implement for tablet devices
     *
     * @param setFullscreen
     * @param landscapeOrientation
     */
    private void setFullscreen(boolean setFullscreen, int landscapeOrientation) {
        if (!mTabletLayout) {
            if (setFullscreen) enterFullscreen(landscapeOrientation);
            else exitFullscreen();
            mCurrentOrientation = OrientationHelper.getCurrentOrientation(mHostActivity);
        }
    }

    /**
     * Fill the screen with the exo player, while also hiding the app bar and status bar. Always
     * put in Landscape mode
     */
    private void enterFullscreen(int landscapeOrientation) {
        mBtn_exoFullscreen.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp);
        mFl_player_container.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        enterLandscape(landscapeOrientation);
        hideSystemBars();
    }

    /**
     * Set exo player to default wrap content height, while also bringing back the app bar and
     * status bar. Always exit Landscape mode
     */
    private void exitFullscreen() {
        mBtn_exoFullscreen.setImageResource(R.drawable.ic_fullscreen_white_24dp);
        mFl_player_container.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        exitLandscape();
        showSystemBars();
    }

    public boolean isLandscape() {
        return mHostActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void setLandscape(boolean setLandscape, int landscapeOrientation) {
        if (setLandscape) enterLandscape(landscapeOrientation);
        else exitLandscape();
        mCurrentOrientation = OrientationHelper.getCurrentOrientation(mHostActivity);
    }

    private void enterLandscape(int landscapeOrientation) {
        if (landscapeOrientation == Surface.ROTATION_270)
            // "Reverse landscape" is when device's left side is on top
            mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        else
            mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void exitLandscape() {
        mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public boolean isVideoLoaded() {
        return mVideoLoaded;
    }

    private void showSystemBars() {
        if (!mActionBar.isShowing())
            mActionBar.show();
        if (mDecorView != null)
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    private void hideSystemBars() {
        if (mActionBar.isShowing())
            mActionBar.hide();
        if (mDecorView != null)
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
