package app.personal.fury.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.dueList.dueAdapter;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
public class allDues extends AppCompatActivity {

    private TextView emptyMsg;
    private RecyclerView recyclerView;
    private dueAdapter adapter;
    private mainViewModel vm;
    private LinearLayout empty;
    private String Currency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exp);
        initViewModel();
        initUi();
        showEmpty();
    }

    private void initUi() {
        TextView title = findViewById(R.id.title);
        String s = "All Dues";
        title.setText(s);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        empty = findViewById(R.id.empty);
        emptyMsg = findViewById(R.id.emptyMsg);
        recyclerView = findViewById(R.id.exp_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        touchHelper();
    }

    private void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        String s = "Great! There's No Pending Dues.";
        emptyMsg.setText(s);
    }

    private void initViewModel() {
        adapter = new dueAdapter(2);
        vm = new ViewModelProvider(this).get(mainViewModel.class);
        vm.getRupee().observe(this, String->{
            if (!String.equals("null")){
                Currency = String.getCurrency();
            }
        });
        vm.getDebt().observe(this, entity -> {
            if (!entity.isEmpty()) {
                adapter.clear();
                adapter.setDebt(entity, 0, Currency);
                showRecyclerView();
            } else {
                showEmpty();
            }
        });
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
                    vm.DeleteDebt(adapter.getDebtAt(viewHolder.getAdapterPosition()));
                    Commons.SnackBar(recyclerView, "Debt deleted.");
                    adapter.clear();
                    adapter.notifyDataSetChanged();
            }
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(allDues.this, R.color.theme_red))
                        .addSwipeLeftActionIcon(R.drawable.common_icon_trash)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(allDues.this, R.color.full_white))
                        .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                        .addSwipeLeftCornerRadius(TypedValue.COMPLEX_UNIT_SP, 15)

                        .addSwipeRightBackgroundColor(ContextCompat.getColor(allDues.this, R.color.theme_red))
                        .addSwipeRightActionIcon(R.drawable.common_icon_trash)
                        .addSwipeRightLabel("Delete")
                        .setSwipeRightLabelColor(ContextCompat.getColor(allDues.this, R.color.full_white))
                        .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                        .addSwipeRightCornerRadius(TypedValue.COMPLEX_UNIT_SP, 15)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
    }).attachToRecyclerView(recyclerView);

//        adapter.setOnItemClickListener(Due -> {});
    }
}