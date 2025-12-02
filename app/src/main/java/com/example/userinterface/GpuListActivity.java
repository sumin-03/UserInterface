package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface.databinding.ActivityGpuListBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GpuListActivity extends AppCompatActivity {
    private List<GPU> gpuList = new ArrayList<>();
    private List<GPU> filteredList=new ArrayList<>();
    private ListAdapter adapter;

    public static class GPU implements Serializable {
        private String name;
        private String power;
        private String pcie;
        private String grade;

        public GPU(){}

        public GPU(String name, String power, String pcie, String grade) {
            this.name = name;
            this.power = power;
            this.pcie = pcie;
            this.grade = grade;
        }

        public String getName() { return name; }
        public String getPower() { return power; }
        public String getPcie() { return pcie; }
        public String getGrade() { return grade; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityGpuListBinding binding=ActivityGpuListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ListLoad.loadGpuDataFromAssets(this, gpuList);
        filteredList.addAll(gpuList);

        adapter = new ListAdapter(filteredList);
        binding.main.setLayoutManager(new LinearLayoutManager(this));
        binding.main.setAdapter(adapter);

        setupSearch(binding);
    }

    private void setupSearch(ActivityGpuListBinding binding) {
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterList(String name) {
        filteredList.clear();

        if (name.isEmpty()) {
            filteredList.addAll(gpuList);
        } else {
            for (GPU gpu : gpuList) {
                if (gpu.getName().contains(name)){
                    filteredList.add(gpu);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    private class ListViewHolder extends RecyclerView.ViewHolder {
        ListCardBinding binding;

        public ListViewHolder(ListCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name=binding.name.getText().toString();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("gpu_selected", name);

                    setResult(1001, resultIntent);
                    finish();
                }
            });
        }

        private void bind(GPU gpu) {
            binding.name.setText(gpu.getName());
            if(gpu.getName().contains("Radeon")){
                binding.manufacturer.setText("AMD");
            }
            else binding.manufacturer.setText("NVIDIA");
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private List<GPU> list;

        public ListAdapter(List<GPU> list) {
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