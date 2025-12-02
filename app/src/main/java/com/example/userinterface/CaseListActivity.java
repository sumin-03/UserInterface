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

import com.example.userinterface.databinding.ActivityCaseListBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CaseListActivity extends AppCompatActivity {
    private List<Case> caseList = new ArrayList<>();
    private List<Case> filteredList=new ArrayList<>();
    private ListAdapter adapter;

    public static class Case implements Serializable {
        private String name;
        private String manufacturer;
        private String boardSize;
        private String coolerSize;
        private String gpuSize;
        private String powerSize;
        private String size;
        private String atxPower;
        public Case(){}

        public Case(String name, String manufacturer, String boardSize, String coolerSize, String gpuSize, String powerSize, String size, String atxPower) {
            this.name = name;
            this.manufacturer=manufacturer;
            this.boardSize=boardSize;
            this.coolerSize=coolerSize;
            this.gpuSize=gpuSize;
            this.powerSize=powerSize;
            this.size=size;
            this.atxPower=atxPower;
        }

        public String getName() { return name; }
        public String getManufacturer() {return manufacturer;}
        public String getBoardSize(){return boardSize;}
        public String getCoolerSize() {return coolerSize;}
        public String getGpuSize(){return gpuSize;}
        public String getPowerSize(){return powerSize;}
        public String getSize(){return size;}
        public String getAtxPower(){return atxPower;}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityCaseListBinding binding=ActivityCaseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ListLoad.loadCaseDataFromAssets(this, caseList);
        filteredList.addAll(caseList);

        adapter = new ListAdapter(filteredList);
        binding.main.setLayoutManager(new LinearLayoutManager(this));
        binding.main.setAdapter(adapter);

        setupSearch(binding);
    }
    private void setupSearch(ActivityCaseListBinding binding) {
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
            filteredList.addAll(caseList);
        } else {
            for (Case casee : caseList) {//case는 사용 불가
                if (casee.getName().contains(name)){
                    filteredList.add(casee);
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
                    Case selectedCase=caseList.stream()
                            .filter(c -> c.getName().equals(name))
                            .findFirst()
                            .orElse(null);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("case_selected", name);

                    setResult(1005, resultIntent);
                    finish();
                }
            });
        }

        private void bind(Case casee) {
            binding.name.setText(casee.getName());
            binding.manufacturer.setText(casee.getManufacturer());
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {
        private List<Case> list;

        public ListAdapter(List<Case> list) {
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