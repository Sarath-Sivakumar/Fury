package app.personal.fury.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import app.personal.MVVM.Entity.balanceEntity;
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
    private TextView salAmt;
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
        ig_vp = v.findViewById(R.id.infoGraphics_earvp);
        ig_tl = v.findViewById(R.id.infoGraphics_ear);
        igAdapter = new infoGraphicsAdapter(getParentFragmentManager());
        FragmentList = new ArrayList<>();
        touchHelper();
    }
    private void setIG_VP(){
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
        View view = inflater.inflate(R.layout.add_exp_item, null);
        popupWindow.setContentView(view);

        TextView title = view.findViewById(R.id.title);

        @SuppressLint("CutPasteId") EditText name = view.findViewById(R.id.salSrc);
        @SuppressLint("CutPasteId") EditText amt = view.findViewById(R.id.expAmt);
        EditText salDate = view.findViewById(R.id.salDate);
        Button yes = view.findViewById(R.id.add_yes);
        Button no = view.findViewById(R.id.add_no);
        RadioGroup grp = view.findViewById(R.id.RadioGroup);

        grp.clearCheck();

        if (!isEdit) {
            salDate.setVisibility(View.GONE);
        } else {
            Log.e("SalEdit", "Called.");
            name.setText(salary.getIncName());
            amt.setText(String.valueOf(salary.getSalary()));
            salDate.setText(salary.getCreationDate());
            switch (salary.getIncType()) {
                case Constants.hourly:
                    grp.check(R.id.hourly);
                    break;
                case Constants.daily:
                    grp.check(R.id.daily);
                    break;
                case Constants.monthly:
                    grp.check(R.id.monthly);
                    break;
                case Constants.oneTime:
                    grp.check(R.id.oneTime);
                    break;
            }

        }

        title.setText("New Income");
        name.setHint("Income Name");
        amt.setHint("Income Amount");

        FrameLayout frm = view.findViewById(R.id.expNameTitle);
        frm.setVisibility(View.GONE);

        no.setOnClickListener(v -> popupWindow.dismiss());
        yes.setOnClickListener(v -> {
            if (!name.getText().toString().trim().isEmpty() && !amt.getText().toString().trim().isEmpty()) {
                adapter.clear();
                salaryEntity entity = new salaryEntity();
                entity.setIncName(name.getText().toString());
                entity.setSalary(Integer.parseInt(amt.getText().toString()));
                entity.setCreationDate(Commons.getDate());
                if (isEdit){
                    entity.setId(salary.getId());
                }
                if (grp.getCheckedRadioButtonId() == R.id.daily) {
                    entity.setIncType(Constants.daily);
                    if (!isEdit){
                        vm.InsertSalary(entity);
                    }else{
                        vm.UpdateSalary(entity);
                    }
                    popupWindow.dismiss();
                } else if (grp.getCheckedRadioButtonId() == R.id.monthly) {
                    entity.setIncType(Constants.monthly);
                    if (!isEdit){
                        vm.InsertSalary(entity);
                    }else{
                        vm.UpdateSalary(entity);
                    }
                    popupWindow.dismiss();
                } else if (grp.getCheckedRadioButtonId() == R.id.hourly) {
                    entity.setIncType(Constants.hourly);
                    if (!isEdit){
                        vm.InsertSalary(entity);
                    }else{
                        vm.UpdateSalary(entity);
                    }
                    popupWindow.dismiss();
                } else if (grp.getCheckedRadioButtonId() == R.id.oneTime) {
                    entity.setIncType(Constants.oneTime);
                    if (!isEdit){
                        vm.InsertSalary(entity);
                    }else{
                        vm.UpdateSalary(entity);
                    }
                    popupWindow.dismiss();
                } else {
                    Commons.SnackBar(view, "Select salary type");
                    return;
                }

                balanceEntity entity1 = vm.getBalance().getValue();
                int balance = 0;
                if (entity1 != null) {
                    balance = entity1.getBalance();
                    vm.DeleteBalance();
                }
                vm.InsertBalance(new balanceEntity(balance + entity.getSalary()));
            } else {
                adapter.clear();
            }
        });

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
                ViewHolder = viewHolder;
                adapter.notifyDataSetChanged();
//                callPopUpWindow();
            }

        }).attachToRecyclerView(salSplitList);

        adapter.setOnItemClickListener(exp -> callPopUpWindow(true, exp));
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