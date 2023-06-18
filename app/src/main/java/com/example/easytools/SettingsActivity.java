package com.example.easytools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            Intent intent = new Intent(SettingsActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id == R.id.settings){
            Intent intent = new Intent(SettingsActivity.this,SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id == R.id.logout){
            auth.signOut();
            Toast.makeText(SettingsActivity.this,"Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SettingsActivity.this,Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(SettingsActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}