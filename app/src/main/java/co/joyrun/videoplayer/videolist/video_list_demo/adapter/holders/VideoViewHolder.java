package co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.joyrun.videoplayer.video_player_manager.widget.VideoPlayer;
import co.joyrun.videoplayer.videolist.R;


public class VideoViewHolder extends RecyclerView.ViewHolder{

//    public final JoyRunVideoPlayer mPlayer;
    public final VideoPlayer mPlayer;
//    public final VideoPlayerView mPlayer;
    public final TextView mTitle;
//    public final ImageView mCover;
    public final TextView mVisibilityPercents;

    public VideoViewHolder(View view) {
        super(view);
//        mPlayer = (JoyRunVideoPlayer) view.findViewById(R.id.player);
        mPlayer = (VideoPlayer) view.findViewById(R.id.player);
//        mPlayer = (VideoPlayerView) view.findViewById(R.id.player);
        mTitle = (TextView) view.findViewById(R.id.title);
//        mCover = (ImageView) view.findViewById(R.id.cover);
        mVisibilityPercents = (TextView) view.findViewById(R.id.visibility_percents);
    }
}