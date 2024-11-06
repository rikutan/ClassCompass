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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ClassRoomSettingActivity extends AppCompatActivity {
    private final String tag = "CRSActivity";
    Context context = this;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_room_setting);

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");
        Log.d(tag, classRoomID);

        ClassRoomsDatabaseManager.getDetailClassRoom(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
        }, new ClassRoomsDatabaseManager.DetailClassRoomListener() {
            @Override
            public void onClassName(String className) {
                //  classNameを利用する場合
                Log.d(tag, className);
                TextView classNameTextView = findViewById(R.id.class_room_setting_classname);
                classNameTextView.setText(className);
                bundle.putString("className", className);
            }

            @Override
            public void onSchoolName(String schoolName) {
                //  schoolNameを利用する場合
                Log.d(tag, schoolName);
                TextView schoolNameTextView = findViewById(R.id.class_room_setting_schoolname);
                schoolNameTextView.setText(schoolName);
                bundle.putString("schoolName", schoolName);
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
                    bundle.putString("classIcon", iconPath);

                    // 画像をダウンロードするための参照を取得
                    StorageReference imageRef = storageRef.child(iconPath);

                    // 画像をダウンロード
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // 画像が正常にダウンロードされた場合の処理
                            classIconImageView classRoomIcon = findViewById(R.id.class_room_setting_icon);
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
                    bundle.putString("classIcon", "");
                }
            }

            @Override
            public void onYear(String year) {
                //  yearを利用する場合
                Log.d(tag, year);
                TextView yearTextView = findViewById(R.id.class_room_setting_year);
                yearTextView.setText(year);
                bundle.putString("year", year);
            }

            @Override
            public void onClassQR(String classQR) {
                //  classQRを利用する場合
                Log.d(tag, classQR);
                // FirebaseStorageへの参照を取得
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                // 取得したい画像のパスを指定
                String iconPath = "classRooms/" + classRoomID + "/" + classQR;

                // 画像をダウンロードするための参照を取得
                StorageReference imageRef = storageRef.child(iconPath);

                // 画像をダウンロード
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // 画像が正常にダウンロードされた場合の処理
                        classIconImageView classRoomQR = findViewById(R.id.class_room_setting_qr);
                        Glide.with(context).load(uri).into(classRoomQR);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // 画像のダウンロード中にエラーが発生した場合の処理
                    }
                });
            }
        });

        Button backBtn = findViewById(R.id.class_room_setting_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button nextBtn = findViewById(R.id.class_room_setting_nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassRoomSettingActivity.this, ClassRoomSettingChangeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
