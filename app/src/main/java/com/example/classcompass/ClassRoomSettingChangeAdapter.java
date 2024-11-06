package com.example.classcompass;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ClassRoomSettingChangeAdapter extends RecyclerView.Adapter<ClassRoomSettingChangeAdapter.ViewHolder> {
    private List<CRSCAdapterItem> dataList;
    private Context context;
    private final String tag = "CRSCAdapter";
    SharedPreferences sharedPreferences;

    public ClassRoomSettingChangeAdapter(Context context, List<CRSCAdapterItem> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.sharedPreferences = context.getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_room_setting_change_child_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CRSCAdapterItem item = dataList.get(position);
        holder.nameTextView.setText(item.getMemberName());

        // アイコンをFirebaseから取得して設定
        if (item.getMemberIcon() != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // 取得したい画像のパスを指定
            String iconPath = "user_icon/" + item.getMemberIcon();
            // 画像をダウンロードするための参照を取得
            StorageReference imageRef = storageRef.child(iconPath);
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // 画像が正常にダウンロードされた場合の処理（IconViewのサイズに合わせて適宜拡大して表示）
                     Glide.with(context)
                        .load(uri)
                        .override(holder.iconImageView.getWidth(), holder.iconImageView.getHeight())
                        .centerCrop()
                        .into(holder.iconImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // 画像のダウンロード中にエラーが発生した場合の処理
                    holder.iconImageView.setImageResource(R.drawable.default_icon);
                }
            });
        } else {
            holder.iconImageView.setImageResource(R.drawable.default_icon);
        }

        //  classRoomIDを取得
        String classRoomID = sharedPreferences.getString("classRoomID", "None");

        //  memberIDを取得
        String memberID = item.getMemberID();
        String memberClass = memberID.substring(0, 2);

        // ボタンのクリックリスナーを設定
        holder.deleteButton.setOnClickListener(v -> {
            // 削除ボタンの処理
            if(memberClass.equals("CT")){
                ClassRoomsDatabaseManager.deleteTeacherClassMember(classRoomID, memberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                    @Override
                    public void onError(String errorMsg) {
                        //  データの削除に失敗した場合
                        Log.d(tag, errorMsg);
                    }

                    @Override
                    public void onSuccess(String successMsg) {
                        //  データの削除に成功した場合
                        Log.d(tag, successMsg);
                    }
                });
            }else{
                ClassRoomsDatabaseManager.deleteStudentClassMember(classRoomID, memberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                    @Override
                    public void onError(String errorMsg) {
                        //  データの削除に失敗した場合
                        Log.d(tag, errorMsg);
                    }

                    @Override
                    public void onSuccess(String successMsg) {
                        //  データの削除に成功した場合
                        Log.d(tag, successMsg);
                    }
                });
            }
        });

        holder.transferButton.setOnClickListener(v -> {
            // 転校ボタンの処理
            ClassRoomsDatabaseManager.setLastClassRoomChats(classRoomID, memberID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    //  データ登録に失敗した場合
                    Log.d(tag, errorMsg);
                }

                @Override
                public void onSuccess(String successMsg) {
                    //  データ登録に成功した場合
                    Log.d(tag, successMsg);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public Button deleteButton;
        public Button transferButton;
        public IconImageView iconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.class_room_setting_change_child_name);
            deleteButton = itemView.findViewById(R.id.class_room_setting_change_child_delete);
            transferButton = itemView.findViewById(R.id.class_room_setting_change_child_transfer);
            iconImageView = itemView.findViewById(R.id.class_room_setting_change_child_icon);
        }
    }
}
