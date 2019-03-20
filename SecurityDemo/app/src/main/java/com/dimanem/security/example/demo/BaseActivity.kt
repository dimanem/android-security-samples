package com.dimanem.security.example.demo

import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

open class BaseActivity: AppCompatActivity() {

  open fun showErrorDialog(title: String, text: String, isCancelable: Boolean = true) {
    val builder = AlertDialog.Builder(this)
      .setTitle(title)
      .setMessage(text)
      .setCancelable(isCancelable)
    if (isCancelable) {
      builder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
    }
    builder.create()
    builder.show()
  }

  open fun showToast(message: String) {
//    Snackbar.make(main_container, message, Snackbar.LENGTH_LONG).show()
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
  }
}