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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthenticationFirst extends AppCompatActivity {

    private static String AUTH_CRED_KEY="AuthCredentials";

    private EditText mCountryCode;
    private EditText mPhoneNumber;

    private Button mGenerateOtpButton;
    private ProgressBar loginProgress;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication_first);

        mCountryCode=(EditText)findViewById(R.id.country_code);
        mPhoneNumber=(EditText)findViewById(R.id.phone_no);
        mGenerateOtpButton=(Button)findViewById(R.id.submit_phone_no);
        loginProgress=(ProgressBar)findViewById(R.id.progress_bar_phone);

        loginProgress.setVisibility(View.INVISIBLE);

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();

        mGenerateOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country_code=mCountryCode.getText().toString();
                String phone_no=mPhoneNumber.getText().toString();

                String complete_phone_number="+"+country_code+phone_no;

                System.out.println("hiii1 "+complete_phone_number);
                if(country_code.trim().isEmpty() || phone_no.trim().isEmpty()){
                    Toast.makeText(PhoneAuthenticationFirst.this, "Please enter a valid number!", Toast.LENGTH_SHORT).show();
                }
                else{
                    loginProgress.setVisibility(View.VISIBLE);

                    // Need to find its updated version
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            complete_phone_number,
                            60,
                            TimeUnit.SECONDS,
                            PhoneAuthenticationFirst.this,
                            mCallbacks
                    );

                    // Updated
//                    PhoneAuthOptions options =
//                            PhoneAuthOptions.newBuilder(mAuth)
//                                    .setPhoneNumber(complete_phone_number)       // Phone number to verify
//                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                                    .setActivity(PhoneAuthenticationFirst.this)                 // Activity (for callback binding)
//                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
//                                    .build();
//                    PhoneAuthProvider.verifyPhoneNumber(options);
                }

            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneAuthenticationFirst.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                // If code received then send the code to the new activity using intent
                // This process can be optimized by using view model+live data and exchange data between fragments
                Intent intent=new Intent(PhoneAuthenticationFirst.this,PhoneAuthenticationNumberVerify.class);
                intent.putExtra(AUTH_CRED_KEY,s);
                startActivity(intent);
            }

            // Implement timeout
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(PhoneAuthenticationFirst.this, "Verification Failed, Time out please re-generate code!", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void signInWithAuthCredential(PhoneAuthCredential phoneAuthCredential){
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(PhoneAuthenticationFirst.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            sendUserHome();
                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(PhoneAuthenticationFirst.this, "Error in verification Process", Toast.LENGTH_SHORT).show();
                            }
                        }
                        loginProgress.setVisibility(View.INVISIBLE);


                    }
                });
    }

    private void sendUserHome(){
        Intent send_home_intent=new Intent(PhoneAuthenticationFirst.this,PhoneAuthenticationWelcome.class);
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
