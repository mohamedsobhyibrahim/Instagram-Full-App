package com.instagram.view.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.instagram.R;
import com.instagram.network.models.Post;
import com.instagram.utils.OnCommentClickListner;
import com.instagram.utils.OnItemClickedListner;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostHolder> {

    private List<Post> posts;
    private OnCommentClickListner onCommentClickListner;
    private OnItemClickedListner onItemClickedListner;

    public PostAdapter(List<Post> posts, OnCommentClickListner onCommentClickListner, OnItemClickedListner onItemClickedListner) {
        this.posts = posts;
        this.onCommentClickListner = onCommentClickListner;
        this.onItemClickedListner = onItemClickedListner;

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, final int position) {
        holder.bind(posts.get(position));

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCommentClickListner.onCommentClick(posts.get(position).getId());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickedListner.onItemClick(posts.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
