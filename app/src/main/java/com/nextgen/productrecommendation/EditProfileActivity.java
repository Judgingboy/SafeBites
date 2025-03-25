package com.nextgen.productrecommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    EditText nameET;
    EditText numberET;
    EditText emailET;
    TextView usernameTV;
    TextView submitTV;
    CircleImageView userIV;

    String intentResponse;

    private GlobalPreference globalPreference;
    private String ip,uid;
    private TextView appBarTitleTV;
    private ImageView backIV;
    String image = "";
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    boolean IMAGE_CHANGE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

        iniit();

        intentResponse = getIntent().getStringExtra("userdata");
        setData(intentResponse);

        appBarTitleTV.setText("Edit Profile");
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        userIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        submitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateDetails();
            }
        });
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                userIV.setImageBitmap(bitmap);
                IMAGE_CHANGE = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDetails() {

        Log.d(TAG, "updateDetails: "+IMAGE_CHANGE);

        if(IMAGE_CHANGE) {
            image = getStringImage(bitmap);
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ ip +"/product_recommendation/api/updateProfile.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(EditProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResponse: "+response);
                Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name",nameET.getText().toString());
                params.put("number",numberET.getText().toString());
                params.put("email",emailET.getText().toString());
                params.put("uid",uid);
                if(IMAGE_CHANGE)
                    params.put("image",image);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void setData(String response) {
        try{
            JSONObject obj = new JSONObject(response);
            JSONArray array = obj.getJSONArray("data");
            JSONObject data = array.getJSONObject(0);

            String name = data.getString("name");
            String number = data.getString("number");
            String email = data.getString("email");
            String username = data.getString("username");
            String image = data.getString("image");

            nameET.setText(name);
            numberET.setText(number);
            emailET.setText(email);
            usernameTV.setText(username);

            if (!image.equals("no image")) {
                Glide.with(getApplicationContext())
                        .load("http://" + ip + "/product_recommendation/user_tbl/uploads/" + image)
                        .into(userIV);
            }


        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void iniit() {
        nameET = findViewById(R.id.eNameEditText);
        numberET = findViewById(R.id.eNumberEditText);
        emailET = findViewById(R.id.eEmailEditText);
        usernameTV = findViewById(R.id.eUsernameTextView);
        userIV = findViewById(R.id.eUserIV);
        submitTV = findViewById(R.id.eSubmitTextView);
        appBarTitleTV = findViewById(R.id.appBarTitle);
        backIV = findViewById(R.id.BackImageButton);
    }
}