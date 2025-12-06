package com.example.userinterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface.databinding.ActivityPowerResultBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.util.ArrayList;
import java.util.List;

public class PowerResult extends AppCompatActivity {
    private User currentUser;
    private List<PowerListActivity.Power> powerList=new ArrayList<>();
    private List<GpuListActivity.GPU> gpuList=new ArrayList<>();
    private List<PowerListActivity.Power> filtered=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityPowerResultBinding binding=ActivityPowerResultBinding.inflate(getLayoutInflater());
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
        ListLoad.loadGpuDataFromAssets(this, gpuList);
        ListLoad.loadPowerDataFromAssets(this, powerList);
        String recommendPower="800";
        GpuListActivity.GPU gpu=gpuList.stream()
                .filter(g -> g.getName().equals(getIntent().getStringExtra("selected_gpu")))
                .findFirst()
                .orElse(null);
        if(gpu!=null) recommendPower=gpu.getPower();
        for (PowerListActivity.Power power : powerList) {
            if (power.getPower().equals(recommendPower)) {
                filtered.add(power);
            }
        }
        binding.main.setLayoutManager(new LinearLayoutManager(this));
        binding.main.setAdapter(new ListAdapter(filtered));
        binding.btnGotoFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private class ListViewHolder extends RecyclerView.ViewHolder {
        ListCardBinding binding;

        public ListViewHolder(ListCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPc.updatePower(currentUser.getUid(), binding.name.getText().toString());
                    Toast.makeText(PowerResult.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void bind(PowerListActivity.Power power) {
            binding.name.setText(power.getName());
            binding.manufacturer.setText(power.getManufacturer());
            binding.selectButton.setText("저장");
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private List<PowerListActivity.Power> list;

        public ListAdapter(List<PowerListActivity.Power> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListCardBinding binding = ListCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            );
            return new ListViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
            holder.bind(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}