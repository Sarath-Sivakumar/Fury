package app.personal.MVVM.Repository;

import static app.personal.Utls.Constants.default_Error;
import static app.personal.Utls.Constants.default_int_entity;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.personal.MVVM.Entity.LaunchChecker;
import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.Utls.Constants;
import app.personal.Utls.hashUtil;

public class dataSyncRepository {
    private final Application application;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;

    private final MutableLiveData<Boolean> bruteForceSync;

    private final MutableLiveData<String> FirebaseError;
    private final MutableLiveData<Boolean> SyncStatus;

    private final MutableLiveData<List<expEntity>> expLiveData;
    private final MutableLiveData<balanceEntity> bankBalLiveData;
    private final MutableLiveData<inHandBalEntity> inHandBalLiveData;
    private final MutableLiveData<List<debtEntity>> debtLiveData;
    private final MutableLiveData<budgetEntity> budgetLiveData;
    private final MutableLiveData<LaunchChecker> launchLiveData;
    private final MutableLiveData<List<salaryEntity>> salaryLiveData;

    private final FirebaseDatabase db = FirebaseDatabase.getInstance(Constants.DB_INSTANCE);
    private final DatabaseReference metaDataRef = db.getReference(Constants.Metadata)
            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    private final DatabaseReference expDataRef = metaDataRef.child(Constants.ExpensesData);
    private final DatabaseReference bankBalDataRef = metaDataRef.child(Constants.BankBal);
    private final DatabaseReference inHandBalDataRef = metaDataRef.child(Constants.InHandBal);
    private final DatabaseReference debtDataRef = metaDataRef.child(Constants.DuesData);
    private final DatabaseReference launchDataRef = metaDataRef.child(Constants.LaunchChecker);
    private final DatabaseReference salaryDataRef = metaDataRef.child(Constants.EarningsData);
    private final DatabaseReference budgetDataRef = metaDataRef.child(Constants.BudgetData);

    public dataSyncRepository(Application application) {
        this.application = application;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
        this.FirebaseError = new MutableLiveData<>();
        this.bruteForceSync = new MutableLiveData<>(false);

        setDefaultError();

        this.SyncStatus = new MutableLiveData<>();
        this.SyncStatus.postValue(false);

        this.expLiveData = new MutableLiveData<>();
        this.bankBalLiveData = new MutableLiveData<>();
        this.inHandBalLiveData = new MutableLiveData<>();
        this.debtLiveData = new MutableLiveData<>();
        this.launchLiveData = new MutableLiveData<>();
        this.budgetLiveData = new MutableLiveData<>();
        this.salaryLiveData = new MutableLiveData<>();

        setDefaultExp();
        setDefaultSalary();
        setDefaultDebt();
        setDefaultBankBalance();
        setDefaultInHandBalance();
        setDefaultLaunch();
        setDefaultBudget();

        fetchAll();
    }

    public void fetchAll() {
        allDataFetcher();
    }

    public void CompareExp(List<expEntity> localExp) {
        SyncStatus.postValue(false);
        if (!localExp.isEmpty() && !localExp.equals(expLiveData.getValue())) {
            Log.e("DataSync-Level3", "Uploading Expenses.");
            putExp(localExp);
            fetchExp();
        } else {
            Log.e("DataSync-Level3", "Expense data match.");
            setDefaultExp();
        }
    }

    public void CompareSalary(List<salaryEntity> localSalary) {
        if (!localSalary.isEmpty() && !localSalary.equals(salaryLiveData.getValue())) {
            Log.e("DataSync-Level3", "Uploading Salary.");
            putSalary(localSalary);
            fetchSalary();
        } else {
            Log.e("DataSync-Level3", "Salary data match.");
            setDefaultSalary();
        }
    }

    public void CompareDebt(List<debtEntity> localDebt) {
        if (!localDebt.isEmpty() && !localDebt.equals(debtLiveData.getValue())) {
            Log.e("DataSync-Level3", "Uploading Debt.");
            putDebt(localDebt);
            fetchDebt();
        } else {
            Log.e("DataSync-Level3", "Debt data match.");
            setDefaultDebt();
        }
    }

    public void CompareBankBalance(balanceEntity localBalance) {
        try {
            if (!localBalance.equals(bankBalLiveData.getValue())) {
                Log.e("DataSync-Level3", "Uploading Bank Balance.");
                putBankBalance(localBalance);
                fetchBankBal();
            } else {
                Log.e("DataSync-Level3", "Bank Balance data match.");
                setDefaultBankBalance();
            }
        } catch (Exception ignored) {
            setDefaultBankBalance();
        }
    }

    public void CompareInHandBal(inHandBalEntity localInHandBal) {
        try {
            if (!localInHandBal.equals(inHandBalLiveData.getValue())) {
                Log.e("DataSync-Level3", "Uploading In Hand Balance.");
                putInHandBalance(localInHandBal);
                fetchInHandBal();
            } else {
                Log.e("DataSync-Level3", "In Hand Balance data match.");
                setDefaultInHandBalance();
            }
        } catch (Exception ignored) {
            setDefaultInHandBalance();
        }
    }

    public void CompareLaunch(LaunchChecker localLaunchChecker) {
        try {
            if (!localLaunchChecker.equals(launchLiveData.getValue())) {
                Log.e("DataSync-Level3", "Uploading Launch Checker.");
                putLaunch(localLaunchChecker);
                fetchLaunch();
            } else {
                Log.e("DataSync-Level3", "Launch Checker data match.");
                setDefaultLaunch();
            }
        } catch (Exception ignored) {
            setDefaultLaunch();
        }
    }

    public void CompareBudget(budgetEntity localBudget) {
        try {
            if (!localBudget.equals(budgetLiveData.getValue())) {
                Log.e("DataSync-Level3", "Uploading Budget.");
                putBudget(localBudget);
            } else {
                Log.e("DataSync-Level3", "Budget data match.");
                setDefaultBudget();
            }
        } catch (Exception ignored) {
            setDefaultBudget();
        }
        SyncStatus.postValue(true);
    }

    public void CompareAll(List<expEntity> localExp, List<salaryEntity> localSalary,
                           List<debtEntity> localDebt, balanceEntity localBalance,
                           inHandBalEntity localInHandBal, LaunchChecker localLaunchChecker,
                           budgetEntity localBudget) {
        CompareExp(localExp);
        CompareSalary(localSalary);
        CompareDebt(localDebt);
        CompareBankBalance(localBalance);
        CompareInHandBal(localInHandBal);
        CompareLaunch(localLaunchChecker);
        CompareBudget(localBudget);
    }

    private void allDataFetcher() {
        fetchBankBal();
        fetchBudget();
        fetchDebt();
        fetchSalary();
        fetchExp();
        fetchInHandBal();
        fetchLaunch();
    }

    //    Fetcher
    private void fetchExp() {
        expDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<expEntity> expList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String id = snapshot.child(Objects.requireNonNull(ds.getKey())).child("id")
                                .getValue(String.class);
                        String ExpenseAmt = snapshot.child(ds.getKey()).child("ExpenseAmt")
                                .getValue(String.class);
                        String day = snapshot.child(ds.getKey()).child("day")
                                .getValue(String.class);
                        String expMode = snapshot.child(ds.getKey()).child("expMode")
                                .getValue(String.class);
                        String ExpenseName = snapshot.child(ds.getKey()).child("ExpenseName")
                                .getValue(String.class);
                        String Date = snapshot.child(ds.getKey()).child("Date")
                                .getValue(String.class);
                        String Time = snapshot.child(ds.getKey()).child("Time")
                                .getValue(String.class);
                        if (id != null && ExpenseAmt != null && day != null && expMode != null
                                && ExpenseName != null && Date != null && Time != null) {
                            expEntity exp = new expEntity(Integer.parseInt(id), Integer.parseInt(ExpenseAmt),
                                    ExpenseName, Date, Time, Integer.parseInt(day), Integer.parseInt(expMode));
                            expList.add(exp);
                        } else {
                            Log.e("DataSync-Level3", "expData: null");
                        }
                    }
                    if (expList.size() > 0) {
                        expLiveData.postValue(expList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    private void fetchSalary() {
        salaryDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<salaryEntity> salaryList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String id = snapshot.child(Objects.requireNonNull(ds.getKey())).child("id")
                                .getValue(String.class);
                        String incName = snapshot.child(Objects.requireNonNull(ds.getKey())).child("incName")
                                .getValue(String.class);
                        String salary = snapshot.child(Objects.requireNonNull(ds.getKey())).child("salary")
                                .getValue(String.class);
                        String incType = snapshot.child(Objects.requireNonNull(ds.getKey())).child("incType")
                                .getValue(String.class);
                        String creationDate = snapshot.child(Objects.requireNonNull(ds.getKey())).child("creationDate")
                                .getValue(String.class);
                        String salMode = snapshot.child(Objects.requireNonNull(ds.getKey())).child("salMode")
                                .getValue(String.class);

                        if (id != null && incName != null && salary != null && incType != null && creationDate != null && salMode != null) {
                            salaryList.add(new salaryEntity(Integer.parseInt(id), Integer.parseInt(salary),
                                    incName, Integer.parseInt(incType), creationDate, Integer.parseInt(salMode)));
                        } else {
                            Log.e("DataSync-Level3", "salaryData: null");
                        }
                    }
                    if (salaryList.size() > 0) {
                        salaryLiveData.postValue(salaryList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    private void fetchDebt() {
        debtDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<debtEntity> debtList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String id = snapshot.child(Objects.requireNonNull(ds.getKey()))
                                .child("id").getValue(String.class);
                        String Source = snapshot.child(Objects.requireNonNull(ds.getKey()))
                                .child("Source").getValue(String.class);
                        String date = snapshot.child(Objects.requireNonNull(ds.getKey()))
                                .child("date").getValue(String.class);
                        String finalDate = snapshot.child(Objects.requireNonNull(ds.getKey()))
                                .child("finalDate").getValue(String.class);
                        String status = snapshot.child(Objects.requireNonNull(ds.getKey()))
                                .child("status").getValue(String.class);
                        String Amount = snapshot.child(Objects.requireNonNull(ds.getKey()))
                                .child("Amount").getValue(String.class);
                        String isRepeat = snapshot.child(Objects.requireNonNull(ds.getKey()))
                                .child("isRepeat").getValue(String.class);
                        if (id != null && Source != null && date != null && finalDate != null && status != null
                                && Amount != null && isRepeat != null) {
                            debtEntity debt = new debtEntity(Integer.parseInt(id), Source, date, finalDate,
                                    Integer.parseInt(Amount), status, Integer.parseInt(isRepeat));
                            debtList.add(debt);
                        } else {
                            Log.e("DataSync-Level3", "debtData: null");
                        }
                    }
                    if (debtList.size() > 0) {
                        debtLiveData.postValue(debtList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    private void fetchBankBal() {
        bankBalDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String id = snapshot.child("id")
                            .getValue(String.class);
                    String balance = snapshot.child("balance")
                            .getValue(String.class);
                    if (id != null && balance != null) {
                        balanceEntity bal = new balanceEntity(Integer.parseInt(id), Integer.parseInt(balance));
                        bankBalLiveData.postValue(bal);
                    } else {
                        Log.e("DataSync-Level3", "bankBalData: null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    private void fetchInHandBal() {
        inHandBalDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String balance = snapshot.child("balance").getValue(String.class);
                    if (id != null && balance != null) {
                        inHandBalEntity inHandBal = new inHandBalEntity(Integer.parseInt(id),
                                Integer.parseInt(balance));
                        inHandBalLiveData.postValue(inHandBal);
                    } else {
                        Log.e("DataSync-Level3", "inHandBalData: null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    private void fetchLaunch() {
        launchDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String timesLaunched = snapshot.child("timesLaunched").getValue(String.class);
                    if (id != null && timesLaunched != null) {
                        LaunchChecker launchChecker = new LaunchChecker(Integer.parseInt(id),
                                Integer.parseInt(timesLaunched));
                        launchLiveData.postValue(launchChecker);
                        SyncStatus.postValue(true);
                        Log.e("DataSync-Level3", "Fetcher terminated.");
                    } else {
                        Log.e("DataSync-Level3", "launchData: null");
                    }
                }else{
                    SyncStatus.postValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    private void fetchBudget() {
        budgetDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String Amount = snapshot.child("Amount").getValue(String.class);
                    String bal = snapshot.child("bal").getValue(String.class);
                    String refreshPeriod = snapshot.child("refreshPeriod").getValue(String.class);
                    String CreationDate = snapshot.child("CreationDate").getValue(String.class);
                    if (id != null && Amount != null && bal != null &&
                            refreshPeriod != null && CreationDate != null) {
                        budgetEntity bud = new budgetEntity(Integer.parseInt(id), Integer.parseInt(Amount),
                                Integer.parseInt(bal), Integer.parseInt(refreshPeriod), CreationDate);
                        budgetLiveData.postValue(bud);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    //    Uploader
    private void putExp(List<expEntity> expEntityList) {
        removeExp();
        for (expEntity exp : expEntityList) {
            expDataRef.push().setValue(new hashUtil(exp).getExpHashMap(), (error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        }
    }

    private void putDebt(List<debtEntity> debtEntityList) {
        removeDebt();
        for (debtEntity debt : debtEntityList) {
            debtDataRef.push().setValue(new hashUtil(debt).getDebtHashMap(), (error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        }
    }

    private void putSalary(List<salaryEntity> salaryEntityList) {
        removeSalary();
        for (salaryEntity salary : salaryEntityList) {
            salaryDataRef.push().setValue(new hashUtil(salary).getSalaryHashMap(), (error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        }
    }

    private void putBankBalance(balanceEntity balance) {
        removeBankBalance();
        bankBalDataRef.setValue(new hashUtil(balance).getBankBalHashMap(), (error, ref) -> {
            if (error != null) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    private void putInHandBalance(inHandBalEntity inHandBal) {
        removeInHand();
        inHandBalDataRef.setValue(new hashUtil(inHandBal).getinHandHashMap(), (error, ref) -> {
            if (error != null) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    private void putBudget(budgetEntity budget) {
        removeBudget();
        budgetDataRef.setValue(new hashUtil(budget).getBudgetHashMap(), (error, ref) -> {
            if (error != null) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    private void putLaunch(LaunchChecker launchChecker) {
        removeLaunch();
        launchDataRef.setValue(new hashUtil(launchChecker).getLaunchHashMap(), (error, ref) -> {
            if (error != null) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    public void setDefaultError() {
        FirebaseError.postValue(default_Error);
    }

    private void removeExp() {
        try {
            expDataRef.removeValue((error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        } catch (Exception ignored) {
        }
    }

    private void removeSalary() {
        try {
            salaryDataRef.removeValue((error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        } catch (Exception ignored) {
        }
    }

    private void removeDebt() {
        try {
            debtDataRef.removeValue((error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        } catch (Exception ignored) {
        }
    }

    private void removeBankBalance() {
        try {
            bankBalDataRef.removeValue((error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        } catch (Exception ignored) {
        }
    }

    private void removeInHand() {
        try {
            inHandBalDataRef.removeValue((error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        } catch (Exception ignored) {
        }
    }

    private void removeLaunch() {
        try {
            launchDataRef.removeValue((error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        } catch (Exception ignored) {
        }
    }

    private void removeBudget() {
        try {
            budgetDataRef.removeValue((error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        } catch (Exception ignored) {
        }
    }

    //Defaults
    private void setDefaultExp() {
        List<expEntity> expList = new ArrayList<>();
        expEntity exp = new expEntity();
        exp.setDate(default_Error);
        expList.add(0, exp);
        expLiveData.postValue(expList);
    }

    private void setDefaultSalary() {
        List<salaryEntity> salaryList = new ArrayList<>();
        salaryEntity salary = new salaryEntity();
        salary.setCreationDate(default_Error);
        salaryList.add(0, salary);
        salaryLiveData.postValue(salaryList);
    }

    private void setDefaultDebt() {
        List<debtEntity> debtList = new ArrayList<>();
        debtEntity debt = new debtEntity();
        debt.setDate(default_Error);
        debtList.add(0, debt);
        debtLiveData.postValue(debtList);
    }

    private void setDefaultBankBalance() {
        balanceEntity balance = new balanceEntity();
        balance.setId(default_int_entity);
        bankBalLiveData.postValue(balance);
    }

    private void setDefaultInHandBalance() {
        inHandBalEntity balance = new inHandBalEntity();
        balance.setId(default_int_entity);
        inHandBalLiveData.postValue(balance);
    }

    private void setDefaultBudget() {
        budgetEntity bud = new budgetEntity();
        bud.setCreationDate(default_Error);
        budgetLiveData.postValue(bud);
    }

    private void setDefaultLaunch() {
        LaunchChecker launchChecker = new LaunchChecker();
        launchChecker.setId(default_int_entity);
        launchLiveData.postValue(launchChecker);
    }

    public MutableLiveData<String> getFirebaseError() {
        return FirebaseError;
    }

    public MutableLiveData<Boolean> getSyncStatus() {
        return SyncStatus;
    }

    public MutableLiveData<List<expEntity>> getExpLiveData() {
        return expLiveData;
    }

    public MutableLiveData<balanceEntity> getBankBalLiveData() {
        return bankBalLiveData;
    }

    public MutableLiveData<inHandBalEntity> getInHandBalLiveData() {
        return inHandBalLiveData;
    }

    public MutableLiveData<List<debtEntity>> getDebtLiveData() {
        return debtLiveData;
    }

    public MutableLiveData<budgetEntity> getBudgetLiveData() {
        return budgetLiveData;
    }

    public MutableLiveData<LaunchChecker> getLaunchLiveData() {
        return launchLiveData;
    }

    public MutableLiveData<List<salaryEntity>> getSalaryLiveData() {
        return salaryLiveData;
    }

    public MutableLiveData<Boolean> getBruteForceSync() {
        return bruteForceSync;
    }

    public void setBruteForceSync(boolean isBruteforce) {
        bruteForceSync.postValue(isBruteforce);
    }
}