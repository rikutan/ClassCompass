package com.example.classcompass;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class QrReadActivity extends AppCompatActivity {
    private final String tag = "QrReadActivity";

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // カメラのパーミッションを確認してリクエスト
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // QRコードスキャンを開始
            startQRScan();
        }
    }



    // QRコードスキャンの開始
    private void startQRScan() {
        new IntentIntegrator(this).initiateScan();
    }

    // パーミッションリクエストの結果を処理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // カメラのパーミッションが与えられた場合、QRコードスキャンを開始
                startQRScan();
            } else {
                Toast.makeText(this, "カメラの使用が許可されていません", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // QRコードスキャンの結果を処理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "キャンセルされました", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // QRコードから取得したデータを変数に格納
                String classRoomID = result.getContents();

                Intent intent = new Intent(QrReadActivity.this, ClassRoomJoinActivity.class);
                intent.putExtra("classRoomID", classRoomID);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}



