package com.dimanem.security.root.example.app1;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

  public boolean isApkRepackaged(Context context) {
    if (BuildConfig.DEBUG) {
      return false;
    }
    try {
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
      Signature[] signatures = packageInfo.signatures;
      String currentSignature = signatureToBase64(signatures[0]);
      String realSignature = context.getResources().getString(R.string.release_signature);
      if (!realSignature.trim().equals(currentSignature.trim())) {
        return true;
      }
    } catch (Exception e) {
    }

    return false;
  }

  public boolean isRootedDevice() {
    String[] paths = {
        "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
        "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
        "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
    };
    for (String path : paths) {
      if (new File(path).exists()) return true;
    }
    return false;
  }

  private static String signatureToBase64(Signature signature) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA");
    md.update(signature.toByteArray());
    return android.util.Base64.encodeToString(md.digest(), android.util.Base64.DEFAULT);
  }
}
