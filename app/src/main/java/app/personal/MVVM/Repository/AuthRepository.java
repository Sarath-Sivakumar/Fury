package app.personal.MVVM.Repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository{

    private final Application application;
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<Boolean> isLoggedOutLiveData;

    public AuthRepository(Application application) {
        this.application = application;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userLiveData = new MutableLiveData<>();
        this.isLoggedOutLiveData = new MutableLiveData<>();

        if (firebaseAuth.getCurrentUser() != null) {
            userLiveData.postValue(firebaseAuth.getCurrentUser());
            isLoggedOutLiveData.postValue(false);
        } else {
            isLoggedOutLiveData.postValue(true);
        }
    }

    public void login(String Email, String Password) {
        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(application.getMainExecutor(),
                task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                        isLoggedOutLiveData.postValue(false);
                    } else {
                        isLoggedOutLiveData.postValue(true);
//                            Commons.SnackBar(view, "Login Failed");
                        Log.e("AuthRepository", "login: "+task.getException().getMessage());
                    }
                });
    }

    public void signup(String Email, String Password) {
        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(application.getMainExecutor(),
                task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                        isLoggedOutLiveData.postValue(false);
                    } else {
                        isLoggedOutLiveData.postValue(true);
//                            Commons.SnackBar(view, "Signup Failed");
                        Log.e("AuthRepository", "SignUp: "+task.getException().getMessage());
                    }
                });
    }

    public void logout() {
        firebaseAuth.signOut();
        isLoggedOutLiveData.postValue(true);
    }

    public MutableLiveData<FirebaseUser> getUserId(){
        return userLiveData;
    }

    public MutableLiveData<Boolean> getIsLoggedOutLiveData(){
        return isLoggedOutLiveData;
    }
}
