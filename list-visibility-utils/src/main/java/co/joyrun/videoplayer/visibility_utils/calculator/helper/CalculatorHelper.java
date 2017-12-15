package co.joyrun.videoplayer.visibility_utils.calculator.helper;

import android.graphics.Rect;
import android.view.View;

import com.joyrun.videoplayer.visibilityutils.BuildConfig;

import co.joyrun.videoplayer.visibility_utils.utils.Logger;

/**
 * Created by keven-liang on 2017/12/15.
 */

public class CalculatorHelper {

    private static Boolean SHOW_LOGS = BuildConfig.DEBUG;
    private static final String TAG = CalculatorHelper.class.getName();

    public static int getVisibilityPercents(View currentView, Rect currentViewRect) {
        if(SHOW_LOGS) Logger.v(TAG, ">> getVisibilityPercents currentView " + currentView);

        int percents = 100;

        currentView.getLocalVisibleRect(currentViewRect);
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents currentViewRect top " + currentViewRect.top + ", left " + currentViewRect.left + ", bottom " + currentViewRect.bottom + ", right " + currentViewRect.right);

        int height = currentView.getHeight();
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents height " + height);

        if(viewIsPartiallyHiddenTop(currentViewRect)){
            // view is partially hidden behind the top edge
            percents = (height - currentViewRect.top) * 100 / height;
        } else if(viewIsPartiallyHiddenBottom(height,currentViewRect)){
            percents = currentViewRect.bottom * 100 / height;
        }

        setVisibilityPercentsText(currentView, percents);
        if(SHOW_LOGS) Logger.v(TAG, "<< getVisibilityPercents, percents " + percents);

        return percents;
    }
    private static boolean viewIsPartiallyHiddenTop(Rect currentViewRect) {
        return currentViewRect.top > 0;
    }
    private static boolean viewIsPartiallyHiddenBottom(int height,Rect currentViewRect) {
        return currentViewRect.bottom > 0 && currentViewRect.bottom < height;
    }
    private static void setVisibilityPercentsText(View currentView, int percents) {
        if(SHOW_LOGS) Logger.v(TAG, "setVisibilityPercentsText percents " + percents);
//        T videoViewHolder = (T) currentView.getTag();
//        String percentsText = "Visibility percents: " + String.valueOf(percents);
//        ((MyVideoHolder)videoViewHolder).mVisibilityPercents.setText(percentsText);
    }
}
