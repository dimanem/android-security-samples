package com.dimanem.security.root.example.app1

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private lateinit var sharedPrefs: SharedPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Init shared prefs
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

    // Fetch last saved value (if any)
    etUserName.setText(sharedPrefs.getString(SHARED_PREFS_KEY_USER_NAME, ""))

    // Save current value
    btnSave.setOnClickListener {
      saveNewUserName()
    }
  }

  private fun saveNewUserName() {
    etUserName.text?.toString().let { newUserName ->
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

  companion object {
    const val SHARED_PREFS_KEY_USER_NAME = "KEY_USER_NAME"
  }
}
