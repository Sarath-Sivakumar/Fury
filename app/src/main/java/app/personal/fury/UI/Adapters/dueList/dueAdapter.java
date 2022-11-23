package app.personal.fury.UI.Adapters.dueList;

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
    private List<debtEntity> debt = new ArrayList<>();
    private onItemClickListener listener;
    private int count = 0;
    private int totalSum = 0;

    @NonNull
    @Override
    public expHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.due_item, parent, false);
        return new expHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull expHolder holder, int position) {
        debtEntity entity = debt.get(position);
        holder.dPaidDate.setText(entity.getDate());
        holder.dPaidDateTitle.setVisibility(View.GONE);
        holder.dPaidDate.setVisibility(View.GONE);
        String amt = Constants.RUPEE + (int) entity.getAmount();
        holder.dAmt.setText(amt);
        holder.dName.setText(entity.getSource());

        //To get first letter in source name--------------------------------------
        String ico = String.valueOf(entity.getSource().charAt(0)).toUpperCase();
        holder.icoText.setText(ico);
        //------------------------------------------------------------------------

        holder.dFinalDate.setText(entity.getFinalDate());
        holder.dStatus.setText(entity.getStatus());
    }

    @Override
    public int getItemCount() {
        return debt.size();
    }

    public int getTotalDebt() {
        return totalSum;
    }

    public void setDebt(List<debtEntity> debt) {
        int size = debt.size();
        totalSum = 0;
        for (int i = 0; i < size; i++) {
            if (debt.get(i).getStatus().equals(Constants.DEBT_NOT_PAID)) {
                totalSum = totalSum + debt.get(i).getAmount();
                this.debt.add(debt.get(i));
                count=this.debt.size();
            }
        }
        notifyDataSetChanged();
    }

    public void clear() {
        debt.clear();
        notifyDataSetChanged();
    }

    public List<debtEntity> getDebtList() {
        return new ArrayList<debtEntity>(debt);
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