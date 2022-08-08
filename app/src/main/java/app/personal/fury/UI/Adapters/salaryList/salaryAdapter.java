package app.personal.fury.UI.Adapters.salaryList;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.salaryEntity;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class salaryAdapter extends RecyclerView.Adapter<salaryAdapter.expHolder> {
    private List<salaryEntity> exp = new ArrayList<>();
    private onItemClickListener listener;
    private Float totalSum=0.0F;

    @NonNull
    @Override
    public expHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.sal_list_item, parent, false);
        return new expHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull expHolder holder, int position) {
        salaryEntity entity = exp.get(position);
        holder.incAmt.setText(Constants.RUPEE + entity.getSalary());
        holder.incName.setText(entity.getIncName());
        if (entity.getIncType()==Constants.monthly){
            holder.incTyp.setText("Monthly");
        }else if (entity.getIncType()==Constants.daily){
            holder.incTyp.setText("Daily");
        }else{
            holder.incTyp.setText("Hourly");
        }
    }

    @Override
    public int getItemCount() {
        return exp.size();
    }

    public Float getTotalSal() {
        if (totalSum != null) {
            return totalSum;
        } else {
            return (float) 0;
        }
    }

    public void setSal(List<salaryEntity> exp) {
        this.exp.addAll(exp);
        int size = exp.size();
        for (int i=0;i<size;i++){
            totalSum = totalSum + exp.get(i).getSalary();
        }
        notifyDataSetChanged();
    }

    public void clear() {
        exp.clear();
    }

    public List<salaryEntity> getExpList() {
        return new ArrayList<salaryEntity>(exp);
    }

    public salaryEntity getExpAt(int position) {
        return exp.get(position);
    }

    class expHolder extends RecyclerView.ViewHolder {
        private final TextView incAmt;
        private final TextView incName;
        private final TextView incTyp;

        public expHolder(@NonNull View itemView) {
            super(itemView);
            incAmt = itemView.findViewById(R.id.salAmt);
            incName = itemView.findViewById(R.id.salName);
            incTyp = itemView.findViewById(R.id.salTyp);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(exp.get(pos));
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(salaryEntity exp);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}