package app.personal.MVVM.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import app.personal.MVVM.DB.localDB;
import app.personal.MVVM.Dao.localDao;
import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;

public class localRepository {
    private localDao dao;
    private LiveData<balanceEntity> getBalance;
    private LiveData<debtEntity> getDebt;
    private LiveData<List<expEntity>> getExp;
    private LiveData<salaryEntity> getSalary;

    public localRepository(Application application) {
        localDB db = localDB.getInstance(application);
        localDao dao = db.dao();
        getBalance = dao.getBalData();
        getDebt = dao.getDebtData();
        getExp = dao.getExpData();
        getSalary = dao.getSalData();
    }

    //----------------------------------------------------------------------------------------------
    public void PutBalance(balanceEntity balance) {
        try{
            new UpdateBalAsyncTask(dao).execute(balance);
        }catch (Exception e) {
            new InsertBalAsyncTask(dao).execute(balance);
            e.printStackTrace();
        }
    }

    public void PutDebt(debtEntity debt) {
        try{
            new UpdateDebtAsyncTask(dao).execute(debt);
        }catch (Exception e) {
            new InsertDebtAsyncTask(dao).execute(debt);
            e.printStackTrace();
        }
    }

    public void PutExp(expEntity exp) {
        try{
            new UpdateExpAsyncTask(dao).execute(exp);
        }catch (Exception e){
            new InsertExpAsyncTask(dao).execute(exp);
            e.printStackTrace();
        }
    }

    public void DeleteExp(expEntity exp){
        new DeleteExpAsyncTask(dao).execute(exp);
    }

    public void PutSalary(salaryEntity salary) {
        try{
            new UpdateSalAsyncTask(dao).execute(salary);
        }catch (Exception e) {
            new InsertSalAsyncTask(dao).execute(salary);
            e.printStackTrace();
        }
    }

    public LiveData<balanceEntity> getBalance(){
        return getBalance;
    }

    public LiveData<List<expEntity>> getExp(){
        return getExp;
    }

    public LiveData<salaryEntity> getSalary(){
        return getSalary;
    }

    public LiveData<debtEntity> getDebt(){
        return getDebt;
    }
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

    private static class UpdateBalAsyncTask extends AsyncTask<balanceEntity,Void,Void> {
        private  localDao dao;
        private UpdateBalAsyncTask(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(balanceEntity... entities) {
            dao.UpdateBal(entities[0]);
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
    //----------------------------------------------------------------------------------------------
}