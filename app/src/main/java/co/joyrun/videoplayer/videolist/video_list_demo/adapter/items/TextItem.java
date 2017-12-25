package co.joyrun.videoplayer.videolist.video_list_demo.adapter.items;

import android.view.View;

import co.joyrun.videoplayer.visibility_utils.items.ListItem;

/**
 * Created by keven-liang on 2017/12/25.
 */

public class TextItem implements ListItem {


    public TextItem(String string) {
    }

    @Override
    public int getVisibilityPercents(View view) {
        return 0;
    }

    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {

    }

    @Override
    public void deactivate(View currentView, int position) {

    }
}
