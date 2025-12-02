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

import com.example.userinterface.databinding.ActivityCaseRecommendBinding;

public class CaseRecommend extends AppCompatActivity {
    private User currentUser;
    private Bundle bundle=new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityCaseRecommendBinding binding=ActivityCaseRecommendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        currentUser=getIntent().getSerializableExtra("USER_PROFILE", User.class);
        ActivityResultLauncher<Intent> launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode()==1002){
                    binding.selectedMainboardName.setText(o.getData().getStringExtra("mainboard_selected"));
                }
                else if(o.getResultCode()==1004){
                    binding.selectedCoolerName.setText(o.getData().getStringExtra("cooler_selected"));
                }
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.selectMainboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(CaseRecommend.this, MainboardListActivity.class));
            }
        });
        binding.selectCooler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(CaseRecommend.this, CoolerListActivity.class));
            }
        });
        binding.btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedBoard=binding.selectedMainboardName.getText().toString();
                String selectedCooler=binding.selectedCoolerName.getText().toString();
                if(selectedBoard.contains("선택")||selectedCooler.contains("선택")){
                    showToast("제품을 선택하여 주십시오");
                }
                else {
                    bundle.putString("selected_mainboard",selectedBoard);
                    bundle.putString("selected_cooler", selectedCooler);
                    launcher.launch(new Intent(CaseRecommend.this, CaseResult.class)
                        .putExtra("condition_for_case", bundle).putExtra("USER_PROFILE", currentUser));
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