package com.cse570.gesture.accelerometer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;
import android.os.PowerManager;


public class SleepDetector implements SensorEventListener {

    private PowerManager powerManager;
    private Sensor mAccelerometer;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    private float changeinX = 0;
    private float changeinY = 0;
    private float changeinZ = 0;
    private long lastUpdate = 0;
    private TextView accelerationX, accelerationY, accelerationZ;
    Activity activity;
    private SleepDetector.OnSleepListener mListener;

    public void setOnSleepListener(SleepDetector.OnSleepListener listener) {
        this.mListener = listener;
    }

    public interface OnSleepListener {
        public void onSleep();
    }

    public SleepDetector(PowerManager _powerManager, Sensor _mAccelerometer,
                         String _classname, Activity _activity)
    {
        Log.wtf("WIRELESS", "Inside Constructor");
        powerManager = _powerManager;
        mAccelerometer = _mAccelerometer;
        wakeLock = powerManager.newWakeLock(field, _classname);


        activity = _activity;
        accelerationX = activity.findViewById(R.id.accelerationX);
        accelerationY = activity.findViewById(R.id.accelerationY);
        accelerationZ = activity.findViewById(R.id.accelerationZ);
        Log.wtf("WIRELESS", "Constructed");
    }


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

            updateDisplay();

            changeinX = event.values[0];
            changeinY = event.values[1];
            changeinZ = event.values[2];

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

    public void updateDisplay(){
        accelerationX.setText("0.0");
        accelerationY.setText("0.0");
        accelerationZ.setText("0.0");
        accelerationX.setText(Float.toString(changeinX));
        accelerationY.setText(Float.toString(changeinY));
        accelerationZ.setText(Float.toString(changeinZ));
    }

}
