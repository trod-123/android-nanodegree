package com.zn.baking;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.net.Uri;
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
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
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
import com.zn.baking.util.Colors;
import com.zn.baking.util.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeStepFragment extends Fragment {

    public static final String BUNDLE_STEP_INTENT_EXTRA_KEY =
            "com.zn.baking.bundle_step_intent_extra_key";
    public static final String STEP_SERIALIZABLE_EXTRA_KEY =
            "com.zn.baking.step_serializable_extra_key";
    public static final String STEP_POSITION_EXTRA_KEY =
            "com.zn.baking.step_position_extra_key";
    public static final String RECIPE_NAME_EXTRA_KEY =
            "com.zn.baking.recipe_name_extra_key";
    public static final String NUM_STEPS_EXTRA_KEY =
            "com.zn.baking.num_steps_extra_key";

    public static final String STEP_VIDEO_POSITION_EXTRA_KEY =
            "com.zn.baking.step_video_position_extra_key";
    public static final long DEFAULT_STEP_VIDEO_POSITION = 0;

    public static final String ORIENTATION_JITTER_EXTRA_KEY =
            "com.zn.baking.orientation_jitter_extra_key";

    public static final String VIDEO_CURRENTLY_PLAYING_EXTRA_KEY =
            "com.zn.baking.video_currently_playing_extra_key";

    StepActivity mHostActivity;
    ActionBar mActionBar;

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
    private boolean mOrientationJitter = false; // if true, prevents system rotation

    // keeps track of whether user presses play or pause button, to preserve state across config changes
    private boolean mVideoPlaying = true;

    private boolean mTabletLayout;

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
        mCurrentOrientation = getCurrentOrientation(mHostActivity);

        // Up navigation handled in hosting Activity
        mActionBar = mHostActivity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // load up the saved video position for restoring state
        long savedVideoPosition = DEFAULT_STEP_VIDEO_POSITION;
        if (savedInstanceState != null) {
            savedVideoPosition = savedInstanceState
                    .getLong(STEP_VIDEO_POSITION_EXTRA_KEY, DEFAULT_STEP_VIDEO_POSITION);
            mVideoPlaying = savedInstanceState
                    .getBoolean(VIDEO_CURRENTLY_PLAYING_EXTRA_KEY, true);
            mOrientationJitter = savedInstanceState
                    .getBoolean(ORIENTATION_JITTER_EXTRA_KEY, false);
        }

        // populate the UI
        mStep = (Step) getArguments().getSerializable(STEP_SERIALIZABLE_EXTRA_KEY);
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
            mActionBar.setBackgroundDrawable(new ColorDrawable(getArguments()
                    .getInt(DetailRecipeFragment.RECIPE_DETAIL_APP_BAR_COLOR_EXTRA_KEY,
                            Colors.DEFAULT_APP_BAR_COLOR)));
        }
        if (mTv_broad_instruction != null)
            mTv_broad_instruction.setText(mStep.getShortDescription());
        if (mTv_instruction != null)
            mTv_instruction
                    .setText(Toolbox.generateReadableDetailedStepInstruction(mStep.getDescription()));

        // set up the exoplayer
        mPlayerView = view.findViewById(R.id.player_recipe_step);
        String videoUrl = mStep.getVideoURL();
        if (!videoUrl.isEmpty()) initializePlayer(Uri.parse(videoUrl), savedVideoPosition);
        else mPlayerView.setVisibility(View.GONE);

        // set up fullscreen mode
        mDecorView = mHostActivity.getWindow().getDecorView();
        setFullscreen(mHostActivity.getResources().getConfiguration()); // if loaded in landscape, start in fullscreen

        // listen to orientation changes, and update display accordingly.
        // TODO: Be mindful of tablet devices where orientation is different
        mOrientationEventListener =
                new OrientationEventListener(mHostActivity, SensorManager.SENSOR_DELAY_NORMAL) {
                    // i = [0,360) where 0 = default and 90 = left side on top
                    @Override
                    public void onOrientationChanged(int i) {
                        if (!mTabletLayout) {
                            // only purpose for this is to note which orientation we're coming from
                            int displayOrientation = getCurrentOrientation(mHostActivity);
                            int targetOrientation = -1; // don't do anything if this stays -1

                            // manually update screen rotations, similar to how OS does it
                            if (displayOrientation == Surface.ROTATION_0) {
                                if (i >= 67 && i <= 133) {
                                    targetOrientation = Surface.ROTATION_270;
                                } else if (i >= 227 && i <= 293) {
                                    targetOrientation = Surface.ROTATION_90;
                                }
                            } else if (displayOrientation == Surface.ROTATION_270) {
                                if (i <= 23) {
                                    targetOrientation = Surface.ROTATION_0;
                                } else if (i >= 227 && i <= 293) {
                                    targetOrientation = Surface.ROTATION_90;
                                }
                            } else if (displayOrientation == Surface.ROTATION_90) {
                                if (i >= 337) {
                                    targetOrientation = Surface.ROTATION_0;
                                } else if (i >= 67 && i <= 133) {
                                    targetOrientation = Surface.ROTATION_270;
                                }
                            }

                            // this is where "overriding" OS rotations happens.
                            // only rotate if there is no jitter
                            if (!mOrientationJitter && targetOrientation != -1) {
                                setFullscreen(targetOrientation != Surface.ROTATION_0,
                                        targetOrientation);

                            } else if (mOrientationJitter) {
                                // otherwise, without rotating, get the actual device orientation
                                int deviceOrientation;

                                if (i >= 315 || i <= 45) {
                                    deviceOrientation = Surface.ROTATION_0;
                                } else if (i <= 135) {
                                    deviceOrientation = Surface.ROTATION_270;
                                } else if (i <= 225) {
                                    deviceOrientation = Surface.ROTATION_180;
                                } else {
                                    deviceOrientation = Surface.ROTATION_90;
                                }

                                // when device orientation matches or is opposite of the display
                                // orientation, turn off the jitter so user can freely rotate again
                                if (deviceOrientation == displayOrientation ||
                                        deviceOrientation == oppositeOrientation(displayOrientation)) {
                                    mOrientationJitter = false;
                                }
                            }
                        }
                    }
                };
        mOrientationEventListener.enable();

        return view;
    }

    /**
     * Helper method that returns the opposite orientation provided. Return -1 if invalid.
     *
     * @param orientation
     * @return
     */
    private int oppositeOrientation(int orientation) {
        switch (orientation) {
            case Surface.ROTATION_0:
                return Surface.ROTATION_180;
            case Surface.ROTATION_90:
                return Surface.ROTATION_270;
            case Surface.ROTATION_180:
                return Surface.ROTATION_0;
            case Surface.ROTATION_270:
                return Surface.ROTATION_90;
            default:
                return -1;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // pause exoPlayer
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
        // allow system to handle rotations again after leaving the fragment
        mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // release exoPlayer
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
    }

    /**
     * Sets up the exo player with the Step video
     *
     * @param videoUri
     * @param position
     */
    private void initializePlayer(Uri videoUri, long position) {
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
            // with. so best to do it dynamically through here
            mExoPlayer.addVideoListener(new VideoListener() {
                @Override
                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

                }

                @Override
                public void onRenderedFirstFrame() {
                    if (mTabletLayout || !isFullscreen()) {
                        mPlayerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            mExoPlayer.seekTo(position);
            mExoPlayer.setPlayWhenReady(mVideoPlaying);
        }
    }

    /**
     * Helper method for forcing exolayer in and out of fullscreen mode, while enabling jitter
     */
    public void forceChangeFullscreen() {
        setFullscreen(!isFullscreen(), mCurrentOrientation); // negate boolean to change fullscreen mode
        mOrientationJitter = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mExoPlayer != null) {
            outState.putLong(STEP_VIDEO_POSITION_EXTRA_KEY, mExoPlayer.getCurrentPosition());
            outState.putBoolean(VIDEO_CURRENTLY_PLAYING_EXTRA_KEY, mVideoPlaying);
            outState.putBoolean(ORIENTATION_JITTER_EXTRA_KEY, mOrientationJitter);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Helper method to set fullscreen mode for the exo player depending on current screen
     * configuration
     *
     * @param config
     */
    public void setFullscreen(Configuration config) {
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT)
            setFullscreen(false, mCurrentOrientation);
        else setFullscreen(true, mCurrentOrientation);
    }

    /**
     * Helper method to set fullscreen mode for the exo player
     *
     * @param setFullscreen
     * @param landscapeOrientation
     */
    public void setFullscreen(boolean setFullscreen, int landscapeOrientation) {
        if (!mTabletLayout) {
            // TODO: Don't do rotations if in tablet layout
            if (setFullscreen) enterFullscreen(landscapeOrientation);
            else exitFullscreen();
            mCurrentOrientation = getCurrentOrientation(mHostActivity);
        }
    }

    /**
     * Fill the screen with the exo player, while also hiding the app bar and status bar. Always
     * put in Landscape mode
     */
    private void enterFullscreen(int landscapeOrientation) {
        mBtn_exoFullscreen.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp);

        hideSystemBars();

        if (landscapeOrientation == Surface.ROTATION_270)
            // "Reverse landscape" is when device's left side is on top
            mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        else
            mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * Set exo player to default wrap content height, while also bringing back the app bar and
     * status bar. Always exit Landscape mode
     */
    private void exitFullscreen() {
        mBtn_exoFullscreen.setImageResource(R.drawable.ic_fullscreen_white_24dp);

        // show the app bar and status bar
        showSystemBars();

        mHostActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public boolean isFullscreen() {
        return mHostActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public int getCurrentOrientation() {
        return mCurrentOrientation;
    }

    public void setOrientationJitter(boolean value) {
        mOrientationJitter = true;
    }

    /**
     * Helper for getting current screen orientation. See more: getRotation() method
     *
     * @param context
     * @return
     */
    private int getCurrentOrientation(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
    }

    private void showSystemBars() {
        if (!mActionBar.isShowing())
            mActionBar.show();
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    private void hideSystemBars() {
        if (mActionBar.isShowing())
            mActionBar.hide();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
