package com.example.classcompass.databaseManager;


import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.example.classcompass.dataModels.StudentsDataModel;
import com.example.classcompass.dataModels.TeachersDataModel;
import com.example.classcompass.firebaseManager.AuthenticationManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class StudentsDatabaseManager {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private  static final DatabaseReference students = database.getReference("students");
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final StorageReference userIconStorage = storage.getReference("user_icon");
    private  static final String tag = "StudentsDBM";


    public interface StudentErrorListener {
        void onError(String errorMsg);
        void onSuccess(String successMsg);
    }



    public interface StudentGetListener {
        void onStudentName(String studentName);
        void onStudentIcon(String studentIcon);
        void onClassRooms(List<String> classRooms);
    }




    public interface StudentDetailGetListener {
        void onStudentName(String studentName);
        void onStudentIcon(String studentIcon);
        void onStudentMail(String studentMail);
        void onStudentBirthday(String studentBirthday);
        void onStudentComment(String studentComment);
    }




    public interface StudentProfileGetListener {
        void onStudentName(String studentName);
        void onStudentIcon(String studentIcon);
        void onStudentBirthday(String studentBirthday);
        void onStudentComment(String studentComment);
    }


    public interface ChatStudentGetListener {
        void onStudentNameAndIcon(String studentName, String studentIcon);
    }


    public interface StudentJoinClassRoomGetListener {
        void onClassRooms(List<String> classRooms);
    }


    public interface StudentMemberIdGetListener {
        void onStudentMemberID(String classMemberID);
    }



    //  SI01  //////////
    public static void setStudent(String pwd, String studentName, String studentNameKana, String studentMail, String studentBirthday, StudentErrorListener StudentErrorListener) {

        students.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String studentID = "S" + UUID.randomUUID().toString();

                StudentsDataModel Student = new StudentsDataModel();
                Student.setStudentName(studentName);
                Student.setStudentNameKana(studentNameKana);
                Student.setStudentMail(studentMail);
                Student.setStudentBirthday(studentBirthday);

                students.child(studentID).setValue(Student, (error, ref) -> {
                    if (error != null) {
                        // データ書き込み失敗時
                        Log.d(tag, "SI01 error");
                        String errorMsg = "接続に失敗しました";
                        StudentErrorListener.onError(errorMsg);
                    }else {
                        //  データの書き込みに成功した場合
                        AuthenticationManager.setUser(studentMail, pwd, studentID, new AuthenticationManager.AuthenticationErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                StudentErrorListener.onError(errorMsg);
                                StudentsDatabaseManager.deleteStudent(studentID);
                            }
                            @Override
                            public void onSuccess(String successMsg) {
                                StudentErrorListener.onSuccess(successMsg);
                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得失敗時
                Log.d(tag, "SI01 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }
        });
    }



    //  呼び出し
//    StudentsDatabaseManager.setStudent(pwd, studentName, studentNameKana, studentMail, studentBirthday, new StudentsDatabaseManager.StudentErrorListener() {
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






    //  SI02  //////////
    public static void setJoinClassRoom(String studentID, String joinClassRoomID, String classMemberID, StudentErrorListener StudentErrorListener){
        students.child(studentID).child("classRooms").child(joinClassRoomID).setValue(classMemberID, ((error, ref) -> {
            if (error != null){
                //  データの書き込みに失敗した場合
                Log.d(tag, "SI02 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }else {
                //  データの書き込みに成功した場合
                String successMsg = "接続に成功しました";
                StudentErrorListener.onSuccess(successMsg);
            }
        }));
    }

    //  呼び出し
//    StudentsDatabaseManager.setJoinClassRoom(studentID, classRoomID, classMemberID, new StudentsDatabaseManager.StudentErrorListener() {
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







    //  SU01  //////////
    public static void setStudentIcon(String studentID, File studentIcon, StudentErrorListener StudentErrorListener){
        // ファイルをFirebase Storageにアップロード
        StorageReference path = userIconStorage.child(studentIcon.getName());
        uploadFileToFirebaseStorage(studentIcon, path, new StudentErrorListener() {
            @Override
            public void onError(String errorMsg) {
                Log.d(tag, "SU01 error");
                StudentErrorListener.onError(errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                students.child(studentID).child("studentIcon").setValue(studentIcon.getName(), (error, ref) -> {
                    if (error != null) {
                        Log.d(tag, "SU01 error");
                        String errorMsg = "接続に失敗しました";
                        StudentErrorListener.onError(errorMsg);
                    } else {
                        StudentErrorListener.onSuccess(successMsg);
                    }
                });
            }
        });
    }
    private static void uploadFileToFirebaseStorage(File file, StorageReference path, StudentErrorListener StudentErrorListener) {
        if (file != null) {
            path.putFile(Uri.fromFile(file))
                    .addOnSuccessListener(taskSnapshot -> {
                        // アップロードに成功した場合
                        StudentErrorListener.onSuccess("アップロードに成功しました");
                    })
                    .addOnFailureListener(e -> {
                        // アップロードに失敗した場合
                        StudentErrorListener.onError("アップロードに失敗しました");
                    });
        }
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
//                        StudentsDatabaseManager.setStudentIcon(studentID, imageFile, new StudentsDatabaseManager.StudentErrorListener() {
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








    //  SU02  //////////
    public static void setStudentComment(String studentID, String studentComment, StudentErrorListener StudentErrorListener){
        students.child(studentID).child("studentComment").setValue(studentComment, (error, ref) -> {
            if (error != null) {
                // データ書き込み失敗時
                Log.d(tag, "SU02 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }else {
                //  データの書き込みに成功した場合
                String successMsg = "接続に成功しました";
                StudentErrorListener.onSuccess(successMsg);
            }
        });
    }

    // 呼び出し
//    StudentsDatabaseManager.setStudentComment(studentID, studentComment, new StudentsDatabaseManager.StudentErrorListener() {
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






    //  SU03  //////////
    public static void setStudentMail(String studentID, String studentMail, StudentErrorListener StudentErrorListener){
        AuthenticationManager.changeEmail(studentMail, new AuthenticationManager.AuthenticationErrorListener() {
            @Override
            public void onError(String errorMsg) {
                //  データの変更に失敗した場合
                StudentErrorListener.onError(errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                //  データの変更に成功した場合
                students.child(studentID).child("studentMail").setValue(studentMail, (error, ref) -> {
                    if (error != null) {
                        // データ書き込み失敗時
                        Log.d(tag, "SU03 error");
                        String errorMsg = "接続に失敗しました";
                        StudentErrorListener.onError(errorMsg);
                    }else {
                        //  データの書き込みに成功した場合
                        String successMsg2 = "接続に成功しました";
                        StudentErrorListener.onSuccess(successMsg2);
                    }
                });
            }
        });
    }

    //  呼び出し
//    StudentsDatabaseManager.setStudentMail(studentID, studentMail, new StudentsDatabaseManager.StudentErrorListener() {
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












    //  SS01  //////////
    public static void getStudent(String studentID, StudentErrorListener StudentErrorListener, StudentGetListener StudentGetListener){
        students.child(studentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    StudentsDataModel studentsDataModel = snapshot.getValue(StudentsDataModel.class);
                    assert studentsDataModel != null;
                    String studentName = studentsDataModel.getStudentName();
                    StudentGetListener.onStudentName(studentName);
                    String studentIcon = studentsDataModel.getStudentIcon();
                    StudentGetListener.onStudentIcon(studentIcon);

                    List<String> classRooms = new ArrayList<>();
                    if (snapshot.child("classRooms").exists() && snapshot.child("classRooms").getChildrenCount() > 0) {
                        for (DataSnapshot classSnapshot : snapshot.child("classRooms").getChildren()) {
                            String joinClassRoomID = classSnapshot.getKey();
                            classRooms.add(joinClassRoomID);
                        }
                    }else {
                        classRooms = null;
                    }
                    StudentGetListener.onClassRooms(classRooms);
                    String successMsg = "接続に成功しました";
                    StudentErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "SS01 error");
                    String errorMsg = "接続に失敗しました";
                    StudentErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "SS01 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    StudentsDatabaseManager.getStudent(studentID, new StudentsDatabaseManager.StudentErrorListener() {
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
//    }, new StudentsDatabaseManager.StudentGetListener() {
//        @Override
//        public void onStudentName(String studentName) {
//            //  studentNameを利用する場合
//            Log.d(tag, studentName);
//        }
//
//        @Override
//        public void onStudentIcon(String studentIcon) {
//            //  studentIconを利用する場合
//            if(studentIcon != null){
//                //  studentIconが存在する場合
//                Log.d(tag, studentIcon);
//            }else {
//                //  studentIconが存在しない場合
//                Log.d(tag, "studentIcon is null");
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








    //  SS02  //////////
    public static void getDetailStudent(String studentID, StudentErrorListener StudentErrorListener, StudentDetailGetListener StudentDetailGetListener){
        students.child(studentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    StudentsDataModel studentsDataModel = snapshot.getValue(StudentsDataModel.class);
                    assert studentsDataModel != null;
                    String studentName = studentsDataModel.getStudentName();
                    StudentDetailGetListener.onStudentName(studentName);
                    String studentIcon = studentsDataModel.getStudentIcon();
                    StudentDetailGetListener.onStudentIcon(studentIcon);
                    String studentMail = studentsDataModel.getStudentMail();
                    StudentDetailGetListener.onStudentMail(studentMail);
                    String studentBirthday = studentsDataModel.getStudentBirthday();
                    StudentDetailGetListener.onStudentBirthday(studentBirthday);
                    String studentComment = studentsDataModel.getStudentComment();
                    StudentDetailGetListener.onStudentComment(studentComment);

                    String successMsg = "接続に成功しました";
                    StudentErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "SS02 error");
                    String errorMsg = "接続に失敗しました";
                    StudentErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "SS02 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    Context context = this;
//    StudentsDatabaseManager.getDetailStudent(studentID, new StudentsDatabaseManager.StudentErrorListener() {
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
//    }, new StudentsDatabaseManager.StudentDetailGetListener() {
//        @Override
//        public void onStudentName(String studentName) {
//            //  studentNameを利用する場合
//            Log.d(tag, studentName);
//        }
//
//        @Override
//        public void onStudentIcon(String studentIcon) {
//            //  studentIconを利用する場合
//            if(studentIcon != null){
//                //  studentIconが存在する場合
//                // FirebaseStorageへの参照を取得
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference();
//
//                // 取得したい画像のパスを指定
//                String iconPath = "user_icon/" + StudentIcon;
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
//                //  studentIconが存在しない場合
//                Log.d(tag, "studentIcon is null");
//            }
//        }
//
//        @Override
//        public void onStudentMail(String studentMail) {
//            //  studentMailを利用する場合
//            Log.d(tag, studentMail);
//        }
//
//        @Override
//        public void onStudentBirthday(String studentBirthday) {
//            //  studentBirthdayを利用する場合
//            Log.d(tag, studentBirthday);
//        }
//
//        @Override
//        public void onStudentComment(String studentComment) {
//            //  studentCommentを利用する場合
//            if(studentComment != null){
//                //  studentCommentが存在する場合
//                Log.d(tag, studentComment);
//            }else {
//                //  studentCommentが存在しない場合
//                Log.d(tag, "studentComment is null");
//            }
//        }
//    });







    //  SS03  //////////
    public static void getChatStudent(String studentID, StudentErrorListener StudentErrorListener, ChatStudentGetListener ChatStudentGetListener){
        students.child(studentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    StudentsDataModel studentsDataModel = snapshot.getValue(StudentsDataModel.class);
                    assert studentsDataModel != null;
                    String studentName = studentsDataModel.getStudentName();
                    String studentIcon = studentsDataModel.getStudentIcon();
                    ChatStudentGetListener.onStudentNameAndIcon(studentName, studentIcon);

                    String successMsg = "接続に成功しました";
                    StudentErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "SS0333 error");
                    String errorMsg = "接続に失敗しました";
                    StudentErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "SS03 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    StudentsDatabaseManager.getChatStudent(studentID, new StudentsDatabaseManager.StudentErrorListener() {
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
//    }, new StudentsDatabaseManager.ChatStudentGetListener() {
//        @Override
//        public void onStudentName(String studentName) {
//            //  studentNameを利用する場合
//                Log.d(tag, studentName);
//        }
//
//        @Override
//        public void onStudentIcon(String studentIcon) {
//            //  studentIconを利用する場合
//            if(studentIcon != null){
//                //  studentIconが存在する場合
//                Log.d(tag, studentIcon);
//            }else{
//                //  studentIconが存在しない場合
//                Log.d(tag, "studentIcon is null");
//            }
//        }
//    });





    //  SS04  //////////
    public static void getProfileStudent(String StudentID, StudentErrorListener StudentErrorListener, StudentProfileGetListener StudentProfileGetListener){
        students.child(StudentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    StudentsDataModel studentsDataModel = snapshot.getValue(StudentsDataModel.class);
                    assert studentsDataModel != null;
                    String teacherName = studentsDataModel.getStudentName();
                    StudentProfileGetListener.onStudentName(teacherName);
                    String teacherIcon = studentsDataModel.getStudentIcon();
                    StudentProfileGetListener.onStudentIcon(teacherIcon);
                    String teacherBirthday = studentsDataModel.getStudentBirthday();
                    StudentProfileGetListener.onStudentBirthday(teacherBirthday);
                    String teacherComment = studentsDataModel.getStudentComment();
                    StudentProfileGetListener.onStudentComment(teacherComment);

                    String successMsg = "接続に成功しました";
                    StudentErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "SS04 error");
                    String errorMsg = "接続に失敗しました";
                    StudentErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "SS04 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    Context context = this;
//    TeachersDatabaseManager.getProfileStudent(StudentID, new StudentsDatabaseManager.StudentErrorListener() {
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
//    }, new StudentsDatabaseManager.StudentProfileGetListener() {
//        @Override
//        public void onStudentName(String StudentName) {
//            //  StudentNameを利用する場合
//            Log.d(tag, StudentName);
//        }
//
//        @Override
//        public void onStudentIcon(String StudentIcon) {
//            //  StudentIconを利用する場合
//            if(StudentIcon != null){
//                //  StudentIconが存在する場合
//                // FirebaseStorageへの参照を取得
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference();
//
//                // 取得したい画像のパスを指定
//                String iconPath = "user_icon/" + StudentIcon;
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
//                // StudentIconが存在しない場合
//                Log.d(tag, "StudentIcon is null");
//            }
//        }
//
//        @Override
//        public void onStudentBirthday(String StudentBirthday) {
//            //  StudentBirthdayを利用する場合
//            Log.d(tag, StudentBirthday);
//        }
//
//        @Override
//        public void onStudentComment(String StudentComment) {
//            //  StudentCommentを利用する場合
//            if(StudentComment != null){
//                //  StudentCommentが存在する場合
//                Log.d(tag, StudentComment);
//            }else {
//                //  StudentCommentが存在しない場合
//                Log.d(tag, "StudentComment is null");
//            }
//        }
//    });







    //  SS05  //////////
    public static void getStudentJoinClassRoom(String studentID, StudentErrorListener StudentErrorListener, StudentJoinClassRoomGetListener StudentJoinClassRoomGetListener){
        students.child(studentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    StudentsDataModel studentsDataModel = snapshot.getValue(StudentsDataModel.class);
                    assert studentsDataModel != null;

                    List<String> classRooms = new ArrayList<>();
                    if (snapshot.child("classRooms").exists() && snapshot.child("classRooms").getChildrenCount() > 0) {
                        for (DataSnapshot classSnapshot : snapshot.child("classRooms").getChildren()) {
                            String joinClassRoomID = classSnapshot.getKey();
                            classRooms.add(joinClassRoomID);
                        }
                    }else {
                        classRooms = null;
                    }
                    StudentJoinClassRoomGetListener.onClassRooms(classRooms);
                    String successMsg = "接続に成功しました";
                    StudentErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "SS05 error");
                    String errorMsg = "接続に失敗しました";
                    StudentErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "SS05 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    StudentsDatabaseManager.getStudentJoinClassRoom(studentID, new StudentsDatabaseManager.StudentErrorListener() {
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
//    }, new StudentsDatabaseManager.StudentJoinClassRoomGetListener() {
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





    //  SS06  //////////
    public static void getStudentMemberID(String studentID, String classRoomID, StudentErrorListener StudentErrorListener, StudentMemberIdGetListener StudentMemberIdGetListener){
        students.child(studentID).child("classRooms").child(classRoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //  データが存在する場合
                    String classMemberID = snapshot.getValue(String.class);
                    StudentMemberIdGetListener.onStudentMemberID(classMemberID);

                    String successMsg = "接続に成功しました";
                    StudentErrorListener.onSuccess(successMsg);
                }else {
                    //  データが存在しない場合
                    Log.d(tag, "SS06 error");
                    String errorMsg = "接続に失敗しました";
                    StudentErrorListener.onError(errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  データ取得中にエラーが発生した場合
                Log.d(tag, "SS06 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }
        });
    }

    //  呼び出し
//    StudentsDatabaseManager.getStudentMemberID(studentID, classRoomID, new StudentsDatabaseManager.StudentErrorListener() {
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
//    }, new StudentsDatabaseManager.StudentMemberIdGetListener() {
//        @Override
//        public void onStudentMemberID(String classMemberID) {
//            //  classMemberIDを利用する場合
//                Log.d(tag, classMemberID);
//        }
//    });








    //  SD01  //////////
    public static void deleteJoinClassRoom(String studentID, String joinClassRoomID, StudentErrorListener StudentErrorListener){
        students.child(studentID).child("classRooms").child(joinClassRoomID).removeValue(((error, ref) -> {
            if (error != null) {
                //  削除に失敗した場合
                Log.d(tag, "SD01 error");
                String errorMsg = "接続に失敗しました";
                StudentErrorListener.onError(errorMsg);
            }else {
                //  削除に成功した場合
                String successMsg = "接続に成功しました";
                StudentErrorListener.onSuccess(successMsg);
            }
        }));
    }

    //  呼び出し
//    StudentsDatabaseManager.deleteJoinClassRoom(studentID, joinClassRoomID, new StudentsDatabaseManager.StudentErrorListener() {
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




    //  SD02  //////////
    public static void deleteStudent(String studentID){
        students.child(studentID).removeValue();
    }

    //  呼び出し
//  StudentsDatabaseManager.deleteStudent(studentID);


}
