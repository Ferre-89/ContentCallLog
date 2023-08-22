package com.example.contentcalllog;

import static android.Manifest.permission.READ_CALL_LOG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Rodrigo", "onCreate");
        if (EasyPermissions.hasPermissions(this, READ_CALL_LOG)) {
            doTheStuff();
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, 123, Manifest.permission.READ_CALL_LOG)
                        .setRationale("Test Permissions")
                        .setPositiveButtonText("Yes")
                        .setNegativeButtonText("No")
                        .setTheme(R.style.Theme_ContentCallLog)
                        .build());
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d("Rodrigo", "onPermissionsGranted");
        doTheStuff();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void doTheStuff() {
        Log.d("Rodrigo", "doTheStuff");

        String[] CALL_TYPE = {"", "incoming", "outgoing", "missed", "voicemail", "canceled", "blocked list"};
        TextView out = findViewById(R.id.out);
        Uri calls = Uri.parse("content://call_log/calls");

        Cursor c = getContentResolver().query(calls, null, null, null);

        if (c != null && c.moveToFirst()) {
            do {
                long callDate = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
                String callDuration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));
                String phoneNumber = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                int callType = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));

                if (callType >= 0 && callType < CALL_TYPE.length) {
                    String typeString = CALL_TYPE[callType];
                    out.append("\n" + DateFormat.format("dd/MM/yy k:mm (", callDate)
                            + callDuration + ") " + phoneNumber + ", " + typeString);
                }
            } while (c.moveToNext());

            c.close();
        }
    }
}
