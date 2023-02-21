package app.personal.fury.UI.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Entity.salaryEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.salaryList.salaryAdapter;
import app.personal.fury.UI.IG_fragment.ig;
import app.personal.fury.ViewPagerAdapter.infoGraphicsAdapter;

public class Ear_Tracker extends Fragment {
    //Daily = 1, Monthly = 0, Hourly = -1, oneTime = ?(To be implemented in a future update).

    private RecyclerView salSplitList;
    private FloatingActionButton addSal;
    private mainViewModel vm;
    private salaryAdapter adapter;
    private TextView salAmt, InhandAmt, AccountAmt, InhandCount, AccountCount;
    private RecyclerView.ViewHolder ViewHolder;
    private ViewPager ig_vp;
    private TabLayout ig_tl;
    private infoGraphicsAdapter igAdapter;
    private ArrayList<Fragment> FragmentList;

    public Ear_Tracker() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        adapter = new salaryAdapter();
        //Comes before onCreateView
        //initialise methods that don't require activity or context
    }

    private void initAd() {
//        Init ad here if necessary else delete method.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_earnings__tracker, container, false);
        initAd();
        findView(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String s = Constants.RUPEE + getSalary();
        salAmt.setText(s);
        setIG_VP();
    }

    private void findView(View v) {
        salAmt = v.findViewById(R.id.salAmt);
        salSplitList = v.findViewById(R.id.salList);
        addSal = v.findViewById(R.id.addSal);
        addSal.setOnClickListener(v1 -> callPopUpWindow(false, null));
        salSplitList.setLayoutManager(new LinearLayoutManager(requireContext()));
        salSplitList.setHasFixedSize(true);
        salSplitList.setAdapter(adapter);
        InhandAmt = v.findViewById(R.id.inhand_Amt);
        InhandCount = v.findViewById(R.id.inhand_count);
        AccountAmt = v.findViewById(R.id.account_amt);
        AccountCount = v.findViewById(R.id.account_count);
        ig_vp = v.findViewById(R.id.infoGraphics_earvp);
        ig_tl = v.findViewById(R.id.infoGraphics_ear);
        igAdapter = new infoGraphicsAdapter(getParentFragmentManager());
        FragmentList = new ArrayList<>();
        touchHelper();
    }

    private void setIG_VP() {
        FragmentList.add(ig.newInstance(R.drawable.info_h1));
        FragmentList.add(ig.newInstance(R.drawable.info_h2));
        FragmentList.add(ig.newInstance(R.drawable.info_h3));
        FragmentList.add(ig.newInstance(R.drawable.info_h4));
        FragmentList.add(ig.newInstance(R.drawable.info_h5));
        FragmentList.add(ig.newInstance(R.drawable.info_h6));
        igAdapter.setInfoGraphics(FragmentList);
        ig_vp.setAdapter(igAdapter);
        ig_tl.setupWithViewPager(ig_vp, true);
    }


    @SuppressLint("SetTextI18n")
    private void callPopUpWindow(boolean isEdit, @Nullable salaryEntity salary) {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View v = inflater.inflate(R.layout.add_exp_item, null);
        popupWindow.setContentView(v);

//        Total elements--------------------------------------------------
        TextView popupTitle, salModeTitle;
        FrameLayout expName;
        EditText salSource, salAmt, salDate;
        RadioGroup rdGrp1, rdGrp2;
        Button yes, no;
        String date;
//        ----------------------------------------------------------------
//        Init Views------------------------------------------------------
        popupTitle = v.findViewById(R.id.title);
        popupTitle.setText("Add Earnings");
        expName = v.findViewById(R.id.expNameTitle);
        expName.setVisibility(View.GONE);
        salDate = v.findViewById(R.id.salDate);
        salSource = v.findViewById(R.id.salSrc);
        salAmt = v.findViewById(R.id.expAmt);
        rdGrp2 = v.findViewById(R.id.RadioGroup2);
        rdGrp1 = v.findViewById(R.id.RadioGroup);
        yes = v.findViewById(R.id.add_yes);
        no = v.findViewById(R.id.add_no);
        no.setOnClickListener(v1 -> popupWindow.dismiss());
        salModeTitle = v.findViewById(R.id.radioTitle2);
//vm.DeleteBalance();
//vm.DeleteInHandBalance();
//        ----------------------------------------------------------------
        if (!isEdit){
            salDate.setVisibility(View.GONE);
            date = Commons.getDate();
        }else{
            rdGrp2.setVisibility(View.GONE);
            salModeTitle.setVisibility(View.GONE);
            salSource.setText(salary.getIncName());
            salAmt.setText(String.valueOf(salary.getSalary()));
            date = salary.getCreationDate();
            salDate.setText(date);
            switch (salary.getSalMode()){
                case Constants.SAL_MODE_ACC:
                    rdGrp2.check(R.id.account);
                    break;
                case Constants.SAL_MODE_CASH:
                    rdGrp2.check(R.id.inHand);
                    break;
                default:
                    rdGrp2.check(R.id.account);
                    break;
            }
            switch(salary.getIncType()){
                case Constants.daily:
                    rdGrp1.check(R.id.daily);
                    break;
                case Constants.monthly:
                    rdGrp1.check(R.id.monthly);
                    break;
                case Constants.oneTime:
                    rdGrp1.check(R.id.oneTime);
                    break;
            }
        }

        yes.setOnClickListener(v1 -> {
            onClickYesPopup(isEdit, salary, salSource, salAmt, date, rdGrp1, rdGrp2);
            popupWindow.dismiss();
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(addSal);
    }

    private void onClickYesPopup(boolean isEdit, @Nullable salaryEntity salary,
                                 EditText salSrc, EditText salAmt, String salDate,
                                 RadioGroup rdGrp1, RadioGroup rdGrp2){
//            Insert new value.
            if (!salSrc.getText().toString().trim().isEmpty()
                    &&!salAmt.getText().toString().trim().isEmpty()){
                salaryEntity sal = new salaryEntity();
                sal.setCreationDate(salDate);
                sal.setIncName(salSrc.getText().toString());
                sal.setSalary(Integer.parseInt(salAmt.getText().toString()));
//                Credit Mode.
                switch (rdGrp2.getCheckedRadioButtonId()){
                    case R.id.account:
                        sal.setSalMode(Constants.SAL_MODE_ACC);
                        break;
                    case R.id.inHand:
                        sal.setSalMode(Constants.SAL_MODE_CASH);
                        break;
                    default:
                        Commons.SnackBar(getView(),"Select a credit mode.");
                        sal = null;
                        break;
                }
//                Income Type.
                try{
                    switch (rdGrp1.getCheckedRadioButtonId()) {
                        case R.id.daily:
                            sal.setIncType(Constants.daily);
                            break;
                        case R.id.monthly:
                            sal.setIncType(Constants.monthly);
                            break;
                        case R.id.oneTime:
                            sal.setIncType(Constants.oneTime);
                            break;
                        default:
                            Commons.SnackBar(getView(), "Select a pay period.");
                            sal = null;
                            break;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (sal!=null){
                    if (!isEdit){
                        vm.InsertSalary(sal);
                    }else{
                        sal.setId(salary.getId());
                        vm.UpdateSalary(sal);
                    }
                    if (sal.getSalMode()==Constants.SAL_MODE_ACC){
                        balanceEntity bal = getBal();
                        int oldBal = sal.getSalary();
                        if (bal!=null){
                            oldBal = oldBal + bal.getBalance();
                            bal.setBalance(oldBal);
                        }
                        vm.DeleteBalance();
                        vm.InsertBalance(bal);
                    }else{
                        inHandBalEntity bal = getInHandBal();
                        int oldBal = sal.getSalary();
                        if (bal!=null){
                            oldBal = oldBal + bal.getBalance();
                            bal.setBalance(oldBal);
                        }
                        vm.DeleteInHandBalance();
                        vm.InsertInHandBalance(bal);
                    }

                }
            }else{
                Commons.SnackBar(getView(),"Field(s) may be empty");
            }
    }

    private void touchHelper() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                ViewHolder = viewHolder;
                adapter.notifyDataSetChanged();
                vm.DeleteSalary(adapter.getSalaryEntity(viewHolder.getPosition()));
                int type = adapter.getSalaryEntity(viewHolder.getPosition()).getIncType();
                int salary = adapter.getSalaryEntity(viewHolder.getPosition()).getSalary();
//Popup to remove from balance or no needed...
//                if (type==Constants.SAL_MODE_ACC){
//                    balanceEntity bal = getBal();
//                    int curBal = bal.getBalance();
//                    vm.DeleteBalance();
//                    vm.InsertBalance(new balanceEntity(curBal-salary));
//                }else{
//                    inHandBalEntity bal = getInHandBal();
//                    int curBal = bal.getBalance();
//                    vm.DeleteInHandBalance();
//                    vm.InsertInHandBalance(new inHandBalEntity(curBal-salary));
//                }
            }
        }).attachToRecyclerView(salSplitList);

        adapter.setOnItemClickListener(exp -> callPopUpWindow(true, exp));
    }

    private balanceEntity getBal(){
        AtomicReference<balanceEntity> bal = new AtomicReference<>(new balanceEntity());
        vm.getBalance().observe(requireActivity(), balanceEntity -> {
            bal.set(balanceEntity);
        });
        return bal.get();
    }

    private inHandBalEntity getInHandBal(){
        AtomicReference<inHandBalEntity> bal = new AtomicReference<>(new inHandBalEntity());
        vm.getInHandBalance().observe(requireActivity(), inHandBalEntity -> {
            bal.set(inHandBalEntity);
        });
        return bal.get();
    }

    private int getSalary() {
        AtomicInteger finalTotalSalary = new AtomicInteger();
        vm.getSalary().observe(requireActivity(), entity -> {
            if (entity != null) {
                adapter.setSal(entity);
                int total = 0;
                for (int i = 0; i < entity.size(); i++) {
                    total = total + entity.get(i).getSalary();
                }
                finalTotalSalary.set(total);
                String s = Constants.RUPEE + " " + finalTotalSalary.get();
                salAmt.setText(s);
            }
        });
        return finalTotalSalary.get();
    }
}