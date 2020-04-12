package com.instagram.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instagram.R;
import com.instagram.network.firebase.Firebase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private ImageView addPhotoImageView;
    private Button logoutBtn;
    private CircleImageView profilePhoto;
    private TextView nameTv;
    private TextView emailTv;

    private String name;
    private String email;
    private String userImage;
    private DatabaseReference reference;

    private static final int PICK_IMAGE = 1;


    private Uri photoUri;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    //Firebase Storage
    StorageReference storageReference;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(view);
        reference = Firebase.getDatabaseRef();
        mAuth = Firebase.getAuthenticationRef();
        user = mAuth.getCurrentUser();
        storageReference = Firebase.getStorageRef();

        addPhotoImageView.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        getMe();


        return  view;
    }

    public void initUI(View view){
        addPhotoImageView = view.findViewById(R.id.add_photo_imageView);
        logoutBtn = view.findViewById(R.id.logout_button);
        profilePhoto = view.findViewById(R.id.img_profile);
        nameTv = view.findViewById(R.id.name_textView);
        emailTv = view.findViewById(R.id.mail_textView);
    }

    public void getMe(){
        reference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("userName").getValue(String.class);
                email = dataSnapshot.child("email").getValue(String.class);
                userImage = dataSnapshot.child("imageUrl").getValue(String.class);

                nameTv.setText(name);
                emailTv.setText(email);
                if (!userImage.isEmpty()) {
                    Picasso.get()
                            .load(userImage)
                            .placeholder(R.drawable.placerholder)
                            .into(profilePhoto);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.add_photo_imageView:
                selectImage();
                break;
            case R.id.logout_button:
                mAuth.signOut();
                startActivity(new Intent(getActivity() , SignInActivity.class));
                break;
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            photoUri = data.getData();
            uploadImage(photoUri);
            Picasso.get()
                    .load(photoUri)
                    .placeholder(R.drawable.placerholder)
                    .into(profilePhoto);
        }
    }

    private void uploadImage(Uri imageUri) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading ....");
        progressDialog.show();

        final String imageName = user.getUid() + ".jpg";
        storageReference.child("users").child("Images").child(user.getUid() + "/" + imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child("users").child("Images").child(user.getUid() + "/" + imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        String downUrl = uri.toString();
                        reference.child("users").child(user.getUid()).child("imageUrl").setValue(downUrl);
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
