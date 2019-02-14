package com.example.diman.hookedapp;

import android.util.Log;
import java.io.File;

public class RootChecker {

  public boolean isDeviceRooted() {
    return checkRootMethod1();
  }

  private boolean checkRootMethod1() {
    String[] paths = {
        "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
        "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
        "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
    };
    for (String path : paths) {
      Log.d("RootChecker", "Found root file: " + path);
      if (new File(path).exists()) return true;
    }
    return false;
  }
}
