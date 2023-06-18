package com.example.easytools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextView signup;
    private EditText  email, password;
    ProgressBar progressBar;
    Button login,forgot;
    private FirebaseAuth auth;
    private static final String TAG="Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        auth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.inputemail1) ;
        password = (EditText) findViewById(R.id.inputpassword1);
        progressBar =(ProgressBar) findViewById(R.id.progressBar);
        forgot = (Button) findViewById(R.id.buttonforgot);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this,"You can reset your Password now!!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(Login.this,ForgotActivity.class));
            }
        });

        signup = (TextView) findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
                finishAffinity();
            }
        });

        login = (Button) findViewById(R.id.buttonlogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String femail= email.getText().toString();
                String pass = password.getText().toString();
                 if (TextUtils.isEmpty(femail)){
                    Toast.makeText(Login.this,"Please Enter your Email",Toast.LENGTH_LONG).show();
                    email.setError("Email is Required");
                    email.requestFocus();
                }
                 else if(!Patterns.EMAIL_ADDRESS.matcher(femail).matches()){
                     Toast.makeText(Login.this,"Please Re-Enter your Email",Toast.LENGTH_LONG).show();
                     email.setError("Vaild Email is Required");
                     email.requestFocus();
                 }
                 else if (TextUtils.isEmpty(pass)){
                     Toast.makeText(Login.this,"Please Enter the Passsword",Toast.LENGTH_LONG).show();
                     password.setError("Password is Required");
                     password.requestFocus();
                 }
                 else {
                     progressBar.setVisibility(View.VISIBLE);
                     loginUser(femail,pass);
                 }
            }
        });

    }

    private void loginUser(String mail, String pwd) {
        auth.signInWithEmailAndPassword(mail,pwd).addOnCompleteListener(Login.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    finishAffinity();
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(Login.this,"You Successfully Logged in!!",Toast.LENGTH_LONG).show();
                    }
                    else if (!firebaseUser.isEmailVerified()){
                        Toast.makeText(Login.this,"Please verify your Email and login again!!",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        email.setError("User does not exists or is no longer valid. Please register again");
                        email.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        password.setError("Invalid Credentials. Enter Correct Email and Password");
                        password.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(Login.this,MainActivity.class);
            startActivity(intent);
            finish();
            finishAffinity();
        }
        else{
            Toast.makeText(Login.this,"Please Login",Toast.LENGTH_LONG).show();
        }
    }
}