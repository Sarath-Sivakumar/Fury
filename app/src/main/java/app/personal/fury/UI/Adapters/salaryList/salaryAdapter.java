package app.personal.fury.UI.Adapters.salaryList;

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

    private String Currency;

    @NonNull
    @Override
    public salHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_item_earnings, parent, false);
        return new salHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull salHolder holder, int position) {
        salaryEntity entity = salList.get(position);
        String s1 = "+" + Currency + entity.getSalary();
        holder.incAmt.setText(s1);
        holder.incName.setText(entity.getIncName());
        String s2;
        if (entity.getSalMode()==Constants.SAL_MODE_ACC){
            s2 = "Bank";
            holder.salMod.setText(s2);
        }else{
            s2 = "Cash";
            holder.salMod.setText(s2);

        }        String ico = String.valueOf(entity.getIncName().charAt(0)).toUpperCase();
        holder.icoText.setText(ico);
        String s3;
        if (entity.getIncType()==Constants.monthly){
            s3 = "Monthly";
            holder.incTyp.setText(s3);
        }else if (entity.getIncType()==Constants.daily){
            s3 = "Daily";
            holder.incTyp.setText(s3);
        }else if (entity.getIncType()==Constants.hourly){
            s3 = "Hourly";
            holder.incTyp.setText(s3);
        }else if (entity.getIncType()==Constants.oneTime){
            s3 = "One Time";
            holder.incTyp.setText(s3);
        }
    }

    @Override
    public int getItemCount() {
        return salList.size();
    }

    public void setSal(List<salaryEntity> exp, String Currency) {
        this.Currency = Currency;
        clear();
        this.salList.addAll(exp);
        notifyDataSetChanged();
    }

    public salaryEntity getSalaryEntity(int position){
        return salList.get(position);
    }

    public void clear() {
        salList.clear();
    }

    class salHolder extends RecyclerView.ViewHolder {
        private final TextView incAmt;
        private final TextView incName;
        private final TextView incTyp;
        private final TextView icoText;
        private final TextView salMod;

        public salHolder(@NonNull View itemView) {
            super(itemView);
            incAmt = itemView.findViewById(R.id.salAmt);
            incName = itemView.findViewById(R.id.salName);
            incTyp = itemView.findViewById(R.id.salTyp);
            icoText = itemView.findViewById(R.id.icoText);
            salMod = itemView.findViewById(R.id.salMod);

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