package at.stefanirndorfer.bakingapp.view;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
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
        mViewModel.start(mCurrRecipeId);

        if (mPlayerView == null) {
            mPlayerView = mBinding.exoplayer;
            mPlayerView.setDefaultArtwork(getResources().getDrawable(R.drawable.question_mark));
            initFullscreenDialog();
            initFullscreenButton();
        }

        if (mStep != null) {
            mBinding.setStep(mStep);
            initPlayer();
        }
        return mBinding.getRoot();
    }

    private void initPlayer() {
        if (mStep.getVideoURL() != null && !TextUtils.isEmpty(mStep.getVideoURL())) {
            // Initialize the Media Session.
            initializeMediaSession();
            initializePlayer(Uri.parse(mStep.getVideoURL()));
        }
    }

    private void subscribeOnStepsData() {
        mViewModel.getSteps().observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                if (steps != null && !steps.isEmpty()) {
                    mStep = steps.get(mCurrStepId);
                    mBinding.setStep(mStep);
                    initPlayer();
                    updateNavigationButtons(steps);
                }
            }
        });
    }

    private void updateNavigationButtons(List<Step> steps) {
        if (mCurrStepId == 0){
            mBinding.previousStepBt.setVisibility(View.INVISIBLE);
        } else {
            mBinding.previousStepBt.setVisibility(View.VISIBLE);
        }
        if (mCurrStepId == steps.size()-1){
            mBinding.nextStepBt.setVisibility(View.INVISIBLE);
        } else {
            mBinding.nextStepBt.setVisibility(View.VISIBLE);
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

            boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                mPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
            }

            mExoPlayer.addListener(this);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this.getActivity(), "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(
                    this.getActivity(), userAgent)).createMediaSource(uri);

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
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
        mViewModel.getSteps().removeObservers(this);
        releasePlayer();
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
            mMediaSession = null;
        }
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
