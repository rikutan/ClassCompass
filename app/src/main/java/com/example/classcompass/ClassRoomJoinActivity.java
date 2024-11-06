package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;


public class ClassRoomJoinActivity extends AppCompatActivity {
    private final String tag = "ClassRoomJoinActivity";
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_room_join);
        Context context = this;

        //  classRoomIDを取得
        Intent intentGet = getIntent();
        String classRoomID =intentGet.getStringExtra("classRoomID");

        //  クラスルームの情報を取得
        ClassRoomsDatabaseManager.getClassRoom2(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
        }, new ClassRoomsDatabaseManager.ClassRoomListener2() {
            @Override
            public void onClassName(String className) {
                //  classNameを利用する場合
                ClassRoomJoinActivity.this.className = className;
                TextView classNameTxt = findViewById(R.id.classNameTxt);
                classNameTxt.setText(className);
            }

            @Override
            public void onClassIcon(String classIcon) {
                //  classIconを利用する場合
                if(classIcon != null){
                    Log.d(tag, classIcon);
                    // FirebaseStorageへの参照を取得
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    // 取得したい画像のパスを指定
                    String iconPath = "classRooms/" + classRoomID + "/" + classIcon;

                    // 画像をダウンロードするための参照を取得
                    StorageReference imageRef = storageRef.child(iconPath);

                    // 画像をダウンロード
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // 画像が正常にダウンロードされた場合の処理
                            ImageView classRoomIcon = findViewById(R.id.classRoomIcon);
                            Glide.with(context).load(uri).into(classRoomIcon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // 画像のダウンロード中にエラーが発生した場合の処理
                        }
                    });
                }else {
                    Log.d(tag, "classIcon is null");
                }
            }
        });

        Button classRoomJoinBtn = findViewById(R.id.classRoomJoinBtn);
        classRoomJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
                String userClass = sharedPreferences.getString("userClass", "None");
                if(userClass.equals("teacher")){
                    //  教師の場合
                    String teacherID = sharedPreferences.getString("teacherID", "None");
                    ClassRoomsDatabaseManager.teacherJoinClassRoom(classRoomID, teacherID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                        @Override
                        public void onError(String errorMsg) {
                            //  データ登録に失敗した場合
                            TextView errorTxt = findViewById(R.id.errorTxt);
                            errorTxt.setText(errorMsg);
                        }

                        @Override
                        public void onSuccess(String successMsg) {
                            //  データ登録に成功した場合
                            SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("classRoomID", classRoomID);
                            editor.apply();
                            Intent intent = new Intent(ClassRoomJoinActivity.this, JoinCompleteActivity.class);
                            intent.putExtra("className", className);
                            startActivity(intent);
                        }
                    }, new ClassRoomsDatabaseManager.ClassRoomExistsListener() {
                        @Override
                        public void onExists(String existsMsg) {
                            //  すでに存在する場合
                            TextView errorTxt = findViewById(R.id.errorTxt);
                            errorTxt.setText(existsMsg);
                        }
                    });
                }else{
                    //  生徒の場合
                    String studentID = sharedPreferences.getString("studentID", "None");
                    ClassRoomsDatabaseManager.studentJoinClassRoom(classRoomID, studentID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                        @Override
                        public void onError(String errorMsg) {
                            //  データ取得に失敗した場合
                            TextView errorTxt = findViewById(R.id.errorTxt);
                            errorTxt.setText(errorMsg);
                        }

                        @Override
                        public void onSuccess(String successMsg) {
                            //  データ取得に成功した場合
                            SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("classRoomID", classRoomID);
                            editor.apply();
                            Intent intent = new Intent(ClassRoomJoinActivity.this, JoinCompleteActivity.class);
                            intent.putExtra("className", className);
                            startActivity(intent);
                        }
                    }, new ClassRoomsDatabaseManager.ClassRoomExistsListener() {
                        @Override
                        public void onExists(String existsMsg) {
                            //  すでに存在する場合
                            TextView errorTxt = findViewById(R.id.errorTxt);
                            errorTxt.setText(existsMsg);
                        }
                    });

                }

            }
        });












    }
}


