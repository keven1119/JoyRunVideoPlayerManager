package co.joyrun.videoplayer.videolist.video_list_demo.adapter.items;

import co.joyrun.videoplayer.video_player_manager.manager.VideoItem;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManager;
import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.MyVideoHolder;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.VideoViewHolder;

/**
 * Created by keven-liang on 2017/12/8.
 */

public class CustomerItem extends BaseVideoItem<MyVideoHolder> {

    private MyVideoItem myVideoItem;


    public CustomerItem(MyVideoItem myVideoItem, VideoPlayerManager videoPlayerManager){
        super(videoPlayerManager);
        this.myVideoItem = myVideoItem;
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoInterfaceV2 player, VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData,player,myVideoItem.getVideoUrl());
    }

    @Override
    public void stopPlayback(VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.stopAnyPlayback();
    }

    public void update(int position, MyVideoHolder holder, VideoPlayerManager videoPlayerManager) {
        holder.mPlayer.setCover(myVideoItem.getCoverUrl());
    }
    static public class MyVideoItem{
        private String  coverUrl;
        private String  videoUrl;

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }
}
