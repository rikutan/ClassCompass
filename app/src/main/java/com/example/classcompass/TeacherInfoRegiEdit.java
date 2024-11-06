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
import android.widget.ImageView;
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
import com.example.classcompass.databaseManager.TeachersDatabaseManager;
import com.example.classcompass.firebaseManager.AuthenticationManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class TeacherInfoRegiEdit extends AppCompatActivity {
    private final String tag = "TeacherInfoRegiEdit";
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_info_regi_edit);
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String teacherID = sharedPreferences.getString("teacherID", "None");


        TextView backBtn = findViewById(R.id.teacher_info_regi_edit_backTxt);
        TextView iconChangeBtn = findViewById(R.id.teacher_info_regi_edit_editTxt);
        EditText commentInput = findViewById(R.id.teacher_info_regi_edit_comment);
        EditText mailInput = findViewById(R.id.teacher_info_regi_edit_mailChangeForm);
        Button mailChageBtn = findViewById(R.id.teacher_info_regi_edit_sendBtn);
        Button pwdChageBtn = findViewById(R.id.teacher_info_regi_edit_resettingBtn);
        TextView errorTxt = findViewById(R.id.teacher_info_regi_edit_ana);



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
                    //  teacherIconが存在しない場合
                    Log.d(tag, "teacherIcon is null");
                }
            }

            @Override
            public void onTeacherMail(String teacherMail) {
                //  teacherMailを利用する場合
                Log.d(tag, teacherMail);
                SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("teacherMail", teacherMail);
                editor.apply();
            }

            @Override
            public void onTeacherBirthday(String teacherBirthday) {
                //  teacherBirthdayを利用する場合
                Log.d(tag, teacherBirthday);
            }

            @Override
            public void onTeacherComment(String teacherComment) {
                //  teacherCommentを利用する場合
                if(teacherComment != null){
                    //  teacherCommentが存在する場合
                    Log.d(tag, teacherComment);
                    EditText commentInput = findViewById(R.id.teacher_info_regi_edit_comment);
                    commentInput.setText(teacherComment);
                }else {
                    //  teacherCommentが存在しない場合
                    Log.d(tag, "teacherComment is null");
                }
            }
        });



        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherInfoRegiEdit.this, TeacherInfoRegiSetting.class);
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

                    TeachersDatabaseManager.setTeacherComment(teacherID, comment, new TeachersDatabaseManager.TeacherErrorListener() {
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
            String email = sharedPreferences.getString("teacherMail", "None");
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
                        String teacherID = sharedPreferences.getString("teacherID", "None");

                        // 画像をFirebaseにアップロード
                        TeachersDatabaseManager.setTeacherIcon(teacherID, imageFile, new TeachersDatabaseManager.TeacherErrorListener() {
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