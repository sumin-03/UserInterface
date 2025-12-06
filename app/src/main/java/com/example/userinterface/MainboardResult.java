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

import com.example.userinterface.databinding.ActivityMainboardResultBinding;
import com.example.userinterface.databinding.ListCardBinding;

import java.util.ArrayList;
import java.util.List;

public class MainboardResult extends AppCompatActivity {
    private User currentUser;
    private int count=0;
    private Bundle bundle=new Bundle();
    private List<CpuListActivity.CPU> cpuList=new ArrayList<>();
    private List<MainboardListActivity.Mainboard> mainboardList=new ArrayList<>();
    private List<MainboardListActivity.Mainboard> filtered=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityMainboardResultBinding binding=ActivityMainboardResultBinding.inflate(getLayoutInflater());
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
        bundle=getIntent().getBundleExtra("condition_for_board");
        String cpuName=bundle.getString("selected_cpu");
        String ram=bundle.getString("selected_ram");
        int ramSize = 0;
        for(int i=4; i<65; i=i*2){
            if(ram.contains(""+i)){ ramSize=i; break;}
        }
        String ramVer;
        if(ram.contains("DDR5")){ramVer="DDR5";}
        else if(ram.contains("DDR4")){ramVer="DDR4";}
        else if(ram.contains("DDR3")){ramVer="DDR3";}
        else ramVer="not_selected";
        String size=bundle.getString("selected_size");
        count=bundle.getInt("count");
        ListLoad.loadCpuDataFromAssets(this, cpuList);
        ListLoad.loadMainboardDataFromAssets(this, mainboardList);
        CpuListActivity.CPU cpu=cpuList.stream()
                .filter(c -> c.getName().equals(cpuName))
                .findFirst()
                .orElse(null);
        if(cpu.getMemVer().contains(ramVer)&&size.contains("ATX")){
            for(MainboardListActivity.Mainboard mainboard : mainboardList){
                if(mainboard.getSocket().equals(cpu.getSocket())
                    && cpu.getMemVer().contains(mainboard.getMemVer())
                    && (Integer.parseInt(mainboard.getMemMax()) >= (ramSize * count))
                    && (Integer.parseInt(mainboard.getMemSlot()) >= count)
                    && mainboard.getSize().equals(size)){
                    filtered.add(mainboard);
                }
            }
        }else if(cpu.getMemVer().contains(ramVer)&&!size.contains("ATX")){
            for(MainboardListActivity.Mainboard mainboard : mainboardList){
                if(mainboard.getSocket().equals(cpu.getSocket())
                        && cpu.getMemVer().contains(mainboard.getMemVer())
                        && (Integer.parseInt(mainboard.getMemMax()) >= (ramSize * count))
                        && (Integer.parseInt(mainboard.getMemSlot()) >= count)){
                    filtered.add(mainboard);
                }
            }
        }
        else if(ramVer.equals("not_selected")&&size.contains("ATX")) {
            for(MainboardListActivity.Mainboard mainboard : mainboardList){
                if(mainboard.getSocket().equals(cpu.getSocket())
                        && cpu.getMemVer().contains(mainboard.getMemVer())
                        && (Integer.parseInt(mainboard.getMemSlot()) >= count)
                        && mainboard.getSize().equals(size)){
                    filtered.add(mainboard);
                }
            }
        }else if(ramVer.equals("not_selected")&&!size.contains("ATX")){
            for(MainboardListActivity.Mainboard mainboard : mainboardList){
                if(mainboard.getSocket().equals(cpu.getSocket())
                        && cpu.getMemVer().contains(mainboard.getMemVer())){
                    filtered.add(mainboard);
                }
            }
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
                    MyPc.updateMainboard(currentUser.getUid(), binding.name.getText().toString());
                    Toast.makeText(MainboardResult.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
        public void bind(MainboardListActivity.Mainboard mainboard){
            binding.name.setText(mainboard.getName());
            binding.manufacturer.setText(mainboard.getManufacturer());
            binding.selectButton.setText("저장");
        }
    }
    private class ListAdapter extends RecyclerView.Adapter<ListViewHolder>{
        private List<MainboardListActivity.Mainboard> list;
        public ListAdapter(List<MainboardListActivity.Mainboard> list){
            this.list=list;
        }

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListCardBinding binding=ListCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
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