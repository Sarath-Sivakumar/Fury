package app.personal.Utls;

//Use only for global constants
public class Constants {

    //Architecture Constants do not change-------------------------
    public static final String balanceTable = "Balance_Table",
            expTable = "Exp_Table",
            salaryTable = "Salary_Table",
            debtTable = "Debt_Table",
            dbName = "Fury_Database";
    public static final int DB_VERSION = 1;
    //--------------------------------------------------------------
    public static final String Dues = "Dues & Debts",
            Exp = "Daily Expenses",
            Salary = "Salary Planner",
            EXP_NAME = "EXPENSE_NAME",
            EXP_AMT = "EXPENSE_AMOUNT",
            EXP_DATE = "EXPENSE_DATE",
            EXP_TIME = "EXPENSE_TIME",
            RUPEE = "₹";
    //Activity Log Constants-----------------------------------------
    public static final String mActivityLog = "MAIN ACTIVITY",
            expFragLog = "EXP FRAGMENT";
    //Internal Constants---------------------------------------------
    public static final int itemDelete = 0,
            itemAdd = 1,
            LIMITER_MAX = 100;
}
