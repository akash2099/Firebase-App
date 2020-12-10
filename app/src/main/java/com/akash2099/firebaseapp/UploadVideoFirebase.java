package com.akash2099.firebaseapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

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

public class UploadVideoFirebase extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;

    private Button mButtonChoose;
    private Button mButtonUpload;
    private EditText mFileUploadName;
    private VideoView mVideoPreview;
    private ProgressBar progressBar;

    private Uri mVideoUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video_firebase);

        mButtonChoose = (Button) findViewById(R.id.choose_video);
        mButtonUpload = (Button) findViewById(R.id.upload_video);
        mFileUploadName = (EditText) findViewById(R.id.video_file_upload_name);
        mVideoPreview = (VideoView) findViewById(R.id.previewVideo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        storageReference = FirebaseStorage.getInstance().getReference("Videos");
        databaseReference = FirebaseDatabase.getInstance().getReference("Videos");

        mButtonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.INVISIBLE);
                OpenVideoChooser();
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

    private void OpenVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*"); // mime type
        intent.setAction(Intent.ACTION_GET_CONTENT); // setting actions system
        startActivityForResult(intent, PICK_VIDEO_REQUEST); // get result
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mVideoUri = data.getData();

            // Load video into VideoView
            // create an object of media controller
            MediaController mediaController=new MediaController(this);
            // set media controller object for video view
            mVideoPreview.setMediaController(mediaController);
            mVideoPreview.setVideoURI(mVideoUri);
//            mVideoPreview.canPause();
//            mVideoPreview.canSeekBackward();
//            mVideoPreview.canSeekForward();
            mVideoPreview.start();


        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadFileToFirebase() {
        if (mVideoUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(mVideoUri));
            // upload data to firebase storage
            fileReference.putFile(mVideoUri)
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
                            UploadVideo uploadVideo = new UploadVideo(mFileUploadName.getText().toString().trim(), taskSnapshot.getUploadSessionUri().toString());
                            String uploadId = databaseReference.push().getKey(); // this is an unique key
                            databaseReference.child(uploadId).setValue(uploadVideo);
                            Toast.makeText(UploadVideoFirebase.this, "File uploaded successfully!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(UploadVideoFirebase.this, "File failed to upload!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }


    }
}
