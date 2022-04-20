package com.example.splashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity implements View.OnClickListener {
    private Button signIn;
    private EditText et_password,et_email;
    private TextView register, forgotpassword;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        forgotpassword = (TextView) findViewById(R.id.forgotpassword);
        forgotpassword.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register:
                startActivity(new Intent(this, MainActivity2.class));
                break;
            case R.id.signIn:
                userLogin();
                break;

            case R.id.forgotpassword:
                startActivity(new Intent(this, forgotPass.class));
                break;
        }
    }

    private void userLogin() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if(email.isEmpty()){
            et_email.setError("Email is required!");
            et_email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError(("Please enter a valid email"));
            et_email.requestFocus();
            return;
        }
        if(password.isEmpty()){
            et_password.setError("Password is required");
            et_password.requestFocus();
            return;
        }
        if(password.length() < 6){
            et_password.setError("Min password length is 6 characters!");
            et_password.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        progressBar.setVisibility(View.GONE);
                        //redirect to user profile
                        Intent intent = new Intent(getApplicationContext(),welcomepage.class);
                        startActivity(intent);
                    }else{
                        progressBar.setVisibility(View.GONE);
                        user.sendEmailVerification();
                        Toast.makeText(login.this,"Check your email to verify your account!", Toast.LENGTH_LONG).show();
                    }

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(login.this,"Failed to login! Please check your credentials",Toast.LENGTH_LONG).show();

                }
            }
        });


    }
}

