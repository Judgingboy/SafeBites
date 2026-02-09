package com.nextgen.productrecommendation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AllergyActivity extends AppCompatActivity {

    private static final String TAG = "AllergyActivity";
    EditText allergyET;
    Button submitBT;
    private GlobalPreference globalPreference;
    private String ip,uid;
    private TextView appBarTitleTV;
    private ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

        allergyET = findViewById(R.id.allergyEditText);
        submitBT = findViewById(R.id.submitButton);
        appBarTitleTV = findViewById(R.id.appBarTitle);
        backIV = findViewById(R.id.BackImageButton);

        appBarTitleTV.setText("Add Allergies");
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllergyActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        getAllergies();

        submitBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateAllergies();
            }
        });
    }

    /* --------------------------- Updating allergies to db --------------------------- */
    private void updateAllergies() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ ip +"/product_recommendation/api/updateAllergies.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if (response.equals("success")){
                    Toast.makeText(AllergyActivity.this,"Updated",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AllergyActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(AllergyActivity.this,""+response,Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AllergyActivity.this,""+error,Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            @Nullable
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("allergy",allergyET.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(AllergyActivity.this);
        requestQueue.add(stringRequest);

    }

    /* --------------------------- fetching user allergies from db --------------------------- */

    private void getAllergies() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ ip +"/product_recommendation/api/getAllergies.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if (response.equals("failed")){

                }
                else{
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i=0; i< jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("id");
                            String allergies = object.getString("allergies");

                            allergyET.setText(allergies);

                        }

                    } catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
            }
        }){
            @Override
            @Nullable
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("uid",uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}

