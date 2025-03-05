package com.example.feelsave;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class InfoDialogHelper {

    public static void showAppInfo(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("App Information") // Titel
                .setMessage("Dies ist eine Informationsbeschreibung der App.\n\n" +
                        "Hier kannst du eine kurze Zusammenfassung über die App, ihre Funktionen und Ziele schreiben.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Schließt den Dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
