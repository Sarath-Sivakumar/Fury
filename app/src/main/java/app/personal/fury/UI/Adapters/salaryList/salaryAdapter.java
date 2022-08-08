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

public class salaryAdapter extends RecyclerView.Adapter<salaryAdapter.salHolder> {
    private final List<salaryEntity> salList = new ArrayList<>();
    private onItemClickListener listener;
    private float totalSum=0;

    @NonNull
    @Override
    public salHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.sal_list_item, parent, false);
        return new salHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull salHolder holder, int position) {
        salaryEntity entity = salList.get(position);
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
        return salList.size();
    }

    public float getTotalSal() {
        return totalSum;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSal(List<salaryEntity> exp) {
        totalSum = 0;
        this.salList.addAll(exp);
        int size = exp.size();
        for (int i=0;i<size;i++){
            totalSum = totalSum + exp.get(i).getSalary();
        }
        notifyDataSetChanged();
    }

    public void clear() {
        salList.clear();
    }

    class salHolder extends RecyclerView.ViewHolder {
        private final TextView incAmt;
        private final TextView incName;
        private final TextView incTyp;

        public salHolder(@NonNull View itemView) {
            super(itemView);
            incAmt = itemView.findViewById(R.id.salAmt);
            incName = itemView.findViewById(R.id.salName);
            incTyp = itemView.findViewById(R.id.salTyp);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(salList.get(pos));
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