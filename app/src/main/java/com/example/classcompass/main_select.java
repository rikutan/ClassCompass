package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.example.classcompass.databaseManager.StudentsDatabaseManager;
import com.example.classcompass.databaseManager.TeachersDatabaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class main_select extends AppCompatActivity {
    private final String TAG = "main_select";
    private ImageButton t_main_all;
    private boolean isUnread = true;
    private DatabaseReference chatRef;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_select);

        // SharedPreferencesからIDを取得
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        //データの仮入れ
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString("userClass", "Teacher"); // または "Student"
        //editor.putString("teacherID", "Tfa262303-700e-4f9a-baee-85743cf3478e"); // 仮の教師ID
        //editor.putString("classRoomID", "C46c49df5-9284-4e83-b192-a0c8764679aa"); // 仮のクラスルームID
        //削除
        //editor.remove("userClass");
        //editor.remove("teacherID");
        //editor.remove("classRoomID");
        //editor.apply();
        //ここまで
        String userClass = sharedPreferences.getString("userClass", "None");

        //生徒の場合隠すため
        ImageButton setting = findViewById(R.id.main_setting);

        if (userClass.equals("student")) {
            String classRoomID = sharedPreferences.getString("classRoomID", "None");
            String studentID = sharedPreferences.getString("studentID", "None");
            if (!studentID.equals("None")) {
                handleStudent(studentID);
            }
            // 学生の場合は設定ボタンを非表示にする
            setting.setVisibility(View.GONE);

            StudentsDatabaseManager.getStudentMemberID(studentID, classRoomID, new StudentsDatabaseManager.StudentErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    //  データの取得に失敗した場合
                    Log.d(TAG, errorMsg+"pqpqpq");
                }

                @Override
                public void onSuccess(String successMsg) {
                    //  データの取得に成功した場合
                    Log.d(TAG, successMsg);
                }
            }, new StudentsDatabaseManager.StudentMemberIdGetListener() {
                @Override
                public void onStudentMemberID(String classMemberID) {
                    //  classMemberIDを利用する場合
                        Log.d(TAG, "classMemberID"+classMemberID);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("classMemberID", classMemberID);
                        editor.apply();
                }
            });
        } else if (userClass.equals("teacher")) {
            String classRoomID = sharedPreferences.getString("classRoomID", "None");
            String teacherID = sharedPreferences.getString("teacherID", "None");
            if (!teacherID.equals("None")) {
                handleTeacher(teacherID);
            }
            TeachersDatabaseManager.getTeacherMemberID(teacherID, classRoomID, new TeachersDatabaseManager.TeacherErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    //  データの取得に失敗した場合
                    Log.d(TAG, errorMsg+"amama");
                }

                @Override
                public void onSuccess(String successMsg) {
                    //  データの取得に成功した場合
                    Log.d(TAG, successMsg);
                }
            }, new TeachersDatabaseManager.TeacherMemberIdGetListener() {
                @Override
                public void onTeacherMemberID(String classMemberID) {
                    //  classMemberIDを利用する場合
                        Log.d(TAG, "classMemberID"+classMemberID);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("classMemberID", classMemberID);
                        editor.apply();
                }
            });
        }

        // Firebaseリファレンスの初期化
        String classRoomID = sharedPreferences.getString("classRoomID", "None");
//        if (!classRoomID.equals("None")) {
//            chatRef = FirebaseDatabase.getInstance().getReference().child("classRooms").child(classRoomID);
//
//            String classMemberID = sharedPreferences.getString("classMemberID", "None");
//            if (!classMemberID.equals("None")) {
//                ClassRoomsDatabaseManager.getReadClassRoomChat(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//                    @Override
//                    public void onError(String errorMsg) {
//                        // データの取得に失敗した場合
//                        Log.d(TAG, errorMsg);
//                    }
//
//                    @Override
//                    public void onSuccess(String successMsg) {
//                        // データの取得に成功した場合
//                        Log.d(TAG, successMsg);
//                    }
//                }, new ClassRoomsDatabaseManager.getReadListener() {
//                    @Override
//                    public void onRead(Boolean read) {
//                        if (read) {
//                            // 未読がある場合
//                            Log.d(TAG, "unread");
//                            isUnread = true; // 未読フラグをセット
//                            t_main_all.setImageResource(R.drawable.acttb_n); // 未読アイコンを表示
//                        } else {
//                            // すべて既読済みの場合
//                            Log.d(TAG, "Already read");
//                            isUnread = false; // 未読フラグをクリア
//                            t_main_all.setImageResource(R.drawable.acttb); // 既読アイコンを表示
//                        }
//                    }
//                });
//            }
//        }



        //  デバイストークン更新
        String classMemberID = sharedPreferences.getString("classMemberID", "None");
        ClassRoomsDatabaseManager.setDeviceID(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データ登録に失敗した場合
                Log.d(TAG, errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データ登録に成功した場合
                Log.d(TAG, successMsg);
            }
        });
        String chatStatus = sharedPreferences.getString("classTopStatus", "None");
        if(chatStatus.equals("0")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("classTopStatus", "1");
            editor.apply();
            Intent intent = new Intent(main_select.this, main_select.class);
            startActivity(intent);
            finish();
        }

        // 設定変更への遷移
        //ImageButton setting = findViewById(R.id.main_setting);上で定義済み
        setting.setOnClickListener(v -> {
             Intent intent = new Intent(main_select.this, ClassRoomSettingActivity.class);
             startActivity(intent);
        });

        // 戻る
        ImageButton logout = findViewById(R.id.main_back);
        logout.setOnClickListener(v -> {
            Intent intent;
            if(userClass.equals("student")){
                intent = new Intent(main_select.this, top_student.class);
            } else {
                intent = new Intent(main_select.this, top_teacher.class);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("classRoomID");
            editor.remove("classMemberID");
            startActivity(intent);
        });

        // 全体チャットへの遷移
        t_main_all = findViewById(R.id.main_talk);
        t_main_all.setOnClickListener(v -> {
             Intent intent = new Intent(main_select.this, ChatActivity.class);
             startActivity(intent);
            // 未読状態を仮に既読に設定
//            chatRef.child("unread").setValue(false);
        });

        // くじ引きへの遷移
        ImageButton day_btn = findViewById(R.id.main_dice);
        day_btn.setOnClickListener(v -> {
             Intent intent = new Intent(main_select.this, LotteryActivity.class);
             startActivity(intent);
        });

        // アルバムへの遷移
        ImageButton albam_btn = findViewById(R.id.main_albam);
        albam_btn.setOnClickListener(v -> {
             Intent intent = new Intent(main_select.this, AlbumActivity.class);
             startActivity(intent);
        });








    }




    private void handleStudent(String studentID) {
        // 学生の処理
        StudentsDatabaseManager.getChatStudent(studentID, new StudentsDatabaseManager.StudentErrorListener() {

            @Override
            public void onError(String errorMsg) {
                //  データの取得に失敗した場合
                Log.d(TAG, errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの取得に成功した場合
                Log.d(TAG, successMsg);
            }
        }, new StudentsDatabaseManager.ChatStudentGetListener() {
            @Override
            public void onStudentNameAndIcon(String studentName, String studentIcon) {
                TextView t_main_name = findViewById(R.id.main_name);
                t_main_name.setText(studentName);

                if (studentIcon != null) {
                    Log.d(TAG, studentIcon);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    String iconPath = "user_icon/" + studentIcon;
                    StorageReference imageRef = storageRef.child(iconPath);

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageView t_main_icon = findViewById(R.id.main_icon);
                            Glide.with(context).load(uri).into(t_main_icon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "Failed to download image: " + exception.getMessage());
                        }
                    });
                } else {
                    Log.d(TAG, "studentIcon is null");
                }
            }
        });



        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");
        ClassRoomsDatabaseManager.getClassRoom2(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データの取得に失敗した場合
                Log.d(TAG, errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの取得に成功した場合
                Log.d(TAG, successMsg);
            }
        }, new ClassRoomsDatabaseManager.ClassRoomListener2() {
            @Override
            public void onClassName(String className) {
                //  classNameを利用する場合
                TextView t_main_allc = findViewById(R.id.main_allc);
                t_main_allc.setText(className);
            }

            @Override
            public void onClassIcon(String classIcon) {
                //  classIconを利用する場合
                if (classIcon != null) {
                    Log.d(TAG, classIcon);
                    classIconImageView t_main_iconc = findViewById(R.id.main_iconc);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    String iconPath = "classRooms/" + classRoomID + "/" + classIcon;
                    StorageReference imageRef = storageRef.child(iconPath);

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(context).load(uri).into(t_main_iconc);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "Failed to download image: " + exception.getMessage());
                        }
                    });
                } else {
                    Log.d(TAG, "classIcon is null");
                }
            }
        });
    }

    private void handleTeacher(String teacherID) {
        // 先生の処理
        TeachersDatabaseManager.getChatTeacher(teacherID, new TeachersDatabaseManager.TeacherErrorListener() {

            @Override
            public void onError(String errorMsg) {
                //  データの取得に失敗した場合
                Log.d(TAG, errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの取得に成功した場合
                Log.d(TAG, successMsg);
            }
        }, new TeachersDatabaseManager.ChatTeacherGetListener() {
            @Override
            public void onTeacherNameAndIcon(String teacherName, String teacherIcon) {
                TextView t_main_name = findViewById(R.id.main_name);
                t_main_name.setText(teacherName);

                if (teacherIcon != null) {
                    Log.d(TAG, teacherIcon);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    String iconPath = "user_icon/" + teacherIcon;
                    StorageReference imageRef = storageRef.child(iconPath);

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageView t_main_icon = findViewById(R.id.main_icon);
                            Glide.with(context).load(uri).into(t_main_icon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "Failed to download image: " + exception.getMessage());
                        }
                    });
                } else {
                    Log.d(TAG, "teacherIcon is null");
                }
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");
        ClassRoomsDatabaseManager.getClassRoom2(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データの取得に失敗した場合
                Log.d(TAG, errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの取得に成功した場合
                Log.d(TAG, successMsg);
            }
        }, new ClassRoomsDatabaseManager.ClassRoomListener2() {
            @Override
            public void onClassName(String className) {
                //  classNameを利用する場合
                TextView t_main_allc = findViewById(R.id.main_allc);
                t_main_allc.setText(className);
            }

            @Override
            public void onClassIcon(String classIcon) {
                //  classIconを利用する場合
                if (classIcon != null) {
                    Log.d(TAG, classIcon);
                    classIconImageView t_main_iconc = findViewById(R.id.main_iconc);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    String iconPath = "classRooms/" + classRoomID + "/" + classIcon;
                    StorageReference imageRef = storageRef.child(iconPath);

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(context).load(uri).into(t_main_iconc);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "Failed to download image: " + exception.getMessage());
                        }
                    });
                } else {
                    Log.d(TAG, "classIcon is null");
                }
            }
        });

        //  デバイストークン更新
        String classMemberID = sharedPreferences.getString("classMemberID", "None");
        ClassRoomsDatabaseManager.setDeviceID(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データ登録に失敗した場合
                Log.d(TAG, errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データ登録に成功した場合
                Log.d(TAG, successMsg);
            }
        });



    }
}
