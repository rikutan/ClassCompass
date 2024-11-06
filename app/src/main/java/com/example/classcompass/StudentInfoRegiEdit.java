package com.example.classcompass;

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
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.StudentsDatabaseManager;
import com.example.classcompass.databaseManager.TeachersDatabaseManager;
import com.example.classcompass.firebaseManager.AuthenticationManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class StudentInfoRegiEdit extends AppCompatActivity {
    private final String tag = "StudentInfoRegiEdit";
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_info_regi_edit);
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String studentID = sharedPreferences.getString("studentID", "None");


        TextView backBtn = findViewById(R.id.student_info_regi_edit_backTxt);
        TextView iconChangeBtn = findViewById(R.id.student_info_regi_edit_editTxt);
        EditText commentInput = findViewById(R.id.student_info_regi_edit_comment);
        EditText mailInput = findViewById(R.id.student_info_regi_edit_mailChangeForm);
        Button mailChageBtn = findViewById(R.id.student_info_regi_edit_sendBtn);
        Button pwdChageBtn = findViewById(R.id.student_info_regi_edit_resettingBtn);
        TextView errorTxt = findViewById(R.id.student_info_regi_edit_ana);


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
                            IconImageView icon = findViewById(R.id.teacher_info_regi_edit_editImg);
                            Glide.with(context).load(uri).into(icon);
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
                Log.d(tag, studentMail);
                SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("studentMail", studentMail);
                editor.apply();
            }

            @Override
            public void onStudentBirthday(String studentBirthday) {
                //  studentBirthdayを利用する場合
                Log.d(tag, studentBirthday);
            }

            @Override
            public void onStudentComment(String studentComment) {
                //  studentCommentを利用する場合
                if(studentComment != null){
                    //  studentCommentが存在する場合
                    Log.d(tag, studentComment);
                    EditText commentInput = findViewById(R.id.student_info_regi_edit_comment);
                    commentInput.setText(studentComment);
                }else {
                    //  studentCommentが存在しない場合
                    Log.d(tag, "studentComment is null");
                }
            }
        });





        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StudentInfoRegiEdit.this, StudentInfoRegiSetting.class);
            startActivity(intent);
        });

        iconChangeBtn.setOnClickListener(v -> {
            mGetContentLauncher.launch("image/*");
        });

        commentInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String comment = commentInput.getText().toString();
                    StudentsDatabaseManager.setStudentComment(studentID, comment, new StudentsDatabaseManager.StudentErrorListener() {
                        @Override
                        public void onError(String errorMsg) {
                            //  データの登録に失敗した場合
                            Log.d(tag, errorMsg);
                        }

                        @Override
                        public void onSuccess(String successMsg) {
                            //  データの登録に成功した場合
                            Log.d(tag, successMsg);
                        }
                    });
                }
            }
        });

        mailChageBtn.setOnClickListener(v -> {
            String email = mailInput.getText().toString();
            if(email.equals("")){
                errorTxt.setText("新しいメールアドレスを入力してください。");
                errorTxt.setTextColor(ContextCompat.getColor(context, R.color.red));
                errorTxt.setVisibility(View.VISIBLE);
            }else {
                AuthenticationManager.changeEmail(email, new AuthenticationManager.AuthenticationErrorListener() {
                    @Override
                    public void onError(String errorMsg) {
                        //  データの変更に失敗した場合
                        Log.d(tag, errorMsg);
                    }

                    @Override
                    public void onSuccess(String successMsg) {
                        //  データの変更に成功した場合
                        Log.d(tag, successMsg);
                        errorTxt.setText("メールアドレス変更のリンクを送付しました");
                        errorTxt.setTextColor(ContextCompat.getColor(context, R.color.green));
                        errorTxt.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        pwdChageBtn.setOnClickListener(v -> {
            String email = sharedPreferences.getString("studentMail", "None");
            AuthenticationManager.emailAuth(email, new AuthenticationManager.AuthenticationErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    //  リンク送付に失敗した場合
                    Log.d(tag, errorMsg);
                }

                @Override
                public void onSuccess(String successMsg) {
                    //  リンク送付に成功した場合
                    Log.d(tag, successMsg);
                    errorTxt.setText("パスワード変更のリンクを送付しました");
                    errorTxt.setTextColor(ContextCompat.getColor(context, R.color.green));
                    errorTxt.setVisibility(View.VISIBLE);
                }
            });

        });
    }

    private final ActivityResultLauncher<String> mGetContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        // 画像のUriを取得
                        File imageFile = new File(getRealPathFromURI(result));
                        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
                        String studentID = sharedPreferences.getString("studentID", "None");

                        // 画像をFirebaseにアップロード
                        StudentsDatabaseManager.setStudentIcon(studentID, imageFile, new StudentsDatabaseManager.StudentErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                Log.d(tag, errorMsg);
                            }

                            @Override
                            public void onSuccess(String successMsg) {
                                Log.d(tag, successMsg);
                            }
                        });
                    }
                }
            });

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String realPath = cursor.getString(columnIndex);
            cursor.close();
            return realPath;
        } else {
            return uri.getPath();
        }
    }
}