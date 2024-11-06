package com.example.classcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class LotteryInputAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<LotteryInputItem> dataList;
    private static Context context;
    private final String tag = "LotteryInputAdapter";

    public LotteryInputAdapter(Context context, List<LotteryInputItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lottery_input_item, parent, false);
        return new LotteryInputAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LotteryInputItem item = dataList.get(position);
        LotteryInputAdapter.ViewHolder viewHolder = (LotteryInputAdapter.ViewHolder) holder;
        ((LotteryInputAdapter.ViewHolder) holder).bindData(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public List<LotteryInputItem> getDataList() {
        return dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lotteryItemNum;
        public EditText lotteryItemName;
        public EditText lotteryItemWinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lotteryItemName = itemView.findViewById(R.id.lottery_input_itemName);
            lotteryItemWinner = itemView.findViewById(R.id.lottery_input_winner);
            lotteryItemNum = itemView.findViewById(R.id.lottery_input_num);
        }

        public void bindData(LotteryInputItem item) {
            lotteryItemNum.setText(item.getLotteryItem());

            lotteryItemName.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    item.setItemName(s.toString());
                }
            });

            lotteryItemWinner.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    item.setItemWinner(s.toString());
                }
            });
        }
    }

}
