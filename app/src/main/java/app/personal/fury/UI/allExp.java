package app.personal.fury.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.expList.expAdapter;

public class allExp extends AppCompatActivity {

    private TextView emptyMsg;
    private RecyclerView recyclerView;
    private expAdapter adapter;
    private mainViewModel vm;
    private RecyclerView.ViewHolder ViewHolder;
    private LinearLayout empty;
//    Button clearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exp);
        setUi();
    }

    private void setUi() {
        TextView title = findViewById(R.id.title);
        String s = "All Expenses";
        title.setText(s);
        adapter = new expAdapter();
        vm = new ViewModelProvider(this).get(mainViewModel.class);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        empty = findViewById(R.id.empty);
        emptyMsg = findViewById(R.id.emptyMsg);
        recyclerView = findViewById(R.id.exp_list);
        showEmpty();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        touchHelper();
        getExp();
    }

    private void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        String s = "No Recorded Expenses Yet..";
        emptyMsg.setText(s);
    }

    private void itemDeletePopUp() {
        PopupWindow popupWindow = new PopupWindow(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_action_expdelete, null);
        popupWindow.setContentView(view);
        Button del = view.findViewById(R.id.yes_btn);
        Button cancel = view.findViewById(R.id.no_btn);

        del.setOnClickListener(v -> {
            expEntity entity = adapter.getExpAt(ViewHolder.getAdapterPosition());
            int amt = entity.getExpenseAmt();
            int oldBal = getBalance();
            vm.DeleteBalance();
            balanceEntity entity1 = new balanceEntity();
            entity1.setBalance(oldBal + amt);
            vm.InsertBalance(entity1);
            vm.DeleteExp(adapter.getExpAt(ViewHolder.getAdapterPosition()));
            adapter.clear();
            adapter.notifyDataSetChanged();
            popupWindow.dismiss();
            Commons.SnackBar(recyclerView, "Expense data deleted");
        });
        cancel.setOnClickListener(v -> {
            adapter.notifyDataSetChanged();
            popupWindow.dismiss();
        });
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(recyclerView);
    }

    private int getBalance() {
        AtomicInteger Balance = new AtomicInteger();
        vm.getBalance().observe(this, entity -> {
            if (entity != null) {
                Balance.set(entity.getBalance());
                Log.e("Bal", String.valueOf(entity.getBalance()));
            }
        });
        return Balance.get();
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
                itemDeletePopUp();
            }

        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(this::expDetailPopup);
    }

    private void getExp() {
        vm.getExp().observe(this, entity -> {
            if (!entity.isEmpty()) {
                adapter.clear();
                adapter.setExp(entity, false);
                showRecyclerView();
            } else {
                showEmpty();
            }
        });
    }

    private void expDetailPopup(expEntity exp) {
        PopupWindow popupWindow = new PopupWindow(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_details_expense, null);
        popupWindow.setContentView(view);

        ImageButton close = view.findViewById(R.id.close);
        close.setOnClickListener(v -> popupWindow.dismiss());
        TextView cat, amt, time, date, day;
        cat = view.findViewById(R.id.cat);
        amt = view.findViewById(R.id.amt);
        date = view.findViewById(R.id.date);
        day = view.findViewById(R.id.day);
        time = view.findViewById(R.id.time);

        cat.setText(exp.getExpenseName());
        String s = Constants.RUPEE + exp.getExpenseAmt();
        amt.setText(s);
        date.setText(exp.getDate());
        day.setText(Commons.getDisplayDay(exp.getDay()));
        time.setText(exp.getTime());

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(recyclerView);
    }
}