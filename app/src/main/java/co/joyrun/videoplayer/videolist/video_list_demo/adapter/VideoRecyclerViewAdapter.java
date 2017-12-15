package co.joyrun.videoplayer.videolist.video_list_demo.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.w3c.dom.Text;

import co.joyrun.videoplayer.video_player_manager.ui.MediaPlayerWrapper;
import co.joyrun.videoplayer.video_player_manager.utils.Logger;
import co.joyrun.videoplayer.videolist.BuildConfig;
import co.joyrun.videoplayer.videolist.R;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.MyVideoHolder;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.TextHolder;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.ViewHolder;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.items.BaseVideoItem;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManager;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.items.CustomerItem;
import co.joyrun.videoplayer.visibility_utils.items.ListItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by danylo.volokh on 9/20/2015.
 */
public class VideoRecyclerViewAdapter extends RecyclerView.Adapter {

    private final static String TAG = VideoRecyclerViewAdapter.class.getName();

    private  VideoPlayerManager mVideoPlayerManager;
    private  List mList;
    private  Context mContext;

    public VideoRecyclerViewAdapter(VideoPlayerManager videoPlayerManager, Context context, List<ListItem> list){
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {


        if(type == CUSTOMER) {
            View view = null;
            try {
                view = CustomerItem.createView(viewGroup, R.layout.video_item, MyVideoHolder.class, mContext.getResources().getDisplayMetrics().widthPixels);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            if(view != null){
                return (RecyclerView.ViewHolder) view.getTag();
            }else {
                return null;
            }


        }else if(type == TEXT){

            View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_item, viewGroup, false);
            return new TextHolder(inflate);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (BuildConfig.DEBUG) Logger.v(TAG, "onBindViewHolder   position ==> "+ position);

        if (viewHolder instanceof MyVideoHolder) {
            BaseVideoItem videoItem =(BaseVideoItem) mList.get(position);
            videoItem.update(position, (MyVideoHolder)viewHolder, mVideoPlayerManager);
        }else if(viewHolder instanceof TextHolder){
            ((TextHolder)viewHolder).mTextView.setText("hahahahahhahhahahahhahahah");
        }
    }

    private final static int CUSTOMER = 1;
    private final static int TEXT = 2;

    @Override
    public int getItemViewType(int position) {

        Object o = mList.get(position);

        if(o instanceof CustomerItem){
            return CUSTOMER;
        }else if(o instanceof String){
            return TEXT;
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
//        return 1;
    }

}
