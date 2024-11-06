package com.example.classcompass;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ONE = 1;
    private static final int VIEW_TYPE_TWO = 2;
    private List<ChatAdapterItem> dataList;
    private Context context;
    private final String tag = "chatAdapter";
    SharedPreferences sharedPreferences;

    public ChatAdapter(Context context, List<ChatAdapterItem> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.sharedPreferences = context.getSharedPreferences("ClassCompass", Context.MODE_PRIVATE);
    }

    @Override
    public int getItemViewType(int position) {
        ChatAdapterItem item = dataList.get(position);
        Log.d("ChatAdapter", "Item at position " + position + ": " + item.getMemberID());
        String classMemberID = sharedPreferences.getString("classMemberID", "None");
        Log.d("ChatAdapter", "classMemberID: " + classMemberID);
        // itemのmemberIDとclassMemberIDを比較して、ViewTypeを決定する
        if (item.getMemberID().equals(classMemberID)) {
            return VIEW_TYPE_ONE;
        } else {
            return VIEW_TYPE_TWO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ONE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_my_item, parent, false);
            return new ViewHolderOne(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_other_item, parent, false);
            return new ViewHolderTwo(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatAdapterItem item = dataList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_ONE) {
            ViewHolderOne viewHolderOne = (ViewHolderOne) holder;
            ((ViewHolderOne) holder).bindData(item);
            adjustViewSize(viewHolderOne.messageTextView, viewHolderOne.itemView.findViewById(R.id.chat_my_view));
        } else {
            ViewHolderTwo viewHolderTwo = (ViewHolderTwo) holder;
            ((ViewHolderTwo) holder).bindData(item, context);
            adjustViewSize(viewHolderTwo.messageTextView, viewHolderTwo.itemView.findViewById(R.id.chat_other_view));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolderOne extends RecyclerView.ViewHolder {
        public TextView messageTextView;

        public ViewHolderOne(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.chat_my_txt);
        }

        public void bindData(ChatAdapterItem item) {
            String formattedMessage = formatTextWithLineBreaks(item.getMessage(), 20);
            messageTextView.setText(formattedMessage);
//            messageTextView.setText(item.getMessage());
        }
    }

    public static class ViewHolderTwo extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public IconImageView iconImageView;
        public TextView nameTextView;

        public ViewHolderTwo(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.chat_other_txt);
            iconImageView = itemView.findViewById(R.id.chat_other_icon);
            nameTextView = itemView.findViewById(R.id.chat_other_name);
        }

        public void bindData(ChatAdapterItem item, Context context) {
            String formattedMessage = formatTextWithLineBreaks(item.getMessage(), 20);
            messageTextView.setText(formattedMessage);
            nameTextView.setText(item.getMemberName());

            // アイコンをFirebaseから取得して設定
            if (item.getMemberIcon() != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                String iconPath = "user_icon/" + item.getMemberIcon();
                StorageReference imageRef = storageRef.child(iconPath);
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context)
                                .load(uri)
                                .override(iconImageView.getWidth(), iconImageView.getHeight())
                                .centerCrop()
                                .into(iconImageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        iconImageView.setImageResource(R.drawable.default_icon);
                    }
                });
            } else {
                iconImageView.setImageResource(R.drawable.default_icon);
            }
        }
    }



    private void adjustViewSize(TextView textView, View view) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                int padding = (int) (10 * context.getResources().getDisplayMetrics().density);
                int maxWidth = (int) (400 * context.getResources().getDisplayMetrics().density);
                int minWidth = (int) (23 * context.getResources().getDisplayMetrics().density);
                int width = textView.getWidth() + 2 * padding;
                int height = textView.getHeight() + 2 * padding;

                if (width < minWidth) {
                    width = minWidth;
                } else if (width > maxWidth) {
                    width = maxWidth;
                }

                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = width;
                params.height = height;
                view.setLayoutParams(params);
            }
        });
    }

    private static String formatTextWithLineBreaks(String text, int interval) {
        StringBuilder formattedText = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i += interval) {
            if (i + interval < length) {
                formattedText.append(text, i, i + interval).append("\n");
            } else {
                formattedText.append(text.substring(i));
            }
        }
        return formattedText.toString();
    }
}
