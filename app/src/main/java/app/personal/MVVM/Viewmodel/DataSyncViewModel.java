package app.personal.MVVM.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
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

    private final List<debtEntity> localDebt;
    private final List<expEntity> localExp;
    private final List<salaryEntity> localSalary;
    private balanceEntity localBalance;
    private budgetEntity localBudget;
    private inHandBalEntity localInHandBal;
    private  LaunchChecker localLaunchChecker;


    public DataSyncViewModel(@NonNull Application application) {
        super(application);
        dsRepo = new dataSyncRepository(application);
        this.FirebaseError = dsRepo.getFirebaseError();
        this.SyncStatus = dsRepo.getSyncStatus();
        this.bruteForceSync = dsRepo.getBruteForceSync();

        this.expLiveData = dsRepo.getExpLiveData();
        this.bankBalLiveData = dsRepo.getBankBalLiveData();
        this.inHandBalLiveData = dsRepo.getInHandBalLiveData();
        this.debtLiveData = dsRepo.getDebtLiveData();
        this.budgetLiveData = dsRepo.getBudgetLiveData();
        this.launchLiveData = dsRepo.getLaunchLiveData();
        this.salaryLiveData = dsRepo.getSalaryLiveData();

        this.localExp = new ArrayList<>();
        this.localBudget = new budgetEntity();
        this.localDebt = new ArrayList<>();
        this.localSalary = new ArrayList<>();
        this.localInHandBal = new inHandBalEntity();
        this.localBalance = new balanceEntity();
        this.localLaunchChecker = new LaunchChecker();
    }

    public void init(){
        dsRepo.init();
    }

    public MutableLiveData<String> getFirebaseError() {
        return FirebaseError;
    }

    public void setDefaultError(){
        dsRepo.setDefaultError();
    }

    public void setBruteForceSync(Boolean isBruteforce){
        dsRepo.setBruteForceSync(isBruteforce);
    }

    public MutableLiveData<Boolean> getBruteForceSync(){
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

    public void setLocalBalance(balanceEntity localBalance) {
        dsRepo.setLocalBalance(localBalance);
    }

    public void setLocalBudget(budgetEntity localBudget) {
        dsRepo.setLocalBudget(localBudget);
    }

    public void setLocalInHandBal(inHandBalEntity localInHandBal) {
        dsRepo.setLocalInHandBal(localInHandBal);
    }

    public void setLocalLaunchChecker(LaunchChecker localLaunchChecker) {
        dsRepo.setLocalLaunchChecker(localLaunchChecker);
    }
    public void setLocalExp(List<expEntity> exp){
        dsRepo.setLocalExp(exp);
    }
    public void setLocalDebt(List<debtEntity> debt){
        dsRepo.setLocalDebt(debt);
    }
    public void setLocalSalary(List<salaryEntity> salary){
        dsRepo.setLocalSalary(salary);
    }
}
