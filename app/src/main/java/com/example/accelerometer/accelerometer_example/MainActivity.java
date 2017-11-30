package com.example.accelerometer.accelerometer_example;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Vibrator vibrator;
    private PowerManager mPowerManager;
    private Context mContext;
    private Activity mActivity;
    //Gesture Class
    private ShakeDetector mShakeDetector;
    private AndroidAccelerometerExample mAccelerometerExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.wtf("WIRELESS", "main onCreate");
        super.onCreate(savedInstanceState);
        Toast.makeText(MainActivity.this, "This is my Toast message!",
                Toast.LENGTH_LONG).show();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mContext = this;
        mActivity = this;

        setContentView(R.layout.activity_main);

        //AccelerometerExample
        createAccelerometerExample();

        //Shake Detector
        createShakeDetector();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mAccelerometerExample, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        mSensorManager.unregisterListener(mAccelerometerExample);
        super.onPause();
    }

    void createAccelerometerExample(){
        String localClass = getLocalClassName();
        mAccelerometerExample = new AndroidAccelerometerExample(mPowerManager, mAccelerometer,
                vibrator, localClass,
                mContext, mActivity);
    }

    void createShakeDetector(){
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
//                handleShakeEvent(count);
                Log.d("WIRELESS", "I got a shake");
                Toast.makeText(mContext, "Shake!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
