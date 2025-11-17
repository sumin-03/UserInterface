package com.example.userinterface;

public class Case {

    private String name;
    private String manufacturer;
    private String boardSize;    // "ATX, M-ATX, M-ITX"
    private String coolerSize;   // "160" (mm)
    private String gpuSize;      // "410" (mm)
    private String powerSize;    // "top" (파워 상단/하단)
    private String size;         // "big tower" (케이스 크기)
    private String atxPower;     // "TRUE" (표준 ATX 파워 지원 여부)

    // ★★★ Firestore가 객체를 변환하기 위해 꼭 필요한 기본 생성자 ★★★
    public Case() {
    }

    // (선택 사항) 데이터를 쉽게 넣기 위한 전체 생성자
    public Case(String name, String manufacturer, String boardSize, String coolerSize,
                String gpuSize, String powerSize, String size, String atxPower) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.boardSize = boardSize;
        this.coolerSize = coolerSize;
        this.gpuSize = gpuSize;
        this.powerSize = powerSize;
        this.size = size;
        this.atxPower = atxPower;
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

    public String getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(String boardSize) {
        this.boardSize = boardSize;
    }

    public String getCoolerSize() {
        return coolerSize;
    }

    public void setCoolerSize(String coolerSize) {
        this.coolerSize = coolerSize;
    }

    public String getGpuSize() {
        return gpuSize;
    }

    public void setGpuSize(String gpuSize) {
        this.gpuSize = gpuSize;
    }

    public String getPowerSize() {
        return powerSize;
    }

    public void setPowerSize(String powerSize) {
        this.powerSize = powerSize;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAtxPower() {
        return atxPower;
    }

    public void setAtxPower(String atxPower) {
        this.atxPower = atxPower;
    }
}