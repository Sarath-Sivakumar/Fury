package app.personal.Utls;

import java.util.HashMap;

import app.personal.MVVM.Entity.LaunchChecker;
import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;

public class hashUtil {

    private final HashMap<String, Object> finalHash;
    private expEntity exp = new expEntity();
    private debtEntity debt = new debtEntity();
    private salaryEntity salary = new salaryEntity();
    private balanceEntity bankBal = new balanceEntity();
    private inHandBalEntity inHandBal = new inHandBalEntity();
    private LaunchChecker launchChecker = new LaunchChecker();
    private budgetEntity budget = new budgetEntity();

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

    public hashUtil(balanceEntity bankBal) {
        this.finalHash = new HashMap<>();
        this.bankBal = bankBal;
    }

    public hashUtil(inHandBalEntity inHandBal) {
        this.finalHash = new HashMap<>();
        this.inHandBal = inHandBal;
    }

    public hashUtil(LaunchChecker launchChecker) {
        this.finalHash = new HashMap<>();
        this.launchChecker = launchChecker;
    }

    public hashUtil(budgetEntity budget) {
        this.finalHash = new HashMap<>();
        this.budget = budget;
    }

    public HashMap<String, Object> getExpHashMap() {
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

    public HashMap<String, Object> getDebtHashMap() {
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

    public HashMap<String, Object> getSalaryHashMap() {
        finalHash.clear();
        finalHash.put("id", String.valueOf(salary.getId()));
        finalHash.put("incName", salary.getIncName());
        finalHash.put("salary", String.valueOf(salary.getSalary()));
        finalHash.put("incType",String.valueOf(salary.getIncType()));
        finalHash.put("creationDate",salary.getCreationDate());
        finalHash.put("salMode", String.valueOf(salary.getSalMode()));
        return finalHash;
    }

    public HashMap<String, Object> getBankBalHashMap() {
        finalHash.clear();
        finalHash.put("id", String.valueOf(bankBal.getId()));
        finalHash.put("balance", String.valueOf(bankBal.getBalance()));
        return finalHash;
    }

    public HashMap<String, Object> getinHandHashMap() {
        finalHash.clear();
        finalHash.put("id", String.valueOf(inHandBal.getId()));
        finalHash.put("balance", String.valueOf(inHandBal.getBalance()));
        return finalHash;
    }

    public HashMap<String, Object> getLaunchHashMap() {
        finalHash.clear();
        finalHash.put("id", String.valueOf(launchChecker.getId()));
        finalHash.put("timesLaunched", String.valueOf(launchChecker.getTimesLaunched()));
        return finalHash;
    }

    public HashMap<String, Object> getBudgetHashMap() {
        finalHash.clear();
        finalHash.put("id", String.valueOf(budget.getId()));
        finalHash.put("Amount", String.valueOf(budget.getAmount()));
        finalHash.put("bal", String.valueOf(budget.getBal()));
        finalHash.put("refreshPeriod", String.valueOf(budget.getRefreshPeriod()));
        finalHash.put("CreationDate", String.valueOf(budget.getCreationDate()));
        return finalHash;
    }
}
