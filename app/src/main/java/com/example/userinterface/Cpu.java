package com.example.userinterface;

// Firestore가 데이터를 읽고 쓰기 위해 @PropertyName을 사용할 수 있습니다.
// 필드 이름이 Firestore 문서의 키 이름이 됩니다.
public class Cpu {

    // 필드 선언
    private String name;
    private String manufacturer;
    private String socket;
    private String cores;
    private String threads;
    private String memory_version;
    private String memory_clock;
    private String base_clock;
    private String boost_clock;
    private String l2cache; // XML의 value_L2cache와 매칭
    private String l3cache; // XML의 value_L3cache와 매칭
    private String graphics;
    private String tdp;
    // private String imageUrl; // 이미지 URL이 있다면 추가

    // ★★★ Firestore는 데이터를 객체로 변환할 때 기본 생성자가 꼭 필요합니다! ★★★
    public Cpu() {
        // 기본 생성자
    }

    // (선택 사항) 데이터를 쉽게 넣기 위한 전체 생성자
    public Cpu(String name, String manufacturer, String socket, String cores, String threads,
               String memory_version, String memory_clock, String base_clock, String boost_clock,
               String l2cache, String l3cache, String graphics, String tdp) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.socket = socket;
        this.cores = cores;
        this.threads = threads;
        this.memory_version = memory_version;
        this.memory_clock = memory_clock;
        this.base_clock = base_clock;
        this.boost_clock = boost_clock;
        this.l2cache = l2cache;
        this.l3cache = l3cache;
        this.graphics = graphics;
        this.tdp = tdp;
    }

    // ★★★ Firestore가 데이터를 읽을 수 있도록 모든 필드의 Getter가 필요합니다! ★★★
    // (Setter는 지금 당장 필요하진 않지만, 좋은 습관을 위해 추가합니다)

    // ... 여기에 모든 필드에 대한 Getter와 Setter를 추가하세요 ...
    // 예:
    public String getname() { return name; }
    public void setname(String name) { this.name = name; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getSocket() { return socket; }
    public void setSocket(String socket) { this.socket = socket; }

    public String getCores() { return cores; }
    public void setCores(String cores) { this.cores = cores; }

    public String getThreads() { return threads; }
    public void setThreads(String threads) { this.threads = threads; }

    public String getMemory_version() { return memory_version; }
    public void setMemory_version(String memory_version) { this.memory_version = memory_version; }

    public String getMemory_clock() { return memory_clock; }
    public void setMemory_clock(String memory_clock) { this.memory_clock = memory_clock; }

    public String getBase_clock() { return base_clock; }
    public void setBase_clock(String base_clock) { this.base_clock = base_clock; }

    public String getBoost_clock() { return boost_clock; }
    public void setBoost_clock(String boost_clock) { this.boost_clock = boost_clock; }

    public String getL2cache() { return l2cache; }
    public void setL2cache(String l2cache) { this.l2cache = l2cache; }

    public String getL3cache() { return l3cache; }
    public void setL3cache(String l3cache) { this.l3cache = l3cache; }

    public String getGraphics() { return graphics; }
    public void setGraphics(String graphics) { this.graphics = graphics; }

    public String getTdp() { return tdp; }
    public void setTdp(String tdp) { this.tdp = tdp; }
}