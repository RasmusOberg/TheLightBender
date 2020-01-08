package com.example.thelightbender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private CameraManager cameraManager;
    private Sensor sensorLight, sensorProximity;
    private boolean sensorLightPresent, sensorProximityPresent, isFlashLightOn;
    private Window window;
    private ContentResolver contentResolver;
    private float brightness;
    private String cameraID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensorEventListener = new Listener(this);
//        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        getSensors();
        registerSensor();
        initializeScreenBrightness();
//        try{
//            cameraID = cameraManager.getCameraIdList()[0];
//            Log.d(TAG, "onCreate: Antal kameror = " + cameraManager.getCameraIdList().length);
//            Log.d(TAG, "onCreate: " + cameraManager.getCameraCharacteristics(cameraID));
//            for(int i = 0; i < cameraManager.getCameraIdList().length; i++){
//                Log.d(TAG, "onCreate: Kamera nr: " + i + " " + cameraManager.getCameraCharacteristics(cameraManager.getCameraIdList()[i]));
//            }
//        }catch (CameraAccessException e){
//            e.printStackTrace();
//        }
    }

    public void getSensors(){
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            sensorLight = sensorManager.getDefaultSensor((Sensor.TYPE_LIGHT));
            sensorLightPresent = true;
        }else{
            sensorLightPresent = false;
        }if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) !=  null){
            sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorProximityPresent = true;
        }else{
            sensorProximityPresent = false;
        }
    }

    public void changeScreenBrightness(float brightness){
        this.brightness = brightness;

        if(!Settings.System.canWrite(this)){
            Intent i = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(i);
        }else{
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, (int)(this.brightness*255));
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = this.brightness;
        window.setAttributes(layoutParams);

        int a = 0;
        try {
            a = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "changeScreenBrightness: " + a);
    }

    public void registerSensor(){
        if(sensorLightPresent)
            sensorManager.registerListener(sensorEventListener, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        if(sensorProximityPresent)
            sensorManager.registerListener(sensorEventListener, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensor(){
        if(sensorLightPresent)
            sensorManager.unregisterListener(sensorEventListener);
        if(sensorProximityPresent)
            sensorManager.unregisterListener(sensorEventListener);
    }

    public void initializeScreenBrightness(){
        contentResolver = getContentResolver();
        window = getWindow();
    }

    @Override
    protected void onPause(){
        super.onPause();
        registerSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        unregisterSensor();
    }
}
