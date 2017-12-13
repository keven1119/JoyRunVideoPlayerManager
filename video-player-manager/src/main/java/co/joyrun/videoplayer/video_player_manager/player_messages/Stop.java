package co.joyrun.videoplayer.video_player_manager.player_messages;

import android.media.MediaPlayer;

import co.joyrun.videoplayer.video_player_manager.PlayerMessageState;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManagerCallback;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;


/**
 * This PlayerMessage calls {@link MediaPlayer#stop()} on the instance that is used inside {@link VideoInterfaceV2}
 */
public class Stop extends PlayerMessage {
    public Stop(VideoInterfaceV2 videoView, VideoPlayerManagerCallback callback) {
        super(videoView, callback);
    }

    @Override
    protected void performAction(VideoInterfaceV2 currentPlayer) {
        currentPlayer.stop();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.STOPPING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.STOPPED;
    }
}
