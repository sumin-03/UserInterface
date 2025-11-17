package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityPartsSearchCasespecBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsSearchCasespec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityPartsSearchCasespecBinding binding = ActivityPartsSearchCasespecBinding.inflate(getLayoutInflater());
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
                binding.valueBoardSize.setText(itemObject.getString("boardSize"));
                binding.valueCoolerSize.setText(itemObject.getString("coolerSize") + " mm");
                binding.valueGpuSize.setText(itemObject.getString("gpuSize") + " mm");
                if(itemObject.getString("powerSize").equals("top"))
                    binding.valuePowerSize.setText("상단");
                else if(itemObject.getString("powerSize").equals("bottom"))
                    binding.valuePowerSize.setText("하단");
                else
                    binding.valuePowerSize.setText(itemObject.getString("powerSize") + " mm");
                binding.valueSize.setText(itemObject.getString("size").toUpperCase());
                if(itemObject.getString("atxPower").equals("TRUE"))
                    binding.valueAtxPower.setText("지원");
                else
                    binding.valueAtxPower.setText("미지원");
            }
            catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
}