package com.instagram.view.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.instagram.R;
import com.instagram.network.firebase.Firebase;
import com.instagram.network.models.Comment;
import com.instagram.network.models.Post;
import com.instagram.utils.Constants;
import com.instagram.view.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView userImage;
    private ImageView postImage;
    private TextView userNameTV;
    private TextView postTimeTV;
    private TextView likeCountTV;
    private ImageView likeBtn;
    private EditText commentET;
    private ImageView addCommentBtn;
    private ImageView removeBtn;

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();


    private String postId;

    private DatabaseReference reference;

    private Post post;

    private static boolean isLiked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        reference = Firebase.getDatabaseRef();

        initUI();
        Intent intent = getIntent();
        postId = intent.getStringExtra(Constants.POST_ID);

        getPost();
        getPostComments();
        likeBtn.setOnClickListener(this);
        addCommentBtn.setOnClickListener(this);
        removeBtn.setOnClickListener(this);
    }

    private void getPost() {
        reference.child("posts").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                Picasso.get()
                        .load(post.getUserImageUrl())
                        .placeholder(R.drawable.placerholder)
                        .into(userImage);

                Picasso.get()
                        .load(post.getPostImageUrl())
                        .placeholder(R.drawable.image_outline)
                        .into(postImage);

                userNameTV.setText(post.getUserName());
                postTimeTV.setText(post.getPostTime());
                likeCountTV.setText("Liked by " + post.getLikeCount() + " user");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostComments() {
        reference.child("posts").child(postId).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_data_to_show), Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = postSnapshot.getValue(Comment.class);
                    comments.add(comment);
                }
                Collections.reverse(comments);
                commentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                commentAdapter = new CommentAdapter(comments);
                commentRecyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initUI() {
        userImage = findViewById(R.id.post_details_user_image);
        postImage = findViewById(R.id.post_details_post_image);
        userNameTV = findViewById(R.id.post_details_user_name);
        postTimeTV = findViewById(R.id.post_details_post_time);
        likeCountTV = findViewById(R.id.post_details_like_count);
        likeBtn = findViewById(R.id.post_details__like);
        commentRecyclerView = findViewById(R.id.comment_recyclerview);
        addCommentBtn = findViewById(R.id.add_comment_button);
        commentET = findViewById(R.id.post_details_add_comment);
        removeBtn = findViewById(R.id.post_details_remove);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.post_details__like:
                int currentPostLikes = post.getLikeCount();
                if (isLiked) {
                    currentPostLikes--;
                    isLiked = false;
                    likeBtn.setImageResource(R.drawable.like);

                } else {
                    isLiked = true;
                    currentPostLikes++;
                    likeBtn.setImageResource(R.drawable.liked);
                }
                post.setLikeCount(currentPostLikes);
                likeCountTV.setText("Liked by " + currentPostLikes + " user");
                reference.child("posts").child(postId).child("likeCount").setValue(currentPostLikes);
                break;

            case R.id.add_comment_button:
                addComment();
                break;

            case R.id.post_details_remove:
                reference.child("posts").child(postId).removeValue();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }

    private void addComment() {
        String commentText = commentET.getText().toString();

        if (!commentText.isEmpty()) {
            Comment comment = new Comment(post.getUserImageUrl(), post.getUserName(), commentText);
            reference.child("posts").child(postId).child("comments").push().setValue(comment);
            commentET.setText("");
            comments = new ArrayList<>();
        } else {
            Toast.makeText(getApplicationContext(), "Comment Required", Toast.LENGTH_SHORT).show();
        }
    }
}
