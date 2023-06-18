package com.example.easytools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotActivity extends AppCompatActivity {

    private Button reset;
    private EditText email;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private static final String TAG="ForgotActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        reset = (Button) findViewById(R.id.buttonsend);
        email = (EditText) findViewById(R.id.inputemail2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nemail = email.getText().toString();

                if(TextUtils.isEmpty(nemail)){
                    Toast.makeText(ForgotActivity.this,"Please Enter your Email",Toast.LENGTH_LONG).show();
                    email.setError("Email is Required");
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(nemail).matches()){
                    Toast.makeText(ForgotActivity.this,"Please Re-Enter your Email",Toast.LENGTH_LONG).show();
                    email.setError("Vaild Email is Required");
                    email.requestFocus();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(nemail);
                }
            }
        });
    }

    private void resetPassword( String nemail) {
        auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(nemail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotActivity.this,"Please check your inbox for password reset link!!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotActivity.this,Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
                else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        email.setError("User does not exist or no longer valid. Please register again");
                    }catch(Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(ForgotActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
                progressBar.setVisibility(View.GONE);

            }
        });
    }
}