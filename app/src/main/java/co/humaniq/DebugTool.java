package co.humaniq;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class DebugTool {
    public static void showDialog(Context context, String title, String message,
                                  DialogInterface.OnClickListener positiveButton,
                                  DialogInterface.OnClickListener negativeButton)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message);

        if (positiveButton != null)
            builder.setPositiveButton("Ok", positiveButton);

        if (negativeButton != null)
            builder.setNegativeButton("Cancel", negativeButton);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showDialog(Context context, String title, String message){
        showDialog(context, title, message, null, null );
    }
}
