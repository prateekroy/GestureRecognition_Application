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

public class ChopDetector implements SensorEventListener{

    private ChopDetector.OnChopListener mListener;
    private final float threshold = 15f;
    private final long timeForChopGesture = 250;
    private long lastTimeChopDetected = System.currentTimeMillis();
    private boolean isGestureInProgress = false;

    public void setOnChopListener(ChopDetector.OnChopListener listener) {
        this.mListener = listener;
    }

    public interface OnChopListener {
        public void onChop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.wtf("WIRELESS", "Chop Event");
        if (mListener != null) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // Make this higher or lower according to how much
            // motion you want to detect
//            if (x > threshold && y < (-threshold) && z > threshold) {
            if (x < -2 && y < 8 && z > 0){
                lastTimeChopDetected = System.currentTimeMillis();
                isGestureInProgress = true;
            }
            else if (x > 8 && y < 3 ){
                long timeDelta = (System.currentTimeMillis() - lastTimeChopDetected);
                if (timeDelta > timeForChopGesture && isGestureInProgress) {
                    isGestureInProgress = false;
                    mListener.onChop();
                }
            }

        }
    }
}
