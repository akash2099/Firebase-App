package com.akash2099.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PhoneAuthenticationWelcome extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    Button mSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication_welcome);

        mSignOut=findViewById(R.id.log_out);

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                sendUserLogIn();
            }
        });
    }

    private void sendUserLogIn(){
        Intent send_back_login=new Intent(PhoneAuthenticationWelcome.this,PhoneAuthenticationFirst.class);
        send_back_login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        send_back_login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(send_back_login);
        finish();
    }
}
