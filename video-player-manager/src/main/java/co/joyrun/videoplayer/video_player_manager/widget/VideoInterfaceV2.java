package co.joyrun.videoplayer.video_player_manager.widget;

import android.content.res.AssetFileDescriptor;

import co.joyrun.videoplayer.video_player_manager.ui.MediaPlayerWrapper;
import co.joyrun.videoplayer.video_player_manager.ui.VideoPlayerView;

/**
 * Created by keven-liang on 2017/12/6.
 */

public interface VideoInterfaceV2 {

    public void reset();

    public void release();

    public void clearPlayerInstance();

    public void createNewPlayerInstance();

    public void prepare();

    public void stop();

    public boolean start();

    public void setDataSource(String path);

    public void setDataSource(AssetFileDescriptor assetFileDescriptor);

    public void setOnVideoStateChangedListener(MediaPlayerWrapper.VideoStateListener listener);

    public void addMediaPlayerListener(MediaPlayerWrapper.MainThreadMediaPlayerListener listener);

    public void setBackgroundThreadMediaPlayerListener(VideoPlayerView.BackgroundThreadMediaPlayerListener listener);

    public void muteVideo();

    public void unMuteVideo();

    public boolean isAllVideoMute();

    public void pause();

    public int getDuration();

    public void seekTo(int progress);

    public int getCurrentPrecent();

    public MediaPlayerWrapper.State getCurrentState();

    public String getVideoUrlDataSource();

    public AssetFileDescriptor getAssetFileDescriptorDataSource();
}
