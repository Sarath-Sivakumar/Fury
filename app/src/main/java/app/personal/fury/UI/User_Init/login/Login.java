package app.personal.fury.UI.User_Init.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;

import app.personal.MVVM.Entity.LaunchChecker;
import app.personal.MVVM.Viewmodel.AppUtilViewModel;
import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.Utls.Commons;
import app.personal.fury.R;
import app.personal.fury.UI.MainActivity;

public class Login extends AppCompatActivity {

    private userInitViewModel uvm;
    private AppUtilViewModel appVM;
    private EditText Email, Password;
    private Button Login;
    private ProgressBar progress;
    private Boolean passVisible =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_login);
        setViewModel();
        init();
    }

    private void setViewModel(){
        uvm = new ViewModelProvider(this).get(userInitViewModel.class);
        appVM = new ViewModelProvider(this).get(AppUtilViewModel.class);
        uvm.getUserId().observe(this, firebaseUser -> {
            if (firebaseUser!=null){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(){
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());
        Email = findViewById(R.id.l_mail);
        Password = findViewById(R.id.l_pass);
        Login = findViewById(R.id.l_btn);
        progress = findViewById(R.id.progress);
        TextView f_pass = findViewById(R.id.f_pass);
        f_pass.setOnClickListener(view -> forgotPassword());
        observeForError();
        Password.setOnTouchListener((v, event) -> {
            final int Right=2;
            if(event.getAction()==MotionEvent.ACTION_UP) {
                if (event.getRawX() >= Password.getRight() - Password.getCompoundDrawables()[Right].getBounds().width()) {
                    int selection = Password.getSelectionEnd();
                    if (passVisible) {
                        //set icon
                        Password.setCompoundDrawablesRelativeWithIntrinsicBounds (0, 0, R.drawable.common_icon_eyeclose,0);
                        //pass hide
                        Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passVisible = false;
                    } else {
                        //set icon
                        Password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.common_icon_eyeopen,0);
                        //pass show
                        Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passVisible = true;
                    }
                    Password.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        Login.setOnClickListener(view -> {
            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();
            if (Commons.isConnectedToInternet(this)) {
                if (Commons.isValidPass(password) && Commons.isEmail(email)) {
                    progress.setVisibility(View.VISIBLE);
                    uvm.Login(email, password);
                    uvm.getUserId().observe(this, firebaseUser -> {
                        if (Objects.equals(firebaseUser.getEmail(), email)){
                            appVM.getCheckerData().observe(this, launchChecker1 -> {
                                if (launchChecker1.getTimesLaunched()==0){
                                    appVM.UpdateLaunchChecker(new LaunchChecker(launchChecker1.getId(),
                                            launchChecker1.getTimesLaunched()+1));
                                    finishAffinity();
                                    appVM.getCheckerData().removeObservers(this);
                                }
                            });
                        }else{
                            progress.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Commons.SnackBar(Login, "Invalid Credentials!");
                    progress.setVisibility(View.GONE);
                }
            }
            else {
                Commons.SnackBar(Login, "No internet available");
            }
        }
        );
    }

    private void observeForError(){
        uvm.getFirebaseError().observe(this, s -> {
            if (!s.equals("Null")){
                Commons.SnackBar(progress, s);
                if (progress.getVisibility()==View.VISIBLE){
                    progress.setVisibility(View.GONE);
                }
                uvm.setDefaultError();
            }
        });
    }

    private void forgotPassword(){
        startActivity(new Intent(Login.this, app.personal.fury.UI.User_Init.login.forgot_pass.class));
    }
}