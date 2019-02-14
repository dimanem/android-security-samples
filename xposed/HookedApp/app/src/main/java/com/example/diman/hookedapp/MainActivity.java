package com.example.diman.hookedapp;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private RootChecker mRootDetector = new RootChecker();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (mRootDetector.isDeviceRooted()) {
      showRootedDeviceDialog();
    }
  }

  private void showRootedDeviceDialog() {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setTitle("Rooted Device!");
    alertDialogBuilder.setMessage("You are not allowed to use this app on a rooted device");
    alertDialogBuilder.setCancelable(false);
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }
}
