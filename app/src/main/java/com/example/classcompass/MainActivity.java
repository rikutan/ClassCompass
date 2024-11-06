package com.example.classcompass;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.example.classcompass.databaseManager.TeachersDatabaseManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private final String tag = "MainActivity";

    Context context = this;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        //  試し用 ※必ず元の状態に戻すこと　///////////////////////////////////////
//        setContentView(R.layout.activity_main);
//        Button button = findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth mAuth0 = FirebaseAuth.getInstance();
//                SharedPreferences sharedPreferences0 = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences0.edit();
//                editor.remove("studentID");
//                editor.remove("teacherID");
//                editor.remove("classRoomID");
//                editor.apply();
//                mAuth0.signOut();
//                intent = new Intent(MainActivity.this, top_student.class);
//                startActivity(intent);
//            }
//        });
        //////////////////////////////////////////////////////////////////////////


        SharedPreferences sharedPreferences0 = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences0.edit();
        editor.putString("chatStatus", "0");
        editor.putString("classTopStatus", "0");
        editor.putString("AlbumStatus", "0");
        editor.apply();



//      //  ログイン状態確認
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
            String teacherID = sharedPreferences.getString("teacherID", "None");
            if (teacherID.equals("None")) {
                Intent login_intent = new Intent(MainActivity.this, top_student.class);
                startActivity(login_intent);
            } else {
                Intent login_intent = new Intent(MainActivity.this, top_teacher.class);
                startActivity(login_intent);
            }
        } else {
            setContentView(R.layout.startup);

            ImageView startup_student = findViewById(R.id.startup_student);
            startup_student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startup_student.setBackgroundResource(R.drawable.startup_serectorbold1);
                    Button startup_btn = findViewById(R.id.startup_btn);
                    startup_btn.setVisibility(View.VISIBLE);
                    startup_btn.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                    startup_btn.setTextColor(ContextCompat.getColor(context, R.color.black));
                    View startup_teacher = findViewById(R.id.startup_teacher);
                    startup_teacher.setBackgroundResource(R.drawable.startup_serector2);
                    intent = new Intent(MainActivity.this, StartStudentActivity.class);
                }
            });

            ImageView startup_teacher = findViewById(R.id.startup_teacher);
            startup_teacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startup_teacher.setBackgroundResource(R.drawable.startup_serectorbold2);
                    Button startup_btn = findViewById(R.id.startup_btn);
                    startup_btn.setVisibility(View.VISIBLE);
                    startup_btn.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                    startup_btn.setTextColor(ContextCompat.getColor(context, R.color.white));
                    View startup_student = findViewById(R.id.startup_student);
                    startup_student.setBackgroundResource(R.drawable.startup_serector1);
                    intent = new Intent(MainActivity.this, StartTeacherActivity.class);
                }
            });

            Button startup_btn = findViewById(R.id.startup_btn);
            startup_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });

        }

    }
}



