package app.personal.fury.UI.User_Init.signUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import app.personal.MVVM.Entity.userEntity;
import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.MainActivity;
import app.personal.fury.UI.User_Init.login.Login;

public class signUp extends AppCompatActivity {

    private EditText Name, email, pass1, pass2;
    private Button signUp;
    private userInitViewModel uvm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_sign_up);
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
        ImageButton back = findViewById(R.id.cBack);
        back.setOnClickListener(view -> finish());
        TextView toLogin = findViewById(R.id.to_login);
        toLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, Login.class));
            finish();
        });
        Name = findViewById(R.id.u_name);
        email = findViewById(R.id.u_mail);
        pass1 = findViewById(R.id.u_pass1);
        pass2 = findViewById(R.id.u_pass2);
        signUp = findViewById(R.id.s_btn);
        signUp.setOnClickListener(view -> {
            String name = Name.getText().toString();
            String mail = email.getText().toString();
            String p1 = pass1.getText().toString();
            String p2 = pass2.getText().toString();
            if (new Commons().isConnectedToInternet(this)) {
                if (!name.trim().isEmpty() && !mail.trim().isEmpty() &&
                        !p1.trim().isEmpty() && !p2.trim().isEmpty()) {
                    if (p1.trim().equals(p2.trim())) {
                        if (Commons.isEmail(mail)) {
                            if (Commons.isValidPass(p1.trim())) {
                                userEntity userData = new userEntity();
                                userData.setName(name);
                                userData.setImgUrl(Constants.DEFAULT_DP);
                                uvm.Signup(mail, p1, userData);
                                uvm.getUserId().observe(this, firebaseUser -> {
                                    if (firebaseUser.getEmail().equals(mail)){
                                        finishAffinity();
                                    }
                                });
                            } else {
                                Commons.SnackBar(signUp, "Password should be at least 6 characters.");
                                pass1.setText("");
                                pass2.setText("");
                            }
                        } else {
                            Commons.SnackBar(signUp, "Check your email again");
                        }
                    } else {
                        Commons.SnackBar(signUp, "Passwords don't match.");
                        pass1.setText("");
                        pass2.setText("");
                    }
                }
            }
            else
            {
                Commons.SnackBar(signUp, "No internet available");
            }
        });

    }
}