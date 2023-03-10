package app.personal.fury.ui.Adapters.mainLists;

import android.util.Log;
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
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.catHolder> {

//    private onItemClickListener listener;
    private final List<expEntity> sumExp = new ArrayList<>();
    private final List<expEntity> orgExp = new ArrayList<>();
    private final List<expEntity> food = new ArrayList<>(), travel = new ArrayList<>(),
            rent = new ArrayList<>(), gas = new ArrayList<>(), groceries = new ArrayList<>(),
            electricity = new ArrayList<>(), recharge = new ArrayList<>(), fees = new ArrayList<>(),
            subscriptions = new ArrayList<>(), health = new ArrayList<>(), bills = new ArrayList<>(),
            others = new ArrayList<>();
    private float salary;
    private int filter;
    private final int foodIndex = 0, travelIndex = 1, rentIndex = 2,
            gasIndex = 3, groceryIndex = 4 ,electricityIndex = 5, rechargeIndex = 6,
            feesIndex = 7, subsIndex = 8, healthIndex = 9, billsIndex = 10, othersIndex = 11;

    @NonNull
    @Override
    public catHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_item_category, parent, false);
        return new catHolder(itemView);
    }

    public void clear(){
        sumExp.clear();
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
    }

    @Override
    public void onBindViewHolder(@NonNull catHolder holder, int position) {
        expEntity entity = sumExp.get(position);
        holder.expName.setText(entity.getExpenseName());
        if (Commons.setProgress(sumExp.get(position).getExpenseAmt(), salary) > 100 ||
                Commons.setProgress(sumExp.get(position).getExpenseAmt(), salary) < 0) {
            clear();
            setExpes(orgExp, salary, filter);
        } else {
            String s = Commons.setProgress(sumExp.get(position).getExpenseAmt(), salary) + "%";
            holder.expPercent.setText(s);
        }

        switch (entity.getExpenseName()) {
            case Constants.Food:
                holder.expIcon.setImageResource(R.drawable.cat_icon_food);
                break;
            case Constants.Travel:
                holder.expIcon.setImageResource(R.drawable.cat_icon_travel);
                break;
            case Constants.Rent:
                holder.expIcon.setImageResource(R.drawable.cat_icon_rent);
                break;
            case Constants.Gas:
                holder.expIcon.setImageResource(R.drawable.cat_icon_gas);
                break;
            case Constants.Groceries:
                holder.expIcon.setImageResource(R.drawable.cat_icon_grocery);
                break;
            case Constants.Electricity:
                holder.expIcon.setImageResource(R.drawable.cat_icon_electricity);
                break;
            case Constants.Recharge:
                holder.expIcon.setImageResource(R.drawable.cat_icon_recharge);
                break;
            case Constants.Fees:
                holder.expIcon.setImageResource(R.drawable.cat_icon_fees);
                break;
            case Constants.Subscriptions:
                holder.expIcon.setImageResource(R.drawable.cat_icon_subscription);
                break;
            case Constants.Health_Care:
                holder.expIcon.setImageResource(R.drawable.cat_icon_health);
                break;
            case Constants.Bills:
                holder.expIcon.setImageResource(R.drawable.cat_icon_bill);
                break;
            case Constants.OTHERS:
//                Change this in expAdapter and budgetAdapter..
                holder.expIcon.setImageResource(R.drawable.cat_icon_otherexp);
                break;
            default:
                break;
        }
    }

    public void setExpes(List<expEntity> exp, float salary, int filter) {
//        0-today, 1-month, 2-year
        clear();
        setDefaultList();
        this.salary = salary;
        this.filter = filter;
        orgExp.clear();
        orgExp.addAll(exp);

        Thread t = new Thread(() -> {
            for (int i = 0; i < exp.size(); i++) {
//            Checks if exp date = current date
                if (filter == 0 && exp.get(i).getDate().equals(Commons.getDate())) {
                    setExpMerge(exp.get(i));

                } else if (filter == 1) {
                    String[] itemDate = exp.get(i).getDate().split("/");
                    int itemMonth = Integer.parseInt(itemDate[1]);
                    int currentMonth = Integer.parseInt(Commons.getMonth());
//                    Checks for same month in dataset
                    if (itemMonth == currentMonth) {
                        setExpMerge(exp.get(i));
                    }

                } else if (filter == 2) {
                    String[] itemDate = exp.get(i).getDate().split("/");
                    int itemYear = Integer.parseInt(itemDate[2]);
                    int currentYear = Integer.parseInt(Commons.getYear());
//                    Checks for same year in dataset
                    if (itemYear == currentYear) {
                        setExpMerge(exp.get(i));
                    }
                }
            }
        });
        t.start();
        int i = 0;
        while (t.getState() == Thread.State.NEW || t.getState() == Thread.State.RUNNABLE) {
            while ((t.getState() != Thread.State.NEW ||
                    t.getState() != Thread.State.RUNNABLE ||
                    t.getState() == Thread.State.TERMINATED) && i == 0) {
                try {
                    this.notifyDataSetChanged();
                } catch (Exception e) {
                    clear();
                    setExpes(exp, salary, filter);
                }
                i++;
            }
        }
    }

    private void setExpMerge(expEntity exp) {
        switch (exp.getExpenseName()) {
            case Constants.Food:
                food.add(exp);
                merge(food, Constants.Food);
                break;
            case Constants.Travel:
                travel.add(exp);
                merge(travel, Constants.Travel);
                break;
            case Constants.Rent:
                rent.add(exp);
                merge(rent, Constants.Rent);
                break;
            case Constants.Gas:
                gas.add(exp);
                merge(gas, Constants.Gas);
                break;
            case Constants.Groceries:
                groceries.add(exp);
                merge(groceries, Constants.Groceries);
                break;
            case Constants.Electricity:
                electricity.add(exp);
                merge(electricity, Constants.Electricity);
                break;
            case Constants.Recharge:
                recharge.add(exp);
                merge(recharge, Constants.Recharge);
                break;
            case Constants.Fees:
                fees.add(exp);
                merge(fees, Constants.Fees);
                break;
            case Constants.Subscriptions:
                subscriptions.add(exp);
                merge(subscriptions, Constants.Subscriptions);
                break;
            case Constants.Health_Care:
                health.add(exp);
                merge(health, Constants.Health_Care);
                break;
            case Constants.Bills:
                bills.add(exp);
                merge(bills, Constants.Bills);
                break;
            case Constants.OTHERS:
                others.add(exp);
                merge(others, Constants.OTHERS);
                break;
            default:
                break;
        }
    }

    private void setDefaultList() {
        sumExp.add(foodIndex, defaultExp(Constants.Food));
        sumExp.add(travelIndex, defaultExp(Constants.Travel));
        sumExp.add(rentIndex, defaultExp(Constants.Rent));
        sumExp.add(gasIndex, defaultExp(Constants.Gas));
        sumExp.add(groceryIndex, defaultExp(Constants.Groceries));
        sumExp.add(electricityIndex, defaultExp(Constants.Electricity));
        sumExp.add(rechargeIndex, defaultExp(Constants.Recharge));
        sumExp.add(feesIndex, defaultExp(Constants.Fees));
        sumExp.add(subsIndex, defaultExp(Constants.Subscriptions));
        sumExp.add(healthIndex, defaultExp(Constants.Health_Care));
        sumExp.add(billsIndex, defaultExp(Constants.Bills));
        sumExp.add(othersIndex, defaultExp(Constants.OTHERS));
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
            try {
                total = total + list.get(i).getExpenseAmt();
            } catch (Exception e) {
                clear();
                setExpes(orgExp, salary, filter);
                break;
            }
        }
        expEntity exp = new expEntity();
        exp.setExpenseAmt(total);
        exp.setTime("Null");
        exp.setDate("Null");
        exp.setExpenseName(expName);
        switch (expName) {
            case Constants.Food:
                try {
                    sumExp.remove(foodIndex);
                    sumExp.add(foodIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }
                break;
            case Constants.Travel:
                try {
                    sumExp.remove(travelIndex);
                    sumExp.add(travelIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            case Constants.Rent:
                try {
                    sumExp.remove(rentIndex);
                    sumExp.add(rentIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            case Constants.Gas:
                try {
                    sumExp.remove(gasIndex);
                    sumExp.add(gasIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;

            case Constants.Groceries:
                try {
                    sumExp.remove(groceryIndex);
                    sumExp.add(groceryIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            case Constants.Electricity:
                try {
                    sumExp.remove(electricityIndex);
                    sumExp.add(electricityIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            case Constants.Recharge:
                try {
                    sumExp.remove(rechargeIndex);
                    sumExp.add(rechargeIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            case Constants.Fees:
                try {
                    sumExp.remove(feesIndex);
                    sumExp.add(feesIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            case Constants.Subscriptions:
                try {
                    sumExp.remove(subsIndex);
                    sumExp.add(subsIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            case Constants.Health_Care:
                try {
                    sumExp.remove(healthIndex);
                    sumExp.add(healthIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            case Constants.Bills:
                try {
                    sumExp.remove(billsIndex);
                    sumExp.add(billsIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            case Constants.OTHERS:
                try {
                    sumExp.remove(othersIndex);
                    sumExp.add(othersIndex, exp);
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultList();
                }

                break;
            default:
                Log.e("Merge", "WTF");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return sumExp.size();
    }

    static class catHolder extends RecyclerView.ViewHolder {
        private final TextView expName,expPercent;
        private final ImageView expIcon;

        public catHolder(@NonNull View v) {
            super(v);
            expName = v.findViewById(R.id.exp_cat);
            expIcon = v.findViewById(R.id.exp_icon);
            expPercent = v.findViewById(R.id.indicatorText);

//            v.setOnClickListener(v1 -> {
//                int pos = getAdapterPosition();
//                if (listener != null && pos != RecyclerView.NO_POSITION) {
//                    listener.onItemClick(sumExp.get(pos));
//                }
//            });
        }
    }
//
//    public interface onItemClickListener {
//        void onItemClick(expEntity exp);
//    }
//    public void setOnItemClickListener(onItemClickListener listener) {
//        this.listener = listener;
//    }
}