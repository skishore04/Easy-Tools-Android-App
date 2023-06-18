package com.example.easytools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.CharArrayWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private TextView welname;
    private String fullname;
    RecyclerView rcv;
    myadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Easy Tools");
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        welname =(TextView) findViewById(R.id.welname);

        rcv = (RecyclerView) findViewById(R.id.rclview);
      //  rcv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new myadapter(dataqueue(),getApplicationContext());
        rcv.setAdapter(adapter);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
      //  rcv.setLayoutManager(linearLayoutManager);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        rcv.setLayoutManager(gridLayoutManager);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(!firebaseUser.isEmailVerified()){
            firebaseUser.sendEmailVerification();
            auth.signOut();
            showAlertDialog();
        }else {
            showUserProfile(firebaseUser);
        }

    }

    public ArrayList<Model> dataqueue(){
        ArrayList<Model> holder = new ArrayList<>();

        Model ob1 = new Model();
        ob1.setHeader("Chat AI");
        ob1.setDesc("Ask me Anything!!");
        ob1.setImgname(R.drawable.bot1);
        holder.add(ob1);

        Model ob2 = new Model();
        ob2.setHeader("AI Images");
        ob2.setDesc("Generate Image from text");
        ob2.setImgname(R.drawable.ai);
        holder.add(ob2);

        Model ob3 = new Model();
        ob3.setHeader("QR Code Scanner");
        ob3.setDesc("Scan any QR Code");
        ob3.setImgname(R.drawable.qr);
        holder.add(ob3);

        Model ob4 = new Model();
        ob4.setHeader("Qr Code Generator");
        ob4.setDesc("Generate QR from text");
        ob4.setImgname(R.drawable.qrgen);
        holder.add(ob4);

        Model ob5 = new Model();
        ob5.setHeader("Calculator");
        ob5.setDesc("Do Simple Calculations");
        ob5.setImgname(R.drawable.simpcal);
        holder.add(ob5);

        Model ob6 = new Model();
        ob6.setHeader("Scientific Calci");
        ob6.setDesc("Do Complex Calculations");
        ob6.setImgname(R.drawable.scical);
        holder.add(ob6);




        return holder;

    }

    private void showUserProfile(FirebaseUser firebaseUser) {

        String userId = firebaseUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    fullname = firebaseUser.getDisplayName();

                    welname.setText(fullname+"!");

                }else{
                    Toast.makeText(MainActivity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please Verify Your Email now. You will be not able to access until you verify your email.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_APP_EMAIL);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finishAffinity();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.refresh){
            startActivity(getIntent());
            finish();
        }
        else if(id == R.id.profile){
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.settings){
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.logout){
            auth.signOut();
            Toast.makeText(MainActivity.this,"Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this,Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(MainActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}