package co.joyrun.videoplayer.video_player_manager.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.joyrun.videoplayer.video_player_manager.R;

import co.joyrun.videoplayer.video_player_manager.ui.MediaPlayerWrapper;
import co.joyrun.videoplayer.video_player_manager.ui.VideoPlayerView;


/**
 * Created by keven-liang on 2017/12/4.
 */

public class JoyRunVideoPlayer extends RelativeLayout implements
//        VideoInterface,
        VideoInterfaceV2 {

    private VideoPlayer mVideoPlayer;
    private SpaceLandDialog mSpaceLandDialog;

    public JoyRunVideoPlayer(@NonNull Context context) {
        this(context, null ,0);
    }

    public JoyRunVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JoyRunVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mVideoPlayer = new VideoPlayer(context);
        setGravity(CENTER_IN_PARENT);
        addView(mVideoPlayer);
        mVideoPlayer.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        mVideoPlayer.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
    }

//    @Override
//    public void init() {
//        mVideoPlayer.init();
//    }
//
////    @Override
////    public void setVideoPath(String path) {
////        mVideoPlayer.setVideoPath(path);
////    }
////
////    @Override
////    public String getVideoPath() {
////        return mVideoPlayer.getVideoPath();
////    }
////
////    @Override
////    public void setVideoUrl(String videoUrl) {
////        mVideoPlayer.setVideoUrl(videoUrl);
////    }
////
////    @Override
////    public String getVideoUrl() {
////        return mVideoPlayer.getVideoUrl();
////    }
//
//    @Override
//    public void setCover(String url) {
//        mVideoPlayer.setCover(url);
//    }
//
//    @Override
//    public String getCoverPath() {
//        return mVideoPlayer.getCoverPath();
//    }

    @Override
    public boolean start() {
        return mVideoPlayer.start();
    }

    @Override
    public void setDataSource(String path) {
        mVideoPlayer.setDataSource(path);
    }

    @Override
    public void setDataSource(AssetFileDescriptor assetFileDescriptor) {
        mVideoPlayer.setDataSource(assetFileDescriptor);
    }

    @Override
    public void setOnVideoStateChangedListener(MediaPlayerWrapper.VideoStateListener listener) {
        mVideoPlayer.setOnVideoStateChangedListener(listener);
    }

    @Override
    public void addMediaPlayerListener(MediaPlayerWrapper.MainThreadMediaPlayerListener listener) {
        mVideoPlayer.addMediaPlayerListener(listener);
    }

    @Override
    public void setBackgroundThreadMediaPlayerListener(VideoPlayerView.BackgroundThreadMediaPlayerListener listener) {
        mVideoPlayer.setBackgroundThreadMediaPlayerListener(listener);
    }

    @Override
    public void muteVideo() {
        mVideoPlayer.muteVideo();
    }

    @Override
    public void unMuteVideo() {
        mVideoPlayer.unMuteVideo();
    }

    @Override
    public boolean isAllVideoMute() {
        return mVideoPlayer.isAllVideoMute();
    }

    @Override
    public void pause() {
        mVideoPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mVideoPlayer.getDuration();
    }

    @Override
    public void seekTo(int progress) {
        mVideoPlayer.seekTo(progress);
    }

    @Override
    public int getCurrentPrecent() {
        return mVideoPlayer.getCurrentPrecent();
    }

    @Override
    public MediaPlayerWrapper.State getCurrentState() {
        return mVideoPlayer.getCurrentState();
    }

    @Override
    public String getVideoUrlDataSource() {
        return getVideoUrlDataSource();
    }

    @Override
    public AssetFileDescriptor getAssetFileDescriptorDataSource() {
        return getAssetFileDescriptorDataSource();
    }

//    @Override
//    public void close() {
//        mVideoPlayer.close();
//        if(mSpaceLandDialog != null){
//            mSpaceLandDialog.cancel();
//            mSpaceLandDialog.dismiss();
//        }
//    }

//    @Override
    public void onChanged(Configuration newConfig) {
        mVideoPlayer.onChanged(newConfig);
        boolean isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;

        if(mSpaceLandDialog == null){
            mSpaceLandDialog = new SpaceLandDialog(getContext());
        }

        if(isPortrait){
            mSpaceLandDialog.dismiss();
        }else {
            mSpaceLandDialog.show();
        }

    }

//    @Override
    public boolean isPortrait() {
        return mVideoPlayer.isPortrait();
    }

//    @Override
    public void setStartVideoListener(VideoPlayer.OnStartVideoListener listener) {
        mVideoPlayer.setStartVideoListener(listener);
    }


    @Override
    public void reset() {
        mVideoPlayer.reset();
    }

    @Override
    public void release() {
        mVideoPlayer.release();
    }

    @Override
    public void clearPlayerInstance() {
        mVideoPlayer.clearPlayerInstance();

        if(mSpaceLandDialog != null){
            mSpaceLandDialog.dismiss();
            mSpaceLandDialog = null;
        }
    }

    @Override
    public void createNewPlayerInstance() {
        mVideoPlayer.createNewPlayerInstance();
        if(mSpaceLandDialog != null){
            mSpaceLandDialog.dismiss();
            mSpaceLandDialog = null;
        }
    }

    @Override
    public void prepare() {
        mVideoPlayer.prepare();
    }

    @Override
    public void stop() {
        mVideoPlayer.stop();
    }

    class SpaceLandDialog extends Dialog {

        private FrameLayout dialogContainer;

        public SpaceLandDialog(@NonNull Context context) {
            super(context, R.style.Dialog_Fullscreen);
            setContentView(R.layout.landspace_dialog_layout);
            getWindow().setWindowAnimations(R.style.shareDialogWindowAnim);

            dialogContainer = findViewById(R.id.framelayout_landspace_dialog);
        }

        @Override
        public void show() {
            super.show();
            JoyRunVideoPlayer.this.removeView(mVideoPlayer);
            dialogContainer.addView(mVideoPlayer);
        }

        @Override
        public void onBackPressed() {

            if(isPortrait()){
                super.onBackPressed();
            }else {
                mVideoPlayer.request2Portrait();
            }
        }

        @Override
        public void dismiss() {
            super.dismiss();
            dialogContainer.removeView(mVideoPlayer);
           JoyRunVideoPlayer.this.addView(mVideoPlayer);
        }

    }
}
