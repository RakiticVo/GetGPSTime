package com.example.getgpstime;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    private android.location.Location location;
    private LocationBroadcastReceiver receiver;
    TextView tv_show_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiver = new LocationBroadcastReceiver();
        tv_show_time = findViewById(R.id.tv_show_time);
        // request location permission.
        requestPermision();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!locationManager.isLocationEnabled()) {
                    buildAlertMessageNoLocation();
                }
            }
        }
//        updateLocation();
    }

    private void updateLocation() {
        IntentFilter filter = new IntentFilter("ACT_LOC");
        // Đăng ký BR
        registerReceiver(receiver, filter);
        Toast.makeText(this, "registerReceiver success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, GPSServices.class);
        startService(intent);
    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    private void buildAlertMessageNoLocation(){
        new AlertDialog.Builder(this)
                .setMessage("Your Location seems to be disabled, do you want to enable it?")
                .setPositiveButton("Settings", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Hàm tạo ra một BR
    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAG", "onReceive:" + intent.getAction());
            if (intent.getAction().equals("ACT_LOC")) {
                Location mlocation = intent.getParcelableExtra("lastLocation");
                location = mlocation;
//                Toast.makeText(context, ""+location.getTime(), Toast.LENGTH_SHORT).show();
//                Log.e("TAG", "onReceive: " + location.getLatitude() + "--" + location.getLongitude());

                String time = getDate(location.getTime(), "dd/MM/yyyy hh:mm:ss:SSS");
                tv_show_time.setText(""+ time);
            }
            Toast.makeText(context, "Vị trí: " + location.getLatitude() + "--" + location.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(MainActivity.this, GPSServices.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, GPSServices.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocation();
    }
}