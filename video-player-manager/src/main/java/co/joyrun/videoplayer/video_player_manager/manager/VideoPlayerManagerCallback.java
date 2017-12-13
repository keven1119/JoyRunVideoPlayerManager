package co.joyrun.videoplayer.video_player_manager.manager;

import co.joyrun.videoplayer.video_player_manager.PlayerMessageState;
import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;
import co.joyrun.videoplayer.video_player_manager.player_messages.PlayerMessage;
//import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

/**
 * This callback is used by {@link PlayerMessage}
 * to get and set data it needs
 */
public interface VideoPlayerManagerCallback {

    void setCurrentItem(MetaData currentItemMetaData, VideoInterfaceV2 newPlayerView);

    void setVideoPlayerState(VideoInterfaceV2 videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
