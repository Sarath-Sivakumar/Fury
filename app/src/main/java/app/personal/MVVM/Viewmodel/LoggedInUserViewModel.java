package app.personal.MVVM.Viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

import app.personal.MVVM.Entity.userEntity;
import app.personal.MVVM.Repository.AuthRepository;

public class LoggedInUserViewModel extends AndroidViewModel {
//    Collect all userEntity data in here.

    private final AuthRepository authRepo;
    private final MutableLiveData<FirebaseUser> userId;
    private final MutableLiveData<Boolean> isLoggedOut;
    private final MutableLiveData<userEntity> userData;
    private final MutableLiveData<String> error;

    public LoggedInUserViewModel(@NonNull Application application) {
        super(application);
        this.authRepo = new AuthRepository(application);
        userId = authRepo.getUserId();
        userData = authRepo.getUserData();
        isLoggedOut = authRepo.getIsLoggedOutLiveData();
        error = authRepo.getFirebaseError();
    }

    public void LogOut() {
        authRepo.logout();
    }

    public void update() {
        authRepo.updateListener();
    }

    public void DeleteAccount(String Email, String Password) {
        authRepo.deleteUserData(Email, Password);
    }

    public MutableLiveData<FirebaseUser> getUserId() {
        return userId;
    }

    public MutableLiveData<Integer> updateListener() {
        return authRepo.getUpdate();
    }

    public void InsertProfilePic(Uri filePath, userEntity entity) {
        authRepo.InsertProfilePic(entity, filePath);
    }

    public void UpdateUserData(userEntity userEntity) {
        authRepo.UpdateUserData(userEntity);
    }

    public MutableLiveData<userEntity> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getIsLoggedOut() {
        return isLoggedOut;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public void setDefaultError() {
        authRepo.setDefaultError();
    }
}
