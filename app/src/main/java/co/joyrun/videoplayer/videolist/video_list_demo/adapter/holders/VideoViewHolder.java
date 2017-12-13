package co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import co.joyrun.videoplayer.video_player_manager.widget.ExpendableVideoPlayerView;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;
import co.joyrun.videoplayer.videolist.R;


public class VideoViewHolder extends RecyclerView.ViewHolder {

//    public final JoyRunVideoPlayer mPlayer;
    public VideoInterfaceV2 mPlayer;
//    public final VideoPlayerView mPlayer;


    public VideoViewHolder(View view) {
        super(view);
        Type type = getClass().getGenericSuperclass();

        // 判断 是否泛型
        if (type instanceof ParameterizedType) {
            // 返回表示此类型实际类型参数的Type对象的数组.
            // 当有多个泛型类时，数组的长度就不是1了
            Type[] ptype = ((ParameterizedType) type).getActualTypeArguments();

            if (ptype.length != 1) {
                throw new RuntimeException("this construction only can set param View");
            }
        }

        mPlayer = view.findViewById(R.id.player);
    }

    public void setPlayer(VideoInterfaceV2 player) {
        this.mPlayer = player;
    }

    public VideoInterfaceV2 getPlayer() {
        return mPlayer;
    }
}
