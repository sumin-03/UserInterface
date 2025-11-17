package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityPartsSearchSsdspecBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsSearchSSDspec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityPartsSearchSsdspecBinding binding = ActivityPartsSearchSsdspecBinding.inflate(getLayoutInflater());
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
                binding.tvHddModelName.setText(itemObject.getString("name"));
                binding.valueManufacturer.setText(itemObject.getString("manufacturer"));
                binding.valueStorage.setText(itemObject.getString("storage") + " TB");
                binding.valueFormfactor.setText(itemObject.getString("formfactor"));
                binding.valuePcie.setText(itemObject.getString("pcie"));
                if(!itemObject.getString("dram_ddr").equals("0"))
                    binding.valueDram.setText("DDR" + itemObject.getString("dram_ddr") + "  "
                            + itemObject.getString("dram_size") + " GB");
                else
                    binding.valueDram.setText("미탑재");
                binding.valueWrite.setText(itemObject.getString("write") + " MB/s");
                binding.valueRead.setText(itemObject.getString("read") + " MB/s");
                binding.valueWriteIOPS.setText(itemObject.getString("write_IOPS") + " K");
                binding.valueReadIOPS.setText(itemObject.getString("read_IOPS") + " K");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}