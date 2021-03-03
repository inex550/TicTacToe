package com.example.tictactoe

import android.app.Activity
import android.app.AlertDialog

class WaitDialog(
    private val activity: Activity
) {
    private lateinit var dialog: AlertDialog

    fun startWaitDialog() {
        val builder = AlertDialog.Builder(activity)
            .setView(activity.layoutInflater.inflate(R.layout.wait_dialog, null))
            .setCancelable(false)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}