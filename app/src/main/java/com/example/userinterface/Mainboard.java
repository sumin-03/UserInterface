package com.example.userinterface;

import com.google.firebase.firestore.PropertyName;

public class Mainboard {

    private String name;
    private String manufacturer;
    private String chipset;
    private String socket;
    private String pcieVer;  // "5" (PCIe 5.0)
    private String size;     // "ATX"
    private String memVer;   // "DDR5"
    private String memMax;   // "192" (GB)
    private String memSlot;  // "4" (개)
    private String m2Slots;  // "5" (개) - 필드명 변경
    private String sata;     // "4" (개)

    // ★★★ Firestore가 객체를 변환하기 위해 꼭 필요한 기본 생성자 ★★★
    public Mainboard() {
    }

    // (선택 사항) 데이터를 쉽게 넣기 위한 전체 생성자
    public Mainboard(String name, String manufacturer, String chipset, String socket,
                     String pcieVer, String size, String memVer, String memMax,
                     String memSlot, String m2Slots, String sata) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.chipset = chipset;
        this.socket = socket;
        this.pcieVer = pcieVer;
        this.size = size;
        this.memVer = memVer;
        this.memMax = memMax;
        this.memSlot = memSlot;
        this.m2Slots = m2Slots;
        this.sata = sata;
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

    public String getChipset() {
        return chipset;
    }

    public void setChipset(String chipset) {
        this.chipset = chipset;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getPcieVer() {
        return pcieVer;
    }

    public void setPcieVer(String pcieVer) {
        this.pcieVer = pcieVer;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMemVer() {
        return memVer;
    }

    public void setMemVer(String memVer) {
        this.memVer = memVer;
    }

    public String getMemMax() {
        return memMax;
    }

    public void setMemMax(String memMax) {
        this.memMax = memMax;
    }

    public String getMemSlot() {
        return memSlot;
    }

    public void setMemSlot(String memSlot) {
        this.memSlot = memSlot;
    }

    // JSON 키 "M.2"는 자바 필드명으로 사용할 수 없으므로,
    // @PropertyName 어노테이션으로 매핑합니다.
    @PropertyName("M.2")
    public String getM2Slots() {
        return m2Slots;
    }

    @PropertyName("M.2")
    public void setM2Slots(String m2Slots) {
        this.m2Slots = m2Slots;
    }

    @PropertyName("SATA") // JSON 키가 "SATA" (대문자)이므로 매핑합니다.
    public String getSata() {
        return sata;
    }

    @PropertyName("SATA")
    public void setSata(String sata) {
        this.sata = sata;
    }
}