package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityPartsSearchRamspecBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsSearchRAMspec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityPartsSearchRamspecBinding binding = ActivityPartsSearchRamspecBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("JSON_STRING");

        if(jsonString != null) {
            try {
                JSONObject itemObject = new JSONObject(jsonString);
                binding.tvRamModelName.setText(itemObject.getString("name"));
                binding.valueManufacturer.setText(itemObject.getString("manufacturer"));
                binding.valueDdr.setText(itemObject.getString("ddr"));
                binding.valueSize.setText(itemObject.getString("size") + " GB");
                binding.valueClock.setText(itemObject.getString("clock") + " MHz");
                if(itemObject.getString("heatsink").equals("0"))
                    binding.valueHeatsink.setText("방열판 없음");
                else
                    binding.valueHeatsink.setText("방열판 있음");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}