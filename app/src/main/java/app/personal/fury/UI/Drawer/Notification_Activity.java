package app.personal.fury.UI.Drawer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import app.personal.fury.R;

public class Notification_Activity extends AppCompatActivity {

    private ImageButton back;
    //ListView for notifications..
    private ListView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        init();
    }

    private void init(){
        findView();
        OnClick();
    }

    private void findView(){
        back = findViewById(R.id.nBack);
        list = findViewById(R.id.notificationList);
    }

    private void OnClick(){
        back.setOnClickListener(v -> finish());
    }
}