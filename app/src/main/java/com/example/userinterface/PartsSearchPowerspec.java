package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityPartsSearchPowerspecBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsSearchPowerspec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityPartsSearchPowerspecBinding binding = ActivityPartsSearchPowerspecBinding.inflate(getLayoutInflater());
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
                String name = itemObject.getString("name");
                String manufacturer = itemObject.getString("manufacturer");
                String plus80 = itemObject.getString("plus80");
                String size = itemObject.getString("size");

                binding.tvPowerModelName.setText(name);
                binding.valueManufacturer.setText(manufacturer);
                binding.valuePlus80.setText(plus80);
                binding.valueSize.setText(size);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}