package co.joyrun.videoplayer.video_player_manager.manager;

import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

/**
 * This is basic interface for Items in Adapter of the list. Regardless of is it {@link android.widget.ListView}
 * or {@link android.support.v7.widget.RecyclerView}
 */
public interface VideoItem {
    void playNewVideo(MetaData currentItemMetaData, VideoInterfaceV2 player, VideoPlayerManager<MetaData> videoPlayerManager);
    void stopPlayback(VideoPlayerManager videoPlayerManager);
}
