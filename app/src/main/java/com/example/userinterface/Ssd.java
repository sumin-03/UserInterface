package com.example.userinterface;

import com.google.firebase.firestore.PropertyName;

public class Ssd {

    private String name;
    private String manufacturer;
    private String storage;      // "1" (TB)
    private String formfactor;   // "M.2"
    private String pcie;         // "4" (PCIe 4.0)
    private String dramDdr;      // "4" (DDR4)
    private String dramSize;     // "1" (GB)
    private String read;         // "7000" (MB/s)
    private String write;        // "6500" (MB/s)
    private String readIOPS;     // "1400" (K)
    private String writeIOPS;    // "1300" (K)

    // ★★★ Firestore가 객체를 변환하기 위해 꼭 필요한 기본 생성자 ★★★
    public Ssd() {
    }

    // (선택 사항) 데이터를 쉽게 넣기 위한 전체 생성자
    public Ssd(String name, String manufacturer, String storage, String formfactor, String pcie,
               String dramDdr, String dramSize, String read, String write,
               String readIOPS, String writeIOPS) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.storage = storage;
        this.formfactor = formfactor;
        this.pcie = pcie;
        this.dramDdr = dramDdr;
        this.dramSize = dramSize;
        this.read = read;
        this.write = write;
        this.readIOPS = readIOPS;
        this.writeIOPS = writeIOPS;
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

    public String getFormfactor() {
        return formfactor;
    }

    public void setFormfactor(String formfactor) {
        this.formfactor = formfactor;
    }

    public String getPcie() {
        return pcie;
    }

    public void setPcie(String pcie) {
        this.pcie = pcie;
    }

    // JSON 키 "dram_ddr"를 "dramDdr" 필드에 매핑
    @PropertyName("dram_ddr")
    public String getDramDdr() {
        return dramDdr;
    }

    @PropertyName("dram_ddr")
    public void setDramDdr(String dramDdr) {
        this.dramDdr = dramDdr;
    }

    // JSON 키 "dram_size"를 "dramSize" 필드에 매핑
    @PropertyName("dram_size")
    public String getDramSize() {
        return dramSize;
    }

    @PropertyName("dram_size")
    public void setDramSize(String dramSize) {
        this.dramSize = dramSize;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getWrite() {
        return write;
    }

    public void setWrite(String write) {
        this.write = write;
    }

    // JSON 키 "read_IOPS"를 "readIOPS" 필드에 매핑
    @PropertyName("read_IOPS")
    public String getReadIOPS() {
        return readIOPS;
    }

    @PropertyName("read_IOPS")
    public void setReadIOPS(String readIOPS) {
        this.readIOPS = readIOPS;
    }

    // JSON 키 "write_IOPS"를 "writeIOPS" 필드에 매핑
    @PropertyName("write_IOPS")
    public String getWriteIOPS() {
        return writeIOPS;
    }

    @PropertyName("write_IOPS")
    public void setWriteIOPS(String writeIOPS) {
        this.writeIOPS = writeIOPS;
    }
}