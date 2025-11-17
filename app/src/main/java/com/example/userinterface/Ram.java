package com.example.userinterface;

public class Ram {

    private String name;
    private String manufacturer;
    private String ddr;        // "5" (DDR5)
    private String size;       // "32" (GB)
    private String clock;      // "5600" (MHz)
    private String heatsink;   // "0" (방열판 없음)

    // ★★★ Firestore가 객체를 변환하기 위해 꼭 필요한 기본 생성자 ★★★
    public Ram() {
    }

    // (선택 사항) 데이터를 쉽게 넣기 위한 전체 생성자
    public Ram(String name, String manufacturer, String ddr, String size, String clock, String heatsink) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.ddr = ddr;
        this.size = size;
        this.clock = clock;
        this.heatsink = heatsink;
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

    public String getDdr() {
        return ddr;
    }

    public void setDdr(String ddr) {
        this.ddr = ddr;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getHeatsink() {
        return heatsink;
    }

    public void setHeatsink(String heatsink) {
        this.heatsink = heatsink;
    }
}