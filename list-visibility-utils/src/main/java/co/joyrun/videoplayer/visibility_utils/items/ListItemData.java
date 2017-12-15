package co.joyrun.videoplayer.visibility_utils.items;

import android.app.LauncherActivity;
import android.graphics.Rect;
import android.view.View;

import co.joyrun.videoplayer.visibility_utils.calculator.helper.CalculatorHelper;
import co.joyrun.videoplayer.visibility_utils.utils.Config;
import co.joyrun.videoplayer.visibility_utils.utils.Logger;

import java.util.List;


public class ListItemData {
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = LauncherActivity.ListItem.class.getSimpleName();

    private Integer mIndexInAdapter;
    private View mView;

    private boolean mIsMostVisibleItemChanged;

    public int getIndex() {
        return mIndexInAdapter;
    }

    public View getView() {
        return mView;
    }

    public ListItemData fillWithData(int indexInAdapter, View view) {
        mIndexInAdapter = indexInAdapter;
        mView = view;
        return this;
    }

    public boolean isAvailable() {
        boolean isAvailable = mIndexInAdapter != null && mView != null;
        if(SHOW_LOGS) Logger.v(TAG, "isAvailable " + isAvailable);
        return isAvailable;
    }

    public int getVisibilityPercents(List listItems,Rect rect) {
        int visibilityPercents = CalculatorHelper.getVisibilityPercents(getView(),rect);
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents, visibilityPercents " + visibilityPercents);
        return visibilityPercents;
    }

    public void setMostVisibleItemChanged(boolean isDataChanged) {
        mIsMostVisibleItemChanged = isDataChanged;
    }

    public boolean isMostVisibleItemChanged() {
        return mIsMostVisibleItemChanged;
    }

    @Override
    public String toString() {
        return "ListItemData{" +
                "mIndexInAdapter=" + mIndexInAdapter +
                ", mView=" + mView +
                ", mIsMostVisibleItemChanged=" + mIsMostVisibleItemChanged +
                '}';
    }
}
