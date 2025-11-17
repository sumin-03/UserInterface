package com.example.userinterface;

public class Cooler {

    private String name;
    private String manufacturer;
    private String kind;       // "air" (공랭/수랭)
    private String size;       // "157" (mm, 쿨러 높이)
    private String cpuGrade;   // "EL4" (CPU 등급)
    private String socket;     // "1851, 1700, ..." (지원 소켓)

    // ★★★ Firestore가 객체를 변환하기 위해 꼭 필요한 기본 생성자 ★★★
    public Cooler() {
    }

    // (선택 사항) 데이터를 쉽게 넣기 위한 전체 생성자
    public Cooler(String name, String manufacturer, String kind, String size, String cpuGrade, String socket) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.kind = kind;
        this.size = size;
        this.cpuGrade = cpuGrade;
        this.socket = socket;
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCpuGrade() {
        return cpuGrade;
    }

    public void setCpuGrade(String cpuGrade) {
        this.cpuGrade = cpuGrade;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }
}