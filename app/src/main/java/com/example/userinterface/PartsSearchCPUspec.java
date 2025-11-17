package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityPartsSearchCpuspecBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class PartsSearchCPUspec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityPartsSearchCpuspecBinding binding = ActivityPartsSearchCpuspecBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("JSON_STRING");

        if (jsonString != null) {
            try {
                Log.d("KSM", "jsonString is not null");
                // 2. 문자열을 다시 JSONObject로 변환
                JSONObject itemObject = new JSONObject(jsonString);
                // 3. JSONObject에서 필요한 데이터 추출
                String name = itemObject.getString("name");
                String manufacturer = itemObject.getString("manufacturer");
                String socket = itemObject.getString("socket");
                String core = itemObject.getString("core");
                String baseClock = itemObject.getString("baseClock");
                String boostClock = itemObject.getString("boostClock");
                String thread = itemObject.getString("thread");
                String L2 = itemObject.getString("L2");
                String L3 = itemObject.getString("L3");
                String gpu = itemObject.getString("gpu");
                String TDP = itemObject.getString("TDP");

                TextView nameTV = binding.tvCpuModelName;
                TextView manufacacturerTV = binding.valueManufacturer;
                TextView socketTV = binding.valueSocket;
                TextView coreTV = binding.valueCores;
                TextView threadTV = binding.valueThreads;
                TextView baseClockTV = binding.valueBaseClock;
                TextView boostClockTV = binding.valueBoostClock;
                TextView L2TV = binding.valueL2cache;
                TextView L3TV = binding.valueL3cache;
                TextView gpuTV = binding.valueGraphics;
                TextView TDPTV = binding.valueTdp;

                nameTV.setText(name);
                manufacacturerTV.setText(manufacturer);
                socketTV.setText(socket);
                coreTV.setText(core);
                threadTV.setText(thread);
                baseClockTV.setText(baseClock);
                boostClockTV.setText(boostClock);
                L2TV.setText(L2);
                L3TV.setText(L3);
                gpuTV.setText(gpu);
                TDPTV.setText(TDP);
                binding.valueMemoryVersion.setText(itemObject.getString("memVer"));
                binding.valueMemoryClock.setText(itemObject.getString("memClock"));
            } catch (JSONException e) {
                e.printStackTrace();
                // 오류 처리 (예: 데이터를 불러올 수 없다는 메시지 표시)
            }
        }
    }
}