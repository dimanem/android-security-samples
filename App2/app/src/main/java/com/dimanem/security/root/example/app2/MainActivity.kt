package com.dimanem.security.root.example.app2

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.IOException
import java.io.StringReader

class MainActivity : AppCompatActivity() {

  private var sharedPrefsFile: File? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    sharedPrefsFile = getSharedPrefsFileForPackage(APP1_PACKAGE_NAME)
  }

  override fun onResume() {
    super.onResume()
    sharedPrefsFile?.let {
      etApp1Username.text = getUsername(it)
    }
  }

  private fun getSharedPrefsFileForPackage(packageName: String): File? {
    try {
      // Get the relevant package context
      val sharedPrefs = PreferenceManager
          .getDefaultSharedPreferences(createPackageContext(packageName, 0))

      // At this point we can't read the shared preferences yet
      // Trying to call

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
          "Failed to get app1 shared prefs shared prefs with error: ${e.localizedMessage}",
          Toast.LENGTH_LONG
      ).show()
    }
    return null
  }

  // For simplicity, let's assume that the shared prefs file contains
  // only 1 entry and it is the entry that we need.
  // "Normally" one would parse the xml to a Map and ready the values
  private fun getUsername(sharedPrefsFile: File): String {
    try {
      val runtime = Runtime.getRuntime()
      val commandBuilder = StringBuilder().apply {
        append("cat \"")
        append(sharedPrefsFile)
        append("\"")
      }

      val process = runtime.exec(arrayOf("su", "-c", commandBuilder.toString()))

      val fileContent = process.inputStream.bufferedReader().use {
        it.readText()
      }

      return readUserNameFromXml(fileContent)

    } catch (e: Exception) {
      Toast.makeText(
          this,
          "Failed to read app1 shared prefs shared prefs with error: ${e.localizedMessage}",
          Toast.LENGTH_LONG
      ).show()
    }

    return ""
  }

  private fun readUserNameFromXml(xml: String): String {
    try {
      val factory = XmlPullParserFactory.newInstance()
      factory.isNamespaceAware = true
      val xpp = factory.newPullParser()

      xpp.setInput(StringReader(xml)) // pass input whatever xml you have
      var eventType = xpp.eventType
      while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
          XmlPullParser.START_DOCUMENT -> {
          }
          XmlPullParser.START_TAG -> {
          }
          XmlPullParser.END_TAG -> {
          }
          XmlPullParser.TEXT -> {
            if (!xpp.text.startsWith("\n")) {
              return xpp.text
            }
          }
        }
        eventType = xpp.next()
      }
    } catch (e: XmlPullParserException) {
      e.printStackTrace()
    } catch (e: IOException) {
      e.printStackTrace()
    }

    return ""
  }

  companion object {
    const val APP1_PACKAGE_NAME = "com.dimanem.security.root.example.app1"
    const val APP1_USERNAME_SHARED_PREFS_KEY = "KEY_USER_NAME"
  }
}
