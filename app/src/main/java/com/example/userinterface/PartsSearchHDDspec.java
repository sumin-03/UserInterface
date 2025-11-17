package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityPartsSearchHddspecBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsSearchHDDspec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityPartsSearchHddspecBinding binding = ActivityPartsSearchHddspecBinding.inflate(getLayoutInflater());
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
                binding.valueInch.setText(itemObject.getString("inch") + " 인치");
                binding.valueInterface.setText(itemObject.getString("interface"));
                binding.valueRpm.setText(itemObject.getString("RPM") + " RPM");
                binding.valueBuffer.setText("메모리 " + itemObject.getString("buffer") + " MB");
                binding.valueWrite.setText(itemObject.getString("write") + " MB/s");
                binding.valueRead.setText(itemObject.getString("read") + " MB/s");
                binding.valueRecord.setText(itemObject.getString("recode"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}