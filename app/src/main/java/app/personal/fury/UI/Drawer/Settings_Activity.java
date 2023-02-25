package app.personal.fury.UI.Drawer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import app.personal.fury.R;

public class Settings_Activity extends AppCompatActivity {

    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_settings);
        init();
    }

        private void init(){
            findView();
            OnClick();
        }

        private void findView(){
            back = findViewById(R.id.nBack);
        }

        private void OnClick(){
            back.setOnClickListener(v -> finish());
        }
    }