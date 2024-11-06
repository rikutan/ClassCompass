package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classcompass.databaseManager.ClassRoomsDatabaseManager;

import java.util.List;

public class LotteryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LotteryItem> dataList;
    private static Context context;
    private final String tag = "lotteryAdapter";
    private static String classRoomID;
    SharedPreferences sharedPreferences;

    public LotteryAdapter(Context context, List<LotteryItem> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.sharedPreferences = context.getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
        classRoomID = sharedPreferences.getString("classRoomID", "None");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lottery_play_item, parent, false);
        return new LotteryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LotteryItem item = dataList.get(position);
        LotteryAdapter.ViewHolder viewHolder = (LotteryAdapter.ViewHolder) holder;
        ((LotteryAdapter.ViewHolder) holder).bindData(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView titleTextView;
        public ImageButton playButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.lottery_date);
            titleTextView = itemView.findViewById(R.id.lottery_title);
            playButton = itemView.findViewById(R.id.lottery_playBtn);
        }

        public void bindData(LotteryItem item) {
            String lotteryID = item.getLotteryID();
            ClassRoomsDatabaseManager.getLottery(classRoomID, lotteryID, new ClassRoomsDatabaseManager.ClassRoomErrorListener() {
                @Override
                public void onError(String errorMsg) {
                    //  データ取得に失敗した場合
                    Log.d("lotteryAdapter", errorMsg);
                }

                @Override
                public void onSuccess(String successMsg) {
                    //  データ取得に成功した場合
                    Log.d("lotteryAdapter", successMsg);
                }
            }, new ClassRoomsDatabaseManager.LotteryListener() {
                @Override
                public void onLottery(String lotteryTitle, String lotteryDate) {
                    //  lotteryTitle, lotteryDateを利用する場合
                    dateTextView.setText(lotteryDate);
                    titleTextView.setText(lotteryTitle);
                }
            });

            playButton.setOnClickListener( v -> {
                Intent intent = new Intent(context, LotteryPlayActivity.class);
                intent.putExtra("lotteryID", item.getLotteryID());
                context.startActivity(intent);
            });
        }
    }

}
