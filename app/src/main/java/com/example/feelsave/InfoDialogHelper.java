package com.example.feelsave;

import android.content.Context;
import android.content.DialogInterface;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class InfoDialogHelper {

    public static void showAppInfo(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView messageTextView = new TextView(context);
        messageTextView.setText(context.getString(R.string.infodialog_title) +
                context.getString(R.string.infodialog1) +
                context.getString(R.string.feelsaveinfolink));

        messageTextView.setAutoLinkMask(Linkify.WEB_URLS);
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        messageTextView.setPadding(50, 20, 50, 20);

        builder.setTitle("Wie funktioniert FeelSave?")
                .setView(messageTextView) // Setze den TextView in den Dialog
                .setPositiveButton("Verstanden.", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
