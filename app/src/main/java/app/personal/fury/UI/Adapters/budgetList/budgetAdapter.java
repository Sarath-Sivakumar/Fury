package app.personal.fury.UI.Adapters.budgetList;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.personal.MVVM.Entity.expEntity;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class budgetAdapter extends RecyclerView.Adapter<budgetAdapter.topCatHolder> {

    private final List<expEntity> allCategory = new ArrayList<>(), toDisplay = new ArrayList<>();
    private final List<expEntity> food = new ArrayList<>(), travel = new ArrayList<>(),
            rent = new ArrayList<>(), gas = new ArrayList<>(), groceries = new ArrayList<>(),
            electricity = new ArrayList<>(), recharge = new ArrayList<>(), fees = new ArrayList<>(),
            subscriptions = new ArrayList<>(), health = new ArrayList<>(), bills = new ArrayList<>();

    @NonNull
    @Override
    public topCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exp_list_item, parent, false);
        return new topCatHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull topCatHolder holder, int position) {
        expEntity currentExp = toDisplay.get(position);
        String DisplayAmt = "+" + Constants.RUPEE + currentExp.getExpenseAmt();
        holder.amt.setText(DisplayAmt);
        holder.name.setText(currentExp.getExpenseName());

        switch (currentExp.getExpenseName()) {
            case "Food":
                holder.ico.setImageResource(R.drawable.fastfood);
                break;
            case "Travel":
                holder.ico.setImageResource(R.drawable.destination);
                break;
            case "Rent":
                holder.ico.setImageResource(R.drawable.rent);
                break;
            case "Gas":
                holder.ico.setImageResource(R.drawable.gaspump);
                break;
            case "Groceries":
                holder.ico.setImageResource(R.drawable.grocery);
                break;
            case "Electricity":
                holder.ico.setImageResource(R.drawable.electricalenergy);
                break;
            case "Recharge":
                holder.ico.setImageResource(R.drawable.recharge);
                break;
            case "Fees":
                holder.ico.setImageResource(R.drawable.fees);
                break;
            case "Subscriptions":
                holder.ico.setImageResource(R.drawable.subscription);
                break;
            case "Health Care":
                holder.ico.setImageResource(R.drawable.healthcare);
                break;
            case "Bills":
                holder.ico.setImageResource(R.drawable.bill);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return toDisplay.size();
    }

    public void clear(){
        allCategory.clear();
        toDisplay.clear();
        food.clear();
        travel.clear();
        rent.clear();
        gas.clear();
        groceries.clear();
        electricity.clear();
        recharge.clear();
        fees.clear();
        subscriptions.clear();
        health.clear();
        bills.clear();
        notifyDataSetChanged();
    }

    public void setExp(List<expEntity> allExp) {
        for (int i = 0; i < allExp.size(); i++) {
            switch (allExp.get(i).getExpenseName()){
                case "Food":
                    food.add(allExp.get(i));
                    break;
                case "Travel":
                    travel.add(allExp.get(i));
                    break;
                case "Rent":
                    rent.add(allExp.get(i));
                    break;
                case "Gas":
                    gas.add(allExp.get(i));
                    break;
                case "Groceries":
                    groceries.add(allExp.get(i));
                    break;
                case "Electricity":
                    electricity.add(allExp.get(i));
                    break;
                case "Recharge":
                    recharge.add(allExp.get(i));
                    break;
                case "Fees":
                    fees.add(allExp.get(i));
                    break;
                case "Subscriptions":
                    subscriptions.add(allExp.get(i));
                    break;
                case "Health Care":
                    health.add(allExp.get(i));
                    break;
                case "Bills":
                    bills.add(allExp.get(i));
                    break;
                default:
                    break;
            }
        }
        if (!food.isEmpty()) {
            expEntity Food = compressor(food);
            allCategory.add(Food);
        }
        if (!travel.isEmpty()) {
            expEntity Travel = compressor(travel);
            allCategory.add(Travel);
        }
        if (!rent.isEmpty()) {
            expEntity Rent = compressor(rent);
            allCategory.add(Rent);
        }
        if (!gas.isEmpty()) {
            expEntity Gas = compressor(gas);
            allCategory.add(Gas);
        }
        if (!electricity.isEmpty()) {
            expEntity Electricity = compressor(electricity);
            allCategory.add(Electricity);
        }
        if (!recharge.isEmpty()) {
            expEntity Recharge = compressor(recharge);
            allCategory.add(Recharge);
        }
        if (!fees.isEmpty()) {
            expEntity Fees = compressor(fees);
            allCategory.add(Fees);
        }
        if (!subscriptions.isEmpty()) {
            expEntity Subscriptions = compressor(subscriptions);
            allCategory.add(Subscriptions);
        }
        if (!health.isEmpty()) {
            expEntity Health_Care = compressor(health);
            allCategory.add(Health_Care);
        }
        if (!groceries.isEmpty()) {
            expEntity Groceries = compressor(groceries);
            allCategory.add(Groceries);
        }
        if (!bills.isEmpty()) {
            expEntity Bills = compressor(bills);
            allCategory.add(Bills);
        }

        List<expEntity> expEntityList = allCategory;
        expEntityList.sort(new compareByAmt());
        toDisplay.clear();
        if (expEntityList.size()>Constants.TOP_CAT_DISPLAY_LIMIT) {
            for (int i = 0; i<Constants.TOP_CAT_DISPLAY_LIMIT;i++){
                toDisplay.add(expEntityList.get(i));
            }
        }else{
            toDisplay.addAll(expEntityList);
        }
        notifyDataSetChanged();
    }

    private expEntity compressor(List<expEntity> e){
        expEntity exp = new expEntity();
        exp.setDate("");
        exp.setDay(0);
        exp.setExpenseName(e.get(0).getExpenseName());
        exp.setTime("");
        int total = 0;
        for (int i = 0; i<e.size();i++){
            total = total + e.get(i).getExpenseAmt();
        }
        exp.setExpenseAmt(total);
        return exp;
    }

    static class topCatHolder extends RecyclerView.ViewHolder {

        private final TextView amt, name;
        private final ImageView ico;

        public topCatHolder(@NonNull View itemView) {
            super(itemView);
            amt = itemView.findViewById(R.id.itemAmt);
            name = itemView.findViewById(R.id.itemTitle);
            ico = itemView.findViewById(R.id.itemIcon);
        }
    }
}
