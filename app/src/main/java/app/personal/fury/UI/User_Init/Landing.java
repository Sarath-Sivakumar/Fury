package app.personal.fury.UI.User_Init;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Currency;
import app.personal.fury.R;
import app.personal.fury.UI.User_Init.login.Login;
import app.personal.fury.UI.User_Init.signUp.signUp;

public class Landing extends AppCompatActivity {

//    private mainViewModel vm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_landing);
        init();
//        vm.getRupee().observe(this, String->{
//            Log.e("Symbol", String);
//            new Currency().setCurrency(String);
//        });
    }

    private void init() {
//        vm = new ViewModelProvider(this).get(mainViewModel.class);
        Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signup);
        login.setOnClickListener(v -> startActivity(new Intent(this, Login.class)));
        signup.setOnClickListener(v -> startActivity(new Intent(this, signUp.class)));
    }

}