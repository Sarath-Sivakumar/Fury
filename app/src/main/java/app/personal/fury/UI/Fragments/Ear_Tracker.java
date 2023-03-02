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
import android.widget.LinearLayout;
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
import app.personal.fury.ViewPagerAdapter.infoGraphicsAdapter;

public class Ear_Tracker extends Fragment {
    //Daily = 1, Monthly = 0, Hourly = -1, oneTime = ?(To be implemented in a future update).

    private RecyclerView salSplitList;
    private FloatingActionButton addSal;
    private mainViewModel vm;
    private salaryAdapter adapter;
    private TextView salAmt, inHandAmt, accountAmt, inHandCount, accountCount;
    private final int[] FragmentList =
            new int[]{R.drawable.info_h1, R.drawable.info_h2,
                    R.drawable.info_h3, R.drawable.info_h4,
                    R.drawable.info_h5, R.drawable.info_h6};
    private int cashAmt, cashCount, accAmt, accCount, totalExp, totalSalary;

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
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        adapter = new salaryAdapter();
        View v = inflater.inflate(R.layout.main_fragment_earningstracker, container, false);
        initAd();
        findView(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String s = Constants.RUPEE + getSalary();
        salAmt.setText(s);
        getExp();
    }

    private void findView(View v) {
        salAmt = v.findViewById(R.id.salAmt);
        salSplitList = v.findViewById(R.id.salList);
        addSal = v.findViewById(R.id.addSal);
        addSal.setOnClickListener(v1 -> callPopUpWindow(false, null));
        salSplitList.setLayoutManager(new LinearLayoutManager(requireContext()));
        salSplitList.setHasFixedSize(true);
        salSplitList.setAdapter(adapter);
        inHandAmt = v.findViewById(R.id.inhand_Amt);
        inHandCount = v.findViewById(R.id.inhand_count);
        accountAmt = v.findViewById(R.id.account_amt);
        accountCount = v.findViewById(R.id.account_count);

        ViewPager ig_vp = v.findViewById(R.id.infoGraphics_earvp);
        TabLayout ig_tl = v.findViewById(R.id.infoGraphics_ear);
        infoGraphicsAdapter igAdapter = new infoGraphicsAdapter(requireContext(), FragmentList);
        ig_vp.setAdapter(igAdapter);
        ig_tl.setupWithViewPager(ig_vp, true);
        Commons.timedSliderInit(ig_vp, FragmentList, 5);
        touchHelper();
    }

    @SuppressLint("SetTextI18n")
    private void callPopUpWindow(boolean isEdit, @Nullable salaryEntity salary) {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View v = inflater.inflate(R.layout.add_item_exp, null);
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
        LinearLayout bal = v.findViewById(R.id.cashDetails);
        bal.setVisibility(View.GONE);
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
        if (!isEdit) {
            salDate.setVisibility(View.GONE);
            date = Commons.getDate();
        } else {
            assert salary != null;
            rdGrp2.setVisibility(View.GONE);
            salModeTitle.setVisibility(View.GONE);
            salSource.setText(salary.getIncName());
            salAmt.setText(String.valueOf(salary.getSalary()));
            date = salary.getCreationDate();
            salDate.setText(date);
            switch (salary.getSalMode()) {
                case Constants.SAL_MODE_ACC:
                    rdGrp2.check(R.id.account);
                    break;
                case Constants.SAL_MODE_CASH:
                    rdGrp2.check(R.id.inHand);
                    break;
                default:
                    break;
            }
            switch (salary.getIncType()) {
                case Constants.daily:
                    rdGrp1.check(R.id.daily);
                    break;
                case Constants.monthly:
                    rdGrp1.check(R.id.monthly);
                    break;
                case Constants.oneTime:
                    rdGrp1.check(R.id.oneTime);
                    break;
                default:
                    break;
            }
        }

        yes.setOnClickListener(v1 -> {
            if (isEdit) {
                onClickYesPopup(true, salary, salSource, salAmt, salDate.getText().toString(), rdGrp1, rdGrp2);
            } else {
                onClickYesPopup(false, salary, salSource, salAmt, date, rdGrp1, rdGrp2);
            }
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
                                 RadioGroup rdGrp1, RadioGroup rdGrp2) {
//            Insert new value.
        if (!salSrc.getText().toString().trim().isEmpty()
                && !salAmt.getText().toString().trim().isEmpty()) {
            salaryEntity sal = new salaryEntity();
            sal.setCreationDate(salDate);
            sal.setIncName(salSrc.getText().toString());
            sal.setSalary(Integer.parseInt(salAmt.getText().toString()));
//                Credit Mode.
            switch (rdGrp2.getCheckedRadioButtonId()) {
                case R.id.account:
                    sal.setSalMode(Constants.SAL_MODE_ACC);
                    break;
                case R.id.inHand:
                    sal.setSalMode(Constants.SAL_MODE_CASH);
                    break;
                default:
                    Commons.SnackBar(getView(), "Select a credit mode.");
                    sal = null;
                    break;
            }
//                Income Type.
            try {
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
            } catch (Exception ignored) {
            }
            if (sal != null) {
                if (!isEdit) {
                    vm.InsertSalary(sal);
                    if (sal.getSalMode() == Constants.SAL_MODE_ACC) {
                        balanceEntity bal = getBal();
                        int oldBal = sal.getSalary();
                        if (bal != null) {
                            oldBal = oldBal + bal.getBalance();
                            bal.setBalance(oldBal);
                        } else {
                            bal.setBalance(0);
                        }
                        vm.DeleteBalance();
                        vm.InsertBalance(bal);
                    } else {
                        inHandBalEntity bal = getInHandBal();
                        int oldBal = sal.getSalary();
                        if (bal != null) {
                            oldBal = oldBal + bal.getBalance();
                            bal.setBalance(oldBal);
                        } else {
                            bal.setBalance(0);
                        }
                        vm.DeleteInHandBalance();
                        vm.InsertInHandBalance(bal);
                    }
                } else {
                    sal.setId(salary.getId());
                    vm.UpdateSalary(sal);
                }
            }
        } else {
            Commons.SnackBar(getView(), "Field(s) may be empty");
        }
    }

    private void callOnDeletePopup(salaryEntity salaryEntity, @Nullable String isUpdate) {
//        "1" = Update Balance
//        "0" = Update Budget
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View v = inflater.inflate(R.layout.popup_action_expdelete, null);
        popupWindow.setContentView(v);

        Button yes = v.findViewById(R.id.yes_btn);
        Button no = v.findViewById(R.id.no_btn);
        no.setOnClickListener(v1 -> popupWindow.dismiss());
        TextView body = v.findViewById(R.id.edit);
//        CheckBox checkBox = v.findViewById(R.id.check);

        if (salaryEntity != null && isUpdate == null) {
            String s = "This Source will be deleted.";
            body.setText(s);

            yes.setOnClickListener(v1 -> {
                vm.DeleteSalary(salaryEntity);
                popupWindow.dismiss();
                callOnDeletePopup(salaryEntity, "1");
            });
        } else if (salaryEntity != null){
            if (isUpdate.equals("1")) {
                String s = "Do you want to update balance according to current deletion?";
                body.setText(s);
                yes.setOnClickListener(v1 -> {
                    int type = salaryEntity.getSalMode();
                    int salary = salaryEntity.getSalary();
                    if (type == Constants.SAL_MODE_ACC) {
                        balanceEntity bal = getBal();
                        int curBal = bal.getBalance();
                        vm.DeleteBalance();
                        vm.InsertBalance(new balanceEntity(curBal - salary));
                    } else {
                        inHandBalEntity bal = getInHandBal();
                        int curBal = bal.getBalance();
                        vm.DeleteInHandBalance();
                        vm.InsertInHandBalance(new inHandBalEntity(curBal - salary));
                    }
                    popupWindow.dismiss();
                    callOnDeletePopup(salaryEntity, "0");

                });
            } else if (isUpdate.equals("0")) {
                String s = "Do you want to update budget according to current deletion?";
                body.setText(s);
                yes.setOnClickListener(v1 -> {
                    Commons.setDefaultBudget(vm, totalSalary, totalExp);
                    popupWindow.dismiss();
                });
            }
        }

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(addSal);
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
                callOnDeletePopup(adapter.getSalaryEntity(viewHolder.getPosition()), null);
                adapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(salSplitList);

        adapter.setOnItemClickListener(exp -> callPopUpWindow(true, exp));
    }

    private balanceEntity getBal() {
        AtomicReference<balanceEntity> bal = new AtomicReference<>(new balanceEntity());
        vm.getBalance().observe(requireActivity(), balanceEntity -> {
            if (balanceEntity != null) {
                bal.set(balanceEntity);
            } else {
                bal.set(new balanceEntity(0));
            }
        });
        return bal.get();
    }

    private inHandBalEntity getInHandBal() {
        AtomicReference<inHandBalEntity> bal = new AtomicReference<>(new inHandBalEntity());
        vm.getInHandBalance().observe(requireActivity(), inHandBalEntity -> {
            if (inHandBalEntity != null) {
                bal.set(inHandBalEntity);
            } else {
                bal.set(new inHandBalEntity(0));
            }
        });
        return bal.get();
    }

    private int getSalary() {
        AtomicInteger finalTotalSalary = new AtomicInteger();
        vm.getSalary().observe(requireActivity(), entity -> {
            int total = 0;
            if (entity != null) {
                adapter.setSal(entity);
                accAmt = 0;
                accCount = 0;
                cashAmt = 0;
                cashCount = 0;
                for (int i = 0; i < entity.size(); i++) {
                    total = total + entity.get(i).getSalary();
                    if (entity.get(i).getSalMode() == Constants.SAL_MODE_ACC) {
                        accCount = accCount + 1;
                        accAmt = accAmt + entity.get(i).getSalary();
                    } else {
                        cashCount = cashCount + 1;
                        cashAmt = cashAmt + entity.get(i).getSalary();
                    }
                }
                finalTotalSalary.set(total);
                try {
                    String s1 = Constants.RUPEE + "" + total;
                    salAmt.setText(s1);
                    inHandCount.setText(String.valueOf(cashCount));
                    accountCount.setText(String.valueOf(accCount));
                    String s2 = Constants.RUPEE + "" + accAmt;
                    accountAmt.setText(s2);
                    String s3 = Constants.RUPEE + "" + cashAmt;
                    inHandAmt.setText(s3);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                finalTotalSalary.set(0);
            }
            totalSalary = total;
        });
        return finalTotalSalary.get();
    }

    private void getExp() {
        vm.getExp().observe(requireActivity(), expEntities -> {
            int total = 0;
            if (expEntities != null) {
                for (int i = 0; i < expEntities.size(); i++) {
                    total = total + expEntities.get(i).getExpenseAmt();
                }
            }
            totalExp = total;
        });
    }
}