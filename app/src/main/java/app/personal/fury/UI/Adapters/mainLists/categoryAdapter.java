package app.personal.fury.UI.Adapters.mainLists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.expEntity;
import app.personal.fury.R;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.catHolder>{

    private onItemClickListener listener;
    private List<expEntity> exp = new ArrayList<>();

    @NonNull
    @Override
    public catHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.overview_list_item, parent, false);
        return new catHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull catHolder holder, int position) {
        expEntity entity = exp.get(position);
        holder.expName.setText(entity.getExpenseName());
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

    public void setExpes(List<expEntity> exp){
        this.exp = exp;
    }

    @Override
    public int getItemCount() {
        return exp.size();
    }

    class catHolder extends RecyclerView.ViewHolder {
        private final TextView expName, expPercent;
        private CircularProgressIndicator progress;
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