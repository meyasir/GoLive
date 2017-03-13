package com.samirk433.golive;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.URI;
import java.net.URISyntaxException;

import utils.UploadVideo;

public class VideoCaptureActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private VideoView mVideoView;
    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);

        mVideoView = (VideoView) findViewById(R.id.videoView1);
        btn1 = (Button) findViewById(R.id.btn1);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        } else {
            Toast.makeText(this, "Camera Not foudn", Toast.LENGTH_SHORT).show();
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mVideoView.isPlaying()){
                    mVideoView.start();
                }else {
                    mVideoView.pause();
                }
            }
        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            mVideoView.setVideoURI(videoUri);
        }
    }

}