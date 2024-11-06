package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class LotteryActivity extends AppCompatActivity {
    private final String tag = "LotteryActivity";
    Context context = this;
    private RecyclerView recyclerView;
    private LotteryAdapter adapter;
    private List<LotteryItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        Button createBtn = findViewById(R.id.lottery_allBtn);
        TextView backBtn = findViewById(R.id.lottery_backBtn);
        recyclerView = findViewById(R.id.lotteryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        itemList = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");

        ClassRoomsDatabaseManager.getLotteryID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
        }, new ClassRoomsDatabaseManager.LotteryGetListener() {
            @Override
            public void onLotteries(List<String> lotteries) {
                // lotteriesを利用する場合
                if(lotteries != null){
                    //  lotteriesが存在する場合
                    for(String lotteryID: lotteries){
                        itemList.add(new LotteryItem(lotteryID));
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    //  lotteriesが存在しない場合
                    Log.d(tag, "lotteries are null");
                }
            }
        });
        adapter = new LotteryAdapter(this, itemList);
        recyclerView.setAdapter(adapter);



        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, main_select.class);
            startActivity(intent);
        });


        createBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, LotteryInputActivity.class);
            startActivity(intent);
        });

    }
}