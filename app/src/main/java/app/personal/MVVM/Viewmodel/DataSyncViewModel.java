package app.personal.MVVM.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import app.personal.MVVM.Entity.LaunchChecker;
import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Repository.dataSyncRepository;

public class DataSyncViewModel extends AndroidViewModel {

    private final dataSyncRepository dsRepo;
    private final MutableLiveData<String> FirebaseError;
    private final MutableLiveData<Boolean> SyncStatus;
    private final MutableLiveData<Boolean> bruteForceSync;

    private final MutableLiveData<List<expEntity>> expLiveData;
    private final MutableLiveData<balanceEntity> bankBalLiveData;
    private final MutableLiveData<inHandBalEntity> inHandBalLiveData;
    private final MutableLiveData<List<debtEntity>> debtLiveData;
    private final MutableLiveData<budgetEntity> budgetLiveData;
    private final MutableLiveData<LaunchChecker> launchLiveData;
    private final MutableLiveData<List<salaryEntity>> salaryLiveData;


    public DataSyncViewModel(@NonNull Application application) {
        super(application);
        this.dsRepo = new dataSyncRepository(application);
//        Functionality
        this.FirebaseError = dsRepo.getFirebaseError();
        this.SyncStatus = dsRepo.getSyncStatus();
        this.bruteForceSync = dsRepo.getBruteForceSync();

//        Returnable Live Data
        this.expLiveData = dsRepo.getExpLiveData();
        this.bankBalLiveData = dsRepo.getBankBalLiveData();
        this.inHandBalLiveData = dsRepo.getInHandBalLiveData();
        this.debtLiveData = dsRepo.getDebtLiveData();
        this.budgetLiveData = dsRepo.getBudgetLiveData();
        this.launchLiveData = dsRepo.getLaunchLiveData();
        this.salaryLiveData = dsRepo.getSalaryLiveData();
    }

    public void fetchAllData() {
        Log.e("DataSync-Level2", "Data fetch init");
        dsRepo.fetchAll();
    }

    public MutableLiveData<String> getFirebaseError() {
        return FirebaseError;
    }

    public void setDefaultError() {
        dsRepo.setDefaultError();
    }

    public void setBruteForceSync(Boolean isBruteforce) {
        dsRepo.setBruteForceSync(isBruteforce);
    }

    public MutableLiveData<Boolean> getBruteForceSync() {
        return bruteForceSync;
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

//    Use for later
    public void setLocalBalance(balanceEntity localBalance) {
        dsRepo.CompareBankBalance(localBalance);
    }

    public void setLocalBudget(budgetEntity localBudget) {
        dsRepo.CompareBudget(localBudget);
    }

    public void setLocalInHandBal(inHandBalEntity localInHandBal) {
        dsRepo.CompareInHandBal(localInHandBal);
    }

    public void setLocalLaunchChecker(LaunchChecker localLaunchChecker) {
        dsRepo.CompareLaunch(localLaunchChecker);
    }

    public void setLocalExp(List<expEntity> exp) {
        dsRepo.CompareExp(exp);
    }

    public void setLocalDebt(List<debtEntity> debt) {
        dsRepo.CompareDebt(debt);
    }

    public void setLocalSalary(List<salaryEntity> salary) {
        dsRepo.CompareSalary(salary);
    }

//    Call on launch->
    public void CompareAll(List<expEntity> localExp, List<salaryEntity> localSalary,
                           List<debtEntity> localDebt, balanceEntity localBalance,
                           inHandBalEntity localInHandBal, LaunchChecker localLaunchChecker,
                           budgetEntity localBudget){
        dsRepo.CompareAll(localExp, localSalary, localDebt, localBalance, localInHandBal,
                localLaunchChecker, localBudget);
    }
}
