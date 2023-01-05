package bca.sendit.filetransfer;

import android.content.Context;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;

public class App {
    public static void setToolbarTransparent(Context context, ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(context,
                    android.R.color.transparent));
            actionBar.setElevation(0);
        }
    }

}
