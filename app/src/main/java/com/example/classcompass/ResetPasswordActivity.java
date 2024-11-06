package com.example.classcompass;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.classcompass.firebaseManager.AuthenticationManager;


public class ResetPasswordActivity extends AppCompatActivity {
    private final String tag = "ResetPasswordActivity";
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        Button reset_password_backBtn = findViewById(R.id.reset_password_backBtn);
        reset_password_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button reset_password_submitBtn = findViewById(R.id.reset_password_submitBtn);
        reset_password_submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView reset_password_errorTxt = findViewById(R.id.reset_password_errorTxt);
                reset_password_errorTxt.setTextColor(ContextCompat.getColor(context, R.color.red));
                reset_password_errorTxt.setText("");
                EditText reset_password_mailInput = findViewById(R.id.reset_password_emailInput);
                String email = reset_password_mailInput.getText().toString();
                if(email.isEmpty()){
                    reset_password_errorTxt = findViewById(R.id.reset_password_errorTxt);
                    reset_password_errorTxt.setTextColor(ContextCompat.getColor(context, R.color.red));
                    reset_password_errorTxt.setText("メールアドレスを入力してください");
                }else{
                    AuthenticationManager.emailAuth(email, new AuthenticationManager.AuthenticationErrorListener() {
                        @Override
                        public void onError(String errorMsg) {
                            //  リンク送付に失敗した場合
                            Log.d(tag, errorMsg);
                            TextView reset_password_errorTxt = findViewById(R.id.reset_password_errorTxt);
                            reset_password_errorTxt.setTextColor(ContextCompat.getColor(context, R.color.red));
                            reset_password_errorTxt.setText(errorMsg);
                        }

                        @Override
                        public void onSuccess(String successMsg) {
                            //  リンク送付に成功した場合
                            Log.d(tag, successMsg);
                            TextView reset_password_errorTxt = findViewById(R.id.reset_password_errorTxt);
                            reset_password_errorTxt.setTextColor(ContextCompat.getColor(context, R.color.green));
                            reset_password_errorTxt.setText(successMsg);
                        }
                    });
                }



            }
        });



    }
}


