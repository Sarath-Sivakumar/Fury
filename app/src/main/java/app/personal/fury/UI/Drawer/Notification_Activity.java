package app.personal.fury.UI.Drawer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import app.personal.fury.R;
import app.personal.fury.UI.Adapters.notificationAdapter.notificationAdapter;

public class Notification_Activity extends AppCompatActivity {

    private ImageButton back;
    //ListView for notifications..
    private RecyclerView list;
    private notificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_notification);
        init();
    }

    private void init(){
        findView();
        OnClick();
        setNoti();
    }

    private void findView(){
        back = findViewById(R.id.nBack);
        list = findViewById(R.id.notiList);
        adapter = new notificationAdapter();
    }

    private void setNoti(){
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setHasFixedSize(true);
        list.setAdapter(adapter);
    }

    private void OnClick(){
        back.setOnClickListener(v -> finish());
    }
}