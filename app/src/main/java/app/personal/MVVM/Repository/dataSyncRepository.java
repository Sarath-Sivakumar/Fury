package app.personal.MVVM.Repository;

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
    private final String default_Error = "Null";
    private final int ALL_PROCESS_COMPLETE = 1;

    private final MutableLiveData<Integer> FetchProgress;
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

    private final List<debtEntity> localDebt;
    private final List<expEntity> localExp;
    private final List<salaryEntity> localSalary;
    private balanceEntity localBalance;
    private budgetEntity localBudget;
    private inHandBalEntity localInHandBal;
    private LaunchChecker localLaunchChecker;

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
    private int taskStatus = 0;

    public dataSyncRepository(Application application) {
        this.application = application;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
        this.FirebaseError = new MutableLiveData<>();
        this.bruteForceSync = new MutableLiveData<>(false);
        setDefaultError();
        this.SyncStatus = new MutableLiveData<>();
        this.SyncStatus.postValue(false);
        this.FetchProgress = new MutableLiveData<>();
        this.FetchProgress.postValue(0);

        this.expLiveData = new MutableLiveData<>();
        this.bankBalLiveData = new MutableLiveData<>();
        this.inHandBalLiveData = new MutableLiveData<>();
        this.debtLiveData = new MutableLiveData<>();
        this.launchLiveData = new MutableLiveData<>();
        this.budgetLiveData = new MutableLiveData<>();
        this.salaryLiveData = new MutableLiveData<>();

        this.localExp = new ArrayList<>();
        this.localBudget = new budgetEntity();
        this.localDebt = new ArrayList<>();
        this.localSalary = new ArrayList<>();
        this.localInHandBal = new inHandBalEntity();
        this.localBalance = new balanceEntity();
        this.localLaunchChecker = new LaunchChecker();
    }

    public void init() {
        populateValues(0);
        Log.e("DataSync-Level(1)", "Init.");
        this.FetchProgress.observeForever(processStatus -> {
            if (processStatus == ALL_PROCESS_COMPLETE && taskStatus == 0) {
                boolean brute = bruteForceSync.getValue();
                if (!brute) {
                    Log.e("DataSync-Level(1)", "CompareTheAndis in progress.");
                    CompareTheAndis();
                } else {
                    Log.e("DataSync-Level(1)", "CompareTheAndis not in progress.");
                    expLiveData.observeForever(localExp::addAll);
                    debtLiveData.observeForever(localDebt::addAll);
                    salaryLiveData.observeForever(localSalary::addAll);
                    bankBalLiveData.observeForever(balance -> localBalance = balance);
                    inHandBalLiveData.observeForever(inHandBal -> localInHandBal = inHandBal);
                    launchLiveData.observeForever(launchChecker -> localLaunchChecker = launchChecker);
                    budgetLiveData.observeForever(budget -> localBudget = budget);
                }
                Log.e("DataSync-Level(1)", "Fetch finished.");
                SyncStatus.postValue(true);
                taskStatus = 1;
            } else {
                Log.e("DataSync-Level(1)", "Fetch in progress.");
                SyncStatus.postValue(false);
            }
        });
    }

    private void CompareTheAndis() {
        if (!localExp.isEmpty() && !localExp.equals(expLiveData.getValue())) {
            Log.e("DataSync-Level(1)", "Uploading Expenses.");
            putExp(localExp);
        }else{
            Log.e("DataSync-Level(1)", "Expense data match.");
        }

        if (!localSalary.isEmpty() && !localSalary.equals(salaryLiveData.getValue())) {
            putSalary(localSalary);
        }else{
            Log.e("DataSync-Level(1)", "Salary data match.");
        }

        if (!localDebt.isEmpty() && !localDebt.equals(debtLiveData.getValue())) {
            putDebt(localDebt);
        }else{
            Log.e("DataSync-Level(1)", "Debt data match.");
        }

        try {
            if (!localBalance.equals(bankBalLiveData.getValue())) {
                putBankBalance(localBalance);
            }else{
                Log.e("DataSync-Level(1)", "Bank Balance data match.");
            }
        } catch (Exception ignored) {
        }

        try {
            if (!localInHandBal.equals(inHandBalLiveData.getValue())) {
                putInHandBalance(localInHandBal);
            }else{
                Log.e("DataSync-Level(1)", "In Hand Balance data match.");
            }
        } catch (Exception ignored) {
        }

        try {
            if (!localLaunchChecker.equals(launchLiveData.getValue())) {
                putLaunch(localLaunchChecker);
            }else{
                Log.e("DataSync-Level(1)", "Launch Checker data match.");
            }
        } catch (Exception ignored) {
        }

        try {
            if (!localBudget.equals(budgetLiveData.getValue())) {
                putBudget(localBudget);
            }else{
                Log.e("DataSync-Level(1)", "Budget data match.");
            }
        } catch (Exception ignored) {
        }
        SyncStatus.postValue(true);
    }

    private void populateValues(int index) {
        if (index == 0) {
            fetchExp();
        } else if (index == 1) {
            fetchDebt();
        } else if (index == 2) {
            fetchBankBal();
        } else if (index == 3) {
            fetchInHandBal();
        } else if (index == 4) {
            fetchLaunch();
        } else if (index == 5) {
            fetchSalary();
        } else if (index == 6) {
            fetchBudget();
        }
    }

    //  Local data fetcher
    public void setLocalExp(List<expEntity> expEntityList) {
        this.localExp.addAll(expEntityList);
    }

    public void setLocalDebt(List<debtEntity> debtEntityList) {
        this.localDebt.addAll(debtEntityList);
    }

    public void setLocalSalary(List<salaryEntity> salaryEntityList) {
        this.localSalary.addAll(salaryEntityList);
    }

    public void setLocalBalance(balanceEntity balance) {
        this.localBalance = balance;
    }

    public void setLocalInHandBal(inHandBalEntity inHandBal) {
        this.localInHandBal = inHandBal;
    }

    public void setLocalBudget(budgetEntity budgetEntity) {
        this.localBudget = budgetEntity;
    }

    public void setLocalLaunchChecker(LaunchChecker launchChecker) {
        this.localLaunchChecker = launchChecker;
    }

    //    Fetcher
    private void fetchExp() {
        expDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<expEntity> expList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        expEntity exp = ds.child(Objects.requireNonNull(ds.getKey()))
                                .getValue(expEntity.class);
                        if (exp != null) {
                            expList.add(exp);
                            Log.e("DataSync", "expData: " + exp.getId() + "Date: " + exp.getDate());
                        }
                    }
                    if (expList.size() > 0) {
                        expLiveData.postValue(expList);
                    }
                    populateValues(1);
                } else {
                    populateValues(1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
                populateValues(1);
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
                        debtEntity debt = ds.child(Objects.requireNonNull(ds.getKey()))
                                .getValue(debtEntity.class);
                        if (debt != null) {
                            debtList.add(debt);
                            Log.e("DataSync", "debtData: " + debt.getId() + "Date: " + debt.getDate());
                        }
                    }
                    if (debtList.size() > 0) {
                        debtLiveData.postValue(debtList);
                    }
                    populateValues(2);
                } else {
                    populateValues(2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
                populateValues(2);
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

                        if (id != null&&incName != null&&salary != null&&incType != null&&creationDate != null&&salMode != null) {
                            salaryList.add(new salaryEntity(Integer.parseInt(id), Integer.parseInt(salary),
                                    incName, Integer.parseInt(incType), creationDate, Integer.parseInt(salMode)));
                            Log.e("DataSync", "salaryData: " + id + "Date: " + creationDate);
                        }
                    }
                    if (salaryList.size() > 0) {
                        salaryLiveData.postValue(salaryList);
                    }
                    populateValues(6);
                } else {
                    populateValues(6);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
                populateValues(6);
            }
        });
    }

    private void fetchBankBal() {
        bankBalDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    balanceEntity bal = snapshot.child(Objects.requireNonNull(snapshot.getKey()))
                            .getValue(balanceEntity.class);
                    if (bal != null) {
                        bankBalLiveData.postValue(bal);
                        populateValues(3);
                    }
                } else {
                    populateValues(3);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
                populateValues(3);
            }
        });
    }

    private void fetchInHandBal() {
        inHandBalDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    inHandBalEntity bal = snapshot.child(Objects.requireNonNull(snapshot.getKey()))
                            .getValue(inHandBalEntity.class);
                    if (bal != null) {
                        inHandBalLiveData.postValue(bal);
                        populateValues(4);
                    }
                } else {
                    populateValues(4);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
                populateValues(4);
            }
        });
    }

    private void fetchLaunch() {
        launchDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    LaunchChecker l = snapshot.child(Objects.requireNonNull(snapshot.getKey()))
                            .getValue(LaunchChecker.class);
                    launchLiveData.postValue(l);
                    populateValues(5);
                } else {
                    populateValues(5);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
                populateValues(5);
            }
        });
    }

    private void fetchBudget() {
        budgetDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    budgetEntity bud = snapshot.child(Objects.requireNonNull(snapshot.getKey()))
                            .getValue(budgetEntity.class);
                    budgetLiveData.postValue(bud);
                    FetchProgress.postValue(1);
                } else {
                    FetchProgress.postValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseError.postValue(error.getMessage());
                FetchProgress.postValue(1);
            }
        });
    }

    //    Uploader
    public void putExp(List<expEntity> expEntityList) {
        for (expEntity exp : expEntityList) {
            expDataRef.push().setValue(new hashUtil(exp).getExpHashMap(), (error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        }
    }

    public void putDebt(List<debtEntity> debtEntityList) {
        for (debtEntity debt : debtEntityList) {
            debtDataRef.push().setValue(new hashUtil(debt).getDebtHashMap(), (error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        }
    }

    public void putSalary(List<salaryEntity> salaryEntityList) {
        for (salaryEntity salary : salaryEntityList) {
            salaryDataRef.push().setValue(new hashUtil(salary).getSalaryHashMap(), (error, ref) -> {
                if (error != null) {
                    FirebaseError.postValue(error.getMessage());
                }
            });
        }
    }

    public void putBankBalance(balanceEntity balance) {
        bankBalDataRef.setValue(balance, (error, ref) -> {
            if (error != null) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    public void putInHandBalance(inHandBalEntity inHandBal) {
        inHandBalDataRef.setValue(inHandBal, (error, ref) -> {
            if (error != null) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    public void putBudget(budgetEntity budget) {
        budgetDataRef.setValue(budget, (error, ref) -> {
            if (error != null) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    public void putLaunch(LaunchChecker launchChecker) {
        launchDataRef.setValue(launchChecker, (error, ref) -> {
            if (error != null) {
                FirebaseError.postValue(error.getMessage());
            }
        });
    }

    public void setDefaultError() {
        FirebaseError.postValue(default_Error);
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