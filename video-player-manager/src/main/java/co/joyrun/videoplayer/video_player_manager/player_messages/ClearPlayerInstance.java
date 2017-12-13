package co.joyrun.videoplayer.video_player_manager.player_messages;

import co.joyrun.videoplayer.video_player_manager.PlayerMessageState;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManagerCallback;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

/**
 * This PlayerMessage clears MediaPlayer instance that was used inside {@link VideoInterfaceV2}
 */
public class ClearPlayerInstance extends PlayerMessage {

    public ClearPlayerInstance(VideoInterfaceV2 videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected void performAction(VideoInterfaceV2 currentPlayer) {
        currentPlayer.clearPlayerInstance();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.CLEARING_PLAYER_INSTANCE;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.PLAYER_INSTANCE_CLEARED;
    }
}
