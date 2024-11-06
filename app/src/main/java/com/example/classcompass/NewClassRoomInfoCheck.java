package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;

public class NewClassRoomInfoCheck extends AppCompatActivity {

    private static final String TAG = "NewClassRoomInfoCheck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_classroom_info_check);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Intentを受け取る
        Intent intentGet = getIntent();

        // 前画面から値の取得
        String School = intentGet.getStringExtra("schoolName");
        String Class = intentGet.getStringExtra("className");
        String Year = intentGet.getStringExtra("year");

        // TextViewに値をセットする
        TextView displaySchoolName = findViewById(R.id.new_classroom_info_check_display_schoolname);
        TextView displayClassname = findViewById(R.id.new_classroom_info_check_display_classname);
        TextView displayYear = findViewById(R.id.new_classroom_info_check_display_year);

        displaySchoolName.setText(School);
        displayClassname.setText(Class);
        displayYear.setText(Year);

        // 作成ボタン
        Button makeBtn = findViewById(R.id.new_classroom_info_check_makeBtn);
        makeBtn.setOnClickListener(v -> {

            //保持している teacherID を　取得する
            SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
            String teacherID = sharedPreferences.getString("teacherID", "None");

            // 入力されたデータの取得
            String className = displayClassname.getText().toString().trim();
            String schoolName = displaySchoolName.getText().toString().trim();
            String year = displayYear.getText().toString().trim();

            // データベースアクセスプログラム
            ClassRoomsDatabaseManager.initialize(getApplicationContext());
            ClassRoomsDatabaseManager.setClassRoom(teacherID, className, schoolName, year, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    // クラスルーム作成に失敗した場合
                    Log.d(TAG, errorMsg);
                }

                @Override
                public void onSuccess(String successMsg) {
                    // クラスルーム作成に成功した場合
                    Log.d(TAG, successMsg);
                }
            }, new ClassRoomsDatabaseManager.ClassQrGetListener() {
                @Override
                public void onClassQR(String classQR) {
                    // クラスルームQR画像を利用する場合
                    Log.d(TAG, classQR);

                    // 次のアクティビティにQRコードを表示するためのデータを渡す
                    Intent makeIntent = new Intent(NewClassRoomInfoCheck.this, NewClassRoomMakeComp.class);
                    makeIntent.putExtra("qrFileName", classQR);
                    startActivity(makeIntent);
                }
            });
        });




        // 修正ボタン
        Button corrBtn = findViewById(R.id.new_classroom_info_check_corrBtn);
        corrBtn.setOnClickListener(v -> {
            Intent corrIntent = new Intent(this, NewClassRoomInfoInput.class);
            corrIntent.putExtra("schoolName", School);
            corrIntent.putExtra("className", Class);
            corrIntent.putExtra("year", Year);
            startActivity(corrIntent);
        });

        // 戻るボタン
        TextView backTxt = findViewById(R.id.new_classroom_info_check_backTxt);
        backTxt.setOnClickListener(v -> {
            Intent backIntent = new Intent(this, NewClassRoomInfoInput.class);
            backIntent.putExtra("schoolName", School);
            backIntent.putExtra("className", Class);
            backIntent.putExtra("year", Year);
            startActivity(backIntent);
        });
    }
}
