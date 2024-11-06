package com.example.classcompass;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class NewClassRoomMakeComp extends AppCompatActivity {
    private static final String TAG = "NewClassRoomMakeComp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_classroom_make_comp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // QRコードを表示する処理
        ImageView qrImageView = findViewById(R.id.new_classroom_make_comp_qrcodeView);
        String qrFileName = getIntent().getStringExtra("qrFileName");
        if (qrFileName != null) {
            File qrFile = new File(getExternalFilesDir(null), qrFileName);
            if (qrFile.exists()) {
                Bitmap qrBitmap = BitmapFactory.decodeFile(qrFile.getAbsolutePath());
                qrImageView.setImageBitmap(qrBitmap);
            } else {
                //ファイルが存在しない場合のログの出力
                Log.d(TAG, "ファイルが存座しません");
            }
        } else {
            //ファイル名が null の場合の出力
            Log.d(TAG, "ファイル名がnullです");
        }


        // トップ画面に戻るボタン
        Button backBtn = findViewById(R.id.new_classroom_make_comp_topBtn);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, top_teacher.class);
            startActivity(intent);
        });
    }
}
