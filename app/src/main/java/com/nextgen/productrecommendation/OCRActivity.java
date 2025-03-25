package com.nextgen.productrecommendation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.Locale;

public class OCRActivity extends AppCompatActivity {

    final private static String TAG ="OCRActivity";

    ImageView selectedIV;
    Button recognizeBT;

    private Uri photoUri;
    String encodeImage;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int CAMERA_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocractivity);

        selectedIV = findViewById(R.id.selectedImageView);
        recognizeBT = findViewById(R.id.recognizeButton);

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }

        recognizeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            }
        });

        
    }

    private void openCamera() {

         /* Capturing image and uploading
          code can be used to capture image and upload it in android 12 and above devices with specific
          permissions in manifest file as manage storage and adding a service provider to fetch file path
         */
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.nextgen.anandu", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    private File createImageFile() throws IOException{

        Log.d(TAG, "createImageFile: "+"success");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return imageFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Log.e("OCRActivity", "Camera permission denied");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Image capture was successful, process the captured image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                encodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                // Use the encoded image as needed
                selectedIV.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* --------------------------- OCR Text Detection --------------------------- */

    private void detectText() {
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Detecting text, please wait...");
        progressDialog.setCancelable(false); // Prevent dismissing dialog by touch
        progressDialog.show();

        // Perform text detection
        new Thread(() -> {
            try {
                // Step 1: Define TextRecognizer
                TextRecognizer recognizer = new TextRecognizer.Builder(OCRActivity.this).build();

                if (!recognizer.isOperational()) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(OCRActivity.this, "Text recognizer is not operational.", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Step 2: Get bitmap from ImageView
                Bitmap bitmap = ((BitmapDrawable) selectedIV.getDrawable()).getBitmap();

                // Step 3: Create a Frame
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();

                // Step 4: Detect text
                SparseArray<TextBlock> sparseArray = recognizer.detect(frame);

                // Step 5: Append detected text
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < sparseArray.size(); i++) {
                    TextBlock tx = sparseArray.get(i);
                    if (tx != null) {
                        // Split detected text by commas and add each segment on a new line
                        String[] segments = tx.getValue().split(",");
                        for (String segment : segments) {
                            stringBuilder.append(segment.trim()).append("\n");
                        }
                    }
                }

                // Step 6: Pass text to the next activity
                runOnUiThread(() -> {
                    progressDialog.dismiss(); // Close the progress dialog
                    Intent intent = new Intent(OCRActivity.this, IngredientsActivity.class);
                    intent.putExtra("ingredients", stringBuilder.toString());
                    startActivity(intent);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(OCRActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }
        }).start();
    }
}


/** packages used
 *
 * Google Vision API for OCR
 *
 * ML Kit for Text Recognition
 *
 * */