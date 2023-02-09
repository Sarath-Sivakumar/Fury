package app.personal.Utls;

//Use only for global constants
public class Constants {

    //Architecture Constants do not change-------------------------
    public static final String balanceTable = "Balance_Table",
            expTable = "Exp_Table",
            salaryTable = "Salary_Table",
            debtTable = "Debt_Table",
            budgetTable = "Budget_Table",
            inHandBalTable = "In_Hand_Bal_Table",
            dbName = "Fury_Database";
    public static final int DB_LATEST_VERSION = 2;
    //--------------------------------------------------------------
    public static final String Dues = "Dues",
            Exp = "Expenses",
            Salary = "Salary",
            main = "Fury",
<<<<<<< HEAD

            home = "Home",

            income = "Income",

            budget = "Budget",

=======
            Budget = "Budget",
>>>>>>> bea2219850e962411c48c0c8fe3494c08bffdd02
            EXP_NAME = "EXPENSE_NAME",
            EXP_AMT = "EXPENSE_AMOUNT",
            EXP_DATE = "EXPENSE_DATE",
            EXP_TIME = "EXPENSE_TIME",
            EXP_DAY = "EXPENSE_DAY",
            RUPEE = "₹",
            DEBT_PAID = "Paid",
            DEBT_NOT_PAID = "Not Paid",
            dAvgNoData = "Collecting data!",
            Food = "Food",
            Travel = "Travel",
            Rent = "Rent",
            Gas = "Gas",
            Electricity = "Electricity",
            Recharge = "Recharge",
            Fees = "Fees",
            Subscriptions = "Subscriptions",
            Health_Care = "Health Care",
            Bills = "Bills";
    //Internal-------------------------------------------------------
    public static final int itemDelete = 0,
            itemAdd = 1,
            itemPaid = 2,
            LIMITER_MAX = 100,
            oneTime = 2,
            monthly = 0,
            daily = 1,
            hourly = -1;
    //Firebase-------------------------------------------------------
    public static final String DB_INSTANCE = "https://fury-d3622-default-rtdb.asia-southeast1.firebasedatabase.app";
}
