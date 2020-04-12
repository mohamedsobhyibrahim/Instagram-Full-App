package com.instagram.view.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instagram.R;
import com.instagram.network.firebase.Firebase;
import com.instagram.network.models.Post;
import com.instagram.network.models.Story;
import com.instagram.utils.Constants;
import com.instagram.utils.OnCommentClickListner;
import com.instagram.utils.OnItemClickedListner;
import com.instagram.view.details.PostDetailsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements OnCommentClickListner, OnItemClickedListner, View.OnClickListener {

    private CircleImageView addStoryBtn;
    private static final int PICK_IMAGE = 1;

    //Stories
    private RecyclerView storysRecyclerView;
    private StoryAdapter storyAdapter;
    private List<String> stories = new ArrayList<>();

    //Posts
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();

    private DatabaseReference reference;
    private Context context;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity().getApplicationContext();
        initUI(view);
        reference = Firebase.getDatabaseRef();

        addStoryBtn.setOnClickListener(this);
        getStories();
        getPosts();
        return view;
    }

    private void initUI(View view) {
        storysRecyclerView = view.findViewById(R.id.stories_recyclerView);
        postRecyclerView = view.findViewById(R.id.posts_recyclerView);
        addStoryBtn = view.findViewById(R.id.add_story);
    }

    private void getStories() {
        reference.child(context.getString(R.string.stories)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), context.getString(R.string.no_data_to_show), Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
                    Story story = storySnapshot.getValue(Story.class);
                    stories.add(story.getImageUrl());
                }
                storyAdapter = new StoryAdapter(stories);
                storysRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                storysRecyclerView.setAdapter(storyAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPosts() {
        reference.child(context.getString(R.string.post)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), context.getString(R.string.no_data_to_show), Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setId(postSnapshot.getKey());
                    posts.add(post);
                }
                Collections.reverse(posts);
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        postAdapter = new PostAdapter(posts,this, this);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        postRecyclerView.setAdapter(postAdapter);
    }

    @Override
    public void onCommentClick(String postId) {
        Intent intent = new Intent(getContext(), PostDetailsActivity.class);
        intent.putExtra(Constants.POST_ID, postId);
        startActivity(intent);
    }

    @Override
    public void onItemClick(String postId) {
        Intent intent = new Intent(getContext(), PostDetailsActivity.class);
        intent.putExtra(Constants.POST_ID, postId);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_story:
                openGallery();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            addStory(imageUri);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void addStory(Uri imageUri) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait ....");
        progressDialog.show();

        final String imageName = UUID.randomUUID().toString();
        storageReference = Firebase.getStorageRef();
        storageReference.child("Stories").child("Images").child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child("Stories").child("Images").child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        String downUrl = uri.toString();
                        Story story = new Story(downUrl);
                        reference.child("stories").push().setValue(story);
                        stories = new ArrayList<>();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }
}
