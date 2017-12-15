package co.joyrun.videoplayer.videolist.video_list_demo.adapter.items;

import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import co.joyrun.videoplayer.video_player_manager.manager.VideoItem;
import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.ui.MediaPlayerWrapper;
import co.joyrun.videoplayer.video_player_manager.utils.Logger;
import co.joyrun.videoplayer.video_player_manager.widget.VideoInterfaceV2;
import co.joyrun.videoplayer.videolist.R;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.MyVideoHolder;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.holders.VideoViewHolder;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManager;
import co.joyrun.videoplayer.video_player_manager.meta.CurrentItemMetaData;
import co.joyrun.videoplayer.visibility_utils.items.ListItem;

public abstract class BaseVideoItem<T extends VideoViewHolder> implements VideoItem, ListItem{

    private static final boolean SHOW_LOGS = false;
    private static final String TAG = BaseVideoItem.class.getSimpleName();
    private Class<T> clz;

    /**
     * An object that is filled with values when {@link #getVisibilityPercents} method is called.
     * This object is local visible rect filled by {@link android.view.View#getLocalVisibleRect}
     */

    private final Rect mCurrentViewRect = new Rect();
    private final VideoPlayerManager<MetaData> mVideoPlayerManager;

    protected BaseVideoItem(VideoPlayerManager<MetaData>  videoPlayerManager) {
        mVideoPlayerManager = videoPlayerManager;
        if (clz == null) {
            //获取泛型的Class对象
            clz = ((Class<T>)
                    (((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]));
        }

    }

    /**
     * This method needs to be called when created/recycled view is updated.
     * Call it in
     * 1. {@link android.widget.ListAdapter#getView(int, View, ViewGroup)}
     * 2. {@link android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     */
    public abstract void update(int position, T view, VideoPlayerManager videoPlayerManager);

    /**
     * When this item becomes active we start playback on the video in this item
     */
    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {
        T viewHolder = (T) newActiveView.getTag();
        if(viewHolder != null) {
            VideoInterfaceV2 player = ((T) viewHolder).getPlayer();
            if (player != null) {
                playNewVideo(new CurrentItemMetaData(newActiveViewPosition, newActiveView), ((T) viewHolder).getPlayer(), mVideoPlayerManager);
            }
        }
    }

    /**
     * When this item becomes inactive we stop playback on the video in this item.
     */
    @Override
    public void deactivate(View currentView, int position) {
        stopPlayback(mVideoPlayerManager);
    }



    /**
     * This method calculates visibility percentage of currentView.
     * This method works correctly when currentView is smaller then it's enclosure.
     * @param currentView - view which visibility should be calculated
     * @return currentView visibility percents
     */
    @Override
    public int getVisibilityPercents(View currentView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> getVisibilityPercents currentView " + currentView);

        int percents = 100;

        currentView.getLocalVisibleRect(mCurrentViewRect);
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents mCurrentViewRect top " + mCurrentViewRect.top + ", left " + mCurrentViewRect.left + ", bottom " + mCurrentViewRect.bottom + ", right " + mCurrentViewRect.right);

        int height = currentView.getHeight();
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents height " + height);

        if(viewIsPartiallyHiddenTop()){
            // view is partially hidden behind the top edge
            percents = (height - mCurrentViewRect.top) * 100 / height;
        } else if(viewIsPartiallyHiddenBottom(height)){
            percents = mCurrentViewRect.bottom * 100 / height;
        }

        setVisibilityPercentsText(currentView, percents);
        if(SHOW_LOGS) Logger.v(TAG, "<< getVisibilityPercents, percents " + percents);

        return percents;
    }

    private void setVisibilityPercentsText(View currentView, int percents) {
        if(SHOW_LOGS) Logger.v(TAG, "setVisibilityPercentsText percents " + percents);
//        T videoViewHolder = (T) currentView.getTag();
//        String percentsText = "Visibility percents: " + String.valueOf(percents);
//        ((MyVideoHolder)videoViewHolder).mVisibilityPercents.setText(percentsText);
    }

    private boolean viewIsPartiallyHiddenBottom(int height) {
        return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
    }

    private boolean viewIsPartiallyHiddenTop() {
        return mCurrentViewRect.top > 0;
    }
}
