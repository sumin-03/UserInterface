package com.example.userinterface;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityMyPcBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyPcActivity extends AppCompatActivity {
    private ActivityMyPcBinding binding;
    private User currentUser;
    private List<CpuListActivity.CPU> cpuList=new ArrayList<>();
    private List<GpuListActivity.GPU> gpuList=new ArrayList<>();
    private List<MainboardListActivity.Mainboard> mainboardList=new ArrayList<>();
    private List<PowerListActivity.Power> powerList=new ArrayList<>();
    private List<CaseListActivity.Case> caseList=new ArrayList<>();
    private List<CoolerListActivity.Cooler> coolerList=new ArrayList<>();
    private CpuListActivity.CPU myCpu=new CpuListActivity.CPU();
    private GpuListActivity.GPU myGpu=new GpuListActivity.GPU();
    private MainboardListActivity.Mainboard myMainboard=new MainboardListActivity.Mainboard();
    private RamListActivity.Ram myRam=new RamListActivity.Ram();
    private PowerListActivity.Power myPower=new PowerListActivity.Power();
    private CaseListActivity.Case myCase=new CaseListActivity.Case();
    private CoolerListActivity.Cooler myCooler=new CoolerListActivity.Cooler();
    private String myStorage;
    private MyPc myPc;
    private int count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMyPcBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        currentUser = getIntent().getSerializableExtra("USER_PROFILE", User.class);

        ActivityResultLauncher<Intent> launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode()==1000){
                    MyPc.updateCpu(currentUser.getUid(),o.getData().getStringExtra("cpu_selected"));
                }
                else if(o.getResultCode()==1001){
                    MyPc.updateGpu(currentUser.getUid(),o.getData().getStringExtra("gpu_selected"));}
                else if(o.getResultCode()==1002){
                    MyPc.updateMainboard(currentUser.getUid(),o.getData().getStringExtra("mainboard_selected"));
                }
                else if(o.getResultCode()==1003){
                    MyPc.updatePower(currentUser.getUid(),o.getData().getStringExtra("power_selected"));
                }
                else if(o.getResultCode()==1004){
                    MyPc.updateCooler(currentUser.getUid(),o.getData().getStringExtra("cooler_selected"));
                }
                else if(o.getResultCode()==1005){
                    MyPc.updateBox(currentUser.getUid(),o.getData().getStringExtra("case_selected"));
                }
                else if(o.getResultCode()==1006){
                    MyPc.updateRam(currentUser.getUid(),o.getData().getStringExtra("ram_selected"));
                }
                else if(o.getResultCode()==1007){
                    MyPc.updateStorage(currentUser.getUid(),o.getData().getStringExtra("storage_selected"));
                }
            }
        });
        ListLoad.loadCpuDataFromAssets(this, cpuList);
        ListLoad.loadGpuDataFromAssets(this, gpuList);
        ListLoad.loadMainboardDataFromAssets(this, mainboardList);
        ListLoad.loadPowerDataFromAssets(this, powerList);
        ListLoad.loadCaseDataFromAssets(this, caseList);
        ListLoad.loadCoolerDataFromAssets(this, coolerList);

        MyPc.listen(currentUser.getUid(), new MyPc.OnMyPcLoadedListener() {
            @Override
            public void onLoaded(@Nullable MyPc pc) {
                if (pc == null) return;

                myPc = pc;
                myCpu=cpuList.stream()
                        .filter(cpu -> cpu.getName().equals(myPc.getCpu()))
                        .findFirst()
                        .orElse(null);

                myGpu=gpuList.stream()
                        .filter(gpu -> gpu.getName().equals(myPc.getGpu()))
                        .findFirst()
                        .orElse(null);

                myMainboard=mainboardList.stream()
                        .filter(m -> m.getName().equals(myPc.getMainboard()))
                        .findFirst()
                        .orElse(null);

                if(myPc.getRam().contains("나만의")){
                    myRam=new RamListActivity.Ram("나만의 RAM", "");
                }
                else{
                    IntStream find=myPc.getRam().chars();
                    String ramSize=find.filter((ch)-> (48 <= ch && ch <= 57))
                            .mapToObj(ch -> (char)ch)
                            .map(Object::toString)
                            .collect(Collectors.joining());
                    ramSize=ramSize.substring(1);
                    if(myPc.getRam().contains("DDR3"))
                        myRam=new RamListActivity.Ram("DDR3", ramSize);
                    else if(myPc.getRam().contains("DDR4"))
                        myRam=new RamListActivity.Ram("DDR4", ramSize);
                    else if(myPc.getRam().contains("DDR5"))
                        myRam=new RamListActivity.Ram("DDR5", ramSize);
                    else myRam=null;
                }

                myPower=powerList.stream()
                        .filter(p -> p.getName().equals(myPc.getPower()))
                        .findFirst()
                        .orElse(null);

                myCase=caseList.stream()
                        .filter(box -> box.getName().equals(myPc.getBox()))
                        .findFirst()
                        .orElse(null);
                myCooler = coolerList.stream()
                        .filter(c -> c.getName().equals(myPc.getCooler()))
                        .findFirst()
                        .orElse(null);

                myStorage=myPc.getStorage();
                if(myStorage.contains("삼성"))
                    binding.manufacturerStorage.setText("samsung");
                else if(myStorage.contains("SK"))
                    binding.manufacturerStorage.setText("SK 하이닉스");

                if(myPc.getCpu().contains("나만의"))
                    binding.nameCpu.setText(myPc.getCpu());
                else{
                    binding.nameCpu.setText(myCpu.getName());
                    binding.manufacturerCpu.setText(myCpu.getManufacturer());}
                if(myPc.getGpu().contains("나만의"))
                    binding.nameGpu.setText(myPc.getGpu());
                else {
                    binding.nameGpu.setText(myGpu.getName());
                    if(myGpu.getName().contains("Radeon"))
                        binding.manufacturerGpu.setText("AMD");
                    else binding.manufacturerGpu.setText("NVIDIA");}
                if(myPc.getMainboard().contains("나만의"))
                    binding.nameMainboard.setText(myPc.getMainboard());
                else {
                    binding.nameMainboard.setText(myMainboard.getName());
                    binding.manufacturerMainboard.setText(myMainboard.getManufacturer());}
                if(myPc.getRam().contains("나만의"))
                    binding.nameRam.setText(myPc.getRam());
                else{
                    binding.nameRam.setText(myRam.getRamVer()+" "+myRam.getSize()+"GB");
                    binding.manufacturerRam.setText(myRam.getRamVer());}
                if(myPc.getPower().contains("나만의"))
                    binding.namePower.setText(myPc.getPower());
                else{
                    binding.namePower.setText(myPower.getName());
                    binding.manufacturerPower.setText(myPower.getManufacturer());}
                if(myPc.getBox().contains("나만의"))
                    binding.nameCase.setText(myPc.getBox());
                else{
                    binding.nameCase.setText(myCase.getName());
                    binding.manufacturerCase.setText(myCase.getManufacturer());}
                if(myPc.getCooler().contains("나만의"))
                    binding.nameCooler.setText(myPc.getCooler());
                else{
                    binding.nameCooler.setText(myCooler.getName());
                    binding.manufacturerCooler.setText(myCooler.getManufacturer());}
                    binding.nameStorage.setText(myStorage);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "실시간 MyPc 오류", e);
            }
        });

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count<4){count++;}
                else Toast.makeText(MyPcActivity.this,"유효범위를 초과했습니다", Toast.LENGTH_SHORT).show();
                binding.count.setText(""+count);
            }
        });
        binding.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count>1){count--;}
                else Toast.makeText(MyPcActivity.this,"유효범위를 초과했습니다", Toast.LENGTH_SHORT).show();
                binding.count.setText(""+count);
            }
        });


        binding.myCpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MyPcActivity.this, CpuListActivity.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.myGpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MyPcActivity.this, GpuListActivity.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.myMainboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MyPcActivity.this, MainboardListActivity.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.myRam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MyPcActivity.this, RamListActivity.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.myPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MyPcActivity.this, PowerListActivity.class));
            }
        });
        binding.myCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MyPcActivity.this, CaseListActivity.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.myCooler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MyPcActivity.this, CoolerListActivity.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.myStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MyPcActivity.this, StorageRecommend.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPc.updateParts(currentUser.getUid(), new MyPc("나만의 cpu",
                        "나만의 gpu", "나만의 메인보드",
                        "나만의 RAM", "나만의 power",
                        "나만의 케이스","나만의 쿨러",
                        "나만의 저장소", currentUser.getUid()));
            }
        });
        binding.btnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(MyPcActivity.this, RecommendMain.class)
                        .putExtra("USER_PROFILE", currentUser));
            }
        });
        binding.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myCpu==null||myGpu==null||myMainboard==null||myRam==null||
                        myPower==null||myCase==null||myCooler==null||myStorage.contains("나만의"))
                    Toast.makeText(MyPcActivity.this, "모든 부품을 선택하여 주십시오", Toast.LENGTH_SHORT).show();
                else {
                    launcher.launch(new Intent(MyPcActivity.this, CheckCompatibility.class)
                            .putExtra("my_cpu", myCpu)
                            .putExtra("my_gpu", myGpu)
                            .putExtra("my_mainboard", myMainboard)
                            .putExtra("my_ram", myRam)
                            .putExtra("my_power", myPower)
                            .putExtra("my_case", myCase)
                            .putExtra("my_cooler", myCooler)
                            .putExtra("ram_count", count));
                }
            }
        });
    }
}