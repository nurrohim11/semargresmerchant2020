package gmedia.net.id.semargres2019merchant.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import gmedia.net.id.semargres2019merchant.R;


public class Proses {
    private Dialog dialog;

    public Proses(Context context) {
        Context context1 = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
    }

    public void ShowDialog() {
        dialog.show();
    }

    public void DismissDialog() {
        dialog.dismiss();
    }

}
