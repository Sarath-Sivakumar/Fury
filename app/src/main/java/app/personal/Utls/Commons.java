package app.personal.Utls;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.personal.MVVM.Entity.expEntity;

public class Commons {

//    Change this later for better accuracy!
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);

    public static void SnackBar(View v, String Text) {
        Snackbar.make(v, Text, Snackbar.LENGTH_SHORT).show();
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

    public static String getSeconds(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("ss");
        return sdf.format(new Date());
    }

    public static int setProgress(float exp, float sal) {
        return (int) ((exp / sal) * 100);
    }

    public static int getDay(){
        Date d = new Date();
        return d.getDay();
    }

    public static String getDisplayDay(int day){
        if (day==1){
            return "Monday";
        }else if (day==2){
            return "Tuesday";
        }else if(day==3){
            return "Wednesday";
        }else if(day==4){
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
        int lastDay= -1;
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
                        Log.e("DailyTotal", "Value: " + dailyTotal + " Date: " + lastDate + " Day: "+ getDisplayDay(lastDay));

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
                        Log.e("DailyTotal", "Value: " + dailyTotal1 + " Date: " + lastDate + " Day: "+ getDisplayDay(lastDay));
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

    private static String limiterAvg(ArrayList<Integer> totalExp) {
        int total = 0;
        if (totalExp.size() >= 7) {
            for (int i = 0; i < totalExp.size(); i++) {
                total = total + totalExp.get(i);
            }
            return String.valueOf(total / totalExp.size());
        }else {
            return String.valueOf(total);
        }
    }

    private static String findAvg(ArrayList<Integer> totalExp) {
        //7 for 1 week
        if (totalExp.size() >= 7) {
            int total = 0;
            for (int i = 0; i < totalExp.size(); i++) {
                total = total + totalExp.get(i);
            }
            Log.e("AVG", "Size: " + totalExp.size() + "Total: " + total);
            return Constants.RUPEE + total / totalExp.size() + "/Day";
        } else {
            return Constants.dAvgNoData;
        }
    }

    public static int getValueByPercent(int totalSalary, int Percent){
        return (int)(totalSalary*Percent)/100;
    }

    public static int getDays(int month){
        if (month==1){
            return 31;
        }else if(month==2){
            return 28;
        }else if(month==3){
            return 31;
        }else if(month==4){
            return 30;
        }else if(month==5){
            return 31;
        }else if(month==6){
            return 30;
        }else if(month==7){
            return 31;
        }else if(month==8){
            return 31;
        }else if(month==9){
            return 30;
        }else if(month==10){
            return 31;
        }else if(month==11){
            return 30;
        }else if(month==12){
            return 31;
        }else{
            return 0;
        }
    }

    public static String getDailyAvg(float budget){
        return String.valueOf((int)(budget/getDays(Integer.parseInt(getMonth()))));
    }

    public static boolean isEmail(String Email){
        Matcher matcher = EMAIL_REGEX.matcher(Email);
        return matcher.find();
    }

    public boolean isConnectedToInternet(Context c){
        return checkNet(c);
    }

    public static boolean isValidPass(String Password){
        if (Password.length()>=6){
            return true;
        }else{
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

    public static void timedSliderInit(ViewPager ig_vp , int[] FragmentList, int Seconds) {
        new CountDownTimer(Seconds* 1000L, 1000) {
            int i = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                i = ig_vp.getCurrentItem();
            }

            @Override
            public void onFinish() {
                i = ig_vp.getCurrentItem();
                try{
                    if (i == FragmentList.length - 1) {
                        ig_vp.setCurrentItem(0);
                    } else {
                        ig_vp.setCurrentItem(i + 1);
                    }
                }catch (Exception ignored){}
                timedSliderInit(ig_vp, FragmentList, Seconds);
            }
        }.start();
    }
}