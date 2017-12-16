package com.example.accelerometer.accelerometer_example;

/**
 * Created by PRATEEK on 11/30/2017.
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.FloatMath;
import android.util.Log;

import java.security.Policy;

public class ChopDetector implements SensorEventListener{

    private ChopDetector.OnChopListener mListener;
    private final float threshold = 15f;
    private final long timeForChopGesture = 250;
    private long lastTimeChopDetected = System.currentTimeMillis();
    private boolean isGestureInProgress = false;
    private boolean bflashOn = false;
    private CameraManager objCameraManager;

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

    public void SetCameraObject(CameraManager _obj){
        objCameraManager = _obj;
    }



    //Reference : https://www.spaceotechnologies.com/implement-flashlight-torchlight-android-app/
    public void TurnOnFlash() throws CameraAccessException {
        String mCameraId = "";
        try {
            mCameraId = objCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            //e.printStackTrace();
        }
        if (mCameraId != "" && bflashOn == false) {
            objCameraManager.setTorchMode(mCameraId, true);
            bflashOn = true;
        }
    }

    public void TurnOffFlash() {
        String mCameraId = "";
        try {
            mCameraId = objCameraManager.getCameraIdList()[0];
            if (mCameraId != "" && bflashOn == true) {
                objCameraManager.setTorchMode(mCameraId, false);
                bflashOn = false;
            }
        } catch (CameraAccessException e) {
            //e.printStackTrace();
        }

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
                    try {
                        if (bflashOn) {
                            TurnOffFlash();
                        }
                        else{
                            TurnOnFlash();
                        }
                    }
                    catch (CameraAccessException e){
                        //Cant turn on flash
                    }
                }
            }

        }
    }
}
