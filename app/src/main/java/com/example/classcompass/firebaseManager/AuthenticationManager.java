package com.example.classcompass.firebaseManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

//  firebaseAuthentication
import com.example.classcompass.databaseManager.TeachersDatabaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//  firebaseRealtimeDatabase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AuthenticationManager {


    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private  static final DatabaseReference users = database.getReference("users");


    private  static final String tag = "AuthenticationManager";

    private static SharedPreferences sharedPreferences;
    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
    }


    public interface AuthenticationErrorListener {
        void onError(String errorMsg);
        void onSuccess(String successMsg);
    }



    //  FI01  //////////
    public static void setUser(String email, String pwd, String userID, AuthenticationErrorListener AuthenticationError){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // アカウント作成に成功した場合
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            users.child(uid).setValue(userID);
                            String successMsg = "接続に成功しました";
                            AuthenticationError.onSuccess(successMsg);
                        }
                    }else {
                        // アカウント作成に失敗した場合
                        Log.d(tag, "FI01 error");
                        String errorMsg = "接続に失敗しました";
                        AuthenticationError.onError(errorMsg);
                    }
                });
    }



    //  呼び出し

//    AuthenticationManager.setUser(teacherMail, pwd, teacherID, new AuthenticationManager.AuthenticationErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  登録に失敗した場合
//            TeacherErrorListener.onError(errorMsg);
//            TeachersDatabaseManager.deleteTeacher(teacherID);
//        }
//        @Override
//        public void onSuccess(String successMsg) {
//            //  登録に成功した場合
//            TeacherErrorListener.onSuccess(successMsg);
//        }
//    });








    //  FU02  //////////
    public static void changeEmail(String email, AuthenticationErrorListener AuthenticationErrorListener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.verifyBeforeUpdateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //  メール送信に成功した場合
                                String successMsg = "送信に成功しました";
                                AuthenticationErrorListener.onSuccess(successMsg);
                            } else {
                                //  メール送信に失敗した場合
                                Log.d(tag, "FU02 error");
                                String errorMsg = "送信に失敗しました";
                                AuthenticationErrorListener.onError(errorMsg);
                            }
                        }
                    });
        }
    }

    //  呼び出し
//    AuthenticationManager.changeEmail(email, new AuthenticationManager.AuthenticationErrorListener() {
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








    //  FS01  //////////
    public static void emailAuth(String email, AuthenticationErrorListener AuthenticationErrorListener){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // リセットリンクが送信された場合
                        AuthenticationErrorListener.onSuccess("送信に成功しました");
                    } else {
                        // エラーが発生した場合
                        Log.d(tag, "FS01 error");
                        AuthenticationErrorListener.onError("送信に失敗しました");
                    }
                });
    }

    //  呼び出し
//    ログイン中に実行可能
//    AuthenticationManager.emailAuth(email, new AuthenticationManager.AuthenticationErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  リンク送付に失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  リンク送付に成功した場合
//            Log.d(tag, successMsg);
//        }
//    });







    //  FS02  //////////
    public static void loginTeacher(String email, String pwd, AuthenticationErrorListener AuthenticationError){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // ログイン成功
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            users.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        //  データが存在する場合
                                        String userID = snapshot.getValue(String.class);
                                        assert userID != null;
                                        if(userID.charAt(0) == 'T'){
                                            saveTeacherID(userID);
                                            String successMsg = "接続に成功しました";
                                            AuthenticationError.onSuccess(successMsg);
                                        }else {
                                            String errorMsg = "接続に失敗しました";
                                            AuthenticationError.onError(errorMsg);
                                            mAuth.signOut();
                                        }
                                    }else {
                                        //  データが存在しない場合
                                        Log.d(tag, "FS02 error");
                                        String errorMsg = "接続に失敗しました";
                                        AuthenticationError.onError(errorMsg);
                                        mAuth.signOut();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    //  データ取得中にエラーが発生した場合
                                    Log.d(tag, "FS02 error");
                                    String errorMsg = "接続に失敗しました";
                                    AuthenticationError.onError(errorMsg);
                                    mAuth.signOut();
                                }
                            });
                        }

                    } else {
                        // ログイン失敗
                        Log.d(tag, "FS02 error");
                        String errorMsg = "接続に失敗しました";
                        AuthenticationError.onError(errorMsg);
                    }
                });
    }

    private static void saveTeacherID(String teacherID) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("teacherID", teacherID);
            editor.putString("userClass", "teacher");
            editor.apply();
        }
    }


    //  呼び出し
//    AuthenticationManager.initialize(getApplicationContext());
//    AuthenticationManager.loginTeacher(teacherMail, pwd, new AuthenticationManager.AuthenticationErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  ログインに失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  ログインに成功した場合
//            SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
//            String teacherID = sharedPreferences.getString("teacherID", "None");
//            Log.d(tag, successMsg);
//        }
//    });






    //  FS03  //////////
    public static void loginStudent(String email, String pwd, AuthenticationErrorListener AuthenticationError){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // ログイン成功
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            users.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        //  データが存在する場合
                                        String userID = snapshot.getValue(String.class);
                                        assert userID != null;
                                        if(userID.charAt(0) == 'S'){
                                            saveStudentID(userID);
                                            String successMsg = "接続に成功しました";
                                            AuthenticationError.onSuccess(successMsg);
                                        }else {
                                            String errorMsg = "接続に失敗しました";
                                            AuthenticationError.onError(errorMsg);
                                            mAuth.signOut();
                                        }
                                    }else {
                                        //  データが存在しない場合
                                        Log.d(tag, "FS03 error");
                                        String errorMsg = "接続に失敗しました";
                                        AuthenticationError.onError(errorMsg);
                                        mAuth.signOut();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    //  データ取得中にエラーが発生した場合
                                    Log.d(tag, "FS033 error");
                                    String errorMsg = "接続に失敗しました";
                                    AuthenticationError.onError(errorMsg);
                                    mAuth.signOut();
                                }
                            });
                        }

                    } else {
                        // ログイン失敗
                        Log.d(tag, "FS033333 error");
                        String errorMsg = "接続に失敗しました";
                        AuthenticationError.onError(errorMsg);
                    }
                });
    }

    private static void saveStudentID(String studentID) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("studentID", studentID);
            editor.putString("userClass", "student");
            editor.apply();
        }
    }


    //  呼び出し
//    AuthenticationManager.initialize(getApplicationContext());
//    AuthenticationManager.loginStudent(studentMail, pwd, new AuthenticationManager.AuthenticationErrorListener() {
//        @Override
//        public void onError(String errorMsg) {
//            //  ログインに失敗した場合
//            Log.d(tag, errorMsg);
//        }
//
//        @Override
//        public void onSuccess(String successMsg) {
//            //  ログインに成功した場合
//            SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
//            String studentID = sharedPreferences.getString("studentID", "None");
//            Log.d(tag, successMsg);
//        }
//    });






}
