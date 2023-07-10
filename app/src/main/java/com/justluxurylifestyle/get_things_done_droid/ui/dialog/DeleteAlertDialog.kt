package com.justluxurylifestyle.get_things_done_droid.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.justluxurylifestyle.get_things_done_droid.viewmodel.TaskViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
fun displayAlertDialog(id: String, context: Context, message: String, viewModel: TaskViewModel) {
    val builder = AlertDialog.Builder(context)
    builder.setMessage(message)
        .setPositiveButton(
            "delete"
        ) { _, _ -> viewModel.deleteTask(id) }
        .setNegativeButton(
            "cancel"
        ) { _, _ ->
            Toast.makeText(context, "pressed cancel", Toast.LENGTH_SHORT).show()
        }
    builder.create()
    builder.show()
}
