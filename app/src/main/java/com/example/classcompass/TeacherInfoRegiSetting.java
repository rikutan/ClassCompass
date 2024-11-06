package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.util.Log;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.TeachersDatabaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class TeacherInfoRegiSetting extends AppCompatActivity {


    private static final String tag = "TeacherInfoRegiSetting";
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.teacher_info_regi_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SharedPreferences sharedPref = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String teacherID = sharedPref.getString("teacherID", null);


          //呼び出し
        TeachersDatabaseManager.getDetailTeacher(teacherID, new TeachersDatabaseManager.TeacherErrorListener() {
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
        }, new TeachersDatabaseManager.TeacherDetailGetListener() {
            @Override
            public void onTeacherName(String teacherName) {
                //  teacherNameを利用する場合

                TextView teacherNameText = findViewById(R.id.teacher_info_regi_setting_nameTxt);
                teacherNameText.setText(teacherName);
                Log.d(tag, teacherName);
            }

            @Override
            public void onTeacherIcon(String teacherIcon) {
                //  teacherIconを利用する場合
                if(teacherIcon != null){
                    //  teacherIconが存在する場合
                    // FirebaseStorageへの参照を取得
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    // 取得したい画像のパスを指定
                    String iconPath = "user_icon/" + teacherIcon;

                    // 画像をダウンロードするための参照を取得
                    StorageReference imageRef = storageRef.child(iconPath);

                    // 画像をダウンロード
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // 画像が正常にダウンロードされた場合の処理
                            IconImageView classRoomIcon = findViewById(R.id.teacher_info_regi_setting_icon);
                            Glide.with(context).load(uri).into(classRoomIcon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // 画像のダウンロード中にエラーが発生した場合の処理
                        }
                    });
                }else {
                    //  teacherIconが存在しない場合
                    Log.d(tag, "teacherIcon is null");
                }
            }

            @Override
            public void onTeacherMail(String teacherMail) {
                //  teacherMailを利用する場合
                TextView teacherMailText = findViewById(R.id.teacher_info_regi_setting_display_mailAddress);
                teacherMailText.setText(teacherMail);
                Log.d(tag, teacherMail);
            }

            @Override
            public void onTeacherBirthday(String teacherBirthday) {
                //  teacherBirthdayを利用する場合
                TextView teacherBirthdayText = findViewById(R.id.teacher_info_regi_setting_display_birthday);
                String year = teacherBirthday.substring(0, 4);
                String month = teacherBirthday.substring(4,6 );
                String day = teacherBirthday.substring(6, 8);
                teacherBirthday = year + "年" + month + "月" + day + "日";

                teacherBirthdayText.setText(teacherBirthday);
                Log.d(tag, teacherBirthday);
            }

            @Override
            public void onTeacherComment(String teacherComment) {
                //  teacherCommentを利用する場合
                TextView teacherSelfIntro = findViewById(R.id.teacher_info_regi_setting_display_selfIntro);
                if(teacherComment != null){
                    //  teacherCommentが存在する場合
                    teacherSelfIntro.setText(teacherComment);
                    Log.d(tag, teacherComment);
                }else {
                    //  teacherCommentが存在しない場合
                    Log.d(tag, "teacherComment is null");
                }
            }
        });

        //編集画面への遷移
        Button editBtn = findViewById(R.id.teacher_info_regi_setting_editBtn);
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this,TeacherInfoRegiEdit.class);
            startActivity(intent);
        });

        //戻るボタン
        TextView backRtn = findViewById(R.id.teacher_info_regi_setting_backTxt);
        backRtn.setOnClickListener(v -> {
            Intent backIntent = new Intent(this,top_teacher.class);
            startActivity(backIntent);
        });



    }
}