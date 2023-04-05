package app.personal.MVVM.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import app.personal.MVVM.Entity.LaunchChecker;
import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Entity.userEntity;

@Dao
public interface localDao {

    //Expense-----
    @Insert
    void InsertExp(expEntity entity);

    @Update
    void UpdateExp(expEntity entity);

    @Delete
    void DeleteExp(expEntity entity);

    @Query("SELECT * FROM Exp_Table ORDER BY id DESC")
    LiveData<List<expEntity>> getExpData();

    @Query("DELETE FROM Exp_Table")
    void deleteAllExpData();
    //Balance------

    @Insert
    void InsertBal(balanceEntity entity);

    @Query("DELETE FROM Bank_Bal_Table")
    void DeleteBal();

    @Query("SELECT * FROM Bank_Bal_Table ORDER BY id DESC")
    LiveData<balanceEntity> getBalData();

    //InHandBalance------

    @Insert
    void InsertInHandBal(inHandBalEntity entity);

    @Query("DELETE FROM In_Hand_Bal_Table")
    void DeleteInHandBal();

    @Query("SELECT * FROM In_Hand_Bal_Table ORDER BY id DESC")
    LiveData<inHandBalEntity> getInHandBalData();

    //Salary------

    @Insert
    void InsertSal(salaryEntity entity);

    @Update
    void UpdateSal(salaryEntity entity);

    @Delete
    void DeleteSal(salaryEntity entity);

    @Query("DELETE FROM Salary_Table")
    void DeleteAllSal();

    @Query("SELECT * FROM Salary_Table ORDER BY id DESC")
    LiveData<List<salaryEntity>> getSalData();

    //Debt-------

    @Insert
    void InsertDebt(debtEntity entity);

    @Update
    void UpdateDebt(debtEntity entity);

    @Delete
    void DeleteDebt(debtEntity entity);

    @Query("DELETE FROM Debt_Table")
    void DeleteAllDebt();

    @Query("SELECT * FROM Debt_Table ORDER BY id DESC")
    LiveData<List<debtEntity>> getDebtData();

    //Budget-------

    @Insert
    void InsertBudget(budgetEntity budgetEntity);

    @Update
    void UpdateBudget(budgetEntity budgetEntity);

    @Query("DELETE FROM Budget_Table")
    void DeleteBudget();

    @Query("SELECT * FROM Budget_Table")
    LiveData<budgetEntity> getBudgetData();

    //User-------
    @Insert
    void InsertUserData(userEntity entity);

    @Update
    void UpdateUserData(userEntity entity);

    @Delete
    void DeleteUserData(userEntity entity);

    @Query("SELECT * FROM User_Table")
    LiveData<userEntity> getUserData();
    //LaunchChecker
    @Insert
    void InsertLaunchChecker(LaunchChecker launchChecker);
    @Update
    void UpdateLaunchChecker(LaunchChecker launchChecker);
    @Query("SELECT * FROM Launch_Checker")
    LiveData<LaunchChecker> getChecker();

}