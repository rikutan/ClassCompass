package com.example.classcompass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeacherSignupCompActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.teacher_signup_comp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //ログイン画面遷移(教師用)
        Button logBtn = findViewById(R.id.teacher_signup_comp_nextLoginBtn);
        logBtn.setOnClickListener(v -> {
            //ログイン画面に遷移できるようにする
            Intent intent = new Intent(TeacherSignupCompActivity.this, StartTeacherActivity.class);
            startActivity(intent);
        });
    }
}