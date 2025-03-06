package com.example.feelsave;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class InfoDialogHelper {

    public static void showAppInfo(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Wie funktioniert FeelSave?")
                .setMessage("Mit einem Knopfdruck sicherer.\n\n" +
                        "1. Erlaube FeelSave alle nötigen Zugriffe!\n\n" +
                        "2. Wähle Notfallkontakte in den Einstellungen aus\n\n" +
                        "3. Personalisiere deine Notfallnachricht (oder lass sie so :))\n\n" +
                        "4. Halte den Button wenn du dich unsicher fühlst, nach ein Paar Sekunden geht der SaveMode an\n\n" +
                        "5. Lässt du nun den Finger ein Paar Sekunden vom ab, werden deine Notfallkontakte alamiert und bekommen deinen Standort\n\n" +
                        "6. Für mehr Infos, besuche unsere Infoseite\n\n")
                .setPositiveButton("Verstanden.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
