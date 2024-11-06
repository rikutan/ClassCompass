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
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    private GridLayout albumContainer;
    private static final String tag = "AlbumActivity";
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String chatStatus = sharedPreferences.getString("AlbumStatus", "None");
        if(chatStatus.equals("1")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("AlbumStatus", "2");
            editor.apply();
            Intent intent = new Intent(AlbumActivity.this, AlbumCreateActivity.class);
            startActivity(intent);
            finish();
        }

        TextView backBtn = findViewById(R.id.album_backBtn);
        Button createBtn = findViewById(R.id.album_createBtn);
        albumContainer = findViewById(R.id.album_container);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumActivity.this, main_select.class);
            startActivity(intent);
        });

        createBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumActivity.this, AlbumCreateActivity.class);
            startActivity(intent);
        });



        String classRoomID = sharedPreferences.getString("classRoomID", "None");
        ClassRoomsDatabaseManager.getAllAlbum(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
        }, new ClassRoomsDatabaseManager.AlbumGetListener() {
            @Override
            public void onAlbums(List<String> albums) {
                // albumsを利用する場合
                if(albums != null){
                    //  albumsが存在する場合
                    for(String albumID: albums){
                        Log.d(tag, albumID);
                        ClassRoomsDatabaseManager.getAlbum(classRoomID, albumID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
                        }, new ClassRoomsDatabaseManager.AlbumListener() {
                            @Override
                            public void onAlbum(String albumName, String albumDate, String photo) {
                                addAlbumView(photo, albumName, albumDate, albumID);
                            }
                        });
                    }
                }else {
                    //  albumsが存在しない場合
                    Log.d(tag, "albums are null");
                }
            }
        });


    }


    private void addAlbumView(String photo, String albumName, String albumDate, String albumID) {
        View albumView = LayoutInflater.from(this).inflate(R.layout.album_item, null);
        classIconImageView photoItem = albumView.findViewById(R.id.album_photo);
        TextView title = albumView.findViewById(R.id.album_title);
        TextView date = albumView.findViewById(R.id.album_date);
        View view = albumView.findViewById(R.id.album_view);
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        albumView.setLayoutParams(params);

        albumContainer.addView(albumView);

        // FirebaseStorageへの参照を取得
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Log.d(tag + "photo:", photo);

        // 取得したい画像のパスを指定
        String iconPath = "classRooms/" + classRoomID + "/albums/" + albumID + "/" + photo;

        // 画像をダウンロードするための参照を取得
        StorageReference imageRef = storageRef.child(iconPath);

        // 画像をダウンロード
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // 画像が正常にダウンロードされた場合の処理
                Glide.with(context).load(uri).into(photoItem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 画像のダウンロード中にエラーが発生した場合の処理
            }
        });

        title.setText(albumName);
        date.setText("作成日: " + albumDate);

        view.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumActivity.this, AlbumPhotoActivity.class);
            intent.putExtra("albumID", albumID);
            intent.putExtra("albumName", albumName);
            intent.putExtra("albumDate", albumDate);
            startActivity(intent);
        });
    }
}