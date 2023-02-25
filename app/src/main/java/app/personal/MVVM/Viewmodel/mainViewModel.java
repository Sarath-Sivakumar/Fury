package app.personal.MVVM.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Repository.localRepository;

public class mainViewModel extends AndroidViewModel {

    private final localRepository repo;
    private final LiveData<balanceEntity> getBalance;
    private final LiveData<inHandBalEntity> getInHandBalance;
    private final LiveData<List<debtEntity>> getDebt;
    private final LiveData<budgetEntity> getBudget;
    private final LiveData<List<expEntity>> getExp;
    private final LiveData<List<salaryEntity>> getSalary;

    public mainViewModel(@NonNull Application application) {
        super(application);
        repo = new localRepository(application);
        getDebt = repo.getDebt();
        getExp = repo.getExp();
        getSalary = repo.getSalary();
        getBalance = repo.getBalance();
        getInHandBalance = repo.getInHandBal();
        getBudget = repo.getBudget();
    }


    public void InsertExp(expEntity entity) {
        repo.InsertExp(entity);
    }

    public void UpdateExp(expEntity entity) {
        repo.UpdateExp(entity);
    }

    public void DeleteExp(expEntity entity) {
        repo.DeleteExp(entity);
    }

    public void InsertBalance(balanceEntity entity) {
        repo.InsertBalance(entity);
    }

    public void DeleteBalance() {
        repo.DeleteBalance();
    }

    public void InsertInHandBalance(inHandBalEntity entity) {
        repo.InsertInHandBalance(entity);
    }

    public void DeleteInHandBalance() {
        repo.DeleteInHandBalance();
    }

    public void InsertSalary(salaryEntity entity) {
        repo.InsertSalary(entity);
    }

    public void UpdateSalary(salaryEntity entity) {
        repo.UpdateSalary(entity);
    }

    public void DeleteSalary(salaryEntity entity) {
        repo.DeleteSalary(entity);
    }

    public void InsertDebt(debtEntity entity) {
        repo.InsertDebt(entity);
    }

    public void UpdateDebt(debtEntity entity) {
        repo.UpdateDebt(entity);
    }

    public void DeleteDebt(debtEntity entity) {
        repo.DeleteDebt(entity);
    }

    public void InsertBudget(budgetEntity entity) {
        repo.InsertBudget(entity);
    }

    public void UpdateBudget(budgetEntity entity) {
        repo.UpdateBudget(entity);
    }

    public void DeleteBudget() {
        repo.DeleteBudget();
    }

    public LiveData<balanceEntity> getBalance() {
        return getBalance;
    }

    public LiveData<inHandBalEntity> getInHandBalance() {
        return getInHandBalance;
    }

    public LiveData<List<salaryEntity>> getSalary() {
        return getSalary;
    }

    public LiveData<List<debtEntity>> getDebt() {
        return getDebt;
    }

    public LiveData<List<expEntity>> getExp() {
        return getExp;
    }

    public LiveData<budgetEntity> getBudget() {
        return getBudget;
    }
}