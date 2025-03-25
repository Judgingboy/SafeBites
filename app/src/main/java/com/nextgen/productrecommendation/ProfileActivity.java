package com.nextgen.productrecommendation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    TextView nameTV;
    TextView numberTV;
    TextView emailTV;
    TextView usernameTV;
    ImageView profileEditIV;
    ImageView userIV;
    private GlobalPreference globalPreference;
    private String ip,uid;
    String userdata;
    ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

        nameTV = findViewById(R.id.uNameTextView);
        numberTV = findViewById(R.id.uPhoneNoTextView);
        emailTV = findViewById(R.id.uEmailTextView);
        usernameTV = findViewById(R.id.uUsernameTextView);
        profileEditIV = findViewById(R.id.uEditImageView);
        userIV = findViewById(R.id.uUserImageView);
        backIV = findViewById(R.id.BackImageButton);


        getUserDetails();

        profileEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                finish();
                intent.putExtra("userdata",userdata);
                startActivity(intent);
            }
        });

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getUserDetails() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ ip +"/product_recommendation/api/getUserDetails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if(!response.equals("")) {
                    try {

                        userdata = response;

                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("data");
                        JSONObject jsonObject = array.getJSONObject(0);
                        String name = jsonObject.getString("name");
                        String number = jsonObject.getString("number");
                        String email = jsonObject.getString("email");
                        String username = jsonObject.getString("username");
                        String image = jsonObject.getString("image");
                        
                        nameTV.setText(name);
                        nameTV.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                        numberTV.setText(number);
                        emailTV.setText(email);
                        usernameTV.setText(username);

                        if (!image.equals("no image")) {
                            Glide.with(getApplicationContext())
                                    .load("http://" + ip + "/product_recommendation/user_tbl/uploads/" + image)
                                    .into(userIV);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    profileEditIV.setVisibility(View.INVISIBLE);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                profileEditIV.setVisibility(View.INVISIBLE);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("uid",uid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(stringRequest);
    }
}