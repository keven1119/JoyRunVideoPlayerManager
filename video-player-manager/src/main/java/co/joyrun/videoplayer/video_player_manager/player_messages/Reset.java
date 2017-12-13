package co.joyrun.videoplayer.video_player_manager.player_messages;

import android.media.MediaPlayer;

import co.joyrun.videoplayer.video_player_manager.PlayerMessageState;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManagerCallback;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

/**
 * This PlayerMessage calls {@link MediaPlayer#reset()} on the instance that is used inside {@link VideoInterfaceV2}
 */
public class Reset extends PlayerMessage {
    public Reset(VideoInterfaceV2 videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected void performAction(VideoInterfaceV2 currentPlayer) {
        currentPlayer.reset();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.RESETTING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.RESET;
    }
}
