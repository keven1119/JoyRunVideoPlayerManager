package co.joyrun.videoplayer.videolist.video_list_demo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.squareup.picasso.Picasso;
import co.joyrun.videoplayer.video_player_manager.Config;
import co.joyrun.videoplayer.video_player_manager.manager.PlayerItemChangeListener;
import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.videolist.R;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.VideoRecyclerViewAdapter;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.items.BaseVideoItem;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.items.CustomerItem;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.items.DirectLinkVideoItem;
import co.joyrun.videoplayer.video_player_manager.manager.SingleVideoPlayerManager;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManager;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.items.ItemFactory;
import co.joyrun.videoplayer.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import co.joyrun.videoplayer.visibility_utils.calculator.ListItemsVisibilityCalculator;
import co.joyrun.videoplayer.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import co.joyrun.videoplayer.visibility_utils.items.ListItem;
import co.joyrun.videoplayer.visibility_utils.scroll_utils.ItemsPositionGetter;
import co.joyrun.videoplayer.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This fragment shows of how to use {@link VideoPlayerManager} with a RecyclerView.
 */
public class VideoRecyclerViewFragment extends Fragment {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = VideoRecyclerViewFragment.class.getSimpleName();

    private ArrayList mList = new ArrayList<>();

    /**
     * Only the one (most visible) view should be active (and playing).
     * To calculate visibility of views we use {@link SingleListViewItemActiveCalculator}
     */
    private SingleListViewItemActiveCalculator mVideoVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback());

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    /**
     * ItemsPositionGetter is used by {@link ListItemsVisibilityCalculator} for getting information about
     * items position in the RecyclerView and LayoutManager
     */
    private ItemsPositionGetter mItemsPositionGetter;

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CustomerItem.MyVideoItem myVideoItem = new CustomerItem.MyVideoItem();
        myVideoItem.setVideoUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        myVideoItem.setCoverUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512133829765&di=aa6cfb825d8c8ed1e401222190a811a2&imgtype=jpg&src=http%3A%2F%2Fimg4.imgtn.bdimg.com%2Fit%2Fu%3D2760457749%2C4161462131%26fm%3D214%26gp%3D0.jpg");
//
//
        mVideoPlayerManager.setPrePrepare(true);
        mVideoPlayerManager.setAutoPlay(true);

        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add("hahahhahhahaha");
        mList.add("hahahhahhahaha");
        mList.add("hahahhahhahaha");
        mList.add("hahahhahhahaha");
        mList.add("hahahhahhahaha");
        mList.add("hahahhahhahaha");
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add("hahahhahhahaha");
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add("hahahhahhahaha");
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add("hahahhahhahaha");
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add("hahahhahhahaha");
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add("hahahhahhahaha");
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add("hahahhahhahaha");
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));
        mList.add(new CustomerItem(myVideoItem, mVideoPlayerManager));

        View rootView = inflater.inflate(R.layout.fragment_video_recycler_view, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mVideoVisibilityCalculator.setListItems(mList);
        VideoRecyclerViewAdapter videoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideoPlayerManager, getActivity(), mList);

        mRecyclerView.setAdapter(videoRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()){

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){
                    mVideoVisibilityCalculator.onScroll(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });
        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){
            // need to call this method from list view handler in order to have filled list

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mList != null){
            mList.clear();
            mList = null;
        }
    }
}