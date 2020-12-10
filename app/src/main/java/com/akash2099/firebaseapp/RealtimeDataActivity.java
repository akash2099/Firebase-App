package com.akash2099.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDataActivity extends AppCompatActivity {

    private TextView mValueField;
    private Button showButton;

    private EditText name,age,address;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private static int maxid=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_data);

        mValueField=(TextView) findViewById(R.id.editText);
        showButton=(Button)findViewById(R.id.button);

        name=(EditText) findViewById(R.id.name_field);
        age=(EditText)findViewById(R.id.age_field);
        address=(EditText)findViewById(R.id.address_field);

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference().child("Users");

        // Connecting with the instance of Realtime Database
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        basicRead();
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Write database
                MyMember myMember=new MyMember();
                myMember.setName(name.getText().toString());
                myMember.setAddress(address.getText().toString());
                myMember.setAge(Integer.parseInt(age.getText().toString()));
                writeDatabase(myMember);

                // Documentation
//                basicRead();
//                basicWrite();
            }
        });

   }

    public void writeDatabase(MyMember myMember){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxid=(int) snapshot.getChildrenCount();
                }
                else{
                    //
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RealtimeDataActivity.this, "Issue in connecting to database", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child(String.valueOf(maxid+1)).setValue(myMember);
    }

   /*
    // BasicReadWrite()
    // https://github.com/firebase/snippets-android/blob/686d8e61edab387ae35c3b6cb2d666b936d54f79/database/app/src/main/java/com/google/firebase/referencecode/database/MainActivity.java#L40-L44

    public void basicRead(){
        // [START read_message]
        // Read from the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        // Read
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String result=" ";

//                String value=snapshot.getValue(String.class);
//                String key=snapshot.getKey();
//                result="hiii "+key+" "+value;


                PostMesseges dataMap=snapshot.getValue(PostMesseges.class);
                System.out.println(dataMap);
//                if(dataMap!=null) {
//                    String name = dataMap.get("Name");
//                    String age = dataMap.get("Age");
////                    String s1=name.getClass().toString();
////                    String s2=age.getClass().toString();
//                    result = "hiii " + name + " " + age;
//                }

                System.out.println(result);
                mValueField.setText(result);

                Toast.makeText(RealtimeDataActivity.this, "Success", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                System.out.println("hiii "+"Failed to read");
                Toast.makeText(RealtimeDataActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
        // [END read_message]
    }
    public void basicWrite() {
        // [START write_message]
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        // Write
        myRef.setValue("Hello, World!");
        Toast.makeText(RealtimeDataActivity.this, "Success Upload", Toast.LENGTH_SHORT).show();

        // [END write_message]
    }
*/

}
