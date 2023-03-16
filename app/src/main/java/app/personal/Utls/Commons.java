package app.personal.Utls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.personal.MVVM.Entity.balanceEntity;
import app.personal.MVVM.Entity.budgetEntity;
import app.personal.MVVM.Entity.debtEntity;
import app.personal.MVVM.Entity.expEntity;
import app.personal.MVVM.Entity.inHandBalEntity;
import app.personal.MVVM.Viewmodel.mainViewModel;
import app.personal.fury.R;

public class Commons {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static void SnackBar(View v, String Text) {
        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_SHORT);
        LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View snackView = inflater.inflate(R.layout.snack_bar, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarLayout.setPadding(0, 0, 0, 180);
        TextView msg = snackView.findViewById(R.id.text);
        msg.setText(Text);
        snackBarLayout.addView(snackView, 0);
        snackbar.show();
    }

    public static String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    public static String getMonth() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return sdf.format(new Date());
    }

    public static String getYear() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new Date());
    }

    public static String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(new Date());
    }

    public static String getSeconds() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("ss");
        return sdf.format(new Date());
    }

    public static int setProgress(float exp, float sal) {
        if (exp > sal) {
            return 100;
        } else {
            return (int) ((exp / sal) * 100);
        }
    }

    public static int setCategoryProgress(float exp, float sal) {
        return (int) ((exp / sal) * 100);
    }

    public static int getDay() {
        Date d = new Date();
        return d.getDay();
    }

    public static String getDisplayDay(int day) {
        if (day == 1) {
            return "Monday";
        } else if (day == 2) {
            return "Tuesday";
        } else if (day == 3) {
            return "Wednesday";
        } else if (day == 4) {
            return "Thursday";
        } else if (day == 5) {
            return "Friday";
        } else if (day == 6) {
            return "Saturday";
        } else {
            return "Sunday";
        }
    }

    public static String getAvg(List<expEntity> listData, boolean AvgDisplay) {
        ArrayList<Integer> byDayTotal = new ArrayList<>(), daily = new ArrayList<>();
        int mainListSize = listData.size();
        String lastDate = "";
        int lastDay = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        for (int i = mainListSize - 1; i >= 0; i--) {
            expEntity exp = listData.get(i);
            if (i < mainListSize - 1) {
                if (!lastDate.equals(exp.getDate())) {
                    try {
                        int dailyTotal = 0;
                        for (int j = 0; j < daily.size(); j++) {
                            dailyTotal = dailyTotal + daily.get(j);
                        }
                        byDayTotal.add(dailyTotal);
                        Log.e("DailyTotal", "Value: " + dailyTotal + " Date: " + lastDate + " Day: " + getDisplayDay(lastDay));

                        Date dateBefore = sdf.parse(lastDate);
                        Date dateAfter = sdf.parse(exp.getDate());
                        assert dateAfter != null;
                        assert dateBefore != null;
                        long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

                        for (int i1 = 1; i1 < daysDiff; i1++) {
                            byDayTotal.add(0);
                            Log.e("Daily", "Value 0");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Adding 0 error", e.getMessage());
                    }

                    daily.clear();
                    daily.add(exp.getExpenseAmt());
                    lastDate = exp.getDate();
                    lastDay = exp.getDay();
                    if (i == 0) {
                        int dailyTotal1 = 0;
                        for (int j = 0; j < daily.size(); j++) {
                            dailyTotal1 = dailyTotal1 + daily.get(j);
                        }
                        byDayTotal.add(dailyTotal1);
                        Log.e("DailyTotal", "Value: " + dailyTotal1 + " Date: " + lastDate + " Day: " + getDisplayDay(lastDay));
                    }
                } else {
                    daily.add(exp.getExpenseAmt());
                }
            } else {
                lastDate = exp.getDate();
                lastDay = exp.getDay();
                daily.add(exp.getExpenseAmt());
            }
        }
        if (AvgDisplay) {
            return findAvg(byDayTotal);
        } else {
            return limiterAvg(byDayTotal);
        }
    }

    static int DAYS_LIMIT = 3;//Change this to change data collection period..(7 for 1 week)

    private static String limiterAvg(ArrayList<Integer> totalExp) {
        int total = 0;
        if (totalExp.size() >= DAYS_LIMIT) {
            for (int i = 0; i < totalExp.size(); i++) {
                total = total + totalExp.get(i);
            }
            return String.valueOf(total / totalExp.size());
        } else {
            return String.valueOf(total);
        }
    }

    private static String findAvg(ArrayList<Integer> totalExp) {

        if (totalExp.size() >= DAYS_LIMIT) {
            int total = 0;
            for (int i = 0; i < totalExp.size(); i++) {
                total = total + totalExp.get(i);
            }
            return Constants.RUPEE + total / totalExp.size() + "/Day";
        } else {
            return Constants.dAvgNoData;
        }
    }

    public static int getValueByPercent(int totalSalary, int Percent) {
        return (int) (totalSalary * Percent) / 100;
    }

    public static int getDays(int month) {
        if (month == 1) {
            return 31;
        } else if (month == 2) {
            return 28;
        } else if (month == 3) {
            return 31;
        } else if (month == 4) {
            return 30;
        } else if (month == 5) {
            return 31;
        } else if (month == 6) {
            return 30;
        } else if (month == 7) {
            return 31;
        } else if (month == 8) {
            return 31;
        } else if (month == 9) {
            return 30;
        } else if (month == 10) {
            return 31;
        } else if (month == 11) {
            return 30;
        } else if (month == 12) {
            return 31;
        } else {
            return 0;
        }
    }

    public static boolean isEmail(String Email) {
        Matcher matcher = EMAIL_REGEX.matcher(Email);
        return matcher.find();
    }

    public boolean isConnectedToInternet(Context c) {
        return checkNet(c);
    }

    public static boolean isValidPass(String Password) {
        if (Password.length() >= 6) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkNet(Context c) {
        //To check internet connectivity.
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void timedSliderInit(ViewPager ig_vp, int[] FragmentList, int Seconds) {
        new CountDownTimer(Seconds * 1000L, 1000) {
            int i = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                i = ig_vp.getCurrentItem();
            }

            @Override
            public void onFinish() {
                i = ig_vp.getCurrentItem();
                try {
                    if (i == FragmentList.length - 1) {
                        ig_vp.setCurrentItem(0);
                    } else {
                        ig_vp.setCurrentItem(i + 1);
                    }
                } catch (Exception ignored) {
                }
                timedSliderInit(ig_vp, FragmentList, Seconds);
            }
        }.start();
    }

    public static void setDefaultBudget(mainViewModel vm, int totalSalary, int totalExp, int budType, String CreationDate) {
        budgetEntity bud = new budgetEntity();
        bud.setAmount(Commons.getValueByPercent(totalSalary, 80));
        bud.setBal(Commons.getValueByPercent(totalSalary, 80) - totalExp);
        bud.setRefreshPeriod(budType);
        bud.setCreationDate(CreationDate);
        vm.DeleteBudget();
        vm.InsertBudget(bud);
    }

    public static ArrayList<debtEntity> debtSorterProMax(ArrayList<debtEntity> debtList) {
        ListSortUtil sorter = new ListSortUtil(debtList);
        return sorter.getSortedList();
    }

    public static void fakeLoadingScreen(Context c, int totalSalary, int totalExp,
                                         int budType, mainViewModel vm, FloatingActionButton anchor, String creationDate) {
        new CountDownTimer(1000, 500) {
            final PopupWindow fakeScrn = new PopupWindow(c);

            @Override
            public void onTick(long millisUntilFinished) {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.popup_budget_fake_loading, null);
                TextView t = view.findViewById(R.id.loadingText);
                String s = "Analyzing Your Earnings..";
                t.setText(s);
                fakeScrn.setContentView(view);
                fakeScrn.setFocusable(true);
                fakeScrn.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                fakeScrn.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                fakeScrn.setBackgroundDrawable(null);
                fakeScrn.setElevation(6);
                fakeScrn.showAsDropDown(anchor);
            }

            @Override
            public void onFinish() {
                fakeScrn.dismiss();
                new CountDownTimer(1000, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.popup_budget_fake_loading, null);
                        TextView t = view.findViewById(R.id.loadingText);
                        String s = "Crafting a ideal budget..";
                        t.setText(s);
                        fakeScrn.setContentView(view);
                        fakeScrn.setFocusable(true);
                        fakeScrn.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                        fakeScrn.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                        fakeScrn.setBackgroundDrawable(null);
                        fakeScrn.setElevation(6);
                        fakeScrn.showAsDropDown(anchor);
                    }

                    @Override
                    public void onFinish() {
                        Commons.setDefaultBudget(vm, totalSalary, totalExp, budType, creationDate);
                        fakeScrn.dismiss();
                    }
                }.start();
            }
        }.start();
    }

    public static boolean isAfterDate(String Date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date dateBefore = sdf.parse(Commons.getDate());
            Date dateAfter = sdf.parse(Date);
            assert dateAfter != null;
            assert dateBefore != null;
            return dateAfter.getTime() <= dateBefore.getTime();
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean budgetValidityChecker(int BudgetType, String Date) {
        try {
            String[] sDate = Date.split("/");
            String newDate;
            if (BudgetType == Constants.BUDGET_WEEKLY) {
                newDate = "07/" + sDate[1] + "/" + sDate[2];
            } else {
                newDate = "31/" + sDate[1] + "/" + sDate[2];
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date refDate = sdf.parse(Commons.getDate());
            Date budgetDate = sdf.parse(Date);
            Date AddDate = sdf.parse(newDate);
            assert refDate != null;
            assert budgetDate != null;
            assert AddDate != null;
            long time = Math.abs(budgetDate.getTime() + AddDate.getTime());
            Date afterDate = new Date(time);
            return afterDate.getTime() >= refDate.getTime();
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void clearData(mainViewModel mainVM) {
        mainVM.DeleteBalance();
        mainVM.DeleteInHandBalance();
        mainVM.DeleteAllDebt();
        mainVM.DeleteAllSalary();
        mainVM.DeleteAllExp();
        mainVM.DeleteBudget();
        mainVM.InsertBalance(new balanceEntity(0));
        mainVM.InsertInHandBalance(new inHandBalEntity(0));
    }
}