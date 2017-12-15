package co.joyrun.videoplayer.videolist.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import co.joyrun.videoplayer.videolist.R;

/**
 * Created by keven-liang on 2017/12/15.
 */

public class MainActivity extends Activity implements View.OnClickListener{

    Button button_into_demo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_into_demo = findViewById(R.id.button_into_demo);
        button_into_demo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, VideoListActivity.class);
        startActivity(intent);
        getRequestedOrientation();
    }
}
