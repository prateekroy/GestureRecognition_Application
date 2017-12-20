package com.example.accelerometer.accelerometer_example;

/**
 * Created by PRATEEK on 11/15/2017.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeDetector implements SensorEventListener {

    private OnShakeListener mListener;
    private long mtime;

    long GetCurrentTime(){
        return System.currentTimeMillis();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public interface OnShakeListener {
        public void onShake();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    public void onSensorChanged(SensorEvent event) {
        Log.wtf("WIRELESS", "Shake Event");

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float X = x;
        float Y = y;
        float Z = z;
        X /= SensorManager.GRAVITY_EARTH;
        Y /= SensorManager.GRAVITY_EARTH;
        Z /= SensorManager.GRAVITY_EARTH;

        float force = (float)Math.sqrt(X * X + Y * Y + Z * Z);

        if (force > 2.7F) {
            long currentTime = GetCurrentTime();
            // ignore shake events too close to each other (500ms)
            if (mtime + 500 > currentTime) {
                return;
            }

            mtime = currentTime;
            //Call the listner in main activity
            mListener.onShake();
        }
    }

}