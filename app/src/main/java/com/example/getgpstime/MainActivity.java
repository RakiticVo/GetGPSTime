package com.example.getgpstime;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;
    private int delay;
    private android.location.Location location;
    private LocationBroadcastReceiver receiver;
    TextView tv_show_time_GPS, tv_show_Device_time, tv_show_time_LocationListener , tv_show_time_delay_locationListener, tv_show_time_delay_service, tv_show_time_Service;
    LinearLayout layout_solution_1, layout_solution_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiver = new LocationBroadcastReceiver();
        tv_show_time_GPS = findViewById(R.id.tv_show_time_GPS);
        tv_show_Device_time = findViewById(R.id.tv_show_time_NTP);
        tv_show_time_LocationListener = findViewById(R.id.tv_show_time_LocationListener);
        tv_show_time_delay_locationListener = findViewById(R.id.tv_show_time_delay_locationListener);
        tv_show_time_delay_service = findViewById(R.id.tv_show_time_delay_service);
        tv_show_time_Service = findViewById(R.id.tv_show_time_Service);
        layout_solution_1 = findViewById(R.id.layout_solution_1);
        layout_solution_2 = findViewById(R.id.layout_solution_2);
      //  tv_show_my_time2 = findViewById(R.id.tv_show_time_MyTime2);
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

        //
//        InitialiseLocationListener(getApplicationContext());
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
    private void buildAlertMessageNoLocation() {
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
                String time = getDate(location.getTime(), "hh:mm:ss");
                Date currentTime = Calendar.getInstance().getTime();
                DateFormat df = new SimpleDateFormat("hh:mm:ss");
                String timeDevide = df.format(currentTime);
                tv_show_time_GPS.setText("GPS GET SERVER: " + time);
                tv_show_Device_time.setText("DEVICE: " + timeDevide);
                Log.e("TAG2", "onLocationChanged: " + location.getProvider() );
                //   Log.e("Location", "Time GPS: " + time); // This is what we want!
                tv_show_time_Service.setText("GPS Time And Delay :" + time);
                delay = delayTime(time, currentTime);
                tv_show_time_delay_service.setText(delay+"");
                layout_solution_2.setVisibility(View.VISIBLE);
            }
        }
    }

//    public void InitialiseLocationListener(android.content.Context context) {
//        LocationManager locationManager = (android.location.LocationManager)
//                context.getSystemService(android.content.Context.LOCATION_SERVICE);
//        LocationListener locationListener = new android.location.LocationListener() {
//            public void onLocationChanged(android.location.Location location) {
//                Date currentTime = Calendar.getInstance().getTime();
//                String time = new java.text.SimpleDateFormat("HH:mm:ss").format(location.getTime());
//                Log.e("TAG2", "onLocationChanged: " + location.getProvider() );
//                if (location.getProvider().equals(android.location.LocationManager.GPS_PROVIDER))
//                {
//                 //   Log.e("Location", "Time GPS: " + time); // This is what we want!
//                    tv_show_time_LocationListener.setText("GPS Time And Delay :" + time);
//                    delay = delayTime(time, currentTime);
//                    tv_show_time_delay_locationListener.setText(delay+"");
//                }
//                else
//                {
//               //     Log.e("Location", "Time Device (" + location.getProvider() + "): " + time);
//                    String mTime = uploadTime(location.getTime(),delay);
//                    tv_show_time_LocationListener.setText("GPS Time And Delay :" + mTime);
//                    tv_show_time_delay_locationListener.setText(delay+"");
//                }
//                layout_solution_1.setVisibility(View.VISIBLE);
//            }
//        };
//
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
//        locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
//        // Note: To Stop listening use: locationManager.removeUpdates(locationListener)
//    }
    // Tính Delay
    public int delayTime(String time, Date currentTime)
    {
        DateFormat df = new SimpleDateFormat("ss");
        String timeDevide = df.format(currentTime);
        String a[] =  time.split(":");
        Log.e("TAG3", ""+time);
        Log.e("TAG3", ""+timeDevide);
        int delay = Integer.parseInt(a[2]) - Integer.parseInt(timeDevide);
        Log.e("Log", "Vao delayTime :"+ delay);
        if (delay >  0)
        {
            return delay;
        }
        else return 0;
    }
    // Update time when GPS Time is not working
    public static String uploadTime(long milliSeconds, int delay)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        int mdelay = delay * 1000 ;
        calendar.setTimeInMillis(milliSeconds + mdelay);
      //  Log.e("Log" , "Delay : " +mdelay + " | " + delay);
        return formatter.format(calendar.getTime());
    }
    // Convert Time
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