package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityPartsSearchGpuspecBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsSearchGPUspec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityPartsSearchGpuspecBinding binding = ActivityPartsSearchGpuspecBinding.inflate(getLayoutInflater());
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
                binding.tvGpuModelName.setText(itemObject.getString("name"));
                binding.valueManufacturer.setText(itemObject.getString("manufacturer"));
                binding.valueChipset.setText(itemObject.getString("gpuVer") + " " + itemObject.getString("chipSet"));
                binding.valuePcie.setText(itemObject.getString("pcie"));
                binding.valueBaseClock.setText(itemObject.getString("base") + " MHz");
                binding.valueBoostClock.setText(itemObject.getString("boost") + " MHz");
                binding.valueVram.setText(itemObject.getString("vram") + " GB");
                binding.valuePower.setText(itemObject.getString("power") + " W");
                binding.valueFan.setText(itemObject.getString("fan") + " 개");
                binding.valueLength.setText(itemObject.getString("size") + " mm");
                binding.valueOutput.setText(itemObject.getString("outPut"));

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}