package com.cse570.gesture.accelerometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.os.PowerManager;


public class SleepDetector implements SensorEventListener {

    private PowerManager powerManager;
    private Sensor mAccelerometer;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    private float lastX, lastY, lastZ;
    private float changeinXMax = 0;
    private float changeinYMax = 0;
    private float changeinZMax = 0;
    private float changeinX = 0;
    private float changeinY = 0;
    private float changeinZ = 0;
    private long lastUpdate = 0;
    private float vibrateThreshold = 0;
    private TextView accelerationX, accelerationY, accelerationZ, maxX, maxY, maxZ;
    public Vibrator v;
    Context context;
    Activity activity;
    private SleepDetector.OnSleepListener mListener;

    public void setOnSleepListener(SleepDetector.OnSleepListener listener) {
        this.mListener = listener;
    }

    public interface OnSleepListener {
        public void onSleep();
    }

    public SleepDetector(PowerManager _powerManager, Sensor _mAccelerometer,
                         Vibrator _vibrator, String _classname,
                         Context _context, Activity _activity)
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



    public void initializeViews() {
        accelerationX = (TextView) activity.findViewById(R.id.accelerationX);
        accelerationY = (TextView) activity.findViewById(R.id.accelerationY);
        accelerationZ = (TextView) activity.findViewById(R.id.accelerationZ);
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
//                    Log.d("LOGGONG", "Wake");
                    mListener.onSleep();
                    wakeLock.release();
                }
            }
            // clean acceleration values
            displayCleanValues();
            // display the acceleration x,y,z accelerometer values
            displayaccelerationValues();
            // display the max x,y,z accelerometer values
            displayMaxValues();
            // get the change of the x,y,z values of the accelerometer
            changeinX = Math.abs(lastX - event.values[0]);
            changeinY = Math.abs(lastY - event.values[1]);
            changeinZ = Math.abs(lastZ - event.values[2]);
            // if the change is below 2, it is just plain noise
            if (changeinX < 2)
                changeinX = 0;
            if (changeinY < 2)
                changeinY = 0;
            if ((changeinZ > vibrateThreshold) || (changeinY > vibrateThreshold) || (changeinZ > vibrateThreshold)) {
                v.vibrate(50);
            }
            if (event.values[2] <= 0) {

                if (!wakeLock.isHeld()) {
//                    Log.d("LOGGONG", "Sleep");
                    mListener.onSleep();
                    wakeLock.acquire();
                }
            }
            lastUpdate = curTime;
        }

    }
    public void displayCleanValues() {
        accelerationX.setText("0.0");
        accelerationY.setText("0.0");
        accelerationZ.setText("0.0");
    }



    // display the acceleration x,y,z accelerometer values

    public void displayaccelerationValues() {
        accelerationX.setText(Float.toString(changeinX));
        accelerationY.setText(Float.toString(changeinY));
        accelerationZ.setText(Float.toString(changeinZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (changeinX > changeinXMax) {
            changeinXMax = changeinX;
            maxX.setText(Float.toString(changeinXMax));
        }
        if (changeinY > changeinYMax) {
            changeinYMax = changeinY;
            maxY.setText(Float.toString(changeinYMax));
        }
        if (changeinZ > changeinZMax) {
            changeinZMax = changeinZ;
            maxZ.setText(Float.toString(changeinZMax));
        }
    }
}
