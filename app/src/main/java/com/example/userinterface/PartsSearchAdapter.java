package com.example.userinterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter; // --- 1. Filterable 구현을 위해 추가
import android.widget.Filterable; // --- 2. Filterable 구현을 위해 추가
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList; // --- 3. 리스트 생성을 위해 추가
import java.util.List;

// --- 4. Filterable 인터페이스 구현 ---
public class PartsSearchAdapter extends RecyclerView.Adapter<PartsSearchAdapter.ViewHolder> implements Filterable {

    // --- 5. 리스트 변수 수정 ---
    // private List<ItemModel> dataSet; // (원본)
    private List<ItemModel> itemListFiltered; // 현재 화면에 보여줄 필터링된 리스트
    private List<ItemModel> itemListFull;     // 필터링되지 않은 원본 리스트

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDescription;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
            textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        // --- 6. 중요: 클릭된 아이템의 '어댑터 내 위치'를 전달합니다. ---
                        // (이 position은 필터링된 리스트 기준입니다)
                        listener.onItemClick(position);
                    }
                }
            });
        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }
        public TextView getTextViewDescription() {
            return textViewDescription;
        }
    }

    /**
     * 어댑터 생성자
     * --- 7. 생성자 수정 ---
     */
    public PartsSearchAdapter(List<ItemModel> dataSet) {
        // this.dataSet = dataSet; // (원본)
        this.itemListFull = new ArrayList<>(dataSet); // 원본 리스트의 '복사본'을 저장
        this.itemListFiltered = new ArrayList<>(dataSet); // 필터링된 리스트도 일단 원본으로 초기화
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * 2. ViewHolder에 데이터 바인딩
     * --- 8. 바인딩 리스트 수정 ---
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        // ItemModel item = dataSet.get(position); // (원본)
        ItemModel item = itemListFiltered.get(position); // 필터링된 리스트 사용
        viewHolder.getTextViewTitle().setText(item.getTitle());
        viewHolder.getTextViewDescription().setText(item.getDescription());
    }

    /**
     * 3. 전체 아이템 개수 반환
     * --- 9. 아이템 개수 기준 수정 ---
     */
    @Override
    public int getItemCount() {
        // return dataSet.size(); // (원본)
        return itemListFiltered.size(); // 필터링된 리스트의 크기 반환
    }

    // --- 10. (추가) 필터링된 리스트에서 아이템을 가져오는 헬퍼 메서드 ---
    // (Activity에서 클릭된 아이템 정보를 가져오기 위해 필요합니다)
    public ItemModel getItem(int position) {
        if (position >= 0 && position < itemListFiltered.size()) {
            return itemListFiltered.get(position);
        }
        return null;
    }

    // (참고) updateData 메서드도 새 리스트로 교체하도록 수정
    public void updateData(List<ItemModel> newData) {
        this.itemListFull = new ArrayList<>(newData);
        this.itemListFiltered = new ArrayList<>(newData);
        notifyDataSetChanged();
    }

    // --- 11. Filterable 인터페이스의 getFilter 메서드 구현 ---
    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    // --- 12. 실제 필터링 로직을 수행할 Filter 객체 생성 ---
    private Filter itemFilter = new Filter() {

        /**
         * 백그라운드 스레드에서 필터링 로직 수행
         * @param constraint (CharSequence) : SearchView에 입력된 검색어
         * @return FilterResults : 필터링 결과
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ItemModel> filteredList = new ArrayList<>();

            // 검색어가 없으면 원본 리스트 전체를 반환
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                // 검색어를 소문자로 변환하고 앞뒤 공백 제거
                String filterPattern = constraint.toString().toLowerCase().trim();

                // "띄어쓰기 단위"로 검색어를 분리 (AND 조건으로 검색하기 위함)
                String[] keywords = filterPattern.split("\\s+"); // 공백 1개 이상으로 분리

                // 원본 리스트(itemListFull)에서 검색
                for (ItemModel item : itemListFull) {
                    String itemTitle = item.getTitle().toLowerCase();
                    boolean allKeywordsMatch = true;

                    // 모든 키워드를 포함하는지 검사
                    for (String keyword : keywords) {
                        if (!itemTitle.contains(keyword)) {
                            allKeywordsMatch = false;
                            break; // 하나라도 포함하지 않으면 검사 중지
                        }
                    }

                    // 모든 키워드를 포함하면 필터링된 리스트에 추가
                    if (allKeywordsMatch) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        /**
         * UI 스레드에서 필터링 결과를 화면에 적용
         * @param constraint (CharSequence) : 검색어
         * @param results (FilterResults) : performFiltering에서 반환된 결과
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemListFiltered.clear();
            itemListFiltered.addAll((List) results.values);
            notifyDataSetChanged(); // RecyclerView 갱신
        }
    };
}