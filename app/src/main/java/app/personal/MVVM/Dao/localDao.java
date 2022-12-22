package app.personal.MVVM.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;

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

    //Balance------

    @Insert
    void InsertBal(balanceEntity entity);

    @Query("DELETE FROM Balance_Table")
    void DeleteBal();

    @Query("SELECT * FROM Balance_Table ORDER BY id DESC")
    LiveData<balanceEntity> getBalData();

    //Salary------

    @Insert
    void InsertSal(salaryEntity entity);

    @Update
    void UpdateSal(salaryEntity entity);

    @Delete
    void DeleteSal(salaryEntity entity);

    @Query("SELECT * FROM Salary_Table ORDER BY id DESC")
    LiveData<List<salaryEntity>> getSalData();

    //Debt-------

    @Insert
    void InsertDebt(debtEntity entity);

    @Update
    void UpdateDebt(debtEntity entity);

    @Delete
    void DeleteDebt(debtEntity entity);

    @Query("SELECT * FROM Debt_Table ORDER BY id DESC")
    LiveData<List<debtEntity>> getDebtData();

    //Budget-------

    @Insert
    void InsertBudget(budgetEntity budgetEntity);

    @Update
    void UpdateBudget(budgetEntity budgetEntity);

    @Delete
    void DeleteBudget(budgetEntity budgetEntity);

    @Query("SELECT * FROM Budget_Table")
    LiveData<List<budgetEntity>> getBudgetData();
}