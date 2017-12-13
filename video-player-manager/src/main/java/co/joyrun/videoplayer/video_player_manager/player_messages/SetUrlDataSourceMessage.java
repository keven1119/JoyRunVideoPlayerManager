package co.joyrun.videoplayer.video_player_manager.player_messages;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManagerCallback;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

/**
 * This PlayerMessage calls {@link MediaPlayer#setDataSource(Context, Uri)} on the instance that is used inside {@link VideoInterfaceV2}
 */
public class SetUrlDataSourceMessage extends SetDataSourceMessage{

    private final String mVideoUrl;

    public SetUrlDataSourceMessage(VideoInterfaceV2 videoPlayerView, String videoUrl, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mVideoUrl = videoUrl;
    }

    @Override
    protected void performAction(VideoInterfaceV2 currentPlayer) {
        currentPlayer.setDataSource(mVideoUrl);
    }
}
