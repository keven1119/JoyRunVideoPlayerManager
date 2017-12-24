package co.joyrun.videoplayer.visibility_utils.calculator;

import android.graphics.Rect;
import android.view.View;

import co.joyrun.videoplayer.visibility_utils.calculator.helper.CalculatorHelper;
import co.joyrun.videoplayer.visibility_utils.items.ListItem;
import co.joyrun.videoplayer.visibility_utils.items.ListItemData;
import co.joyrun.videoplayer.visibility_utils.scroll_utils.ItemsPositionGetter;
import co.joyrun.videoplayer.visibility_utils.scroll_utils.ScrollDirectionDetector;
import co.joyrun.videoplayer.visibility_utils.utils.Config;
import co.joyrun.videoplayer.visibility_utils.utils.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * A utility that tracks current {@link ListItem} visibility.
 * Current ListItem is an item defined by calling {@link #setCurrentItem(ListItemData)}.
 * Or it might be mock current item created in method {@link #getMockCurrentItem}
 *
 * The logic is following: when current view is going out of screen (up or down) by {@link #INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS} or more then neighbour item become "active" by calling {@link Callback#activateNewCurrentItem}
 * "Going out of screen" is calculated when {@link #onStateTouchScroll} is called from super class {@link BaseItemsVisibilityCalculator}
 *
 * Method {@link ListItemsVisibilityCalculator#onScrollStateIdle} should be called only when scroll state become idle. // TODO: test it
 * When it's called we look for new current item that eventually will be set as "active" by calling {@link #setCurrentItem(ListItemData)}
 * Regarding the {@link #mScrollDirection} new current item is calculated from top to bottom (if DOWN) or from bottom to top (if UP).
 * The first(or last) visible item is set to current. It's visibility percentage is calculated. Then we are going though all visible items and find the one that is the most visible.
 *
 * Method {@link #onStateFling} is calling {@link Callback#deactivateCurrentItem}
 *
 * @author danylo.volokh
 */
public class SingleListViewItemActiveCalculator extends BaseItemsVisibilityCalculator {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = SingleListViewItemActiveCalculator.class.getSimpleName();

    private static final int INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS = 70;

    private  Callback<ListItem> mCallback;
    private  List mListItems;

    /** Initial scroll direction should be UP in order to set as active most top item if no active item yet*/
    private ScrollDirectionDetector.ScrollDirection mScrollDirection = ScrollDirectionDetector.ScrollDirection.UP;

    /**
     * The data of this member will be changing all the time
     */
    private final ListItemData mCurrentItem = new ListItemData();

    public SingleListViewItemActiveCalculator(Callback<ListItem> callback) {
        mCallback = callback;
    }

    public void setListItems(List listItems){
        if(mItemRectMap == null){
            mItemRectMap = new HashMap<>();
        }
        mItemRectMap.clear();
        mListItems = listItems;
    }

    /**
     * Methods of this callback will be called when new active item is found {@link Callback#activateNewCurrentItem(ListItem, View, int)}
     * or when there is no active item {@link Callback#deactivateCurrentItem(ListItem, View, int)} - this might happen when user scrolls really fast
     */
    public interface Callback<T extends ListItem>{
        void activateNewCurrentItem(T newListItem, View currentView, int position);
        void deactivateCurrentItem(T listItemToDeactivate, View view, int position);
    }

    /**
     * When Scrolling list is in this state we start calculating Active Item.
     * Here we assume that scroll state was idle previously and {@link #mCurrentItem} already contains some data
     *
     * @param itemsPositionGetter
     */
    @Override
    protected void onStateTouchScroll(ItemsPositionGetter itemsPositionGetter) {
        if(SHOW_LOGS) Logger.v(TAG, ">> onStateTouchScroll, mScrollDirection " + mScrollDirection);

        ListItemData listItemData = mCurrentItem;
        if(SHOW_LOGS) Logger.v(TAG, "onStateTouchScroll, listItemData " + listItemData);

        calculateActiveItem(itemsPositionGetter, listItemData);
        if(SHOW_LOGS) Logger.v(TAG, "<< onStateTouchScroll, mScrollDirection " + mScrollDirection);
    }

    /**
     * This method calculates visibility of next item.
     * There are some cases when next item cannot be filled with data:
     *
     * 1. When current data is last item in the list. In this case there is no next data
     * 2. Index of current view cannot be calculated because view was already recycled
     *
     * @param itemsPositionGetter
     * @param currentIem - the item that is active right now
     * @param outNextItemData - out parameter. It will be filled with next item data if the one exists
     */
    private void findNextItem(ItemsPositionGetter itemsPositionGetter, ListItemData currentIem, ListItemData outNextItemData) {
        int nextItemVisibilityPercents = 0;
        int nextItemIndex = currentIem.getIndex() + 1;
        if(SHOW_LOGS) Logger.v(TAG, "findNextItem, nextItemIndex " + nextItemIndex);

        if(nextItemIndex < mListItems.size()){
            int indexOfCurrentView = itemsPositionGetter.indexOfChild(currentIem.getView());
            if(SHOW_LOGS) Logger.v(TAG, "findNextItem, indexOfCurrentView " + indexOfCurrentView);

            if(indexOfCurrentView >= 0){
                View nextView = itemsPositionGetter.getChildAt(indexOfCurrentView + 1);
                if(nextView != null){
                    Rect rect = getViewRect(nextView);
//                    ListItem next = mListItems.get(nextItemIndex);
//                    if(SHOW_LOGS) Logger.v(TAG, "findNextItem, next " + next + ", nextView " + nextView);

                    nextItemVisibilityPercents = CalculatorHelper.getVisibilityPercents(nextView,rect);
                    outNextItemData.fillWithData(nextItemIndex, nextView);

                } else {
                    if(SHOW_LOGS) Logger.v(TAG, "findNextItem, nextView null. There is no view next to current");
                }

            } else {
                if(SHOW_LOGS) Logger.v(TAG, "findNextItem, current view is no longer attached to listView");
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "findNextItem, nextItemVisibilityPercents " + nextItemVisibilityPercents);
    }

    private Rect getViewRect(View  view){
        if(view != null) {
            Rect rect = mItemRectMap.get(view);
            if (rect == null) {
                rect = new Rect();
                mItemRectMap.put(view,rect);
            }

            return rect;
        }else {
            return null;
        }
    }

    private HashMap<View , Rect> mItemRectMap = new HashMap<>();

//    private final Rect mCurrentViewRect = new Rect();

//    public int getVisibilityPercents(View currentView, Rect currentViewRect) {
//        if(SHOW_LOGS) Logger.v(TAG, ">> getVisibilityPercents currentView " + currentView);
//
//        int percents = 100;
//
//        currentView.getLocalVisibleRect(currentViewRect);
//        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents currentViewRect top " + currentViewRect.top + ", left " + currentViewRect.left + ", bottom " + currentViewRect.bottom + ", right " + currentViewRect.right);
//
//        int height = currentView.getHeight();
//        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents height " + height);
//
//        if(viewIsPartiallyHiddenTop(currentViewRect)){
//            // view is partially hidden behind the top edge
//            percents = (height - currentViewRect.top) * 100 / height;
//        } else if(viewIsPartiallyHiddenBottom(height,currentViewRect)){
//            percents = currentViewRect.bottom * 100 / height;
//        }
//
//        setVisibilityPercentsText(currentView, percents);
//        if(SHOW_LOGS) Logger.v(TAG, "<< getVisibilityPercents, percents " + percents);
//
//        return percents;
//    }
//    private boolean viewIsPartiallyHiddenTop(Rect currentViewRect) {
//        return currentViewRect.top > 0;
//    }
//    private boolean viewIsPartiallyHiddenBottom(int height,Rect currentViewRect) {
//        return currentViewRect.bottom > 0 && currentViewRect.bottom < height;
//    }
//    private void setVisibilityPercentsText(View currentView, int percents) {
//        if(SHOW_LOGS) Logger.v(TAG, "setVisibilityPercentsText percents " + percents);
////        T videoViewHolder = (T) currentView.getTag();
////        String percentsText = "Visibility percents: " + String.valueOf(percents);
////        ((MyVideoHolder)videoViewHolder).mVisibilityPercents.setText(percentsText);
//    }

    /**
     * This method calculates visibility of previous item.
     * There are some cases when previous item cannot be filled with data:
     *
     * 1. When current data is first item in the list. in this case there is no previous data
     * 2. Index of current view cannot be calculated because view was already recycled
     *
     * @param itemsPositionGetter
     * @param currentIem - the item that is active right now
     * @param outPreviousItemData - out parameter. It will be filled with previous item data if the one exists
     */
    private void findPreviousItem(ItemsPositionGetter itemsPositionGetter, ListItemData currentIem, ListItemData outPreviousItemData) {
        int previousItemVisibilityPercents = 0;
        int previousItemIndex = currentIem.getIndex() -1;
        if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, previousItemIndex " + previousItemIndex);

        if(previousItemIndex >= 0){
            int indexOfCurrentView = itemsPositionGetter.indexOfChild(currentIem.getView());
            if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, indexOfCurrentView " + indexOfCurrentView);

            if(indexOfCurrentView > 0){
                View previousView = itemsPositionGetter.getChildAt(indexOfCurrentView - 1);
//                ListItem previous = mListItems.get(previousItemIndex);
                if(previousView != null) {
                    Rect rect = getViewRect(previousView);
//                if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, previous " + previous + ", previousView " + previousView);

                    previousItemVisibilityPercents = CalculatorHelper.getVisibilityPercents(previousView, rect);
                    outPreviousItemData.fillWithData(previousItemIndex, previousView);
                }

            } else {
                if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, current view is no longer attached to listView");
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, previousItemVisibilityPercents " + previousItemVisibilityPercents);
    }

    @Override
    public void onScrollStateIdle(ItemsPositionGetter itemsPositionGetter, int firstVisiblePosition, int lastVisiblePosition) {

        if(SHOW_LOGS) Logger.v(TAG, "onScrollStateIdle, firstVisiblePosition " + firstVisiblePosition + ", lastVisiblePosition " + lastVisiblePosition);
        calculateMostVisibleItem(itemsPositionGetter, firstVisiblePosition, lastVisiblePosition);
    }

    /**
     * This method calculates most visible item from top to bottom or from bottom to top depends on scroll direction.
     *
     * @param itemsPositionGetter
     * @param firstVisiblePosition
     * @param lastVisiblePosition
     */
    private void calculateMostVisibleItem(ItemsPositionGetter itemsPositionGetter, int firstVisiblePosition, int lastVisiblePosition) {

        ListItemData mostVisibleItem = getMockCurrentItem(itemsPositionGetter, firstVisiblePosition, lastVisiblePosition);
        View mostVisibleItemView = mostVisibleItem.getView();
        if(mostVisibleItemView != null) {
            int maxVisibilityPercents = mostVisibleItem.getVisibilityPercents(mListItems, getViewRect(mostVisibleItemView));

            switch (mScrollDirection) {
                case UP:
                    bottomToTopMostVisibleItem(itemsPositionGetter, maxVisibilityPercents, mostVisibleItem);
                    break;
                case DOWN:
                    topToBottomMostVisibleItem(itemsPositionGetter, maxVisibilityPercents, mostVisibleItem);
                    break;
                default:
                    throw new RuntimeException("not handled mScrollDirection " + mScrollDirection);
            }
            if (SHOW_LOGS)
                Logger.v(TAG, "topToBottomMostVisibleItem, mostVisibleItem " + mostVisibleItem);

            if (mostVisibleItem.isMostVisibleItemChanged()) {
                if (SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, item changed");

                //滑动停止idle 时调用
                setCurrentItem(mostVisibleItem);
            } else {
                if (SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, item not changed");

            }
        }
    }

    private void topToBottomMostVisibleItem(ItemsPositionGetter itemsPositionGetter, int maxVisibilityPercents, ListItemData outMostVisibleItem) {
        int mostVisibleItemVisibilityPercents = maxVisibilityPercents;

        int currentItemVisibilityPercents;

        for(int indexOfCurrentItem = itemsPositionGetter.getFirstVisiblePosition(), indexOfCurrentView = itemsPositionGetter.indexOfChild(outMostVisibleItem.getView())
            ; indexOfCurrentView < itemsPositionGetter.getChildCount() // iterating via listView Items
                ; indexOfCurrentItem++, indexOfCurrentView++){

            if(SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, indexOfCurrentView " + indexOfCurrentView);
//            ListItem listItem = mListItems.get(indexOfCurrentItem);

            View currentView = itemsPositionGetter.getChildAt(indexOfCurrentView);
            if(currentView != null) {
                Rect rect = getViewRect(currentView);
                currentItemVisibilityPercents = CalculatorHelper.getVisibilityPercents(currentView, rect);
                if (SHOW_LOGS)
                    Logger.v(TAG, "topToBottomMostVisibleItem, currentItemVisibilityPercents " + currentItemVisibilityPercents);
                if (SHOW_LOGS)
                    Logger.v(TAG, "topToBottomMostVisibleItem, mostVisibleItemVisibilityPercents " + mostVisibleItemVisibilityPercents);

                if (currentItemVisibilityPercents > mostVisibleItemVisibilityPercents) {

                    mostVisibleItemVisibilityPercents = currentItemVisibilityPercents;
                    outMostVisibleItem.fillWithData(indexOfCurrentItem, currentView);

                }
            }
        }

        View currentItemView = mCurrentItem.getView();
        View mostVisibleView = outMostVisibleItem.getView();

        // set if newly found most visible view is different from previous most visible view
        boolean itemChanged = currentItemView != mostVisibleView;
        if(SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, itemChanged " + itemChanged);

        outMostVisibleItem.setMostVisibleItemChanged(itemChanged);

        if(SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, outMostVisibleItem index " + outMostVisibleItem.getIndex() + ", outMostVisibleItem view " + outMostVisibleItem.getView());
    }

    private void bottomToTopMostVisibleItem(ItemsPositionGetter itemsPositionGetter, int maxVisibilityPercents, ListItemData outMostVisibleItem) {
        int mostVisibleItemVisibilityPercents = maxVisibilityPercents;

        int currentItemVisibilityPercents;
        for(int indexOfCurrentItem = itemsPositionGetter.getLastVisiblePosition(), indexOfCurrentView = itemsPositionGetter.indexOfChild(outMostVisibleItem.getView())
            ; indexOfCurrentView >= 0 // iterating via listView Items
                ; indexOfCurrentItem--, indexOfCurrentView--){

            if(SHOW_LOGS) Logger.v(TAG, "bottomToTopMostVisibleItem, indexOfCurrentView " + indexOfCurrentView);
//            ListItem listItem = mListItems.get(indexOfCurrentItem);
            View currentView = itemsPositionGetter.getChildAt(indexOfCurrentView);
            if (currentView != null) {
                Rect rect = getViewRect(currentView);
                currentItemVisibilityPercents = CalculatorHelper.getVisibilityPercents(currentView, rect);
                if (SHOW_LOGS)
                    Logger.v(TAG, "bottomToTopMostVisibleItem, currentItemVisibilityPercents " + currentItemVisibilityPercents);

                if (currentItemVisibilityPercents > mostVisibleItemVisibilityPercents) {
                    mostVisibleItemVisibilityPercents = currentItemVisibilityPercents;
                    outMostVisibleItem.fillWithData(indexOfCurrentItem, currentView);
                }

                View currentItemView = mCurrentItem.getView();
                View mostVisibleView = outMostVisibleItem.getView();

                // set if newly found most visible view is different from previous most visible view
                boolean itemChanged = currentItemView != mostVisibleView;
                if (SHOW_LOGS)
                    Logger.v(TAG, "topToBottomMostVisibleItem, itemChanged " + itemChanged);

                outMostVisibleItem.setMostVisibleItemChanged(itemChanged);
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "bottomToTopMostVisibleItem, outMostVisibleItem " + outMostVisibleItem);
    }

    /**
     * @param firstVisiblePosition in {@link #mListItems}
     * @param lastVisiblePosition in {@link #mListItems}
     * @return ListItemData at lastVisiblePosition if user scrolled UP and ListItemData at firstVisiblePosition if user scrolled DOWN
     */
    private ListItemData getMockCurrentItem(ItemsPositionGetter itemsPositionGetter, int firstVisiblePosition, int lastVisiblePosition) {
        if(SHOW_LOGS) Logger.v(TAG, "getMockCurrentItem, mScrollDirection " + mScrollDirection);
        if(SHOW_LOGS) Logger.v(TAG, "getMockCurrentItem, firstVisiblePosition " + firstVisiblePosition);
        if(SHOW_LOGS) Logger.v(TAG, "getMockCurrentItem, lastVisiblePosition " + lastVisiblePosition);

        ListItemData mockCurrentItemData;
        switch (mScrollDirection){
            case UP:
                int lastVisibleItemIndex;
                if(lastVisiblePosition < 0/*-1 may be returned from ListView*/){
                    lastVisibleItemIndex = firstVisiblePosition;
                } else {
                    lastVisibleItemIndex = lastVisiblePosition;
                }

                mockCurrentItemData = new ListItemData().fillWithData(lastVisibleItemIndex, itemsPositionGetter.getChildAt(itemsPositionGetter.getChildCount() - 1));
                break;
            case DOWN:
                mockCurrentItemData = new ListItemData().fillWithData(firstVisiblePosition, itemsPositionGetter.getChildAt(0/*first visible*/));
                break;
            default:
                throw new RuntimeException("not handled mScrollDirection " + mScrollDirection);
        }
        return mockCurrentItemData;
    }

    /**
     * 1. This method get current item visibility percents.
     *
     * 2. Then get scroll direction and depending on it either call {@link #findNextItem(ItemsPositionGetter, ListItemData, ListItemData)}
     * or {@link #findPreviousItem(ItemsPositionGetter, ListItemData, ListItemData)}
     *
     * 3. Then it checks if current item visibility percents is enough for deactivating it.
     * If it's enough it checks if new active item was found.
     *
     * 4. If all conditions match it calls {@link #setCurrentItem(ListItemData)}
     * @param itemsPositionGetter
     * @param listItemData
     */
    private void calculateActiveItem(ItemsPositionGetter itemsPositionGetter, ListItemData listItemData) {
        /** 1. */
        View listItemDataView = listItemData.getView();
        if(listItemDataView != null) {
            int currentItemVisibilityPercents = listItemData.getVisibilityPercents(mListItems, getViewRect(listItemDataView));
            if (SHOW_LOGS)
                Logger.v(TAG, "calculateActiveItem, mScrollDirection " + mScrollDirection);

            /** 2. */
            ListItemData neighbourItemData = new ListItemData();
            switch (mScrollDirection) {
                case UP:
                    findPreviousItem(itemsPositionGetter, listItemData, neighbourItemData);
                    break;
                case DOWN:
                    findNextItem(itemsPositionGetter, listItemData, neighbourItemData);
                    break;
            }
            if (SHOW_LOGS)
                Logger.v(TAG, "calculateActiveItem, currentItemVisibilityPercents " + currentItemVisibilityPercents);

            /** 3. */
            if (enoughPercentsForDeactivation(currentItemVisibilityPercents) && neighbourItemData.isAvailable()) {

                // neighbour item become active (current)
//            /** 4. */
//            //当滑过一个item时，暂停视屏处理
                onStateFling(itemsPositionGetter);
//
//            //当划过一屏时处理所有 播放状态
                setCurrentItem(neighbourItemData);

            }
        }
    }

    private boolean enoughPercentsForDeactivation(int visibilityPercents) {
        boolean enoughPercentsForDeactivation = visibilityPercents <= INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS;
        if(SHOW_LOGS) Logger.v(TAG, "enoughPercentsForDeactivation " + enoughPercentsForDeactivation);
        return enoughPercentsForDeactivation;
    }

    @Override
    protected void onStateFling(ItemsPositionGetter itemsPositionGetter) {
        ListItemData currentItemData = mCurrentItem;

        Object o = mListItems.get(currentItemData.getIndex());
        if(o instanceof ListItem) {
            //视频item滑出屏幕
            mCallback.deactivateCurrentItem((ListItem) o, currentItemData.getView(), currentItemData.getIndex());
        }
    }

    @Override
    public void onScrollDirectionChanged(ScrollDirectionDetector.ScrollDirection scrollDirection) {
        if(SHOW_LOGS) Logger.v(TAG, "onScrollDirectionChanged, scrollDirection " + scrollDirection);
        mScrollDirection = scrollDirection;
    }

    private void setCurrentItem(ListItemData newCurrentItem) {
        if(SHOW_LOGS) Logger.v(TAG, "setCurrentItem, newCurrentItem " + newCurrentItem);

        int itemPosition = newCurrentItem.getIndex();
        View view = newCurrentItem.getView();
        Object o = mListItems.get(itemPosition);
            mCurrentItem.fillWithData(itemPosition, view);
        if(o instanceof ListItem) {
            //新的视频item滑进屏幕
            mCallback.activateNewCurrentItem(
                    (ListItem)o
                    , view
                    , itemPosition);
        }
    }
}
