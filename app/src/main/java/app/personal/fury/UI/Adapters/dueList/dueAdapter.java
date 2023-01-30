package app.personal.fury.UI.Adapters.dueList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
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
    private boolean filter;
    private Context context;
    @ColorInt
    private int colorGreen;

    @NonNull
    @Override
    public expHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.due_item1, parent, false);
        return new expHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull expHolder holder, int position) {
        debtEntity entity = debt.get(position);
        String amt = Constants.RUPEE + entity.getAmount();
        holder.dAmt.setText(amt);
        holder.dName.setText(entity.getSource());
        if (entity.getStatus().equals(Constants.DEBT_PAID)){
            holder.dFinalDate.setText(entity.getDate());
        }else {
            holder.dFinalDate.setText(entity.getFinalDate());
        }
        holder.dStatus.setText(entity.getStatus());
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

    public void setContext(Context context){
        this.context = context;
    }

    public void setDebt(List<debtEntity> debt, boolean filter) {
        int size = debt.size();
        this.filter = filter;
        totalSum = 0;
        if (filter) {
            for (int i = 0; i < size; i++) {
                if (debt.get(i).getStatus().equals(Constants.DEBT_NOT_PAID)) {
                    totalSum = totalSum + debt.get(i).getAmount();
                    this.debt.add(debt.get(i));
                }
            }
        } else {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
            colorGreen = typedValue.data;

            for (int i = 0; i < size; i++) {
                this.debt.add(debt.get(i));
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
        private final TextView dAmt, dStatus, dFinalDate,dName, icoText;

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