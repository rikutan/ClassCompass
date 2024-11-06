package com.example.classcompass.databaseManager;



import android.content.Context;
import androidx.annotation.NonNull;

//  firebaseRealtimeDatabase
import com.example.classcompass.dataModels.ClassRoomsDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//  firebaseStorage
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


//  QRコード生成に必要↓
import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ClassRoomsDatabaseManager {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private  static final DatabaseReference classRooms = database.getReference("classRooms");

    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final StorageReference classRoomsStorage = storage.getReference("classRooms");
    private static final String tag = "ClassRoomsDBM";

    private static Context classContext;

    public static void initialize(Context context) {
        classContext = context.getApplicationContext();
    }

    public interface ClassRoomErrorListener {
        void onError(String errorMsg);
        void onSuccess(String successMsg);
    }

    public  interface ClassRoomExistsListener{
        void onExists(String existsMsg);
    }


    public  interface ClassNameListener{
        void onClassName(String className);
    }



    public interface ClassQrGetListener {
        void onClassQR(String classQR);
    }

    public interface ClassRoomListener {
        void onClassRoom(String className, String schoolName, String classIcon, String year);
    }



    public interface ClassRoomListener2 {
        void onClassName(String className);
        void onClassIcon(String classIcon);
    }
    public interface AlbumListener {
        void onAlbum(String albumName, String albumDate, String albumPhoto);
    }



    public interface DetailClassRoomListener {
        void onClassName(String className);
        void onSchoolName(String schoolName);
        void onClassIcon(String classIcon);
        void onYear(String year);
        void onClassQR(String classQR);
    }


    public interface ClassRoomMemberGetListener {
        void onMembers(List<String> Members);
    }


    public interface ClassMemberGetListener {
        void onClassMembers(List<String> classMembers);
    }
    public interface AlbumGetListener {
        void onAlbums(List<String> albums);
    }

    public interface PhotoGetListener {
        void onPhotos(List<String> photos);
    }
    public interface LotteryGetListener {
        void onLotteries(List<String> lotteries);
    }

    public interface ClassMemberDeviceIdGetListener {
        void onClassMemberDeviceIDs(List<String> deviceIDs);
    }


    public interface ClassRoomGetRoomsListener {
        void onGroupRooms(List<String> groupRooms);
        void onPrivateRooms(List<String> PrivateRooms);
    }



    public interface getReadListener {
        void onRead(Boolean read);
    }


    public interface memberIDGetListener {
        void onMemberID(String memberID);
    }
    public interface deviceIDGetListener {
        void onDeviceID(String deviceID);
    }


    public interface ClassRoomNoticeListener {
        void onNotice(Boolean notice);
    }



    public interface LotteryIDGetListener {
        void onLotteryID(String lotteryID);
    }
    public interface AlbumIDGetListener {
        void onAlbumID(String AlbumID);
    }

    public interface LotteryResultListener {
        void onLotteryResultID(String lotteryResultID);
    }
    public interface LotteryListener {
        void onLottery(String lotteryTitle, String lotteryDate);
    }

    public interface LotteryResultItemListener {
        void onLotteryItem(String lotteryItem);
    }



    public interface ClassChatGetListener {
        void onClassChats(List<String> classChats);
    }


    public interface ClassRoomChatListener {
        void onClassRoomChat(String sender, String message);
    }






    //  CI01  //////////
    public static void teacherJoinClassRoom(String classRoomID, String teacherID, ClassRoomErrorListener ClassRoomErrorListener, ClassRoomExistsListener ClassRoomExistsListener){
        TeachersDatabaseManager.getTeacherJoinClassRoom(teacherID, new TeachersDatabaseManager.TeacherErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データ取得に失敗した場合
                Log.d(tag, errorMsg);
                ClassRoomErrorListener.onError(errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データ取得に成功した場合
                Log.d(tag, successMsg);
            }
        }, new TeachersDatabaseManager.TeacherJoinClassRoomGetListener() {
            @Override
            public void onClassRooms(List<String> classRoomList) {
                //  classRoomsを利用する場合
                if(classRoomList != null && classRoomList.contains(classRoomID)){
                    //  classRoomIDが存在する場合
                    ClassRoomExistsListener.onExists("すでに存在しています");
                }else {
                    //  classRoomIDが存在しない場合
                    classRooms.child(classRoomID).child("classMembers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String classMemberID;
                            //  classMembersノードにclassMemberIDが存在するか確認
                            if (snapshot.hasChildren()) {
                                //  classMembersノードにclassMemberIDノードが存在する場合
                                classMemberID = "CT" + UUID.randomUUID().toString();

                                ClassRoomsDataModel ClassRoom = new ClassRoomsDataModel();
                                getToken(new TokenCallback() {
                                    @Override
                                    public void onTokenReceived(String token) {
                                        ClassRoom.setDeviceID(token);
                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        // エラーハンドリング
                                        Log.e(tag, "Error retrieving token", exception);
                                    }
                                });
                                ClassRoom.setMemberID(teacherID);
                                ClassRoom.setClassRoomNotice(true);

                                classRooms.child(classRoomID).child("classMembers").child(classMemberID).setValue(ClassRoom, (error, ref) -> {
                                    if (error != null) {
                                        // データ書き込み失敗時
                                        Log.d(tag, "CI01 error");
                                        String errorMsg = "接続に失敗しました";
                                        ClassRoomErrorListener.onError(errorMsg);
                                    }else {
                                        // データ書き込み成功時
                                        TeachersDatabaseManager.setJoinClassRoom(teacherID, classRoomID, classMemberID, new TeachersDatabaseManager.TeacherErrorListener() {
                                            @Override
                                            public void onError(String errorMsg) {
                                                // データ書き込み失敗時
                                                Log.d(tag, errorMsg);
                                            }

                                            @Override
                                            public void onSuccess(String successMsg) {
                                                // データ書き込み成功時
                                                Log.d(tag, successMsg);
                                            }
                                        });
                                    }
                                });
                                String successMsg = "接続に成功しました";
                                ClassRoomErrorListener.onSuccess(successMsg);
                            }else {
                                Log.d(tag, "CI01 error");
                                String errorMsg = "接続に失敗しました";
                                ClassRoomErrorListener.onError(errorMsg);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //  データ登録中にエラーが発生した場合
                            Log.d(tag, "CI01 error");
                            String errorMsg = "接続に失敗しました";
                            ClassRoomErrorListener.onError(errorMsg);
                        }
                    });

                }
            }
        });
    }

    // 呼び出し
//        ClassRoomsDatabaseManager.teacherJoinClassRoom(classRoomID, teacherID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//            @Override
//            public void onError(String errorMsg) {
//                //  データ取得に失敗した場合
//                Log.d(tag, errorMsg);
//            }
//
//            @Override
//            public void onSuccess(String successMsg) {
//                //  データ取得に成功した場合
//                Log.d(tag, successMsg);
//            }
//        }, new ClassRoomsDatabaseManager.ClassRoomExistsListener() {
//            @Override
//            public void onExists(String existsMsg) {
//                //  すでに存在する場合
//                Log.d(tag, existsMsg);
//            }
//        });













    //  CI02  //////////
    public static void setClassRoom(String teacherID, String className, String schoolName, String year, ClassRoomErrorListener ClassRoomErrorListener, ClassQrGetListener ClassQrGetListener){
        classRooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String classRoomID = "C" + UUID.randomUUID().toString();

                //  QRコード生成
                String qrFileName = classRoomID + "_qr.png";
                generateQRCode(qrFileName, classRoomID, new ClassRoomErrorListener() {
                    @Override
                    public void onError(String errorMsg) {
                        //  QRコード生成に失敗した場合
                        Log.d(tag, "CI02 error");
                        ClassRoomErrorListener.onError(errorMsg);
                    }

                    @Override
                    public void onSuccess(String successMsg) {
                        //  QRコード生成に成功した場合

                        ClassRoomsDataModel ClassRoom = new ClassRoomsDataModel();
                        ClassRoom.setClassName(className);
                        ClassRoom.setClassQR(qrFileName);
                        ClassRoom.setSchoolName(schoolName);
                        ClassRoom.setYear(year);

                        classRooms.child(classRoomID).setValue(ClassRoom, (error, ref) -> {
                            if (error != null) {
                                // データ書き込み失敗時
                                Log.d(tag, "CI02 error");
                                String errorMsg = "接続に失敗しました";
                                ClassRoomErrorListener.onError(errorMsg);
                            }else {
                                // データ書き込み成功時
                                ClassRoomsDataModel classMembers = new ClassRoomsDataModel();
                                classMembers.setMemberID(teacherID);
                                getToken(new TokenCallback() {
                                    @Override
                                    public void onTokenReceived(String token) {
                                        classMembers.setDeviceID(token);
                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        // エラーハンドリング
                                        Log.e(tag, "Error retrieving token", exception);
                                    }
                                });
                                classMembers.setClassRoomNotice(true);
                                String classMemberID = "CT" + UUID.randomUUID().toString();
                                classRooms.child(classRoomID).child("classMembers").child(classMemberID).setValue(classMembers, (error2, ref2) -> {
                                    if (error2 != null) {
                                        // データ書き込み失敗時
                                        Log.d(tag, "CI02 error");
                                        String errorMsg = "接続に失敗しました";
                                        ClassRoomErrorListener.onError(errorMsg);
                                    }else {
                                        // データ書き込み成功時
                                        //  TI02実行
                                        TeachersDatabaseManager.setJoinClassRoom(teacherID, classRoomID, classMemberID, new TeachersDatabaseManager.TeacherErrorListener() {
                                            @Override
                                            public void onError(String errorMsg) {
                                                // データ書き込み失敗時
                                                ClassRoomErrorListener.onError(errorMsg);
                                            }

                                            @Override
                                            public void onSuccess(String successMsg) {
                                                // データ書き込み成功時
                                                Log.d(tag, "Success!");
                                                String successMsg2 = "接続に成功しました";
                                                ClassRoomErrorListener.onSuccess(successMsg2);
                                                ClassQrGetListener.onClassQR(qrFileName);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ登録中にエラーが発生した場合
                Log.d(tag, "CI02 error");
                ClassRoomErrorListener.onError("接続できませんでした");
            }
        });
    }

    // QRコードを生成する
    public static void generateQRCode(String qrFileName, String classRoomID, ClassRoomErrorListener ClassRoomErrorListener) {
        Bitmap qrBitmap = null;
        QRCodeWriter writer = new QRCodeWriter();
        try {
            //  QRコード生成に成功した場合
            BitMatrix bitMatrix = writer.encode(classRoomID, BarcodeFormat.QR_CODE, 400, 400);
            qrBitmap = toBitmap(bitMatrix);
            saveBitmapAsPNG(qrBitmap, qrFileName, classRoomID, new ClassRoomErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    // PNGへの変換に失敗した場合
                    ClassRoomErrorListener.onError("pngへの変換に失敗しました");
                }

                @Override
                public void onSuccess(String successMsg) {
                    // PNGへの変換に成功した場合
                    ClassRoomErrorListener.onSuccess("pngへの変換に成功しました");
                }
            });
            ClassRoomErrorListener.onSuccess("QR生成に成功しました");
        } catch (WriterException e) {
            //  QRコード生成に失敗した場合
            ClassRoomErrorListener.onError("QR生成に失敗しました");
        }
    }

    // BitMatrixをBitmapに変換する
    private static Bitmap toBitmap(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return qrBitmap;
    }

    // BitmapをPNGに変換する
    private static void saveBitmapAsPNG(Bitmap qrBitmap, String qrFileName, String classRoomID, ClassRoomErrorListener ClassRoomErrorListener) {
        File file = new File(classContext.getExternalFilesDir(null), qrFileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // PNGへの変換に成功した場合
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            // Firebase Storage に PNG 画像をアップロード
            StorageReference path = classRoomsStorage.child(classRoomID).child(qrFileName);
            uploadFileToFirebaseStorage(file, path, new ClassRoomErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    ClassRoomErrorListener.onError("アップロードに失敗しました");
                }

                @Override
                public void onSuccess(String successMsg) {
                    ClassRoomErrorListener.onSuccess("アップロードに成功しました");
                }
            });
            ClassRoomErrorListener.onSuccess("pngへの変換に成功しました");
        } catch (IOException e) {
            // PNGへの変換に失敗した場合
            ClassRoomErrorListener.onError("pngへの変換に失敗しました");
        }
    }

    // Firebase Storage にファイルをアップロードする
    private static void uploadFileToFirebaseStorage(File file, StorageReference path, ClassRoomErrorListener ClassRoomErrorListener) {
        if (file != null) {
            path.putFile(Uri.fromFile(file))
                    .addOnSuccessListener(taskSnapshot -> {
                        // アップロードに成功した場合
                        ClassRoomErrorListener.onSuccess("アップロードに成功しました");
                    })
                    .addOnFailureListener(e -> {
                        // アップロードに失敗した場合
                        ClassRoomErrorListener.onError("アップロードに失敗しました");
                    });
        }
    }


    // デバイストークン取得
    public interface TokenCallback {
        void onTokenReceived(String token);
        void onError(Exception exception);
    }

    private static void getToken(final TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String token = task.getResult();
                            Log.d(tag, "Device Token: " + token);
                            // コールバックを使用してトークンを返す
                            callback.onTokenReceived(token);
                        } else {
                            Log.e(tag, "Failed to get token", task.getException());
                            // エラーをコールバックに渡す
                            callback.onError(task.getException());
                        }
                    }
                });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.initialize(getApplicationContext());
//    ClassRoomsDatabaseManager.setClassRoom(teacherID, className, schoolName, year, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  クラスルーム作成に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  クラスルーム作成に成功した場合
//            Log.d(tag, successMsg);
//        }
//    }, new ClassRoomsDatabaseManager.ClassQrGetListener() {
//        @Override
//        public void onClassQR(String classQR) {
//            //  クラスルームQR画像を利用する場合
//            Log.d(tag, classQR);
//        }
//    });



    //  CI04  //////////
    public static void setClassRoomChat(String classRoomID, String classMemberID, String message, ClassRoomErrorListener ClassRoomErrorListener){

        long timestamp = System.currentTimeMillis();
        Date date = new Date(timestamp);

        // SimpleDateFormatを使用してYYYYMMDDHHMMSS形式にフォーマット
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = sdf.format(date);
        String uuid = UUID.randomUUID().toString();
        String classRoomChatID = formattedDate + uuid;

        ClassRoomsDataModel ClassRoom = new ClassRoomsDataModel();
        ClassRoom.setSender(classMemberID);
        ClassRoom.setMessage(message);

        classRooms.child(classRoomID).child("classRoomChats").child(classRoomChatID).setValue(ClassRoom, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                Log.d(tag, "CI04 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }else {
                // データ書き込み成功時
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
            }
        });
    }

//      呼び出し
//    ClassRoomsDatabaseManager.setClassRoomChat(classRoomID, classMemberID, message, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            Log.d(tag, successMsg);
//        }
//    });




    //  CI09  //////////
    public static void setLastClassRoomChats(String classRoomID, String classMemberID, ClassRoomErrorListener ClassRoomErrorListener){
        classRooms.child(classRoomID).child("classRoomChats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    long maxClassRoomChatTime = 0;
                    String maxClassRoomChatID = null;
                    for (DataSnapshot chatsnapshot : snapshot.getChildren()) {
                        String classRoomChatID = chatsnapshot.getKey();

                        long classRoomChatIDTime = Long.parseLong(classRoomChatID.substring(0, 16));

                        if (classRoomChatIDTime > maxClassRoomChatTime) {
                            maxClassRoomChatTime = classRoomChatIDTime;
                            maxClassRoomChatID = classRoomChatID;
                        }
                    }
                    ClassRoomsDataModel ClassRoom = new ClassRoomsDataModel();
                    ClassRoom.setLastClassRoomChatID(maxClassRoomChatID);
                    classRooms.child(classRoomID).child("classMembers").child(classMemberID).setValue(ClassRoom, (error, ref) -> {
                        if (error != null) {
                            // データ書き込み失敗時
                            Log.d(tag, "CI09 error");
                            String errorMsg = "接続に失敗しました";
                            ClassRoomErrorListener.onError(errorMsg);
                        }else {
                            // データ書き込み成功時
                            String successMsg = "接続に成功しました";
                            ClassRoomErrorListener.onSuccess(successMsg);
                        }
                    });
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CI09 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CI09 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.setLastClassRoomChats(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データ登録に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データ登録に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });








    //  CI10  //////////
    public static void studentJoinClassRoom(String classRoomID, String studentID, ClassRoomErrorListener ClassRoomErrorListener, ClassRoomExistsListener ClassRoomExistsListener){
        StudentsDatabaseManager.getStudentJoinClassRoom(studentID, new StudentsDatabaseManager.StudentErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データ取得に失敗した場合
                Log.d(tag, errorMsg);
                ClassRoomErrorListener.onError(errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データ取得に成功した場合
                Log.d(tag, successMsg);
            }
        }, new StudentsDatabaseManager.StudentJoinClassRoomGetListener() {
            @Override
            public void onClassRooms(List<String> classRoomList) {
                //  classRoomsを利用する場合
                if(classRoomList != null && classRoomList.contains(classRoomID)){
                    //  classRoomIDが存在する場合
                    ClassRoomExistsListener.onExists("すでに存在しています");
                }else {
                    //  classRoomIDが存在しない場合
                    classRooms.child(classRoomID).child("classMembers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String classMemberID;
                            //  classMembersノードにclassMemberIDが存在するか確認
                            if (snapshot.hasChildren()) {
                                //  classMembersノードにclassMemberIDノードが存在する場合

                                classMemberID = "CS" + UUID.randomUUID().toString();

                                ClassRoomsDataModel ClassRoom = new ClassRoomsDataModel();
                                getToken(new TokenCallback() {
                                    @Override
                                    public void onTokenReceived(String token) {
                                        ClassRoom.setDeviceID(token);
                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        // エラーハンドリング
                                        Log.e(tag, "Error retrieving token", exception);
                                    }
                                });
                                ClassRoom.setMemberID(studentID);
                                ClassRoom.setClassRoomNotice(true);

                                classRooms.child(classRoomID).child("classMembers").child(classMemberID).setValue(ClassRoom, (error, ref) -> {
                                    if (error != null) {
                                        // データ書き込み失敗時
                                        Log.d(tag, "CI10 error");
                                        String errorMsg = "接続に失敗しました";
                                        ClassRoomErrorListener.onError(errorMsg);
                                    }else {
                                        // データ書き込み成功時
                                        StudentsDatabaseManager.setJoinClassRoom(studentID, classRoomID, classMemberID, new StudentsDatabaseManager.StudentErrorListener() {
                                            @Override
                                            public void onError(String errorMsg) {
                                                // データ書き込み失敗時
                                                Log.d(tag, errorMsg);
                                            }

                                            @Override
                                            public void onSuccess(String successMsg) {
                                                // データ書き込み成功時
                                                Log.d(tag, successMsg);
                                            }
                                        });
                                    }
                                });
                                String successMsg = "接続に成功しました";
                                ClassRoomErrorListener.onSuccess(successMsg);
                            }else {
                                Log.d(tag, "CI10 error");
                                String errorMsg = "接続に失敗しました";
                                ClassRoomErrorListener.onError(errorMsg);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //  データ登録中にエラーが発生した場合
                            Log.d(tag, "CI10 error");
                            String errorMsg = "接続に失敗しました";
                            ClassRoomErrorListener.onError(errorMsg);
                        }
                    });

                }
            }
        });
    }

    // 呼び出し
//        ClassRoomsDatabaseManager.studentJoinClassRoom(classRoomID, studentID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//            @Override
//            public void onError(String errorMsg) {
//                //  データ取得に失敗した場合
//                Log.d(tag, errorMsg);
//            }
//
//            @Override
//            public void onSuccess(String successMsg) {
//                //  データ取得に成功した場合
//                Log.d(tag, successMsg);
//            }
//        }, new ClassRoomsDatabaseManager.ClassRoomExistsListener() {
//            @Override
//            public void onExists(String existsMsg) {
//                //  すでに存在する場合
//                Log.d(tag, existsMsg);
//            }
//        });








    //  CI11  //////////
    public static void setJoinPrivateRoom(String classRoomID, String classMemberID, String joinPrivateRoomID, ClassRoomErrorListener ClassRoomErrorListener){

        classRooms.child(classRoomID).child("classMembers").child(classMemberID).child("privateRooms").child(joinPrivateRoomID).child("privateRoomNotice").setValue(true, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                Log.d(tag, "CI11 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }else {
                // データ書き込み成功時
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.setJoinPrivateRoom(classRoomID, classMemberID, joinPrivateRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データ作成に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  クラスルーム作成に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });




    //  CU02  //////////
    public static void setClassRoomNotice(String classRoomID, Boolean classNotice, ClassRoomErrorListener ClassRoomErrorListener){
        classRooms.child(classRoomID).child("classRoomNotice").setValue(classNotice, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                Log.d(tag, "CU02 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }else {
                //  データの書き込みに成功した場合
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.setClassRoomNotice(classRoomID, classNotice, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データ登録に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データ登録に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });





    //  CU05  //////////
    public static void setSchoolName(String classRoomID, String schoolName, ClassRoomErrorListener ClassRoomErrorListener){
        classRooms.child(classRoomID).child("schoolName").setValue(schoolName, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                Log.d(tag, "CU05 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }else {
                //  データの書き込みに成功した場合
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.setSchoolName(classRoomID, schoolName, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データ登録に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データ登録に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });







    //  CU06  //////////
    public static void setYear(String classRoomID, String year, ClassRoomErrorListener ClassRoomErrorListener){
        classRooms.child(classRoomID).child("year").setValue(year, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                Log.d(tag, "CU06 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }else {
                //  データの書き込みに成功した場合
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.setYear(classRoomID, year, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データ登録に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データ登録に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });







    //  CU07  //////////
    public static void setClassName(String classRoomID, String className, ClassRoomErrorListener ClassRoomErrorListener){
        classRooms.child(classRoomID).child("className").setValue(className, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                Log.d(tag, "CU07 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }else {
                //  データの書き込みに成功した場合
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.setClassName(classRoomID, className, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データ登録に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データ登録に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });






    //  CU08  //////////
    public static void setClassIcon(String classRoomID, File classIcon, ClassRoomErrorListener ClassRoomErrorListener) {
        // ファイル名を変更


        // ファイルをFirebase Storageにアップロード
        StorageReference path = classRoomsStorage.child(classRoomID).child(classIcon.getName());
        uploadFileToFirebaseStorage(classIcon, path, new ClassRoomErrorListener() {
            @Override
            public void onError(String errorMsg) {
                Log.d(tag, "CU088 error");
                ClassRoomErrorListener.onError(errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                classRooms.child(classRoomID).child("classIcon").setValue(classIcon.getName(), (error, ref) -> {
                    if (error != null) {
                        Log.d(tag, "CU08 error");
                        String errorMsg = "接続に失敗しました";
                        ClassRoomErrorListener.onError(errorMsg);
                    } else {
                        ClassRoomErrorListener.onSuccess(successMsg);
                    }
                });
            }
        });
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
//                        ClassRoomsDatabaseManager.setClassIcon(classRoomID, classIcon, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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


    //  CU09  //////////
    public static void setReadClassRoomChat(String classRoomID, String classMemberID, ClassRoomErrorListener ClassRoomErrorListener){
        Query lastChatQuery = classRooms.child(classRoomID).child("classRoomChats").orderByKey().limitToLast(1);
        lastChatQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    //  クラスルームのデータが存在する場合
                    ClassRoomsDataModel classRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert classRoomsDataModel != null;
                    String readClassRoomChatID = classRoomsDataModel.getClassRoomChatID();
                    classRooms.child(classRoomID).child("classMembers").child(classMemberID).child("readClassRoomChatID").setValue(readClassRoomChatID, (error, ref) -> {
                        if (error != null) {
                            // データ書き込み失敗時
                            Log.d(tag, "CU09 error");
                            String errorMsg = "接続に失敗しました";
                            ClassRoomErrorListener.onError(errorMsg);
                        }else {
                            //  データの書き込みに成功した場合
                            String successMsg = "接続に成功しました";
                            ClassRoomErrorListener.onSuccess(successMsg);
                        }
                    });
                }else {
                    //  クラスルームのデータが存在しない場合
                    Log.d(tag, "CU09 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CU09 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }
    //  呼び出し
//    ClassRoomsDatabaseManager.setReadClassRoomChat(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データ登録に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データ登録に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });






    //  CU10  //////////
    public static void setDeviceID(String classRoomID, String classMemberID, ClassRoomErrorListener ClassRoomErrorListener){

        getToken(new TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                classRooms.child(classRoomID).child("classMembers").child(classMemberID).child("deviceID").setValue(token, (error, ref) -> {
                    if (error != null) {
                        // データ書き込み失敗時
                        Log.d(tag, "CU10 error");
                        String errorMsg = "接続に失敗しました";
                        ClassRoomErrorListener.onError(errorMsg);
                    }else {
                        //  データの書き込みに成功した場合
                        String successMsg = "接続に成功しました";
                        ClassRoomErrorListener.onSuccess(successMsg);
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                // エラーハンドリング
                Log.e(tag, "Error retrieving token", exception);
                // データ書き込み失敗時
                Log.d(tag, "CU10 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.setDeviceID(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  データ登録に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  データ登録に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });











    //  CS01  //////////
    public static void getClassRoom(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, ClassRoomListener ClassRoomListener){
        classRooms.child(classRoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  クラスルームのデータが存在する場合
                    ClassRoomsDataModel classRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert classRoomsDataModel != null;
                    String className = classRoomsDataModel.getClassName();
                    String schoolName = classRoomsDataModel.getSchoolName();
                    String classIcon = classRoomsDataModel.getClassIcon();
                    String year = classRoomsDataModel.getYear();
                    ClassRoomListener.onClassRoom(className, schoolName, classIcon, year);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  クラスルームのデータが存在しない場合
                    Log.d(tag, "CS01 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS01 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し

//    ClassRoomsDatabaseManager.getClassRoom(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.ClassRoomListener() {
//        @Override
//        public void onClassRoom(String className, String schoolName, String classIcon, String year) {
//            //  classRoomを利用する場合
//            //  classIconを利用する場合
//            if(classIcon != null){
//                Log.d(tag, classIcon);
//            }else {
//                Log.d(tag, "classIcon is null");
//            }
//        }
//    });





    //  CS02  //////////
    public static void getClassRoom2(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, ClassRoomListener2 ClassRoomListener2){
        classRooms.child(classRoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  クラスルームのデータが存在する場合
                    ClassRoomsDataModel classRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert classRoomsDataModel != null;
                    String className = classRoomsDataModel.getClassName();
                    ClassRoomListener2.onClassName(className);
                    String classIcon = classRoomsDataModel.getClassIcon();
                    ClassRoomListener2.onClassIcon(classIcon);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  クラスルームのデータが存在しない場合
                    Log.d(tag, "CS02 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS02 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    Context context = this;
//    ClassRoomsDatabaseManager.getClassRoom2(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.ClassRoomListener2() {
//        @Override
//        public void onClassName(String className) {
//            //  classNameを利用する場合
//            Log.d(tag, className);
//        }
//
//        @Override
//        public void onClassIcon(String classIcon) {
//            //  classIconを利用する場合
//            if(classIcon != null){
//                Log.d(tag, classIcon);
//                // FirebaseStorageへの参照を取得
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference();
//
//                // 取得したい画像のパスを指定
//                String iconPath = "classRooms/" + classRoomID + "/" + classIcon;
//
//                // 画像をダウンロードするための参照を取得
//                StorageReference imageRef = storageRef.child(iconPath);
//
//                // 画像をダウンロード
//                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
//                Log.d(tag, "classIcon is null");
//            }
//        }
//    });








    //  CS04  //////////
    public static void getDetailClassRoom(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, DetailClassRoomListener DetailClassRoomListener){
        classRooms.child(classRoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  クラスルームのデータが存在する場合
                    ClassRoomsDataModel classRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert classRoomsDataModel != null;
                    String className = classRoomsDataModel.getClassName();
                    DetailClassRoomListener.onClassName(className);
                    String schoolName = classRoomsDataModel.getSchoolName();
                    DetailClassRoomListener.onSchoolName(schoolName);
                    String classIcon = classRoomsDataModel.getClassIcon();
                    DetailClassRoomListener.onClassIcon(classIcon);
                    String year = classRoomsDataModel.getYear();
                    DetailClassRoomListener.onYear(year);
                    String classQR = classRoomsDataModel.getClassQR();
                    DetailClassRoomListener.onClassQR(classQR);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  クラスルームのデータが存在しない場合
                    Log.d(tag, "CS04 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS04 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    Context context = this;
//    ClassRoomsDatabaseManager.getDetailClassRoom(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.DetailClassRoomListener() {
//        @Override
//        public void onClassName(String className) {
//            //  classNameを利用する場合
//            Log.d(tag, className);
//        }
//
//        @Override
//        public void onSchoolName(String schoolName) {
//            //  schoolNameを利用する場合
//            Log.d(tag, schoolName);
//        }
//
//        @Override
//        public void onClassIcon(String classIcon) {
//            //  classIconを利用する場合
//            if(classIcon != null){
//                Log.d(tag, classIcon);
//                // FirebaseStorageへの参照を取得
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference();
//
//                // 取得したい画像のパスを指定
//                String iconPath = "classRooms/" + classRoomID + "/" + classIcon;
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
//                Log.d(tag, "classIcon is null");
//            }
//        }
//
//        @Override
//        public void onYear(String year) {
//            //  yearを利用する場合
//            Log.d(tag, year);
//        }
//
//        @Override
//        public void onClassQR(String classQR) {
//            //  classQRを利用する場合
//            Log.d(tag, classQR);
//            // FirebaseStorageへの参照を取得
//            FirebaseStorage storage = FirebaseStorage.getInstance();
//            StorageReference storageRef = storage.getReference();
//
//            // 取得したい画像のパスを指定
//            String iconPath = "classRooms/" + classRoomID + "/" + classQR;
//
//            // 画像をダウンロードするための参照を取得
//            StorageReference imageRef = storageRef.child(iconPath);
//
//            // 画像をダウンロード
//            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    // 画像が正常にダウンロードされた場合の処理
//                    ImageView classRoomQR = findViewById(R.id.classRoomQR);
//                    Glide.with(context).load(uri).into(classRoomQR);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // 画像のダウンロード中にエラーが発生した場合の処理
//                }
//            });
//        }
//    });




    //  CS05  //////////
    public static void getClassMemberID(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, ClassMemberGetListener ClassMemberGetListener){
        classRooms.child(classRoomID).child("classMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    List<String> classMembers = new ArrayList<>();
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                            String getClassMemberID = memberSnapshot.getKey();
                            classMembers.add(getClassMemberID);
                        }
                    }else {
                        classMembers = null;
                    }
                    ClassMemberGetListener.onClassMembers(classMembers);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CS05 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS05 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.getClassMemberID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.ClassMemberGetListener() {
//        @Override
//        public void onClassMembers(List<String> classMembers) {
//            // classMembersを利用する場合
//            if(classMembers != null){
//                //  classMembersが存在する場合
//                for(String classMemberID: classMembers){
//                    Log.d(tag, classMemberID);
//                }
//            }else {
//                //  classMembersが存在しない場合
//                Log.d(tag, "classMembers are null");
//            }
//        }
//    });


    //  CS06  //////////
    public static void getStudentClassMemberID(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, ClassMemberGetListener ClassMemberGetListener){
        classRooms.child(classRoomID).child("classMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    List<String> classMembers = new ArrayList<>();
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                            String getClassMemberID = memberSnapshot.getKey();
                            if (getClassMemberID != null && getClassMemberID.startsWith("CS")) {
                                classMembers.add(getClassMemberID);
                                Log.d(tag, getClassMemberID);
                            }
                        }
                    }else {
                        classMembers = null;
                    }
                    ClassMemberGetListener.onClassMembers(classMembers);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CS06 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS06 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    // 呼び出し
//    ClassRoomsDatabaseManager.getStudentClassMemberID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.ClassMemberGetListener() {
//        @Override
//        public void onClassMembers(List<String> classMembers) {
//            // classMembersを利用する場合
//            if(classMembers != null){
//                //  classMembersが存在する場合
//                for(String classMemberID: classMembers){
//                    Log.d(tag, classMemberID);
//                }
//            }else {
//                //  classMembersが存在しない場合
//                Log.d(tag, "classMembers are null");
//            }
//        }
//    });










    //  CS08  //////////
    public static void getMemberID(String classRoomID, String classMemberID, ClassRoomErrorListener ClassRoomErrorListener, memberIDGetListener memberIDGetListener){
        classRooms.child(classRoomID).child("classMembers").child(classMemberID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ClassRoomsDataModel ClassRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert ClassRoomsDataModel != null;
                    String memberID = ClassRoomsDataModel.getMemberID();
                    memberIDGetListener.onMemberID(memberID);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CS08 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS08 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.getMemberID(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.memberIDGetListener() {
//        @Override
//        public void onMemberID(String memberID) {
//            //  memberIDを利用する場合
//            Log.d(tag, memberID);
//        }
//    });







    //  CS10  //////////
    public static void getClassRoomNotice(String classRoomID, String classMemberID, ClassRoomErrorListener ClassRoomErrorListener, ClassRoomNoticeListener ClassRoomNoticeListener){
        classRooms.child(classRoomID).child("classMembers").child(classMemberID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ClassRoomsDataModel ClassRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert ClassRoomsDataModel != null;
                    Boolean classRoomNotice = ClassRoomsDataModel.getClassRoomNotice();
                    ClassRoomNoticeListener.onNotice(classRoomNotice);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CS10 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS10 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);

            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.getClassRoomNotice(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.ClassRoomNoticeListener() {
//        @Override
//        public void onNotice(Boolean notice) {
//            if(notice){
//                //  通知オンの場合
//                Log.d(tag, "on notice");
//            }else {
//                //  通知オフの場合
//                Log.d(tag, "off notice");
//            }
//
//        }
//    });



    //  CS15  //////////
    public static void getClassName(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, ClassNameListener ClassNameListener){
        classRooms.child(classRoomID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ClassRoomsDataModel ClassRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert ClassRoomsDataModel != null;
                    String className = ClassRoomsDataModel.getClassName();
                    ClassNameListener.onClassName(className);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CS15 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS15 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.getClassName(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.ClassNameListener() {
//        @Override
//        public void onClassName(String className) {
//            //  classNameを利用する場合
//            Log.d(tag, className);
//        }
//    });







    //  CS16  //////////
    public static void getReadClassRoomChat(String classRoomID, String classMemberID, ClassRoomErrorListener ClassRoomErrorListener, getReadListener getReadListener){

        classRooms.child(classRoomID).child("classMembers").child(classMemberID).child("readClassRoomChatID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    ClassRoomsDataModel classRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert classRoomsDataModel != null;
                    String readClassRoomChatID = classRoomsDataModel.getReadClassRoomChatID();
                    long readClassRoomChatIDTime = Long.parseLong(readClassRoomChatID.substring(0, 16));
                    classRooms.child(classRoomID).child("classRoomChats").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if(snapshot2.exists()){
                                //  データが存在する場合
                                long maxClassRoomChatTime = 0;
                                for (DataSnapshot chatsnapshot2 : snapshot2.getChildren()) {
                                    String classRoomChatID = chatsnapshot2.getKey();

                                    long classRoomChatIDTime = Long.parseLong(classRoomChatID.substring(0, 16));

                                    if (classRoomChatIDTime > maxClassRoomChatTime) {
                                        maxClassRoomChatTime = classRoomChatIDTime;
                                    }
                                }
                                if(maxClassRoomChatTime > readClassRoomChatIDTime){
                                    getReadListener.onRead(true);
                                }else {
                                    getReadListener.onRead(false);
                                }
                            }else {
                                //  データが存在しない場合
                                Log.d(tag, "CS16 error");
                                String errorMsg = "接続に失敗しました";
                                ClassRoomErrorListener.onError(errorMsg);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //  データ取得中にエラーが発生した場合
                            Log.d(tag, "CS16 error");
                            String errorMsg = "接続に失敗しました";
                            ClassRoomErrorListener.onError(errorMsg);
                        }
                    });
                }else {
                    //  データが存在しない場合
                    getReadListener.onRead(false);
                }
                ClassRoomErrorListener.onSuccess("接続に成功しました");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS16 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.getReadClassRoomChat(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.getReadListener() {
//        @Override
//        public void onRead(Boolean read) {
//            if(read){
//                //  未読がある場合
//                Log.d(tag, "unread");
//            }else {
//                //  すべて既読済みの場合
//                Log.d(tag, "Already read");
//            }
//
//        }
//    });
















    //  CS19  ///////////
    public static void getStudentsClassMemberID(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, ClassRoomMemberGetListener ClassRoomMemberGetListener){
        classRooms.child(classRoomID).child("classMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    ClassRoomsDataModel ClassRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert ClassRoomsDataModel != null;

                    List<String> members = new ArrayList<>();
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                            String getMemberID = classSnapshot.getKey();
                            if (getMemberID != null && getMemberID.length() >= 2 && getMemberID.substring(0, 2).equals("CS")){
                                members.add(getMemberID);
                            }
                        }
                    }else {
                        members = null;
                    }
                    ClassRoomMemberGetListener.onMembers(members);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CS19 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS19 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });

    }
    //  呼び出し
//        ClassRoomsDatabaseManager.getStudentsClassMemberID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//            @Override
//            public void onError(String errorMsg) {
//                //  データの取得に失敗した場合
//                Log.d(tag, errorMsg);
//            }
//
//            @Override
//            public void onSuccess(String successMsg) {
//                //  データの取得に成功した場合
//                Log.d(tag, successMsg);
//            }
//        }, new ClassRoomsDatabaseManager.ClassRoomMemberGetListener() {
//            @Override
//            public void onMembers(List<String> Members) {
//                //  Membersを利用する場合
//                if(Members != null){
//                    //  Membersが存在する場合
//                    for(String Member: Members){
//                        Log.d(tag, Member);
//                    }
//                }else {
//                    //  Membersが存在しない場合
//                    Log.d(tag, "Members are null");
//                }
//            }
//        });











    //  CS20  ///////////
    public static void getTeachersClassMemberID(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, ClassRoomMemberGetListener ClassRoomMemberGetListener){
        classRooms.child(classRoomID).child("classMembers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    ClassRoomsDataModel ClassRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert ClassRoomsDataModel != null;

                    List<String> members = new ArrayList<>();
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                            String getMemberID = classSnapshot.getKey();
                            if (getMemberID != null && getMemberID.length() >= 2 && getMemberID.substring(0, 2).equals("CT")){
                                members.add(getMemberID);
                            }
                        }
                    }else {
                        members = null;
                    }
                    ClassRoomMemberGetListener.onMembers(members);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CS20 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS20 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });

    }
    //  呼び出し
//        ClassRoomsDatabaseManager.getTeachersClassMemberID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//            @Override
//            public void onError(String errorMsg) {
//                //  データの取得に失敗した場合
//                Log.d(tag, errorMsg);
//            }
//
//            @Override
//            public void onSuccess(String successMsg) {
//                //  データの取得に成功した場合
//                Log.d(tag, successMsg);
//            }
//        }, new ClassRoomsDatabaseManager.ClassRoomMemberGetListener() {
//            @Override
//            public void onMembers(List<String> Members) {
//                //  Membersを利用する場合
//                if(Members != null){
//                    //  Membersが存在する場合
//                    for(String Member: Members){
//                        Log.d(tag, Member);
//                    }
//                }else {
//                    //  Membersが存在しない場合
//                    Log.d(tag, "Members are null");
//                }
//            }
//        });





    //  CS21  //////////
    public static void getClassChatID(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, ClassChatGetListener ClassChatGetListener){
        classRooms.child(classRoomID).child("classRoomChats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    List<String> classChats = new ArrayList<>();
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            String getClassMemberID = chatSnapshot.getKey();
                            classChats.add(getClassMemberID);
                        }
                    }else {
                        classChats = null;
                    }
                    ClassChatGetListener.onClassChats(classChats);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CS21 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS21 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.getClassChatID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.ClassChatGetListener() {
//        @Override
//        public void onClassChats(List<String> classChats) {
//            // classChatsを利用する場合
//            if(classChats != null){
//                //  classChatsが存在する場合
//                for(String classChatID: classChats){
//                    Log.d(tag, classChatID);
//                }
//            }else {
//                //  classChatsが存在しない場合
//                Log.d(tag, "classChats are null");
//            }
//        }
//    });





    //  CS22  //////////
    public static void getChatMessage(String classRoomID, String classRoomChatID, ClassRoomErrorListener ClassRoomErrorListener, ClassRoomChatListener ClassRoomChatListener){
        classRooms.child(classRoomID).child("classRoomChats").child(classRoomChatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  クラスルームのデータが存在する場合
                    ClassRoomsDataModel classRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert classRoomsDataModel != null;
                    String sender = classRoomsDataModel.getSender();
                    String message = classRoomsDataModel.getMessage();
                    ClassRoomChatListener.onClassRoomChat(sender, message);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  クラスルームのデータが存在しない場合
                    Log.d(tag, "CS22 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS22 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し

//    ClassRoomsDatabaseManager.getChatMessage(classRoomID, classRoomChatID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.ClassRoomChatListener() {
//        @Override
//        public void onClassRoomChat(String sender, String message) {
//        }
//    });





    //  CS23  //////////
      public static void getClassMemberDeviceID(String classRoomID, ClassRoomErrorListener classRoomErrorListener, ClassMemberDeviceIdGetListener ClassMemberDeviceIdGetListener) {
          classRooms.child(classRoomID).child("classMembers").addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  List<String> deviceIDs = new ArrayList<>();
                  for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                      String deviceID = memberSnapshot.child("deviceID").getValue(String.class);
                      if (deviceID != null) {
                          deviceIDs.add(deviceID);
                      }
                  }
                  ClassMemberDeviceIdGetListener.onClassMemberDeviceIDs(deviceIDs);
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
                  classRoomErrorListener.onError(databaseError.getMessage());
              }
          });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.getClassMemberDeviceID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.ClassMemberDeviceIdGetListener() {
//        @Override
//        public void onClassMemberDeviceIDs(List<String> classMemberDeviceIDs) {
//            // classMemberDeviceIDsを利用する場合
//            if(classMemberDeviceIDs != null){
//                //  classMemberDeviceIDsが存在する場合
//                for(String classMemberDeviceID: classMemberDeviceIDs){
//                    Log.d(tag, classMemberDeviceID);
//                }
//            }else {
//                //  classMemberDeviceIDsが存在しない場合
//                Log.d(tag, "classMemberDeviceIDs are null");
//            }
//        }
//    });





    //  CS24  //////////
    public static void getDeviceID(String classRoomID, String classMemberID, ClassRoomErrorListener ClassRoomErrorListener, deviceIDGetListener deviceIDGetListener){
        classRooms.child(classRoomID).child("classMembers").child(classMemberID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ClassRoomsDataModel ClassRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert ClassRoomsDataModel != null;
                    String deviceID = ClassRoomsDataModel.getDeviceID();
                    deviceIDGetListener.onDeviceID(deviceID);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "CS24 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "CS24 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.getDeviceID(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.deviceIDGetListener() {
//        @Override
//        public void onDeviceID(String deviceID) {
//            //  deviceIDを利用する場合
//            Log.d(tag, deviceID);
//        }
//    });









    //  CD01  //////////
    public static void deleteTeacherClassMember(String classRoomID, String classMemberID, ClassRoomErrorListener ClassRoomErrorListener){
        getMemberID(classRoomID, classMemberID, new ClassRoomErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データの取得に失敗した場合
                ClassRoomErrorListener.onError(errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの取得に成功した場合
                Log.d(tag, successMsg);
            }
        }, new memberIDGetListener() {
            @Override
            public void onMemberID(String memberID) {
                //  memberIDを利用する場合
                TeachersDatabaseManager.deleteJoinClassRoom(memberID, classRoomID, new TeachersDatabaseManager.TeacherErrorListener() {
                    @Override
                    public void onError(String errorMsg) {
                        //  データの削除に失敗した場合
                        ClassRoomErrorListener.onError(errorMsg);
                    }

                    @Override
                    public void onSuccess(String successMsg) {
                        //  データの削除に成功した場合
                        Log.d(tag, successMsg);
                        classRooms.child(classRoomID).child("classMembers").child(classMemberID).removeValue(((error, ref) -> {
                            if (error != null) {
                                //  削除に失敗した場合
                                Log.d(tag, "CD01 error");
                                String errorMsg = "接続に失敗しました";
                                ClassRoomErrorListener.onError(errorMsg);
                            }else {
                                //  削除に成功した場合
                                String successMsg2 = "接続に成功しました";
                                ClassRoomErrorListener.onSuccess(successMsg2);
                            }
                        }));
                    }
                });
            }
        });
    }

    //  呼び出し  ※転勤ではなく、間違えてクラス参加してしまった場合の削除
//    ClassRoomsDatabaseManager.deleteTeacherClassMember(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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



    //  CD02  //////////
    public static void deleteClassRoom(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener){
        classRooms.child(classRoomID).removeValue(((error, ref) -> {
            if (error != null) {
                //  削除に失敗した場合
                Log.d(tag, "CD02 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }else {
                //  削除に成功した場合
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
            }
        }));
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.deleteClassRoom(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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





    //  CD05  //////////
    public static void deleteStudentClassMember(String classRoomID, String classMemberID, ClassRoomErrorListener ClassRoomErrorListener){
        getMemberID(classRoomID, classMemberID, new ClassRoomErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データの取得に失敗した場合
                ClassRoomErrorListener.onError(errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの取得に成功した場合
                Log.d(tag, successMsg);
            }
        }, new memberIDGetListener() {
            @Override
            public void onMemberID(String memberID) {
                //  memberIDを利用する場合
                StudentsDatabaseManager.deleteJoinClassRoom(memberID, classRoomID, new StudentsDatabaseManager.StudentErrorListener() {
                    @Override
                    public void onError(String errorMsg) {
                        //  データの削除に失敗した場合
                        ClassRoomErrorListener.onError(errorMsg);
                    }

                    @Override
                    public void onSuccess(String successMsg) {
                        //  データの削除に成功した場合
                        Log.d(tag, successMsg);
                        classRooms.child(classRoomID).child("classMembers").child(classMemberID).removeValue(((error, ref) -> {
                            if (error != null) {
                                //  削除に失敗した場合
                                Log.d(tag, "CD05 error");
                                String errorMsg = "接続に失敗しました";
                                ClassRoomErrorListener.onError(errorMsg);
                            }else {
                                //  削除に成功した場合
                                String successMsg2 = "接続に成功しました";
                                ClassRoomErrorListener.onSuccess(successMsg2);
                            }
                        }));
                    }
                });
            }
        });
    }

    //  呼び出し  ※転校ではなく、間違えてクラス参加してしまった場合の削除
//    ClassRoomsDatabaseManager.deleteStudentClassMember(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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



    //  Lotteryノード  //////////////////////////////////////////////////////////////////


    //  LI01  //////////
    public static void setLottery(String classRoomID, String lotteryTitle, ClassRoomErrorListener ClassRoomErrorListener, LotteryIDGetListener LotteryIDGetListener){
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = sdf.format(date);
        String lotteryID = "L" + formattedDate + UUID.randomUUID().toString();
        String lotteryDate = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(new Date());
        ClassRoomsDataModel ClassRoom = new ClassRoomsDataModel();
        ClassRoom.setLotteryTitle(lotteryTitle);
        ClassRoom.setLotteryDate(lotteryDate);

        classRooms.child(classRoomID).child("lottery").child(lotteryID).setValue(ClassRoom, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                Log.d(tag, "LI01 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            } else {
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
                LotteryIDGetListener.onLotteryID(lotteryID);
            }
        });
    }

    //  呼び出し
//        ClassRoomsDatabaseManager.setLottery(classRoomID, lotteryTitle, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//            @Override
//            public void onError(String errorMsg) {
//                //  データ取得に失敗した場合
//                Log.d(tag, errorMsg);
//            }
//
//            @Override
//            public void onSuccess(String successMsg) {
//                //  データ取得に成功した場合
//                Log.d(tag, successMsg);
//            }
//        }, new ClassRoomsDatabaseManager.LotteryIDGetListener() {
//            @Override
//            public void onLotteryID(String lotteryID) {
//                //  lotteryIDを利用する場合
//                Log.d(tag, lotteryID);
//            }
//        });





    //  LI03  //////////
    public static void setLotteryWinner(String classRoomID, String lotteryID, String winnerMemberID, String lotteryResultID, ClassRoomErrorListener classRoomErrorListener) {
        ClassRoomsDataModel classRoom = new ClassRoomsDataModel();
        classRoom.setLotteryResultID(lotteryResultID);
        classRooms.child(classRoomID).child("lottery").child(lotteryID).child("lotteryResults").child(winnerMemberID).setValue(classRoom, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                String errorMsg = "接続に失敗しました";
                classRoomErrorListener.onError(errorMsg);
            } else {
                // データ書き込み成功時
                String successMsg = "接続に成功しました";
                classRoomErrorListener.onSuccess(successMsg);
            }
        });
    }

    //  呼び出し
//        ClassRoomsDatabaseManager.setLotteryWinner(classRoomID, lotteryID, winnerMemberID, lotteryResultID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//            @Override
//            public void onError(String errorMsg) {
//                //  データ取得に失敗した場合
//                Log.d(tag, errorMsg);
//            }
//
//            @Override
//            public void onSuccess(String successMsg) {
//                //  データ取得に成功した場合
//                Log.d(tag, successMsg);
//            }
//        });





    //  LS01  //////////
    public static void getLotteryResult(String classRoomID, String lotteryID, String winnerMemberID, ClassRoomErrorListener ClassRoomErrorListener, LotteryResultListener LotteryResultListener){
        classRooms.child(classRoomID).child("lottery").child(lotteryID).child("lotteryResults").child(winnerMemberID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    ClassRoomsDataModel ClassRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert ClassRoomsDataModel != null;
                    String lotteryResultID = ClassRoomsDataModel.getLotteryResultID();
                    LotteryResultListener.onLotteryResultID(lotteryResultID);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "LS01 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "LS01 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }
    //  呼び出し
//        ClassRoomsDatabaseManager.getLotteryResult(classRoomID, lotteryID, winnerMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//            @Override
//            public void onError(String errorMsg) {
//                //  データ取得に失敗した場合
//                Log.d(tag, errorMsg);
//            }
//
//            @Override
//            public void onSuccess(String successMsg) {
//                //  データ取得に成功した場合
//                Log.d(tag, successMsg);
//            }
//        }, new ClassRoomsDatabaseManager.LotteryResultListener() {
//            @Override
//            public void onLotteryResultID(String lotteryResultID) {
//                //  lotteryResultIDを利用する場合
//                Log.d(tag, lotteryResultID);
//            }
//        });


    //  LS02  //////////
    public static void getLotteryResultItem(String classRoomID, String lotteryID, String lotteryItemID, ClassRoomErrorListener ClassRoomErrorListener, LotteryResultItemListener LotteryResultItemListener){
        classRooms.child(classRoomID).child("lottery").child(lotteryID).child("lotteryItems").child(lotteryItemID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    ClassRoomsDataModel ClassRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert ClassRoomsDataModel != null;
                    String lotteryItem = ClassRoomsDataModel.getLotteryItem();
                    LotteryResultItemListener.onLotteryItem(lotteryItem);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "LS02 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "LS02 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }
    //  呼び出し
//        ClassRoomsDatabaseManager.getLotteryResultItem(classRoomID, lotteryID, lotteryItemID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//            @Override
//            public void onError(String errorMsg) {
//                //  データ取得に失敗した場合
//                Log.d(tag, errorMsg);
//            }
//
//            @Override
//            public void onSuccess(String successMsg) {
//                //  データ取得に成功した場合
//                Log.d(tag, successMsg);
//            }
//        }, new ClassRoomsDatabaseManager.LotteryResultItemListener() {
//            @Override
//            public void onLotteryItem(String lotteryItem) {
//                //  lotteryItemを利用する場合
//                Log.d(tag, lotteryItem);
//            }
//        });




    //  LS03  //////////
    public static void getLotteryID(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, LotteryGetListener LotteryGetListener){
        classRooms.child(classRoomID).child("lottery").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    List<String> lotteries = new ArrayList<>();
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot lottrySnapshot : snapshot.getChildren()) {
                            String getClassMemberID = lottrySnapshot.getKey();
                            lotteries.add(getClassMemberID);
                        }
                    }else {
                        lotteries = null;
                    }
                    LotteryGetListener.onLotteries(lotteries);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "LS03 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "LS03 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    ClassRoomsDatabaseManager.getLotteryID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
//    }, new ClassRoomsDatabaseManager.LotteryGetListener() {
//        @Override
//        public void onLotteries(List<String> lotteries) {
//            // lotteriesを利用する場合
//            if(lotteries != null){
//                //  lotteriesが存在する場合
//                for(String lotteryID: lotteries){
//                    Log.d(tag, lotteryID);
//                }
//            }else {
//                //  lotteriesが存在しない場合
//                Log.d(tag, "lotteries are null");
//            }
//        }
//    });



    //  LS04  //////////
    public static void getLottery(String classRoomID, String lotteryID, ClassRoomErrorListener ClassRoomErrorListener, LotteryListener LotteryListener){
        classRooms.child(classRoomID).child("lottery").child(lotteryID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    ClassRoomsDataModel ClassRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert ClassRoomsDataModel != null;
                    String lotteryTitle = ClassRoomsDataModel.getLotteryTitle();
                    String lotteryDate = ClassRoomsDataModel.getLotteryDate();
                    LotteryListener.onLottery(lotteryTitle, lotteryDate);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "LS04 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "LS04 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }
    //  呼び出し
//        ClassRoomsDatabaseManager.getLottery(classRoomID, lotteryID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
//            @Override
//            public void onError(String errorMsg) {
//                //  データ取得に失敗した場合
//                Log.d(tag, errorMsg);
//            }
//
//            @Override
//            public void onSuccess(String successMsg) {
//                //  データ取得に成功した場合
//                Log.d(tag, successMsg);
//            }
//        }, new ClassRoomsDatabaseManager.LotteryListener() {
//            @Override
//            public void onLottery(String lotteryTitle, String lotteryDate) {
//                //  lotteryTitle, lotteryDateを利用する場合
//                Log.d(tag, lotteryTitle);
//                Log.d(tag, lotteryDate);
//            }






    //  AI01  //////////
    public static void setPhoto(String classRoomID, String albumID, String photo, ClassRoomErrorListener ClassRoomErrorListener){
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = sdf.format(date);
        String photoID = "P" + formattedDate + UUID.randomUUID().toString();
        ClassRoomsDataModel ClassRoom = new ClassRoomsDataModel();
        ClassRoom.setPhoto(photo);
        classRooms.child(classRoomID).child("albums").child(albumID).child("photos").child(photoID).setValue(ClassRoom, (error, ref) -> {
            if (error != null) {
                Log.d(tag, "AI01 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            } else {
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
            }
        });
    }

    //  呼び出し
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
//                        ClassRoomsDatabaseManager.setPhoto(classRoomID, albumID, photo, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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




    //  AI02  //////////
    public static void setAlbum(String classRoomID, String albumName, ClassRoomErrorListener ClassRoomErrorListener, AlbumIDGetListener AlbumIDGetListener){
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = sdf.format(date);
        String albumID = "A" + formattedDate + UUID.randomUUID().toString();

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.US);
        String created = dateFormat.format(currentDate);

        ClassRoomsDataModel ClassRoom = new ClassRoomsDataModel();
        ClassRoom.setAlbumName(albumName);
        ClassRoom.setCreated(created);
        classRooms.child(classRoomID).child("albums").child(albumID).setValue(ClassRoom, (error, ref) -> {
            if (error != null) {
                Log.d(tag, "AI02 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            } else {
                String successMsg = "接続に成功しました";
                ClassRoomErrorListener.onSuccess(successMsg);
                AlbumIDGetListener.onAlbumID(albumID);
            }
        });
    }
    //  呼び出し
//    ClassRoomsDatabaseManager.setAlbum(classRoomID, albumName new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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




    //  AS01  //////////
    public static void getAllAlbum(String classRoomID, ClassRoomErrorListener ClassRoomErrorListener, AlbumGetListener AlbumGetListener){
        classRooms.child(classRoomID).child("albums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    List<String> albums = new ArrayList<>();
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot albumSnapshot : snapshot.getChildren()) {
                            String getAlbumID = albumSnapshot.getKey();
                            albums.add(getAlbumID);
                        }
                    }else {
                        albums = null;
                    }
                    AlbumGetListener.onAlbums(albums);
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "AS01 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "AS01 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });

    }




    //  AS02  //////////
    public static void getAlbum(String classRoomID, String albumID, ClassRoomErrorListener ClassRoomErrorListener, AlbumListener AlbumListener){
        classRooms.child(classRoomID).child("albums").child(albumID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  クラスルームのデータが存在する場合
                    ClassRoomsDataModel classRoomsDataModel = snapshot.getValue(ClassRoomsDataModel.class);
                    assert classRoomsDataModel != null;
                    String albumName = classRoomsDataModel.getAlbumName();
                    String albumDate = classRoomsDataModel.getCreated();
                    classRooms.child(classRoomID).child("albums").child(albumID).child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot firstPhotoSnapshot = snapshot.getChildren().iterator().next();
                            String photo = firstPhotoSnapshot.child("photo").getValue(String.class);
                            AlbumListener.onAlbum(albumName, albumDate, photo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //  データ取得中にエラーが発生した場合
                            Log.d(tag, "AS02 error");
                            String errorMsg = "接続に失敗しました";
                            ClassRoomErrorListener.onError(errorMsg);
                        }
                    });
                    String successMsg = "接続に成功しました";
                    ClassRoomErrorListener.onSuccess(successMsg);
                }else {
                    //  クラスルームのデータが存在しない場合
                    Log.d(tag, "AS02 error");
                    String errorMsg = "接続に失敗しました";
                    ClassRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "AS02 error");
                String errorMsg = "接続に失敗しました";
                ClassRoomErrorListener.onError(errorMsg);
            }
        });
    }



    //  AS03  //////////
      public static void getAllPhoto(String classRoomID, String albumID, ClassRoomErrorListener classRoomErrorListener, PhotoGetListener photoGetListener) {
        classRooms.child(classRoomID).child("albums").child(albumID).child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    List<String> photos = new ArrayList<>();
                    for (DataSnapshot photoSnapshot : snapshot.getChildren()) {
                        String photoValue = photoSnapshot.child("photo").getValue(String.class);
                        if (photoValue != null) {
                            photos.add(photoValue);
                        }
                    }
                    photoGetListener.onPhotos(photos);
                    String successMsg = "接続に成功しました";
                    classRoomErrorListener.onSuccess(successMsg);
                } else {
                    //  データが存在しない場合
                    Log.d(tag, "AS03 error");
                    String errorMsg = "接続に失敗しました";
                    classRoomErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "AS03 error");
                String errorMsg = "接続に失敗しました";
                classRoomErrorListener.onError(errorMsg);
            }
        });
    }








    //  AD01  //////////
    public static void deleteAlbumsWithNoPhotos(String classRoomID) {
        classRooms.child(classRoomID).child("albums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot albumSnapshot : dataSnapshot.getChildren()) {
                    String albumID = albumSnapshot.getKey();
                    if (albumID != null) {
                        checkAndDeleteAlbumIfNoPhotos(classRoomID, albumID);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // エラー時の処理
                Log.e("AlbumManager", "Error retrieving albums: " + databaseError.getMessage());
            }
        });
    }

    private static void checkAndDeleteAlbumIfNoPhotos(String classRoomID, String albumID) {
        classRooms.child(classRoomID).child("albums").child(albumID).child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // photos ノードが存在しない場合、albumIDノードを削除
                    classRooms.child(classRoomID).child("albums").child(albumID).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                // 削除成功時の処理
                                Log.d("AlbumManager", "Album " + albumID + " deleted successfully.");
                            })
                            .addOnFailureListener(e -> {
                                // 削除失敗時の処理
                                Log.e("AlbumManager", "Failed to delete album " + albumID + ": " + e.getMessage());
                            });
                } else {
                    // photos ノードが存在する場合の処理
                    Log.d("AlbumManager", "Album " + albumID + " has photos and will not be deleted.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // エラー時の処理
                Log.e("AlbumManager", "Error checking photos for album " + albumID + ": " + databaseError.getMessage());
            }
        });
    }


}
