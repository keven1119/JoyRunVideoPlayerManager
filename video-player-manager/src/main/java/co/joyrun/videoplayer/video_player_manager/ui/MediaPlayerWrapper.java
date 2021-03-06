package co.joyrun.videoplayer.video_player_manager.ui;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import com.joyrun.videoplayer.video_player_manager.BuildConfig;

import co.joyrun.videoplayer.video_player_manager.Config;
import co.joyrun.videoplayer.video_player_manager.utils.Logger;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class encapsulates {@link MediaPlayer}
 * and follows this use-case diagram:
 *
 * http://developer.android.com/reference/android/media/MediaPlayer.html
 */
public abstract class MediaPlayerWrapper
        implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnSeekCompleteListener
{

    private String TAG;
    private static final boolean SHOW_LOGS = BuildConfig.DEBUG;

    public static final int POSITION_UPDATE_NOTIFYING_PERIOD = 1000;         // milliseconds
    private ScheduledFuture<?> mFuture;
    private Surface mSurface;
    public enum State {
        IDLE,  // seDatasource  之前
        INITIALIZED, // setDataSource  之后
        PREPARING, //调用 prepareAsync 之后
        PREPARED, //调用 prepare 之后
        STARTED, //调用 start() 之后
        PAUSED,  //调用pause() 之后
        STOPPED, //调用 stop() 之后
        PLAYBACK_COMPLETED, //onCompletionListener 回调之后
        END,  // 调用release之后
        ERROR //onErrorListerer  回调之后
    }

    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private MediaPlayer mMediaPlayer;
    private final AtomicReference<State> mState = new AtomicReference<>();

    private MainThreadMediaPlayerListener mListener;
    private VideoStateListener mVideoStateListener;

    private ScheduledExecutorService mPositionUpdateNotifier = Executors.newScheduledThreadPool(1);
    private boolean isAutoPlay = false;

    protected MediaPlayerWrapper(MediaPlayer mediaPlayer) {
        TAG = "" + this;
        if (SHOW_LOGS) Logger.v(TAG, "constructor of MediaPlayerWrapper");
        if (SHOW_LOGS) Logger.v(TAG, "constructor of MediaPlayerWrapper, main Looper " + Looper.getMainLooper());
        if (SHOW_LOGS) Logger.v(TAG, "constructor of MediaPlayerWrapper, my Looper " + Looper.myLooper());

        if(Looper.myLooper() != null){
            throw new RuntimeException("myLooper not null, a bug in some MediaPlayer implementation cause that listeners are not called at all. Please use a thread without Looper");
        }
        mMediaPlayer = mediaPlayer;

        mState.set(State.IDLE);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
    }

    private final Runnable mOnVideoPreparedMessage = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoPreparedMainThread");
            mListener.onVideoPreparedMainThread();
            if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoPreparedMainThread");
        }
    };

    private final Runnable mOnVideoStartedMessage = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoStartMainThread");
            mListener.onVideoStartMainThread();
            if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoStartMainThread");
        }
    };

    private final Runnable mOnVideoPauseMessage = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoPauseMainThread");
            mListener.onVideoPauseMainThread();
            if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoPauseMainThread");
        }
    };

    protected boolean isAutoPlay() {
        return isAutoPlay;
    }

    protected void setAutoPlay(boolean isAutoPlay){
        this.isAutoPlay = isAutoPlay;
    }

    public void prepare() {
        if (SHOW_LOGS) Logger.v(TAG, ">> prepare, mState " + mState);

        synchronized (mState) {
            switch (mState.get()) {
                case STOPPED:
                case INITIALIZED:
//                    try {
                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mState.set(State.PREPARED);
                                if (mListener != null) {
                                    mMainThreadHandler.post(mOnVideoPreparedMessage);
                                }

                                if(MediaPlayerWrapper.this.isAutoPlay){
                                    start();

                                    if (mListener != null) {
                                        mMainThreadHandler.post(mOnVideoStartedMessage);
                                    }
                                }

                            }
                        });
                        mMediaPlayer.prepareAsync();
                        mState.set(State.PREPARING);
                    break;
                case IDLE:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                    Logger.v(TAG, ">> prepare, called from illegal state " + mState);
//                    throw new IllegalStateException("prepare, called from illegal state " + mState);
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< prepare, mState " + mState);
    }


    /**
     * This method propagates error when {@link IOException} is thrown during synchronous {@link #prepare(boolean)}
     * @param ex
     */
    private void onPrepareError(IOException ex) {
        if (SHOW_LOGS) Logger.err(TAG, "catch IO exception [" + ex + "]");
        // might happen because of lost internet connection
//      TODO: if (SHOW_LOGS) Logger.err(TAG, "catch exception, is Network Connected [" + Utils.isNetworkConnected());
        mState.set(State.ERROR);
        if(mListener != null){
            mListener.onErrorMainThread(1, -1004); //TODO: remove magic numbers. Find a way to get actual error
        }
        if (mListener != null) {
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoPreparedMainThread");
                    mListener.onErrorMainThread(1, -1004); //TODO: remove magic numbers. Find a way to get actual error
                    if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoPreparedMainThread");
                }
            });
        }
    }

    /**
     * @see MediaPlayer#setDataSource(Context, Uri)
     */
    public void setDataSource(String filePath) throws IOException {
        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "setDataSource, filePath " + filePath + ", mState " + mState);

            switch (mState.get()) {
                case IDLE:
                    mMediaPlayer.setDataSource(filePath);
                    mState.set(State.INITIALIZED);
                    break;
                case INITIALIZED:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                default:
                    Logger.v(TAG, ">> setDataSource called in state " + mState);
//                    throw new IllegalStateException("setDataSource called in state " + mState);
            }
        }
    }

    /**
     * @see MediaPlayer#setDataSource(FileDescriptor fd, long offset, long length)
     */
    public void setDataSource(AssetFileDescriptor assetFileDescriptor) throws IOException {
        synchronized (mState) {
            switch (mState.get()) {
                case IDLE:
                    mMediaPlayer.setDataSource(
                            assetFileDescriptor.getFileDescriptor(),
                            assetFileDescriptor.getStartOffset(),
                            assetFileDescriptor.getLength());
                    mState.set(State.INITIALIZED);
                    break;
                case INITIALIZED:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                default:
                    Logger.v(TAG, ">> setDataSource called in state " + mState);
//                    throw new IllegalStateException("setDataSource called in state " + mState);
            }
        }
    }

    public void setAudioStreamType(int streamtype){
        if(mMediaPlayer != null){
//            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(streamtype);
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (SHOW_LOGS) Logger.v(TAG, "onVideoSizeChanged, width " + width + ", height " + height);
        if(!inUiThread()){
            throw new RuntimeException("this should be called in Main Thread");
        }
        if (mListener != null) {
            mListener.onVideoSizeChangedMainThread(width, height);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (SHOW_LOGS) Logger.v(TAG, "onVideoCompletion, mState " + mState);

        synchronized (mState) {
            mState.set(State.PLAYBACK_COMPLETED);
        }

        if (mListener != null) {
            mListener.onVideoCompletionMainThread();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (SHOW_LOGS) Logger.v(TAG, "onErrorMainThread, what " + what + ", extra " + extra);

        synchronized (mState) {
            mState.set(State.ERROR);
        }

        if(positionUpdaterIsWorking()){
            stopPositionUpdateNotifier();
        }
        if (SHOW_LOGS) Logger.v(TAG, "onErrorMainThread, mListener " + mListener);

        if (mListener != null) {
            mListener.onErrorMainThread(what, extra);
        }
        // We always return true, because after Error player stays in this state.
        // See here http://developer.android.com/reference/android/media/MediaPlayer.html
        return true;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if(mListener != null){
            mListener.onVideoSeekComplete();
        }
    }

    private boolean positionUpdaterIsWorking() {
        return mFuture != null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mListener != null) {
            mListener.onBufferingUpdateMainThread(percent);
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (SHOW_LOGS) Logger.v(TAG, "onInfo");
        printInfo(what);
        return false;
    }

    private void printInfo(int what) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_VIDEO_RENDERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_BUFFERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_BUFFERING_END");
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_NOT_SEEKABLE");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                break;
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_SUBTITLE_TIMED_OUT");
                break;
        }
    }

    /**
     * Listener trigger 'onVideoPreparedMainThread' and `onVideoCompletionMainThread` events
     */
    public void setMainThreadMediaPlayerListener(MainThreadMediaPlayerListener listener) {
        mListener = listener;
    }

    public void setVideoStateListener(VideoStateListener listener) {
        mVideoStateListener = listener;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     * <p/>
     * If video is stopped or ended and play() method was called, video will start over.
     */
    public void start() {
        if (SHOW_LOGS) Logger.v(TAG, ">> start");

        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "start, mState " + mState);

            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case STARTED:
//                    throw new IllegalStateException("start, called from illegal state " + mState);
                    break;
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case PREPARED:
                case PREPARING:
                case PAUSED:

                    if (SHOW_LOGS) Logger.v(TAG, "start, video is " + mState + ", starting playback.");
                    mMediaPlayer.start();
                    startPositionUpdateNotifier();
                    mState.set(State.STARTED);

                    if (mListener != null) {
                        mMainThreadHandler.post(mOnVideoStartedMessage);
                    }

                    break;
                case ERROR:
                case END:
                    mState.set(State.STARTED);
//                    throw new IllegalStateException("start, called from illegal state " + mState);
//                    mMediaPlayer.start();
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< start");
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    public void pause() {
        if (SHOW_LOGS) Logger.v(TAG, ">> pause");

        synchronized (mState) {
            if (SHOW_LOGS)
                Logger.v(TAG, "pause, mState " + mState);

            switch (mState.get()) {
                case PAUSED:
                case IDLE:
                case INITIALIZED:
                case PLAYBACK_COMPLETED:

                case PREPARING:
                case STOPPED:
                case PREPARED:
                case END:
                    break;
//                    throw new IllegalStateException("pause, called from illegal state "  + mState);
                case STARTED:
                    mMediaPlayer.pause();
                    mState.set(State.PAUSED);
                    if (mListener != null) {
                        mMainThreadHandler.post(mOnVideoPauseMessage);
                    }

                    break;

                case ERROR:
//                    mState.set(State.E);
                    break;

            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< pause");
    }

    private final Runnable mOnVideoStopMessage = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoStoppedMainThread");
            mListener.onVideoStoppedMainThread();
            if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoStoppedMainThread");
        }
    };

    public void stop() {
        if (SHOW_LOGS) Logger.v(TAG, ">> stop");

        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "stop, mState " + mState);

            switch (mState.get()) {

                case STARTED:
                case PAUSED:
                    stopPositionUpdateNotifier();
                    // should stop only if paused or started
                    // FALL-THROUGH
                case PLAYBACK_COMPLETED:
                case PREPARED:
                case PREPARING: // This is evaluation of http://developer.android.com/reference/android/media/MediaPlayer.html. Canot stop when preparing

                    if (SHOW_LOGS) Logger.v(TAG, ">> stop");

                    mMediaPlayer.stop();

                    if (SHOW_LOGS) Logger.v(TAG, "<< stop");

                    mState.set(State.STOPPED);

                    if (mListener != null) {
                        mMainThreadHandler.post(mOnVideoStopMessage);
                    }
                    break;
                case STOPPED:
                    Logger.v(TAG, ">> already stopped");
                case IDLE:
                case INITIALIZED:
                case END:
                case ERROR:
                    if(mListener != null){
                        mListener.onErrorMainThread(0,0);
                    }
//                    throw new IllegalStateException("cannot stop. Player in mState " + mState);
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< stop");
    }

    public void reset() {
        if (SHOW_LOGS) Logger.v(TAG, ">> reset , mState " + mState);

        synchronized (mState) {
            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case PREPARED:
                case PREPARING:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case ERROR:
                    mMediaPlayer.reset();
                    mState.set(State.IDLE);
                    break;
                case END:
                    Logger.v(TAG, ">> video  end");
//                    throw new IllegalStateException("cannot call reset from state " + mState.get());
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< reset , mState " + mState);
    }

    public void release() {
        if (SHOW_LOGS) Logger.v(TAG, ">> release, mState " + mState);
        synchronized (mState) {
            if(mMediaPlayer != null) {
                mMediaPlayer.release();
            }
            mState.set(State.END);
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< release, mState " + mState);
    }

    public void clearAll() {
        if (SHOW_LOGS) Logger.v(TAG, ">> clearAll, mState " + mState);
        synchronized (mState) {
            if(mMediaPlayer != null) {
                mMediaPlayer.setOnVideoSizeChangedListener(null);
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.setOnErrorListener(null);
                mMediaPlayer.setOnBufferingUpdateListener(null);
                mMediaPlayer.setOnInfoListener(null);
                mMediaPlayer.setOnSeekCompleteListener(null);
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< clearAll, mState " + mState);
    }

    public void close(){
        clearAll();
        release();
        mMediaPlayer = null;

        if(mMainThreadHandler != null){
            mMainThreadHandler.removeCallbacksAndMessages(null);
            mMainThreadHandler = null;
        }
    }

    public void setLooping(boolean looping) {
        if (SHOW_LOGS) Logger.v(TAG, "setLooping " + looping);
        mMediaPlayer.setLooping(looping);
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (SHOW_LOGS) Logger.v(TAG, ">> setSurfaceTexture " + surfaceTexture);
        if (SHOW_LOGS) Logger.v(TAG, "setSurfaceTexture mSurface " + mSurface);


        if(surfaceTexture != null){
            mSurface = new Surface(surfaceTexture);
            mMediaPlayer.setSurface(mSurface); // TODO fix illegal state exception
        } else {
            mMediaPlayer.setSurface(null);
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< setSurfaceTexture " + surfaceTexture);

    }

    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    public int getCurrentPosition() {
        if(mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public boolean isReadyForPlayback() {
        boolean isReadyForPlayback = false;
        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "isReadyForPlayback, mState " + mState);
            State state = mState.get();

            switch (state) {
                case IDLE:
                case INITIALIZED:
                case ERROR:
                case PREPARING:
                case STOPPED:
                case END:
                    isReadyForPlayback = false;
                    break;
                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                    isReadyForPlayback = true;
                    break;
            }

        }
        return isReadyForPlayback;
    }

    public int getDuration() {
        int duration = 0;
        synchronized (mState) {
            switch (mState.get()) {
                case END:
                case IDLE:
                case INITIALIZED:
                case PREPARING:
                case ERROR:
                    duration = 0;

                    break;
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                    duration = mMediaPlayer.getDuration();
            }
        }
        return duration;
    }

    public void seekToPercent(int percent) {
        synchronized (mState) {
            State state = mState.get();

            if (SHOW_LOGS) Logger.v(TAG, "seekToPercent, percent " + percent + ", mState " + state);

            switch (state) {
                case IDLE:
                case INITIALIZED:
                case ERROR:
                case PREPARING:
                case END:
                case STOPPED:
                    if (SHOW_LOGS) Logger.w(TAG, "seekToPercent, illegal state");
                    break;

                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
//                    int positionMillis = (int) ((float) percent / 100f * getDuration());

                default:
                    mMediaPlayer.seekTo(percent);
                    notifyPositionUpdated();
                    break;

            }
        }
    }

    private final Runnable mNotifyPositionUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            notifyPositionUpdated();
        }
    };

    private void startPositionUpdateNotifier() {
        if (SHOW_LOGS)
            Logger.v(TAG, "startPositionUpdateNotifier, mPositionUpdateNotifier " + mPositionUpdateNotifier);
            mFuture = mPositionUpdateNotifier.scheduleAtFixedRate(
                    mNotifyPositionUpdateRunnable,
                    0,
                    POSITION_UPDATE_NOTIFYING_PERIOD,
                    TimeUnit.MILLISECONDS);
    }

    private void stopPositionUpdateNotifier() {
        if (SHOW_LOGS)
            Logger.v(TAG, "stopPositionUpdateNotifier, mPositionUpdateNotifier " + mPositionUpdateNotifier);

        mFuture.cancel(true);
        mFuture = null;
    }

    private void notifyPositionUpdated() {
        synchronized (mState) { //todo: remove
//            if (SHOW_LOGS) Logger.v(TAG, "notifyPositionUpdated, mVideoStateListener " + mVideoStateListener);

            if (mVideoStateListener != null && mState.get() == State.STARTED) {
                mVideoStateListener.onVideoPlayTimeChanged(mMediaPlayer.getCurrentPosition());
            }
        }
    }

    public State getCurrentState() {
        synchronized (mState){
            return mState.get();
        }
    }

    public void setCurrentState(State state){
        synchronized (mState){
            mState.set( state);
        }
    }

    public static int positionToPercent(int progressMillis, int durationMillis) {
        float percentPrecise = (float) progressMillis / (float) durationMillis * 100f;
        return Math.round(percentPrecise);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode();
    }

    public interface MainThreadMediaPlayerListener {
        void onVideoSizeChangedMainThread(int width, int height);

        void onVideoPreparedMainThread();

        void onVideoStartMainThread();

        void onVideoPauseMainThread();

        void onVideoCompletionMainThread();

        void onErrorMainThread(int what, int extra);

        void onBufferingUpdateMainThread(int percent);

        void onVideoStoppedMainThread();

        void onVideoSeekComplete();
    }

    public interface VideoStateListener {
        void onVideoPlayTimeChanged(int positionInMilliseconds);
    }

    private boolean inUiThread() {
        return Thread.currentThread().getId() == 1;
    }
}
