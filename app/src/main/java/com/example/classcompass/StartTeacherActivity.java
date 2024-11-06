package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartTeacherActivity extends AppCompatActivity {
    private final String tag = "StartTeacherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_teacher);

        Button start_teacherB = findViewById(R.id.start_teacherB);
        start_teacherB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTeacherActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //  教師として利用
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userClass", "teacher");
        editor.apply();


        Button Teacher_new = findViewById(R.id.Teacher_new);
        Teacher_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTeacherActivity.this, TeacherSignupActivity.class);
                startActivity(intent);
            }
        });

        Button Teacher_login = findViewById(R.id.Teacher_login);
        Teacher_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTeacherActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}