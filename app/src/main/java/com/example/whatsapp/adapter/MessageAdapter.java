package com.example.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.helper.UserFirebase;
import com.example.whatsapp.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Message> mMessages;
    private Context context;
    private static final int TYPE_SENDER = 0;
    private static final int TYPE_RECEIVER = 1;

    public MessageAdapter(List<Message> list, Context c) {
        this.mMessages = list;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;

        if (viewType == TYPE_SENDER) {
             item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_sender, parent, false);

        } else if (viewType == TYPE_RECEIVER) {
             item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_receiver, parent, false);
        }

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = mMessages.get(position);
        String msg = message.getMessage();
        String image = message.getImage();

        if (image != null) {
            Uri uri = Uri.parse(image);
            Glide.with(context).load(uri).into(holder.images);
            holder.messages.setVisibility(View.GONE);
        } else {
            holder.messages.setText(msg);
            holder.images.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {

        Message message = mMessages.get(position);

        String idUser = UserFirebase.getIdentifyUser();

        if (idUser.equals(message.getIdUser())) {
            return TYPE_SENDER;
        }

        return TYPE_RECEIVER;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView messages;
        ImageView images;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messages = itemView.findViewById(R.id.textMessageText);
            images = itemView.findViewById(R.id.imageMessagePhoto);
        }
    }
}
