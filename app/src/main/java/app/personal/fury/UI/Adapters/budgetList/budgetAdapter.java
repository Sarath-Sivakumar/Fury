package app.personal.fury.UI.Adapters.budgetList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.personal.MVVM.Entity.expEntity;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class budgetAdapter extends RecyclerView.Adapter<budgetAdapter.topCatHolder> {

    private final List<expEntity> allCategory = new ArrayList<>(), toDisplay = new ArrayList<>();
    private final List<expEntity> food = new ArrayList<>(), travel = new ArrayList<>(),
            rent = new ArrayList<>(), gas = new ArrayList<>(), groceries = new ArrayList<>(),
            electricity = new ArrayList<>(), recharge = new ArrayList<>(), fees = new ArrayList<>(),
            subscriptions = new ArrayList<>(), health = new ArrayList<>(), bills = new ArrayList<>(),
            others = new ArrayList<>();

    @NonNull
    @Override
    public topCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.top_catlist_item, parent, false);
        return new topCatHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull topCatHolder holder, int position) {
        expEntity currentExp = toDisplay.get(position);
        String DisplayAmt = Constants.RUPEE + currentExp.getExpenseAmt();
        holder.amt.setText(DisplayAmt);
        holder.name.setText(currentExp.getExpenseName());

        switch(currentExp.getExpenseName()){
            case Constants.Food:
                holder.ico.setImageResource(R.drawable.cat_icon_food);
                break;
            case Constants.Travel:
                holder.ico.setImageResource(R.drawable.cat_icon_travel);
                break;
            case Constants.Rent:
                holder.ico.setImageResource(R.drawable.cat_icon_rent);
                break;
            case Constants.Gas:
                holder.ico.setImageResource(R.drawable.cat_icon_gas);
                break;
            case Constants.Groceries:
                holder.ico.setImageResource(R.drawable.cat_icon_grocery);
                break;
            case Constants.Electricity:
                holder.ico.setImageResource(R.drawable.cat_icon_electricity);
                break;
            case Constants.Recharge:
                holder.ico.setImageResource(R.drawable.cat_icon_recharge);
                break;
            case Constants.Fees:
                holder.ico.setImageResource(R.drawable.cat_icon_fees);
                break;
            case Constants.Subscriptions:
                holder.ico.setImageResource(R.drawable.cat_icon_subscription);
                break;
            case Constants.Health_Care:
                holder.ico.setImageResource(R.drawable.cat_icon_health);
                break;
            case Constants.Bills:
                holder.ico.setImageResource(R.drawable.cat_icon_bill);
                break;
            case Constants.OTHERS:
                holder.ico.setImageResource(R.drawable.nav_icon_settings);
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
        others.clear();
        notifyDataSetChanged();
    }

    public void setExp(List<expEntity> allExp) {
        for (int i = 0; i < allExp.size(); i++) {
            switch (allExp.get(i).getExpenseName()){
                case Constants.Food:
                    food.add(allExp.get(i));
                    break;
                case Constants.Travel:
                    travel.add(allExp.get(i));
                    break;
                case Constants.Rent:
                    rent.add(allExp.get(i));
                    break;
                case Constants.Gas:
                    gas.add(allExp.get(i));
                    break;
                case Constants.Groceries:
                    groceries.add(allExp.get(i));
                    break;
                case Constants.Electricity:
                    electricity.add(allExp.get(i));
                    break;
                case Constants.Recharge:
                    recharge.add(allExp.get(i));
                    break;
                case Constants.Fees:
                    fees.add(allExp.get(i));
                    break;
                case Constants.Subscriptions:
                    subscriptions.add(allExp.get(i));
                    break;
                case Constants.Health_Care:
                    health.add(allExp.get(i));
                    break;
                case Constants.Bills:
                    bills.add(allExp.get(i));
                    break;
                case Constants.OTHERS:
                    others.add(allExp.get(i));
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
        if (!others.isEmpty()) {
            expEntity other = compressor(others);
            allCategory.add(other);
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
