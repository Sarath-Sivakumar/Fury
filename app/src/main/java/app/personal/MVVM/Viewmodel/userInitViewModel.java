package app.personal.MVVM.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

import app.personal.MVVM.Entity.userEntity;
import app.personal.MVVM.Repository.AuthRepository;

public class userInitViewModel extends AndroidViewModel {

    private final AuthRepository authRepo;
    private final MutableLiveData<FirebaseUser> userId;

    public userInitViewModel(@NonNull Application application) {
        super(application);
        authRepo = new AuthRepository(application);
        userId = authRepo.getUserId();
    }

    public void Login(String Email, String Password){
        authRepo.login(Email, Password);
    }

    public void Signup(String Email, String Password, userEntity userData){
        authRepo.signup(Email, Password, userData);
    }

    public MutableLiveData<FirebaseUser> getUserId(){
        return userId;
    }
}
