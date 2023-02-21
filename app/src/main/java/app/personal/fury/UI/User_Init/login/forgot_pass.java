package app.personal.fury.UI.User_Init.login;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import app.personal.Utls.Commons;
import app.personal.fury.R;

public class forgot_pass extends AppCompatActivity {

    private EditText email_add;
    private Button resetpass;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        email_add = findViewById(R.id.email);
        resetpass = findViewById(R.id.passchange);

        auth = FirebaseAuth.getInstance();

        resetpass.setOnClickListener(view -> resetpassword());

    }
    private void resetpassword(){
        String email = email_add.getText().toString().trim();

        if (new Commons().isConnectedToInternet(this)) {
            if (email.isEmpty()) {
                email_add.setError("Provide an email address!");
                email_add.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                email_add.setError("Please provide a valid email!");
                email_add.requestFocus();
                return;
            }
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(forgot_pass.this, "Check your email to reset your password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(forgot_pass.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Commons.SnackBar(resetpass, "No internet available");
        }
    }

}

