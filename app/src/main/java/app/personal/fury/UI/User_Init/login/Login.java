package app.personal.fury.UI.User_Init.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import app.personal.MVVM.Viewmodel.userInitViewModel;
import app.personal.Utls.Commons;
import app.personal.fury.R;
import app.personal.fury.UI.MainActivity;

public class Login extends AppCompatActivity {

    private userInitViewModel uvm;
    private EditText Email, Password;
    private Button Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        Login.setOnClickListener(view -> {
            String email = Email.getText().toString();
            String password = Password.getText().toString();
            if (Commons.isValidPass(password)&&Commons.isEmail(email)){
                uvm.Login(email, password);
            }else{
                Commons.SnackBar(Login, "Invalid Credentials.");
            }
        });
    }

    private void forgotPassword(){
        startActivity(new Intent(Login.this,forgot_pass.class));
    }
}