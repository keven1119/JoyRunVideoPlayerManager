package co.joyrun.videoplayer.video_player_manager.player_messages;

import co.joyrun.videoplayer.video_player_manager.PlayerMessageState;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManagerCallback;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

/**
 * This PlayerMessage creates new MediaPlayer instance that will be used inside {@link VideoInterfaceV2}
 */
public class CreateNewPlayerInstance extends PlayerMessage {
    private boolean isAutoPlay = false;

    public CreateNewPlayerInstance(VideoInterfaceV2 videoPlayerView, VideoPlayerManagerCallback callback, boolean isAutoPlay) {
        super(videoPlayerView, callback);
        this.isAutoPlay = isAutoPlay;
    }

    @Override
    protected void performAction(VideoInterfaceV2 currentPlayer) {
        currentPlayer.createNewPlayerInstance(isAutoPlay);
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.CREATING_PLAYER_INSTANCE;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.PLAYER_INSTANCE_CREATED;
    }
}
