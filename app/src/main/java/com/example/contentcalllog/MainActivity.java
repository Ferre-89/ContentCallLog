package com.example.contentcalllog;

import static android.Manifest.permission.READ_CALL_LOG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Rodrigo", "onCreate");
        if (EasyPermissions.hasPermissions(this, READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)) {
            managingContentProvider();
            listCalls();
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, 123, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)
                        .setRationale("Test Permissions")
                        .setPositiveButtonText("Yes")
                        .setNegativeButtonText("No")
                        .setTheme(R.style.Theme_ContentCallLog)
                        .build());
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d("Rodrigo", "onPermissionsGranted");
        listCalls();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void managingContentProvider() {

        // Creating ContentValues and adding personalized data.
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.DATE, new Date().getTime());
        values.put(CallLog.Calls.NUMBER, "555555555");
        values.put(CallLog.Calls.DURATION, "55");
        values.put(CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE);

        // Inserting data. The variable "newElement" saves the amount of changes.
        Uri newElement = getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);

        // Updating data.
        ContentValues values2 = new ContentValues();
        values2.put(CallLog.Calls.NUMBER, "444444444");
        getContentResolver().update(CallLog.Calls.CONTENT_URI, values2, "Number = 555555555", null);


        // Deleting elements from the Content Provider.
        getContentResolver().delete(CallLog.Calls.CONTENT_URI, "number = 555555555", null);

    }

    private void listCalls() {
        Log.d("Rodrigo", "doTheStuff");

        String[] CALL_TYPE = {"", "incoming", "outgoing", "missed", "voicemail", "canceled", "blocked list"};
        TextView out = findViewById(R.id.out);
        Uri calls = Uri.parse("content://call_log/calls");

//        Cursor c = getContentResolver().query(calls, null, null, null);

        String[] projection = new String[] {CallLog.Calls.DATE, CallLog.Calls.DURATION,
                CallLog.Calls.NUMBER, CallLog.Calls.TYPE};
        String[] argsSelecc = new String[] {"1"};

        Cursor c = getContentResolver().query(
                calls, //provider URI
                projection, // Columns we want
                "type = ?", // WHERE queries
                argsSelecc, // Parameters of the previous query
                "date DESC"); // Sorted by date, descending order

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
