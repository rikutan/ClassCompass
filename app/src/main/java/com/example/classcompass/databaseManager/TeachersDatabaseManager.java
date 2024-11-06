package com.example.classcompass.databaseManager;


import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

//  firebaseRealtimeDatabase
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import com.example.classcompass.dataModels.TeachersDataModel;
import com.example.classcompass.firebaseManager.AuthenticationManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeachersDatabaseManager {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private  static final DatabaseReference teachers = database.getReference("teachers");
    private  static final String tag = "TeachersDBM";
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final StorageReference userIconStorage = storage.getReference("user_icon");


    public interface TeacherErrorListener {
        void onError(String errorMsg);
        void onSuccess(String successMsg);
    }



    public interface TeacherGetListener {
        void onTeacherName(String teacherName);
        void onTeacherIcon(String teacherIcon);
        void onClassRooms(List<String> classRooms);
    }




    public interface TeacherDetailGetListener {
        void onTeacherName(String teacherName);
        void onTeacherIcon(String teacherIcon);
        void onTeacherMail(String teacherMail);
        void onTeacherBirthday(String teacherBirthday);
        void onTeacherComment(String teacherComment);
    }



    public interface TeacherProfileGetListener {
        void onTeacherName(String teacherName);
        void onTeacherIcon(String teacherIcon);
        void onTeacherBirthday(String teacherBirthday);
        void onTeacherComment(String teacherComment);
    }



    public interface ChatTeacherGetListener {
        void onTeacherNameAndIcon(String teacherName, String teacherIcon);
    }


    public interface TeacherJoinClassRoomGetListener {
        void onClassRooms(List<String> classRooms);
    }


    public interface TeacherMemberIdGetListener {
        void onTeacherMemberID(String classMemberID);
    }



    //  TI01  //////////
    public static void setTeacher(String pwd, String teacherName, String teacherNameKana, String teacherMail, String teacherBirthday, TeacherErrorListener TeacherErrorListener) {

        teachers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String teacherID = "T" + UUID.randomUUID().toString();

                TeachersDataModel Teacher = new TeachersDataModel();
                Teacher.setTeacherName(teacherName);
                Teacher.setTeacherNameKana(teacherNameKana);
                Teacher.setTeacherMail(teacherMail);
                Teacher.setTeacherBirthday(teacherBirthday);

                teachers.child(teacherID).setValue(Teacher, (error, ref) -> {
                    if (error != null) {
                        // データ書き込み失敗時
                        Log.d(tag, "TI01 error");
                        String errorMsg = "接続に失敗しました";
                        TeacherErrorListener.onError(errorMsg);
                    }else {
                        //  データの書き込みに成功した場合
                        AuthenticationManager.setUser(teacherMail, pwd, teacherID, new AuthenticationManager.AuthenticationErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                TeacherErrorListener.onError(errorMsg);
                                TeachersDatabaseManager.deleteTeacher(teacherID);
                            }
                            @Override
                            public void onSuccess(String successMsg) {
                                TeacherErrorListener.onSuccess(successMsg);
                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得失敗時
                Log.d(tag, "TI01 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    TeachersDatabaseManager.setTeacher(pwd, teacherName, teacherNameKana, teacherMail, teacherBirthday, new TeachersDatabaseManager.TeacherErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  新規登録に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//        @Override
//        public void onSuccess(String SuccessMsg) {
//            //  新規登録に成功した場合
//            Log.d(tag, SuccessMsg);
//        }
//    });






    //  TI02  //////////
    public static void setJoinClassRoom(String teacherID, String joinClassRoomID, String classMemberID, TeacherErrorListener TeacherErrorListener){
        teachers.child(teacherID).child("classRooms").child(joinClassRoomID).setValue(classMemberID, ((error, ref) -> {
            if (error != null){
                //  データの書き込みに失敗した場合
                Log.d(tag, "TI02 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }else {
                //  データの書き込みに成功した場合
                String successMsg = "接続に成功しました";
                TeacherErrorListener.onSuccess(successMsg);
            }
        }));
    }

    //  呼び出し
//    TeachersDatabaseManager.setJoinClassRoom(teacherID, classRoomID, classMemberID, new TeachersDatabaseManager.TeacherErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            // データ書き込み失敗時
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            // データ書き込み成功時
//        }
//    });







    //  TU01  //////////
    public static void setTeacherIcon(String teacherID, File teacherIcon, TeacherErrorListener TeacherErrorListener) {

        // ファイルをFirebase Storageにアップロード
        StorageReference path = userIconStorage.child(teacherIcon.getName());
        uploadFileToFirebaseStorage(teacherIcon, path, new TeacherErrorListener() {
            @Override
            public void onError(String errorMsg) {
                Log.d(tag, "TU01 error");
                TeacherErrorListener.onError(errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                teachers.child(teacherID).child("teacherIcon").setValue(teacherIcon.getName(), (error, ref) -> {
                    if (error != null) {
                        Log.d(tag, "TU01 error");
                        String errorMsg = "接続に失敗しました";
                        TeacherErrorListener.onError(errorMsg);
                    } else {
                        TeacherErrorListener.onSuccess(successMsg);
                    }
                });
            }
        });
    }

    private static void uploadFileToFirebaseStorage(File file, StorageReference path, TeacherErrorListener TeacherErrorListener) {
        if (file != null) {
            path.putFile(Uri.fromFile(file))
                    .addOnSuccessListener(taskSnapshot -> {
                        // アップロードに成功した場合
                        TeacherErrorListener.onSuccess("アップロードに成功しました");
                    })
                    .addOnFailureListener(e -> {
                        // アップロードに失敗した場合
                        TeacherErrorListener.onError("アップロードに失敗しました");
                    });
        }
    }

//      呼び出し
//    //  Buttonイベント内に配置↓
//    mGetContentLauncher.launch("image/*");
//    //  onCreate()の外に配置↓
//    private final ActivityResultLauncher<String> mGetContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri result) {
//                    if (result != null) {
//                        // 画像のUriを取得
//                        File imageFile = new File(getRealPathFromURI(result));
//
//                        // 画像をFirebaseにアップロード
//                        TeachersDatabaseManager.setTeacherIcon(teacherID, imageFile, new TeachersDatabaseManager.TeacherErrorListener() {
//                            @Override
//                            public void onError(String errorMsg) {
//                                Log.d(tag, errorMsg);
//                            }
//
//                            @Override
//                            public void onSuccess(String successMsg) {
//                                Log.d(tag, successMsg);
//                            }
//                        });
//                    }
//                }
//            });
//
//    private String getRealPathFromURI(Uri uri) {
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(projection[0]);
//            String realPath = cursor.getString(columnIndex);
//            cursor.close();
//            return realPath;
//        } else {
//            return uri.getPath();
//        }
//    }








    //  TU02  //////////
    public static void setTeacherComment(String teacherID, String teacherComment, TeacherErrorListener TeacherErrorListener){
        teachers.child(teacherID).child("teacherComment").setValue(teacherComment, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                Log.d(tag, "TU02 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }else {
                //  データの書き込みに成功した場合
                String successMsg = "接続に成功しました";
                TeacherErrorListener.onSuccess(successMsg);
            }
        });
    }

    // 呼び出し
//    TeachersDatabaseManager.setTeacherComment(teacherID, teacherComment, new TeachersDatabaseManager.TeacherErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データの登録に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データの登録に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });






    //  TU03  //////////
    public static void setTeacherMail(String teacherID, String teacherMail, TeacherErrorListener TeacherErrorListener){
        AuthenticationManager.changeEmail(teacherMail, new AuthenticationManager.AuthenticationErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データの変更に失敗した場合
                TeacherErrorListener.onError(errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの変更に成功した場合
                teachers.child(teacherID).child("teacherMail").setValue(teacherMail, (error, ref) -> {
                    if (error != null) {
                        // データ書き込み失敗時
                        Log.d(tag, "TU03 error");
                        String errorMsg = "接続に失敗しました";
                        TeacherErrorListener.onError(errorMsg);
                    }else {
                        //  データの書き込みに成功した場合
                        String successMsg2 = "接続に成功しました";
                        TeacherErrorListener.onSuccess(successMsg2);
                    }
                });
            }
        });
    }

    //  呼び出し
//    TeachersDatabaseManager.setTeacherMail(teacherID, newTeacherMail, new TeachersDatabaseManager.TeacherErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データの変更に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データの変更に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });












    //  TS01  //////////
    public static void getTeacher(String teacherID, TeacherErrorListener TeacherErrorListener, TeacherGetListener TeacherGetListener){
        teachers.child(teacherID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    TeachersDataModel teachersDataModel = snapshot.getValue(TeachersDataModel.class);
                    assert teachersDataModel != null;
                    String teacherName = teachersDataModel.getTeacherName();
                    TeacherGetListener.onTeacherName(teacherName);
                    String teacherIcon = teachersDataModel.getTeacherIcon();
                    TeacherGetListener.onTeacherIcon(teacherIcon);

                    List<String> classRooms = new ArrayList<>();
                    if (snapshot.child("classRooms").exists() && snapshot.child("classRooms").getChildrenCount() > 0) {
                        for (DataSnapshot classSnapshot : snapshot.child("classRooms").getChildren()) {
                            String joinClassRoomID = classSnapshot.getKey();
                            classRooms.add(joinClassRoomID);
                        }
                    }else {
                        classRooms = null;
                    }
                    TeacherGetListener.onClassRooms(classRooms);
                    String successMsg = "接続に成功しました";
                    TeacherErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "TS01 error");
                    String errorMsg = "接続に失敗しました";
                    TeacherErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "TS01 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    TeachersDatabaseManager.getTeacher(teacherID, new TeachersDatabaseManager.TeacherErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データの取得に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データの取得に成功した場合
//            Log.d(tag, successMsg);
//        }
//    }, new TeachersDatabaseManager.TeacherGetListener() {
//        @Override
//        public void onTeacherName(String teacherName) {
//            //  teacherNameを利用する場合
//            Log.d(tag, teacherName);
//        }
//
//        @Override
//        public void onTeacherIcon(String teacherIcon) {
//            //  teacherIconを利用する場合
//            if(teacherIcon != null){
//                //  teacherIconが存在する場合
//                Log.d(tag, teacherIcon);
//            }else {
//                //  teacherIconが存在しない場合
//                Log.d(tag, "teacherIcon is null");
//            }
//        }
//
//        @Override
//        public void onClassRooms(List<String> classRooms) {
//            //  classRoomsを利用する場合
//            if(classRooms != null){
//                //  classRoomsが存在する場合
//                for(String classRoom: classRooms){
//                    Log.d(tag, classRoom);
//                }
//            }else {
//                //  classRoomsが存在しない場合
//                Log.d(tag, "classRooms are null");
//            }
//        }
//    });








    //  TS02  //////////
    public static void getDetailTeacher(String teacherID, TeacherErrorListener TeacherErrorListener, TeacherDetailGetListener TeacherDetailGetListener){
        teachers.child(teacherID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    TeachersDataModel teachersDataModel = snapshot.getValue(TeachersDataModel.class);
                    assert teachersDataModel != null;
                    String teacherName = teachersDataModel.getTeacherName();
                    TeacherDetailGetListener.onTeacherName(teacherName);
                    String teacherIcon = teachersDataModel.getTeacherIcon();
                    TeacherDetailGetListener.onTeacherIcon(teacherIcon);
                    String teacherMail = teachersDataModel.getTeacherMail();
                    TeacherDetailGetListener.onTeacherMail(teacherMail);
                    String teacherBirthday = teachersDataModel.getTeacherBirthday();
                    TeacherDetailGetListener.onTeacherBirthday(teacherBirthday);
                    String teacherComment = teachersDataModel.getTeacherComment();
                    TeacherDetailGetListener.onTeacherComment(teacherComment);

                    String successMsg = "接続に成功しました";
                    TeacherErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "TS02 error");
                    String errorMsg = "接続に失敗しました";
                    TeacherErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "TS02 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    Context context = this;
//    TeachersDatabaseManager.getDetailTeacher(teacherID, new TeachersDatabaseManager.TeacherErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データの取得に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データの取得に成功した場合
//            Log.d(tag, successMsg);
//        }
//    }, new TeachersDatabaseManager.TeacherDetailGetListener() {
//        @Override
//        public void onTeacherName(String teacherName) {
//            //  teacherNameを利用する場合
//            Log.d(tag, teacherName);
//        }
//
//        @Override
//        public void onTeacherIcon(String teacherIcon) {
//            //  teacherIconを利用する場合
//            if(teacherIcon != null){
//                //  teacherIconが存在する場合
//                // FirebaseStorageへの参照を取得
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference();
//
//                // 取得したい画像のパスを指定
//                String iconPath = "user_icon/" + teacherIcon;
//
//                // 画像をダウンロードするための参照を取得
//                StorageReference imageRef = storageRef.child(iconPath);
//
//                // 画像をダウンロード
//                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        // 画像が正常にダウンロードされた場合の処理
//                        ImageView classRoomIcon = findViewById(R.id.classRoomIcon);
//                        Glide.with(context).load(uri).into(classRoomIcon);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // 画像のダウンロード中にエラーが発生した場合の処理
//                    }
//                });
//            }else {
//                //  teacherIconが存在しない場合
//                Log.d(tag, "teacherIcon is null");
//            }
//        }
//
//        @Override
//        public void onTeacherMail(String teacherMail) {
//            //  teacherMailを利用する場合
//            Log.d(tag, teacherMail);
//        }
//
//        @Override
//        public void onTeacherBirthday(String teacherBirthday) {
//            //  teacherBirthdayを利用する場合
//            Log.d(tag, teacherBirthday);
//        }
//
//        @Override
//        public void onTeacherComment(String teacherComment) {
//            //  teacherCommentを利用する場合
//            if(teacherComment != null){
//                //  teacherCommentが存在する場合
//                Log.d(tag, teacherComment);
//            }else {
//                //  teacherCommentが存在しない場合
//                Log.d(tag, "teacherComment is null");
//            }
//        }
//    });









    //  TS03  //////////
    public static void getChatTeacher(String teacherID, TeacherErrorListener TeacherErrorListener, ChatTeacherGetListener ChatTeacherGetListener){
        teachers.child(teacherID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    TeachersDataModel TeachersDataModel = snapshot.getValue(TeachersDataModel.class);
                    assert TeachersDataModel != null;
                    String teacherName = TeachersDataModel.getTeacherName();
                    String teacherIcon = TeachersDataModel.getTeacherIcon();
                    ChatTeacherGetListener.onTeacherNameAndIcon(teacherName, teacherIcon);

                    String successMsg = "接続に成功しました";
                    TeacherErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "TS03 error");
                    String errorMsg = "接続に失敗しました";
                    TeacherErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "TS03 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    Context context = this;
//    TeachersDatabaseManager.getChatTeacher(teacherID, new TeachersDatabaseManager.TeacherErrorListener() {
//
//        @Override
//        public void onError(String errorMsg) {
//            //  データの取得に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データの取得に成功した場合
//            Log.d(tag, successMsg);
//        }
//    }, new TeachersDatabaseManager.ChatTeacherGetListener() {
//        @Override
//        public void onTeacherName(String teacherName) {
//            //  teacherNameを利用する場合
//                Log.d(tag, teacherName);
//        }
//
//        @Override
//        public void onTeacherIcon(String teacherIcon) {
//            //  teacherIconを利用する場合
//            if(teacherIcon != null){
//                //  teacherIconが存在する場合
//                // FirebaseStorageへの参照を取得
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference();
//
//                // 取得したい画像のパスを指定
//                String iconPath = "user_icon/" + teacherIcon;
//
//                // 画像をダウンロードするための参照を取得
//                StorageReference imageRef = storageRef.child(iconPath);
//
//                // 画像をダウンロード
//                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        // 画像が正常にダウンロードされた場合の処理
//                        ImageView classRoomIcon = findViewById(R.id.classRoomIcon);
//                        Glide.with(context).load(uri).into(classRoomIcon);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // 画像のダウンロード中にエラーが発生した場合の処理
//                    }
//                });
//            }else{
//                //  teacherIconが存在しない場合
//                Log.d(tag, "teacherIcon is null");
//            }
//        }
//    });




    //  TS04  //////////
    public static void getProfileTeacher(String teacherID, TeacherErrorListener TeacherErrorListener, TeacherProfileGetListener TeacherProfileGetListener){
        teachers.child(teacherID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    TeachersDataModel teachersDataModel = snapshot.getValue(TeachersDataModel.class);
                    assert teachersDataModel != null;
                    String teacherName = teachersDataModel.getTeacherName();
                    TeacherProfileGetListener.onTeacherName(teacherName);
                    String teacherIcon = teachersDataModel.getTeacherIcon();
                    TeacherProfileGetListener.onTeacherIcon(teacherIcon);
                    String teacherBirthday = teachersDataModel.getTeacherBirthday();
                    TeacherProfileGetListener.onTeacherBirthday(teacherBirthday);
                    String teacherComment = teachersDataModel.getTeacherComment();
                    TeacherProfileGetListener.onTeacherComment(teacherComment);

                    String successMsg = "接続に成功しました";
                    TeacherErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "TS04 error");
                    String errorMsg = "接続に失敗しました";
                    TeacherErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "TS04 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    Context context = this;
//    TeachersDatabaseManager.getProfileTeacher(teacherID, new TeachersDatabaseManager.TeacherErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データの取得に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データの取得に成功した場合
//            Log.d(tag, successMsg);
//        }
//    }, new TeachersDatabaseManager.TeacherProfileGetListener() {
//        @Override
//        public void onTeacherName(String teacherName) {
//            //  teacherNameを利用する場合
//            Log.d(tag, teacherName);
//        }
//
//        @Override
//        public void onTeacherIcon(String teacherIcon) {
//            //  teacherIconを利用する場合
//            if(teacherIcon != null){
//                //  teacherIconが存在する場合
//                // FirebaseStorageへの参照を取得
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference();
//
//                // 取得したい画像のパスを指定
//                String iconPath = "user_icon/" + teacherIcon;
//
//                // 画像をダウンロードするための参照を取得
//                StorageReference imageRef = storageRef.child(iconPath);
//
//                // 画像をダウンロード
//                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        // 画像が正常にダウンロードされた場合の処理
//                        ImageView classRoomIcon = findViewById(R.id.classRoomIcon);
//                        Glide.with(context).load(uri).into(classRoomIcon);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // 画像のダウンロード中にエラーが発生した場合の処理
//                    }
//                });
//            }else {
//                //  teacherIconが存在しない場合
//                Log.d(tag, "teacherIcon is null");
//            }
//        }
//
//        @Override
//        public void onTeacherBirthday(String teacherBirthday) {
//            //  teacherBirthdayを利用する場合
//            Log.d(tag, teacherBirthday);
//        }
//
//        @Override
//        public void onTeacherComment(String teacherComment) {
//            //  teacherCommentを利用する場合
//            if(teacherComment != null){
//                //  teacherCommentが存在する場合
//                Log.d(tag, teacherComment);
//            }else {
//                //  teacherCommentが存在しない場合
//                Log.d(tag, "teacherComment is null");
//            }
//        }
//    });





    //  TS05  //////////
    public static void getTeacherJoinClassRoom(String teacherID, TeacherErrorListener TeacherErrorListener, TeacherJoinClassRoomGetListener TeacherJoinClassRoomGetListener){
        teachers.child(teacherID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    TeachersDataModel teachersDataModel = snapshot.getValue(TeachersDataModel.class);
                    assert teachersDataModel != null;

                    List<String> classRooms = new ArrayList<>();
                    if (snapshot.child("classRooms").exists() && snapshot.child("classRooms").getChildrenCount() > 0) {
                        for (DataSnapshot classSnapshot : snapshot.child("classRooms").getChildren()) {
                            String joinClassRoomID = classSnapshot.getKey();
                            classRooms.add(joinClassRoomID);
                        }
                    }else {
                        classRooms = null;
                    }
                    TeacherJoinClassRoomGetListener.onClassRooms(classRooms);
                    String successMsg = "接続に成功しました";
                    TeacherErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "TS05 error");
                    String errorMsg = "接続に失敗しました";
                    TeacherErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "TS05 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    TeachersDatabaseManager.getTeacherJoinClassRoom(teacherID, new TeachersDatabaseManager.TeacherErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データ取得に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データ取得に成功した場合
//            Log.d(tag, successMsg);
//        }
//    }, new TeachersDatabaseManager.TeacherJoinClassRoomGetListener() {
//        @Override
//        public void onClassRooms(List<String> classRooms) {
//            //  classRoomsを利用する場合
//            if(classRooms != null){
//                //  classRoomsが存在する場合
//                for(String classRoom: classRooms){
//                    Log.d(tag, classRoom);
//                }
//            }else {
//                //  classRoomsが存在しない場合
//                Log.d(tag, "classRooms are null");
//            }
//        }
//    });




    //  TS06  //////////
    public static void getTeacherMemberID(String teacherID, String classRoomID, TeacherErrorListener TeacherErrorListener, TeacherMemberIdGetListener TeacherMemberIdGetListener){
        teachers.child(teacherID).child("classRooms").child(classRoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    String classMemberID = snapshot.getValue(String.class);
                    TeacherMemberIdGetListener.onTeacherMemberID(classMemberID);

                    String successMsg = "接続に成功しました";
                    TeacherErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "TS06 error");
                    String errorMsg = "接続に失敗しました";
                    TeacherErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "TS06 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    TeachersDatabaseManager.getTeacherMemberID(teacherID, classRoomID, new TeachersDatabaseManager.TeacherErrorListener() {
//
//        @Override
//        public void onError(String errorMsg) {
//            //  データの取得に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データの取得に成功した場合
//            Log.d(tag, successMsg);
//        }
//    }, new TeachersDatabaseManager.TeacherMemberIdGetListener() {
//        @Override
//        public void onTeacherMemberID(String classMemberID) {
//            //  classMemberIDを利用する場合
//                Log.d(tag, classMemberID);
//        }
//    });





    //  TD01  //////////
    public static void deleteJoinClassRoom(String teacherID, String joinClassRoomID, TeacherErrorListener TeacherErrorListener){
        teachers.child(teacherID).child("classRooms").child(joinClassRoomID).removeValue(((error, ref) -> {
            if (error != null) {
                //  削除に失敗した場合
                Log.d(tag, "TD01 error");
                String errorMsg = "接続に失敗しました";
                TeacherErrorListener.onError(errorMsg);
            }else {
                //  削除に成功した場合
                String successMsg = "接続に成功しました";
                TeacherErrorListener.onSuccess(successMsg);
            }
        }));
    }

    //  呼び出し
//    TeachersDatabaseManager.deleteJoinClassRoom(teacherID, joinClassRoomID, new TeachersDatabaseManager.TeacherErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データの削除に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データの削除に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });



    //  TD02  //////////
    public static void deleteTeacher(String teacherID){
        teachers.child(teacherID).removeValue();
    }





    //  呼び出し
//  TeachersDatabaseManager.deleteTeacher(teacherID);


}
