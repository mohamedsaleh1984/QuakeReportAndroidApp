package com.mega.android.quakereport.UI;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;

public class ErrorDialog
{

    public static void Show(Context context, String strDialogTitle, String strDialogMessage, @DrawableRes int iResourceIdIcon)
    {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(strDialogTitle)
                .setMessage(strDialogMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)//android.R.drawable.ic_dialog_alert)
                .show();
    }
}
