package com.hltc.mtmap.app;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by redoblue on 15-7-12.
 */
public class DialogManager {

    private static ProgressDialog dialog;

    public static ProgressDialog getProgressDialog(Context context) {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("无缝链接中...");
        }
        return dialog;
    }

    public static ProgressDialog buildProgressDialog(Context context, String text) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(text);
        return dialog;
    }
}
