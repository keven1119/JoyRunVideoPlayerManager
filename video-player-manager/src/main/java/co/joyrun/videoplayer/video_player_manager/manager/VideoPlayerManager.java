package co.joyrun.videoplayer.video_player_manager.manager;

import android.content.res.AssetFileDescriptor;

import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

/**
 * This is a general interface for VideoPlayerManager
 * It supports :
 * 1. Start playback of new video by calling:
 *  a) {@link #playNewVideo(MetaData, VideoInterfaceV2, String)} if you have direct url or path to video source
 *  b) {@link #playNewVideo(MetaData, VideoInterfaceV2, AssetFileDescriptor)} is your video file is in assets directory
 * 2. Stop existing playback. {@link #stopAnyPlayback()}
 * 3. Reset Media Player if it's no longer needed. {@link #resetMediaPlayer()}
 */
public interface VideoPlayerManager<T extends MetaData> {

    /**
     * Call it if you have direct url or path to video source
     * @param metaData - optional Meta Data
     * @param videoPlayerView - the actual video player
     * @param videoUrl - the link to the video source
     */
    void playNewVideo(T metaData, VideoInterfaceV2 videoPlayerView, String videoUrl);

    /**
     * Call it if you have video source in assets directory
     * @param metaData - optional Meta Data
     * @param videoPlayerView - the actual video player
     * @param assetFileDescriptor -The asset descriptor of the video file
     */
    void playNewVideo(T metaData, VideoInterfaceV2 videoPlayerView, AssetFileDescriptor assetFileDescriptor);

    /**
     * Call it if you need to stop any playback that is currently playing
     */
    void stopAnyPlayback();

    /**
     * Call it if you no longer need the player
     */
    void resetMediaPlayer();

    /**
     * 自动播放
     * @return
     */
    public boolean isAutoPlay() ;

    /**
     * 自动播放
     */
    public void setAutoPlay(boolean autoPlay) ;


    /**
     * 是否预加载
     * @return
     */
    public boolean isPrePrepare();

    /**
     * 是否预加载
     */
    public void setPrePrepare(boolean prePrepare);


}
