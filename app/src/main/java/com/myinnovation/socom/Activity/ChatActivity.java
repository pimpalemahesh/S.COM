package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myinnovation.socom.Adapter.MessageAdapter;
import com.myinnovation.socom.Model.Message;
import com.myinnovation.socom.Model.Request;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.ActivityChatBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;

    MessageAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom, receiverRoom;

    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog dialog;
    String senderUid, receiverUid, name, profile, token;
    String messageTxt, randromKey;
    SpeechRecognizer speechRecognizer;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        messages = new ArrayList<>();

        name = getIntent().getStringExtra("name");
        profile = getIntent().getStringExtra("image");
        token = getIntent().getStringExtra("token");

        //Toast.makeText(this, token, Toast.LENGTH_SHORT).show();

        binding.name.setText(name);
        Picasso.get()
                .load(profile)
                .placeholder(R.drawable.avatar)
                .into(binding.profile);

        binding.imageView2.setOnClickListener(v -> finish());

        receiverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();

        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (status != null && !status.isEmpty()) {
                        if (status.equals("Offline")) {
                            binding.status.setVisibility(View.GONE);
                        } else {
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        adapter = new MessageAdapter(this, messages, senderRoom, receiverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        binding.micbtn.setOnClickListener(v -> {

                // count == 0 mic is off / count == 1 mic is on.
                if(count == 0)
                {
                    // change mic image from off to on.
                    binding.micbtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_mic_24));

                    // startListening
                    speechRecognizer.startListening(speechRecognizerIntent);
                    count = 1;
                }
                else{
                    // change mic image from on to off.
                    binding.micbtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_mic_off_24));

                    // stopListening
                    speechRecognizer.stopListening();
                    count = 0;
                }
            });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                binding.messageBox.setText(data.get(0));

                String messageTxt = data.get(0);
                Date date = new Date();
                Message message = new Message(messageTxt, senderUid, date.getTime());
                database.getReference().child("chats").child(senderRoom)
                        .child("Status")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String status = snapshot.getValue(String.class);
                                    if(status != null && status.equals("Accept")){
                                        randromKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                                        binding.messageBox.setText("");

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .child(randromKey)
                                                .setValue(message).addOnSuccessListener(aVoid -> database.getReference().child("chats")
                                                .child(receiverRoom)
                                                .child("messages")
                                                .child(randromKey)
                                                .setValue(message).addOnSuccessListener(aVoid1 -> {
                                                    if(message.getMessage() != null && message.getMessage().equals("")){
                                                        sendNotification(name, message.getMessage(), token);
                                                    }
                                                }));
                                    }
                                    else{
                                        randromKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                                        binding.messageBox.setText("");

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .child(randromKey)
                                                .setValue(message).addOnSuccessListener(aVoid -> database.getReference().child("chats")
                                                .child(receiverRoom)
                                                .child("messages")
                                                .child(randromKey)
                                                .setValue(message).addOnSuccessListener(aVoid1 -> {
                                                    if(message.getMessage() != null && message.getMessage().equals("")){
                                                        sendNotification(name, message.getMessage(), token);
                                                    }

                                                    Request request = new Request();
                                                    request.setRequestId(senderUid);
                                                    request.setRequestAt(new Date().getTime());
                                                    request.setRequestTo(receiverUid);

                                                    database.getReference().child("Requests")
                                                            .child(senderRoom)
                                                            .setValue(request).addOnSuccessListener(unused -> {
                                                        binding.sendBtn.setVisibility(View.GONE);
                                                        binding.micbtn.setVisibility(View.VISIBLE);
                                                        Toast.makeText(getApplicationContext(), "Request sent successfully",Toast.LENGTH_LONG).show();
                                                    });
                                                }));
                                    }
                                }
                                else{
                                    randromKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                                    Date date = new Date();
                                    binding.messageBox.setText("");

                                    HashMap<String, Object> lastMsgObj = new HashMap<>();
                                    lastMsgObj.put("lastMsg", message.getMessage());
                                    lastMsgObj.put("lastMsgTime", date.getTime());

                                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                    database.getReference().child("chats")
                                            .child(senderRoom)
                                            .child("messages")
                                            .child(randromKey)
                                            .setValue(message).addOnSuccessListener(aVoid -> database.getReference().child("chats")
                                            .child(receiverRoom)
                                            .child("messages")
                                            .child(randromKey)
                                            .setValue(message).addOnSuccessListener(aVoid1 -> {
                                                if(message.getMessage() != null && message.getMessage().equals("")){
                                                    sendNotification(name, message.getMessage(), token);
                                                }

                                                Request request = new Request();
                                                request.setRequestId(senderUid);
                                                request.setRequestAt(new Date().getTime());
                                                request.setRequestTo(receiverUid);

                                                database.getReference().child("Requests")
                                                        .child(senderRoom)
                                                        .setValue(request).addOnSuccessListener(unused -> {
                                                    binding.sendBtn.setVisibility(View.GONE);
                                                    binding.micbtn.setVisibility(View.VISIBLE);
                                                    Toast.makeText(getApplicationContext(), "Request sent successfully",Toast.LENGTH_LONG).show();
                                                });
                                            }));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        binding.sendBtn.setOnClickListener(v -> {
            messageTxt = binding.messageBox.getText().toString();
            Date date = new Date();
            Message message = new Message(messageTxt, senderUid, date.getTime());

            if(binding.messageBox.getText().toString().equals("") || binding.messageBox.getText().length() < 0){
                Toast.makeText(getApplicationContext(), "Your message is Empty.", Toast.LENGTH_LONG).show();
            } else{
                randromKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                database.getReference().child("chats").child(senderRoom)
                        .child("Status")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String status = snapshot.getValue(String.class);
                                    if(status != null && status.equals("Accept")){

                                        binding.messageBox.setText("");

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .child(randromKey)
                                                .setValue(message).addOnSuccessListener(aVoid -> database.getReference().child("chats")
                                                .child(receiverRoom)
                                                .child("messages")
                                                .child(randromKey)
                                                .setValue(message).addOnSuccessListener(aVoid1 -> {
                                                    if(message.getMessage() != null && message.getMessage().equals("")){
                                                        sendNotification(name, message.getMessage(), token);
                                                    }
                                                }));
                                    }
                                    else{
                                        binding.messageBox.setText("");

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .child(randromKey)
                                                .setValue(message).addOnSuccessListener(aVoid -> database.getReference().child("chats")
                                                .child(receiverRoom)
                                                .child("messages")
                                                .child(randromKey)
                                                .setValue(message).addOnSuccessListener(aVoid1 -> {
                                                    if(message.getMessage() != null && message.getMessage().equals("")){
                                                        sendNotification(name, message.getMessage(), token);
                                                    }

                                                    Request request = new Request();
                                                    request.setRequestId(senderUid);
                                                    request.setRequestAt(new Date().getTime());
                                                    request.setRequestTo(receiverUid);

                                                    database.getReference().child("Requests")
                                                            .child(senderRoom)
                                                            .setValue(request).addOnSuccessListener(unused -> {
                                                        binding.sendBtn.setVisibility(View.GONE);
                                                        binding.micbtn.setVisibility(View.VISIBLE);
                                                        Toast.makeText(getApplicationContext(), "Request sent successfully",Toast.LENGTH_LONG).show();
                                                    });
                                                }));
                                    }
                                }
                                else{

                                    Date date = new Date();
                                    binding.messageBox.setText("");

                                    HashMap<String, Object> lastMsgObj = new HashMap<>();
                                    lastMsgObj.put("lastMsg", message.getMessage());
                                    lastMsgObj.put("lastMsgTime", date.getTime());

                                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                    database.getReference().child("chats")
                                            .child(senderRoom)
                                            .child("messages")
                                            .child(randromKey)
                                            .setValue(message).addOnSuccessListener(aVoid -> database.getReference().child("chats")
                                            .child(receiverRoom)
                                            .child("messages")
                                            .child(randromKey)
                                            .setValue(message).addOnSuccessListener(aVoid1 -> {
                                                if(message.getMessage() != null && message.getMessage().equals("")){
                                                    sendNotification(name, message.getMessage(), token);
                                                }

                                                Request request = new Request();
                                                request.setRequestId(senderUid);
                                                request.setRequestAt(new Date().getTime());
                                                request.setRequestTo(receiverUid);

                                                database.getReference().child("Requests")
                                                        .child(senderRoom)
                                                        .setValue(request).addOnSuccessListener(unused -> {
                                                    binding.sendBtn.setVisibility(View.GONE);
                                                    binding.micbtn.setVisibility(View.VISIBLE);
                                                    Toast.makeText(getApplicationContext(), "Request sent successfully",Toast.LENGTH_LONG).show();
                                                });
                                            }));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

        });

        binding.attachment.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 25);
        });

        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.micbtn.setVisibility(View.VISIBLE);
                binding.micbtn.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_mic_off_24));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.micbtn.setVisibility(View.GONE);
                binding.sendBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderUid).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping, 1000);
                binding.sendBtn.setVisibility(View.VISIBLE);
                binding.micbtn.setVisibility(View.GONE);
            }

            final Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("Online");
                    binding.micbtn.setVisibility(View.GONE);
                    binding.sendBtn.setVisibility(View.VISIBLE);
                }
            };
        });


        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

    }

    void sendNotification(String name, String message, String token) {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", name);
            data.put("body", message);
            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData
                    , response -> {
                    }, error -> Toast.makeText(ChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> map = new HashMap<>();
                    String key = "Key=AAAAfGnalso:APA91bFM2gch_6tRGoxx-5cY-kz9pjIrEqCp90wAYUXuIJfdByGYtJNtDyCL85YLMH-zcDjOqXM79UYYtnGqpzNA5F50E1_lwoR0Ib308nFNBnxQhFQA96H34_bd7LLjStOUfhHwCjD8";
                    map.put("Content-Type", "application/json");
                    map.put("Authorization", key);

                    return map;
                }
            };

            queue.add(request);


        } catch (Exception ex) {
            Toast.makeText(this, "Notification Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    randromKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(task -> {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String filePath = uri.toString();

                                String messageTxt = binding.messageBox.getText().toString();

                                Date date = new Date();
                                Message message = new Message(messageTxt, senderUid, date.getTime());
                                message.setMessage("photo");
                                message.setImageUrl(filePath);
                                binding.messageBox.setText("");

                                HashMap<String, Object> lastMsgObj = new HashMap<>();
                                lastMsgObj.put("lastMsg", message.getMessage());
                                lastMsgObj.put("lastMsgTime", date.getTime());

                                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                database.getReference().child("chats")
                                        .child(senderRoom)
                                        .child("messages")
                                        .child(randromKey)
                                        .setValue(message).addOnSuccessListener(aVoid -> database.getReference().child("chats")
                                                .child(receiverRoom)
                                                .child("messages")
                                                .child(randromKey)
                                                .setValue(message).addOnSuccessListener(aVoid1 -> {

                                                }));
                            });
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom)
                .child("Status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.getValue(String.class);
                            if(status != null && status.equals("Accept")){
                                database.getReference().child("chats")
                                        .child(senderRoom)
                                        .child("messages")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                messages.clear();
                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                    Message message = snapshot1.getValue(Message.class);
                                                    if(message != null){
                                                        message.setMessageId(snapshot1.getKey());
                                                        messages.add(message);
                                                    }
                                                }

                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        } else{
                            Toast.makeText(getApplicationContext(), "User is not in your Friend List.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        String currentId = FirebaseAuth.getInstance().getUid();
        if (currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("Online");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        if (currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("Online");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        if (currentId != null) {
            database.getReference().child("presence").child(currentId).setValue("Offline");
        }
    }
}