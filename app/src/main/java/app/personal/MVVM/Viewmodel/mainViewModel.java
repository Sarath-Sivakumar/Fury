package app.personal.MVVM.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Repository.localRepository;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;

public class mainViewModel extends AndroidViewModel {

    private final localRepository repo;
    private final LiveData<balanceEntity> getBalance;
    private final LiveData<inHandBalEntity> getInHandBalance;
    private final LiveData<List<debtEntity>> getDebt;
    private final LiveData<budgetEntity> getBudget;
    private final LiveData<List<expEntity>> getExp;
    private final LiveData<List<salaryEntity>> getSalary;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    public mainViewModel(@NonNull Application application) {
        super(application);
        repo = new localRepository(application);
        getBalance = repo.getBalance();
        getDebt = repo.getDebt();
        getExp = repo.getExp();
        getSalary = repo.getSalary();
        getBudget = repo.getBudget();
        getInHandBalance = repo.getInHandBal();
        SalProcess();
    }

    private void SalProcess(){
        Transformations.switchMap(getSalary(),salaryEntity->{
            if (!salaryEntity.isEmpty()){
                processSalary(salaryEntity);
            }
            return null;
        });
    }

    private LiveData<balanceEntity> getBal(){
        return Transformations.switchMap(getBalance, balanceEntity-> repo.getBalance());
    }

    private LiveData<inHandBalEntity> getInHandBal(){
        return Transformations.switchMap(getInHandBalance, inHandBal-> repo.getInHandBal());
    }

    private void processSalary(@NonNull List<salaryEntity> salList){
        for (int i = 0; i< salList.size(); i++){
            salaryEntity sal = salList.get(i);
            if (sal.getIncType() == Constants.monthly){
                String creationDate = sal.getCreationDate();
                String currentDate = Commons.getDate();
                String[] splitCreationDate = creationDate.split("/");
                String[] splitCurrentDate = currentDate.split("/");

                if (splitCurrentDate[2]==splitCreationDate[2]){
                    //Will work for 1 year.
                    if (splitCurrentDate[0] == splitCreationDate[0] &&
                            Integer.parseInt(splitCurrentDate[1]) >
                                    Integer.parseInt(splitCreationDate[1])) {
                        int mode = sal.getSalMode();
                        if (mode == Constants.SAL_MODE_CASH) {
                            int inHandBal = Objects.requireNonNull(getInHandBal().getValue()).getBalance();
                            DeleteInHandBalance();
                            InsertInHandBalance(new inHandBalEntity(inHandBal + sal.getSalary()));
                        } else {
                            int bal = Objects.requireNonNull(getBal().getValue()).getBalance();
                            DeleteBalance();
                            InsertBalance(new balanceEntity(bal + sal.getSalary()));
                        }
                        sal.setCreationDate(Commons.getDate());
                        UpdateSalary(sal);
                    }
                }else{
                    //Next Year.
                    int mode = sal.getSalMode();
                    if (mode == Constants.SAL_MODE_CASH) {
                        int inHandBal = Objects.requireNonNull(getInHandBal().getValue()).getBalance();
                        DeleteInHandBalance();
                        InsertInHandBalance(new inHandBalEntity(inHandBal + sal.getSalary()));
                    } else {
                        int bal = Objects.requireNonNull(getBal().getValue()).getBalance();
                        DeleteBalance();
                        InsertBalance(new balanceEntity(bal + sal.getSalary()));
                    }
                    sal.setCreationDate(Commons.getDate());
                    UpdateSalary(sal);
                }
            }
            else if (sal.getIncType() == Constants.daily) {
                try{
                    String creationDate = sal.getCreationDate();
                    Date dateBefore = sdf.parse(creationDate);
                    Date dateAfter = sdf.parse(Commons.getDate());
                    if (dateBefore!=null&&dateAfter!=null) {
                        long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                        for(int i1 = 1; i1 < daysDiff; i1++){
                            int mode = sal.getSalMode();
                            if (mode == Constants.SAL_MODE_CASH) {
                                int inHandBal = Objects.requireNonNull(getInHandBal().getValue()).getBalance();
                                DeleteInHandBalance();
                                InsertInHandBalance(new inHandBalEntity(inHandBal + sal.getSalary()));
                            } else {
                                int bal = Objects.requireNonNull(getBal().getValue()).getBalance();
                                DeleteBalance();
                                InsertBalance(new balanceEntity(bal + sal.getSalary()));
                            }
                            sal.setCreationDate(Commons.getDate());
                            UpdateSalary(sal);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
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

    public void DeleteBalance(){repo.DeleteBalance();}

    public void InsertInHandBalance(inHandBalEntity entity) {
        repo.InsertInHandBalance(entity);
    }

    public void DeleteInHandBalance(){repo.DeleteInHandBalance();}

    public void InsertSalary(salaryEntity entity) {
        repo.InsertSalary(entity);
    }

    public void UpdateSalary(salaryEntity entity) {
        repo.UpdateSalary(entity);
    }

    public void DeleteSalary(salaryEntity entity){repo.DeleteSalary(entity);}

    public void InsertDebt(debtEntity entity) {
        repo.InsertDebt(entity);
    }

    public void UpdateDebt(debtEntity entity) {
        repo.UpdateDebt(entity);
    }

    public void DeleteDebt(debtEntity entity){repo.DeleteDebt(entity);}

    public void InsertBudget(budgetEntity entity){
        repo.InsertBudget(entity);
    }

    public void UpdateBudget(budgetEntity entity){
        repo.UpdateBudget(entity);
    }

    public void DeleteBudget(){
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

    public LiveData<budgetEntity> getBudget(){
        return getBudget;
    }
}