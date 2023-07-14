package com.justluxurylifestyle.get_things_done_droid.ui.message

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.justluxurylifestyle.get_things_done_droid.R
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModel


fun displayAlertDialog(id: String, context: Context, message: String, viewModel: TaskViewModel) {
    AlertDialog.Builder(context).apply {
        setMessage(message)
        setPositiveButton(
            context.getString(R.string.delete_task)
        ) { _, _ -> viewModel.deleteTask(id) }
        setNegativeButton(
            context.getString(R.string.cancel_delete_task)
        ) { _, _ ->
            Toast.makeText(
                context,
                context.getString(R.string.pressed_cancel_delete),
                Toast.LENGTH_SHORT
            )
                .show()
        }
        create()
    }.show()
}

fun showToastMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}