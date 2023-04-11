package app.personal.MVVM.Repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import app.personal.MVVM.Entity.update;
import app.personal.MVVM.Entity.userEntity;
import app.personal.Utls.Constants;

public class AuthRepository {

    private final Application application;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance(Constants.DB_INSTANCE);
    private final DatabaseReference userDataRef;
    private final DatabaseReference updateDataRef;
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<Boolean> isLoggedOutLiveData;
    private final MutableLiveData<userEntity> userData;
    private final MutableLiveData<String> FirebaseAuthError;
    private final MutableLiveData<Integer> Update;
    private final String default_Error = "Null";
    private final FirebaseStorage storage;
    private final StorageReference storageReference;

    public AuthRepository(Application application) {
        this.application = application;

        userDataRef = db.getReference(Constants.Users);
        updateDataRef = db.getReference(Constants.AppVersion);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userLiveData = new MutableLiveData<>();
        this.isLoggedOutLiveData = new MutableLiveData<>();
        this.userData = new MutableLiveData<>();
        this.FirebaseAuthError = new MutableLiveData<>();
        this.Update = new MutableLiveData<>();

        this.FirebaseAuthError.postValue(default_Error);
        this.Update.postValue(Constants.UpdateNotAvailable);

        if (firebaseAuth.getCurrentUser() != null) {
            userLiveData.postValue(firebaseAuth.getCurrentUser());
            isLoggedOutLiveData.postValue(false);
        } else {
            isLoggedOutLiveData.postValue(true);
        }
        this.storage = FirebaseStorage.getInstance();
        this.storageReference = storage.getReference();
    }

    public void login(String Email, String Password) {
        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(application.getMainExecutor(),
                task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                        fetchUserData();
                    } else {
                        isLoggedOutLiveData.postValue(true);
                        FirebaseAuthError.postValue(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void InsertProfilePic(userEntity entity, Uri filePath) {
        StorageReference ref = storageReference.child(firebaseAuth.getCurrentUser()
                + "images/" + UUID.randomUUID().toString());
        if (!entity.getImgUrl().equals(Constants.DEFAULT_DP)) {
            StorageReference photoRef = storage.getReferenceFromUrl(entity.getImgUrl());
            photoRef.delete().addOnSuccessListener(aVoid -> {
                // File deleted successfully
                Log.d("DP Delete", "onSuccess: deleted file");
                ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                    if (taskSnapshot.getError() == null) {
                        Log.e("Firebase", "Image uploaded!");
                        ref.getDownloadUrl().addOnSuccessListener(uri -> UpdateDBDP(uri.toString(), entity.getName()));

                    } else {
                        FirebaseAuthError.postValue(Objects.requireNonNull(taskSnapshot.getError()).getMessage());
                        Log.e("Firebase", "Image error: " + taskSnapshot.getError().getMessage());
                    }
                });
            }).addOnFailureListener(exception -> {
                // Uh-oh, an error occurred!
                FirebaseAuthError.postValue(Objects.requireNonNull(exception.getMessage()));
                Log.d("DP Delete", "onFailure: did not delete file");
            });
        } else {

            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                if (taskSnapshot.getError() == null) {
                    Log.e("Firebase", "Image uploaded!");
                    ref.getDownloadUrl().addOnSuccessListener(uri -> UpdateDBDP(uri.toString(), entity.getName()));

                } else {
                    FirebaseAuthError.postValue(Objects.requireNonNull(taskSnapshot.getError().getMessage()));
                    Log.e("Firebase", "Image error: " + taskSnapshot.getError().getMessage());
                }
            });
        }

    }

    public void UpdateUserData(userEntity userEntity) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", userEntity.getName());
        map.put("imgUrl", userEntity.getImgUrl());
        String user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        db.getReference(Constants.Users).child(user)
                .setValue(map, (error, ref) -> {
                    if (error != null) {
                        FirebaseAuthError.postValue(Objects.requireNonNull(error.getMessage()));
                    }
                });
    }

    private void UpdateDBDP(String uri, @NonNull String userName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", userName);
        map.put("imgUrl", uri);
        String user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        db.getReference(Constants.Users).child(user)
                .setValue(map, (error, ref) -> {
                    if (error != null) {
                        FirebaseAuthError.postValue(Objects.requireNonNull(error.getMessage()));
                    }
                });
    }

    public void insertUserData(userEntity userData) {
        userLiveData.postValue(firebaseAuth.getCurrentUser());
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", userData.getName());
        map.put("imgUrl", userData.getImgUrl());
        String user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        Log.e("Firebase", "signup: User: " + user);
        db.getReference(Constants.Users).child(user)
                .setValue(map, (error, ref) -> {
                    if (error == null) {
                        isLoggedOutLiveData.postValue(false);
                    } else {
                        FirebaseAuthError.postValue(Objects.requireNonNull(error.getMessage()));
                        Log.e("Firebase", "signup: Data add error: " + error.getMessage());
                    }
                });
    }

    public void checkForUser() {
        userLiveData.postValue(firebaseAuth.getCurrentUser());
    }

    public void fetchUserData() {
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
                        FirebaseAuthError.postValue(Objects.requireNonNull(error.getMessage()));
                    }
                });
    }

    public void updateListener() {
        updateDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                update data = snapshot.getValue(update.class);
                if (data != null) {
                    if (data.getVersion().equals(Constants.APP_VERSION)) {
                        Update.postValue(Constants.UpdateNotAvailable);
                    } else {
                        Update.postValue(Constants.UpdateAvailable);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseAuthError.postValue(Objects.requireNonNull(error.getMessage()));
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
                        Log.e("AuthRepository", "SignUp: " + Objects.requireNonNull(task.getException()).getMessage());
                        FirebaseAuthError.postValue(Objects.requireNonNull(task.getException().getMessage()));
                    }
                });
    }

    public void logout() {
        firebaseAuth.signOut();
        isLoggedOutLiveData.postValue(true);
    }

    public MutableLiveData<FirebaseUser> getUserId() {
        return userLiveData;
    }

    public MutableLiveData<Integer> getUpdate() {
        return Update;
    }

    public MutableLiveData<userEntity> getUserData() {
        try {
            fetchUserData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userData;
    }

    public void deleteUserData(String email, String password) {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        if (user != null) {
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.e("Account Termination", "ReAuth Complete");
                    deleteStorageData();
                } else {
                    FirebaseAuthError.postValue(Objects.requireNonNull(task.getException()).getMessage());
                    Log.e("Account Termination", "ReAuth Error: "
                            + Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        }
    }

    private void deleteStorageData() {
        userEntity entity = userData.getValue();
        if (entity != null) {
            if (!entity.getImgUrl().equals(Constants.DEFAULT_DP)) {
                StorageReference photoRef = storage.getReferenceFromUrl(entity.getImgUrl());
                photoRef.delete().addOnSuccessListener(aVoid -> {
                    // File deleted successfully
                    Log.e("Account Termination", "User DP deleted.");
                    deleteDatabaseData();
                }).addOnFailureListener(exception -> {
                    // Uh-oh, an error occurred!
                    FirebaseAuthError.postValue(exception.getMessage());
                    Log.e("Account Termination", "DP Error: " + exception.getMessage());
                });
            } else {
                Log.e("Account Termination", "No DP to delete");
                deleteDatabaseData();
            }
        }
    }

    private void deleteDatabaseData() {
        userDataRef.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).setValue(null,
                (error, ref) -> {
                    if (error == null) {
                        Log.e("Account Termination", "User data deleted.");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        logout();
                        deleteAccount(user);
                    } else {
                        Log.e("Account Termination", "DB Error: " + error.getMessage());
                        FirebaseAuthError.postValue(error.getMessage());
                    }
                });
    }

    private void deleteAccount(FirebaseUser user) {
        user.delete().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Log.e("Account Termination", "User account deleted.");
                //Account Deletes here..
            } else {
                deleteAccount(user);
                Log.e("Account Termination", "Account Error: " + Objects.requireNonNull(task1.getException()).getMessage());
                FirebaseAuthError.postValue(Objects.requireNonNull(task1.getException()).getMessage());
            }
        });
    }

    public MutableLiveData<String> getFirebaseError() {
        return FirebaseAuthError;
    }

    public MutableLiveData<Boolean> getIsLoggedOutLiveData() {
        return isLoggedOutLiveData;
    }

    public void setDefaultError() {
        FirebaseAuthError.postValue(default_Error);
    }
}
