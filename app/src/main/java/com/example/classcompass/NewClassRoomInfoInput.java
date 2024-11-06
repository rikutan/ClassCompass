package com.example.classcompass;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewClassRoomInfoInput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_classroom_info_input);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ビューの取得
        EditText inputSchoolName = findViewById(R.id.new_classroom_info_input_schoolname);
        EditText inputNewClassroom = findViewById(R.id.new_classroom_info_input_classroom);
        EditText inputYear = findViewById(R.id.new_classroom_input_info_yearEdit);

        // ビューの取得(エラーメッセージ)
        TextView errMsg1 = findViewById(R.id.new_classroom_info_input_err1);
        TextView errMsg2 = findViewById(R.id.new_classroom_info_input_err2);
        TextView errMsg3 = findViewById(R.id.new_classroom_info_input_err3);

        // 入力制限を設定
        InputFilter lengthFilter30 = new InputFilter.LengthFilter(30);
        InputFilter lengthFilter20 = new InputFilter.LengthFilter(20);

        // 半角数字のみを許可するフィルタを設定
        InputFilter numberFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        inputYear.setFilters(new InputFilter[]{numberFilter, new InputFilter.LengthFilter(4)});

        inputSchoolName.setFilters(new InputFilter[]{lengthFilter30});
        inputNewClassroom.setFilters(new InputFilter[]{lengthFilter20});

        // Intentから値を取得し、各フィールドに設定
        Intent intent = getIntent();
        if (intent != null) {
            inputSchoolName.setText(intent.getStringExtra("schoolName"));
            inputNewClassroom.setText(intent.getStringExtra("className"));
            inputYear.setText(intent.getStringExtra("year"));
        }

        // 次に進むボタン
        Button nextButton = findViewById(R.id.new_classroom_info_input_nextBtn);
        nextButton.setOnClickListener(v -> {
            // 入力データの取得
            String schoolName = inputSchoolName.getText().toString().trim();
            String className = inputNewClassroom.getText().toString().trim();
            String year = inputYear.getText().toString().trim();

            // 入力チェック
            boolean isValid = true;

            if (schoolName.isEmpty()) {
                errMsg1.setText("学校名を入力してください");
                errMsg1.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errMsg1.setVisibility(View.GONE);
            }

            if (className.isEmpty()) {
                errMsg2.setText("クラス名を入力してください");
                errMsg2.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errMsg2.setVisibility(View.GONE);
            }

            if (year.isEmpty()) {
                errMsg3.setText("年を入力してください");
                errMsg3.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (!year.matches("\\d+")) {
                errMsg3.setText("年は数字のみで入力してください");
                errMsg3.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (year.length() != 4) {
                errMsg3.setText("年は4桁で入力してください");
                errMsg3.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                int yearInt = Integer.parseInt(year);
                if (yearInt < 1900 || yearInt > 2024) {
                    errMsg3.setText("年は1900年から2024年の間で入力してください");
                    errMsg3.setVisibility(View.VISIBLE);
                    isValid = false;
                } else {
                    errMsg3.setVisibility(View.GONE);
                }
            }

            // エラーがない場合に次のアクティビティへ遷移
            if (isValid) {
                Intent intentNext = new Intent(this, NewClassRoomInfoCheck.class);
                intentNext.putExtra("schoolName", schoolName);
                intentNext.putExtra("className", className);
                intentNext.putExtra("year", year);
                startActivity(intentNext);
            }
        });

        // 戻るボタン
        TextView backTxt = findViewById(R.id.new_classroom_info_input_backTxt);
        backTxt.setOnClickListener(v -> {
            Intent intentNext = new Intent(this, top_teacher.class);
        });
    }
}
