package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.VideoView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;

public class LotteryPlayActivity extends AppCompatActivity {
    private final String tag = "LotteryPlayActivity";
    private VideoView videoView;
    private String[] videoPaths;
    private int currentVideoIndex = 0;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_play);
        result = findViewById(R.id.lottery_play_result);
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");
        String classMemberID = sharedPreferences.getString("classMemberID", "None");
        Log.d("LotteryPlayActivity classMemberID", classMemberID);

        Intent intent = getIntent();
        String lotteryID = intent.getStringExtra("lotteryID");
        if(classMemberID.startsWith("CT")){
            result.setText("先生お疲れ様です");
        }else {
            ClassRoomsDatabaseManager.getLotteryResult(classRoomID, lotteryID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    //  データ取得に失敗した場合
                    Log.d(tag, errorMsg);
                }

                @Override
                public void onSuccess(String successMsg) {
                    //  データ取得に成功した場合
                    Log.d(tag, successMsg);
                }
            }, new ClassRoomsDatabaseManager.LotteryResultListener() {
                @Override
                public void onLotteryResultID(String lotteryResultID) {
                    //  lotteryResultIDを利用する場合
                    Log.d(tag, lotteryResultID);
                    result.setText(lotteryResultID);
                }
            });
        }



        videoPaths = new String[]{
                "android.resource://" + getPackageName() + "/" + R.raw.lottery1,
                "android.resource://" + getPackageName() + "/" + R.raw.lottery2
        };

        videoView = findViewById(R.id.lottery_play_video);

        CustomMediaController mediaController = new CustomMediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        playVideo();

        videoView.setOnCompletionListener(mp -> {
            currentVideoIndex++;
            if (currentVideoIndex < videoPaths.length) {
                playVideo();
            } else {
                finish();
            }
        });
    }

    private void playVideo() {
        Uri uri = Uri.parse(videoPaths[currentVideoIndex]);
        videoView.setVideoURI(uri);
        videoView.start();

        if (currentVideoIndex == 1) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> result.setVisibility(TextView.VISIBLE), 700);
        } else {
            result.setVisibility(TextView.INVISIBLE);
        }
    }

    private static class CustomMediaController extends android.widget.MediaController {
        public CustomMediaController(android.content.Context context) {
            super(context);
        }

        @Override
        public void hide() {
        }

        @Override
        public void setMediaPlayer(MediaPlayerControl player) {
            super.setMediaPlayer(player);
            this.setPrevNextListeners(null, null);
        }

        @Override
        public void show(int timeout) {
        }
    }
}
