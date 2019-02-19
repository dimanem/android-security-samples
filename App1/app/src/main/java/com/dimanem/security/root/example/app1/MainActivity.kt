package com.dimanem.security.root.example.app1

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.dimanem.security.root.example.app1.Constants.Companion.BASE_URL
import com.dimanem.security.root.example.app1.Constants.Companion.SHARED_PREFS_KEY_USER_NAME
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private lateinit var sharedPrefs: SharedPreferences
  private val securityUtils = SecurityUtils()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

    // Demo 1 - repackaging with apktool- modifying base url colors and title
    tvBaseUrlValue.text = "$BASE_URL"

    // Demo 2 - rooted device - malicious app stealing data
    etUserNameValue.setText(sharedPrefs.getString(SHARED_PREFS_KEY_USER_NAME, ""))
    btnSaveUserName.setOnClickListener {
      saveNewUserName()
    }

    // Demo 3 - uncomment - xposed - malicious app hooks our root check
//    performSecurityChecks()
  }

  private fun performSecurityChecks() {
//    if (securityUtils.isApkRepackaged(this)) {
//      showSimpleDialog("Repackaged app", "This app is not from original developer")
//    }

    // Check root
    if (securityUtils.isRootedDevice) {
      showSimpleDialog("Rooted Device", "You are not allowed to use this app on a rooted device")
    }
  }

  private fun showSimpleDialog(title: String, text: String) {
    val dialogBuilder = AlertDialog.Builder(this).apply {
      setTitle(title)
      setMessage(text)
//      setPositiveButton("Ok") { _, _ -> }
      setCancelable(false)
      create()
    }
    dialogBuilder.show()
  }

  private fun saveNewUserName() {
    etUserNameValue.text?.toString().let { newUserName ->
      if (newUserName != null && newUserName.isNotEmpty()) {
        sharedPrefs.edit().putString(SHARED_PREFS_KEY_USER_NAME, newUserName).apply()
        Toast.makeText(this, "User name saved: $newUserName", Toast.LENGTH_SHORT).show()
        hideKeyboard()
      }
    }
  }

  private fun hideKeyboard() {
    val view = this.currentFocus
    view?.let { v ->
      val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
    }
  }
}
