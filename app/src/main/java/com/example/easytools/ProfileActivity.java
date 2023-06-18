package com.example.easytools;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView welname,name,email,phone;
    private String fullname,fullemail,phonenumber;
    private ImageView profile,camera;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST =1;
    private Uri uriImage;
    private Button upload;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        welname = (TextView) findViewById(R.id.welcomename);
        name = (TextView) findViewById(R.id.nametext);
        email =(TextView) findViewById(R.id.emailtext);
        phone = (TextView) findViewById(R.id.phtext);
        progressBar=(ProgressBar) findViewById(R.id.progressBar) ;
        profile = (ImageView) findViewById(R.id.profileimage);
        camera = (ImageView) findViewById(R.id.camera);
        upload = (Button) findViewById(R.id.buttonupload);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if(firebaseUser==null){
            Toast.makeText(this, "Something went Wrong, User details not available at the moment", Toast.LENGTH_SHORT).show();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");
        Uri uri = firebaseUser.getPhotoUrl();

        Picasso.with(ProfileActivity.this).load(uri).into(profile);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                upload.setVisibility(View.VISIBLE);

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Uploadpic();
            }
        });

    }

    private void Uploadpic() {
        if(uriImage!=null){
            StorageReference fileReference = storageReference.child(auth.getCurrentUser().getUid() + "."+ getFileExtension(uriImage));
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = auth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri)
                                    .build();
                            firebaseUser.updateProfile(profileUpdates);

                            Toast.makeText(ProfileActivity.this, "Picture Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
                            upload.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));


    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()!=null){
            uriImage =data.getData();
            profile.setImageURI(uriImage);
        }
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
                    fullemail = firebaseUser.getEmail();
                    phonenumber= readUserDetails.phnum;

                    welname.setText(fullname+"!");
                    name.setText(fullname);
                    email.setText(fullemail);
                    phone.setText(phonenumber);

                    Uri uri = firebaseUser.getPhotoUrl();

                    Picasso.with(ProfileActivity.this).load(uri).into(profile);
                }else{
                    Toast.makeText(ProfileActivity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
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
            Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id == R.id.settings){
            Intent intent = new Intent(ProfileActivity.this,SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id == R.id.logout){
            auth.signOut();
            Toast.makeText(ProfileActivity.this,"Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ProfileActivity.this,Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(ProfileActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        upload.setVisibility(View.GONE);
    }
}