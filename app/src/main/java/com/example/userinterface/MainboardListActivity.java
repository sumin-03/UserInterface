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

import com.example.userinterface.databinding.ActivityMainboardListBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainboardListActivity extends AppCompatActivity {
    private List<Mainboard> mainboardList = new ArrayList<>();
    private List<Mainboard> filteredList=new ArrayList<>();
    private ListAdapter adapter;

    public static class Mainboard implements Serializable {
        private String name;
        private String manufacturer;
        private String chipset;
        private String socket;
        private String pcieVer;
        private String size;
        private String memVer;
        private String memMax;
        private String memSlot;

        public Mainboard(){}
        public Mainboard(String name, String manufacturer, String chipset, String socket, String pcieVer, String size, String memVer, String memMax, String memSlot) {
            this.name = name;
            this.manufacturer=manufacturer;
            this.chipset=chipset;
            this.socket=socket;
            this.pcieVer=pcieVer;
            this.size=size;
            this.memVer=memVer;
            this.memMax=memMax;
            this.memSlot=memSlot;
        }

        public String getName() { return name;}
        public String getManufacturer() { return manufacturer; }
        public String getChipset(){return chipset;}
        public String getSocket(){return socket;}
        public String getPcieVer() { return pcieVer;}
        public String getSize() { return size;}
        public String getMemVer(){return memVer;}
        public String getMemMax(){return memMax;}
        public String getMemSlot(){return memSlot;}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityMainboardListBinding binding=ActivityMainboardListBinding.inflate(getLayoutInflater());
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
        ListLoad.loadMainboardDataFromAssets(this, mainboardList);
        filteredList.addAll(mainboardList);

        adapter = new ListAdapter(filteredList);
        binding.main.setLayoutManager(new LinearLayoutManager(this));
        binding.main.setAdapter(adapter);

        setupSearch(binding);
    }
    private void setupSearch(ActivityMainboardListBinding binding) {
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
            filteredList.addAll(mainboardList);
        } else {
            for (Mainboard mainboard : mainboardList) {
                if (mainboard.getName().contains(name)){
                    filteredList.add(mainboard);
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
                    Mainboard selectedMainboard=mainboardList.stream()
                            .filter(m -> m.getName().equals(name))
                            .findFirst()
                            .orElse(null);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("mainboard_selected", name);

                    setResult(1002, resultIntent);
                    finish();
                }
            });
        }

        private void bind(Mainboard mainboard) {
            binding.name.setText(mainboard.getName());
            binding.manufacturer.setText(mainboard.getManufacturer());
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private List<Mainboard> list;

        public ListAdapter(List<Mainboard> list) {
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