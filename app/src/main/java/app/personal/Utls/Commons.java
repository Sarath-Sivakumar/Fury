package app.personal.Utls;


import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import app.personal.MVVM.Entity.expEntity;

public class Commons {

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
        int daily = 0;
        String lastDate = "";
        ArrayList<Integer> perDayExp = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);
        for (int i = listData.size() - 1; i >= 0; i--) {
            expEntity exp = listData.get(i);
            if (i == listData.size() - 1) {
                daily = exp.getExpenseAmt();
                lastDate = exp.getDate();
            } else {
                if (lastDate.equals(exp.getDate())) {
                    daily = daily + exp.getExpenseAmt();
                } else {
                    try {
                        String[] itemDate = exp.getDate().split("/");
                        String[] lastDateSplit = exp.getDate().split("/");
                        int itemMonth = Integer.parseInt(itemDate[1]);
                        int lastMonth = Integer.parseInt(lastDateSplit[1]);
                        int itemYear = Integer.parseInt(itemDate[2]);
                        int lastYear = Integer.parseInt(lastDateSplit[2]);
                        Date dateBefore = sdf.parse(lastDate);
                        Date dateAfter = sdf.parse(exp.getDate());
                        assert dateAfter != null;
                        assert dateBefore != null;
                        long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

                        perDayExp.add(daily);
                        Log.e("Daily", "Value: " + daily);

                        if (itemMonth == lastMonth && itemYear == lastYear) {
                            for (int i1 = 1; i1 < daysDiff; i1++) {
                                perDayExp.add(0);
                                Log.e("Daily", "Adding 0");
                            }
                        }
                        daily =exp.getExpenseAmt();
                        lastDate = exp.getDate();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (AvgDisplay) {
            return findAvg(perDayExp);
        } else {
            return limiterAvg(perDayExp);
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
}