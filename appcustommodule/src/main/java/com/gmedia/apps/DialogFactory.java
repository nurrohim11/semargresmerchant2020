package com.gmedia.apps;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class DialogFactory {
    private static final DialogFactory ourInstance = new DialogFactory();

    public static DialogFactory getInstance() {
        return ourInstance;
    }

    private DialogFactory() {
    }

    public Dialog createDialog(Activity activity, int res_id, int widthpercentage, int heightpercentage){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);
        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * widthpercentage / 100 ,
                    device_TotalHeight * heightpercentage / 100);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }

    public Dialog createDialog(Activity activity, int res_id, int anim_id, int widthpercentage, int heightpercentage){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);

        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * widthpercentage / 100 ,
                    device_TotalHeight * heightpercentage / 100);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            //lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = anim_id;
            dialog.getWindow().setAttributes(lp);
        }

        return dialog;
    }
}
