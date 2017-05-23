package co.humaniq.controllers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import co.humaniq.App
import co.humaniq.Preferences
import co.humaniq.models.WalletHMQ


class ResetPinCodeController(private val context: Context) {
    private val preferences: Preferences = App.getPreferences(context)

    private fun alert(title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        val alertDialog = builder.setTitle(title).setMessage(message).create()
        alertDialog.show()
    }

    fun handle() {
        if (!WalletHMQ.hasKeyOnDevice(context)) {
            alert("Error", "There is no key on your device")
            return
        }

        displayYesNoDialog()
    }

    private fun displayYesNoDialog() {
        val dialogClickListener = { dialog: DialogInterface, which: Int ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }// Nothing
        }

        val builder = AlertDialog.Builder(context)
        builder.setMessage("Reset pin code?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show()
    }
}
