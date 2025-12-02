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

import com.example.userinterface.databinding.ActivityRamListBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RamListActivity extends AppCompatActivity {
    private int count=0;
    List<Ram> ramList=new ArrayList<>();
    private List<Ram> filteredList=new ArrayList<>();
    private ListAdapter adapter;
    public static class Ram implements Serializable{
        private String ramVer;
        private String size;

        public Ram(){}
        public Ram(String ramVer, String size){
            this.ramVer=ramVer;
            this.size=size;;
        }
        public String getRamVer(){return ramVer;}
        public String getSize(){return size;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityRamListBinding binding=ActivityRamListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        for(int i=4; i<33; i=i*2){
            Ram ram=new Ram("DDR3", i+"");
            ramList.add(ram);
        }
        for(int i=4; i<65; i=i*2){
            Ram ram=new Ram("DDR4", i+"");
            ramList.add(ram);
        }
        for(int i=8; i<65; i=i*2){
            Ram ram=new Ram("DDR5", i+"");
            ramList.add(ram);
        }
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        filteredList.addAll(ramList);

        adapter = new ListAdapter(filteredList);
        binding.main.setLayoutManager(new LinearLayoutManager(this));
        binding.main.setAdapter(adapter);

        setupSearch(binding);
    }

    private void setupSearch(ActivityRamListBinding binding) {
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
            filteredList.addAll(ramList);
        } else {
            for (Ram ram : ramList) {
                if (ram.getRamVer().contains(name)){
                    filteredList.add(ram);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        ListCardBinding binding;
        public ListViewHolder(@NonNull ListCardBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            binding.selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name=binding.name.getText().toString();
                    Intent resultIntent=new Intent();
                    resultIntent.putExtra("ram_selected",name);
                    setResult(1006,resultIntent);
                    finish();
                }
            });
        }
        public void bind(Ram ram){
            binding.name.setText(ram.getRamVer()+" "+ram.getSize()+"GB");
            binding.manufacturer.setText(ram.getRamVer());
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder>{
        private List<Ram> list;
        public ListAdapter(List<Ram> list) {
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