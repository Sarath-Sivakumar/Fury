package app.personal.fury.UI.Adapters.expList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.expEntity;
import app.personal.Utls.Commons;
import app.personal.fury.R;

public class expAdapter extends RecyclerView.Adapter<expAdapter.expHolder> {
    private List<expEntity> exp = new ArrayList<>();
    private final List<expEntity> todayExp = new ArrayList<>();
    private onItemClickListener listener;
    private boolean filter;
    private Float totalSum;

    @NonNull
    @Override
    public expHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exp_list_item, parent, false);
        return new expHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull expHolder holder, int position) {
        expEntity currentExp = todayExp.get(position);
        String amt = String.valueOf(currentExp.getExpenseAmt());
        String DisplayAmt = "- ₹" + amt;
        holder.expAmt.setText(DisplayAmt);
        holder.expName.setText(currentExp.getExpenseName());
        holder.expDate.setText(currentExp.getDate());
        holder.expTime.setText(currentExp.getTime());
    }

    @Override
    public int getItemCount() {
        return todayExp.size();
    }

    public Float getTotalExp() {
        if (totalSum!=null){
            return totalSum;
        }else{
            return (float) 0;
        }
    }

    public void setExp(List<expEntity> exp, boolean filter) {
        this.filter = filter;
        if (filter) {
            this.exp = exp;
            int size = exp.size();
            totalSum = 0.0F;
            for (int i = 0; i < size; i++) {
                expEntity entity = exp.get(i);
                if (entity.getDate().equals(Commons.getDate()) && !todayExp.contains(entity)) {
                    todayExp.add(entity);
                    totalSum = totalSum + entity.getExpenseAmt();
                }
            }
        } else {
            todayExp.addAll(exp);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        todayExp.clear();
        exp.clear();
    }

    public expEntity getExpAt(int position) {
        return todayExp.get(position);
    }

    class expHolder extends RecyclerView.ViewHolder {
        private final TextView expAmt;
        private final TextView expName;
        private final TextView expTime;
        private final TextView expDate;

        public expHolder(@NonNull View itemView) {
            super(itemView);
            expAmt = itemView.findViewById(R.id.itemAmt);
            expName = itemView.findViewById(R.id.itemTitle);
            expTime = itemView.findViewById(R.id.itemTime);
            expDate = itemView.findViewById(R.id.itemDate);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(todayExp.get(pos));
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(expEntity exp);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}