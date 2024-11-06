package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.classcompass.firebaseManager.AuthenticationManager;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private final String tag = "LoginActivity";
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        AuthenticationManager.initialize(getApplicationContext());

        // TextInputLayoutのhintアニメーション無効
        final TextInputLayout login_pwdInputLayout = findViewById(R.id.login_pwdInputLayout);
        final CharSequence hint = login_pwdInputLayout.getHint();
        login_pwdInputLayout.setHint(null);
        Objects.requireNonNull(login_pwdInputLayout.getEditText()).setHint(hint);

        // 教師生徒表示区別
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String userClass = sharedPreferences.getString("userClass", "None");
        if (userClass.equals("teacher")) {
            Button loginBtn = findViewById(R.id.loginBtn);
            loginBtn.setText("ログイン");
        }else {
            Button loginBtn = findViewById(R.id.loginBtn);
            loginBtn.setText("はじめる");
        }

        //  戻るボタン
        Button login_backBtn = findViewById(R.id.login_backBtn);
        login_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //  ログイン
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView login_errorTxt = findViewById(R.id.login_errorTxt);
                login_errorTxt.setText("");

                EditText login_emailInput = findViewById(R.id.login_emailInput);
                String email = login_emailInput.getText().toString();

                EditText login_pwdInput = findViewById(R.id.login_pwdInput);
                String pwd = login_pwdInput.getText().toString();

                if(email.isEmpty()){
                    login_errorTxt.setText("メールアドレスを入力してください");
                } else if (pwd.isEmpty()) {
                    login_errorTxt.setText("パスワードを入力してください");
                }else {
                    if (userClass.equals("teacher")) {
                        AuthenticationManager.loginTeacher(email, pwd, new AuthenticationManager.AuthenticationErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                //  ログインに失敗した場合
                                Log.d(tag, errorMsg);
                                login_errorTxt.setText(errorMsg);
                            }

                            @Override
                            public void onSuccess(String successMsg) {
                                //  ログインに成功した場合
                                Intent login_intent = new Intent(LoginActivity.this, top_teacher.class);
                                startActivity(login_intent);
                            }
                        });

                    }else {
                        AuthenticationManager.loginStudent(email, pwd, new AuthenticationManager.AuthenticationErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                //  ログインに失敗した場合
                                Log.d(tag, errorMsg);
                                login_errorTxt.setText(errorMsg);
                            }

                            @Override
                            public void onSuccess(String successMsg) {
                                //  ログインに成功した場合
                                Intent login_intent = new Intent(LoginActivity.this, top_student.class);
                                startActivity(login_intent);
                            }
                        });
                    }
                }
            }
        });


        //  パスワードをリセットするときの遷移ボタン
        TextView login_resetBtn = findViewById(R.id.login_resetBtn);
        login_resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetpwd_intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(resetpwd_intent);
            }
        });


    }
}


