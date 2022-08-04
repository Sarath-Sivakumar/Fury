package app.personal.fury.UI.Adapters.dueList;

import static app.personal.fury.UI.Exp_Tracker.getDate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.expEntity;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class dueAdapter extends RecyclerView.Adapter<dueAdapter.expHolder> {
    private List<expEntity> exp = new ArrayList<>();
    private List<expEntity> todayExp = new ArrayList<>();
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
        String DisplayAmt = Constants.RUPEE + amt;
        holder.expAmt.setText("+ " + DisplayAmt);
        holder.expName.setText(currentExp.getExpenseName());
        if (filter) {
            holder.expTime.setText(currentExp.getTime());
        } else {
            holder.expTime.setText(currentExp.getDate());
        }
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
            totalSum = Float.valueOf(0);
            for (int i = 0; i < size; i++) {
                expEntity entity = exp.get(i);
                if (entity.getDate().equals(getDate()) && !todayExp.contains(entity)) {
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

    public List<expEntity> getExpList() {
        return new ArrayList<expEntity>(exp);
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