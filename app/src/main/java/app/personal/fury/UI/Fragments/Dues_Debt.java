package app.personal.fury.UI.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Viewmodel.AppUtilViewModel;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.Utls.Commons;
import app.personal.Utls.Constants;
import app.personal.Utls.TutorialUtil;
import app.personal.fury.R;
import app.personal.fury.UI.Adapters.dueList.dueAdapter;
import app.personal.fury.UI.MainActivity;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Dues_Debt extends Fragment {

    private TextView totalDueDisplay, totalRepeatingDues, noDues;
    private RecyclerView dueList, repeatList;
    private mainViewModel vm;
    private AppUtilViewModel appVM;
    private TutorialUtil util;
    private dueAdapter mainDueAdapter, repeatDue;
    private int finalTotalDue = 0;
    private AdView ad;
    private AdRequest adRequest;
    private FloatingActionButton fltBtn;

    public Dues_Debt() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainDueAdapter = new dueAdapter(0);
        repeatDue = new dueAdapter(1);
        vm = new ViewModelProvider(requireActivity()).get(mainViewModel.class);
        appVM = new ViewModelProvider(requireActivity()).get(AppUtilViewModel.class);
        MobileAds.initialize(requireContext());
        if (savedInstanceState != null) {
            adRequest = new AdRequest.Builder().build();
            requestAd();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment_dues, container, false);
        find(v);
        initViewModel();
        return v;
    }

    private void requestAd() {
        ad.loadAd(adRequest);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                ad.setVisibility(View.GONE);
                ad.loadAd(adRequest);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                ad.setVisibility(View.VISIBLE);
            }
        });
    }

    private void find(View v) {
        dueList = v.findViewById(R.id.dueList);
        fltBtn = v.findViewById(R.id.addDue);
        ad = v.findViewById(R.id.adView3);
        fltBtn.setOnClickListener(v1 -> callPopupWindow(Constants.itemAdd));
        totalDueDisplay = v.findViewById(R.id.dueTotalText);
        noDues = v.findViewById(R.id.dueTotalNo);
        repeatList = v.findViewById(R.id.RepeatdueList);
        totalRepeatingDues = v.findViewById(R.id.repeatDues);
        touchHelper();
        dueList.setLayoutManager(new LinearLayoutManager(requireContext()));
        dueList.setHasFixedSize(true);
        dueList.setAdapter(mainDueAdapter);
        repeatList.setLayoutManager(new LinearLayoutManager(requireContext()));
        repeatList.setHasFixedSize(true);
        repeatList.setAdapter(repeatDue);
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    private void callPopupWindow(int Layout) {
        PopupWindow popupWindow = new PopupWindow(getContext());
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        if (Layout == Constants.itemAdd) {
            View view = inflater.inflate(R.layout.add_item_due, null);
            popupWindow.setContentView(view);

            Button add, cancel;
            TextView DueDateTitle;
            EditText name, amt;
            CalendarView c;
            CheckBox isRepeat;
            AtomicBoolean date = new AtomicBoolean(false);
            final String[] currDate = new String[1];
            currDate[0] = null;
            add = view.findViewById(R.id.due_add);
            name = view.findViewById(R.id.D_name);
            amt = view.findViewById(R.id.D_amt);
            cancel = view.findViewById(R.id.due_c);
            DueDateTitle = view.findViewById(R.id.Due_);
            isRepeat = view.findViewById(R.id.Due_repeat);
            c = view.findViewById(R.id.D_date);
            c.setVisibility(View.GONE);

            DueDateTitle.setOnClickListener(v -> {
                DueDateTitle.setVisibility(View.GONE);
                c.setVisibility(View.VISIBLE);
            });

            c.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
                currDate[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                try {
                    Date dateBefore = sdf.parse(Commons.getDate());
                    Date dateAfter = sdf.parse(currDate[0]);
                    assert dateAfter != null;
                    assert dateBefore != null;
                    long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                    long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

                    if (daysDiff >= 1 && dateAfter.getTime() > dateBefore.getTime()) {
                        DueDateTitle.setVisibility(View.VISIBLE);
                        DueDateTitle.setText(currDate[0]);
                        c.setVisibility(View.GONE);
                        date.set(true);
                    } else {
                        Commons.SnackBar(add, "Set a date one day ahead of " + Commons.getDate());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            add.setOnClickListener(v -> {
                if (currDate[0] != null && !name.getText().toString().trim().isEmpty()
                        && !amt.getText().toString().trim().isEmpty()) {
                    if (date.get()) {
                        debtEntity entity = new debtEntity();
                        entity.setAmount(Integer.parseInt(amt.getText().toString()));
                        entity.setDate(Commons.getDate());
                        entity.setFinalDate(currDate[0]);
                        entity.setStatus(Constants.DEBT_NOT_PAID);
                        entity.setSource(name.getText().toString());
                        if (isRepeat.isChecked()) {
                            entity.setIsRepeat(Constants.REPEATING_DUE);
                        } else {
                            entity.setIsRepeat(Constants.NON_REPEATING_DUE);
                        }
                        vm.InsertDebt(entity);
                        mainDueAdapter.clear();
                        appVM.getCheckerData().observe(requireActivity(), launchChecker -> {
                            if (launchChecker.getTimesLaunched() == 0) {
                                clearDataAfterTutorial();
                            } else {
                                appVM.getCheckerData().removeObservers(requireActivity());
                            }
                        });
                        popupWindow.dismiss();
                    } else {
                        Commons.SnackBar(add, "Select a valid date");
                    }
                } else {
                    Commons.SnackBar(add, "Set due date");
                }
            });
            cancel.setOnClickListener(v -> popupWindow.dismiss());

        } else if (Layout == Constants.itemPaid) {
            Log.e("onSwipe", "called");
            View view = inflater.inflate(R.layout.popup_action_duepaid, null);
            popupWindow.setContentView(view);

            ImageView check = view.findViewById(R.id.check);

            ViewPropertyAnimatorCompat viewAnimator;
            viewAnimator = ViewCompat.animate(check)
                    .scaleX(2).scaleY(2)
                    .setStartDelay(500)
                    .setDuration(500);
            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
            new CountDownTimer(1500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    popupWindow.dismiss();
                }
            }.start();

        }//use else if delete popup exists in future..
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setElevation(6);
        popupWindow.showAsDropDown(fltBtn);
    }

    private void clearDataAfterTutorial() {
        new CountDownTimer(2000, 1000) {
            final PopupWindow popupWindow = new PopupWindow(requireContext());

            @Override
            public void onTick(long millisUntilFinished) {
                LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                View view = inflater.inflate(R.layout.popup_budget_fake_loading, null);
                TextView t = view.findViewById(R.id.loadingText);
                String s = "Clearing your data, please wait..";
                t.setText(s);
                popupWindow.setContentView(view);
                popupWindow.setFocusable(true);
                popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setElevation(6);
                popupWindow.showAsDropDown(dueList);
            }

            @Override
            public void onFinish() {
                popupWindow.dismiss();
                Commons.clearData(vm);
                appVM.getCheckerData().removeObservers(requireActivity());
                MainActivity.redirectTo(2);
            }
        }.start();
    }

    private void initViewModel() {
        vm.getDebt().observe(requireActivity(), entity -> {
            if (entity != null) {
                mainDueAdapter.clear();
                repeatDue.clear();
                repeatDue.setDebt(entity, 3);
                mainDueAdapter.setDebt(entity, 1);
                finalTotalDue = 0;
                finalTotalDue = mainDueAdapter.getTotalDebt();
                String totalDues = "0" + mainDueAdapter.getItemCount();
                noDues.setText(totalDues);
                String totalRepeatingDue = "0" + repeatDue.getItemCount();
                totalRepeatingDues.setText(totalRepeatingDue);
            }
            String s = Constants.RUPEE + finalTotalDue;
            totalDueDisplay.setText(s);
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
                if (direction == ItemTouchHelper.RIGHT) {
                    vm.DeleteDebt(mainDueAdapter.getDebtAt(viewHolder.getAdapterPosition()));
                    Commons.SnackBar(requireView(), "Debt deleted.");
                    mainDueAdapter.clear();
                } else {
                    debtEntity entity = mainDueAdapter.getDebtAt(viewHolder.getAdapterPosition());

                    if (!entity.getStatus().equals(Constants.DEBT_PAID)) {
                        entity.setStatus(Constants.DEBT_PAID);
                        entity.setDate(Commons.getDate());
                        vm.DeleteDebt(mainDueAdapter.getDebtAt(viewHolder.getAdapterPosition()));
                        vm.InsertDebt(entity);

//                        Commons.SnackBar(recyclerView, "Debt marked as paid.");

                        callPopupWindow(Constants.itemPaid);
                        mainDueAdapter.notifyDataSetChanged();
                    } else {
                        Commons.SnackBar(requireView(), "Debt marked as paid on " + entity.getDate() + ".");
                        mainDueAdapter.notifyDataSetChanged();
                    }
                }
            }

            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.theme_green))
                        .addSwipeLeftActionIcon(R.drawable.common_icon_mark)
                        .addSwipeLeftLabel("Mark as paid")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(requireActivity(), R.color.full_white))
                        .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                        .addSwipeLeftCornerRadius(TypedValue.COMPLEX_UNIT_SP, 15)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.theme_red))
                        .addSwipeRightActionIcon(R.drawable.common_icon_trash)
                        .addSwipeRightLabel("Delete")
                        .setSwipeRightLabelColor(ContextCompat.getColor(requireActivity(), R.color.full_white))
                        .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                        .addSwipeRightCornerRadius(TypedValue.COMPLEX_UNIT_SP, 15)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(dueList);

        mainDueAdapter.setOnItemClickListener(Due -> {
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    vm.DeleteDebt(repeatDue.getDebtAt(viewHolder.getAdapterPosition()));
                    Commons.SnackBar(requireView(), "Debt deleted.");
                    repeatDue.clear();
                } else {
                    debtEntity entity = repeatDue.getDebtAt(viewHolder.getAdapterPosition());

                    if (!entity.getStatus().equals(Constants.DEBT_PAID)) {
                        entity.setStatus(Constants.DEBT_NOT_PAID);
                        entity.setDate(Commons.getDate());
                        String[] date = entity.getFinalDate().split("/");
                        String month;
                        String year;
                        if (Integer.parseInt(date[1]) < 12) {
                            month = String.valueOf(Integer.parseInt(date[1]) + 1);
                            year = date[2];
                        } else {
                            month = String.valueOf(1);
                            year = String.valueOf(Integer.parseInt(date[2]) + 1);
                        }
                        entity.setFinalDate(date[0] + "/" + month + "/" + year);
                        vm.DeleteDebt(repeatDue.getDebtAt(viewHolder.getAdapterPosition()));
                        vm.InsertDebt(entity);
                        callPopupWindow(Constants.itemPaid);
                        repeatDue.notifyDataSetChanged();
                    }
                }
            }

            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.theme_green))
                        .addSwipeLeftActionIcon(R.drawable.common_icon_mark)
                        .addSwipeLeftLabel("Mark as paid")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(requireActivity(), R.color.full_white))
                        .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                        .addSwipeLeftCornerRadius(TypedValue.COMPLEX_UNIT_SP, 15)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.theme_red))
                        .addSwipeRightActionIcon(R.drawable.common_icon_trash)
                        .addSwipeRightLabel("Delete")
                        .setSwipeRightLabelColor(ContextCompat.getColor(requireActivity(), R.color.full_white))
                        .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                        .addSwipeRightCornerRadius(TypedValue.COMPLEX_UNIT_SP, 15)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(repeatList);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                appVM.getCheckerData().observe(requireActivity(), launchChecker -> {
                    if (launchChecker.getTimesLaunched() == 0) {
                        DuesTutorial();
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }

    private void DuesTutorial() {
        util = new TutorialUtil(requireActivity(), requireContext(), requireActivity(), requireActivity());
        ArrayList<View> Target = new ArrayList<>();
        ArrayList<String> PrimaryTexts = new ArrayList<>(), SecondaryTexts = new ArrayList<>();

        Target.add(fltBtn);
        PrimaryTexts.add("Dues And Debt");
        SecondaryTexts.add("1. Personalised notifications are provided to remind you of payment when the due date is nearby\n\n2. Repeating dues get repeated on the date provided\n\n3. Swiping left on a due will mark it as paid and on right swipe the due will be deleted");

        util.TutorialPhase7(Target, PrimaryTexts, SecondaryTexts);
    }
}