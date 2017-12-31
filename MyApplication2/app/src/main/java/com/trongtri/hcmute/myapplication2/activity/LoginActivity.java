package com.trongtri.hcmute.myapplication2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trongtri.hcmute.myapplication2.R;

public class LoginActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    EditText txtEmail, txtPassword;
    Button btnLogin;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        anhXa();
        login();
    }

    private void login() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                if(email.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter email address!", Toast.LENGTH_SHORT).show();
                }
                else if(password.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);

                    //authenticate user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        Toast.makeText(LoginActivity.this, "Email or password is incorrect", Toast.LENGTH_LONG).show();
                                    } else {

                                        //Toast.makeText(LoginActivity.this, "ok", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void anhXa() {
        auth = FirebaseAuth.getInstance();
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        {
            /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userid", user.getUid());
            editor.commit();*/
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
           // Toast.makeText(LoginActivity.this, "You Clicked " + user.getEmail(), Toast.LENGTH_LONG).show();
        }

    }
}
