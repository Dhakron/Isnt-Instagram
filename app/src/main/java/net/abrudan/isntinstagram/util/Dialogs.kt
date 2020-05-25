package net.abrudan.isntinstagram.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import net.abrudan.isntinstagram.R
import net.abrudan.isntinstagram.views.login.registerFR.RegisterFR3Directions

class DialogExitSetUserID : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.exit_register3))
                .setPositiveButton(getString(R.string.btn_exit)
                ) { _, _ ->
                    requireActivity().finish()
                }
                .setNegativeButton(getString(R.string.btn_cancel)
                ) { _, _ ->
                    dialog?.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}