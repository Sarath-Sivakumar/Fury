package app.personal.fury.UI.Adapters.mainLists;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.expEntity;
import app.personal.Utls.Commons;
import app.personal.fury.R;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.catHolder>{

    private onItemClickListener listener;
    private final List<expEntity> sumExp = new ArrayList<>();
    private final List<expEntity> orgList = new ArrayList<>();
    private final List<expEntity> food = new ArrayList<>(), travel = new ArrayList<>(),
            rent = new ArrayList<>(), gas = new ArrayList<>(), electricity = new ArrayList<>(),
            recharge = new ArrayList<>(), fees = new ArrayList<>(), subscriptions = new ArrayList<>(),
            health = new ArrayList<>(), bills = new ArrayList<>();
    private float salary;

    @NonNull
    @Override
    public catHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.overview_list_item, parent, false);
        return new catHolder(itemView);
    }

    public void clear(){
        sumExp.clear();
        orgList.clear();
        food.clear();
        travel.clear();
        rent.clear();
        gas.clear();
        electricity.clear();
        recharge.clear();
        fees.clear();
        subscriptions.clear();
        health.clear();
        bills.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull catHolder holder, int position) {
        expEntity entity = sumExp.get(position);
        holder.expName.setText(entity.getExpenseName());
//        holder.progress.setProgress(Commons.setProgress(sumExp.get(position).getExpenseAmt(), salary), true);
        String s = Commons.setProgress(sumExp.get(position).getExpenseAmt(), salary) + "%";
        holder.expPercent.setText(s);

        switch (entity.getExpenseName()) {
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
        orgList.clear();
        sumExp.clear();
        orgList.addAll(exp);
        setDefaultList();

        this.salary = salary;

        for (int i = 0; i < exp.size(); i++) {
            if (exp.get(i).getDate().equals(Commons.getDate())) {
                switch (exp.get(i).getExpenseName()) {
                    case "Food":
                        food.add(exp.get(i));
                        merge(food, "Food");
                        break;
                    case "Travel":
                        travel.add(exp.get(i));
                        merge(travel, "Travel");
                        break;
                    case "Rent":
                        rent.add(exp.get(i));
                        merge(rent, "Rent");
                        break;
                    case "Gas":
                        gas.add(exp.get(i));
                        merge(gas, "Gas");
                        break;
                    case "Electricity":
                        electricity.add(exp.get(i));
                        merge(electricity, "Electricity");
                        break;
                    case "Recharge":
                        recharge.add(exp.get(i));
                        merge(recharge, "Recharge");
                        break;
                    case "Fees":
                        fees.add(exp.get(i));
                        merge(fees, "Fees");
                        break;
                    case "Subscriptions":
                        subscriptions.add(exp.get(i));
                        merge(subscriptions, "Subscriptions");
                        break;
                    case "Health Care":
                        health.add(exp.get(i));
                        merge(health, "Health Care");
                        break;
                    case "Bills":
                        bills.add(exp.get(i));
                        merge(bills, "Bills");
                        break;
                    default:
                        break;
                }
            }
        }
        this.notifyDataSetChanged();
    }

    private void setDefaultList() {
        sumExp.add(0, defaultExp("Bills"));
        sumExp.add(1, defaultExp("Health Care"));
        sumExp.add(2, defaultExp("Subscriptions"));
        sumExp.add(3, defaultExp("Fees"));
        sumExp.add(4, defaultExp("Recharge"));
        sumExp.add(5, defaultExp("Electricity"));
        sumExp.add(6, defaultExp("Gas"));
        sumExp.add(7, defaultExp("Rent"));
        sumExp.add(8, defaultExp("Travel"));
        sumExp.add(9, defaultExp("Food"));
    }

    private expEntity defaultExp(String Name) {
        expEntity exp = new expEntity();
        exp.setExpenseName(Name);
        exp.setExpenseAmt(0);
        exp.setTime("NULL");
        exp.setDate("NULL");
        return exp;
    }

    private void merge(List<expEntity> list, String expName) {
        int total = 0;
        for (int i = 0; i < list.size(); i++) {
            total = total + list.get(i).getExpenseAmt();
        }
        expEntity exp = new expEntity();
        exp.setExpenseAmt(total);
        exp.setTime("Null");
        exp.setDate("Null");
        exp.setExpenseName(expName);
        if (expName.equals("Food")) {
            try {
                sumExp.remove(9);
                sumExp.add(9, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }
        }else if (expName.equals("Travel")){
            try{
                sumExp.remove(8);
                sumExp.add(8, exp);
            }catch (Exception e){
                e.printStackTrace();
                setDefaultList();
            }

        }else if (expName.equals("Rent")){
            try{
                sumExp.remove(7);
                sumExp.add(7, exp);
            }catch (Exception e){
                e.printStackTrace();
                setDefaultList();
            }

        }else if (expName.equals("Gas")){
            try{
                sumExp.remove(6);
                sumExp.add(6, exp);
            }catch (Exception e){
                e.printStackTrace();
                setDefaultList();
            }

        }else if (expName.equals("Electricity")){
            try{
                sumExp.remove(5);
                sumExp.add(5, exp);
            }catch (Exception e){
                e.printStackTrace();
                setDefaultList();
            }

        }else if (expName.equals("Recharge")){
            try{
                sumExp.remove(4);
                sumExp.add(4, exp);
            }catch (Exception e){
                e.printStackTrace();
                setDefaultList();
            }

        }else if (expName.equals("Fees")){
            try{
                sumExp.remove(3);
                sumExp.add(3, exp);
            }catch (Exception e){
                e.printStackTrace();
                setDefaultList();
            }

        }else if (expName.equals("Subscriptions")){
            try{
                sumExp.remove(2);
                sumExp.add(2, exp);
            }catch (Exception e){
                e.printStackTrace();
                setDefaultList();
            }

        }else if (expName.equals("Health Care")){
            try{
                sumExp.remove(1);
                sumExp.add(1, exp);
            }catch (Exception e){
                e.printStackTrace();
                setDefaultList();
            }

        }else if (expName.equals("Bills")){
            try{
                sumExp.remove(0);
                sumExp.add(0, exp);
            }catch (Exception e){
                e.printStackTrace();
                setDefaultList();
            }

        }else{
            Log.e("Merge", "WTF");
        }
    }

    @Override
    public int getItemCount() {
        return sumExp.size();
    }

    class catHolder extends RecyclerView.ViewHolder {
        private final TextView expName,expPercent;
//        private final LinearProgressIndicator progress;
        private final ImageView expIcon;

        public catHolder(@NonNull View v) {
            super(v);
            expName = v.findViewById(R.id.exp_cat);
            expIcon = v.findViewById(R.id.exp_icon);
//            progress = v.findViewById(R.id.indicator);
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

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void setColor(LinearProgressIndicator indicator){
//        int progress = Commons.setProgress(salary,);
//        indicator.setProgress(progress, true);
//        if (progress<34){
//            indicator.setIndicatorColor(Color.GREEN);
//        }else if (progress<67){
//            indicator.setIndicatorColor(Color.YELLOW);
//        }else{
//            indicator.setIndicatorColor(Color.RED);
//        }
//    }
}