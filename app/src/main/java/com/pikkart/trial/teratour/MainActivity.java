package com.pikkart.trial.teratour;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.pikkart.ar.recognition.IRecognitionListener;
import com.pikkart.ar.recognition.RecognitionFragment;
import com.pikkart.ar.recognition.RecognitionOptions;
import com.pikkart.ar.recognition.data.CloudRecognitionInfo;
import com.pikkart.ar.recognition.items.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private int m_permissionCode = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT < 23){
            startActivity(new Intent(getBaseContext(),ImageCloudRecoClass.class));
        }
        else
        {
            checkPermissions(m_permissionCode);
        }

    }



    private void checkPermissions(int code) {
        String[] permissions_required = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE};

        List permissions_not_granted_list = new ArrayList<>();
        for (String permission : permissions_required) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissions_not_granted_list.add(permission);
            }
        }
        if (permissions_not_granted_list.size() > 0) {
            String[] permissions = new String[permissions_not_granted_list.size()];
            permissions_not_granted_list.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, code);
        }
        else {
            startActivity(new Intent(getBaseContext(),ImageCloudRecoClass.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == m_permissionCode) {
            boolean ok = true;
            for(int i=0;i<grantResults.length;++i) {
                ok = ok && (grantResults[i]==PackageManager.PERMISSION_GRANTED);
            }
            if(ok) {
                startActivity(new Intent(getBaseContext(),ImageCloudRecoClass.class));
            }
            else {
                Toast.makeText(this, "Error: required permissions not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}