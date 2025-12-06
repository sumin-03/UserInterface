package com.example.userinterface;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityCheckCompatibilityBinding;

public class CheckCompatibility extends AppCompatActivity {
    private CpuListActivity.CPU myCpu=new CpuListActivity.CPU();
    private GpuListActivity.GPU myGpu=new GpuListActivity.GPU();
    private MainboardListActivity.Mainboard myMainboard=new MainboardListActivity.Mainboard();
    private RamListActivity.Ram myRam=new RamListActivity.Ram();
    private PowerListActivity.Power myPower=new PowerListActivity.Power();
    private CaseListActivity.Case myCase=new CaseListActivity.Case();
    private CoolerListActivity.Cooler myCooler=new CoolerListActivity.Cooler();
    private boolean error=false;
    private boolean waring=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityCheckCompatibilityBinding binding=ActivityCheckCompatibilityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        myCpu=getIntent().getSerializableExtra("my_cpu", CpuListActivity.CPU.class);
        myGpu=getIntent().getSerializableExtra("my_gpu", GpuListActivity.GPU.class);
        myMainboard=getIntent().getSerializableExtra("my_mainboard", MainboardListActivity.Mainboard.class);
        myRam=getIntent().getSerializableExtra("my_ram", RamListActivity.Ram.class);
        myPower=getIntent().getSerializableExtra("my_power", PowerListActivity.Power.class);
        myCase=getIntent().getSerializableExtra("my_case", CaseListActivity.Case.class);
        myCooler=getIntent().getSerializableExtra("my_cooler", CoolerListActivity.Cooler.class);
        int count=getIntent().getIntExtra("ram_count",1);

        binding.detailContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.detailContent.setVisibility(VISIBLE);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnGoToMypc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(myCpu.getMemVer().contains(myRam.getRamVer())){}
        else{
            error=true;
            binding.cpuRamVer.setVisibility(VISIBLE);
            binding.cpuRamVerContent.setText("현재 cpu 메모리 버전 : "+myCpu.getMemVer()+"\n현재 RAM 메모리 버전 : "+myRam.getRamVer());
        }

        if(myCpu.getMemVer().contains(myMainboard.getMemVer())){}
        else{
            error=true;
            binding.cpuBoardMem.setVisibility(VISIBLE);
            binding.cpuBoardMemContent.setText("현재 cpu 메모리 버전 : "+myCpu.getMemVer()+"\n현재 메인보드 메모리 버전 : "+myMainboard.getMemVer());
        }

        if(myMainboard.getMemVer().equals(myRam.getRamVer())){}
        else {
            error=true;
            binding.ramBoardMem.setVisibility(VISIBLE);
            binding.ramBoardMemContent.setText("현재 메인보드 메모리 버전 : "+myMainboard.getMemVer()+"\n현재 메모리 버전 : "+myRam.getRamVer());
        }

        if(myMainboard.getSocket().equals(myCpu.getSocket())){}
        else {
            error=true;
            binding.cpuBoardSocket.setVisibility(VISIBLE);
            binding.cpuBoardSocketContent.setText("현재 cpu 소켓 : "+myCpu.getSocket()+"\n현재 메인보드 소켓 : "+myMainboard.getSocket());
        }

        if(myCase.getBoardSize().contains(myMainboard.getSize())){}
        else {
            binding.caseBoardSize.setVisibility(VISIBLE);
            binding.caseBoardSizeContent.setText("현재 케이스가 호환하는 크기 : "+myCase.getBoardSize()+"\n현재 메인보드 크기 : "+myMainboard.getSize());
        }

        if(Double.parseDouble(myMainboard.getMemMax())>=(Double.parseDouble(myRam.getSize())*count)){}
        else{
            error=true;
            binding.memMax.setVisibility(VISIBLE);
            binding.memMaxContent.setText("현재 메인보드의 최대 메모리 용량 : "+myMainboard.getMemMax()+"GB"
                    +"\n현재 메모리 총 용량 : "+(Integer.parseInt(myRam.getSize())*count)+"GB");
        }

        if(Integer.parseInt(myMainboard.getMemSlot())>=count){}
        else {
            error=true;
            binding.memSlot.setVisibility(VISIBLE);
            binding.memSlotContent.setText("현재 메인보드 메모리 최대 슬롯 수 : "+myMainboard.getMemSlot()+"\n현재 메모리 개수 : "+count);
        }

        if(Integer.parseInt(myGpu.getPower())<=Integer.parseInt(myPower.getPower())){}
        else{
            error=true;
            binding.power.setVisibility(VISIBLE);
            binding.powerContent.setText("권장 파워 : "+myGpu.getPower()+"\n현재 파워 : "+myPower.getPower());
        }

        if(myCooler.getKind().equals("air")){
            if(Double.parseDouble(myCooler.getSize())<=Double.parseDouble(myCase.getCoolerSize())){}
            else{
                error=true;
                binding.coolerCase.setVisibility(VISIBLE);
                binding.coolerCaseContent.setText("현재 케이스가 지원하는 최대 공랭쿨러 크기 : "+myCase.getCoolerSize()+"mm"
                        +"\n현재 쿨러 크기 : "+myCooler.getSize()+"mm");
            }}
        else{
            if(Double.parseDouble(myCooler.getSize())<=Double.parseDouble(myCase.getGpuSize())){}
            else{
                error=true;
                binding.coolerCase.setVisibility(VISIBLE);
                binding.coolerCaseContent.setText("현재 케이스가 지원하는 최대 수랭쿨러 크기 : "+myCase.getCoolerSize()+"mm"
                        +"\n현재 쿨러 크기 : "+myCooler.getSize()+"mm");
            }}

        if(!myCase.getAtxPower().equals("FALSE")){}
        else{
            error=true;
            binding.casePower.setVisibility(VISIBLE);
            binding.casePowerContent.setText("현재 케이스는 ATX크기의 파워를 지원하지 않습니다.\n현재 앱은 ATX크기의 파워만 지원합니다");
        }

        if(Integer.parseInt(myMainboard.getPcieVer())<Integer.parseInt(myGpu.getPcie())){
            waring=true;
            binding.gpuPcie.setVisibility(VISIBLE);
        }
        if(Double.parseDouble(myCase.getGpuSize())<300){
            waring=true;
            binding.caseGpu.setVisibility(VISIBLE);
        }
        if(error){
            binding.result.setText("이상 발견!\n현재 부품으로는 조립할 수 없거나\n조립 시 치명적인 문제가 발생합니다");
        }
        else if(waring){
            binding.result.setText("주의!\n현재 부품으로 조립 시 주의해야 할 부분이 있습니다\n그 이외에는 정상 작동");
        }
    }
}