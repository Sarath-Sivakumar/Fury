package app.personal.MVVM.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import app.personal.MVVM.DB.localDB;
import app.personal.MVVM.Dao.localDao;
import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;

public class localRepository {
    private final localDao dao;
    private final LiveData<balanceEntity> getBalance;
    private final LiveData<List<debtEntity>> getDebt;
    private final LiveData<List<budgetEntity>> getBudget;
    private final LiveData<List<expEntity>> getExp;
    private final LiveData<List<salaryEntity>> getSalary;

    public localRepository(Application application) {
        localDB db = localDB.getInstance(application);
        dao = db.dao();
        getBalance = dao.getBalData();
        getDebt = dao.getDebtData();
        getExp = dao.getExpData();
        getSalary = dao.getSalData();
        getBudget = dao.getBudgetData();
    }

    //----------------------------------------------------------------------------------------------
    public void InsertBalance(balanceEntity balance) {
        new InsertBalAsyncTask(dao).execute(balance);
    }

    public void DeleteBalance(){
        new DeleteBalAsyncTask(dao).execute();
    }

    public void InsertDebt(debtEntity debt) {
        new InsertDebtAsyncTask(dao).execute(debt);
    }

    public void UpdateDebt(debtEntity debt){
        new UpdateDebtAsyncTask(dao).execute(debt);
    }

    public void DeleteDebt(debtEntity debt){
        new DeleteDebtAsyncTask(dao).execute(debt);
    }

    public void InsertSalary(salaryEntity salary) {
        new InsertSalAsyncTask(dao).execute(salary);
    }

    public void UpdateSalary(salaryEntity salary){
        new UpdateSalAsyncTask(dao).execute(salary);
    }

    public void DeleteSalary(salaryEntity salary){
        new DeleteSalAsyncTask(dao).execute(salary);
    }

    public void InsertExp(expEntity exp) {
        new InsertExpAsyncTask(dao).execute(exp);
    }

    public void UpdateExp(expEntity exp){
        new UpdateExpAsyncTask(dao).execute(exp);
    }

    public void DeleteExp(expEntity exp){
        new DeleteExpAsyncTask(dao).execute(exp);
    }

    public void InsertBudget(budgetEntity budgetEntity){
        new InsertBudgetAsyncTask(dao).execute(budgetEntity);
    }

    public void UpdateBudget(budgetEntity budgetEntity){
        new UpdateBudgetAsyncTask(dao).execute(budgetEntity);
    }

    public void DeleteBudget(budgetEntity budgetEntity){
        new DeleteBudgetAsyncTask(dao).execute(budgetEntity);
    }

    public LiveData<List<budgetEntity>> getBudget(){return getBudget;}

    public LiveData<balanceEntity> getBalance(){
        return getBalance;
    }

    public LiveData<List<expEntity>> getExp(){
        return getExp;
    }

    public LiveData<List<salaryEntity>> getSalary(){
        return getSalary;
    }

    public LiveData<List<debtEntity>> getDebt(){
        return getDebt;
    }
    //----------------------------------------------------------------------------------------------
    //To run during first execution-----------------------------------------------------------------



    //----------------------------------------------------------------------------------------------
    //Exp background task---------------------------------------------------------------------------
    private static class InsertExpAsyncTask extends AsyncTask<expEntity,Void,Void> {
        private  localDao dao;
        private InsertExpAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(expEntity... entities) {
            dao.InsertExp(entities[0]);
            return null;
        }
    }

    private static class UpdateExpAsyncTask extends AsyncTask<expEntity,Void,Void> {
        private  localDao dao;
        private UpdateExpAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(expEntity... entities) {
            dao.UpdateExp(entities[0]);
            return null;
        }
    }

    private static class DeleteExpAsyncTask extends AsyncTask<expEntity,Void,Void> {
        private  localDao dao;
        private DeleteExpAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(expEntity... entities) {
            dao.DeleteExp(entities[0]);
            return null;
        }
    }
    //----------------------------------------------------------------------------------------------
    //Balance background task-----------------------------------------------------------------------
    private static class InsertBalAsyncTask extends AsyncTask<balanceEntity,Void,Void> {
        private  localDao dao;
        private InsertBalAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(balanceEntity... entities) {
            dao.InsertBal(entities[0]);
            return null;
        }
    }

    private static class DeleteBalAsyncTask extends AsyncTask<Void,Void,Void> {
        private  localDao dao;
        private DeleteBalAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.DeleteBal();
            return null;
        }
    }
    //----------------------------------------------------------------------------------------------
    //Salary background task------------------------------------------------------------------------
    private static class InsertSalAsyncTask extends AsyncTask<salaryEntity,Void,Void> {
        private  localDao dao;
        private InsertSalAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(salaryEntity... entities) {
            dao.InsertSal(entities[0]);
            return null;
        }
    }

    private static class UpdateSalAsyncTask extends AsyncTask<salaryEntity,Void,Void> {
        private  localDao dao;
        private UpdateSalAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(salaryEntity... entities) {
            dao.UpdateSal(entities[0]);
            return null;
        }
    }

    private static class DeleteSalAsyncTask extends AsyncTask<salaryEntity,Void,Void> {
        private  localDao dao;
        private DeleteSalAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(salaryEntity... entities) {
            dao.DeleteSal(entities[0]);
            return null;
        }
    }
    //----------------------------------------------------------------------------------------------
    //Debt background task------------------------------------------------------------------------
    private static class InsertDebtAsyncTask extends AsyncTask<debtEntity,Void,Void> {
        private  localDao dao;
        private InsertDebtAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(debtEntity... entities) {
            dao.InsertDebt(entities[0]);
            return null;
        }
    }

    private static class UpdateDebtAsyncTask extends AsyncTask<debtEntity,Void,Void> {
        private  localDao dao;
        private UpdateDebtAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(debtEntity... entities) {
            dao.UpdateDebt(entities[0]);
            return null;
        }
    }

    private static class DeleteDebtAsyncTask extends AsyncTask<debtEntity,Void,Void> {
        private  localDao dao;
        private DeleteDebtAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(debtEntity... entities) {
            dao.DeleteDebt(entities[0]);
            return null;
        }
    }
    //----------------------------------------------------------------------------------------------
    //Budget background task------------------------------------------------------------------------

    private static class InsertBudgetAsyncTask extends AsyncTask<budgetEntity,Void,Void> {
        private  localDao dao;
        private InsertBudgetAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(budgetEntity... entities) {
            dao.InsertBudget(entities[0]);
            return null;
        }
    }

    private static class UpdateBudgetAsyncTask extends AsyncTask<budgetEntity,Void,Void> {
        private  localDao dao;
        private UpdateBudgetAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(budgetEntity... entities) {
            dao.UpdateBudget(entities[0]);
            return null;
        }
    }

    private static class DeleteBudgetAsyncTask extends AsyncTask<budgetEntity,Void,Void> {
        private  localDao dao;
        private DeleteBudgetAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(budgetEntity... entities) {
            dao.DeleteBudget(entities[0]);
            return null;
        }
    }

}