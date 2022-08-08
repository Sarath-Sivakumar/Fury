package app.personal.Utls;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Commons {

    public static void SnackBar(View v, String Text) {
        Snackbar.make(v, Text, Snackbar.LENGTH_SHORT).show();
    }

    public static int OneTimeSnackBar(View v, String Text, int count) {
        //Increase OneTimeSnackBarCount by 1 after calling once
        //Call like this:
//        private static int count = 0;
//        if (Commons.OneTimeSnackBar(getView(),"Set Salary.",count)){
//            count++;
//        }
        Thread t = new Thread(() -> {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        while (t.getState() == Thread.State.TERMINATED && count == 0){
                Snackbar.make(v, Text, Snackbar.LENGTH_SHORT).show();
                count = count + 1;
        }
        return count;
    }

    public static String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    public static String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(new Date());
    }

    public static int setProgress(float exp, float sal) {
        return (int) ((exp / sal) * 100);
    }
}