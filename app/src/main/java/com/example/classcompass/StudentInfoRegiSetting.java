package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.StudentsDatabaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StudentInfoRegiSetting extends AppCompatActivity {

    private static final String tag = "StudentInfoRegiSetting";
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.student_info_regi_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SharedPreferences sharedPref = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String studentID = sharedPref.getString("studentID", null);



              //呼び出し
        StudentsDatabaseManager.getDetailStudent(studentID, new StudentsDatabaseManager.StudentErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データの取得に失敗した場合
                Log.d(tag, errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの取得に成功した場合
                Log.d(tag, successMsg);
            }
        }, new StudentsDatabaseManager.StudentDetailGetListener() {
            @Override
            public void onStudentName(String studentName) {
                //  studentNameを利用する場合
                TextView studentNameText = findViewById(R.id.student_info_regi_setting_nameTxt);
                studentNameText.setText(studentName);
                Log.d(tag, studentName);
            }

            @Override
            public void onStudentIcon(String studentIcon) {
                //  studentIconを利用する場合
                if(studentIcon != null){
                    //  studentIconが存在する場合
                    // FirebaseStorageへの参照を取得
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    // 取得したい画像のパスを指定
                    String iconPath = "user_icon/" + studentIcon;

                    // 画像をダウンロードするための参照を取得
                    StorageReference imageRef = storageRef.child(iconPath);

                    // 画像をダウンロード
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // 画像が正常にダウンロードされた場合の処理
                            IconImageView classRoomIcon = findViewById(R.id.student_info_regi_setting_icon);
                            Glide.with(context).load(uri).into(classRoomIcon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // 画像のダウンロード中にエラーが発生した場合の処理
                        }
                    });
                }else {
                    //  studentIconが存在しない場合
                    Log.d(tag, "studentIcon is null");
                }
            }

            @Override
            public void onStudentMail(String studentMail) {
                //  studentMailを利用する場合
                TextView studentMailText = findViewById(R.id.student_info_regi_setting_display_mailAddress);
                studentMailText.setText(studentMail);
                Log.d(tag, studentMail);
            }

            @Override
            public void onStudentBirthday(String studentBirthday) {
                //  studentBirthdayを利用する場合
                TextView studentBirthdayText = findViewById(R.id.student_info_regi_setting_display_birthday);
                String year = studentBirthday.substring(0, 4);
                String month = studentBirthday.substring(4,6 );
                String day = studentBirthday.substring(6, 8);
                studentBirthday = year + "年" + month + "月" + day + "日";

                studentBirthdayText.setText(studentBirthday);
                Log.d(tag, studentBirthday);
            }

            @Override
            public void onStudentComment(String studentComment) {
                //  studentCommentを利用する場合
                TextView studentSelfIntro = findViewById(R.id.student_info_regi_setting_display_selfIntro);
                if(studentComment != null){
                    studentSelfIntro.setText(studentComment);
                    //  studentCommentが存在する場合
                    Log.d(tag, studentComment);
                }else {
                    //  studentCommentが存在しない場合
                    Log.d(tag, "studentComment is null");
                }
            }
        });


        //編集画面への遷移
        Button editBtn = findViewById(R.id.student_info_regi_setting_editBtn);
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this,StudentInfoRegiEdit.class);
            startActivity(intent);
        });

        //戻るボタン
        TextView backRtn = findViewById(R.id.student_info_regi_setting_backTxt);
        backRtn.setOnClickListener(v -> {
            Intent backIntent = new Intent(this,top_student.class);
            startActivity(backIntent);
        });


    }
}