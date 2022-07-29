package app.personal.Utls;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Commons {
    public static void SnackBar(View v, String Text){
        Snackbar.make(v,Text,Snackbar.LENGTH_SHORT).show();
    }
}
