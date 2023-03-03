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
            dbName = "Fury_Database",
            userTable = "User_Table";
    //DO NOT CHANGE----------------------------------------------------
    public static final int DB_LATEST_VERSION = 10;
    //-------------------------------------------------------------
    public static final String Dues = "Dues And Debt",
            Exp = "Expense Tracker",
            Earnings = "Earnings Tracker",
            main = "Fury",
            Budget = "Budgets",
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
            Groceries = "Groceries",
            Bills = "Bills",
            OTHERS = "Others",
    //    Firebase------------------------------------
    DEFAULT_DP = "Default_DP",
            Users = "USERS",
            Metadata = "METADATA",
            ExpensesData = "EXPENSES",
            DuesData = "DUES",
            EarningsData = "EARNINGS",
            BudgetData = "BUDGET",
            ExpFirebaseService = "expense";
    //Internal-------------------------------------------------------
    public static final int itemDelete = 0,
            itemAdd = 1,
            itemPaid = 2,
            LIMITER_MAX = 100,
    //    Earnings-------------------------------------------
    oneTime = 2,
            monthly = 0,
            daily = 1,
            hourly = -1,
    //    ---------------------------------------------------
    TOP_CAT_DISPLAY_LIMIT = 12,
            SAL_MODE_CASH = 0,
            SAL_MODE_ACC = 1,
            BUDGET_MONTHLY = 1,
            BUDGET_WEEKLY = 0,
    //Firebase-------------------------------------------------------
//    ---------------------------------------------------------------
//    DueWarning-----------------------------------------
    SHOW_WARNING_BY_IN_APP = 4,
            SHOW_WARNING_BY_PUSH_NOTIFICATIONS = 7;
    public static final String DB_INSTANCE = "https://fury-d3622-default-rtdb.asia-southeast1.firebasedatabase.app";
}