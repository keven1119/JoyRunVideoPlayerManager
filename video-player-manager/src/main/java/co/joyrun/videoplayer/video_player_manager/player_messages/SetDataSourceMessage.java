package co.joyrun.videoplayer.video_player_manager.player_messages;

import co.joyrun.videoplayer.video_player_manager.PlayerMessageState;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManagerCallback;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

/**
 * This is generic PlayerMessage for setDataSource
 */
public abstract class SetDataSourceMessage extends PlayerMessage{

    public SetDataSourceMessage(VideoInterfaceV2 videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_DATA_SOURCE;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.DATA_SOURCE_SET;
    }
}
