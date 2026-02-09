package com.nextgen.productrecommendation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IngredientsActivity extends AppCompatActivity {

    private static final String TAG = "IngredientsActivity";
    TextView ingredientsListTV;
    Button analyzeBT;
    Button allergyBT;
    String ingredients;
    LinearLayout noteLL;

    private ImageView backIV;

    Set<String> hazardousIngredients = new HashSet<>();
    String[] ingredientArray;

    Set<String> userAllergies = new HashSet<>();

    private GlobalPreference globalPreference;
    String ip,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        ingredients = getIntent().getStringExtra("ingredients");
        ingredientArray = ingredients.split("\n");

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

        ingredientsListTV = findViewById(R.id.ingredientsListTextView);
        backIV = findViewById(R.id.BackImageButton);
        analyzeBT = findViewById(R.id.analyzeButton);
        allergyBT = findViewById(R.id.allergyButton);
        noteLL = findViewById(R.id.noteLL);
        ingredientsListTV.setText(ingredients);

        getIngredients();

        analyzeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                analyzeBT.setVisibility(View.GONE);
                allergyBT.setVisibility(View.VISIBLE);

                SpannableStringBuilder spannableBuilder = new SpannableStringBuilder();

                for (String ingredient : ingredientArray) {
                    Log.d(TAG, "Original ingredient: " + ingredient);

                    // Normalize the ingredient
                    String sanitizedIngredient = sanitize(ingredient);
                    Log.d(TAG, "Sanitized ingredient: " + sanitizedIngredient);

                    // Check if the sanitized ingredient is in the hazardous list
                    boolean isHazardous = false;
                    for (String hazardous : hazardousIngredients) {
                        if (sanitize(hazardous).equals(sanitizedIngredient)) {
                            isHazardous = true;
                            break;
                        }
                    }

                    if (isHazardous) {
                        // Highlight hazardous ingredient
                        int start = spannableBuilder.length();
                        spannableBuilder.append(ingredient).append("\n"); // Append the original ingredient for display
                        int end = spannableBuilder.length();
                        spannableBuilder.setSpan(
                                new ForegroundColorSpan(Color.RED), // Apply red color
                                start,
                                end - 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        Log.d(TAG, "Hazardous ingredient detected: " + ingredient);
                        noteLL.setVisibility(View.VISIBLE);
                    } else {
                        // Append non-hazardous ingredient as normal
                        spannableBuilder.append(ingredient).append("\n");
                        Log.d(TAG, "Non-hazardous ingredient: " + ingredient);

                    }
                }
                ingredientsListTV.setText(spannableBuilder, TextView.BufferType.SPANNABLE);
            }
        });

        getAllergies();

        allergyBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show progress dialog
                ProgressDialog progressDialog = new ProgressDialog(IngredientsActivity.this);
                progressDialog.setMessage("Analysing Ingredients, please wait...");
                progressDialog.setCancelable(false); // Prevent dismissing dialog by touch
                progressDialog.show();

                /**------  Processing Ingredients ---------
                 * comparing the ingredients from ocr and checking it against the allergies from db
                 *  */

                List<String> matchingAllergies = new ArrayList<>();

                for (String ingredient : ingredientArray) {
                    Log.d(TAG, "Original ingredient: " + ingredient);

                    // Normalize the ingredient
                    String sanitizedIngredient = sanitize(ingredient);
                    Log.d(TAG, "Sanitized ingredient: " + sanitizedIngredient);

                    // Check if the sanitized ingredient is in the user allergies list
                    Log.d(TAG, "allergy ingredient: "+userAllergies);
                    for (String allergy : userAllergies) {
                        if (sanitize(allergy).equals(sanitizedIngredient)) {
                            matchingAllergies.add(ingredient);
                            Log.d(TAG, "Allergic ingredient detected: " + ingredient);
                            break;
                        }
                    }
                }


                if (!matchingAllergies.isEmpty()) {

                    String allergiesDetected = String.join(", ", matchingAllergies);

                    progressDialog.dismiss();
                    Log.d(TAG, "matching allergens found. " + matchingAllergies);
                    Intent intent = new Intent(IngredientsActivity.this, AllergiesFoundActivity.class);
                    intent.putExtra("matchingAllergies", allergiesDetected);
                    intent.putExtra("ingredients",ingredients);
                    startActivity(intent);


                } else {
                    Log.d(TAG, "No matching allergens found.");
                    progressDialog.dismiss();

                    Toast.makeText(IngredientsActivity.this, "No allergies", Toast.LENGTH_SHORT).show();
                    allergyBT.setVisibility(View.GONE);

                }

            }
        });



        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(IngredientsActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String sanitize(String input) {
        return input.trim().toLowerCase().replaceAll("[^a-z0-9 ]", "");
    }

    /* --------------------------- fetching harmful ingredients from db --------------------------- */
    private void getIngredients() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://"+ ip +"/product_recommendation/api/getHarmfulIngredients.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+"..ingredients.."+response);

                if (response.equals("failed")){

                }
                else{
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i=0; i< jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            //String id = object.getString("id");
                            String ingredient = object.getString("ingredient");

                            hazardousIngredients.add(ingredient.trim().toLowerCase().replaceAll("[^a-z0-9 ]", ""));


                        }

                        Log.d(TAG, "onResponse: "+hazardousIngredients);
                        if (!(hazardousIngredients ==null)){
                            analyzeBT.setVisibility(View.VISIBLE);
                        }else {
                            analyzeBT.setVisibility(View.GONE);
                            allergyBT.setVisibility(View.VISIBLE);
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
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }


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

                            if (allergies != null && !allergies.isEmpty()) {
                                String[] allergensArray = allergies.trim().toLowerCase()
                                        .replaceAll("[^a-z0-9, ]", "")
                                        .split(",\\s*");
                                for (String allergen : allergensArray) {
                                    if (!allergen.isEmpty()) {
                                        userAllergies.add(allergen);
                                    }
                                }
                            }

                        }

                        Log.d(TAG, "onResponse: "+userAllergies);

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