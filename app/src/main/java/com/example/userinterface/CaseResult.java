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

import com.example.userinterface.databinding.ActivityCaseResultBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.util.ArrayList;
import java.util.List;

public class CaseResult extends AppCompatActivity {
    private List<MainboardListActivity.Mainboard> mainboardList=new ArrayList<>();
    private List<CoolerListActivity.Cooler> coolerList=new ArrayList<>();
    private List<CaseListActivity.Case> caseList=new ArrayList<>();
    private List<CaseListActivity.Case> filtered=new ArrayList<>();
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityCaseResultBinding binding=ActivityCaseResultBinding.inflate(getLayoutInflater());
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
        binding.btnGotoFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ListLoad.loadMainboardDataFromAssets(this, mainboardList);
        ListLoad.loadCoolerDataFromAssets(this, coolerList);
        ListLoad.loadCaseDataFromAssets(this, caseList);
        Bundle bundle=getIntent().getBundleExtra("condition_for_case");
        String boardName=bundle.getString("selected_mainboard");
        String coolerName=bundle.getString("selected_cooler");
        MainboardListActivity.Mainboard mainboard=mainboardList.stream()
                .filter(m -> m.getName().equals(boardName))
                .findFirst()
                .orElse(null);
        CoolerListActivity.Cooler cooler=coolerList.stream()
                .filter(c -> c.getName().equals(coolerName))
                .findFirst()
                .orElse(null);
        String boardSize=mainboard.getSize();
        double coolerSize=Double.parseDouble(cooler.getSize());
        if(mainboard.getSize().equals("ATX")) {
            for (CaseListActivity.Case cases : caseList) {
                if(cases.getSize().contains("big"))
                    filtered.add(cases);
            }
        } else if (mainboard.getSize().equals("M-ATX")) {
            for (CaseListActivity.Case cases : caseList) {
                if(cases.getSize().contains("middle"))
                    filtered.add(cases);
            }
        }
        if(cooler.getKind().equals("air")) {
            filtered.removeIf(c -> {
                try {
                    return Double.parseDouble(c.getCoolerSize()) <= coolerSize;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
        }
        else{
            filtered.removeIf(c -> {
                try {
                    return Double.parseDouble(c.getGpuSize()) <= coolerSize;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
        }
        binding.main.setLayoutManager(new LinearLayoutManager(this));
        binding.main.setAdapter(new ListAdapter(filtered));
    }
    private class ListViewHolder extends RecyclerView.ViewHolder{
        ListCardBinding binding;
        public ListViewHolder(@NonNull ListCardBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            binding.selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPc.updateBox(currentUser.getUid(), binding.name.getText().toString());
                    Toast.makeText(CaseResult.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
        public void bind(CaseListActivity.Case cases){
            binding.name.setText(cases.getName());
            binding.manufacturer.setText(cases.getManufacturer());
            binding.selectButton.setText("저장");
        }
    }
    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder>{
        private List<CaseListActivity.Case> list;
        public ListAdapter(List<CaseListActivity.Case> list){
            this.list=list;
        }
        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListCardBinding binding=ListCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,
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