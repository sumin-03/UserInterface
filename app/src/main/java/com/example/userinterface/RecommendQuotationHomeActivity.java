package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityRecommendQuotationHomeBinding;

public class RecommendQuotationHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityRecommendQuotationHomeBinding binding = ActivityRecommendQuotationHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.buttonApps.setOnClickListener(v -> {
            Intent intent = new Intent(RecommendQuotationHomeActivity.this, RecommendQuotationByappsActivity.class);
            startActivity(intent);
        });

        binding.buttonParts.setOnClickListener(v -> {
            Intent intent = new Intent(RecommendQuotationHomeActivity.this, RecommendQuotationBypartsActivity.class);
            startActivity(intent);
        });
    }
}