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

import com.example.userinterface.databinding.ActivityCoolerListBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoolerListActivity extends AppCompatActivity {
    private List<Cooler> coolerList = new ArrayList<>();
    private List<Cooler> filteredList=new ArrayList<>();
    private ListAdapter adapter;

    public static class Cooler implements Serializable {
        private String name;
        private String manufacturer;
        private String kind;
        private String size;
        private String cpuGrade;
        private String socket;
        public Cooler(){}

        public Cooler(String name, String manufacturer, String kind, String size, String cpuGrade, String socket) {
            this.name = name;
            this.manufacturer=manufacturer;
            this.kind=kind;
            this.size=size;
            this.cpuGrade=cpuGrade;
            this.socket=socket;
        }

        public String getName() { return name; }
        public String getManufacturer() {return manufacturer;}
        public String getKind(){return kind;}
        public String getSize(){return size;}
        public String getCpuGrade(){return cpuGrade;}
        public String getSocket(){return socket;}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityCoolerListBinding binding=ActivityCoolerListBinding.inflate(getLayoutInflater());
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
        ListLoad.loadCoolerDataFromAssets(this, coolerList);
        filteredList.addAll(coolerList);

        adapter = new ListAdapter(filteredList);
        binding.main.setLayoutManager(new LinearLayoutManager(this));
        binding.main.setAdapter(adapter);

        setupSearch(binding);
    }
    private void setupSearch(ActivityCoolerListBinding binding) {
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
            filteredList.addAll(coolerList);
        } else {
            for (Cooler cooler : coolerList) {
                if (cooler.getName().contains(name)){
                    filteredList.add(cooler);
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
                    Cooler selectedCooler=coolerList.stream()
                            .filter(cl -> cl.getName().equals(name))
                            .findFirst()
                            .orElse(null);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("cooler_selected", name);

                    setResult(1004, resultIntent);
                    finish();
                }
            });
        }

        private void bind(Cooler cooler) {
            binding.name.setText(cooler.getName());
            binding.manufacturer.setText(cooler.getManufacturer()+" / 쿨링 방식 : "+cooler.getKind());
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private List<Cooler> list;

        public ListAdapter(List<Cooler> list) {
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