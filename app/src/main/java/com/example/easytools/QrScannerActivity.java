package com.example.easytools;


import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;

import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;

import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class QrScannerActivity extends AppCompatActivity {

    ImageView cam ,gal;
    Button btn;
    public static TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        getSupportActionBar().setTitle("Qr Code Scanner");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cam = (ImageView) findViewById(R.id.imageViewcam);
        gal =  (ImageView) findViewById(R.id.imageView2gal);
        text = (TextView) findViewById(R.id.textmain);
        Button copy = (Button) findViewById(R.id.button);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Text",text.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(QrScannerActivity.this, "Text Copied !!", Toast.LENGTH_SHORT).show();
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), scannerView.class));

            }
        });


        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1000);

            }
        });
    }

    @Override

    protected void onActivityResult(int reqCode, int resultCode, Intent data) {

        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            try {

                final Uri imageUri = data.getData();

                final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                try {

                    Bitmap bMap = selectedImage;

                    int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];

                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());



                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);

                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));


                    Reader reader = new MultiFormatReader();

                    Result result = reader.decode(bitmap);

                    QrScannerActivity.text.setText(result.getText());



                }catch (Exception e){

                    e.printStackTrace();

                }


            } catch (FileNotFoundException e) {

                e.printStackTrace();

                Toast.makeText(QrScannerActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();

            }

        }else {

            Toast.makeText(QrScannerActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();

        }

    }

}