package com.example.userinterface;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // (추가)
import android.widget.Toast; // (추가)

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface.databinding.ActivityPartsSearchMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore; // (추가)
import com.google.firebase.firestore.QueryDocumentSnapshot; // (추가)
import com.google.gson.Gson; // (추가)

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PartsSearchMain extends AppCompatActivity implements PartsSearchAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private PartsSearchAdapter cpuAdapter, gpuAdapter, mbAdapter, ramAdapter, powerAdapter, ssdAdapter, hddAdapter, coolerAdapter, caseAdapter;
    private List<ItemModel> cpuNameList = new ArrayList<>();
    private List<ItemModel> gpuNameList = new ArrayList<>();
    private List<ItemModel> mbNameList = new ArrayList<>();
    private List<ItemModel> ramNameList = new ArrayList<>();
    private List<ItemModel> powerNameList = new ArrayList<>();
    private List<ItemModel> ssdNameList = new ArrayList<>();
    private List<ItemModel> hddNameList = new ArrayList<>();
    private List<ItemModel> coolerNameList = new ArrayList<>();
    private List<ItemModel> caseNameList = new ArrayList<>();
    private User currentUser;

    private int currentTabPosition;

    private ActivityPartsSearchMainBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("KSM", "OnCreate()");
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityPartsSearchMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = getIntent().getSerializableExtra("USER_PROFILE", User.class);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = binding.partsRecyclerView;
        searchView = binding.searchView;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                switch (currentTabPosition) {
                    case 0: if (cpuAdapter != null) { cpuAdapter.getFilter().filter(newText); } break;
                    case 1: if (gpuAdapter != null) { gpuAdapter.getFilter().filter(newText); } break;
                    case 2: if (mbAdapter != null) { mbAdapter.getFilter().filter(newText); } break;
                    case 3: if (ramAdapter != null) { ramAdapter.getFilter().filter(newText); } break;
                    case 4: if (powerAdapter != null) { powerAdapter.getFilter().filter(newText); } break;
                    case 5: if (ssdAdapter != null) { ssdAdapter.getFilter().filter(newText); } break;
                    case 6: if (hddAdapter != null) { hddAdapter.getFilter().filter(newText); } break;
                    case 7: if (coolerAdapter != null) { coolerAdapter.getFilter().filter(newText); } break;
                    case 8: if (caseAdapter != null) { caseAdapter.getFilter().filter(newText); } break;
                }
                return true;
            }
        });
        Log.d("KSM", "onQueryTextChange() complete");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("KSM", "recyclerView.setLayoutManager(new LinearLayoutManager(this)); complete");

        setupTabListener();
        setupAddButtonListener();
        Log.d("KSM", "setupTabListener()");

        if (savedInstanceState == null) {
            Log.d("KSM", "savedInstanceState == null");
            loadCpuData();
            Log.d("KSM", "loadCpuData()");
        }

        binding.toolbar.setOnClickListener(v -> { // 위에 toolbar 클릭시 홈으로
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
        });

        binding.bottomNavigation.setSelectedItemId(R.id.navigation_search); //내비게이션 부품 검색으로 설정
        setupNavigationListeners(); //내비게이션 바
    }

    private void setupNavigationListeners() { //밑에 내비게이션 바 리스너
        binding.bottomNavigation.setOnItemSelectedListener(Item -> {
            int itemId = Item.getItemId();
            Fragment selectedFragment = null;
            User user = getIntent().getSerializableExtra("USER_PROFILE", User.class);

            if(itemId == R.id.navigation_home){
                Log.d("KSM", "MoveHome");
                Intent intent = new Intent(PartsSearchMain.this, HomeActivity.class);
                intent.putExtra("USER_PROFILE", user);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(this, 0, 0);
                startActivity(intent, options.toBundle());
                return true;
            }
            else if (itemId == R.id.navigation_search) {
                Log.d("KSM", "STAY");
                return true;
            }
            else if (itemId == R.id.navigation_guide) {
                Log.d("MOVE", "MoveGuide");
                selectedFragment = GuideMenuFragment.newInstance(currentUser);
            }
            else if (itemId == R.id.navigation_recommended_builds) {
                Log.d("MOVE", "goRecommendBuild");
                Intent intent = new Intent(PartsSearchMain.this, RecommendQuotationHomeActivity.class);
                intent.putExtra("USER_PROFILE", currentUser); // 유저 정보 넘겨주기
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.navigation_community) {
                Log.d("MOVE", "CommunityMainFragment");
                selectedFragment = CommunityMainFragment.newInstance(currentUser);
            }
            else if (itemId == R.id.navigation_profile) {
                Log.d("MOVE", "MoveProfile");
                selectedFragment = MyPageFragment.newInstance(currentUser);
            }
            if (selectedFragment != null) { // null 이 아니면 탭 선택 변경
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }
    private void setupAddButtonListener() {
        binding.addPartButton.setOnClickListener(v -> {
            Fragment fragmentToAdd;
            String backStackName;
            long userLevel = currentUser.getLevel();
            Log.d("KSM", "UserLevel is over 10");
            if(userLevel >= 10) {
                switch (currentTabPosition) {
                    case 0:
                        fragmentToAdd = new AddCpuFragment();
                        backStackName = "add_cpu";
                        break;
                    case 1:
                        fragmentToAdd = new AddGpuFragment(); // 나중에 만들 프래그먼트
                        backStackName = "add_gpu";
                        break;
                    case 2:
                        fragmentToAdd = new AddMainboardFragment(); // 나중에 만들 프래그먼트
                        backStackName = "add_mainboard";
                        break;
                    case 3:
                        fragmentToAdd = new AddRamFragment(); // 나중에 만들 프래그먼트
                        backStackName = "add_ram";
                        break;
                    case 4:
                        fragmentToAdd = new AddPowerFragment(); // 나중에 만들 프래그먼트
                        backStackName = "add_power";
                        break;
                    case 5:
                        fragmentToAdd = new AddSsdFragment(); // 나중에 만들 프래그먼트
                        backStackName = "add_ssd";
                        break;
                    case 6:
                        fragmentToAdd = new AddHddFragment(); // 나중에 만들 프래그먼트
                        backStackName = "add_hdd";
                        break;
                    case 7:
                        fragmentToAdd = new AddCoolerFragment(); // 나중에 만들 프래그먼트
                        backStackName = "add_cooler";
                        break;
                    case 8:
                        fragmentToAdd = new AddCaseFragment(); // 나중에 만들 프래그먼트
                        backStackName = "add_case";
                        break;
                    default:
                        Toast.makeText(this, "이 부품의 추가 기능은 아직 준비되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        return; // 프래그먼트를 열지 않고 종료
                }
                // 트랜잭션 실행
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main, fragmentToAdd) // replace 사용
                        .addToBackStack(backStackName)     // back stack에 추가
                        .commit();
            }
            else Toast.makeText(this, "레벨 10부터 가능합니다.", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupTabListener() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                searchView.setQuery("", false);
                searchView.clearFocus();

                switch(position) {
                    case 0: loadCpuData(); break;
                    case 1: loadGpuData(); break;
                    case 2: loadMbData(); break;
                    case 3: loadRamData(); break;
                    case 4: loadPowerData(); break;
                    case 5: loadSsdData(); break;
                    case 6: loadHddData(); break;
                    case 7: loadCoolerData(); break;
                    case 8: loadCaseData(); break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    // 1. CPU 로드 (수정 완료)
    private void loadCpuData() {
        currentTabPosition = 0;
        if (!cpuNameList.isEmpty()) {
            if (cpuAdapter == null) {
                cpuAdapter = new PartsSearchAdapter(cpuNameList);
                cpuAdapter.setOnItemClickListener(this);
            }
            recyclerView.setAdapter(cpuAdapter);
            return;
        }

        showLoading(true);
        recyclerView.setAdapter(null);

        db.collection("cpus").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (currentTabPosition != 0) return;
            if (task.isSuccessful()) {
                cpuNameList.clear();
                Gson gson = new Gson();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String manufacturer = document.getString("manufacturer");
                    String jsonString = gson.toJson(document.getData());
                    cpuNameList.add(new ItemModel(name, manufacturer, jsonString));
                }
                cpuAdapter = new PartsSearchAdapter(cpuNameList);
                cpuAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(cpuAdapter);
            } else {
                Log.w("KSM", "Error loading cpus", task.getException());
                Toast.makeText(this, "CPU 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 2. GPU 로드 (★수정됨★)
    private void loadGpuData() {
        currentTabPosition = 1;
        if (!gpuNameList.isEmpty()) {
            if (gpuAdapter == null) {
                gpuAdapter = new PartsSearchAdapter(gpuNameList);
                gpuAdapter.setOnItemClickListener(this);
            }
            recyclerView.setAdapter(gpuAdapter);
            return;
        }

        showLoading(true);
        recyclerView.setAdapter(null);
        db.collection("gpus").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (currentTabPosition != 1) return;
            if (task.isSuccessful()) {
                gpuNameList.clear();
                Gson gson = new Gson();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String manufacturer = document.getString("manufacturer");
                    String jsonString = gson.toJson(document.getData());
                    gpuNameList.add(new ItemModel(name, manufacturer, jsonString));
                }
                gpuAdapter = new PartsSearchAdapter(gpuNameList);
                gpuAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(gpuAdapter);
            } else {
                Log.w("KSM", "Error loading gpus", task.getException());
                Toast.makeText(this, "GPU 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 3. Mainboard 로드 (★수정됨★)
    private void loadMbData() {
        currentTabPosition = 2;
        if (!mbNameList.isEmpty()) {
            if (mbAdapter == null) {
                mbAdapter = new PartsSearchAdapter(mbNameList);
                mbAdapter.setOnItemClickListener(this);
            }
            recyclerView.setAdapter(mbAdapter);
            return;
        }

        showLoading(true);
        recyclerView.setAdapter(null);
        db.collection("mainboards").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (currentTabPosition != 2) return;
            if (task.isSuccessful()) {
                mbNameList.clear();
                Gson gson = new Gson();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String manufacturer = document.getString("manufacturer");
                    String jsonString = gson.toJson(document.getData());
                    mbNameList.add(new ItemModel(name, manufacturer, jsonString));
                }
                mbAdapter = new PartsSearchAdapter(mbNameList);
                mbAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(mbAdapter);
            } else {
                Log.w("KSM", "Error loading mainboards", task.getException());
                Toast.makeText(this, "메인보드 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 4. RAM 로드 (★수정됨★)
    private void loadRamData() {
        currentTabPosition = 3;
        if (!ramNameList.isEmpty()) {
            if (ramAdapter == null) {
                ramAdapter = new PartsSearchAdapter(ramNameList);
                ramAdapter.setOnItemClickListener(this);
            }
            recyclerView.setAdapter(ramAdapter);
            return;
        }

        showLoading(true);
        recyclerView.setAdapter(null);
        db.collection("rams").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (currentTabPosition != 3) return;
            if (task.isSuccessful()) {
                ramNameList.clear();
                Gson gson = new Gson();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String manufacturer = document.getString("manufacturer");
                    String jsonString = gson.toJson(document.getData());
                    ramNameList.add(new ItemModel(name, manufacturer, jsonString));
                }
                ramAdapter = new PartsSearchAdapter(ramNameList);
                ramAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(ramAdapter);
            } else {
                Log.w("KSM", "Error loading rams", task.getException());
                Toast.makeText(this, "RAM 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 5. Power 로드 (★수정됨★)
    private void loadPowerData() {
        currentTabPosition = 4;
        if (!powerNameList.isEmpty()) {
            if (powerAdapter == null) {
                powerAdapter = new PartsSearchAdapter(powerNameList);
                powerAdapter.setOnItemClickListener(this);
            }
            recyclerView.setAdapter(powerAdapter);
            return;
        }

        showLoading(true);
        recyclerView.setAdapter(null);
        db.collection("powers").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (currentTabPosition != 4) return;
            if (task.isSuccessful()) {
                powerNameList.clear();
                Gson gson = new Gson();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String manufacturer = document.getString("manufacturer");
                    String jsonString = gson.toJson(document.getData());
                    powerNameList.add(new ItemModel(name, manufacturer, jsonString));
                }
                powerAdapter = new PartsSearchAdapter(powerNameList);
                powerAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(powerAdapter);
            } else {
                Log.w("KSM", "Error loading powerspecs", task.getException());
                Toast.makeText(this, "파워 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 6. SSD 로드 (★수정됨★)
    private void loadSsdData() {
        currentTabPosition = 5;
        if (!ssdNameList.isEmpty()) {
            if (ssdAdapter == null) {
                ssdAdapter = new PartsSearchAdapter(ssdNameList);
                ssdAdapter.setOnItemClickListener(this);
            }
            recyclerView.setAdapter(ssdAdapter);
            return;
        }

        showLoading(true);
        recyclerView.setAdapter(null);
        db.collection("ssds").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (currentTabPosition != 5) return;
            if (task.isSuccessful()) {
                ssdNameList.clear();
                Gson gson = new Gson();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String manufacturer = document.getString("manufacturer");
                    String jsonString = gson.toJson(document.getData());
                    ssdNameList.add(new ItemModel(name, manufacturer, jsonString));
                }
                ssdAdapter = new PartsSearchAdapter(ssdNameList);
                ssdAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(ssdAdapter);
            } else {
                Log.w("KSM", "Error loading ssds", task.getException());
                Toast.makeText(this, "SSD 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 7. HDD 로드 (★수정됨★)
    private void loadHddData() {
        currentTabPosition = 6;
        if (!hddNameList.isEmpty()) {
            if (hddAdapter == null) {
                hddAdapter = new PartsSearchAdapter(hddNameList);
                hddAdapter.setOnItemClickListener(this);
            }
            recyclerView.setAdapter(hddAdapter);
            return;
        }

        showLoading(true);
        recyclerView.setAdapter(null);
        db.collection("hdds").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (currentTabPosition != 6) return;
            if (task.isSuccessful()) {
                hddNameList.clear();
                Gson gson = new Gson();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String manufacturer = document.getString("manufacturer");
                    String jsonString = gson.toJson(document.getData());
                    hddNameList.add(new ItemModel(name, manufacturer, jsonString));
                }
                hddAdapter = new PartsSearchAdapter(hddNameList);
                hddAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(hddAdapter);
            } else {
                Log.w("KSM", "Error loading hdds", task.getException());
                Toast.makeText(this, "HDD 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 8. Cooler 로드 (★수정됨★)
    private void loadCoolerData() {
        currentTabPosition = 7;
        if (!coolerNameList.isEmpty()) {
            if (coolerAdapter == null) {
                coolerAdapter = new PartsSearchAdapter(coolerNameList);
                coolerAdapter.setOnItemClickListener(this);
            }
            recyclerView.setAdapter(coolerAdapter);
            return;
        }

        showLoading(true);
        recyclerView.setAdapter(null);
        db.collection("coolers").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (currentTabPosition != 7) return;
            if (task.isSuccessful()) {
                coolerNameList.clear();
                Gson gson = new Gson();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String manufacturer = document.getString("manufacturer");
                    String jsonString = gson.toJson(document.getData());
                    coolerNameList.add(new ItemModel(name, manufacturer, jsonString));
                }
                coolerAdapter = new PartsSearchAdapter(coolerNameList);
                coolerAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(coolerAdapter);
            } else {
                Log.w("KSM", "Error loading coolers", task.getException());
                Toast.makeText(this, "쿨러 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 9. Case 로드 (★수정됨★)
    private void loadCaseData() {
        currentTabPosition = 8;
        if (!caseNameList.isEmpty()) {
            if (caseAdapter == null) {
                caseAdapter = new PartsSearchAdapter(caseNameList);
                caseAdapter.setOnItemClickListener(this);
            }
            recyclerView.setAdapter(caseAdapter);
            return;
        }

        showLoading(true);
        recyclerView.setAdapter(null);
        db.collection("cases").get().addOnCompleteListener(task -> {
            showLoading(false);
            if (currentTabPosition != 8) return;
            if (task.isSuccessful()) {
                caseNameList.clear();
                Gson gson = new Gson();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String manufacturer = document.getString("manufacturer");
                    String jsonString = gson.toJson(document.getData());
                    caseNameList.add(new ItemModel(name, manufacturer, jsonString));
                }
                caseAdapter = new PartsSearchAdapter(caseNameList);
                caseAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(caseAdapter);
            } else {
                Log.w("KSM", "Error loading cases", task.getException());
                Toast.makeText(this, "케이스 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // (수정) --- onItemClick ---
    // 이 코드는 변경할 필요가 없습니다.
    @Override
    public void onItemClick(int position) {
        ItemModel clickedItem;
        Intent intent;
        switch(currentTabPosition) {
            case 0:
                clickedItem = cpuAdapter.getItem(position);
                if (clickedItem == null) return;
                intent = new Intent(PartsSearchMain.this, PartsSearchCPUspec.class);
                intent.putExtra("ITEM_TITLE", clickedItem.getTitle());
                intent.putExtra("ITEM_DESCRIPTION", clickedItem.getDescription());
                intent.putExtra("JSON_STRING", clickedItem.getJsonString());
                Log.d("KSM", "onItemClick - currentTabPosition: 0");
                startActivity(intent);
                break;
            case 1:
                clickedItem = gpuAdapter.getItem(position);
                if (clickedItem == null) return;
                intent = new Intent(PartsSearchMain.this, PartsSearchGPUspec.class);
                intent.putExtra("ITEM_TITLE", clickedItem.getTitle());
                intent.putExtra("ITEM_DESCRIPTION", clickedItem.getDescription());
                intent.putExtra("JSON_STRING", clickedItem.getJsonString());
                Log.d("KSM", "onItemClick - currentTabPosition: 1");
                startActivity(intent);
                break;
            case 2:
                clickedItem = mbAdapter.getItem(position);
                if (clickedItem == null) {
                    Log.d("KSM", "mainboard clickedItem is null");
                    return;
                }
                intent = new Intent(PartsSearchMain.this, PartsSearchMainboardspec.class);
                intent.putExtra("ITEM_TITLE", clickedItem.getTitle());
                intent.putExtra("ITEM_DESCRIPTION", clickedItem.getDescription());
                intent.putExtra("JSON_STRING", clickedItem.getJsonString());
                Log.d("KSM", "onItemClick - currentTabPosition: 2");
                startActivity(intent);
                break;
            case 3:
                clickedItem = ramAdapter.getItem(position);
                if (clickedItem == null) return;
                intent = new Intent(PartsSearchMain.this, PartsSearchRAMspec.class);
                intent.putExtra("ITEM_TITLE", clickedItem.getTitle());
                intent.putExtra("ITEM_DESCRIPTION", clickedItem.getDescription());
                intent.putExtra("JSON_STRING", clickedItem.getJsonString());
                startActivity(intent);
                break;
            case 4:
                clickedItem = powerAdapter.getItem(position);
                if (clickedItem == null) return;
                intent = new Intent(PartsSearchMain.this, PartsSearchPowerspec.class);
                intent.putExtra("ITEM_TITLE", clickedItem.getTitle());
                intent.putExtra("ITEM_DESCRIPTION", clickedItem.getDescription());
                intent.putExtra("JSON_STRING", clickedItem.getJsonString());
                Log.d("KSM", "onItemClick - currentTabPosition: 4");
                startActivity(intent);
                break;
            case 5:
                clickedItem = ssdAdapter.getItem(position);
                if (clickedItem == null) return;
                intent = new Intent(PartsSearchMain.this, PartsSearchSSDspec.class);
                intent.putExtra("ITEM_TITLE", clickedItem.getTitle());
                intent.putExtra("ITEM_DESCRIPTION", clickedItem.getDescription());
                intent.putExtra("JSON_STRING", clickedItem.getJsonString());
                Log.d("KSM", "onItemClick - currentTabPosition: 5");
                startActivity(intent);
                break;
            case 6:
                clickedItem = hddAdapter.getItem(position);
                if (clickedItem == null) return;
                intent = new Intent(PartsSearchMain.this, PartsSearchHDDspec.class);
                intent.putExtra("ITEM_TITLE", clickedItem.getTitle());
                intent.putExtra("ITEM_DESCRIPTION", clickedItem.getDescription());
                intent.putExtra("JSON_STRING", clickedItem.getJsonString());
                Log.d("KSM", "onItemClick - currentTabPosition: 6");
                startActivity(intent);
                break;
            case 7:
                clickedItem = coolerAdapter.getItem(position);
                if (clickedItem == null) return;
                intent = new Intent(PartsSearchMain.this, PartsSearchCoolerspec.class);
                intent.putExtra("ITEM_TITLE", clickedItem.getTitle());
                intent.putExtra("ITEM_DESCRIPTION", clickedItem.getDescription());
                intent.putExtra("JSON_STRING", clickedItem.getJsonString());
                Log.d("KSM", "onItemClick - currentTabPosition: 7");
                startActivity(intent);
                break;
            case 8:
                clickedItem = caseAdapter.getItem(position);
                if (clickedItem == null) return;
                intent = new Intent(PartsSearchMain.this, PartsSearchCasespec.class);
                intent.putExtra("ITEM_TITLE", clickedItem.getTitle());
                intent.putExtra("ITEM_DESCRIPTION", clickedItem.getDescription());
                intent.putExtra("JSON_STRING", clickedItem.getJsonString());
                Log.d("KSM", "onItemClick - currentTabPosition: 8");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_search);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}