package com.example.easytools;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    TextView login;
    private EditText name, email,number, password;
    private ProgressBar progressBar;
    Button signup;
    private static final String TAG="Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
          login = (TextView) findViewById(R.id.loginbut);
          login.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent i = new Intent(Register.this,Login.class);
                  startActivity(i);
                  finish();
                  finishAffinity();
              }
          });

          name = (EditText) findViewById(R.id.inputusername);
          email = (EditText) findViewById(R.id.inputemail);
          password = (EditText) findViewById(R.id.inputpassword);
          number =(EditText) findViewById(R.id.inputpnum);
          progressBar =(ProgressBar) findViewById(R.id.progressBar); 
          signup= (Button) findViewById(R.id.buttonsignup);

          signup.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  String fname = name.getText().toString();
                  String femail = email.getText().toString();
                  String pnum = number.getText().toString();
                  String pass = password.getText().toString();

                  String mobile = "[6-9][0-9]{9}" ;
                  Matcher mobilematcher;
                  Pattern mobilePattern = Pattern.compile(mobile);
                  mobilematcher = mobilePattern.matcher(pnum);

                  if (TextUtils.isEmpty(fname)){
                      Toast.makeText(Register.this,"Please Enter your Name",Toast.LENGTH_LONG).show();
                      name.setError("Name is Required");
                      name.requestFocus();
                  }
                  else if (TextUtils.isEmpty(femail)){
                      Toast.makeText(Register.this,"Please Enter your Email",Toast.LENGTH_LONG).show();
                      email.setError("Email is Required");
                      email.requestFocus();
                  }
                  else if(!Patterns.EMAIL_ADDRESS.matcher(femail).matches()){
                      Toast.makeText(Register.this,"Please Re-Enter your Email",Toast.LENGTH_LONG).show();
                      email.setError("Vaild Email is Required");
                      email.requestFocus();
                  }
                  else if (TextUtils.isEmpty(pnum)){
                      Toast.makeText(Register.this,"Please Enter your Phone Number",Toast.LENGTH_LONG).show();
                      number.setError("Phone Number is Required");
                      number.requestFocus();
                  }

                  else if (TextUtils.isEmpty(pass)){
                      Toast.makeText(Register.this,"Please Enter the Passsword",Toast.LENGTH_LONG).show();
                      password.setError("Password is Required");
                      password.requestFocus();
                  }
                  else if(!mobilematcher.find()){
                      Toast.makeText(Register.this,"Please Enter Valid Phone Number",Toast.LENGTH_LONG).show();
                      number.setError("Phone Number is not Valid");
                      number.requestFocus();

                  }
                  else if(pass.length()< 6){
                      Toast.makeText(Register.this,"Passsword must be atleast 6 characters",Toast.LENGTH_LONG).show();
                      password.setError("Password too weak");
                  }
                  else{
                      progressBar.setVisibility(View.VISIBLE);

                      registerUser(fname,femail,pnum,pass);
                  }
              }
          });

    }

    private void registerUser(String fname, String femail, String pnum, String pass) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(femail,pass).addOnCompleteListener(Register.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(fname).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fname,femail,pnum,pass);

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(Register.this,"User Registered Successfully,Please Verify Your email",Toast.LENGTH_LONG).show();
                                        Intent intent =new Intent(Register.this,Login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        finishAffinity();
                                    }
                                    else {
                                        Toast.makeText(Register.this,"User Registration Failed",Toast.LENGTH_LONG).show();

                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            });

                        }
                        else{
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                name.setError("Password too weak");
                                name.requestFocus();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                password.setError("Your email is invalid or already in use. Kindly Re-enter");
                                password.requestFocus();
                            }
                            catch(FirebaseAuthUserCollisionException e){
                                password.setError("User is already registered with this email.Use an other email");
                                password.requestFocus();
                            }
                            catch (Exception e){
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}