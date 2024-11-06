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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.example.classcompass.databaseManager.StudentsDatabaseManager;
import com.example.classcompass.databaseManager.TeachersDatabaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassRoomSettingChangeActivity extends AppCompatActivity {
    private final String tag = "CRSCActivity";

    Context context = this;
    private Intent intent;
    private RecyclerView recyclerView;
    private ClassRoomSettingChangeAdapter adapter;
    private List<CRSCAdapterItem> itemList;
    String className;
    String schoolName;
    String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_room_setting_change);
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        //  メンバー情報取得
        ClassRoomsDatabaseManager.getClassMemberID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
        }, new ClassRoomsDatabaseManager.ClassMemberGetListener() {

            @Override
            public void onClassMembers(List<String> classMembers) {
                // classMembersを利用する場合
                if(classMembers != null){
                    //  classMembersが存在する場合
                    for(String classMemberID: classMembers){
                        Log.d(tag, classMemberID);
                        ClassRoomsDatabaseManager.getMemberID(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
                        }, new ClassRoomsDatabaseManager.memberIDGetListener() {
                            @Override
                            public void onMemberID(String memberID) {
                                //  memberIDを利用する場合
                                Log.d(tag, memberID);
                                String memberClass = memberID.substring(0, 1);
                                if(memberClass.equals("T")){
                                    TeachersDatabaseManager.getChatTeacher(memberID, new TeachersDatabaseManager.TeacherErrorListener() {

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
                                            }, new TeachersDatabaseManager.ChatTeacherGetListener() {
                                                @Override
                                                public void onTeacherNameAndIcon(String teacherName, String teacherIcon) {
                                                    Log.d(tag, classMemberID+teacherName + teacherIcon);
                                                    itemList.add(new CRSCAdapterItem(classMemberID, teacherIcon, teacherName));
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                }else {
                                    StudentsDatabaseManager.getChatStudent(memberID, new StudentsDatabaseManager.StudentErrorListener() {

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
                                            }, new StudentsDatabaseManager.ChatStudentGetListener() {
                                                @Override
                                                public void onStudentNameAndIcon(String studentName, String studentIcon) {
                                                    Log.d(tag, classMemberID +studentIcon + studentName);
                                                    itemList.add(new CRSCAdapterItem(classMemberID, studentIcon, studentName));
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                }
                            }
                        });
                    }
                }else {
                    //  classMembersが存在しない場合
                    Log.d(tag, "classMembers are null");
                }
            }
        });
        adapter = new ClassRoomSettingChangeAdapter(this, itemList);
        recyclerView.setAdapter(adapter);



        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            className = bundle.getString("className");
            schoolName = bundle.getString("schoolName");
            String classIcon = bundle.getString("classIcon");
            year = bundle.getString("year");

            EditText classNameEdit = findViewById(R.id.class_room_setting_change_classNameInput);
            classNameEdit.setText(className);
            EditText schoolNameEdit = findViewById(R.id.class_room_setting_change_schoolNameInput);
            schoolNameEdit.setText(schoolName);
            EditText yearEdit = findViewById(R.id.class_room_setting_change_yearInput);
            yearEdit.setText(year);

            if(!Objects.equals(classIcon, "")){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                // 画像をダウンロードするための参照を取得
                StorageReference imageRef = storageRef.child(classIcon);

                // 画像をダウンロード
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // 画像が正常にダウンロードされた場合の処理
                        classIconImageView classRoomIcon = findViewById(R.id.class_room_setting_change_icon);
                        Glide.with(context).load(uri).into(classRoomIcon);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // 画像のダウンロード中にエラーが発生した場合の処理
                    }
                });

            }

        }


        //  ルームの削除確認
        Button deleteClassRoomBtn = findViewById(R.id.class_room_setting_change_classDeleteCheckBtn);
        deleteClassRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View deleteCheckView = findViewById(R.id.class_room_setting_change_classDeleteCheck);
                deleteCheckView.setVisibility(View.VISIBLE);
                TextView deleteCheckInfo = findViewById(R.id.class_room_setting_change_classDeleteCheckInfo);
                deleteCheckInfo.setVisibility(View.VISIBLE);
                Button deleteCancelBtn = findViewById(R.id.class_room_setting_change_classDeleteCancelBtn);
                deleteCancelBtn.setVisibility(View.VISIBLE);
                Button deleteBtn = findViewById(R.id.class_room_setting_change_classDeleteBtn);
                deleteBtn.setVisibility(View.VISIBLE);


                LinearLayout liner = findViewById(R.id.class_room_setting_change_linear);
                int childCount = liner.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = liner.getChildAt(i);
                    child.setEnabled(false);
                }
                EditText classNameEdit = findViewById(R.id.class_room_setting_change_classNameInput);
                classNameEdit.setFocusable(false);
                classNameEdit.setFocusableInTouchMode(false);
                EditText yearEdit = findViewById(R.id.class_room_setting_change_yearInput);
                yearEdit.setFocusable(false);
                yearEdit.setFocusableInTouchMode(false);
                EditText schoolNameEdit = findViewById(R.id.class_room_setting_change_schoolNameInput);
                schoolNameEdit.setFocusable(false);
                schoolNameEdit.setFocusableInTouchMode(false);
                Button deleteClassRoomBtn = findViewById(R.id.class_room_setting_change_classDeleteCheckBtn);
                deleteClassRoomBtn.setFocusable(false);
                deleteClassRoomBtn.setFocusableInTouchMode(false);


            }
        });

        //  キャンセルボタン
        Button deleteCancelBtn = findViewById(R.id.class_room_setting_change_classDeleteCancelBtn);
        deleteCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View deleteCheckView = findViewById(R.id.class_room_setting_change_classDeleteCheck);
                deleteCheckView.setVisibility(View.INVISIBLE);
                TextView deleteCheckInfo = findViewById(R.id.class_room_setting_change_classDeleteCheckInfo);
                deleteCheckInfo.setVisibility(View.INVISIBLE);
                Button deleteCancelBtn = findViewById(R.id.class_room_setting_change_classDeleteCancelBtn);
                deleteCancelBtn.setVisibility(View.INVISIBLE);
                Button deleteBtn = findViewById(R.id.class_room_setting_change_classDeleteBtn);
                deleteBtn.setVisibility(View.INVISIBLE);


                LinearLayout liner = findViewById(R.id.class_room_setting_change_linear);
                int childCount = liner.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = liner.getChildAt(i);
                    child.setEnabled(true);
                }
                EditText classNameEdit = findViewById(R.id.class_room_setting_change_classNameInput);
                classNameEdit.setFocusable(true);
                classNameEdit.setFocusableInTouchMode(true);
                EditText yearEdit = findViewById(R.id.class_room_setting_change_yearInput);
                yearEdit.setFocusable(true);
                yearEdit.setFocusableInTouchMode(true);
                EditText schoolNameEdit = findViewById(R.id.class_room_setting_change_schoolNameInput);
                schoolNameEdit.setFocusable(true);
                schoolNameEdit.setFocusableInTouchMode(true);
                Button deleteClassRoomBtn = findViewById(R.id.class_room_setting_change_classDeleteCheckBtn);
                deleteClassRoomBtn.setFocusable(true);
                deleteClassRoomBtn.setFocusableInTouchMode(true);

            }
        });

        //  ルームを削除
        Button deleteBtn = findViewById(R.id.class_room_setting_change_classDeleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassRoomsDatabaseManager.deleteClassRoom(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                    @Override
                    public void onError(String errorMsg) {
                        //  データの削除に失敗した場合
                        Log.d(tag, errorMsg);
                    }

                    @Override
                    public void onSuccess(String successMsg) {
                        //  データの削除に成功した場合
                        Log.d(tag, successMsg);
                        Intent intent = new Intent(ClassRoomSettingChangeActivity.this, top_teacher.class);
                        startActivity(intent);
                    }
                });
            }
        });




        //  クラスアイコンの変更
        ImageView changeBtn = findViewById(R.id.class_room_setting_change_film);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContentLauncher.launch("image/*");
            }
        });



        //  編集実行
        Button backBtn = findViewById(R.id.class_room_setting_change_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String changeClassName = ((EditText)findViewById(R.id.class_room_setting_change_classNameInput)).getText().toString();
                String changeSchoolName = ((EditText)findViewById(R.id.class_room_setting_change_schoolNameInput)).getText().toString();
                String changeYear = ((EditText)findViewById(R.id.class_room_setting_change_yearInput)).getText().toString();

                if(!Objects.equals(className, changeClassName) || !Objects.equals(schoolName, changeSchoolName) || !Objects.equals(year, changeYear)){
                    if (!Objects.equals(className, changeClassName)){
                        ClassRoomsDatabaseManager.setClassName(classRoomID, changeClassName, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                //  データ登録に失敗した場合
                                Log.d(tag, errorMsg);
                            }

                            @Override
                            public void onSuccess(String successMsg) {
                                //  データ登録に成功した場合
                                Log.d(tag, successMsg);
                            }
                        });
                    }if(!Objects.equals(schoolName, changeSchoolName)){
                        ClassRoomsDatabaseManager.setSchoolName(classRoomID, changeSchoolName, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                //  データ登録に失敗した場合
                                Log.d(tag, errorMsg);
                            }

                            @Override
                            public void onSuccess(String successMsg) {
                                //  データ登録に成功した場合
                                Log.d(tag, successMsg);
                            }
                        });
                    }if(!Objects.equals(year, changeYear)){
                        ClassRoomsDatabaseManager.setYear(classRoomID, changeYear, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                //  データ登録に失敗した場合
                                Log.d(tag, errorMsg);
                            }

                            @Override
                            public void onSuccess(String successMsg) {
                                //  データ登録に成功した場合
                                Log.d(tag, successMsg);
                            }
                        });
                    }
                }
                finish();
            }
        });


    }

    private final ActivityResultLauncher<String> mGetContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        // 画像のUriを取得
                        File classIcon = new File(getRealPathFromURI(result));

                        // 画像をFirebaseにアップロード
                        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
                        String classRoomID = sharedPreferences.getString("classRoomID", "None");
                        ClassRoomsDatabaseManager.setClassIcon(classRoomID, classIcon, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                Log.d(tag, errorMsg);
                            }

                            @Override
                            public void onSuccess(String successMsg) {
                                Log.d(tag, successMsg);

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();

                                // 画像をダウンロードするための参照を取得
                                StorageReference imageRef = storageRef.child("classRooms/"+classRoomID+"/"+classIcon.getName());

                                // 画像をダウンロード
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // 画像が正常にダウンロードされた場合の処理
                                        classIconImageView classRoomIcon = findViewById(R.id.class_room_setting_change_icon);
                                        Glide.with(context).load(uri).into(classRoomIcon);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // 画像のダウンロード中にエラーが発生した場合の処理
                                    }
                                });
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
