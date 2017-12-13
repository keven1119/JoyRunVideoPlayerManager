package co.joyrun.videoplayer.video_player_manager.widget;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.joyrun.videoplayer.video_player_manager.R;
import co.joyrun.videoplayer.video_player_manager.ui.MediaPlayerWrapper;
import co.joyrun.videoplayer.video_player_manager.ui.VideoPlayerView;
import co.joyrun.videoplayer.video_player_manager.utils.Logger;

import static android.media.MediaPlayer.MEDIA_ERROR_TIMED_OUT;

public class ExpendableVideoPlayerView extends FrameLayout implements VideoInterfaceV2,
                                                        View.OnClickListener,TextureView.SurfaceTextureListener ,
        MediaPlayerWrapper.MainThreadMediaPlayerListener {

    private final static String TAG = ExpendableVideoPlayerView.class.getName();
    private static final int UPDATE_PROGRESS = 0x1;
    private static final int HIDE_CONTROLLER = 0x2;
    private static final int UPDATE_VIEW = 0x3;
    private static final int UPDATE_CONFIGURATION_VIEW = 0x4;

    private Handler mHandler;
    private boolean mPortrait = true;
    private View mRoot, mCover;
    private ImageView mCoverImg;
    private SeekBar mSeekBar_progress;
    private ImageView mImageView_volume, mImageView_expend, mImageView_mute, mImageView_resize, mImageView_play, mImageView_pause, mImageview_back;
    private VideoPlayerView mVideoPlayerView;
    private LinearLayout mLayout_video;
    private TextView mTextView_currentTime, mTextView_totalTime;
    private FrameLayout mFrameLayout_controller;
    private FrameLayout mVideo_play_video_layout;
    private FrameLayout mVideo_play_whole_layout;
    private Activity mActivity;
    private int mHeight, mWidth;
    private ProgressBar mProgressBar_loading;
    private AudioManager audioManager = null; // 音频
    private String mCoverPath;
    private OnStartVideoListener mStartVideoListener;
    private int mProgress;

    private SpaceLandDialog mSpaceLandDialog;
    private MediaPlayerWrapper.State beforeState;//记录切换屏前的视频状态

    public ExpendableVideoPlayerView(Context context) {
        this(context, null, 0);
    }

    public ExpendableVideoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpendableVideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mActivity = (Activity) context;
        audioManager = (AudioManager) mActivity.getSystemService(Service.AUDIO_SERVICE);
        mRoot = inflate(context, R.layout.layout_video, this);
        mSeekBar_progress = (SeekBar) mRoot.findViewById(R.id.video_progress);
        mImageView_expend = (ImageView) mRoot.findViewById(R.id.imageview_expend);
        mImageView_volume = (ImageView) mRoot.findViewById(R.id.imageview_volume);
        mImageView_play = (ImageView) mRoot.findViewById(R.id.imageview_play);
        mImageView_pause = (ImageView) mRoot.findViewById(R.id.imageview_pause);
        mCoverImg = (ImageView) mRoot.findViewById(R.id.imageview_cover);
        mImageView_mute = (ImageView) mRoot.findViewById(R.id.imageview_mute);
        mImageView_resize = (ImageView) mRoot.findViewById(R.id.imageview_resize);
        mImageview_back = (ImageView) mRoot.findViewById(R.id.imageview_back);
        mVideoPlayerView = (VideoPlayerView)mRoot.findViewById(R.id.video_play_view);
        mProgressBar_loading = (ProgressBar) mRoot.findViewById(R.id.progress_loading);
        mLayout_video = (LinearLayout) mRoot.findViewById(R.id.layout_video);
        mCover = mRoot.findViewById(R.id.view_video);
        mTextView_currentTime = (TextView) mRoot.findViewById(R.id.tv_video_currenttime);
        mTextView_totalTime = (TextView) mRoot.findViewById(R.id.tv_video_totaltime);
        mFrameLayout_controller = (FrameLayout) mRoot.findViewById(R.id.layout_controller);
        mVideo_play_video_layout = mRoot.findViewById(R.id.video_play_video_layout);
        mVideo_play_whole_layout = mRoot.findViewById(R.id.video_play_whole_layout);

        mSeekBar_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mHandler.removeMessages(UPDATE_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mVideoPlayerView.seekTo(mSeekBar_progress.getProgress());
                mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
            }
        });

        mLayout_video.setOnClickListener(this);
        mCover.setOnClickListener(this);
        mImageView_volume.setOnClickListener(this);
        mImageView_expend.setOnClickListener(this);
        mImageView_pause.setOnClickListener(this);
        mImageView_play.setOnClickListener(this);
        mImageView_mute.setOnClickListener(this);
        mImageview_back.setOnClickListener(this);
        mImageView_volume.setOnClickListener(this);
        mImageView_resize.setOnClickListener(this);
        init();
    }

    public void init() {
        if (mCoverPath != null) {
            setCover(mCoverPath);
        }
        mSeekBar_progress.setMax(0);
        mSeekBar_progress.setProgress(0);
        mSeekBar_progress.setSecondaryProgress(0);
        mTextView_currentTime.setText(dataFormat(0));
        mTextView_totalTime.setText(dataFormat(0));
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == UPDATE_PROGRESS) {
                    mSeekBar_progress.setProgress(mVideoPlayerView.getCurrentPrecent());
                    mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
                    mTextView_currentTime.setText(dataFormat((int) (mVideoPlayerView.getCurrentPrecent() + 500)));
                } else if (msg.what == HIDE_CONTROLLER) {
                    mLayout_video.setVisibility(GONE);
                    mCover.setVisibility(VISIBLE);
                    mImageView_play.setVisibility(GONE);
                    mImageView_pause.setVisibility(GONE);
                }else if(msg.what == UPDATE_VIEW) {
                    updateView();
                }else if(msg.what == UPDATE_CONFIGURATION_VIEW) {
                    if(beforeState == MediaPlayerWrapper.State.STARTED){
                        start();
                    }else if(beforeState == MediaPlayerWrapper.State.PAUSED){
                        seekTo(mProgress);//该操作是防止屏幕黑屏
                    }
                }
            }
        };
        initVideoPlayer();
    }

    private float mLastY, mLastX;
    private int mTouchSlop = 10;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mVideoPlayerView.getCurrentState() == MediaPlayerWrapper.State.PREPARING )
            return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getY();
                mLastX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float absX, absY;
                absX = Math.abs(ev.getX() - mLastX);
                absY = Math.abs(ev.getY() - mLastY);
                if (absY > absX && absY > mTouchSlop) {
                    requestDisallowInterceptTouchEvent(true);
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInPlaybackState() || mPortrait)
            return false;
        if (event.getAction() == MotionEvent.ACTION_MOVE && isInPlaybackState()) {
            float diffY = event.getY() - mLastY;
            if (Math.abs(diffY) > mTouchSlop) {
                if (diffY > 0) {
                    //降低音量
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND
                                    | AudioManager.FLAG_SHOW_UI);
                } else {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND
                                    | AudioManager.FLAG_SHOW_UI);
                }
                mLastY = event.getY();
            }
        }
        return true;
    }

    private void initVideoPlayer(){
        mVideoPlayerView.addMediaPlayerListener(this);
        mVideoPlayerView.setSurfaceTextureListener(this);
        mVideoPlayerView.seekTo(mProgress);
    }


    @Override
    public void onVideoSizeChangedMainThread(int width, int height) {

    }

    @Override
    public void onVideoPreparedMainThread() {
        initText();
        start();
        mHandler.sendEmptyMessage(UPDATE_VIEW);
    }

    @Override
    public void onVideoCompletionMainThread() {
        if(!isInPlaybackState())
            return;
        mHandler.sendEmptyMessage(UPDATE_VIEW);
        if (mHandler != null) {
            mHandler.removeMessages(UPDATE_PROGRESS);
            mHandler.removeMessages(HIDE_CONTROLLER);
        }
    }

    @Override
    public void onErrorMainThread(int what, int extra) {
        if(what == MEDIA_ERROR_TIMED_OUT){
            Toast.makeText(getContext(),"网络超时..",Toast.LENGTH_LONG).show();
        }
//        release();
//            seekTo(10)

    }

    @Override
    public void onBufferingUpdateMainThread(int percent) {
        int p = (int) (mSeekBar_progress.getMax() * (percent / 100f));
        if (p > mSeekBar_progress.getMax()) {
            p = mSeekBar_progress.getMax();
        }
        mSeekBar_progress.setSecondaryProgress(p);
    }

    @Override
    public void onVideoStoppedMainThread() {
        if (mVideoPlayerView.getMediaPlayerWrapper() != null && isInPlaybackState()) {
            mProgress = mSeekBar_progress.getProgress();
        }
    }

    @Override
    public void onVideoSeekComplete() {
        if (mVideoPlayerView.getCurrentState() == MediaPlayerWrapper.State.STARTED) {
            start();
        }
    }

    private void initText() {
        if (mVideoPlayerView.getMediaPlayerWrapper() == null)
            return;
        mSeekBar_progress.setMax(mVideoPlayerView.getDuration());
        mSeekBar_progress.setProgress(0);
//        if (mPath != null) {
            mSeekBar_progress.setSecondaryProgress(mVideoPlayerView.getDuration());
//        }
        mVideoPlayerView.seekTo(0);
        mTextView_currentTime.setText(dataFormat(0));
        mTextView_totalTime.setText(dataFormat((int) (mVideoPlayerView.getDuration() + 500)));
    }

    //是否在可操作状态
    public boolean isInPlaybackState() {
        return (mVideoPlayerView.getMediaPlayerWrapper() != null &&
                mVideoPlayerView.getCurrentState() != MediaPlayerWrapper.State.ERROR &&
                mVideoPlayerView.getCurrentState() != MediaPlayerWrapper.State.IDLE &&
                mVideoPlayerView.getCurrentState() != MediaPlayerWrapper.State.PREPARING);
    }

    //输出16：9
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = (int) ((widthSize * 9 / 16) + 0.5);
        if (mWidth == 0 && mHeight == 0) {
            mWidth = widthSize;
            mHeight = heightSize;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }

    public String dataFormat(int time) {
        int totalSeconds = (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
                seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public boolean start() {
//        mMediaPlayer.start();
        boolean start = mVideoPlayerView.start();
        if(start) {
            if (mVideoPlayerView.getMediaPlayerWrapper() != null)
                if(!mHandler.hasMessages(UPDATE_PROGRESS)) {
                    mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
                }
            mHandler.sendEmptyMessage(UPDATE_VIEW);
        }

        return start;
    }

    @Override
    public void setDataSource(String path) {
        mVideoPlayerView.setDataSource(path);
    }

    public String  getDataSource(){
        return mVideoPlayerView.getDataSource();
    }

    @Override
    public void setDataSource(AssetFileDescriptor assetFileDescriptor) {
        mVideoPlayerView.setDataSource(assetFileDescriptor);
    }

    @Override
    public void setOnVideoStateChangedListener(MediaPlayerWrapper.VideoStateListener listener) {
        mVideoPlayerView.setOnVideoStateChangedListener(listener);
    }

    @Override
    public void addMediaPlayerListener(MediaPlayerWrapper.MainThreadMediaPlayerListener listener) {
        mVideoPlayerView.addMediaPlayerListener(listener);
    }

    @Override
    public void setBackgroundThreadMediaPlayerListener(VideoPlayerView.BackgroundThreadMediaPlayerListener listener) {
        mVideoPlayerView.setBackgroundThreadMediaPlayerListener(listener);
    }

    @Override
    public void muteVideo() {
        mVideoPlayerView.muteVideo();
    }

    @Override
    public void unMuteVideo() {
        mVideoPlayerView.unMuteVideo();
    }

    @Override
    public boolean isAllVideoMute() {
        return mVideoPlayerView.isAllVideoMute();
    }

    private MaterialDialog createDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity);
        MaterialDialog dialog = builder.content(mActivity.getString(R.string.video_download_no_wifi))
                .positiveText(mActivity.getString(R.string.video_download_no_wifi_continue))
                .negativeText(mActivity.getString(R.string.video_download_no_wifi_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        prepare();
                        if (mStartVideoListener != null) {
                            mStartVideoListener.onStart();
                        }
                    }
                }).build();

        return dialog;
    }

    public void prepare() {
        mVideoPlayerView.prepare();
        mHandler.sendEmptyMessage(UPDATE_VIEW);
    }

    @Override
    public void stop() {
        mVideoPlayerView.stop();
    }

    public void setCover(String url) {
        mCoverPath = url;
        if(!TextUtils.isEmpty(mCoverPath)) {
            try {
                Glide.with(getContext()).load(url).into(mCoverImg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String getCoverPath() {
        return mCoverPath;
    }

    public void reset(){
        if(mVideoPlayerView.getMediaPlayerWrapper()==null)
            return;
//        mVideoPlayerView.setCurrentState(MediaPlayerWrapper.State.IDLE);
        mHandler.sendEmptyMessage(UPDATE_VIEW);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        if (mHandler != null) {
            mHandler.removeMessages(UPDATE_PROGRESS);
            mHandler.removeMessages(HIDE_CONTROLLER);
        }
        mVideoPlayerView.reset();
    }

    @Override
    public void release() {
        mVideoPlayerView.release();
    }

    @Override
    public void clearPlayerInstance() {
        mVideoPlayerView.clearPlayerInstance();
    }

    @Override
    public void createNewPlayerInstance() {
        mVideoPlayerView.createNewPlayerInstance();
    }

    public void pause() {
        if (!isInPlaybackState()) {
            return;
        }
        if (mHandler != null) {
            mHandler.removeMessages(UPDATE_PROGRESS);
//            mVideoPlayerView.setCurrentState(MediaPlayerWrapper.State.PAUSED);
        }
        if (mVideoPlayerView.getMediaPlayerWrapper() != null) {
            mVideoPlayerView.pause();
            mHandler.sendEmptyMessage(UPDATE_VIEW);
        }
    }

    @Override
    public int getDuration() {
        return mVideoPlayerView.getDuration();
    }

    @Override
    public void seekTo(int progress) {
        mVideoPlayerView.seekTo(progress);
    }

    @Override
    public int getCurrentPrecent() {
        return mVideoPlayerView.getCurrentPrecent();
    }

    @Override
    public MediaPlayerWrapper.State getCurrentState() {
        return mVideoPlayerView.getCurrentState();
    }

    @Override
    public AssetFileDescriptor getAssetFileDescriptorDataSource() {
        return mVideoPlayerView.getAssetFileDescriptorDataSource();
    }

    public void close() {
        if (mHandler != null) {
            mHandler.removeMessages(UPDATE_PROGRESS);
            mHandler.removeMessages(HIDE_CONTROLLER);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mVideoPlayerView.getMediaPlayerWrapper() != null) {
            mVideoPlayerView.release();
//            mVideoPlayerView.clearPlayerInstance();
        }
    }

    private boolean isWifiConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) mActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.layout_video) {//隐藏控制按钮
            mLayout_video.setVisibility(GONE);
            mCover.setVisibility(VISIBLE);
            mImageView_play.setVisibility(GONE);
            mImageView_pause.setVisibility(GONE);

        } else if (i == R.id.view_video) {//显示控制按钮
            mLayout_video.setVisibility(VISIBLE);
            mCover.setVisibility(GONE);
            if (mVideoPlayerView.getCurrentState() == MediaPlayerWrapper.State.STARTED) {
                mImageView_pause.setVisibility(VISIBLE);
            } else {
                mImageView_play.setVisibility(VISIBLE);
            }

        } else if (i == R.id.imageview_pause) {
            pause();

        } else if (i == R.id.imageview_play) {
            Logger.v(TAG,"CurrentState ==>" +mVideoPlayerView.getCurrentState().name());
            if (mVideoPlayerView.getCurrentState() == MediaPlayerWrapper.State.INITIALIZED ||
                    mVideoPlayerView.getCurrentState() == MediaPlayerWrapper.State.IDLE) {
                if (!isWifiConnected()) {
                    createDialog().show();
                    return;
                }
                prepare();
                if (mStartVideoListener != null) {
                    mStartVideoListener.onStart();
                }
            } else {
                start();
                mImageView_pause.setVisibility(VISIBLE);
            }
        } else if (i == R.id.imageview_mute) {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mImageView_volume.setVisibility(VISIBLE);
            mImageView_mute.setVisibility(GONE);

        } else if (i == R.id.imageview_volume) {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            mImageView_mute.setVisibility(VISIBLE);
            mImageView_volume.setVisibility(GONE);

        } else if (i == R.id.imageview_expend) {
            isExpending = true;
            request2Landspace();

        } else if (i == R.id.imageview_resize) {
            request2Portrait();
        } else if (i == R.id.imageview_back) {
            request2Portrait();
        }
        mHandler.removeMessages(HIDE_CONTROLLER);
        mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 3000);
    }

    public void request2Landspace(){
        if (mActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public void request2Portrait(){
        if (mActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private boolean isExpending;

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onChanged(newConfig);
    }

    public synchronized void onChanged(Configuration newConfig) {
        mPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(mPortrait);

        if(mPortrait){
            if(mSpaceLandDialog != null) {
                mSpaceLandDialog.dismiss();
                mSpaceLandDialog = null;
            }
        }else {
            if(isExpending) {
                if(mSpaceLandDialog != null) {
                    mSpaceLandDialog.dismiss();
                    mSpaceLandDialog = null;
                }
                mSpaceLandDialog = new SpaceLandDialog(getContext());
                mSpaceLandDialog.show();
                isExpending = false;
            }

        }


    }

    public boolean isPortrait() {
        return mPortrait;
    }

    /**
     * 当竖横屏切换时处理视频窗口
     *
     * @param portrait
     */
    private void doOnConfigurationChanged( boolean portrait) {
        final boolean tempIsPortrait = portrait;
        if (mVideoPlayerView.getMediaPlayerWrapper() != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!tempIsPortrait);
                    if (tempIsPortrait) {
                        ViewGroup.LayoutParams layoutParams = mVideo_play_video_layout.getLayoutParams();
                        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        layoutParams.width = mWidth;
                        layoutParams.height = mHeight;
                        mVideo_play_video_layout.setLayoutParams(layoutParams);
                        requestLayout();
                    } else {
                        int heightPixels = mActivity.getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = mActivity.getResources().getDisplayMetrics().widthPixels;
                        ViewGroup.LayoutParams layoutParams = mVideo_play_video_layout.getLayoutParams();
                        layoutParams.height = heightPixels;
                        layoutParams.width = widthPixels;
                        mVideo_play_video_layout.setLayoutParams(layoutParams);
                    }
                    updateScreenButton();
                }
            });
        }
    }

    private void updateScreenButton() {
        if (mPortrait) {
            mImageView_resize.setVisibility(GONE);
            mImageView_expend.setVisibility(VISIBLE);
            mImageView_play.setImageResource(R.drawable.video_play);
            mImageView_pause.setImageResource(R.drawable.video_pause);
            mImageView_volume.setImageResource(R.drawable.ico_volume);
            mImageView_mute.setImageResource(R.drawable.ico_mute);
            mImageview_back.setVisibility(GONE);
        } else {
            mImageView_resize.setVisibility(VISIBLE);
            mImageView_expend.setVisibility(GONE);
            mImageView_play.setImageResource(R.drawable.play_fullscreen);
            mImageView_pause.setImageResource(R.drawable.pause_fullscreen);
            mImageView_volume.setImageResource(R.drawable.ico_volume_fullscreen);
            mImageView_mute.setImageResource(R.drawable.ico_mute_fullscreen);
            mImageview_back.setVisibility(VISIBLE);
        }
    }

    private void setFullScreen(boolean fullScreen) {
        if (mActivity != null) {
            WindowManager.LayoutParams attrs = mActivity.getWindow()
                    .getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }

    private void initControllerView() {
        mImageView_play.setVisibility(GONE);
        mImageView_pause.setVisibility(GONE);
        mProgressBar_loading.setVisibility(GONE);
        mFrameLayout_controller.setVisibility(VISIBLE);
        mCoverImg.setVisibility(VISIBLE);
    }

    private void updateView() {
        MediaPlayerWrapper.State currentState = mVideoPlayerView.getCurrentState();

        if(currentState == MediaPlayerWrapper.State.IDLE){
            //停止状态
            initControllerView();
            mFrameLayout_controller.setVisibility(INVISIBLE);
            mImageView_play.setVisibility(VISIBLE);
            mImageView_mute.setVisibility(GONE);
            mImageView_volume.setVisibility(VISIBLE);
        }else if(currentState == MediaPlayerWrapper.State.PREPARING || currentState == MediaPlayerWrapper.State.INITIALIZED){
            //加载中
            initControllerView();
            mProgressBar_loading.setVisibility(VISIBLE);
        }else if(currentState == MediaPlayerWrapper.State.PREPARED ){
            //准备中
            initControllerView();
            initText();
            mImageView_play.setVisibility(VISIBLE);
        }else if(currentState == MediaPlayerWrapper.State.PAUSED){
            //暂停
            initControllerView();
            mImageView_play.setVisibility(VISIBLE);
            mCoverImg.setVisibility(GONE);

        }else if(currentState == MediaPlayerWrapper.State.STARTED){
            //播放
            initControllerView();
            mImageView_pause.setVisibility(VISIBLE);
            mCoverImg.setVisibility(GONE);
        }else if(currentState == MediaPlayerWrapper.State.PLAYBACK_COMPLETED){
            //播放完毕
            initControllerView();
            initText();
            mImageView_play.setVisibility(VISIBLE);
        }
    }

    public void setStartVideoListener(OnStartVideoListener listener) {
        mStartVideoListener = listener;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        updateView();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mVideoPlayerView.getMediaPlayerWrapper() != null && isInPlaybackState()) {
                    mProgress = mSeekBar_progress.getProgress();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    public interface OnStartVideoListener {
        void onStart();
    }

    class  SpaceLandDialog extends Dialog {

        private FrameLayout dialogContainer;

        public SpaceLandDialog(@NonNull Context context) {
            super(context, R.style.Dialog_Fullscreen);
            setContentView(R.layout.landspace_dialog_layout);
            dialogContainer = findViewById(R.id.framelayout_landspace_dialog);
        }


        @Override
        public void show() {
            //当切换横竖屏时需要先暂停播放，否则mediaplayer 会报  error (1, -19)  ，error (1, -38)
            saveCurrentState();
            mVideo_play_whole_layout.removeView(mVideo_play_video_layout);
            dialogContainer.addView(mVideo_play_video_layout);
            super.show();
            mHandler.sendEmptyMessageDelayed(UPDATE_CONFIGURATION_VIEW,100);
//            mHandler.sendEmptyMessage(UPDATE_CONFIGURATION_VIEW);
        }

        @Override
        public void onBackPressed() {
            if(isPortrait()){
                super.onBackPressed();
            }else {
                request2Portrait();
            }
        }

        /**
         * 保存当前状态，并暂停
         */
        private void  saveCurrentState(){
            beforeState = getCurrentState();
            if(beforeState == MediaPlayerWrapper.State.STARTED) {
                pause();
            }else if(beforeState == MediaPlayerWrapper.State.PAUSED){
                mProgress = mSeekBar_progress.getProgress();
                pause();
            }
        }

        @Override
        public void dismiss() {
            //当切换横竖屏时需要先暂停播放，否则mediaplayer 会报  error (1, -19)  ，error (1, -38)
            saveCurrentState();
            dialogContainer.removeView(mVideo_play_video_layout);
            mVideo_play_whole_layout.addView(mVideo_play_video_layout);
            super.dismiss();
            mHandler.sendEmptyMessageDelayed(UPDATE_CONFIGURATION_VIEW,100);
//            mHandler.sendEmptyMessage(UPDATE_CONFIGURATION_VIEW);
        }

    }

}