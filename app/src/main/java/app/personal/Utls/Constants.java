package app.personal.Utls;

//Use only for global constants
public class Constants {

    //Architecture Constants do not change-------------------------
    public static final String balanceTable = "Balance_Table",
            expTable = "Exp_Table",
            salaryTable = "Salary_Table",
            debtTable = "Debt_Table",
            budgetTable="Budget_Table",
            dbName = "Fury_Database";
    public static final int DB_VERSION = 1;
    //--------------------------------------------------------------
    public static final String Dues = "Dues",
            Exp = "Expenses",
            Salary = "Salary",
            main = "Fury",
            EXP_NAME = "EXPENSE_NAME",
            EXP_AMT = "EXPENSE_AMOUNT",
            EXP_DATE = "EXPENSE_DATE",
            EXP_TIME = "EXPENSE_TIME",
            EXP_DAY = "EXPENSE_DAY",
            RUPEE = "₹",
            DEBT_PAID = "Paid",
            DEBT_NOT_PAID = "Not Paid",
            DUE_SRC = "DueSource",
            DUE_AMT = "DueAmount",
            DUE_FINAL_DATE = "DueFinalDate",
            DUE_STATUS = "DueStatus",
            DUE_PAID_DATE = "DuePaidDate",
            dAvgNoData = "Collecting data!";
    //Internal-------------------------------------------------------
    public static final int itemDelete = 0,
            itemAdd = 1,
            itemPaid = 2,
            LIMITER_MAX = 100,
            oneTime = 2,
            monthly = 0,
            daily = 1,
            hourly = -1;
}
