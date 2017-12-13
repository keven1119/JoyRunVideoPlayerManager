package co.joyrun.videoplayer.videolist.video_list_demo.adapter.items;

import co.joyrun.videoplayer.video_player_manager.manager.VideoItem;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManager;
import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.VideoViewHolder;

/**
 * Created by keven-liang on 2017/12/8.
 */

public class DirectLinkVideoUrlImgItem implements VideoItem {
    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoInterfaceV2 player, VideoPlayerManager<MetaData> videoPlayerManager) {

    }

    @Override
    public void stopPlayback(VideoPlayerManager videoPlayerManager) {

    }

    public void update(int position, VideoViewHolder view, VideoPlayerManager videoPlayerManager) {

    }
}
