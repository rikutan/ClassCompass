package com.example.classcompass;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

public class AlbumPhotoActivity extends AppCompatActivity {
    private GridLayout albumPhotoContainer;
    private static final String tag = "AlbumPhotoActivity";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photo);
        context = this;

        TextView albumName = findViewById(R.id.album_photo_title);
        TextView albumDate = findViewById(R.id.album_photo_date);
        TextView backBtn = findViewById(R.id.album_photo_backBtn);
        albumPhotoContainer = findViewById(R.id.album_photo_container);

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");

        Intent intent = getIntent();
        String albumID = intent.getStringExtra("albumID");
        String title = intent.getStringExtra("albumName");
        String date = intent.getStringExtra("albumDate");
        albumName.setText(title);
        albumDate.setText("作成日: " + date);

        ClassRoomsDatabaseManager.getAllPhoto(classRoomID, albumID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
            @Override
            public void onError(String errorMsg) {
                Log.d(tag, errorMsg);
            }

            @Override
            public void onSuccess(String successMsg) {
                Log.d(tag, successMsg);
            }
        }, new ClassRoomsDatabaseManager.PhotoGetListener() {
            @Override
            public void onPhotos(List<String> photos) {
                if (photos != null) {
                    for (String photo : photos) {
                        addAlbumPhotoView(photo, albumID);
                    }
                } else {
                    Log.d(tag, "photos are null");
                }
            }
        });
        
        backBtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(context, AlbumActivity.class);
            startActivity(intent1);
        });
    }

    private void addAlbumPhotoView(String photo, String albumID) {
        View albumPhotoView = LayoutInflater.from(this).inflate(R.layout.album_photo_item, null);
        classIconImageView photoItem = albumPhotoView.findViewById(R.id.album_photo_photo);

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        albumPhotoView.setLayoutParams(params);

        albumPhotoContainer.addView(albumPhotoView);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String iconPath = "classRooms/" + classRoomID + "/albums/" + albumID + "/" + photo;
        StorageReference imageRef = storageRef.child(iconPath);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(photoItem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // エラー処理
            }
        });

        photoItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoViewActivity.class);
            intent.putExtra("photo", photo);
            intent.putExtra("albumID", albumID);
            startActivity(intent);
        });

    }

}
