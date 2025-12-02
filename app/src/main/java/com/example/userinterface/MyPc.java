package com.example.userinterface;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

public class MyPc {

    private String cpu;
    private String gpu;
    private String mainboard;
    private String ram;
    private String power;
    private String box;
    private String cooler;
    private String storage;
    private String userId;

    public MyPc() {}

    public MyPc(String cpu, String gpu, String mainboard, String ram,
                String power, String box, String cooler, String storage, String userId) {
        this.cpu = cpu;
        this.gpu = gpu;
        this.mainboard = mainboard;
        this.ram = ram;
        this.power = power;
        this.box = box;
        this.cooler = cooler;
        this.storage = storage;
        this.userId = userId;
    }

    // ===== Getter =====
    public String getCpu() { return cpu; }
    public String getGpu() { return gpu; }
    public String getMainboard() { return mainboard; }
    public String getRam() { return ram; }
    public String getPower() { return power; }
    public String getBox() { return box; }
    public String getCooler() { return cooler; }
    public String getStorage() { return storage; }
    public String getUserId() { return userId; }

    public static void load(String uid, OnMyPcLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("mypcs").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        MyPc myPc = documentSnapshot.toObject(MyPc.class);
                        listener.onLoaded(myPc);
                    } else {
                        listener.onLoaded(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MyPc", "load failed", e);
                    listener.onError(e);
                });
    }

    public static void listen(String uid, OnMyPcLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("mypcs")
                .document(uid)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        listener.onError(e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        MyPc myPc = snapshot.toObject(MyPc.class);
                        listener.onLoaded(myPc);
                    } else {
                        listener.onLoaded(null);
                    }
                });
    }

    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("mypcs")
                .document(this.userId)
                .set(this);
    }

    public static void updateCpu(String uid, String cpu) {
        FirebaseFirestore.getInstance()
                .collection("mypcs")
                .document(uid)
                .update("cpu", cpu);
    }

    public static void updateGpu(String uid, String gpu) {
        FirebaseFirestore.getInstance()
                .collection("mypcs")
                .document(uid)
                .update("gpu", gpu);
    }

    public static void updateMainboard(String uid, String mainboard) {
        FirebaseFirestore.getInstance()
                .collection("mypcs")
                .document(uid)
                .update("mainboard", mainboard);
    }

    public static void updateRam(String uid, String ram) {
        FirebaseFirestore.getInstance()
                .collection("mypcs")
                .document(uid)
                .update("ram", ram);
    }

    public static void updatePower(String uid, String power) {
        FirebaseFirestore.getInstance()
                .collection("mypcs")
                .document(uid)
                .update("power", power);
    }
    public static void updateBox(String uid, String box) {
        FirebaseFirestore.getInstance()
                .collection("mypcs")
                .document(uid)
                .update("box", box);
    }
    public static void updateCooler(String uid, String cooler) {
        FirebaseFirestore.getInstance()
                .collection("mypcs")
                .document(uid)
                .update("cooler", cooler);
    }
    public static void updateStorage(String uid, String storage) {
        FirebaseFirestore.getInstance()
                .collection("mypcs")
                .document(uid)
                .update("storage", storage);
    }
    public static void updateParts(String uid, MyPc newPc) {
        FirebaseFirestore.getInstance()
                .collection("mypcs")
                .document(uid)
                .set(newPc);
    }

    public interface OnMyPcLoadedListener {
        void onLoaded(@Nullable MyPc myPc);
        void onError(Exception e);
    }
}
