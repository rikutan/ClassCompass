package com.example.classcompass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classcompass.databaseManager.StudentsDatabaseManager;
public class StudentSignupCheckActivity extends AppCompatActivity {
    private static final String tag = "StudentSignupCheck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.student_signup_check);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Intentを受け取る
        Intent intentGet = getIntent();

        // 前画面から値の取得
        String mail = intentGet.getStringExtra("mail");
        String pass = intentGet.getStringExtra("pass");
        String passCheck = intentGet.getStringExtra("passCheck"); // 追加
        String name = intentGet.getStringExtra("name");
        String kana = intentGet.getStringExtra("kana");
        String year = intentGet.getStringExtra("year");
        String month = intentGet.getStringExtra("month");
        String day = intentGet.getStringExtra("day");

        // TextViewに値をセットする
        TextView displayMail = findViewById(R.id.student_signup_check_displayMail);
        TextView displayPass = findViewById(R.id.student_signup_check_displayPass);
        TextView displayName = findViewById(R.id.student_signup_check_displayName);
        TextView displayKana = findViewById(R.id.student_signup_check_displayKana);
        TextView displayYear = findViewById(R.id.student_signup_check_displayYear);
        TextView displayMonth = findViewById(R.id.student_signup_check_displayMonth);
        TextView displayDay = findViewById(R.id.student_signup_check_displayDay);

        displayMail.setText(mail);
        displayPass.setText(pass);
        displayName.setText(name);
        displayKana.setText(kana);
        displayYear.setText(year);
        displayMonth.setText(month);
        displayDay.setText(day);



        // 送信ボタン(データも転送)
        Button sendBtn = findViewById(R.id.student_signup_check_sendBtn);
        sendBtn.setOnClickListener(v -> {

            // 入力されたデータの取得
            String studentMail = displayMail.getText().toString().trim();
            String pwd = displayPass.getText().toString().trim();
            String studentName = displayName.getText().toString().trim();
            String studentNameKana = displayKana.getText().toString().trim();
            String studentBirthday = displayYear.getText().toString().trim() +
                    displayMonth.getText().toString().trim() +
                    displayDay.getText().toString().trim();

            //データアクセスプログラム
            StudentsDatabaseManager.setStudent(pwd, studentName, studentNameKana, studentMail, studentBirthday, new StudentsDatabaseManager.StudentErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    //  新規登録に失敗した場合
                    Log.d(tag, errorMsg);
                }
                @Override
                public void onSuccess(String SuccessMsg) {
                    //  新規登録に成功した場合
                    Log.d(tag, SuccessMsg);
                    Intent intentSend = new Intent(StudentSignupCheckActivity.this, StudentSignupCompActivity.class);
                    startActivity(intentSend);
                }
            });
        });





        // 修正ボタン(データも転送)
        Button corrBtn = findViewById(R.id.student_signup_check_corrBtn);
        corrBtn.setOnClickListener(v -> {
            Intent intentBack = new Intent(this, TeacherSignupActivity.class);
            intentBack.putExtra("mail", mail);
            intentBack.putExtra("pass", pass);
            intentBack.putExtra("passCheck", passCheck);
            intentBack.putExtra("name", name);
            intentBack.putExtra("kana", kana);
            intentBack.putExtra("year", year);
            intentBack.putExtra("month", month);
            intentBack.putExtra("day", day);
            startActivity(intentBack);
        });

        // 戻る(TextView)ボタン
        TextView textView = findViewById(R.id.student_signup_check_backtext);
        textView.setOnClickListener(v -> {
            Intent intentBack = new Intent(this, TeacherSignupActivity.class);
            intentBack.putExtra("mail", mail);
            intentBack.putExtra("pass", pass);
            intentBack.putExtra("passCheck", passCheck);
            intentBack.putExtra("name", name);
            intentBack.putExtra("kana", kana);
            intentBack.putExtra("year", year);
            intentBack.putExtra("month", month);
            intentBack.putExtra("day", day);
            startActivity(intentBack);
        });
    }
}
