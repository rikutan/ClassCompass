package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class JoinCompleteActivity extends AppCompatActivity {
    private final String tag = "JoinCompleteActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_complete);

        Intent intentGet = getIntent();
        String className =intentGet.getStringExtra("className");

        TextView joinCompleteInfo = findViewById(R.id.joinCompleteInfo);

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String userClass = sharedPreferences.getString("userClass", "None");
        String setInfo;
        if(userClass.equals("teacher")){
            setInfo = "「" + className + "」に参加しました";
        }else {
            setInfo = "「" + className + "」へようこそ！";
        }
        joinCompleteInfo.setText(setInfo);

        // クラスルームへのボタンを作成する //////////////////////////////////////
        Button classRoomEntryBtn = findViewById(R.id.classRoomEntryBtn);
        classRoomEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinCompleteActivity.this, main_select.class);
                startActivity(intent);
            }
        });

    }
}


