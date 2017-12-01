package com.example.accelerometer.accelerometer_example;

/**
 * Created by PRATEEK on 11/30/2017.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.util.Log;

public class TwistDetector implements SensorEventListener{

    private TwistDetector.OnTwistListener mListener;
    private final long timeForWristTwistGesture = 1000;
    private long lastTimeWristTwistDetected = System.currentTimeMillis();
    private boolean isGestureInProgress = false;

    public void setOnTwistListener(TwistDetector.OnTwistListener listener) {
        this.mListener = listener;
    }

    public interface OnTwistListener {
        public void onTwist();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        // Make this higher or lower according to how much
        // motion you want to detect
        if (x < -9.8f && y > -3f && z < (-15f)) {
            lastTimeWristTwistDetected = System.currentTimeMillis();
            isGestureInProgress = true;
        }
        else {
            long timeDelta = (System.currentTimeMillis() - lastTimeWristTwistDetected);
            if (timeDelta > timeForWristTwistGesture && isGestureInProgress) {
                isGestureInProgress = false;
                mListener.onTwist();
            }
        }
    }
}
