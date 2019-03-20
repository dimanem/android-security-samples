package com.dimanem.security.example.demo.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.dimanem.security.example.demo.ROOT_BLOCK_ENABLED
import com.dimanem.security.example.demo.BaseActivity
import com.dimanem.security.example.demo.MainActivity
import com.dimanem.security.example.demo.R
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File

class LoginActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    // Root check
    if (ROOT_BLOCK_ENABLED && isRootedDevice()) {
      showErrorDialog(
        "Root detected!",
        "This app can't be used on a rooted device",
        false
      )
    }

    initViews()
    initObservations()
  }

  private fun isRootedDevice() : Boolean {
    val rootFiles = arrayOf(
      "/system/app/Superuser.apk",
      "/sbin/su",
      "/system/bin/su",
      "/system/xbin/su",
      "/data/local/xbin/su",
      "/data/local/bin/su",
      "/system/sd/xbin/su",
      "/system/bin/failsafe/su",
      "/data/local/su",
      "/su/bin/su"
    )
    for (path in rootFiles) {
      if (File(path).exists()) {
        return true
      }
    }
    return false
  }

  private fun initViews() {
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

  private fun initObservations() {
    loginViewModel.apply {
      loginSuccessLiveData.observe({ lifecycle }) {
        Timber.i("Login success!")
        showProgress(false)
        launchMainActivity()
      }
      loginFailedLiveData.observe({ lifecycle }) {
        Timber.e("Login failed with error ${it?.localizedMessage}")
        showProgress(false)
        showErrorDialog("Sign In error", it?.localizedMessage ?: "Unknown error!")
      }
    }
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
    val userIdStr = username.text.toString()
    val passwordStr = password.text.toString()

    var cancel = false
    var focusView: View? = null

    // Check for a valid password, if the user entered one.
    if (TextUtils.isEmpty(passwordStr) || !isPasswordValid(passwordStr)) {
      password.error = getString(R.string.error_invalid_password)
      focusView = password
      cancel = true
    }

    // Check for a valid email address.
    if (TextUtils.isEmpty(userIdStr)) {
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
      loginViewModel.login(userIdStr, passwordStr)
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

  companion object {
    const val INTENT_ACTION_LOGOUT = "INTENT_ACTION_LOGOUT"
  }

  private val loginViewModel: LoginViewModel by viewModel()
}
