package co.joyrun.videoplayer.videolist.video_list_demo.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;
import co.joyrun.videoplayer.video_player_manager.Config;
import co.joyrun.videoplayer.video_player_manager.manager.PlayerItemChangeListener;
import co.joyrun.videoplayer.video_player_manager.manager.SingleVideoPlayerManager;
import co.joyrun.videoplayer.video_player_manager.manager.VideoPlayerManager;
import co.joyrun.videoplayer.video_player_manager.meta.MetaData;
import co.joyrun.videoplayer.video_player_manager.utils.Logger;
import co.joyrun.videoplayer.videolist.R;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.VideoListViewAdapter;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.items.BaseVideoItem;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.items.DirectLinkVideoItem;
import co.joyrun.videoplayer.videolist.video_list_demo.adapter.items.ItemFactory;
import co.joyrun.videoplayer.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import co.joyrun.videoplayer.visibility_utils.calculator.ListItemsVisibilityCalculator;
import co.joyrun.videoplayer.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import co.joyrun.videoplayer.visibility_utils.scroll_utils.ItemsPositionGetter;
import co.joyrun.videoplayer.visibility_utils.scroll_utils.ListViewItemPositionGetter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This fragment shows of how to use {@link VideoPlayerManager} with a ListView.
 */
public class VideoListFragment extends Fragment {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = VideoListFragment.class.getSimpleName();

    private final ArrayList<BaseVideoItem> mList = new ArrayList<>();
    /**
     * Only the one (most visible) view should be active (and playing).
     * To calculate visibility of views we use {@link SingleListViewItemActiveCalculator}
     */
    private final SingleListViewItemActiveCalculator mListItemVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback());

    /**
     * ItemsPositionGetter is used by {@link ListItemsVisibilityCalculator} for getting information about
     * items position in the ListView
     */
    private ItemsPositionGetter mItemsPositionGetter;

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {
            if(SHOW_LOGS) Logger.v(TAG, "onPlayerItemChanged " + metaData);

        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // if your files are in "assets" directory you can pass AssetFileDescriptor to the VideoPlayerView
        // if they are url's or path values you can pass the String path to the VideoPlayerView
        try {
            mList.add(ItemFactory.createItemFromAsset("video_sample_1.mp4", R.drawable.video_sample_1_pic, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.drawable.video_sample_2_pic, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_3.mp4", R.drawable.video_sample_3_pic, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_4.mp4", R.drawable.video_sample_4_pic, getActivity(), mVideoPlayerManager));

            mList.add(ItemFactory.createItemFromAsset("video_sample_1.mp4", R.drawable.video_sample_1_pic, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.drawable.video_sample_2_pic, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_3.mp4", R.drawable.video_sample_3_pic, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_4.mp4", R.drawable.video_sample_4_pic, getActivity(), mVideoPlayerManager));

            mList.add(ItemFactory.createItemFromAsset("video_sample_1.mp4", R.drawable.video_sample_1_pic, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.drawable.video_sample_2_pic, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_3.mp4", R.drawable.video_sample_3_pic, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_4.mp4", R.drawable.video_sample_4_pic, getActivity(), mVideoPlayerManager));

            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_1_pic));
            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_2_pic));
            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_3_pic));
            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_4_pic));
            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_1_pic));
            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_2_pic));
            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_3_pic));
            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_4_pic));
            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_1_pic));
            mList.add(new DirectLinkVideoItem("hello", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",mVideoPlayerManager, Picasso.with(getActivity()),R.drawable.video_sample_2_pic));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        View rootView = inflater.inflate(R.layout.fragment_video_list_view, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListItemVisibilityCalculator.setListItems(mList);
        VideoListViewAdapter videoListViewAdapter = new VideoListViewAdapter(mVideoPlayerManager, getActivity(), mList);
        mListView.setAdapter(videoListViewAdapter);

        mItemsPositionGetter = new ListViewItemPositionGetter(mListView);
        /**
         * We need to set onScrollListener after we create {@link #mItemsPositionGetter}
         * because {@link android.widget.AbsListView.OnScrollListener#onScroll(AbsListView, int, int, int)}
         * is called immediately and we will get {@link NullPointerException}
         */
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;
                if (scrollState == SCROLL_STATE_IDLE && !mList.isEmpty()) {
                    mListItemVisibilityCalculator.onScrollStateIdle(mItemsPositionGetter, view.getFirstVisiblePosition(), view.getLastVisiblePosition());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!mList.isEmpty()) {
                    // on each scroll event we need to call onScroll for mListItemVisibilityCalculator
                    // in order to recalculate the items visibility
                    mListItemVisibilityCalculator.onScroll(mItemsPositionGetter, firstVisibleItem, visibleItemCount, mScrollState);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){
            // need to call this method from list view handler in order to have list filled previously

            mListView.post(new Runnable() {
                @Override
                public void run() {

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mListView.getFirstVisiblePosition(),
                            mListView.getLastVisiblePosition());

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
}