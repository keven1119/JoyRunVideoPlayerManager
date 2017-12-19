package co.joyrun.videoplayer.videolist.video_player_manager_demo.fragments;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import co.joyrun.videoplayer.video_player_manager.manager.PlayerItemChangeListener;
import co.joyrun.videoplayer.video_player_manager.manager.SingleVideoPlayerManager;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManager;
import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import co.joyrun.videoplayer.video_player_manager.widget.ExpendableVideoPlayerView;
import co.joyrun.videoplayer.videolist.R;

import java.io.IOException;

/**
 * Created by danylo.volokh on 1/10/2016.
 */
public class VideoPlayerManagerFragment extends Fragment {

    private ExpendableVideoPlayerView mExpendableVideoPlayer_View_1;
    private ExpendableVideoPlayerView mExpendableVideoPlayer_View_2;

    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });
    private AssetFileDescriptor mVideoFileDecriptor_sample_1;
    private AssetFileDescriptor mVideoFileDecriptor_sample_2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.video_player_manager_fragment, container, false);

        try {

            mVideoFileDecriptor_sample_1 = getActivity().getAssets().openFd("video_sample_1.mp4");
            mVideoFileDecriptor_sample_2 = getActivity().getAssets().openFd("video_sample_2.mp4");

        } catch (IOException e) {
            e.printStackTrace();
        }

        mExpendableVideoPlayer_View_1 = (ExpendableVideoPlayerView)root.findViewById(R.id.video_player_1);
        mExpendableVideoPlayer_View_1.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
            @Override
            public void onVideoPreparedMainThread() {
                // We hide the cover when video is prepared. Playback is about to start
//                mVideoCover.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVideoStoppedMainThread() {
                // We show the cover when video is stopped
//                mVideoCover.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoCompletionMainThread() {
                // We show the cover when video is completed
//                mVideoCover.setVisibility(View.VISIBLE);
            }
        });
//        mVideoCover = (ImageView)root.findViewById(R.id.video_cover_1);
//        mVideoCover.setOnClickListener(this);

        mExpendableVideoPlayer_View_2 = (ExpendableVideoPlayerView) root.findViewById(R.id.video_player_2);
        mExpendableVideoPlayer_View_2.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
            @Override
            public void onVideoPreparedMainThread() {
                // We hide the cover when video is prepared. Playback is about to start
//                mVideoCover2.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVideoStoppedMainThread() {
                // We show the cover when video is stopped
//                mVideoCover2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoCompletionMainThread() {
                // We show the cover when video is completed
//                mVideoCover2.setVisibility(View.VISIBLE);
            }
        });
//        mVideoCover2 = (ImageView)root.findViewById(R.id.video_cover_2);
//        mVideoCover2.setOnClickListener(this);
//
//        Picasso.with(getActivity()).load(R.drawable.video_sample_1_pic).into(mVideoCover);
//        Picasso.with(getActivity()).load(R.drawable.video_sample_2_pic).into(mVideoCover2);
        mVideoPlayerManager.setAutoPlay(true);
        mVideoPlayerManager.setPrePrepare(false);
        mVideoPlayerManager.playNewVideo(null, mExpendableVideoPlayer_View_1, mVideoFileDecriptor_sample_1);
//        mVideoPlayerManager.playNewVideo(null, mExpendableVideoPlayer_View_2, mVideoFileDecriptor_sample_2);

        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        // in case we exited screen in playback
//        mVideoCover.setVisibility(View.VISIBLE);
//        mVideoCover2.setVisibility(View.VISIBLE);

        mVideoPlayerManager.stopAnyPlayback();
    }
}
