/**
 *
 */
package com.hltc.mtmap.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public static void showShort(Context context, Object object) {
        Toast.makeText(context, object.toString(), Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, Object object) {
        Toast.makeText(context, object.toString(), Toast.LENGTH_LONG).show();
    }
}
