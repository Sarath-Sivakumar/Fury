package app.personal.fury.UI.User_Init;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import app.personal.fury.R;
import app.personal.fury.UI.User_Init.login.Login;
import app.personal.fury.UI.User_Init.signUp.signUp;

public class Landing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_landing);
        init();
    }

    private void init() {
        Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signup);
        login.setOnClickListener(v -> startActivity(new Intent(this, Login.class)));
        signup.setOnClickListener(v -> startActivity(new Intent(this, signUp.class)));
    }

}