package com.dimanem.security.root.example.app2;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.File;

public class SimpleModule implements IXposedHookLoadPackage {

  @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
    if (!loadPackageParam.packageName.equals("com.dimanem.security.example.demo")) {
      return;
    }

    // Manipulate File.exist(su) to return false for our SecurityDemo app
    // Hacker might use this technique when your code is well obfuscated
    // and he doesn't find your root check.
    try {
      XposedHelpers.findAndHookMethod("java.io.File", loadPackageParam.classLoader, "exists",
          new XC_MethodHook() {

            protected void afterHookedMethod(MethodHookParam methodHookParam) {
              String file = ((File) methodHookParam.thisObject).getAbsolutePath();
              if (file.endsWith("/su") || file.endsWith("/Superuser.apk")) {
                methodHookParam.setResult(false);
              }
            }
          });
    } catch (Throwable throwable) {
      XposedBridge.log(throwable);
    }
  }
}
