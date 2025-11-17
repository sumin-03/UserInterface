package com.example.userinterface;

public class Power {

    private String name;
    private String manufacturer;
    private String power;    // "500" (W)
    private String plus80;   // "bronze" (80PLUS 등급)
    private String size;     // "ATX"

    // ★★★ Firestore가 객체를 변환하기 위해 꼭 필요한 기본 생성자 ★★★
    public Power() {
    }

    // (선택 사항) 데이터를 쉽게 넣기 위한 전체 생성자
    public Power(String name, String manufacturer, String power, String plus80, String size) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.power = power;
        this.plus80 = plus80;
        this.size = size;
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

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getPlus80() {
        return plus80;
    }

    public void setPlus80(String plus80) {
        this.plus80 = plus80;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}