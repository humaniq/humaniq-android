package co.humaniq;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by andrey on 23.05.17.
 */

public class DebugTool {

    public interface DialogButton {
        void onClick();
    }

    public static void showDialog(Context context, String title, String message,
                                  DialogButton positiveButton, DialogButton negativeButton){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message);

        if (positiveButton != null){
            builder.setPositiveButton("Ok", (dialog, which) -> positiveButton.onClick());
        }
        if (negativeButton != null){
            builder.setNegativeButton("Cancel", (dialog, which) -> negativeButton.onClick());
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showDialog(Context context, String title, String message){
        showDialog(context, title, message, null, null );
    }
}
