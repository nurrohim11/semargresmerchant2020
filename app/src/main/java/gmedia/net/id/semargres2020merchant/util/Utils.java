package gmedia.net.id.semargres2020merchant.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import java.text.SimpleDateFormat;
import java.util.Locale;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.merchant.KirimEmailMerActivity;

public class Utils {
    public static SimpleDateFormat dateFormat(String pattern){
        return new SimpleDateFormat(pattern, Locale.getDefault());
    }
    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
