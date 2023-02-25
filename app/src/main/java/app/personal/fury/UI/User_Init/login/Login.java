package app.personal.fury.UI.User_Init.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Objects;

import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.Utls.Commons;
import app.personal.fury.R;
import app.personal.fury.UI.MainActivity;

public class Login extends AppCompatActivity {

    private userInitViewModel uvm;
    private EditText Email, Password;
    private Button Login;
    private Boolean passvisible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_login);
        setViewModel();
        init();
    }

    private void setViewModel(){
        uvm = new ViewModelProvider(this).get(userInitViewModel.class);
        uvm.getUserId().observe(this, firebaseUser -> {
            if (firebaseUser!=null){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    private void init(){
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());
        Email = findViewById(R.id.l_mail);
        Password = findViewById(R.id.l_pass);
        Login = findViewById(R.id.l_btn);
        TextView f_pass = findViewById(R.id.f_pass);
        f_pass.setOnClickListener(view -> forgotPassword());
        Password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= Password.getRight() - Password.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = Password.getSelectionEnd();
                        if (passvisible==true) {
                            //set icon
                            Password.setCompoundDrawablesRelativeWithIntrinsicBounds (0, 0, R.drawable.common_icon_eyeclose,0);
                            //pass hide
                            Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passvisible = false;
                        } else {
                            //set icon
                            Password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.common_icon_eyeopen,0);
                            //pass show
                            Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passvisible = true;
                        }
                        Password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        Login.setOnClickListener(view -> {
            String email = Email.getText().toString();
            String password = Password.getText().toString();
            if (new Commons().isConnectedToInternet(this)) {
                if (Commons.isValidPass(password) && Commons.isEmail(email)) {
                    uvm.Login(email, password);
                    uvm.getUserId().observe(this, firebaseUser -> {
                        if (Objects.equals(firebaseUser.getEmail(), email)){
                            finishAffinity();
                        }
                    });
                } else {
                    Commons.SnackBar(Login, "Invalid Credentials.");
                }
            }
            else {
                Commons.SnackBar(Login, "No internet available");
            }
        }
        );
    }

    private void forgotPassword(){
        startActivity(new Intent(Login.this,forgot_pass.class));
    }
}