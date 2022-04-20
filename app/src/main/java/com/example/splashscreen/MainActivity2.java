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

import com.example.splashscreen.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{
    private Button signup;
    private EditText dob, comfirmpassword, username, password, email;
    private ProgressBar progressBar;
    private TextView sign_login;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();




        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(this);

        sign_login = (TextView) findViewById(R.id.sign_login );
        sign_login.setOnClickListener(this);


        dob = (EditText) findViewById(R.id.dob);
        comfirmpassword = (EditText) findViewById(R.id.comfirmpassword);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_login:
                startActivity(new Intent(this, login.class));
                break;
            case R.id.signup:
                registerUser();
                break;
        }
    }
    private void registerUser() {
        String cpsword = comfirmpassword.getText().toString().trim();
        String psword = password.getText().toString().trim();
        String age = dob.getText().toString().trim();
        String uname = username.getText().toString().trim();
        String mail = email.getText().toString().trim();




        if (age.isEmpty()) {
            dob.setError("Date of birth is required");
            dob.requestFocus();
            return;
        }
        if (uname.isEmpty()) {
            username.setError("User name is required");
            username.requestFocus();
            return;
        }
        if (psword.isEmpty()) {
            password.setError("Password is Required");
            password.requestFocus();
            return;
        }
        if (psword.length() < 6) {
            password.setError("Min password length should be 6 characters");
            password.requestFocus();
            return;

        }
        if (cpsword.isEmpty()) {
            comfirmpassword.setError("Comfirmation password is required");
            comfirmpassword.requestFocus();
            return;
        }
        if (cpsword.length() < 6) {
            password.setError("Min password length should be 6 characters");
            password.requestFocus();
            return;

        }
        if (mail.isEmpty()) {
            email.setError("Email is Required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;

        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(mail, psword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(age,uname,mail,psword,cpsword);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity2.this, "User has been registered succesfully!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.VISIBLE);
                                //REDIRECT TO LAYOUT
                                Intent intent = new Intent(getApplicationContext(),login.class);
                                startActivity(intent);

                            }else{
                                Toast.makeText(MainActivity2.this, "Failed to register user! Try again!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }else{
                    Toast.makeText(MainActivity2.this, "Failed to register user!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
//



    }
}