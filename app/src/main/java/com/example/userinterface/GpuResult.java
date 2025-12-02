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

import com.example.userinterface.databinding.ActivityGpuResultBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.util.ArrayList;
import java.util.List;

public class GpuResult extends AppCompatActivity {
    private User currentUser;
    private List<CpuListActivity.CPU> cpuList=new ArrayList<>();
    private List<GpuListActivity.GPU> gpuList=new ArrayList<>();
    private List<GpuListActivity.GPU> filtered=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityGpuResultBinding binding=ActivityGpuResultBinding.inflate(getLayoutInflater());
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
        ListLoad.loadGpuDataFromAssets(this, gpuList);
        CpuListActivity.CPU cpu =cpuList.stream()
                .filter(c -> c.getName().equals(getIntent().getStringExtra("selected_cpu")))
                .findFirst()
                .orElse(null);
        String grade= null;
        if (cpu != null) {
            grade = cpu.getGrade();
        }
        else grade="1";
        filtered.clear();
        switch(grade) {
            case "0":
                for (GpuListActivity.GPU gpu : gpuList) {
                    if (gpu.getGrade().equals("0")||gpu.getGrade().equals("1")) {
                        filtered.add(gpu);
                    }
                }
                break;
            case "1":
                for (GpuListActivity.GPU gpu : gpuList) {
                    if (gpu.getGrade().equals("1")||gpu.getGrade().equals("2")||gpu.getGrade().equals("3")) {
                        filtered.add(gpu);
                    }
                }
                break;
            case "2":
                for (GpuListActivity.GPU gpu : gpuList) {
                    if (gpu.getGrade().equals("4")||gpu.getGrade().equals("5")) {
                        filtered.add(gpu);
                    }
                }
                break;
            case "3":
                for (GpuListActivity.GPU gpu : gpuList) {
                    if (gpu.getGrade().equals("5")||gpu.getGrade().equals("6")) {
                        filtered.add(gpu);
                    }
                }
                break;
            case "4":
                for (GpuListActivity.GPU gpu : gpuList) {
                    if (gpu.getGrade().equals("6")||gpu.getGrade().equals("7")) {
                        filtered.add(gpu);
                    }
                }
                break;
            case "5":
                for (GpuListActivity.GPU gpu : gpuList) {
                    if (gpu.getGrade().equals("7")||gpu.getGrade().equals("8")||gpu.getGrade().equals("9")) {
                        filtered.add(gpu);
                    }
                }
                break;
            default:
                for (GpuListActivity.GPU gpu : gpuList) {
                    if (gpu.getGrade().equals("10")||gpu.getGrade().equals("11")) {
                        filtered.add(gpu);
                    }
                }
                break;
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
                    MyPc.updateGpu(currentUser.getUid(), binding.name.getText().toString());
                    Toast.makeText(GpuResult.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void bind(GpuListActivity.GPU gpu) {
            binding.name.setText(gpu.getName());
            if(gpu.getName().contains("Radeon")){
                binding.manufacturer.setText("AMD");
            }
            else binding.manufacturer.setText("NVIDIA");
            binding.selectButton.setText("저장");
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private List<GpuListActivity.GPU> list;

        public ListAdapter(List<GpuListActivity.GPU> list) {
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