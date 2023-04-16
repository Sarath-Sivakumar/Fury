package app.personal.MVVM.Application;

import static app.personal.Utls.Constants.default_Error;
import static app.personal.Utls.Constants.default_int_entity;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

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

    public ViewLevelSyncHelper(mainViewModel Mvm, DataSyncViewModel Dvm,
                               AppUtilViewModel AppVm, LifecycleOwner lifecycleOwner) {
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
        Mvm.getBalance().observe(lifecycleOwner, balance -> {
            try {
                localBalance = balance;
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local bank balance");
            }
        });
        Mvm.getInHandBalance().observe(lifecycleOwner, inHandBal -> {
            try {
                localInHandBal = inHandBal;
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local in hand balance");
            }
        });
        Mvm.getBudget().observe(lifecycleOwner, budget -> {
            try {
                localBudget = budget;
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local budget");
            }
        });
        Mvm.getExp().observe(lifecycleOwner, expEntityList -> {
            try {
                if (!expEntityList.isEmpty()) {
                    localExp = expEntityList;
                } else {
                    Log.e("DataSync-Level(VC)", "No local expenses");
                }
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local expenses");
            }
        });
        Mvm.getSalary().observe(lifecycleOwner, salaryEntityList -> {
            try {
                if (!salaryEntityList.isEmpty()) {
                    localSalary = salaryEntityList;
                } else {
                    Log.e("DataSync-Level(VC)", "No local salary");
                }
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local salary");
            }
        });
        Mvm.getDebt().observe(lifecycleOwner, debtEntityList -> {
            try {
                if (!debtEntityList.isEmpty()) {
                    localDebt = debtEntityList;
                } else {
                    Log.e("DataSync-Level(VC)", "No local salary");
                }
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "No local debt");
            }
        });
        AppVm.getCheckerData().observe(lifecycleOwner, launchChecker -> {
            try {
                localLaunchChecker = launchChecker;
            } catch (Exception ignored) {
                Log.e("DataSync-Level(VC)", "Launch load error");
            }
        });
    }

    public void Sync() {
        Dvm.fetchAllData();
        Dvm.getExpLiveData().observe(lifecycleOwner, expEntityList -> {
            if (expEntityList != null) {
                if (!expEntityList.get(0).getDate().equals(default_Error)) {
                    if (!expEntityList.equals(localExp)) {
                        Mvm.DeleteAllExp();
                        Mvm.setExpList(expEntityList);
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
                    }
                }
            } catch (Exception ignored) {
            }
        });
        Dvm.getLaunchLiveData().observe(lifecycleOwner, launchChecker -> {
            try {
                if (launchChecker.getId() != default_int_entity) {
                    if (launchChecker != localLaunchChecker) {
                        Mvm.DeleteInHandBalance();
                        AppVm.DeleteLaunchChecker();
                        AppVm.InsertLaunchChecker(launchChecker);
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
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }

    public void SaveToCloud() {
        init();
        Dvm.CompareAll(localExp, localSalary, localDebt, localBalance, localInHandBal,
                localLaunchChecker, localBudget);
    }
}
