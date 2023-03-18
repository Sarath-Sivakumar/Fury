package app.personal.fury.UI.Adapters.dueList;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.debtEntity;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class dueAdapter extends RecyclerView.Adapter<dueAdapter.expHolder> {
    private final List<debtEntity> debt = new ArrayList<>();
    private onItemClickListener listener;
    private int totalSum = 0;
    private final int isRepeating;
    private int repeatSize = 0;
//    @ColorInt
//    private int colorGreen;

    public dueAdapter(int isRepeating){
        this.isRepeating = isRepeating;
    }

    @NonNull
    @Override
    public expHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isRepeating==0){
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.recycler_item_upcoming, parent, false);
            return new expHolder(itemView);
        }else if (isRepeating==1){
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.recycler_item_repeatingdue, parent, false);
            return new expHolder(itemView);
        } else{
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.recycler_item_alldue, parent, false);
            return new expHolder(itemView);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull expHolder holder, int position) {
        debtEntity entity = debt.get(position);
        String amt = Constants.RUPEE + entity.getAmount();
        holder.dAmt.setText(amt);
        String uname = entity.getSource();
        String[] arr = uname.split(" ");
        String dname = arr[0];
        holder.dName.setText(dname);
        if (entity.getStatus().equals(Constants.DEBT_PAID)) {
            holder.dFinalDate.setText(entity.getDate());
        } else {
            holder.dFinalDate.setText(entity.getFinalDate());
        }
        if (isRepeating>=2){
            holder.dStatus.setText(entity.getStatus());
        }
        //To get first letter in source name--------------------------------------
        String ico = String.valueOf(entity.getSource().charAt(0)).toUpperCase();
        holder.icoText.setText(ico);
        //------------------------------------------------------------------------
    }

    @Override
    public int getItemCount() {
        return debt.size();
    }

    public int getTotalDebt() {
        return totalSum;
    }

    public void setDebt(List<debtEntity> debt, int filter) {
        totalSum = 0;
        for (int i = 0; i < debt.size(); i++) {
            totalSum = totalSum + debt.get(i).getAmount();
            if (filter == 1) {
                if (debt.get(i).getStatus().equals(Constants.DEBT_NOT_PAID) &&
                        debt.get(i).getIsRepeat() == Constants.NON_REPEATING_DUE) {
                    this.debt.add(debt.get(i));
                }
            } else if (filter == 0) {
                this.debt.add(debt.get(i));

            } else {
                if (debt.get(i).getIsRepeat() == Constants.REPEATING_DUE) {
                    repeatSize = repeatSize + 1;
                    this.debt.add(debt.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void clear() {
        debt.clear();
        notifyDataSetChanged();
    }

    public debtEntity getDebtAt(int position) {
        return debt.get(position);
    }

    class expHolder extends RecyclerView.ViewHolder {
        private final TextView dAmt, dFinalDate, dName, icoText, dStatus;

        public expHolder(@NonNull View itemView) {
            super(itemView);
            dAmt = itemView.findViewById(R.id.dAmt);
            dStatus = itemView.findViewById(R.id.dStatus);
            dFinalDate = itemView.findViewById(R.id.finalDate);
            icoText = itemView.findViewById(R.id.icoText);
            dName = itemView.findViewById(R.id.dName);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(debt.get(pos));
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(debtEntity exp);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}