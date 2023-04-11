package app.personal.Utls;

import java.util.HashMap;

import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;

public class hashUtil {

    private final HashMap<String, String> finalHash;
    private expEntity exp = new expEntity();
    private debtEntity debt = new debtEntity();
    private salaryEntity salary = new salaryEntity();

    public hashUtil(expEntity exp) {
        this.finalHash = new HashMap<>();
        this.exp = exp;
    }

    public hashUtil(debtEntity debt) {
        this.finalHash = new HashMap<>();
        this.debt = debt;
    }

    public hashUtil(salaryEntity salary) {
        this.finalHash = new HashMap<>();
        this.salary = salary;
    }

    public HashMap<String, String> getExpHashMap() {
        finalHash.clear();
        finalHash.put("id", String.valueOf(exp.getId()));
        finalHash.put("ExpenseAmt", String.valueOf(exp.getExpenseAmt()));
        finalHash.put("day", String.valueOf(exp.getDay()));
        finalHash.put("expMode",String.valueOf(exp.getExpMode()));
        finalHash.put("ExpenseName",exp.getExpenseName());
        finalHash.put("Date", exp.getDate());
        finalHash.put("Time", exp.getTime());
        return finalHash;
    }

    public HashMap<String, String> getDebtHashMap() {
        finalHash.clear();
        finalHash.put("id", String.valueOf(debt.getId()));
        finalHash.put("Source", String.valueOf(debt.getSource()));
        finalHash.put("date", String.valueOf(debt.getDate()));
        finalHash.put("finalDate",String.valueOf(debt.getFinalDate()));
        finalHash.put("status",debt.getStatus());
        finalHash.put("Amount", String.valueOf(debt.getAmount()));
        finalHash.put("isRepeat", String.valueOf(debt.getIsRepeat()));
        return finalHash;
    }

    public HashMap<String, String> getSalaryHashMap() {
        finalHash.clear();
        finalHash.put("id", String.valueOf(salary.getId()));
        finalHash.put("incName", salary.getIncName());
        finalHash.put("salary", String.valueOf(salary.getSalary()));
        finalHash.put("incType",String.valueOf(salary.getIncType()));
        finalHash.put("creationDate",salary.getCreationDate());
        finalHash.put("salMode", String.valueOf(salary.getSalMode()));
        return finalHash;
    }
}
