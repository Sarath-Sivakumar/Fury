package app.personal.fury.UI.User_Init;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import app.personal.MVVM.Viewmodel.AppUtilViewModel;
import app.personal.fury.R;
import app.personal.fury.UI.User_Init.login.Login;
import app.personal.fury.UI.User_Init.signUp.signUp;

public class Landing extends AppCompatActivity {

    private AppUtilViewModel appVM;
    private Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_landing);
        init();
//        Tutorial();
    }

    private void init() {
        Button login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        login.setOnClickListener(v -> startActivity(new Intent(this, Login.class)));
        signup.setOnClickListener(v -> startActivity(new Intent(this, signUp.class)));
    }

//    private void Tutorial(){
//        appVM = new ViewModelProvider(this).get(AppUtilViewModel.class);
//        appVM.getCheckerData().observe(this,launchChecker -> {
//            try{
////                Init Tutorial here
//                if (launchChecker.getTimesLaunched()==0){
//                    MaterialTapTargetPrompt.Builder builder = new MaterialTapTargetPrompt.Builder(this);
//                    builder.setTarget(signup);
//                    builder.setPrimaryTextGravity(Gravity.CENTER);
//                    builder.setSecondaryTextGravity(Gravity.CENTER);
//                    builder.setTextGravity(Gravity.CENTER);
//                    builder.setPrimaryText("Welcome to Fury!");
//                    builder.setSecondaryText("If you're here for the first time,please sign up and join our community.");
//                    builder.setBackgroundColour(getResources().getColor(R.color.d2));
//                    builder.setFocalColour(getResources().getColor(R.color.l1));
//                    builder.setBackButtonDismissEnabled(false);
//                    builder.setPromptStateChangeListener((prompt, state) -> {
//                        //Use if needed
//                    });
//                    builder.show();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//                appVM.InsertLaunchChecker(new LaunchChecker(0));
//            }
//        });
//    }
}