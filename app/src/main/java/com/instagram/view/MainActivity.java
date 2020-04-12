package com.instagram.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instagram.R;
import com.instagram.network.firebase.Firebase;
import com.instagram.network.models.Comment;
import com.instagram.network.models.Post;
import com.instagram.view.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final int PICK_IMAGE = 1;
    private Uri imageUri;

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    //Firebase Storage
    FirebaseStorage storage;
    StorageReference storageReference;

    //Database Reference
    private DatabaseReference reference;

    private ProgressDialog progressDialog;

    private String userName;
    private String userImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reference = Firebase.getDatabaseRef();
        storageReference = Firebase.getStorageRef();
        mAuth = Firebase.getAuthenticationRef();
        user = mAuth.getCurrentUser();

        initUI();
        openFragment(new HomeFragment());
    }

    private void initUI() {
        BottomNavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                openFragment(new HomeFragment());
                return true;
            case R.id.nav_add:
                openGallery();
                return true;
            case R.id.nav_profile:
                openFragment(new ProfileFragment());
                return true;
        }
        return false;
    }

    private void openGallery() {
        if (user != null) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } else {
            Toast.makeText(getApplicationContext(), "Please Sign In", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frameLayout, fragment);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting ....");
        progressDialog.show();

        final String imageName = UUID.randomUUID().toString();
        storageReference.child("Posts").child("Images").child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child("Posts").child("Images").child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        String downUrl = uri.toString();
                        addPost(downUrl);
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

    private void addPost(String imageUrl) {

        reference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("userName").getValue(String.class);
                userImage = dataSnapshot.child("imageUrl").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:MM");
        String currentDate = format.format(new Date());
        List<Comment> comments = new ArrayList<>();

        Post post = new Post(userImage, userName, currentDate, imageUrl, 0);
        reference.child("posts").push().setValue(post);
        openFragment(new HomeFragment());

    }
}
