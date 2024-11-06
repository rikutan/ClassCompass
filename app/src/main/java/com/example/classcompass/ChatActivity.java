package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.example.classcompass.databaseManager.StudentsDatabaseManager;
import com.example.classcompass.databaseManager.TeachersDatabaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private final String tag = "ChatActivity";
    private final String TAG = "ChatActivity";
    Context context = this;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatAdapterItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();


        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");


        ClassRoomsDatabaseManager.getClassName(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
        }, new ClassRoomsDatabaseManager.ClassNameListener() {
            @Override
            public void onClassName(String className) {
                //  classNameを利用する場合
                TextView chatRoomName = findViewById(R.id.chatRoomName);
                chatRoomName.setText(className);
            }
        });



        String chatStatus = sharedPreferences.getString("chatStatus", "None");
        if(chatStatus.equals("0")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("chatStatus", "1");
            editor.apply();
            Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }
        if(chatStatus.equals("1")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("chatStatus", "2");
            editor.apply();
            Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }

        ClassRoomsDatabaseManager.getClassChatID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
        }, new ClassRoomsDatabaseManager.ClassChatGetListener() {
            @Override
            public void onClassChats(List<String> classChats) {
                // classChatsを利用する場合
                if(classChats != null){
                    //  classChatsが存在する場合
                    for(String classChatID: classChats){
                        Log.d(tag, classChatID);

                        ClassRoomsDatabaseManager.getChatMessage(classRoomID, classChatID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
                        }, new ClassRoomsDatabaseManager.ClassRoomChatListener() {
                            @Override
                            public void onClassRoomChat(String sender, String message) {
                                Log.d(tag, sender);
                                Log.d(tag, message);
                                ClassRoomsDatabaseManager.getMemberID(classRoomID, sender, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
                                }, new ClassRoomsDatabaseManager.memberIDGetListener() {
                                    @Override
                                    public void onMemberID(String memberID) {
                                        //  memberIDを利用する場合
                                        String memberClass = sender.substring(0, 2);
                                        Log.d(tag,"memberClass:"+memberClass+",  memberID:"+memberID);
                                        if(memberClass.equals("CT")){
                                            TeachersDatabaseManager.getChatTeacher(memberID, new TeachersDatabaseManager.TeacherErrorListener() {

                                                @Override
                                                public void onError(String errorMsg) {
                                                    //  データの取得に失敗した場合
                                                    Log.d(tag, errorMsg+"ttt");
                                                }

                                                @Override
                                                public void onSuccess(String successMsg) {
                                                    //  データの取得に成功した場合
                                                    Log.d(tag, successMsg+"ttt");
                                                }
                                            }, new TeachersDatabaseManager.ChatTeacherGetListener() {
                                                @Override
                                                public void onTeacherNameAndIcon(String teacherName, String teacherIcon) {
                                                    itemList.add(new ChatAdapterItem(sender, teacherIcon, teacherName, message));
                                                    adapter.notifyDataSetChanged();
                                                    recyclerView.scrollToPosition(itemList.size() - 1);
                                                }
                                            });

                                        }if(memberClass.equals("CS")) {
                                            StudentsDatabaseManager.getChatStudent(memberID, new StudentsDatabaseManager.StudentErrorListener() {

                                                @Override
                                                public void onError(String errorMsg) {
                                                    //  データの取得に失敗した場合
                                                    Log.d(tag, errorMsg+"sss");
                                                }

                                                @Override
                                                public void onSuccess(String successMsg) {
                                                    //  データの取得に成功した場合
                                                    Log.d(tag, successMsg+"sss");
                                                }
                                            }, new StudentsDatabaseManager.ChatStudentGetListener() {
                                                @Override
                                                public void onStudentNameAndIcon(String studentName, String studentIcon) {
                                                    itemList.add(new ChatAdapterItem(sender, studentIcon, studentName, message));
                                                    adapter.notifyDataSetChanged();
                                                    recyclerView.scrollToPosition(itemList.size() - 1);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });



                    }
                }else {
                    //  classChatsが存在しない場合
                    Log.d(tag, "classChats are null");
                }
            }
        });
        adapter = new ChatAdapter(this, itemList);
        recyclerView.setAdapter(adapter);






        //  メッセージ送信  //////////
        ImageButton chatSendBtn = findViewById(R.id.chat_send_btn);
        String classMemberID = sharedPreferences.getString("classMemberID", "None");
        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText chatInput = findViewById(R.id.chat_input);
                String chatText = chatInput.getText().toString();
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(itemList.size() - 1);
                if(!chatText.isEmpty()){

                    ClassRoomsDatabaseManager.setClassRoomChat(classRoomID, classMemberID, chatText, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                        @Override
                        public void onError(String errorMsg) {
                            Log.d(tag, errorMsg);
                        }

                        @Override
                        public void onSuccess(String successMsg) {
                            Log.d(tag, successMsg);
                            itemList.clear();
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(itemList.size() - 1);
                            chatInput.setText("");



                            //  メッセージ通知  //////////
                            sendNotification();



                        }
                    });

                }
            }
        });


        TextView chatBackBtn = findViewById(R.id.chatBackBtn);
        chatBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, main_select.class);
                startActivity(intent);
            }
        });



        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(itemList.size() - 1); // 最後のアイテムにスクロール
            }
        });




        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Subscribed to topic 'all'");
                        } else {
                            Log.e(TAG, "Failed to subscribe to topic 'all'", task.getException());
                        }
                    }
                });





    }


    //  メッセージ通知　　 //////////
    private void sendNotification() {
        OkHttpClient client = new OkHttpClient();
        //  ここに通知をする端末のトークンを登録していく
        JSONArray tokens = new JSONArray();

        SharedPreferences sharedPreferences = getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        String classRoomID = sharedPreferences.getString("classRoomID", "None");
        String classMemberID = sharedPreferences.getString("classMemberID", "None");

        ClassRoomsDatabaseManager.getDeviceID(classRoomID, classMemberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
        }, new ClassRoomsDatabaseManager.deviceIDGetListener() {
            @Override
            public void onDeviceID(String deviceID) {
                //  deviceIDを利用する場合
                Log.d(tag, deviceID);

                ClassRoomsDatabaseManager.getClassMemberDeviceID(classRoomID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
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
                }, new ClassRoomsDatabaseManager.ClassMemberDeviceIdGetListener() {
                    @Override
                    public void onClassMemberDeviceIDs(List<String> classMemberDeviceIDs) {
                        // classMemberDeviceIDsを利用する場合
                        if(classMemberDeviceIDs != null){
                            //  classMemberDeviceIDsが存在する場合
                            for(String classMemberDeviceID: classMemberDeviceIDs){
                                Log.d("classMemberDeviceIDssss: ", classMemberDeviceID);
                                if(!classMemberDeviceID.equals(deviceID)){
                                    tokens.put(classMemberDeviceID);
                                }
                            }

                            // リクエストボディを作成
                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("tokens", tokens);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            RequestBody requestBody = RequestBody.create(
                                    jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

                            Request request = new Request.Builder()
                                    .url("https://us-central1-classcompass-4d21e.cloudfunctions.net/sendNotificationToAll")
                                    .post(requestBody)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        Log.d(TAG, "Notification sent successfully");
                                    } else {
                                        Log.e(TAG, "Failed to send notification");
                                    }
                                }
                            });
                        }else {
                            //  classMemberDeviceIDsが存在しない場合
                            Log.d(tag, "classMemberDeviceIDs are null");
                        }
                    }
                });
            }
        });
    }





}