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

import com.example.userinterface.databinding.ActivityCpuResultBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.util.ArrayList;
import java.util.List;

public class CpuResult extends AppCompatActivity {
    private User currentUser;
    private List<CpuListActivity.CPU> cpuList=new ArrayList<>();
    private List<GpuListActivity.GPU> gpuList=new ArrayList<>();
    private List<CpuListActivity.CPU> filtered=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityCpuResultBinding binding=ActivityCpuResultBinding.inflate(getLayoutInflater());
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
        GpuListActivity.GPU gpu=gpuList.stream()
                .filter(g -> g.getName().equals(getIntent().getStringExtra("selected_gpu")))
                .findFirst()
                .orElse(null);
        String grade= null;
        if (gpu != null) {
            grade = gpu.getGrade();
        }
        else grade="3";
        filtered.clear();
        switch(grade) {
            case "0":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("0")) {
                        filtered.add(cpu);
                    }
                }
                break;
            case "1":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("0")||cpu.getGrade().equals("1")) {
                        filtered.add(cpu);
                    }
                }
                break;
            case "2":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("0")||cpu.getGrade().equals("1")) {
                        filtered.add(cpu);
                    }
                }
                break;
            case "3":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("1")) {
                        filtered.add(cpu);
                    }
                }
                break;
            case "4":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("1")||cpu.getGrade().equals("2")) {
                        filtered.add(cpu);
                    }
                }
                break;
            case "5":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("2")||cpu.getGrade().equals("3")) {
                        filtered.add(cpu);
                    }
                }
                break;
            case "6":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("3")||cpu.getGrade().equals("4")) {
                        filtered.add(cpu);
                    }
                }
                break;
            case "7":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("3")||cpu.getGrade().equals("4")||cpu.getGrade().equals("5")) {
                        filtered.add(cpu);
                    }
                }
                break;
            case "8":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("4")||cpu.getGrade().equals("5")) {
                        filtered.add(cpu);
                    }
                }
                break;
            case "9":
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (cpu.getGrade().equals("5")) {
                        filtered.add(cpu);
                    }
                }
                break;
            default:
                for (CpuListActivity.CPU cpu : cpuList) {
                    if (!cpu.getGrade().equals("0")&&!cpu.getGrade().equals("1")&&!cpu.getGrade().equals("2")&&!cpu.getGrade().equals("3")&&!cpu.getGrade().equals("4")) {
                        filtered.add(cpu);
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
                    MyPc.updateCpu(currentUser.getUid(), binding.name.getText().toString());
                    Toast.makeText(CpuResult.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void bind(CpuListActivity.CPU cpu) {
            binding.name.setText(cpu.getName());
            binding.manufacturer.setText(cpu.getManufacturer());
            binding.selectButton.setText("저장");
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private List<CpuListActivity.CPU> list;

        public ListAdapter(List<CpuListActivity.CPU> list) {
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