package com.example.classcompass;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AlbumCreateActivity extends AppCompatActivity {
    private final String tag = "AlbumCreateActivity";
    private GridLayout albumCreateContainer;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private List<Uri> selectedImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_create);

        EditText title = findViewById(R.id.album_create_title);
        Button createBtn = findViewById(R.id.album_create_createBtn);
        Button cancelBtn = findViewById(R.id.album_create_cancelBtn);
        Button addBtn = findViewById(R.id.album_create_addBtn);
        TextView errorTxt = findViewById(R.id.album_create_error);
        albumCreateContainer = findViewById(R.id.album_create_container);

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");

        String chatStatus = sharedPreferences.getString("AlbumStatus", "None");
        if (chatStatus.equals("2")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("AlbumStatus", "3");
            editor.apply();
            errorTxt.setVisibility(View.VISIBLE);
            errorTxt.setText("作成に失敗しました。もう一度やり直してください");
        }

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
            selectedImages.clear();
            selectedImages.addAll(uris);
            albumCreateContainer.removeAllViews();
            for (Uri uri : selectedImages) {
                addAlbumCreateView(uri.toString());
            }
        });

        addBtn.setOnClickListener(view -> {
            imagePickerLauncher.launch("image/*");
        });

        createBtn.setOnClickListener(view -> {
            String albumName = title.getText().toString();
            if (chatStatus.equals("0")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("AlbumStatus", "1");
                editor.apply();
            }
            if (albumName.isEmpty()) {
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("アルバム名を入力してください");
            } else {
                errorTxt.setVisibility(View.INVISIBLE);
                errorTxt.setText("");
                ClassRoomsDatabaseManager.setAlbum(classRoomID, albumName, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                    @Override
                    public void onError(String errorMsg) {
                        // データ書き込み失敗時
                        Log.e(tag, "Failed to set album: " + errorMsg);
                    }

                    @Override
                    public void onSuccess(String successMsg) {
                        // データ書き込み成功時
                        Log.d(tag, "Album set successfully: " + successMsg);
                    }
                }, new ClassRoomsDatabaseManager.AlbumIDGetListener() {
                    @Override
                    public void onAlbumID(String albumID) {
                        // Handle albumID if needed
                        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("albumID", albumID);
                        editor.apply();
                        uploadImagesToFirebase();
                        Intent intent = new Intent(AlbumCreateActivity.this, AlbumActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });

        cancelBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AlbumCreateActivity.this, AlbumActivity.class);
            startActivity(intent);
        });

        ClassRoomsDatabaseManager.deleteAlbumsWithNoPhotos(classRoomID);
    }

    private void addAlbumCreateView(String photoUri) {
        View albumCreateView = LayoutInflater.from(this).inflate(R.layout.album_create_item, null);
        classIconImageView photoItem = albumCreateView.findViewById(R.id.album_create_photo);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        albumCreateView.setLayoutParams(params);

        albumCreateContainer.addView(albumCreateView);

        // Glideを使用して画像を表示
        Glide.with(this)
                .load(photoUri)
                .into(photoItem);
    }

    private void uploadImagesToFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("classRooms");
        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");
        String albumID = sharedPreferences.getString("albumID", "None");

        for (Uri imageUri : selectedImages) {
            // 拡張子を取得
            String extension = getFileExtension(this, imageUri);
            String fileName = System.currentTimeMillis() + (extension != null ? "." + extension : "");

            StorageReference fileReference = storageReference
                    .child(classRoomID)
                    .child("albums")
                    .child(albumID)
                    .child(fileName);

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(tag, "Upload successful: " + imageUri.toString());
                        ClassRoomsDatabaseManager.setPhoto(classRoomID, albumID, fileName, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                            @Override
                            public void onError(String errorMsg) {
                                Log.d(tag, errorMsg);
                            }

                            @Override
                            public void onSuccess(String successMsg) {
                                Log.d(tag, successMsg);
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(tag, "Upload failed: " + e.getMessage());
                    });
        }
    }

    public String getFileExtension(Context context, Uri uri) {
        String extension = null;
        ContentResolver contentResolver = context.getContentResolver();
        String mimeType = contentResolver.getType(uri);
        if (mimeType != null) {
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }
        return extension;
    }
}
