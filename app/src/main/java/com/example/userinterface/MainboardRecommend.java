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

import com.example.userinterface.databinding.ActivityMainboardRecommendBinding;

public class MainboardRecommend extends AppCompatActivity {
    private User currentUser;
    private int count=0;
    private Bundle bundle=new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityMainboardRecommendBinding binding=ActivityMainboardRecommendBinding.inflate(getLayoutInflater());
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
        ActivityResultLauncher<Intent> launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode()==1000){binding.selectedCpuName.setText(o.getData().getStringExtra("cpu_selected"));}
                else if(o.getResultCode()==1006){binding.selectedRamName.setText(o.getData().getStringExtra("ram_selected"));}
                else if(o.getResultCode()==300){binding.selectedSizeName.setText(o.getData().getStringExtra("board_size"));}
            }
        });
        binding.selectCpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MainboardRecommend.this, CpuListActivity.class));
            }
        });
        binding.selectRam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MainboardRecommend.this, RamListActivity.class));
            }
        });
        binding.selectSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MainboardRecommend.this, BoardSizeDialog.class));
            }
        });
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count<4){count++;}
                else showToast("유효범위를 초과했습니다");
                binding.count.setText(""+count);
            }
        });
        binding.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count>0){count--;}
                else showToast("유효범위를 초과했습니다");
                binding.count.setText(""+count);
            }
        });
        binding.btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ram=binding.selectedRamName.getText().toString();
                String cpu=binding.selectedCpuName.getText().toString();
                String size=binding.selectedSizeName.getText().toString();
                if(cpu.contains("선택")){
                    showToast("cpu와 크기를 선택하여 주십시오");
                }
                else{
                    bundle.putInt("count", count);
                    bundle.putString("selected_ram", ram);
                    bundle.putString("selected_cpu", cpu);
                    bundle.putString("selected_size", size);
                    launcher.launch(new Intent(MainboardRecommend.this, MainboardResult.class)
                            .putExtra("condition_for_board",bundle).putExtra("USER_PROFILE", currentUser));
                finish();}
            }
        });
    }
    private void showToast(String message){
        Toast toast=Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}