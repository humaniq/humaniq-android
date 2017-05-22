package co.humaniq.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import co.humaniq.App;
import co.humaniq.Preferences;
import co.humaniq.models.WalletHMQ;


public class ResetPinCodeController {
    private Context context;
    private Preferences preferences;

    private void alert(final String title, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.setTitle(title).setMessage(message).create();
        alertDialog.show();
    }

    public ResetPinCodeController(Context context) {
        this.context = context;
        this.preferences = App.getPreferences(context);
    }

    public void handle() {
        if (!WalletHMQ.hasKeyOnDevice(context)) {
            alert("Error", "There is no key on your device");
            return;
        }

        displayYesNoDialog();
    }

    private void displayYesNoDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // Nothing
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Reset pin code?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }
}
