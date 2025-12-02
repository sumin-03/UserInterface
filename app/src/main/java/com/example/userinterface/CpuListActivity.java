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

import com.example.userinterface.databinding.ActivityCpuListBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CpuListActivity extends AppCompatActivity {
    private List<CPU> cpuList = new ArrayList<>();
    private List<CPU> filteredList=new ArrayList<>();
    private ListAdapter adapter;

    public static class CPU implements Serializable {
        private String name;
        private String manufacturer;
        private String socket;
        private String memVer;
        private String pcie;
        private String grade;

        public CPU(){}

        public CPU(String name, String manufacturer, String socket, String memVer, String pcie, String grade) {
            this.name = name;
            this.manufacturer = manufacturer;
            this.socket=socket;
            this.memVer=memVer;
            this.pcie = pcie;
            this.grade = grade;
        }

        public String getName() { return name; }
        public String getManufacturer() { return manufacturer; }
        public String getSocket(){return socket;}
        public String getMemVer(){return memVer;}
        public String getPcie() { return pcie; }
        public String getGrade() { return grade; }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityCpuListBinding binding=ActivityCpuListBinding.inflate(getLayoutInflater());
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
        ListLoad.loadCpuDataFromAssets(this, cpuList);
        filteredList.addAll(cpuList);

        adapter = new ListAdapter(filteredList);
        binding.main.setLayoutManager(new LinearLayoutManager(this));
        binding.main.setAdapter(adapter);

        setupSearch(binding);
    }

    private void setupSearch(ActivityCpuListBinding binding) {
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
            filteredList.addAll(cpuList);
        } else {
            for (CPU cpu : cpuList) {
                if (cpu.getName().contains(name)){
                    filteredList.add(cpu);
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
                    CPU selectedCpu= cpuList.stream()
                            .filter(g -> g.getName().equals(name))
                            .findFirst()
                            .orElse(null);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("cpu_selected", name);

                    setResult(1000, resultIntent);
                    finish();
                }
            });
        }

        private void bind(CPU cpu) {
            binding.name.setText(cpu.getName());
            binding.manufacturer.setText(cpu.getManufacturer());
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private List<CPU> list;

        public ListAdapter(List<CPU> list) {
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
