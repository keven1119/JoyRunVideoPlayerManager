package co.joyrun.videoplayer.video_player_manager;

import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManagerCallback;
import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.player_messages.PlayerMessage;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

public class SetNewViewForPlayback extends PlayerMessage {

    private final MetaData mCurrentItemMetaData;
    private final VideoInterfaceV2 mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;

    public SetNewViewForPlayback(MetaData currentItemMetaData, VideoInterfaceV2 videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mCurrentItemMetaData = currentItemMetaData;
        mCurrentPlayer = videoPlayerView;
        mCallback = callback;
    }

    @Override
    public String toString() {
        return SetNewViewForPlayback.class.getSimpleName() + ", mCurrentPlayer " + mCurrentPlayer;
    }

    @Override
    protected void performAction(VideoInterfaceV2 currentPlayer) {
        mCallback.setCurrentItem(mCurrentItemMetaData, mCurrentPlayer);
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_NEW_PLAYER;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.IDLE;
    }
}
