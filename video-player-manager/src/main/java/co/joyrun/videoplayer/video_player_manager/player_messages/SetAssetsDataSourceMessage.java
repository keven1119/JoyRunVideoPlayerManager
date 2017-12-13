package co.joyrun.videoplayer.video_player_manager.player_messages;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManagerCallback;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;

import java.io.FileDescriptor;

/**
 * This PlayerMessage calls {@link MediaPlayer#setDataSource(FileDescriptor)} on the instance that is used inside {@link VideoInterfaceV2}
 */
public class SetAssetsDataSourceMessage extends SetDataSourceMessage{

    private final AssetFileDescriptor mAssetFileDescriptor;

    public SetAssetsDataSourceMessage(VideoInterfaceV2 videoPlayerView, AssetFileDescriptor assetFileDescriptor, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mAssetFileDescriptor = assetFileDescriptor;
    }

    @Override
    protected void performAction(VideoInterfaceV2 currentPlayer) {
        currentPlayer.setDataSource(mAssetFileDescriptor);
    }
}
