package com.example.diman.simplexposedmodule;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;

public class SimpleModule implements IXposedHookLoadPackage {

  @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
    // Hook only my app
    if (!loadPackageParam.packageName.equals("com.example.diman.hookedapp")) {
      return;
    }

    XposedBridge.log("Hooking Dima's app");
    ClassLoader classLoader = loadPackageParam.classLoader;

    //hookAppMethod(classLoader);
    hookOSMethod(classLoader);
  }

  /**
   * Hook the app method so that RootChecker.isDeviceRooted() = false.
   * Attacker knows the app method name doing the root check.
   */
  private void hookAppMethod(ClassLoader classLoader) {
    try {
      XposedHelpers.findAndHookMethod("com.example.diman.hookedapp.RootChecker", classLoader,
          "isDeviceRooted", (Object[]) new Object[] {
              new XC_MethodHook() {

                protected void afterHookedMethod(MethodHookParam methodHookParam) {
                  methodHookParam.setResult(false);
                }
              }
          });
    } catch (Exception e) {
      XposedBridge.log(e);
    }
  }

  /**
   * Hook Android call - File.exist
   * Attacker doesn't know the app method name but knows how root check is done and
   * hooks the internal logic.
   */
  private void hookOSMethod(ClassLoader classLoader) {
    try {
      XposedHelpers.findAndHookMethod("java.io.File", classLoader, "exists",
          (Object[]) new Object[] {
              new XC_MethodHook() {

                protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                  String string = ((File) methodHookParam.thisObject).getAbsolutePath();
                  if (string.endsWith("/su") || string.endsWith("/Superuser.apk")) {
                    methodHookParam.setResult(false);
                  }
                }
              }
          });
    } catch (Throwable throwable) {
      XposedBridge.log(throwable);
    }
  }
}
