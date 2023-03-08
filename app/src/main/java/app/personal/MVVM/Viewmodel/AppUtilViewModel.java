package app.personal.MVVM.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import app.personal.MVVM.Repository.*;
import app.personal.MVVM.Entity.LaunchChecker;

public class AppUtilViewModel extends AndroidViewModel {

    private LiveData<LaunchChecker> checkerData;
    private AppUtilRepository repo;

    public AppUtilViewModel(@NonNull Application application) {
        super(application);
        repo = new AppUtilRepository(application);
        checkerData = repo.getCheckerData();
    }

    public void InsertLaunchChecker(LaunchChecker launchChecker){
        repo.InsertLaunchChecker(launchChecker);
    }

    public void UpdateLaunchChecker(LaunchChecker launchChecker){
        repo.UpdateLaunchChecker(launchChecker);
    }

    public LiveData<LaunchChecker> getCheckerData(){
        return checkerData;
    }
}
