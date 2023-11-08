package com.example.assign_mobilicis.Utility;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showToastShort(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
