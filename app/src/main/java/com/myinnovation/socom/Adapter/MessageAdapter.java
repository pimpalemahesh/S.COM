package com.myinnovation.socom.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.myinnovation.socom.Model.Message;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.DeleteDialogBinding;
import com.myinnovation.socom.databinding.ItemReceiveBinding;
import com.myinnovation.socom.databinding.ItemSentBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    String senderRoom;
    String receiverRoom;

    FirebaseRemoteConfig remoteConfig;
    String currentId = FirebaseAuth.getInstance().getUid();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public MessageAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(currentId.equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        int[] reactions = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            if(pos < 0)
                return false;

            if(holder.getClass() == SentViewHolder.class) {
                SentViewHolder viewHolder = (SentViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);


            }

            message.setFeeling(pos);

            reference
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            reference
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);



            return true; // true is closing popup, false is requesting a new selection
        });


        if(holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder)holder;

            if(message.getMessage().equals("photo")) {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Picasso.get()
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.ic_image)
                        .into(viewHolder.binding.image);
            }

            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener((v, event) -> {

                boolean isFeelingsEnabled = remoteConfig.getBoolean("isFeelingsEnabled");
                if(isFeelingsEnabled)
                    popup.onTouch(v, event);
                return false;
            });

            viewHolder.binding.image.setOnTouchListener((v, event) -> {
                popup.onTouch(v, event);
                return false;
            });

            viewHolder.itemView.setOnLongClickListener(v -> {
                View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setView(binding.getRoot())
                        .create();

                if(remoteConfig.getBoolean("isEveryoneDeletionEnabled")) {
                    binding.everyone.setVisibility(View.VISIBLE);
                } else {
                    binding.everyone.setVisibility(View.GONE);
                }
                binding.everyone.setOnClickListener(v1 -> {
                    message.setMessage("This message is removed.");
                    message.setFeeling(-1);
                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(message.getMessageId()).setValue(message);

                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .child(message.getMessageId()).setValue(message);
                    dialog.dismiss();
                });

                binding.delete.setOnClickListener(v12 -> {
                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(message.getMessageId()).setValue(null);
                    dialog.dismiss();
                });

                binding.cancel.setOnClickListener(v13 -> dialog.dismiss());

                dialog.show();

                return false;
            });
        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            if(message.getMessage().equals("photo")) {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Picasso.get()
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.ic_image)
                        .into(viewHolder.binding.image);
            }
            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener((v, event) -> {
                popup.onTouch(v, event);
                return false;
            });

            viewHolder.binding.image.setOnTouchListener((v, event) -> {
                popup.onTouch(v, event);
                return false;
            });

            viewHolder.itemView.setOnLongClickListener(v -> {
                View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setView(binding.getRoot())
                        .create();

                binding.everyone.setOnClickListener(v14 -> {
                    message.setMessage("This message is removed.");
                    message.setFeeling(-1);
                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(message.getMessageId()).setValue(message);

                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .child(message.getMessageId()).setValue(message);
                    dialog.dismiss();
                });

                binding.delete.setOnClickListener(v15 -> {
                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(message.getMessageId()).setValue(null);
                    dialog.dismiss();
                });

                binding.cancel.setOnClickListener(v16 -> dialog.dismiss());

                dialog.show();

                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SentViewHolder extends RecyclerView.ViewHolder {

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }

}
