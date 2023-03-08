package app.personal.MVVM.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import app.personal.MVVM.DB.localDB;
import app.personal.MVVM.Dao.localDao;
import app.personal.MVVM.Entity.LaunchChecker;

public class AppUtilRepository {

    private final localDao dao;
    private final LiveData<LaunchChecker> checkerData;

    public AppUtilRepository(Application application){
        localDB db = localDB.getInstance(application);
        dao = db.dao();
        checkerData = dao.getChecker();
    }

    public void InsertLaunchChecker(LaunchChecker launchChecker){
        new InsertLaunchCheckerAsync(dao).execute(launchChecker);
    }

    public void UpdateLaunchChecker(LaunchChecker launchChecker){
        new UpdateLaunchCheckerAsync(dao).execute(launchChecker);
    }

    public LiveData<LaunchChecker> getCheckerData(){
        return checkerData;
    }

    private static class InsertLaunchCheckerAsync extends AsyncTask<LaunchChecker,Void,Void> {
        private  localDao dao;
        private InsertLaunchCheckerAsync(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(LaunchChecker... entities) {
            dao.InsertLaunchChecker(entities[0]);
            return null;
        }
    }

    private static class UpdateLaunchCheckerAsync extends AsyncTask<LaunchChecker,Void,Void> {
        private  localDao dao;
        private UpdateLaunchCheckerAsync(localDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(LaunchChecker... entities) {
            dao.UpdateLaunchChecker(entities[0]);
            return null;
        }
    }
}
