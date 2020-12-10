package com.akash2099.firebaseapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReadRecyclerView extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseRecyclerAdapter<MyMember, RecyclerViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_recycler_view);

        recyclerView = findViewById(R.id.myrecycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users");
    }

    public void configureRecyclerView() {
        FirebaseRecyclerOptions<MyMember> options = new FirebaseRecyclerOptions.Builder<MyMember>()
                .setQuery(reference, MyMember.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MyMember, RecyclerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position, @NonNull MyMember model) {
                holder.setView(getApplicationContext(), model.name, model.age, model.address, model.Image, model.Video);
                // model.id contains the unique key for that user

                holder.setOnClickListener(new RecyclerViewHolder.Clicklistener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                        String name_id = getItem(position).getName();

                        showDeleteDataDialog(model);
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_row, parent, false);
                return new RecyclerViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void showDeleteDataDialog(MyMember myMember) {
        String id = myMember.getId();
        String name = myMember.getName();
        int age = myMember.getAge();
        String address = myMember.getAddress();
        String Image = myMember.getImage();
        String Video = myMember.getVideo();

        AlertDialog.Builder builder = new AlertDialog.Builder(ReadRecyclerView.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Deleting from realtime database
                Query query = reference.orderByChild("id").equalTo(myMember.getId());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(ReadRecyclerView.this, "Data deleted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReadRecyclerView.this, "Data failed to delete!", Toast.LENGTH_SHORT).show();
                    }
                });

                // Deleting from storage Video and Image
                deleteFromFirebaseStore(Image); // Image
                deleteFromFirebaseStore(Video); // Video
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteFromFirebaseStore(String file_storage_path) {
        String url = file_storage_path;

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        reference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ReadRecyclerView.this, "File has been deleted from Storage!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReadRecyclerView.this, "Failed to delete File from Storage!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        configureRecyclerView();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
