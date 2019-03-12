package com.dimanem.security.example.demo

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.dimanem.security.example.demo.login.LoginActivity
import com.dimanem.security.example.demo.util.SecurityUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

// TODO pull to refresh data
class MainActivity : AppCompatActivity() {

  private val securityUtils: SecurityUtils by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    if (ROOT_CHECK_ENABLED && securityUtils.isRootedDevice()) {
      showErrorDialog("Root detected", "Un-root device to continue")
    } else {
      showToast("Login Success!!!")
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.main_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.logout -> {
        logout()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun logout() {
    val intent = Intent(this, LoginActivity::class.java)
    intent.action = LoginActivity.INTENT_ACTION_LOGOUT
    startActivity(intent)
    finish()
  }

  private fun showErrorDialog(title: String, text: String) {
    AlertDialog.Builder(this)
      .setTitle(title)
      .setMessage(text)
      .setCancelable(false)
      .create()
      .show()
  }

  private fun showToast(message: String) {
//    Snackbar.make(main_container, message, Snackbar.LENGTH_LONG).show()
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
  }
}
