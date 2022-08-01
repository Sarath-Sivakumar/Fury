package app.personal.fury.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Constants;
import app.personal.Utls.ViewPager.viewPager;
import app.personal.fury.R;
import app.personal.fury.ViewPagerAdapter.vpAdapter;

public class MainActivity extends AppCompatActivity {

    //this is a comment
    private mainViewModel vm;
    private viewPager vp;
    private TabLayout tl;
    private TextView toolbarTitle;
    private LiveData<balanceEntity> getBalance;
    private LiveData<debtEntity> getDebt;
    private LiveData<List<expEntity>> getExp;
    private LiveData<salaryEntity> getSalary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewModel();
        init();
    }

    private void init() {
        //init AD here..
        findView();
        initViewPager();
    }

    private void findView() {
        vp = findViewById(R.id.viewPager);
        tl = findViewById(R.id.tabLayout);
        toolbarTitle = findViewById(R.id.ab_title);
    }

    private void initViewModel() {
        vm = new ViewModelProvider(this)
                .get(mainViewModel.class);
        vm.getBalance().observe(this, entity -> {
            //Balance updates here..
            Log.e("VM", "Balance");
        });

        vm.getDebt().observe(this, entity -> {
            //Debt updates here
            Log.e("VM", "Debt");
        });

        vm.getExp().observe(this, entity -> {
            //Exp updates here
            Log.e("VM", "Exp");
        });

        vm.getSalary().observe(this, entity -> {
            //Salary updates here
            Log.e("VM", "Salary");
        });
    }

    private void initViewPager() {
        vpAdapter adapter = new vpAdapter(getSupportFragmentManager());

        //Add fragments here
        adapter.addFragment(new Dues_Debt(), Constants.Dues);
        adapter.addFragment(new Exp_Tracker(), Constants.Exp);
        adapter.addFragment(new Salary_Planner(), Constants.Salary);
        adapter.addFragment(new Salary_Planner(), Constants.Salary);
        adapter.addFragment(new Salary_Planner(), Constants.Salary);

        vp.setAdapter(adapter);
        vp.setPagingEnabled(false);
        tl.setupWithViewPager(vp, true);
        toolbarTitle.setText(Objects.requireNonNull(tl.getTabAt(0)).getText());
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
                toolbarTitle.setText(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                toolbarTitle.setText(tab.getText());
            }
        });

//        Setting icons for items in tabLayout
        for (int i = 0; i < tl.getTabCount(); i++) {
//            Use after all icons available
            CharSequence text = Objects.requireNonNull(tl.getTabAt(i)).getText();
            if (text != null) {
                if (Constants.Exp.contentEquals(text)) {
                    Objects.requireNonNull(tl.getTabAt(i)).setIcon(R.drawable.ic_home);
                } else if (Constants.Dues.contentEquals(text)) {
                    Objects.requireNonNull(tl.getTabAt(i)).setIcon(R.drawable.ic_home);
                } else if (Constants.Salary.contentEquals(text)) {
                    Objects.requireNonNull(tl.getTabAt(i)).setIcon(R.drawable.ic_home);
                }
            }else{
                Log.e(Constants.mActivityLog,"Tab text null");
            }
        }
    }
}