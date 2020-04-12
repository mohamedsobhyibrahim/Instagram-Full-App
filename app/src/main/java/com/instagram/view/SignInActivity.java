package com.instagram.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.instagram.R;
import com.instagram.utils.InputValidator;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEdt;
    private EditText passwordEdt;
    private Button signinBtn;
    private TextView signupTv;

    private String email;
    private String password;

    private ProgressDialog progressDialog;

    //Firebase Authentication
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        init();
        signinBtn.setOnClickListener(this);
        signupTv.setOnClickListener(this);

    }

    private void init() {
        emailEdt = findViewById(R.id.signIn_email_editText);
        passwordEdt = findViewById(R.id.signIn_password_editText);
        signinBtn = findViewById(R.id.signIn_sign_in_button);
        signupTv = findViewById(R.id.sign_in_signUp_TextView);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.signIn_sign_in_button:
                if (getInputData())
                    signIn(email, password);
                break;
            case R.id.sign_in_signUp_TextView:
                intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    private boolean getInputData() {
        if (!InputValidator.signInValidation(getApplicationContext(), emailEdt, passwordEdt))
            return false;

        email = emailEdt.getText().toString();
        password = passwordEdt.getText().toString();
        return true;
    }

    private void signIn(String email, String password) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ....");
        progressDialog.setCancelable(false);
        progressDialog.show();


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
