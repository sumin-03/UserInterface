package com.example.userinterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<AppItem> appList;
    private int layoutResId; // ◀️ 레이아웃 ID를 저장할 변수 추가

    // 1. 기존 생성자 (기본값으로 item_app 사용 - 기존 코드 호환성 유지)
    public AppAdapter(List<AppItem> appList) {
        this.appList = appList;
        this.layoutResId = R.layout.item_app; // 기본 레이아웃
    }

    // 2. [추가됨] 레이아웃을 직접 지정할 수 있는 생성자
    public AppAdapter(List<AppItem> appList, int layoutResId) {
        this.appList = appList;
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ◀️ 고정된 R.layout.item_app 대신 변수(layoutResId)를 사용
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new AppViewHolder(view);
    }

    // ... (onBindViewHolder, getItemCount, getSelectedAppNames 등은 그대로 유지) ...
    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppItem item = appList.get(position);
        holder.tvAppName.setText(item.getName());
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(item.isSelected());
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelected(isChecked));
        holder.itemView.setOnClickListener(v -> {
            boolean newState = !item.isSelected();
            item.setSelected(newState);
            holder.cbSelect.setChecked(newState);
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public ArrayList<String> getSelectedAppNames() {
        ArrayList<String> selectedNames = new ArrayList<>();
        for (AppItem item : appList) {
            if (item.isSelected()) selectedNames.add(item.getName());
        }
        return selectedNames;
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppName;
        CheckBox cbSelect;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            // ⚠️ 주의: 새로운 XML 파일에도 아래 ID들이 반드시 있어야 함!
            tvAppName = itemView.findViewById(R.id.tvAppName);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }
    }
}