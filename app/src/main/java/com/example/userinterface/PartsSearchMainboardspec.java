package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityPartsSearchMainboardspecBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsSearchMainboardspec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityPartsSearchMainboardspecBinding binding = ActivityPartsSearchMainboardspecBinding.inflate(getLayoutInflater());
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
                Log.d("KSM", "mainboard jsonString is not null");
                JSONObject itemObject = new JSONObject(jsonString);
                String name = itemObject.getString("name");
                Log.d("KSM", name);
                String manufacturer = itemObject.getString("manufacturer");
                String chipset = itemObject.getString("chipset");
                String socket = itemObject.getString("socket");
                String pcieVer = itemObject.getString("pcieVer") + ".0";
                String size = itemObject.getString("size");
                String memVer = itemObject.getString("memVer");
                String memMax = itemObject.getString("memMax") + " GB";
                String memSlot = itemObject.getString("memSlot") + " 개";
                String m2 = itemObject.getString("M.2") + " 개";
                String sata = itemObject.getString("SATA") + " 개";

                binding.tvMainboardModelName.setText(name);
                binding.valueManufacturer.setText(manufacturer);
                binding.valueChipset.setText(chipset);
                binding.valueSocket.setText(socket);
                binding.valuePcie.setText(pcieVer);
                binding.valueSize.setText(size);
                binding.valueMemVer.setText(memVer);
                binding.valueMemMax.setText(memMax);
                binding.valueMemSlot.setText(memSlot);
                binding.valueM2.setText(m2);
                binding.valueSATA.setText(sata);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.d("KSM", "jsonString is null");
        }
    }
}