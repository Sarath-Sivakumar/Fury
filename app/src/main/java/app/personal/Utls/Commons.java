package app.personal.Utls;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Commons {

    public static void SnackBar(View v, String Text) {
        Snackbar.make(v, Text, Snackbar.LENGTH_SHORT).show();
    }

    public static boolean OneTimeSnackBar(View v, String Text, int count) {
        //Increase OneTimeSnackBarCount by 1 after calling once
        //Call like this:
//        private static int count = 0;
//        if (Commons.OneTimeSnackBar(getView(),"Set Salary.",count)){
//            count++;
//        }
        if (count == 0) {
            Snackbar.make(v, Text, Snackbar.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
}