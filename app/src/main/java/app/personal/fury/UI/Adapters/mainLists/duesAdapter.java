package app.personal.fury.UI.Adapters.mainLists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.debtEntity;
import app.personal.fury.R;

public class duesAdapter extends RecyclerView.Adapter<duesAdapter.dueHolder>{

    private onItemClickListener listener;
    private List<debtEntity> debt = new ArrayList<>();

    @NonNull
    @Override
    public duesAdapter.dueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.main_due_item, parent, false);
        return new duesAdapter.dueHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull duesAdapter.dueHolder holder, int position) {
        debtEntity entity = debt.get(position);
        holder.day.setText(entity.getDate());
        holder.dueName.setText(entity.getSource());
    }

    public void setDues(List<debtEntity> debt){
        this.debt = debt;
    }

    @Override
    public int getItemCount() {
        return debt.size();
    }

    class dueHolder extends RecyclerView.ViewHolder {
        private final TextView day, dueName;
        private final ImageView ico;

        public dueHolder(@NonNull View v) {
            super(v);
            day = v.findViewById(R.id.dueDays);
            dueName = v.findViewById(R.id.dueName);
            ico = v.findViewById(R.id.ico);

            v.setOnClickListener(v1 -> {
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
