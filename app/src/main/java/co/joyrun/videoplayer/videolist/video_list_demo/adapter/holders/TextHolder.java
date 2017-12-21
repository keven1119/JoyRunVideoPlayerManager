package co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.joyrun.videoplayer.videolist.R;

/**
 * Created by keven-liang on 2017/12/14.
 */

public class TextHolder extends RecyclerView.ViewHolder{

    public TextView mTextView ;


    public TextHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.text_item);
    }
}
