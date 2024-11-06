package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.example.classcompass.databaseManager.StudentsDatabaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class top_student extends AppCompatActivity {
    private static final String TAG = "top_student";
    private static final String tag = "top_student";

    private DatabaseReference databaseReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_student);

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String studentID = sharedPreferences.getString("studentID", "None");

        // Firebaseデータベースの参照を取得
        databaseReference = FirebaseDatabase.getInstance().getReference();


        // 設定ボタンの遷移（未実装）
        ImageButton setting = findViewById(R.id.setting);
        setting.setOnClickListener(v -> {
             Intent intent = new Intent(top_student.this, StudentInfoRegiSetting.class);
             startActivity(intent);
        });

        // ログアウトボタンの遷移（未実装）
        ImageButton logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("studentID");
            editor.remove("userClass");
            editor.apply();
            Intent intent = new Intent(top_student.this, MainActivity.class);
            startActivity(intent);
        });


        // QRボタンの遷移
        ImageButton qr = findViewById(R.id.QR);
        qr.setOnClickListener(v -> {
            Intent intent = new Intent(top_student.this, QrReadActivity.class);
            startActivity(intent);
        });


            StudentsDatabaseManager.getStudent(studentID, new StudentsDatabaseManager.StudentErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データの取得に失敗した場合
                Log.d(tag, "Error getting student: " + errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの取得に成功した場合
                Log.d(tag, "Student data retrieved successfully: " + successMsg);
            }
        }, new StudentsDatabaseManager.StudentGetListener() {
            @Override
            public void onStudentName(String studentName) {
                //  studentNameを利用する場合
                if (studentName != null) {
                    Log.d(tag, studentName);
                    TextView t_name = findViewById(R.id.s_name);
                    t_name.setText(studentName);
                } else {
                    Log.d(tag, "studentName is null");
                }
            }

            @Override
            public void onStudentIcon(String studentIcon) {
                //  studentIconを利用する場合
                if(studentIcon != null){
                    Log.d(tag, studentIcon);
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
                            IconImageView classRoomIcon = findViewById(R.id.icon);
                            Glide.with(context).load(uri).into(classRoomIcon);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // 画像のダウンロード中にエラーが発生した場合の処理
                            Log.e(TAG, "Failed to download image: " + exception.getMessage());
                        }
                    });
                }else {
                    Log.d(tag, "classIcon is null");
                }
            }
            @Override
            public void onClassRooms(List<String> classRooms) {
                //  classRoomsを利用する場合
                if(classRooms != null){
                    for(String classRoom : classRooms){
                            ClassRoomsDatabaseManager.getClassRoom(classRoom, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                //  データの取得に失敗した場合
                                Log.e(tag, "ClassRoom Error: " + errorMsg);
                            }

                            @Override
                            public void onSuccess(String successMsg) {
                                //  データの取得に成功した場合
                                Log.d(tag, "ClassRoom Success: " + successMsg);
                            }
                        }, new ClassRoomsDatabaseManager.ClassRoomListener() {
                            @Override
                            public void onClassRoom(String className, String schoolName, String classIcon, String year) {
                                //  classRoomを利用する場合
                                addClassView(classRoom, className, schoolName, year , classIcon);
                            }
                        });
                    }
                }else {
                    //  classRoomsが存在しない場合
                    Log.d(tag, "classRooms are null");
                }
            }
        });

            //データを入れる

    }




    private void addClassView(String classRoomID, String className, String schoolName, String year , String classIcon) {
        View classView = LayoutInflater.from(this).inflate(R.layout.class_item, null);

        TextView classNameTextView = classView.findViewById(R.id.class_id1);
        classNameTextView.setText(className);

        TextView schoolNameTextView = classView.findViewById(R.id.school_name);
        schoolNameTextView.setText(schoolName);

        classIconImageView classIconImg = classView.findViewById(R.id.classIconI);
        if (classIcon != null) {
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
                    Glide.with(context).load(uri).into(classIconImg);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // 画像のダウンロード中にエラーが発生した場合の処理
                }
            });
        } else {
            Log.d(TAG, "classIcon is null");
        }

        TextView yearTextView = classView.findViewById(R.id.year);
        yearTextView.setText(year + "年度");


        Button classButton = classView.findViewById(R.id.class_button);
        classButton.setOnClickListener(v -> {
            // クラスアイテムがクリックされたときの処理
            SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("classRoomID", classRoomID);
            editor.apply();
            Intent intent = new Intent(top_student.this, main_select.class);
            startActivity(intent);
        });

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        classView.setLayoutParams(params);

        GridLayout classContainer = findViewById(R.id.class_container);
        classContainer.addView(classView);


    }
}
