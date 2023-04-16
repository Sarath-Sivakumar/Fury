package app.personal.MVVM.Application;

import static app.personal.Utls.Constants.default_Error;
import static app.personal.Utls.Constants.default_int_entity;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

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
import app.personal.MVVM.Viewmodel.AppUtilViewModel;
import app.personal.MVVM.Viewmodel.DataSyncViewModel;
import app.personal.MVVM.Viewmodel.mainViewModel;

public class ViewLevelSyncHelper {

    private final mainViewModel Mvm;
    private final DataSyncViewModel Dvm;
    private final AppUtilViewModel AppVm;
    private final LifecycleOwner lifecycleOwner;

    private List<debtEntity> localDebt;
    private List<expEntity> localExp;
    private List<salaryEntity> localSalary;
    private balanceEntity localBalance;
    private budgetEntity localBudget;
    private inHandBalEntity localInHandBal;
    private LaunchChecker localLaunchChecker;

    private final MutableLiveData<Integer> initTask;

    public ViewLevelSyncHelper(mainViewModel Mvm, DataSyncViewModel Dvm,
                               AppUtilViewModel AppVm, LifecycleOwner lifecycleOwner) {
        this.initTask = new MutableLiveData<>();
        this.initTask.postValue(0);
        this.Mvm = Mvm;
        this.Dvm = Dvm;
        this.AppVm = AppVm;
        this.lifecycleOwner = lifecycleOwner;

        Dvm.fetchAllData();

        this.localDebt = new ArrayList<>();
        this.localExp = new ArrayList<>();
        this.localSalary = new ArrayList<>();
        this.localBalance = new balanceEntity();
        this.localBudget = new budgetEntity();
        this.localInHandBal = new inHandBalEntity();
        this.localLaunchChecker = new LaunchChecker();

        init();
    }

    private void init() {
        Log.e("DataSync-Level(VC)", "Helper init.");
        Mvm.getBalance().observe(lifecycleOwner, balance -> {
            try {
                localBalance = balance;
                initTask.postValue(1);
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local bank balance");
                initTask.postValue(1);
            }
        });
        Mvm.getInHandBalance().observe(lifecycleOwner, inHandBal -> {
            try {
                localInHandBal = inHandBal;
                initTask.postValue(2);
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local in hand balance");
                initTask.postValue(2);
            }
        });
        Mvm.getBudget().observe(lifecycleOwner, budget -> {
            try {
                localBudget = budget;
                initTask.postValue(3);
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local budget");
                initTask.postValue(3);
            }
        });
        Mvm.getExp().observe(lifecycleOwner, expEntityList -> {
            try {
                if (!expEntityList.isEmpty()) {
                    localExp = expEntityList;
                } else {
                    Log.e("DataSync-Level(VC)", "No local expenses");
                }
                initTask.postValue(4);
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local expenses");
                initTask.postValue(4);
            }
        });
        Mvm.getSalary().observe(lifecycleOwner, salaryEntityList -> {
            try {
                if (!salaryEntityList.isEmpty()) {
                    localSalary = salaryEntityList;
                } else {
                    Log.e("DataSync-Level(VC)", "No local salary");
                }
                initTask.postValue(5);
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local salary");
                initTask.postValue(5);
            }
        });
        Mvm.getDebt().observe(lifecycleOwner, debtEntityList -> {
            try {
                if (!debtEntityList.isEmpty()) {
                    localDebt = debtEntityList;
                } else {
                    Log.e("DataSync-Level(VC)", "No local salary");
                }
                initTask.postValue(6);
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local debt");
                initTask.postValue(6);
            }
        });
        AppVm.getCheckerData().observe(lifecycleOwner, launchChecker -> {
            try {
                localLaunchChecker = launchChecker;
                initTask.postValue(7);
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "Launch load error");
                initTask.postValue(7);
            }
        });
    }

    public void newUserSync() {
        Dvm.fetchAllData();
        Dvm.getExpLiveData().observe(lifecycleOwner, expEntityList -> {
            if (expEntityList != null) {
                if (!expEntityList.get(0).getDate().equals(default_Error)) {
                    if (!expEntityList.equals(localExp)) {
                        Mvm.DeleteAllExp();
                        Mvm.setExpList(expEntityList);
                        Dvm.getExpLiveData().removeObservers(lifecycleOwner);
                    }
                }
            }
        });
        Dvm.getSalaryLiveData().observe(lifecycleOwner, salaryEntityList -> {
            if (salaryEntityList != null) {
                if (!salaryEntityList.get(0).getCreationDate().equals(default_Error)) {
                    if (!salaryEntityList.equals(localSalary)) {
                        Mvm.DeleteAllSalary();
                        Mvm.setSalaryList(salaryEntityList);
                        Dvm.getSalaryLiveData().removeObservers(lifecycleOwner);
                    }
                }
            }
        });
        Dvm.getDebtLiveData().observe(lifecycleOwner, debtEntityList -> {
            if (debtEntityList != null) {
                if (!debtEntityList.get(0).getDate().equals(default_Error)) {
                    if (!debtEntityList.equals(localDebt)) {
                        Mvm.DeleteAllDebt();
                        Mvm.setDebtList(debtEntityList);
                        Dvm.getDebtLiveData().removeObservers(lifecycleOwner);
                    }
                }
            }
        });
        Dvm.getBankBalLiveData().observe(lifecycleOwner, balance -> {
            try {
                if (balance.getId() != default_int_entity) {
                    if (balance != localBalance) {
                        Mvm.DeleteBalance();
                        Mvm.InsertBalance(balance);
                        Dvm.getBankBalLiveData().removeObservers(lifecycleOwner);
                    }
                }
            } catch (Exception ignored) {
            }
        });
        Dvm.getInHandBalLiveData().observe(lifecycleOwner, inHandBal -> {
            try {
                if (inHandBal.getId() != default_int_entity) {
                    if (inHandBal != localInHandBal) {
                        Mvm.DeleteInHandBalance();
                        Mvm.InsertInHandBalance(inHandBal);
                        Dvm.getInHandBalLiveData().removeObservers(lifecycleOwner);
                    }
                }
            } catch (Exception ignored) {
            }
        });
        Dvm.getLaunchLiveData().observe(lifecycleOwner, launchChecker -> {
            try {
                if (launchChecker.getId() != default_int_entity) {
                    if (launchChecker != localLaunchChecker) {
                        AppVm.DeleteLaunchChecker();
                        AppVm.InsertLaunchChecker(launchChecker);
                        Dvm.getLaunchLiveData().removeObservers(lifecycleOwner);
                    }
                }
            } catch (Exception ignored) {
            }
        });
        Dvm.getBudgetLiveData().observe(lifecycleOwner, budget -> {
            try {
                if (!Objects.equals(budget.getCreationDate(), default_Error)) {
                    if (budget != localBudget) {
                        Mvm.DeleteBudget();
                        Mvm.InsertBudget(budget);
                        Dvm.getBudgetLiveData().removeObservers(lifecycleOwner);
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }

    public void regularLaunchSync(){
        Dvm.fetchAllData();
        Dvm.getExpLiveData().observe(lifecycleOwner, expEntityList -> {
            if (expEntityList != null) {
                if (!expEntityList.get(0).getDate().equals(default_Error)) {
                    if (!expEntityList.equals(localExp)) {
                        SaveToCloud();
                        Mvm.DeleteAllExp();
                        Mvm.setExpList(expEntityList);
                        Dvm.getExpLiveData().removeObservers(lifecycleOwner);
                    }
                }
            }
        });
        Dvm.getSalaryLiveData().observe(lifecycleOwner, salaryEntityList -> {
            if (salaryEntityList != null) {
                if (!salaryEntityList.get(0).getCreationDate().equals(default_Error)) {
                    if (!salaryEntityList.equals(localSalary)) {
                        SaveToCloud();
                        Mvm.DeleteAllSalary();
                        Mvm.setSalaryList(salaryEntityList);
                        Dvm.getSalaryLiveData().removeObservers(lifecycleOwner);
                    }
                }
            }
        });
        Dvm.getDebtLiveData().observe(lifecycleOwner, debtEntityList -> {
            if (debtEntityList != null) {
                if (!debtEntityList.get(0).getDate().equals(default_Error)) {
                    if (!debtEntityList.equals(localDebt)) {
                        SaveToCloud();
                        Mvm.DeleteAllDebt();
                        Mvm.setDebtList(debtEntityList);
                        Dvm.getDebtLiveData().removeObservers(lifecycleOwner);
                    }
                }
            }
        });
        Dvm.getBankBalLiveData().observe(lifecycleOwner, balance -> {
            try {
                if (balance.getId() != default_int_entity) {
                    if (balance != localBalance) {
                        SaveToCloud();
                        Mvm.DeleteBalance();
                        Mvm.InsertBalance(balance);
                        Dvm.getBankBalLiveData().removeObservers(lifecycleOwner);
                    }
                }
            } catch (Exception ignored) {
            }
        });
        Dvm.getInHandBalLiveData().observe(lifecycleOwner, inHandBal -> {
            try {
                if (inHandBal.getId() != default_int_entity) {
                    if (inHandBal != localInHandBal) {
                        SaveToCloud();
                        Mvm.DeleteInHandBalance();
                        Mvm.InsertInHandBalance(inHandBal);
                        Dvm.getInHandBalLiveData().removeObservers(lifecycleOwner);
                    }
                }
            } catch (Exception ignored) {
            }
        });
        Dvm.getLaunchLiveData().observe(lifecycleOwner, launchChecker -> {
            try {
                if (launchChecker.getId() != default_int_entity) {
                    if (launchChecker != localLaunchChecker) {
                        SaveToCloud();
                        AppVm.DeleteLaunchChecker();
                        AppVm.InsertLaunchChecker(launchChecker);
                        Dvm.getLaunchLiveData().removeObservers(lifecycleOwner);
                    }
                }
            } catch (Exception ignored) {
            }
        });
        Dvm.getBudgetLiveData().observe(lifecycleOwner, budget -> {
            try {
                if (!Objects.equals(budget.getCreationDate(), default_Error)) {
                    if (budget != localBudget) {
                        SaveToCloud();
                        Mvm.DeleteBudget();
                        Mvm.InsertBudget(budget);
                        Dvm.getBudgetLiveData().removeObservers(lifecycleOwner);
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }

    public void SaveToCloud() {
        init();
        Dvm.CompareAll(localExp, localSalary, localDebt, localBalance,
                localInHandBal, localLaunchChecker, localBudget);
    }
}
