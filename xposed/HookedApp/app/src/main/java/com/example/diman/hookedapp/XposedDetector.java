package com.example.diman.hookedapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import dalvik.system.DexFile;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XposedDetector {

  private static final String TAG = "XposedDetector";

  private Context mContext;
  private String mPackageName;

  public XposedDetector(Context context) {
    this.mContext = context;
    this.mPackageName = context.getPackageName();
  }

  // gizatlxynb.com.waze.de.robv.androidxjcxsziakfXposedBridge.jarkgxbyyuchmXdesopBridge.jar , xor 'i', xor index
  private static final byte[] XPOSED_KEY = new byte[] {
      14, 1, 17, 11, 25, 0, 23, 23, 15, 2, 77, 1, 10, 9, 73, 17, 24, 2, 30, 84, 25, 25, 81, 12, 30,
      18, 5, 92, 20, 26, 19, 4, 38, 33, 47, 50, 39, 47, 55, 61, 59, 41, 34, 41, 35, 28, 55, 41, 42,
      61, 63, 24, 47, 53, 59, 57, 52, 126, 57, 51, 39, 63, 48, 46, 75, 81, 82, 95, 78, 68, 66, 118,
      69, 69, 80, 77, 85, 102, 85, 79, 93, 95, 94, 20, 87, 93, 77
  };

  public boolean isXposedInsalled(Context context) {
    StringBuilder decrypted = new StringBuilder();
    for (int i = 0; i < XPOSED_KEY.length; i++) {
      decrypted.append((char)((XPOSED_KEY[i] ^ 'i') ^ i));
    }
    String key = decrypted.toString();
    return
        //xposedMethod1(context, key) ||
        xposedMethod2(key)
        // || xposedMethod3(key)
        ;
  }

  private boolean xposedMethod1(Context context, String key) {
    PackageManager packageManager = context.getPackageManager();
    List<ApplicationInfo> applicationInfoList =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

    for (ApplicationInfo applicationInfo : applicationInfoList) {
      if (applicationInfo.packageName.contains(key.substring(20, 35))) {
        Log.e("Xposed", "Mehod 1!");
        return true;
      }
    }

    return false;
  }

  private boolean xposedMethod2(String key) {
    try {
      throw new Exception("No error");
    } catch (Exception e) {
      for (StackTraceElement stackTraceElement : e.getStackTrace()) {
        if (stackTraceElement.getClassName().contains(key.substring(20, 35))) {
          Log.e("Xposed", "Mehod 2!");
          return true;
        }
      }
    }
    return false;
  }

  private boolean xposedMethod3(String key) {
    BufferedReader reader = null;
    boolean result = false;

    Log.e("Xposed", "Looking for: " + key.substring(45, 61) + ", " + key.substring(71, 87));

    try {
      Set<String> libraries = new HashSet();
      String mapsFilename = "/proc/" + android.os.Process.myPid() + "/maps";
      reader = new BufferedReader(new FileReader(mapsFilename));
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.endsWith(".jar")) {
          int n = line.lastIndexOf(" ");
          libraries.add(line.substring(n + 1));
        }
      }

      for (String library : libraries) {
        Log.e("Xposed", "Library name: " + library);
        if (library.contains(key.substring(45, 61)) || library.contains(key.substring(71, 87))) {
          Log.e("Xposed", "Mehod 3!");
          result = true;
        }
      }
    } catch (Exception e) {
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
        }
      }
    }
    return result;
  }
}
