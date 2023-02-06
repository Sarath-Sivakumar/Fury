package app.personal.MVVM.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

import app.personal.MVVM.Repository.AuthRepository;

public class LoggedInUserViewModel extends AndroidViewModel {
//    Collect all user data in here.

    private final AuthRepository authRepo;
    private final MutableLiveData<FirebaseUser> userId;
    private final MutableLiveData<Boolean> isLoggedOut;

    public LoggedInUserViewModel(@NonNull Application application) {
        super(application);
        this.authRepo = new AuthRepository(application);
        userId = authRepo.getUserId();
        isLoggedOut = authRepo.getIsLoggedOutLiveData();
    }

    public void LogOut(){
        authRepo.logout();
    }

    public MutableLiveData<FirebaseUser> getUserId(){
        return userId;
    }

    public MutableLiveData<Boolean> getIsLoggedOut(){
        return isLoggedOut;
    }
}
