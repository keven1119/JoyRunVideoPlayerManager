package co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;
import co.joyrun.videoplayer.videolist.R;

/**
 * Created by keven-liang on 2017/12/13.
 */

public class MyVideoHolderV2 extends RecyclerView.ViewHolder {

    public VideoInterfaceV2 mPlayer;

    public  TextView mTitle;
    //    public final ImageView mCover;
    public  TextView mVisibilityPercents;


    public MyVideoHolderV2(View view) {
        super(view);
        mPlayer = view.findViewById(R.id.player);

        mTitle = view.findViewById(R.id.title);
        mVisibilityPercents = view.findViewById(R.id.visibility_percents);
    }

}
