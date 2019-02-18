package com.dimanem.security.repackaged.example.myapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog

class MainActivity : AppCompatActivity() {

  private val BASE_URL = "https://www.example.com"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    showBaseUrlDialog()
  }

  private fun showBaseUrlDialog() {
    val alertDialogBuilder = AlertDialog.Builder(this)
    alertDialogBuilder.setTitle("Base url")
    alertDialogBuilder.setMessage("$BASE_URL")
    val alertDialog = alertDialogBuilder.create()
    alertDialog.show()
  }
}
