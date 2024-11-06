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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.example.classcompass.databaseManager.TeachersDatabaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class top_teacher extends AppCompatActivity {
    private static final String TAG = "top_teacher";
    private static final String tag = "top_teacher";

    private DatabaseReference databaseReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_teacher);

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String teacherID = sharedPreferences.getString("teacherID", "None");

        // Firebaseデータベースの参照を取得
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // 設定,ログアウトボタン
        ImageButton setting = findViewById(R.id.setting);
        ImageButton logout = findViewById(R.id.logout);

        // 設定ボタンの遷移（未実装）
        setting.setOnClickListener(v -> {
             Intent intent = new Intent(top_teacher.this, TeacherInfoRegiSetting.class);
             startActivity(intent);
        });

        // ログアウトボタンの遷移（未実装）
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("teacherID");
            editor.remove("userClass");
            editor.apply();
            Intent intent = new Intent(top_teacher.this, MainActivity.class);
            startActivity(intent);
        });

        // クラス作成ボタンの遷移
        ImageButton createClass = findViewById(R.id.create_class);
        createClass.setOnClickListener(v -> {
            Intent intent = new Intent(top_teacher.this, NewClassRoomInfoInput.class);
            startActivity(intent);
        });

        // QRボタンの遷移
        ImageButton qr = findViewById(R.id.QR);
        qr.setOnClickListener(v -> {
            Intent intent = new Intent(top_teacher.this, QrReadActivity.class);
        });



            TeachersDatabaseManager.getTeacher(teacherID, new TeachersDatabaseManager.TeacherErrorListener() {
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
        }, new TeachersDatabaseManager.TeacherGetListener() {
            @Override
            public void onTeacherName(String teacherName) {
                if (teacherName != null) {
                    //  teacherNameを利用する場合
                    Log.d(tag, teacherName);
                    TextView t_name = findViewById(R.id.s_name);
                    t_name.setText(teacherName);
                } else {
                    Log.d(tag, "teacherName is null");
                }
            }

            @Override
            public void onTeacherIcon(String teacherIcon) {
                //  teacherIconを利用する場合
                if(teacherIcon != null){
                    Log.d(tag, teacherIcon);
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
                            IconImageView classRoomIcon = findViewById(R.id.icon);
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

            @Override
            public void onClassRooms(List<String> classRooms) {
                //  classRoomsを利用する場合
                if(classRooms != null){
                    //  classRoomsが存在する場合
                    for(String classRoom : classRooms){
                            ClassRoomsDatabaseManager.getClassRoom(classRoom, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
            Intent intent = new Intent(top_teacher.this, main_select.class);
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
