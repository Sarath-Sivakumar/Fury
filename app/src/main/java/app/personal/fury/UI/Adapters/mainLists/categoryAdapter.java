package app.personal.fury.UI.Adapters.mainLists;

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
import app.personal.fury.R;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.catHolder> {

    private onItemClickListener listener;
    private final List<expEntity> sumExp = new ArrayList<>();
    private final List<expEntity> orgExp = new ArrayList<>();
    private final List<expEntity> food = new ArrayList<>(), travel = new ArrayList<>(),
            rent = new ArrayList<>(), gas = new ArrayList<>(), electricity = new ArrayList<>(),
            recharge = new ArrayList<>(), fees = new ArrayList<>(), subscriptions = new ArrayList<>(),
            health = new ArrayList<>(), bills = new ArrayList<>();
    private float salary;
    private int filter;
    private int foodIndex = 0, travelIndex = 1, rentIndex = 2, gasIndex = 3, electricityIndex = 4, rechargeIndex = 5,
            feesIndex = 6, subsIndex = 7, healthIndex = 8, billsIndex = 9;

    @NonNull
    @Override
    public catHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new catHolder(itemView);
    }

    public void clear(){
        sumExp.clear();
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
            case "Food":
                food.add(exp);
                merge(food, "Food");
                break;
            case "Travel":
                travel.add(exp);
                merge(travel, "Travel");
                break;
            case "Rent":
                rent.add(exp);
                merge(rent, "Rent");
                break;
            case "Gas":
                gas.add(exp);
                merge(gas, "Gas");
                break;
            case "Electricity":
                electricity.add(exp);
                merge(electricity, "Electricity");
                break;
            case "Recharge":
                recharge.add(exp);
                merge(recharge, "Recharge");
                break;
            case "Fees":
                fees.add(exp);
                merge(fees, "Fees");
                break;
            case "Subscriptions":
                subscriptions.add(exp);
                merge(subscriptions, "Subscriptions");
                break;
            case "Health Care":
                health.add(exp);
                merge(health, "Health Care");
                break;
            case "Bills":
                bills.add(exp);
                merge(bills, "Bills");
                break;
            default:
                break;
        }
    }

    private void setDefaultList() {
        sumExp.add(foodIndex, defaultExp("Food"));
        sumExp.add(travelIndex, defaultExp("Travel"));
        sumExp.add(rentIndex, defaultExp("Rent"));
        sumExp.add(gasIndex, defaultExp("Gas"));
        sumExp.add(electricityIndex, defaultExp("Electricity"));
        sumExp.add(rechargeIndex, defaultExp("Recharge"));
        sumExp.add(feesIndex, defaultExp("Fees"));
        sumExp.add(subsIndex, defaultExp("Subscriptions"));
        sumExp.add(healthIndex, defaultExp("Health Care"));
        sumExp.add(billsIndex, defaultExp("Bills"));
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
        if (expName.equals("Food")) {
            try {
                sumExp.remove(foodIndex);
                sumExp.add(foodIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }
        } else if (expName.equals("Travel")) {
            try {
                sumExp.remove(travelIndex);
                sumExp.add(travelIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }

        } else if (expName.equals("Rent")) {
            try {
                sumExp.remove(rentIndex);
                sumExp.add(rentIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }

        } else if (expName.equals("Gas")) {
            try {
                sumExp.remove(gasIndex);
                sumExp.add(gasIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }

        } else if (expName.equals("Electricity")) {
            try {
                sumExp.remove(electricityIndex);
                sumExp.add(electricityIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }

        } else if (expName.equals("Recharge")) {
            try {
                sumExp.remove(rechargeIndex);
                sumExp.add(rechargeIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }

        } else if (expName.equals("Fees")) {
            try {
                sumExp.remove(feesIndex);
                sumExp.add(feesIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }

        } else if (expName.equals("Subscriptions")) {
            try {
                sumExp.remove(subsIndex);
                sumExp.add(subsIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }

        } else if (expName.equals("Health Care")) {
            try {
                sumExp.remove(healthIndex);
                sumExp.add(healthIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }

        } else if (expName.equals("Bills")) {
            try {
                sumExp.remove(billsIndex);
                sumExp.add(billsIndex, exp);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultList();
            }

        } else {
            Log.e("Merge", "WTF");
        }
    }

    @Override
    public int getItemCount() {
        return sumExp.size();
    }

    class catHolder extends RecyclerView.ViewHolder {
        private final TextView expName,expPercent;
        private final ImageView expIcon;

        public catHolder(@NonNull View v) {
            super(v);
            expName = v.findViewById(R.id.exp_cat);
            expIcon = v.findViewById(R.id.exp_icon);
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
}