package co.joyrun.videoplayer.videolist.video_list_demo.adapter.items;

import com.squareup.picasso.Picasso;
import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.VideoViewHolder;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManager;

/**
 * Use this class if you have direct path to the video source
 */
public class DirectLinkVideoItem extends BaseVideoItem {

    private final String mDirectUrl;
    private final String mTitle;

    private final Picasso mImageLoader;
    private final int mImageResource;

    public DirectLinkVideoItem(String title, String directUr, VideoPlayerManager videoPlayerManager, Picasso imageLoader, int imageResource) {
        super(videoPlayerManager);
        mDirectUrl = directUr;
        mTitle = title;
        mImageLoader = imageLoader;
        mImageResource = imageResource;

    }

    @Override
    public void update(int position, VideoViewHolder viewHolder, VideoPlayerManager videoPlayerManager) {
        viewHolder.mTitle.setText(mTitle);
//        viewHolder.mCover.setVisibility(View.VISIBLE);
//        mImageLoader.load(mImageResource).into(viewHolder.mCover);
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoInterfaceV2 player, VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mDirectUrl);
    }

    @Override
    public void stopPlayback(VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.stopAnyPlayback();
    }
}
