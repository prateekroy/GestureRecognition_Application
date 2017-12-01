package com.example.accelerometer.accelerometer_example;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.os.PowerManager;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.widget.Toast;

import static android.content.Context.POWER_SERVICE;


public class AndroidAccelerometerExample implements SensorEventListener {

    private PowerManager powerManager;
    private Sensor mAccelerometer;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    private float lastX, lastY, lastZ;
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private long lastUpdate = 0;
    private float vibrateThreshold = 0;
    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;
    public Vibrator v;
    Context context;
    Activity activity;

    public AndroidAccelerometerExample(PowerManager _powerManager, Sensor _mAccelerometer,
                                       Vibrator _vibrator, String _classname,
                                       Context _context,Activity _activity)
    {
        Log.wtf("WIRELESS", "Inside Constructor");
        powerManager = _powerManager;
        mAccelerometer = _mAccelerometer;
        wakeLock = powerManager.newWakeLock(field, _classname);
        vibrateThreshold = mAccelerometer.getMaximumRange() / 2;

        v = _vibrator;
        context = _context;
        activity = _activity;
        initializeViews();
        Log.wtf("WIRELESS", "Constructed");
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.wtf("WIRELESS", "AccelExample on Create");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        initializeViews();
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
//            // success! we have an accelerometer
//            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//            vibrateThreshold = accelerometer.getMaximumRange() / 2;
//        } else {
//            // fai! we dont have an accelerometer!
//        }
//        //initialize vibration
//
//        try {
//            // Yeah, this is hidden field.
//            field = PowerManager.class.getClass().getField("SCREEN_BRIGHT_WAKE_LOCK").getInt(null);
//        } catch (Throwable ignored) {
//        }
//
//
//    }
    public void initializeViews() {
        currentX = (TextView) activity.findViewById(R.id.currentX);
        currentY = (TextView) activity.findViewById(R.id.currentY);
        currentZ = (TextView) activity.findViewById(R.id.currentZ);
        maxX = (TextView) activity.findViewById(R.id.maxX);
        maxY = (TextView) activity.findViewById(R.id.maxY);
        maxZ = (TextView) activity.findViewById(R.id.maxZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.wtf("WIRELESS", "AccelExample Event");
        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdate) > 100) {
            if (event.values[2] > 0) {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }
            // clean current values
            displayCleanValues();
            // display the current x,y,z accelerometer values
            displayCurrentValues();
            // display the max x,y,z accelerometer values
            displayMaxValues();
            // get the change of the x,y,z values of the accelerometer
            deltaX = Math.abs(lastX - event.values[0]);
            deltaY = Math.abs(lastY - event.values[1]);
            deltaZ = Math.abs(lastZ - event.values[2]);
            // if the change is below 2, it is just plain noise
            if (deltaX < 2)
                deltaX = 0;
            if (deltaY < 2)
                deltaY = 0;
            if ((deltaZ > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
                v.vibrate(50);
            }
            if (event.values[2] <= 0) {

                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
            }
        }

    }
    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }



    // display the current x,y,z accelerometer values

    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }
}