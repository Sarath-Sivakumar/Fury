package app.personal.fury.UI.Adapters.dueList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.debtEntity;
import app.personal.fury.R;

public class dueAdapter extends RecyclerView.Adapter<dueAdapter.expHolder> {
    private List<debtEntity> debt = new ArrayList<>();
    private onItemClickListener listener;
    private boolean filter;
    private Float totalSum = 0.00F;

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
        debtEntity entity = debt.get(position);
//        holder.expAmt.setText();
        if (filter) {

        }
    }

    @Override
    public int getItemCount() {
        return debt.size();
    }

    public Float getTotalDebt() {
        return totalSum;
    }

    public void setDebt(List<debtEntity> debt, boolean filter) {
        this.filter = filter;
        this.debt = debt;
        int size = debt.size();
        totalSum = 0F;
        for (int i = 0; i < size; i++) {
            totalSum = totalSum + debt.get(i).getAmount();
        }
        notifyDataSetChanged();
    }

    public void clear() {
        debt.clear();
    }

    public List<debtEntity> getDebtList() {
        return new ArrayList<debtEntity>(debt);
    }

    public debtEntity getDebtAt(int position) {
        return debt.get(position);
    }

    class expHolder extends RecyclerView.ViewHolder {
        private final TextView dAmt, dStatus, dFinalDate, dPaidDateTitle, dPaidDate, icoText;
        private Button mark;


        public expHolder(@NonNull View itemView) {
            super(itemView);
            dAmt = itemView.findViewById(R.id.dAmt);
            dStatus = itemView.findViewById(R.id.dStatus);
            dFinalDate = itemView.findViewById(R.id.finalDate);
            dPaidDate = itemView.findViewById(R.id.paidDate);
            dPaidDateTitle = itemView.findViewById(R.id.paidDateTitle);
            icoText = itemView.findViewById(R.id.icoText);
            mark = itemView.findViewById(R.id.mark);

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