package co.joyrun.videoplayer.videolist.video_list_demo.adapter.items;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManager;
import co.joyrun.videoplayer.video_player_manager.ui.MediaPlayerWrapper;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.MyVideoHolderV2;
import co.joyrun.videoplayer.visibility_utils.items.ListItem;

/**
 * Created by keven-liang on 2017/12/21.
 */

public class CustomerItemV2 implements ListItem {

    private VideoPlayerManager mVideoPlayerManager;
    private CustomerItem.MyVideoItem myVideoItem;

    public CustomerItemV2(VideoPlayerManager videoPlayerManager,CustomerItem.MyVideoItem myVideoItem){
        mVideoPlayerManager = videoPlayerManager;
        this.myVideoItem = myVideoItem;
    }

    public CustomerItem.MyVideoItem getMyVideoItem() {
        return myVideoItem;
    }

    public void setMyVideoItem(CustomerItem.MyVideoItem myVideoItem) {
        this.myVideoItem = myVideoItem;
    }

    @Override
    public int getVisibilityPercents(View view) {
        return 0;
    }

    public static View createView(ViewGroup parent, @LayoutRes int layoutId, Class holderClass, int screenWidth) throws NoSuchMethodException,IllegalAccessException,
            InstantiationException,InvocationTargetException {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = screenWidth;

        //创建Viewholder实例
        Class[] parameterTypes = {View.class};
        Constructor constructor = holderClass.getConstructor(parameterTypes);
        Object[] paramters = {view};
        Object o = constructor.newInstance(paramters);
        view.setTag(o);
        return view;
    }

    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {
        MyVideoHolderV2 videoHolderV2 = (MyVideoHolderV2) newActiveView.getTag();
        if(myVideoItem != null){
            if(videoHolderV2.mPlayer.getCurrentState() == MediaPlayerWrapper.State.IDLE)
            mVideoPlayerManager.playNewVideo(null,videoHolderV2.mPlayer,myVideoItem.getVideoUrl());
            videoHolderV2.mPlayer.setCover(myVideoItem.getCoverUrl());

        }
    }

    @Override
    public void deactivate(View currentView, int position) {
        mVideoPlayerManager.stopAnyPlayback();
    }
}
