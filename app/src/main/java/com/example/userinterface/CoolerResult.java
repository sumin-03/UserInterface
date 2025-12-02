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

import com.example.userinterface.databinding.ActivityCoolerResultBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.util.ArrayList;
import java.util.List;

public class CoolerResult extends AppCompatActivity {
    private User currentUser;
    private List<CpuListActivity.CPU> cpuList=new ArrayList<>();
    private List<CoolerListActivity.Cooler> coolerList=new ArrayList<>();
    private List<CoolerListActivity.Cooler> filtered=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityCoolerResultBinding binding=ActivityCoolerResultBinding.inflate(getLayoutInflater());
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
        ListLoad.loadCoolerDataFromAssets(this, coolerList);
        CpuListActivity.CPU cpu=cpuList.stream()
                .filter(c -> c.getName().equals(getIntent().getStringExtra("selected_cpu")))
                .findFirst()
                .orElse(null);
        int cpuGrade=Integer.parseInt(cpu.getGrade());
        if(cpuGrade>3){
            for(CoolerListActivity.Cooler cooler : coolerList)
                if(cooler.getCpuGrade().equals("EL4"))
                    filtered.add(cooler);
        }
        else if(cpuGrade>1){
            for(CoolerListActivity.Cooler cooler : coolerList)
                if(cooler.getCpuGrade().equals("EL2"))
                    filtered.add(cooler);
        }
        else {
            for(CoolerListActivity.Cooler cooler : coolerList)
                if(cooler.getCpuGrade().equals("EL0") || cooler.getCpuGrade().equals("EG1"))
                    filtered.add(cooler);
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
    private class ListViewHolder extends RecyclerView.ViewHolder{
        ListCardBinding binding;
        public ListViewHolder(@NonNull ListCardBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            binding.selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPc.updateCooler(currentUser.getUid(), binding.name.getText().toString());
                    Toast.makeText(CoolerResult.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
        public void bind(CoolerListActivity.Cooler cooler){
            binding.name.setText(cooler.getName());
            binding.manufacturer.setText(cooler.getManufacturer()+" / 쿨링 방식 : "+cooler.getKind());
            binding.selectButton.setText("저장");
        }
    }
    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder>{
        private List<CoolerListActivity.Cooler> list=new ArrayList<>();
        public ListAdapter(List<CoolerListActivity.Cooler> list){
            this.list=list;
        }
        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListCardBinding binding=ListCardBinding.inflate(
                    LayoutInflater.from(parent.getContext())
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