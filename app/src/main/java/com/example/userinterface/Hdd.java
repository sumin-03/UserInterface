package com.example.userinterface;

public class Hdd {

    private String name;
    private String manufacturer;
    private String storage;      // "8" (TB)
    private String inch;         // "3.5" (인치)
    private String anInterface;  // "SATA3" (interface는 자바 예약어라 anInterface로 변경)
    private String RPM;          // "5400"
    private String buffer;       // "256" (MB)
    private String write;        // "190" (MB/s)
    private String read;         // "190" (MB/s)
    private String recode;       // "SMR"

    // ★★★ Firestore가 객체를 변환하기 위해 꼭 필요한 기본 생성자 ★★★
    public Hdd() {
    }

    // (선택 사항) 데이터를 쉽게 넣기 위한 전체 생성자
    public Hdd(String name, String manufacturer, String storage, String inch, String anInterface,
               String RPM, String buffer, String write, String read, String recode) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.storage = storage;
        this.inch = inch;
        this.anInterface = anInterface;
        this.RPM = RPM;
        this.buffer = buffer;
        this.write = write;
        this.read = read;
        this.recode = recode;
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

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getInch() {
        return inch;
    }

    public void setInch(String inch) {
        this.inch = inch;
    }

    // 'interface'는 자바의 예약어(keyword)이므로 변수명으로 사용할 수 없습니다.
    // 'anInterface' 또는 'storageInterface' 등으로 변경해야 합니다.
    // JSON의 'interface' 키와 매칭시키려면 @PropertyName("interface")를 사용할 수 있습니다.
    public String getAnInterface() {
        return anInterface;
    }

    public void setAnInterface(String anInterface) {
        this.anInterface = anInterface;
    }

    public String getRPM() {
        return RPM;
    }

    public void setRPM(String RPM) {
        this.RPM = RPM;
    }

    public String getBuffer() {
        return buffer;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public String getWrite() {
        return write;
    }

    public void setWrite(String write) {
        this.write = write;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getRecode() {
        return recode;
    }

    public void setRecode(String recode) {
        this.recode = recode;
    }
}