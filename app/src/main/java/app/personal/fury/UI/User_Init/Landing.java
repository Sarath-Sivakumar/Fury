package app.personal.fury.UI.User_Init;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;

import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.User_Init.login.Login;
import app.personal.fury.UI.User_Init.signUp.signUp;

public class Landing extends AppCompatActivity {

    private mainViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_landing);
        init();
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String code;
        if (!tm.getSimCountryIso().isEmpty()) {
            code = tm.getSimCountryIso();
            vm.setCountryCode(code);
            vm.initCurrency();
            vm.getRupee().observe(this, Constants::setRUPEE);
        } else {
            code = tm.getNetworkCountryIso();
            vm.setCountryCode(code);
            vm.initCurrency();
            vm.getRupee().observe(this, Constants::setRUPEE);
        }
    }

    private void init() {
        vm = new ViewModelProvider(this).get(mainViewModel.class);
        Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signup);
        login.setOnClickListener(v -> startActivity(new Intent(this, Login.class)));
        signup.setOnClickListener(v -> startActivity(new Intent(this, signUp.class)));
    }

}