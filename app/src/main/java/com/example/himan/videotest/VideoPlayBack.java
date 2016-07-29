package com.example.himan.videotest;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by himan on 25/7/16.
 */
public class VideoPlayBack extends Activity {


    List<String> link;
    TextView playlistTextView;
    private int size=0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.video_playback);
        playlistTextView=(TextView)findViewById(R.id.playlist);
        link=new LinkedList<>();
        //declare linklist globally
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        File[] list = file.listFiles();
        for (File f : list) {
            String name = f.getName();
            if (name.endsWith(".mp4"))
            link.add(file.getAbsolutePath()+"/"+name);
        }

startvideo();

    }
    public void startvideo()
    {
        MediaController controller  = new MediaController(this) {



            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    Activity a = (Activity)getContext();
                    a.finish();
                }
                return true;
            }


        };
//        View mMediaControllerView = (View)findViewById(R.id.mediaController);

        VideoView vdos=(VideoView) findViewById(R.id.videoView);
        //nakli
        controller.setAnchorView(vdos);
        controller.setMediaPlayer(vdos);
        vdos.setMediaController(controller);
        //nakli

        String path=link.get(size);
        playlistTextView.setText("playing " + (size + 1) + "/" + link.size() + " video");
        vdos.setVideoURI(Uri.parse(path));
        vdos.start();
       //og
        vdos.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                if(link.size()>size+1){
                    size++;
//                    String video = link.get(size);
                   // link.remove(0);
//                    link.add(video);
                    //this above code will put first video to last index of list
                    //by doing this we can play one video after another

                    startvideo();


                }



            }
        });

    }



//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        View decorView = getWindow().getDecorView();
//        if (hasFocus) {
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
//    }
}
