package at.stefanirndorfer.bakingapp.view;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;
import java.util.Objects;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Step;
import at.stefanirndorfer.bakingapp.databinding.FragmentStepBinding;
import at.stefanirndorfer.bakingapp.view.input.FragmentNavigationListener;
import at.stefanirndorfer.bakingapp.viewmodel.StepsViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;
import timber.log.Timber;

/**
 * FullScreen support as seen here:
 * https://github.com/GeoffLedak/ExoplayerFullscreen
 */
public class StepFragment extends Fragment implements Player.EventListener {

    private static final String TAG = StepFragment.class.getSimpleName();
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private final String CURR_STEP_ID = "currentStepId";
    private final String CURR_RECIPE_ID = "currRecipeId";


    FragmentStepBinding mBinding;
    private StepsViewModel mViewModel;
    private Step mStep;
    private List<Step> mSteps;

    private FragmentNavigationListener mFragmentNavigationListener;

    private SimpleExoPlayer mExoPlayer;
    PlayerView mPlayerView;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;

    private int mResumeWindow;
    private long mResumePosition;
    private int mCurrRecipeId;
    private int mCurrStepId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        mBinding = FragmentStepBinding.inflate(inflater, container, false);
        mViewModel = obtainViewModel(Objects.requireNonNull(this.getActivity()));
        mBinding.setViewModel(mViewModel);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
            mCurrRecipeId = savedInstanceState.getInt(CURR_RECIPE_ID);
            mCurrStepId = savedInstanceState.getInt(CURR_STEP_ID);
        }
        subscribeOnStepsData();


        mPlayerView = mBinding.exoplayer;
        mPlayerView.setDefaultArtwork(getResources().getDrawable(R.drawable.question_mark));
        initFullscreenDialog();
        initFullscreenButton();

        mBinding.setStep(mStep);
        adaptViewIfVideoURLIsNull();

        setupButtonOnClickHanderls();
        return mBinding.getRoot();
    }

    private void adaptViewIfVideoURLIsNull() {
        if (mStep != null) {
            if (TextUtils.isEmpty(mStep.getVideoURL())) {
                mBinding.mainMediaFrame.setVisibility(View.GONE);
            } else {
                mBinding.mainMediaFrame.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.start(mCurrRecipeId);
        initPlayer();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mFragmentNavigationListener = (FragmentNavigationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentNavigationListener");
        }
    }

    private int getIndexOfCurrentStep() {
        for (int i = 0; i < mSteps.size(); i++) {
            if (mSteps.get(i).getId() == mCurrStepId) {
                return i;
            }
        }
        throw new IllegalStateException("Current StepId does not exist.");
    }

    private void updateStep() {
        bindCorrectStep(mSteps);
        if (mExoPlayer != null) {
            mExoPlayer.stop(true);
            if (mStep.getVideoURL() != null && !TextUtils.isEmpty(mStep.getVideoURL())) {
                prepareMediaSource(Uri.parse(mStep.getVideoURL()));
            }
        }
        updateNavigationButtons();
    }

    private void bindCorrectStep(List<Step> mSteps) {
        for (Step currStep :
                mSteps) {
            if (currStep.getId() == mCurrStepId) {
                mStep = currStep;
                mBinding.setStep(mStep);
                adaptViewIfVideoURLIsNull();
                return;
            }
        }
        throw new IllegalStateException("Current StepId cannot be found in list fetched from repository.");
    }

    private void initPlayer() {
        Timber.d("Initializing player");

        // Initialize the Media Session.
        initializeMediaSession();
        initializePlayer(Uri.parse(mStep.getVideoURL()));
        setFullscreenIfLandscape();
    }

    /**
     * sets the player to fullscreen if the device is a phone and
     * orientation is landscape
     */
    private void setFullscreenIfLandscape() {
        boolean twoPane = getResources().getBoolean(R.bool.isTablet);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !twoPane) {
            openFullscreenDialog();
        }
    }

    private void subscribeOnStepsData() {
        mViewModel.getSteps().observe(this, new Observer<List<Step>>() {

            @Override
            public void onChanged(@Nullable List<Step> steps) {
                if (steps != null && !steps.isEmpty()) {
                    Timber.d("Current Id: " + mCurrStepId);
                    Timber.d("Received Steps size: " + steps.size());
                    bindCorrectStep(steps);
                    mSteps = steps;
                    initPlayer();
                    updateNavigationButtons();
                }
            }
        });
    }

    private void updateNavigationButtons() {
        if (mSteps != null && !mSteps.isEmpty()) {
            if (mCurrStepId == 0) {
                mBinding.previousStepBt.setVisibility(View.INVISIBLE);
            } else {
                mBinding.previousStepBt.setVisibility(View.VISIBLE);
            }
            if (mCurrStepId == mSteps.size() - 1) {
                mBinding.nextStepBt.setVisibility(View.INVISIBLE);
            } else {
                mBinding.nextStepBt.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        outState.putInt(CURR_RECIPE_ID, mCurrRecipeId);
        outState.putInt(CURR_STEP_ID, mCurrStepId);
        super.onSaveInstanceState(outState);
    }


    private void initializePlayer(Uri uri) {
        if (mExoPlayer == null) {
            Timber.d("VideUrl: " + uri.toString());
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this.getActivity(), new DefaultRenderersFactory(this.getContext()), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);


            mExoPlayer.addListener(this);
            prepareMediaSource(uri);

            boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                Timber.d("Player can resume on position: " + mResumePosition);
                mExoPlayer.seekTo(mResumeWindow, mResumePosition);
            }
        }
    }

    private void prepareMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(this.getActivity(), "Baking App");
        MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(
                this.getActivity(), userAgent)).createMediaSource(uri);

        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {
        if (mMediaSession == null) {
            // Create a MediaSessionCompat.
            mMediaSession = new MediaSessionCompat(this.getContext(), TAG);

            // Enable callbacks from MediaButtons and TransportControls.
            mMediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            // Do not let MediaButtons restart the player when the app is not visible.
            mMediaSession.setMediaButtonReceiver(null);

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
            mStateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PAUSE |
                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE);

            mMediaSession.setPlaybackState(mStateBuilder.build());


            // MySessionCallback has methods that handle callbacks from a media controller.
            mMediaSession.setCallback(new MySessionCallback());

            // Start the Media Session since the activity is active.
            mMediaSession.setActive(true);
        }
    }

    // Player Events


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        mFullScreenDialog.addContentView(mPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        mBinding.mainMediaFrame.addView(mPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton() {
        PlayerControlView controlView = mPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause");
        if (mPlayerView != null && mPlayerView.getPlayer() != null) {
            mResumeWindow = mPlayerView.getPlayer().getCurrentWindowIndex();
            mResumePosition = Math.max(0, mPlayerView.getPlayer().getContentPosition());
        }
        if (mFullScreenDialog != null) {
            mFullScreenDialog.dismiss();
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            releasePlayer();
        }
    }

    private void destroyMediaSession() {
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
            mMediaSession = null;
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroy");
        destroyMediaSession();
        mViewModel.getSteps().removeObservers(this);
    }

    public static StepsViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        StepsViewModel viewModel = ViewModelProviders.of(activity, factory).get(StepsViewModel.class);
        return viewModel;
    }

    public void setStep(Step step) {
        if (step != null) {
            mCurrStepId = step.getId();
            mCurrRecipeId = step.getRecipeId();
            this.mStep = step;
        }
    }


    private void setupButtonOnClickHanderls() {
        mBinding.nextStepBt.setOnClickListener(v -> {
            if (mSteps != null && !mSteps.isEmpty()) {
                int i = getIndexOfCurrentStep();
                if (i < mSteps.size() - 1) {
                    mCurrStepId = mSteps.get(i + 1).getId();
                    updateStep();
                }
            }
        });
        mBinding.previousStepBt.setOnClickListener(v -> {
            if (mSteps != null && !mSteps.isEmpty()) {
                int i = getIndexOfCurrentStep();
                if (1 > 0) {
                    mCurrStepId = mSteps.get(i - 1).getId();
                    updateStep();
                }
            }
        });

        mBinding.viewIngredientsBt.setOnClickListener(v -> {
            mFragmentNavigationListener.navigateToIngredientsFragment(mCurrRecipeId);
        });
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
