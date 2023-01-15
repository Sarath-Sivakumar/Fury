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
    private List<debtEntity> debt = new ArrayList<>();
    private onItemClickListener listener;
    private int totalSum = 0;
    private boolean filter;
    private TypedValue typedValue;
    private Context context;
    @ColorInt
    private int colorGreen;

    @NonNull
    @Override
    public expHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.due_item, parent, false);
        return new expHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull expHolder holder, int position) {
        debtEntity entity = debt.get(position);
        String amt = Constants.RUPEE + entity.getAmount();
        holder.dAmt.setText(amt);
        holder.dName.setText(entity.getSource());
        holder.dFinalDate.setText(entity.getFinalDate());

        //To get first letter in source name--------------------------------------
        String ico = String.valueOf(entity.getSource().charAt(0)).toUpperCase();
        holder.icoText.setText(ico);
        //------------------------------------------------------------------------

        if (!filter) {
            holder.dStatus.setText(entity.getStatus());
            if (entity.getStatus().equals(Constants.DEBT_PAID)) {
                try {
                    holder.dPaidDateTitle.setVisibility(View.VISIBLE);
                    holder.dPaidDate.setVisibility(View.VISIBLE);
                    holder.dStatus.setTextColor(colorGreen);
                    holder.dPaidDate.setTextColor(colorGreen);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                holder.dPaidDateTitle.setVisibility(View.GONE);
                holder.dPaidDate.setVisibility(View.GONE);
            }
            holder.dPaidDate.setText(entity.getDate());
        } else {
            holder.dPaidDateTitle.setVisibility(View.GONE);
            holder.dPaidDate.setVisibility(View.GONE);
            holder.dStatus.setVisibility(View.GONE);
        }
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
            typedValue = new TypedValue();
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
        private final TextView dAmt, dStatus, dFinalDate, dPaidDateTitle, dPaidDate,dName, icoText;

        public expHolder(@NonNull View itemView) {
            super(itemView);
            dAmt = itemView.findViewById(R.id.dAmt);
            dStatus = itemView.findViewById(R.id.dStatus);
            dFinalDate = itemView.findViewById(R.id.finalDate);
            dPaidDate = itemView.findViewById(R.id.paidDate);
            dPaidDateTitle = itemView.findViewById(R.id.paidDateTitle);
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