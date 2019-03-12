package com.dimanem.security.example.demo.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.dimanem.security.example.demo.MainActivity
import com.dimanem.security.example.demo.R
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

  companion object {
    const val INTENT_ACTION_LOGOUT = "INTENT_ACTION_LOGOUT"
  }

  private val loginViewModel: LoginViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    if (intent.action == INTENT_ACTION_LOGOUT) {
      Timber.i("Logout")
      loginViewModel.logout()
    } else if (loginViewModel.isLoggedIn()) {
      Timber.i("Auto Login")
      launchMainActivity()
      return
    }

    loginViewModel.apply {
      loginSuccessLiveData.observe({ lifecycle }) {
        Timber.i("Login success!")
        showProgress(false)
        launchMainActivity()
      }
      loginFailedLiveData.observe({ lifecycle }) {
        Timber.e("Login failed with error ${it?.localizedMessage}")
        showProgress(false)
        showSignInErrorDialog(it?.localizedMessage ?: "Unknown error!")
      }
    }

    // Set up the login form.
    password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
      if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
        attemptLogin()
        return@OnEditorActionListener true
      }
      false
    })

    btnSignIn.setOnClickListener { attemptLogin() }
  }

  private fun launchMainActivity() {
    startActivity(Intent(this, MainActivity::class.java))
    finish()
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  private fun attemptLogin() {
    // Reset errors.
    username.error = null
    password.error = null

    // Store values at the time of the login attempt.
    val emailStr = username.text.toString()
    val passwordStr = password.text.toString()

    var cancel = false
    var focusView: View? = null

    // Check for a valid password, if the user entered one.
    if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
      password.error = getString(R.string.error_invalid_password)
      focusView = password
      cancel = true
    }

    // Check for a valid email address.
    if (TextUtils.isEmpty(emailStr)) {
      username.error = getString(R.string.error_field_required)
      focusView = username
      cancel = true
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView?.requestFocus()
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true)
      loginViewModel.login(emailStr, passwordStr)
    }
  }

  private fun isPasswordValid(password: String): Boolean {
    return password.length > 3
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private fun showProgress(show: Boolean) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

      login_form.visibility = if (show) View.GONE else View.VISIBLE
      login_form.animate()
        .setDuration(shortAnimTime)
        .alpha((if (show) 0 else 1).toFloat())
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            login_form.visibility = if (show) View.GONE else View.VISIBLE
          }
        })

      login_progress.visibility = if (show) View.VISIBLE else View.GONE
      login_progress.animate()
        .setDuration(shortAnimTime)
        .alpha((if (show) 1 else 0).toFloat())
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
          }
        })
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      login_progress.visibility = if (show) View.VISIBLE else View.GONE
      login_form.visibility = if (show) View.GONE else View.VISIBLE
    }
  }

  private fun showSignInErrorDialog(error: String) {
    AlertDialog.Builder(this)
      .setTitle("Sign in error")
      .setMessage(error)
      .setCancelable(true)
      .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
      .create()
      .show()
  }
}
