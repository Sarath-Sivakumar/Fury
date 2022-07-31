package app.personal.fury;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.fury3.R;

public class MainActivity extends AppCompatActivity {

    private mainViewModel vm;
    private LiveData<balanceEntity> getBalance;
    private LiveData<debtEntity> getDebt;
    private LiveData<List<expEntity>> getExp;
    private LiveData<salaryEntity> getSalary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewModel();
    }

    private void initViewModel() {
        vm = new ViewModelProvider(this)
                .get(mainViewModel.class);
        vm.getBalance().observe(this, entity -> {
            //Balance updates here..
            Log.e("VM","Balance");
        });

        vm.getDebt().observe(this, entity -> {
            //Debt updates here
            Log.e("VM","Debt");
        });

        vm.getExp().observe(this, entity -> {
            //Exp updates here
            Log.e("VM","Exp");
        });

        vm.getSalary().observe(this, entity -> {
            //Salary updates here
            Log.e("VM","Salary");
        });
    }
}