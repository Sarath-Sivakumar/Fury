package app.personal.MVVM.Repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import app.personal.MVVM.Entity.userEntity;
import app.personal.Utls.Constants;

public class AuthRepository{

    private final Application application;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance(Constants.DB_INSTANCE);
    private final DatabaseReference userDataRef = db.getReference(Constants.Users);
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<Boolean> isLoggedOutLiveData;
    private final MutableLiveData<userEntity> userData;

    public AuthRepository(Application application) {
        this.application = application;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userLiveData = new MutableLiveData<>();
        this.isLoggedOutLiveData = new MutableLiveData<>();
        this.userData = new MutableLiveData<>();

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
                        fetchUserData();
                    } else {
                        isLoggedOutLiveData.postValue(true);
                        Log.e("AuthRepository", "login: "+task.getException().getMessage());
                    }
                });
    }

    private void putDp(){

    }

    public void insertUserData(userEntity userData){
        userLiveData.postValue(firebaseAuth.getCurrentUser());
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", userData.getName());
        map.put("imgUrl", userData.getImgUrl());
        String user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        Log.e("Firebase", "signup: User: "+user);
        db.getReference(Constants.Users).child(user)
                .setValue(map, (error, ref) -> {
                    if (error==null){
                        isLoggedOutLiveData.postValue(false);
                    }else{
                        Log.e("Firebase", "signup: Data add error: "+error.getMessage());
                    }
                });
    }

    public void fetchUserData(){
        userDataRef.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userEntity data = snapshot.getValue(userEntity.class);
                        userData.postValue(data);
                        isLoggedOutLiveData.postValue(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void signup(String Email, String Password, userEntity userData) {
        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(application.getMainExecutor(),
                task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                        insertUserData(userData);
                    } else {
                        isLoggedOutLiveData.postValue(true);
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
    public MutableLiveData<userEntity> getUserData(){
        try{
            fetchUserData();
        }catch(Exception e){
            e.printStackTrace();
        }
        return userData;
    }
    public MutableLiveData<Boolean> getIsLoggedOutLiveData(){
        return isLoggedOutLiveData;
    }
}
