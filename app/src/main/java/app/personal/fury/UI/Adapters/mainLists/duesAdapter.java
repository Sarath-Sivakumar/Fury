package app.personal.fury.UI.Adapters.mainLists;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import app.personal.MVVM.Entity.debtEntity;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date dateBefore = sdf.parse(Commons.getDate());
            Date dateAfter = sdf.parse(entity.getFinalDate());
            long timeDiff = Math.abs(dateAfter.getTime()-dateBefore.getTime());
            long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
            if ((int)daysDiff >= 1 && entity.getStatus().equals(Constants.DEBT_NOT_PAID)){
                holder.day.setText(String.valueOf((int) daysDiff));
                String s = Constants.RUPEE+entity.getAmount();
                holder.dueAmt.setText(s);
                holder.dueName.setText(entity.getSource());
                if (daysDiff<4){
                    holder.day.setTextColor(Color.RED);
                }else{
                    holder.day.setTextColor(Color.WHITE);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        debt.clear();
    }

    public void setDues(List<debtEntity> debt){
        int size = debt.size();
        for (int i = 0;i<size;i++){
            if (debt.get(i).getStatus().equals(Constants.DEBT_NOT_PAID)){
                this.debt.add(debt.get(i));
            }
        }
    }

    @Override
    public int getItemCount() {
        return debt.size();
    }

    class dueHolder extends RecyclerView.ViewHolder {
        private final TextView day, dueName,dueAmt;

        public dueHolder(@NonNull View v) {
            super(v);
            day = v.findViewById(R.id.dueDays);
            dueName = v.findViewById(R.id.dueName);
            dueAmt = v.findViewById(R.id.dueAmt);

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
