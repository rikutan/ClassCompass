package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartStudentActivity extends AppCompatActivity {
    private final String tag = "StartStudentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_student);

        Button start_studentB = findViewById(R.id.start_studentB);
        start_studentB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartStudentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //  生徒として利用
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userClass", "student");
        editor.apply();


        Button Student_new = findViewById(R.id.Student_new);
        Student_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartStudentActivity.this, StudentSignupActivity.class);
                startActivity(intent);
            }
        });

        Button Student_start = findViewById(R.id.Student_start);
        Student_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartStudentActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
