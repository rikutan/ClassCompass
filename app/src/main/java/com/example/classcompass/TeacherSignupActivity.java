package com.example.classcompass;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeacherSignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.teacher_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ビューの取得
        EditText inputMail = findViewById(R.id.teacher_signup_inputMail);
        EditText inputPass = findViewById(R.id.teacher_signup_inputPass);
        EditText inputPassCheck = findViewById(R.id.teacher_signup_inputPassCheck);
        EditText inputName = findViewById(R.id.teacher_signup_inputName);
        EditText inputKana = findViewById(R.id.teacher_signup_inputKana);
        EditText inputYear = findViewById(R.id.teacher_signup_inputYear);
        EditText inputMonth = findViewById(R.id.teacher_signup_inputMonth);
        EditText inputDay = findViewById(R.id.teacher_signup_inputDay);

        TextView errorMail = findViewById(R.id.teacher_signup_err1);
        TextView errorPass = findViewById(R.id.teacher_signup_err2);
        TextView errorPassCheck = findViewById(R.id.teacher_signup_err3);
        TextView errorName = findViewById(R.id.teacher_signup_err4);
        TextView errorKana = findViewById(R.id.teacher_signup_err5);
        TextView errorDate = findViewById(R.id.teacher_signup_err6);

        // 半角数字のみを許可するInputFilter
        InputFilter numberFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (!Character.isDigit(c)) {
                        return "";
                    }
                }
                return null;
            }
        };

        // 年、月、日の入力制限を設定
        InputFilter lengthFilterYear = new InputFilter.LengthFilter(4);
        InputFilter lengthFilterMonthDay = new InputFilter.LengthFilter(2);

        inputYear.setFilters(new InputFilter[]{lengthFilterYear, numberFilter});
        inputMonth.setFilters(new InputFilter[]{lengthFilterMonthDay, numberFilter});
        inputDay.setFilters(new InputFilter[]{lengthFilterMonthDay, numberFilter});

        // 日本語と英語だけを許可するInputFilter
        InputFilter japaneseEnglishFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
                    if (!(unicodeBlock == Character.UnicodeBlock.BASIC_LATIN ||
                            unicodeBlock == Character.UnicodeBlock.HIRAGANA ||
                            unicodeBlock == Character.UnicodeBlock.KATAKANA ||
                            unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                            unicodeBlock == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)) {
                        return "";
                    }
                }
                return null;
            }
        };

        inputName.setFilters(new InputFilter[]{japaneseEnglishFilter});
        inputKana.setFilters(new InputFilter[]{japaneseEnglishFilter});

        // パスワードの長さ制限
        InputFilter lengthFilterPassword = new InputFilter.LengthFilter(20);
        inputPass.setFilters(new InputFilter[]{lengthFilterPassword});
        inputPassCheck.setFilters(new InputFilter[]{lengthFilterPassword});

        // Intentから値を取得し、各フィールドに設定
        Intent intent = getIntent();
        if (intent != null) {
            inputMail.setText(intent.getStringExtra("mail"));
            inputPass.setText(intent.getStringExtra("pass"));
            inputPassCheck.setText(intent.getStringExtra("passCheck"));
            inputName.setText(intent.getStringExtra("name"));
            inputKana.setText(intent.getStringExtra("kana"));
            inputYear.setText(intent.getStringExtra("year"));
            inputMonth.setText(intent.getStringExtra("month"));
            inputDay.setText(intent.getStringExtra("day"));
        }

        // メールアドレスの入力を監視
        inputMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains("@")) {
                    errorMail.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 名前の入力を監視
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorName.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // ふりがなの入力を監視
        inputKana.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorKana.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // パスワードの入力を監視
        inputPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 8) {
                    errorPass.setVisibility(View.GONE);
                } else {
                    errorPass.setText("パスワードは8文字以上でなければなりません。");
                    errorPass.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // パスワード確認と一致しているかをチェック
                if (s.toString().equals(inputPassCheck.getText().toString())) {
                    errorPassCheck.setVisibility(View.GONE);
                } else {
                    errorPassCheck.setText("パスワードが一致しません。");
                    errorPassCheck.setVisibility(View.VISIBLE);
                }
            }
        });

        // パスワード確認の入力を監視
        inputPassCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // パスワードと一致しているかをチェック
                if (s.toString().equals(inputPass.getText().toString())) {
                    errorPassCheck.setVisibility(View.GONE);
                } else {
                    errorPassCheck.setText("パスワードが一致しません。");
                    errorPassCheck.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 戻る(TextView)ボタン
        TextView backtext = findViewById(R.id.teacher_signup_backtext);
        backtext.setOnClickListener(v -> {
            Intent intentBack = new Intent(this, StartTeacherActivity.class);
            startActivity(intentBack);
        });

        // 次に進むボタン(入力データ転送)
        Button nextBtn = findViewById(R.id.teacher_signup_nextBtn);
        nextBtn.setOnClickListener(v -> {

            // 入力されたデータの取得
            String mail = inputMail.getText().toString().trim();
            String pass = inputPass.getText().toString().trim();
            String passCheck = inputPassCheck.getText().toString().trim();
            String name = inputName.getText().toString().trim();
            String kana = inputKana.getText().toString().trim();
            String year = inputYear.getText().toString().trim();
            String month = inputMonth.getText().toString().trim();
            String day = inputDay.getText().toString().trim();

            boolean isValid = true;

            // 全ての項目が空のチェック
            if (mail.isEmpty() && pass.isEmpty() && passCheck.isEmpty() && name.isEmpty() && kana.isEmpty() && year.isEmpty() && month.isEmpty() && day.isEmpty()) {
                errorMail.setText("入力がされていません。");
                errorMail.setVisibility(View.VISIBLE);
                errorPass.setVisibility(View.GONE);
                errorPassCheck.setVisibility(View.GONE);
                errorName.setVisibility(View.GONE);
                errorKana.setVisibility(View.GONE);
                errorDate.setVisibility(View.GONE);
                isValid = false;
            } else {
                errorMail.setVisibility(View.GONE);
            }

            // メールアドレスの検証
            if (mail.isEmpty()) {
                errorMail.setText("メールアドレスが入力されていません。");
                errorMail.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (!mail.contains("@")) {
                errorMail.setText("メールアドレスの形式が正しくありません。");
                errorMail.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errorMail.setVisibility(View.GONE);
            }

            // パスワードの検証
            if (pass.isEmpty()) {
                errorPass.setText("パスワードが入力されていません。");
                errorPass.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (pass.length() < 8) {
                errorPass.setText("パスワードは8文字以上でなければなりません。");
                errorPass.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errorPass.setVisibility(View.GONE);
            }

            // パスワード確認の検証
            if (passCheck.isEmpty()) {
                errorPassCheck.setText("パスワード確認が入力されていません。");
                errorPassCheck.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (!pass.equals(passCheck)) {
                errorPassCheck.setText("パスワードが一致しません。");
                errorPassCheck.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errorPassCheck.setVisibility(View.GONE);
            }

            // お名前の検証
            if (name.isEmpty()) {
                errorName.setText("お名前を入力してください。");
                errorName.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errorName.setVisibility(View.GONE);
            }

            // ふりがなの検証
            if (kana.isEmpty()) {
                errorKana.setText("ふりがなを入力してください。");
                errorKana.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errorKana.setVisibility(View.GONE);
            }

            // 生年月日の検証
            if (year.isEmpty() || month.isEmpty() || day.isEmpty()) {
                errorDate.setText("生年月日のすべての項目を入力してください。");
                errorDate.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (containsFullWidthCharacters(year) || containsFullWidthCharacters(month) || containsFullWidthCharacters(day)) {
                errorDate.setText("生年月日は半角で入力してください。");
                errorDate.setVisibility(View.VISIBLE);
                isValid = false;
            } else if (!isValidDate(year, month, day)) {
                errorDate.setText("無効な生年月日です。");
                errorDate.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                errorDate.setVisibility(View.GONE);
            }

            if (isValid) {
                // エラーメッセージを非表示にする
                errorMail.setVisibility(View.GONE);
                errorPass.setVisibility(View.GONE);
                errorPassCheck.setVisibility(View.GONE);
                errorName.setVisibility(View.GONE);
                errorKana.setVisibility(View.GONE);
                errorDate.setVisibility(View.GONE);

                // 画面遷移
                Intent intentNext = new Intent(this, TeacherSignupCheckActivity.class);

                // 値を次の画面に受け渡す
                intentNext.putExtra("mail", mail);
                intentNext.putExtra("pass", pass);
                intentNext.putExtra("passCheck", passCheck); // 追加
                intentNext.putExtra("name", name);
                intentNext.putExtra("kana", kana);
                intentNext.putExtra("year", year);
                intentNext.putExtra("month", month);
                intentNext.putExtra("day", day);

                startActivity(intentNext);
            }
        });
    }

    // メールアドレスが「@」を含むかどうかをチェックする
    private boolean isValidEmail(CharSequence email) {
        return email != null && email.toString().contains("@");
    }

    // 全角文字が含まれているかをチェックするメソッド
    private boolean containsFullWidthCharacters(String str) {
        for (char c : str.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                return true;
            }
        }
        return false;
    }

    // 生年月日が有効かどうかを検証するメソッド
    private boolean isValidDate(String yearStr, String monthStr, String dayStr) {
        try {
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);

            if (year < 1900 || year > 2024) {
                return false;
            }

            if (month < 1 || month > 12) {
                return false;
            }

            int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

            // 閏年の判定
            if (month == 2 && isLeapYear(year)) {
                daysInMonth[1] = 29;
            }

            return day >= 1 && day <= daysInMonth[month - 1];
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 閏年かどうかを判定するメソッド
    private boolean isLeapYear(int year) {
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                return year % 400 == 0;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
