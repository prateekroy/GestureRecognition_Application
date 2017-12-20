package com.example.accelerometer.accelerometer_example;

/**
 * Created by PRATEEK on 10/30/2017.
 */
import android.hardware.Camera;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;


public class ChopDetector implements SensorEventListener{

    private ChopDetector.OnChopListener mListener;
    private long lastDetect = System.currentTimeMillis();
    private boolean isChop = false;
    private boolean bflashOn = false;
    private CameraManager objCameraManager;

    long GetCurrentTime(){
        return System.currentTimeMillis();
    }

    public void setOnChopListener(ChopDetector.OnChopListener listener) {
        this.mListener = listener;
    }

    public interface OnChopListener {
        public void onChop();
    }

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

    public void onSensorChanged(SensorEvent event) {
//        Log.wtf("WIRELESS", "Chop Event");

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (x < -2 && y < 8 && z > 0){
            lastDetect = GetCurrentTime();
            isChop = true;
        }
        else if (x > 8 && y < 3 ){
            long diff = (GetCurrentTime() - lastDetect);
            if (diff > 250 && isChop) {
                isChop = false;
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
