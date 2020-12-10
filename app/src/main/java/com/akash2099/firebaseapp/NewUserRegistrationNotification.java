package com.akash2099.firebaseapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class NewUserRegistrationNotification extends AppCompatActivity {

    Button register_button;
    EditText username_et;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
//    private static int maxid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_registration_notification);

        register_button = (Button) findViewById(R.id.register_button);
        username_et = (EditText) findViewById(R.id.new_username);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Registered User");

        // setting value event listener, not required
//        addValueEventListenerDatabase();

        // setting child event listener
        setChildEventListenerForUser();

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username_et.getText().toString();

                if (name.trim().isEmpty()) {
                    Toast.makeText(NewUserRegistrationNotification.this, "Please give a valid Username", Toast.LENGTH_SHORT).show();
                } else {
                    // Creating a new user
                    RegisteredUser registeredUser = new RegisteredUser();
                    registeredUser.setName(username_et.getText().toString());



                    // Write database
                    String unique_key = databaseReference.push().getKey(); // this is an unique key
                    databaseReference.child(unique_key).setValue(registeredUser);
//                    databaseReference.child(String.valueOf(maxid + 1)).setValue(registeredUser);
                }
            }
        });
    }

    /*
    public void addValueEventListenerDatabase() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    maxid = (int) snapshot.getChildrenCount();
                } else {
                    //
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NewUserRegistrationNotification.this, "Issue in connecting to database", Toast.LENGTH_SHORT).show();
            }
        });

    }
    */


    public void setChildEventListenerForUser() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String name = username_et.getText().toString();

                if (name.trim().isEmpty()) {
//                    Toast.makeText(NewUserRegistrationNotification.this, "Please give a valid Username", Toast.LENGTH_SHORT).show();
                } else {
                    notificationOn(name); // turn on the notification for registered user
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Setting up the basic notification for android Oreo and up
    private void notificationOn(String name) {
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID="example.firebase.services.test";

        String messege=" Thank you for registration!";

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("ITech Prophecy");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(androidx.media.R.drawable.notification_icon_background)
                .setContentTitle("Welcome")
                .setContentText(name+messege)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
    }

}
