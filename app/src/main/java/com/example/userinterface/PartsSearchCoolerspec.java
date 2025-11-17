package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityPartsSearchCoolerspecBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsSearchCoolerspec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityPartsSearchCoolerspecBinding binding = ActivityPartsSearchCoolerspecBinding.inflate(getLayoutInflater());
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
                binding.tvCaseModelName.setText(itemObject.getString("name"));
                binding.valueManufacturer.setText(itemObject.getString("manufacturer"));
                if(itemObject.getString("kind").equals("air"))
                    binding.valueKind.setText("공랭");
                else
                    binding.valueKind.setText("수랭");
                binding.valueSize.setText(itemObject.getString("size"));
                binding.valueSocket.setText(itemObject.getString("socket"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}