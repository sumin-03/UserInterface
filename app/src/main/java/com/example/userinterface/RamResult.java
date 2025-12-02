package com.example.userinterface;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityRamResultBinding;

import java.util.ArrayList;
import java.util.List;

public class RamResult extends AppCompatActivity {
    private User currentUser;
    private List<CpuListActivity.CPU> cpuList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityRamResultBinding binding=ActivityRamResultBinding.inflate(getLayoutInflater());
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
        ListLoad.loadCpuDataFromAssets(this, cpuList);
        CpuListActivity.CPU cpu=cpuList.stream()
                .filter(c -> c.getName().equals(getIntent().getStringExtra("selected_cpu")))
                .findFirst()
                .orElse(null);
        String ramVer=cpu.getMemVer();
        String sizeSmall;
        String sizeBig;
        int countSmall=2;
        int countBig=2;
        switch(cpu.getGrade()) {
            case "0":
                sizeBig="32";
                sizeSmall="16";
                break;
            case "1":
                sizeBig="32";
                sizeSmall="16";
                break;
            case "2":
                sizeBig="16";
                sizeSmall="8";
                break;
            case "3":
                sizeBig="16";
                sizeSmall="8";
                break;
            case "4":
                sizeBig="8";
                sizeSmall="4";
                break;
            default:
                sizeBig="4";
                sizeSmall="4";
                countSmall=1;
                break;
        }
        binding.ramSt.setText(ramVer+" "+sizeBig+"GB"+" x"+countBig);
        binding.verSt.setText(ramVer);
        binding.ramNd.setText(ramVer+" "+sizeSmall+"GB"+" x"+countSmall);
        binding.verNd.setText(ramVer);
        binding.selectButtonSt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPc.updateRam(currentUser.getUid(), ramVer+" "+sizeBig+"GB");
                Toast.makeText(RamResult.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        binding.selectButtonNd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPc.updateRam(currentUser.getUid(), ramVer+" "+sizeSmall+"GB");
                Toast.makeText(RamResult.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnGotoFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}