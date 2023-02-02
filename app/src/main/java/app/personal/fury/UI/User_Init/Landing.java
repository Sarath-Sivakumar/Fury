package app.personal.fury.UI.User_Init;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import app.personal.fury.R;
import app.personal.fury.UI.MainActivity;

public class Landing extends AppCompatActivity {

    private Button login,signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        init();
    }

    private void init() {
        login = findViewById(R.id.login);
//        signup = findViewById(R.id.signup);
        login.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
//        signup.setOnClickListener(v -> startActivity(new Intent(this, signUp.class)));
    }
}