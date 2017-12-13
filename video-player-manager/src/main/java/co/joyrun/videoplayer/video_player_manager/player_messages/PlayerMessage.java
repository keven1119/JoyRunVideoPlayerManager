package co.joyrun.videoplayer.video_player_manager.player_messages;

import co.joyrun.videoplayer.video_player_manager.Config;
import co.joyrun.videoplayer.video_player_manager.PlayerMessageState;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManagerCallback;
import co.joyrun.videoplayer.video_player_manager.utils.Logger;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

/**
 * This is generic interface for PlayerMessage
 */
public abstract class PlayerMessage implements Message {

    private static final String TAG = PlayerMessage.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private VideoInterfaceV2 mCurrentPlayer;
    private  VideoPlayerManagerCallback mCallback;

    public PlayerMessage(VideoInterfaceV2 currentPlayer, VideoPlayerManagerCallback callback) {
        mCurrentPlayer = currentPlayer;
        mCallback = callback;
    }

    protected final PlayerMessageState getCurrentState(){
        return mCallback.getCurrentPlayerState();
    }

    @Override
    public final void polledFromQueue() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateBefore());
    }

    @Override
    public final void messageFinished() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateAfter());
        mCurrentPlayer = null;
    }

    public final void runMessage(){
        if(SHOW_LOGS) Logger.v(TAG, ">> runMessage, " + getClass().getSimpleName());
        performAction(mCurrentPlayer);
        if(SHOW_LOGS) Logger.v(TAG, "<< runMessage, " + getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    protected abstract void performAction(VideoInterfaceV2 currentPlayer);
    protected abstract PlayerMessageState stateBefore();
    protected abstract PlayerMessageState stateAfter();

}
