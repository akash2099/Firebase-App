package com.akash2099.firebaseapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    View view;
    SimpleExoPlayer exoPlayer;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClicklistener.onItemLongClick(view, getAdapterPosition());// replace with getAdapterAbsolutePosition()
                return false;
            }
        });
    }

    private RecyclerViewHolder.Clicklistener mClicklistener;

    public interface Clicklistener {
        // creating interface for clicklistener
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(RecyclerViewHolder.Clicklistener clickListener) {
        this.mClicklistener = clickListener;
    }

    public void setView(Context context, String name, int age, String address, String image_url, String video_url) {
        TextView name_tv = view.findViewById(R.id.name_id);
        TextView age_tv = view.findViewById(R.id.age_id);
        TextView address_tv = view.findViewById(R.id.address_id);
        ImageView imageView = view.findViewById(R.id.imageViewDownloaded);
        PlayerView playerView = view.findViewById(R.id.exoplayer_id);

        name_tv.setText(name);
        age_tv.setText(String.valueOf(age));
        address_tv.setText(address);

        // download and set the image into imageView
        Picasso.get().load(image_url).into(imageView);

        // download and set the video into videoView using ExoPlayer
//        System.out.println("ViewHolderException_1");
        try {
            SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();

            // Bind the player to the view.
            playerView.setPlayer(player);

            // Build the media item.
            MediaItem mediaItem = MediaItem.fromUri(video_url);
            // Set the media item to be played.
            player.setMediaItem(mediaItem);
            // Prepare the player.
            player.prepare();
            player.setPlayWhenReady(false);

            // Start the playback.
            //  player.play();


        } catch (Exception e) {
            System.out.println("ViewHolderException " + " Error in recycler view holder : " + e.toString());
            Log.d("ViewHolderException", "Error in recycler view holder : " + e.toString());
        }

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        view.startAnimation(animation); // itemView

    }


}
