package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityBoardSizeDialogBinding;

public class BoardSizeDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityBoardSizeDialogBinding binding=ActivityBoardSizeDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.atx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(300, new Intent().putExtra("board_size", binding.atxSize.getText().toString()));
                finish();
            }
        });
        binding.matx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(300, new Intent().putExtra("board_size", binding.matxSize.getText().toString()));
                finish();
            }
        });
    }
}