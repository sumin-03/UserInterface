package com.example.userinterface;

public class Gpu {

    private String name;
    private String manufacturer;
    private int fan;              // 3
    private int vram;             // 32
    private double size;          // 323.0 (mm, 그래픽카드 길이)
    private int base;             // 2017 (MHz)
    private int boost;            // 2407 (MHz)
    private String outPut;        // "HDMI2.1, DP2.1"
    private int power;            // 1000 (W, 권장 파워)
    private int pcie;             // 5 (PCIe 5.0)
    private String chipSet;       // "RTX 5090"
    private String gpuVer;        // "NVIDIA"

    // ★★★ Firestore가 객체를 변환하기 위해 꼭 필요한 기본 생성자 ★★★
    public Gpu() {
    }

    // (선택 사항) 데이터를 쉽게 넣기 위한 전체 생성자
    public Gpu(String name, String manufacturer, int fan, int vram, double size, int base,
               int boost, String outPut, int power, int pcie, String chipSet, String gpuVer) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.fan = fan;
        this.vram = vram;
        this.size = size;
        this.base = base;
        this.boost = boost;
        this.outPut = outPut;
        this.power = power;
        this.pcie = pcie;
        this.chipSet = chipSet;
        this.gpuVer = gpuVer;
    }

    // ★★★ Firestore가 데이터를 읽기 위해 모든 필드의 Getter가 필요합니다! ★★★

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getFan() {
        return fan;
    }

    public void setFan(int fan) {
        this.fan = fan;
    }

    public int getVram() {
        return vram;
    }

    public void setVram(int vram) {
        this.vram = vram;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public int getBoost() {
        return boost;
    }

    public void setBoost(int boost) {
        this.boost = boost;
    }

    public String getOutPut() {
        return outPut;
    }

    public void setOutPut(String outPut) {
        this.outPut = outPut;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPcie() {
        return pcie;
    }

    public void setPcie(int pcie) {
        this.pcie = pcie;
    }

    public String getChipSet() {
        return chipSet;
    }

    public void setChipSet(String chipSet) {
        this.chipSet = chipSet;
    }

    public String getGpuVer() {
        return gpuVer;
    }

    public void setGpuVer(String gpuVer) {
        this.gpuVer = gpuVer;
    }
}