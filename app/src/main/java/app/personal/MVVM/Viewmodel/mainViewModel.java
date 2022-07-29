package app.personal.MVVM.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Repository.localRepository;

public class mainViewModel extends AndroidViewModel {

    private localRepository repo;
    private LiveData<balanceEntity> getBalance;
    private LiveData<debtEntity> getDebt;
    private LiveData<List<expEntity>> getExp;
    private LiveData<salaryEntity> getSalary;

    public mainViewModel(@NonNull Application application) {
        super(application);
        repo = new localRepository(application);
        getBalance = repo.getBalance();
        getDebt = repo.getDebt();
        getExp = repo.getExp();
        getSalary = repo.getSalary();
    }

    public void PutExp(expEntity entity) {
        repo.PutExp(entity);
    }

    public void DeleteExp(expEntity entity) {
        repo.DeleteExp(entity);
    }

    public void PutBalance(balanceEntity entity) {
        repo.PutBalance(entity);
    }

    public void PutSalary(salaryEntity entity) {
        repo.PutSalary(entity);
    }

    public void PutDebt(debtEntity entity) {
        repo.PutDebt(entity);
    }

    public LiveData<balanceEntity> getBalance() {
        return getBalance;
    }

    public LiveData<salaryEntity> getSalary() {
        return getSalary;
    }

    public LiveData<debtEntity> getDebt() {
        return getDebt;
    }

    public LiveData<List<expEntity>> getExp() {
        return getExp;
    }
}