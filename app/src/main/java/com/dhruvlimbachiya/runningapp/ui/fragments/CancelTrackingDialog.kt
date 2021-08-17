package com.dhruvlimbachiya.runningapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dhruvlimbachiya.runningapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Created by Dhruv Limbachiya on 16-08-2021.
 */

class CancelTrackingDialog : DialogFragment() {

    private var positiveListener : (() -> Unit)? = null

    // Set the listener.
    fun setPositiveListener(listener: () -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).apply {
                setTitle("Cancel the Run?")
                setMessage("Are you sure to cancel the current run and delete all its data?")
                setIcon(R.drawable.ic_delete)
                setPositiveButton("Yes") { _, _ ->
                    positiveListener?.let { yesListener ->
                        yesListener()
                    }
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            }.create()
    }
}