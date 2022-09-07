package app.personal.fury.UI.Adapters.mainLists;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.expEntity;
import app.personal.Utls.Commons;
import app.personal.fury.R;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.catHolder>{

    private onItemClickListener listener;
    private final List<expEntity> sumExp = new ArrayList<>();
    private float salary;

    @NonNull
    @Override
    public catHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.overview_list_item, parent, false);
        return new catHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull catHolder holder, int position) {
        expEntity entity = sumExp.get(position);
        holder.expName.setText(entity.getExpenseName());
        holder.progress.setProgress(Commons.setProgress(sumExp.get(position).getExpenseAmt(),salary), true);
        holder.expPercent.setText(String.valueOf(Commons.setProgress(sumExp.get(position).getExpenseAmt(),salary)
                + entity.getExpenseAmt()));
        switch(entity.getExpenseName()){
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

    public void setExpes(List<expEntity> exp, float salary) {
        this.salary = salary;
        for (int i = 0; i < exp.size(); i++) {
            switch (exp.get(i).getExpenseName()) {
                case "Food":
                    setSumExp("Food", exp, i, 0);
                    break;
                case "Travel":
                    setSumExp("Travel", exp, i, 1);
                    break;
                case "Rent":
                    setSumExp("Rent", exp, i, 2);
                    break;
                case "Gas":
                    setSumExp("Gas", exp, i, 3);
                    break;
                case "Electricity":
                    setSumExp("Electricity", exp, i, 4);
                    break;
                case "Recharge":
                    setSumExp("Recharge", exp, i, 5);
                    break;
                case "Fees":
                    setSumExp("Fees", exp, i, 6);
                    break;
                case "Subscriptions":
                    setSumExp("Subscriptions", exp, i, 7);
                    break;
                case "Health Care":
                    setSumExp("Health Care", exp, i, 8);
                    break;
                case "Bills":
                    setSumExp("Bills", exp, i, 9);
                    break;
                default:
                    break;
            }
        }
    }

    private void setSumExp(String ExpName,List<expEntity> exp, int ExpIndex, int SumIndex){
        if (sumExp.isEmpty()) {
            sumExp.add(0, exp.get(ExpIndex));
        } else {
            expEntity e = new expEntity();
            e.setDate(exp.get(ExpIndex).getDate());
            e.setExpenseAmt(sumExp.get(SumIndex).getExpenseAmt() + exp.get(ExpIndex).getExpenseAmt());
            e.setExpenseName(ExpName);
            e.setTime(exp.get(ExpIndex).getTime());
            sumExp.add(SumIndex, e);
        }
    }

    @Override
    public int getItemCount() {
        return sumExp.size();
    }

    class catHolder extends RecyclerView.ViewHolder {
        private final TextView expName, expPercent;
        private final CircularProgressIndicator progress;
        private final ImageView expIcon;

        public catHolder(@NonNull View v) {
            super(v);
            expName = v.findViewById(R.id.exp_cat);
            expIcon = v.findViewById(R.id.exp_icon);
            progress = v.findViewById(R.id.indicator);
            expPercent = v.findViewById(R.id.indicatorText);

            v.setOnClickListener(v1 -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(sumExp.get(pos));
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