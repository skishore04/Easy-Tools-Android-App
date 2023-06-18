package com.example.easytools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.easytools.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class QrGeneratorActivity extends AppCompatActivity {

    EditText edit_input;
    Button bt_generate,share,download;
    ImageView iv_qr;
    Bitmap bitmap;
    BitmapDrawable bitmapDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);
        getSupportActionBar().setTitle("Qr Code Generator");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_input = findViewById(R.id.edit_input);
        bt_generate = findViewById(R.id.bt_generate);
        iv_qr = findViewById(R.id.iv_qr);
        LinearLayout buttonlay = findViewById(R.id.buttonlay);
        share = findViewById(R.id.buttonshare);
        download = findViewById(R.id.buttondownload);

        bt_generate.setOnClickListener(v->{
            generateQR();
            buttonlay.setVisibility(View.VISIBLE);
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) iv_qr.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap);
            }

        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapDrawable = (BitmapDrawable) iv_qr.getDrawable();
              bitmap=  bitmapDrawable.getBitmap();

                FileOutputStream fileOutputStream;
                File sdcard = Environment.getExternalStorageDirectory();
                File Directory = new File(sdcard.getAbsolutePath()+"/Download");
                Directory.mkdir();

                String filename= String.format("%d.jpg",System.currentTimeMillis());
                File outfile = new File(Directory,filename);

                Toast.makeText(QrGeneratorActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

                try{
                    fileOutputStream = new FileOutputStream(outfile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(outfile));
                    sendBroadcast(intent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void shareImageandText(Bitmap bitmap) {
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Image Text");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Image Subject");
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent,"Share Via"));

    }

    private Uri getImageToShare(Bitmap bitmap) {
        File folder = new File(getCacheDir(),"images");
        Uri uri = null;

        try{
            folder.mkdir();
            File file =new File(folder,"image.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            uri  = FileProvider.getUriForFile(this,"com.example.easytools",file);
        }
         catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    private void generateQR()
    {
        String text = edit_input.getText().toString().trim();
        MultiFormatWriter writer = new MultiFormatWriter();
        try
        {
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE,600,600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            iv_qr.setImageBitmap(bitmap);

        } catch (WriterException e)
        {
            e.printStackTrace();
        }
    }
}