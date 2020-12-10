package com.akash2099.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class PhoneAuthenticationNumberVerify extends AppCompatActivity {

    private static String AUTH_CRED_KEY="AuthCredentials";

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText mOtpText;
    private Button mVerifyBtn;

    private ProgressBar verifyProgressBar;
    private String mAuthVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication_number_verify);

        mOtpText=findViewById(R.id.verification_code_edittext);
        mVerifyBtn=findViewById(R.id.verify_otp_button);
        verifyProgressBar=findViewById(R.id.progress_bar_phone2);

        verifyProgressBar.setVisibility(View.INVISIBLE);


        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();

        mAuthVerificationId=getIntent().getStringExtra(AUTH_CRED_KEY); // GET THE CREDENTIAL ID FROM THE INTENT

        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp=mOtpText.getText().toString();
                if(otp.trim().isEmpty()){
                    Toast.makeText(PhoneAuthenticationNumberVerify.this, "CODE INVALID", Toast.LENGTH_SHORT).show();
                }
                else{
                    verifyProgressBar.setVisibility(View.VISIBLE);

                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(mAuthVerificationId,otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential){
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(PhoneAuthenticationNumberVerify.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            sendUserHome();
                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(PhoneAuthenticationNumberVerify.this, "Error in verification Process", Toast.LENGTH_SHORT).show();
                            }
                        }
                        verifyProgressBar.setVisibility(View.INVISIBLE);


                    }
                });
    }

    private void sendUserHome(){
        Intent send_home_intent=new Intent(PhoneAuthenticationNumberVerify.this,PhoneAuthenticationWelcome.class);
        send_home_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        send_home_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(send_home_intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser!=null){ // if the user is already verified then it will be redirected to home directly
            sendUserHome();
        }

    }
}
