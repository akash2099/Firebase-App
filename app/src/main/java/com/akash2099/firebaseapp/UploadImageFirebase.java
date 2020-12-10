package com.akash2099.firebaseapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadImageFirebase extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChoose;
    private Button mButtonUpload;
    private EditText mFileUploadName;
    private ImageView mImagePreview;
    private ProgressBar progressBar;

    private Uri mImageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image_firebase);

        mButtonChoose = (Button) findViewById(R.id.choose_image);
        mButtonUpload = (Button) findViewById(R.id.upload_image);
        mFileUploadName = (EditText) findViewById(R.id.image_file_upload_name);
        mImagePreview = (ImageView) findViewById(R.id.previewImage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        storageReference = FirebaseStorage.getInstance().getReference("Images");
        databaseReference = FirebaseDatabase.getInstance().getReference("Images");

        mButtonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.INVISIBLE);
                OpenImageChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                UploadFileToFirebase();
            }
        });

    }

    private void OpenImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*"); // mime type
        intent.setAction(Intent.ACTION_GET_CONTENT); // setting actions system
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // get result
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            // Load image into ImageView
            Picasso.get().load(mImageUri).into(mImagePreview);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadFileToFirebase() {
        if (mImageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            // upload data to firebase storage
            fileReference.putFile(mImageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            // show progress bar even after upload complete
//                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // upload data to realtime database
                            UploadImage uploadImage = new UploadImage(mFileUploadName.getText().toString().trim(), taskSnapshot.getUploadSessionUri().toString());
                            String uploadId = databaseReference.push().getKey(); // this is an unique key
                            databaseReference.child(uploadId).setValue(uploadImage);
                            Toast.makeText(UploadImageFirebase.this, "File uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            long totalByteCount = snapshot.getTotalByteCount();
                            long bytesTransferred = snapshot.getBytesTransferred();
                            float left = ((float) bytesTransferred / (float) totalByteCount) * 100;
                            progressBar.setProgress((int) left);

                            System.out.println("hithis " + left + " " + totalByteCount + " " + bytesTransferred);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadImageFirebase.this, "File failed to upload!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }


    }
}
