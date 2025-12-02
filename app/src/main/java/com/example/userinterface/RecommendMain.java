package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityRecommendBinding;

public class RecommendMain extends AppCompatActivity {
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityRecommendBinding binding=ActivityRecommendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        currentUser=getIntent().getSerializableExtra("USER_PROFILE", User.class);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.cpuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecommendMain.this, CpuRecommend.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.gpuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecommendMain.this, GpuRecommend.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.mainboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecommendMain.this, MainboardRecommend.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.ramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecommendMain.this, RamRecommend.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.caseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecommendMain.this, CaseRecommend.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecommendMain.this, PowerRecommend.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.coolerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecommendMain.this, CoolerRecommend.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.storageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecommendMain.this, StorageRecommend.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
    }
}