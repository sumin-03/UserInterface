package com.example.userinterface;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
public class ListLoad {
    public static void loadCpuDataFromAssets(Context context, List<CpuListActivity.CPU> cpuList) {
        try {
            InputStream is = context.getAssets().open("cpu.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            JSONObject root = new JSONObject(sb.toString());
            JSONArray cpuArray = root.getJSONArray("cpu");

            for (int i = 0; i < cpuArray.length(); i++) {
                JSONObject obj = cpuArray.getJSONObject(i);

                String name = obj.getString("name");
                String manufacturer = obj.getString("manufacturer");
                String socket=obj.getString("socket");
                String memVer=obj.getString("memVer");
                String pcie = obj.getString("pcie");
                String grade = obj.getString("grade");

                cpuList.add(new CpuListActivity.CPU(name, manufacturer, socket, memVer, pcie, grade));
            }

            Log.d("CPU", "총 " + cpuList.size() + "개 로드됨");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e("CPU", "JSON 로드 실패", e);
        }
    }

    public static void loadGpuDataFromAssets(Context context,List<GpuListActivity.GPU> gpuList) {
        try {
            InputStream is = context.getAssets().open("gpu.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            JSONObject root = new JSONObject(sb.toString());
            JSONArray gpuArray = root.getJSONArray("gpu");

            for (int i = 0; i < gpuArray.length(); i++) {
                JSONObject obj = gpuArray.getJSONObject(i);

                String name = obj.getString("name");
                String power = obj.getString("power");
                String pcie = obj.getString("pcie");
                String grade = obj.getString("grade");

                gpuList.add(new GpuListActivity.GPU(name, power, pcie, grade));
            }

            Log.d("GPU", "총 " + gpuList.size() + "개 로드됨");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e("GPU", "JSON 로드 실패", e);
        }
    }
    public static void loadMainboardDataFromAssets(Context context, List<MainboardListActivity.Mainboard> mainboardList) {
        try {
            InputStream is = context.getAssets().open("mainboard.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            JSONObject root = new JSONObject(sb.toString());
            JSONArray mainboardArray = root.getJSONArray("mainboard");

            for (int i = 0; i < mainboardArray.length(); i++) {
                JSONObject obj = mainboardArray.getJSONObject(i);

                String name=obj.getString("name");
                String manufacturer=obj.getString("manufacturer");
                String chipset=obj.getString("chipset");
                String socket=obj.getString("socket");
                String pcieVer=obj.getString("pcieVer");
                String size=obj.getString("size");
                String memVer=obj.getString("memVer");
                String memMax=obj.getString("memMax");
                String memSlot=obj.getString("memSlot");

                mainboardList.add(new MainboardListActivity.Mainboard(name, manufacturer, chipset, socket, pcieVer, size, memVer, memMax, memSlot));
            }

            Log.d("Mainboard", "총 " + mainboardList.size() + "개 로드됨");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e("Mainboard", "JSON 로드 실패", e);
        }
    }
    public static void loadPowerDataFromAssets(Context context, List<PowerListActivity.Power> powerList) {
        try {
            InputStream is = context.getAssets().open("power.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            JSONObject root = new JSONObject(sb.toString());
            JSONArray powerArray = root.getJSONArray("power");

            for (int i = 0; i < powerArray.length(); i++) {
                JSONObject obj = powerArray.getJSONObject(i);

                String name = obj.getString("name");
                String manufacturer=obj.getString("manufacturer");
                String power = obj.getString("power");
                String plus80 = obj.getString("plus80");
                String size = obj.getString("size");

                powerList.add(new PowerListActivity.Power(name, manufacturer, power, plus80, size));
            }

            Log.d("Power", "총 " + powerList.size() + "개 로드됨");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e("GPU", "JSON 로드 실패", e);
        }
    }
    public static void loadCaseDataFromAssets(Context context, List<CaseListActivity.Case> caseList) {
        try {
            InputStream is = context.getAssets().open("case.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            JSONObject root = new JSONObject(sb.toString());
            JSONArray caseArray = root.getJSONArray("case");

            for (int i = 0; i < caseArray.length(); i++) {
                JSONObject obj = caseArray.getJSONObject(i);
                String name=obj.getString("name");
                String manufacturer=obj.getString("manufacturer");
                String boardSize=obj.getString("boardSize");
                String coolerSize=obj.getString("coolerSize");
                String gpuSize=obj.getString("gpuSize");
                String powerSize=obj.getString("powerSize");
                String size=obj.getString("size");
                String atxPower=obj.getString("atxPower");

                caseList.add(new CaseListActivity.Case(name, manufacturer, boardSize, coolerSize, gpuSize,powerSize, size, atxPower));
            }

            Log.d("Case", "총 " + caseList.size() + "개 로드됨");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e("Case", "JSON 로드 실패", e);
        }
    }
    public static void loadCoolerDataFromAssets(Context context, List<CoolerListActivity.Cooler> coolerList) {
        try {
            InputStream is = context.getAssets().open("cooler.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            JSONObject root = new JSONObject(sb.toString());
            JSONArray coolerArray = root.getJSONArray("cooler");

            for (int i = 0; i < coolerArray.length(); i++) {
                JSONObject obj = coolerArray.getJSONObject(i);
                String name=obj.getString("name");
                String manufacturer=obj.getString("manufacturer");
                String kind=obj.getString("kind");
                String size=obj.getString("size");
                String cpuGrade=obj.getString("cpuGrade");
                String socket=obj.getString("socket");

                coolerList.add(new CoolerListActivity.Cooler(name, manufacturer, kind, size, cpuGrade, socket));
            }

            Log.d("Cooler", "총 " + coolerList.size() + "개 로드됨");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e("Cooler", "JSON 로드 실패", e);
        }
    }
}
