package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityCoolerRecommendBinding;

public class CoolerRecommend extends AppCompatActivity {
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityCoolerRecommendBinding binding=ActivityCoolerRecommendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        currentUser=getIntent().getSerializableExtra("USER_PROFILE", User.class);
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == 1000)
                    binding.selectedCpuName.setText(o.getData().getStringExtra("cpu_selected"));
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.selectCpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(CoolerRecommend.this, CpuListActivity.class));
            }
        });
        binding.btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cpuName=binding.selectedCpuName.getText().toString();
                if(cpuName.contains("선택")){
                    showToast("cpu를 선택하여 주십시오");
                }
                else {
                    launcher.launch(new Intent(CoolerRecommend.this, CoolerResult.class)
                            .putExtra("selected_cpu", cpuName).putExtra("USER_PROFILE", currentUser));
                    finish();
                }
            }
        });
    }
    private void showToast(String message){
        Toast toast=Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}