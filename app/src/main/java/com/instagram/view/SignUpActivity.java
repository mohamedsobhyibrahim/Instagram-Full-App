package com.instagram.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.instagram.R;
import com.instagram.network.firebase.Firebase;
import com.instagram.network.models.User;
import com.instagram.utils.InputValidator;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameEdt;
    private EditText emailEdt;
    private EditText passwordEdt;
    private EditText confirmPasswordEdt;
    private Button signUpBtn;

    private String name;
    private String email;
    private String password;
    private ProgressDialog progressDialog;

    //Firebase Authentication
    FirebaseAuth mAuth;

    //Database Reference
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        reference = Firebase.getDatabaseRef();
        mAuth = Firebase.getAuthenticationRef();

        init();
        signUpBtn.setOnClickListener(this);
    }

    private void init() {
        nameEdt = findViewById(R.id.signUp_name_editText);
        emailEdt = findViewById(R.id.signUp_email_editText);
        passwordEdt = findViewById(R.id.signUp_password_editText);
        confirmPasswordEdt = findViewById(R.id.signUp_confirmPassword_editText);
        signUpBtn = findViewById(R.id.signUp_button);

        signUpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUp_button:
                if (getInputData()) {
                    register(email, password);
                }
                break;
        }
    }

    public boolean getInputData() {
        if (!InputValidator.signUpValidation(getApplicationContext(), nameEdt, emailEdt, passwordEdt, confirmPasswordEdt))
            return false;

        name = nameEdt.getText().toString();
        email = emailEdt.getText().toString();
        password = passwordEdt.getText().toString();
        return true;
    }

    private void register(String email, String password) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ....");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    FirebaseUser user = task.getResult().getUser();
                    saveUser(user);
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }
        });
    }

    private void saveUser(final FirebaseUser user) {

        User newUser = new User(user.getUid(), name, "", user.getEmail());
        reference.child("users").child(user.getUid()).setValue(newUser);
        startActivity(new Intent(this, SignInActivity.class));
        finish();


    }
}
