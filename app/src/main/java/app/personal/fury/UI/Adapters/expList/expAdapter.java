package app.personal.fury.UI.Adapters.expList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.expEntity;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class expAdapter extends RecyclerView.Adapter<expAdapter.expHolder> {
    private final List<expEntity> exp = new ArrayList<>();
    private onItemClickListener listener;

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
        expEntity currentExp = exp.get(position);
        String DisplayAmt = Constants.RUPEE + (int) currentExp.getExpenseAmt();
        holder.expAmt.setText(DisplayAmt);
        holder.expName.setText(currentExp.getExpenseName());
        holder.expDate.setText(currentExp.getDate());
        holder.expTime.setText(currentExp.getTime());


        switch(currentExp.getExpenseName()){
            case "Food":
                holder.expIcon.setImageResource(R.drawable.hamburger);
                break;
            case "Travel":
                holder.expIcon.setImageResource(R.drawable.destination);
                break;
            case "Rent":
                holder.expIcon.setImageResource(R.drawable.rent);
                break;
            case "Gas":
                holder.expIcon.setImageResource(R.drawable.gaspump);
                break;
            case "Electricity":
                holder.expIcon.setImageResource(R.drawable.electricalenergy);
                break;
            case "Recharge":
                holder.expIcon.setImageResource(R.drawable.recharge);
                break;
            case "Fees":
                holder.expIcon.setImageResource(R.drawable.fees);
                break;
            case "Subscriptions":
                holder.expIcon.setImageResource(R.drawable.subscription);
                break;
            case "Health Care":
                holder.expIcon.setImageResource(R.drawable.healthcare);
                break;
            case "Bills":
                holder.expIcon.setImageResource(R.drawable.bill);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return exp.size();
    }

    public int getTotalExpInt() {
        int totalSum = 0;
        for (int i = 0; i < exp.size(); i++) {
            totalSum = totalSum + exp.get(i).getExpenseAmt();
        }
        return totalSum;
    }

    public String getTotalExpStr() {
        return Constants.RUPEE + (int) getTotalExpInt();
    }

    public void setExp(List<expEntity> exp, boolean filter) {
        clear();
        if (filter) {
            int size = exp.size();
            for (int i = 0; i < size; i++) {
                expEntity entity = exp.get(i);
                if (entity.getDate().equals(Commons.getDate())) {
                    this.exp.add(entity);
                }
            }
        } else {
            this.exp.addAll(exp);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        exp.clear();
    }

    public expEntity getExpAt(int position) {
        return this.exp.get(position);
    }

    class expHolder extends RecyclerView.ViewHolder {
        private final TextView expAmt;
        private final TextView expName;
        private final TextView expTime;
        private final TextView expDate;
        private final ImageView expIcon;

        public expHolder(@NonNull View itemView) {
            super(itemView);
            expAmt = itemView.findViewById(R.id.itemAmt);
            expName = itemView.findViewById(R.id.itemTitle);
            expTime = itemView.findViewById(R.id.itemTime);
            expDate = itemView.findViewById(R.id.itemDate);
            expIcon = itemView.findViewById(R.id.itemIcon);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(exp.get(pos));
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