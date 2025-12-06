package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityStorageRecommendBinding;

public class StorageRecommend extends AppCompatActivity {
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityStorageRecommendBinding binding=ActivityStorageRecommendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        currentUser=getIntent().getSerializableExtra("USER_PROFILE", User.class);
        binding.hynix1tbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser==null){
                Intent resultIntent=new Intent().putExtra("storage_selected", binding.hynix1tb.getText().toString());
                setResult(1007, resultIntent);}
                else{
                MyPc.updateStorage(currentUser.getUid(), binding.hynix1tb.getText().toString());
                Toast.makeText(StorageRecommend.this, "저장되었습니다", Toast.LENGTH_SHORT).show();}
            }
        });
        binding.hynix2tbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser==null){
                Intent resultIntent=new Intent().putExtra("storage_selected", binding.hynix2tb.getText().toString());
                setResult(1007, resultIntent);}
                else{
                    MyPc.updateStorage(currentUser.getUid(), binding.hynix1tb.getText().toString());
                    Toast.makeText(StorageRecommend.this, "저장되었습니다", Toast.LENGTH_SHORT).show();}
            }
        });
        binding.samsung1tbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser==null){
                Intent resultIntent=new Intent().putExtra("storage_selected", binding.samsung1tb.getText().toString());
                setResult(1007, resultIntent);}
                else{
                    MyPc.updateStorage(currentUser.getUid(), binding.hynix1tb.getText().toString());
                    Toast.makeText(StorageRecommend.this, "저장되었습니다", Toast.LENGTH_SHORT).show();}
            }
        });
        binding.samsung2tbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser==null){
                Intent resultIntent=new Intent().putExtra("storage_selected", binding.samsung2tbBtn.getText().toString());
                setResult(1007, resultIntent);}
                else{
                    MyPc.updateStorage(currentUser.getUid(), binding.hynix1tb.getText().toString());
                    Toast.makeText(StorageRecommend.this, "저장되었습니다", Toast.LENGTH_SHORT).show();}
            }
        });
    }
}