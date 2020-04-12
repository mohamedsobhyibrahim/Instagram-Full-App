package com.instagram.view.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.instagram.R;
import com.instagram.network.models.Post;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostHolder extends RecyclerView.ViewHolder {

    private CircleImageView userImage;
    private ImageView postImage;
    private TextView userNameTV;
    private TextView postTimeTV;
    private TextView likeCountTV;
    public ImageView likeBtn;
    public ImageView commentBtn;

    public PostHolder(@NonNull View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.item_post_user_image);
        postImage = itemView.findViewById(R.id.item_post_image);
        userNameTV = itemView.findViewById(R.id.item_post_user_name);
        postTimeTV = itemView.findViewById(R.id.item_post_time);
        likeCountTV = itemView.findViewById(R.id.item_post_like_count);
        likeBtn = itemView.findViewById(R.id.item_post_like);
        commentBtn = itemView.findViewById(R.id.item_post_comment);
    }

    public void bind(final Post post) {
        Picasso.get()
                .load(post.getUserImageUrl())
                .placeholder(R.drawable.user)
                .into(userImage);

        Picasso.get()
                .load(post.getPostImageUrl())
                .placeholder(R.drawable.post_placeholder)
                .into(postImage);

        userNameTV.setText(post.getUserName());
        postTimeTV.setText(post.getPostTime());
        likeCountTV.setText("Liked by " + post.getLikeCount() + " user");

    }
}
