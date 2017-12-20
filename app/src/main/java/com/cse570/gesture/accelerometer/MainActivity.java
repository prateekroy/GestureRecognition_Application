package com.cse570.gesture.accelerometer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Vibrator vibrator;
    private PowerManager mPowerManager;
    private CameraManager objCameraManager;
    private Camera camera;
    private Context mContext;
    private Activity mActivity;
//    private OutputStreamWriter out;
    private Button button;
    private int state = 0;
    private int shakeCount = 0;
    private int chopCount = 0;
    private int sleepCount = 0;
    //Gesture Class
    private ShakeDetector mShakeDetector;
    private ChopDetector mChopDetector;
    private SleepDetector mSleepDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.wtf("WIRELESS", "main onCreate");
        super.onCreate(savedInstanceState);
        Toast.makeText(MainActivity.this, "Welcome!",
                Toast.LENGTH_LONG).show();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        objCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        mContext = this;
        mActivity = this;

        setContentView(R.layout.activity_main);

        //AccelerometerExample
        createAccelerometerExample();

        //Shake Detector
        createShakeDetector();

        //Chop Detector
        createChopDetector();

        ScreenNoti();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSleepDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mChopDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mChopDetector.TurnOffFlash();
        mSensorManager.unregisterListener(mShakeDetector);
        mSensorManager.unregisterListener(mSleepDetector);
        mSensorManager.unregisterListener(mChopDetector);
        super.onPause();
    }

    public void ScreenNoti(){
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    Log.d("LOGGONG", Intent.ACTION_SCREEN_OFF);
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    Log.d("LOGGONG", Intent.ACTION_SCREEN_ON);
                }
            }
        }, intentFilter);
    }

    void createAccelerometerExample(){
        String localClass = getLocalClassName();
        mSleepDetector = new SleepDetector(mPowerManager, mAccelerometer,
                localClass, mActivity);
        mSleepDetector.setOnSleepListener(new SleepDetector.OnSleepListener() {
            @Override
            public void onSleep() {
                        sleepCount++;
            }
        });
    }

    void createShakeDetector(){
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake() {
                Log.d("WIRELESS", "I got a shake");
                Toast.makeText(mContext, "Shake!",
                        Toast.LENGTH_SHORT).show();
                shakeCount++;

            }
        });
    }

    void createChopDetector(){
        mChopDetector = new ChopDetector();
        mChopDetector.setOnChopListener(new ChopDetector.OnChopListener() {
            @Override
            public void onChop() {
                Log.d("WIRELESS", "I got a chop");
                Toast.makeText(mContext, "Chop!",
                        Toast.LENGTH_SHORT).show();
                chopCount++;

            }
        });
        mChopDetector.SetCameraObject(objCameraManager);
    }



    /** Called when the user touches the button */
    public void ButtonClicked(View view) {
        // Do something in response to button click
        button = (Button) mActivity.findViewById(R.id.button_send);
        if (state == 0) {
            button.setText("Send");

            sleepCount = 0;
            shakeCount = 0;
            chopCount = 0;
            state = 1;
        }
        else{
            button.setText("Start Log");
            Save("SmartScreenTrigger : " + sleepCount + " ShakeCount : " + shakeCount + " ChopCount : " + chopCount);
            ReadFileAndReport();
            state = 0;
        }
    }

    void Save(String str){
        try {
//            OutputStreamWriter out = new OutputStreamWriter(openFileOutput("logData.txt", Context.MODE_APPEND));
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput("logData.txt", 0));
            out.write(str);
            out.close();
        }
        catch(Throwable t){
            Log.d("LOGGONG", "Exception cannot write");
        }
    }



    public void ReadFileAndReport()
    {
        try {
            InputStream inStream = openFileInput("logData.txt");

            if (inStream != null) {
                InputStreamReader streamReader=new InputStreamReader(inStream);
                if (streamReader == null){
                    Log.d("LOGGONG", "Stream Reader NULL");
                    return;
                }
                BufferedReader reader=new BufferedReader(streamReader);
                if (reader == null){
                    Log.d("LOGGONG", "BufferedReader NULL");
                    return;
                }
                StringBuilder buf=new StringBuilder(); String str;

                while ((str = reader.readLine()) != null) {
//                    Log.d("LOGGONG", buf.toString());
                    buf.append(str);
                }

//                in.close();

                Log.d("LOGGONG", buf.toString());
                SendMail(buf.toString());
            }
        }

        catch (java.io.FileNotFoundException e) {
            // file not created yet
        }

        catch (Throwable t) {
            //
        }
    }


    public void SendMail(String body) {

        //send file using email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        // Set type to "email"
        emailIntent.setType("vnd.android.cursor.dir/email");
        String toemail[] = {"prateek00000@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, toemail);

        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Log Data");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
        startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }
}
