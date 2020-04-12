package com.instagram.view.details;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.instagram.R;
import com.instagram.network.models.Comment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentHolder extends RecyclerView.ViewHolder {

    private CircleImageView userImageView;
    private TextView userNameTv;
    private TextView commentTv;
    public CommentHolder(@NonNull View itemView) {
        super(itemView);
        userImageView = itemView.findViewById(R.id.item_comment_user_image);
        userNameTv = itemView.findViewById(R.id.item_comment_user_name);
        commentTv = itemView.findViewById(R.id.item_comment_comment);
    }

    public void bind(Comment comment){
        Picasso.get()
                .load(comment.getUserImage())
                .placeholder(R.drawable.placerholder)
                .into(userImageView);

        userNameTv.setText(comment.getUserName());
        commentTv.setText(comment.getCommment());

    }
}
