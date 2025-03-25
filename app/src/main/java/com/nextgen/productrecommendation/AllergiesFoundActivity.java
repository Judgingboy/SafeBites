package com.nextgen.productrecommendation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AllergiesFoundActivity extends AppCompatActivity {

    TextView ingredientsListTV;
    TextView allergiesTV;
    String allergiesDetected;
    String ingredients;

    TextView appBarTitleTV;
    ImageView backIV;
    LinearLayout ingredientsLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies_found);

        allergiesDetected = getIntent().getStringExtra("matchingAllergies");
        ingredients = getIntent().getStringExtra("ingredients");

        allergiesTV = findViewById(R.id.detectedAllergiesTextView);
        ingredientsListTV = findViewById(R.id.aIngredientsListTextView);
        appBarTitleTV = findViewById(R.id.appBarTitle);
        backIV = findViewById(R.id.BackImageButton);
        ingredientsLL = findViewById(R.id.ingredientsLL);

        appBarTitleTV.setText("Allergies");
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllergiesFoundActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ingredientsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientsListTV.setVisibility(View.VISIBLE);
            }
        });

        ingredientsListTV.setText(ingredients);

        String text = "The Item contains "+ allergiesDetected +" which may be harmful for body avoid consumption";


        SpannableString spannable = new SpannableString(text);

        int start = text.indexOf(allergiesDetected);
        int end = start + allergiesDetected.length();

        spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        allergiesTV.setText(spannable);

       // allergiesTV.setText(text);
    }
}