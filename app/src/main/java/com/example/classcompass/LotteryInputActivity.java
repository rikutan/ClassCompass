package com.example.classcompass;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LotteryInputActivity extends AppCompatActivity {
    private final String tag = "LotteryInputActivity";
    Context context = this;
    private RecyclerView recyclerView;
    private LotteryInputAdapter adapter;
    private List<LotteryInputItem> itemList;
    private int count = 1;

    private int winnerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lottery_input);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");


        TextView backBtn = findViewById(R.id.lottery_input_backBtn);
        TextView errorTxt = findViewById(R.id.lottery_input_error);
        EditText title = findViewById(R.id.lottery_input_title);
        Button createBtn = findViewById(R.id.lottery_input_createBtn);
        Button addBtn = findViewById(R.id.lottery_input_add);
        Button customBtn = findViewById(R.id.lottery_input_customBtn);
        recyclerView = findViewById(R.id.lottery_input_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        itemList = new ArrayList<>();

        createBtn.setOnClickListener(v -> {
            String titleText = title.getText().toString();
            if (titleText.isEmpty()) {
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("タイトルを入力してください");
            } else {
                errorTxt.setVisibility(View.INVISIBLE);
                ClassRoomsDatabaseManager.setLottery(classRoomID, titleText, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                    @Override
                    public void onError(String errorMsg) {
                        //  データ取得に失敗した場合
                        Log.d(tag, errorMsg);
                    }

                    @Override
                    public void onSuccess(String successMsg) {
                        //  データ取得に成功した場合
                        Log.d(tag, successMsg);
                    }
                }, new ClassRoomsDatabaseManager.LotteryIDGetListener() {
                    @Override
                    public void onLotteryID(String lotteryID) {
                        //  lotteryIDを利用する場合
                        Log.d(tag, lotteryID);
                        ClassRoomsDatabaseManager.getStudentClassMemberID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
                        }, new ClassRoomsDatabaseManager.ClassMemberGetListener() {
                            @Override
                            public void onClassMembers(List<String> classMembers) {
                                // classMembersを利用する場合
                                if(classMembers != null){
                                    //  classMembersが存在する場合
                                    int numMembers = classMembers.size();
                                    List<Integer> ids = new ArrayList<>();
                                    for (int i = 1; i <= numMembers; i++) {
                                        ids.add(i);
                                    }
                                    Collections.shuffle(ids);
                                    for(String classMemberID: classMembers){
                                        Log.d(tag, classMemberID);
                                        String lotteryResultID = ids.get(0).toString();
                                        ids.remove(0);
                                        ClassRoomsDatabaseManager.setLotteryWinner(classRoomID, lotteryID, classMemberID, lotteryResultID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                                            @Override
                                            public void onError(String errorMsg) {
                                                //  データ取得に失敗した場合
                                                Log.d(tag, errorMsg);
                                            }

                                            @Override
                                            public void onSuccess(String successMsg) {
                                                //  データ取得に成功した場合
                                                Log.d(tag, successMsg);
                                            }
                                        });
                                    }
                                    Intent intent = new Intent(context, LotteryActivity.class);
                                    startActivity(intent);
                                }else {
                                    //  classMembersが存在しない場合
                                    Log.d(tag, "classMembers are null");
                                }
                            }
                        });

                    }
                });
            }
        });

        addBtn.setOnClickListener(v -> {
            String countText = String.valueOf(count);
            itemList.add(new LotteryInputItem(countText, "", ""));
            adapter.notifyDataSetChanged();
            count++;
        });

        adapter = new LotteryInputAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        customBtn.setOnClickListener(v -> {
            winnerCount = 0;
            ClassRoomsDatabaseManager.getStudentClassMemberID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
            }, new ClassRoomsDatabaseManager.ClassMemberGetListener() {
                @Override
                public void onClassMembers(List<String> classMembers) {
                    // classMembersを利用する場合
                    if(classMembers != null){
                        //  classMembersが存在する場合
                        List<LotteryInputItem> items = adapter.getDataList();
                        for (LotteryInputItem item : items) {
                            winnerCount = winnerCount + Integer.parseInt(item.getItemWinner());
                        }
                        int numMembers = classMembers.size();
                        if(winnerCount <= numMembers){
                            errorTxt.setVisibility(View.INVISIBLE);
                            String titleText = title.getText().toString();
                            if (titleText.isEmpty()) {
                                errorTxt.setVisibility(View.VISIBLE);
                                errorTxt.setText("タイトルを入力してください");
                            }else {
                                errorTxt.setVisibility(View.INVISIBLE);
                                ClassRoomsDatabaseManager.setLottery(classRoomID, titleText, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                                    @Override
                                    public void onError(String errorMsg) {
                                        //  データ取得に失敗した場合
                                        Log.d(tag, errorMsg);
                                    }

                                    @Override
                                    public void onSuccess(String successMsg) {
                                        //  データ取得に成功した場合
                                        Log.d(tag, successMsg);
                                    }
                                }, new ClassRoomsDatabaseManager.LotteryIDGetListener() {
                                    @Override
                                    public void onLotteryID(String lotteryID) {
                                        //  lotteryIDを利用する場合
                                        List<String> ids = new ArrayList<>();
                                        for (LotteryInputItem item : items) {
                                            for(int i = 1; i <= Integer.parseInt(item.getItemWinner()); i++){
                                                ids.add(item.getItemName());
                                            }
                                        }
                                        int loserCount = numMembers - winnerCount;
                                        for(int i = 1; i <= loserCount; i++){
                                            ids.add("はずれ");
                                        }
                                        Collections.shuffle(ids);
                                        for(String classMemberID: classMembers){
                                            Log.d(tag, classMemberID);
                                            String lotteryResultID = ids.get(0).toString();
                                            ids.remove(0);
                                            ClassRoomsDatabaseManager.setLotteryWinner(classRoomID, lotteryID, classMemberID, lotteryResultID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                                                @Override
                                                public void onError(String errorMsg) {
                                                    //  データ取得に失敗した場合
                                                    Log.d(tag, errorMsg);
                                                }

                                                @Override
                                                public void onSuccess(String successMsg) {
                                                    //  データ取得に成功した場合
                                                    Log.d(tag, successMsg);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }else{
                            String over = String.valueOf(winnerCount - numMembers);
                            errorTxt.setVisibility(View.VISIBLE);
                            errorTxt.setText("当選人数が" + over + "人超過しています");
                        }
                        Intent intent = new Intent(context, LotteryActivity.class);
                        startActivity(intent);
                    }else {
                        //  classMembersが存在しない場合
                        Log.d(tag, "classMembers are null");
                    }
                }
            });
        });


        backBtn.setOnClickListener(v -> {
            finish();
        });

    }
}