package com.example.thelightbender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private CameraManager cameraManager;
    private Sensor sensorLight, sensorProximity;
    private boolean sensorLightPresent, sensorProximityPresent, isFlashLightOn, flashlightEnabled;
    private Window window;
    private ContentResolver contentResolver;
    private float brightness;
    private String cameraID;
    private CameraCharacteristics parameters;
    private RadioGroup group1, group2;
    private RadioButton btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: FUCK");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensorEventListener = new Listener(this);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        getSensors();
        registerSensor();
        initializeScreenBrightness();
        initUI();

        try{
            cameraID = cameraManager.getCameraIdList()[0];
            parameters = cameraManager.getCameraCharacteristics(cameraID);
            Log.d(TAG, "onCreate: Antal kameror = " + cameraManager.getCameraIdList().length);
            Log.d(TAG, "onCreate: " + cameraManager.getCameraCharacteristics(cameraID));
            for(int i = 0; i < cameraManager.getCameraIdList().length; i++){
                Log.d(TAG, "onCreate: Kamera nr: " + i + " " + cameraManager.getCameraCharacteristics(cameraManager.getCameraIdList()[i]));
            }
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    public void initUI(){
        group1 = findViewById(R.id.group1);
        group2 = findViewById(R.id.group2);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);
        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioID = group2.getCheckedRadioButtonId();
                Log.d(TAG, "onClick: Val = " + radioID);
            }
        });
    }

    public void getSensors(){
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            sensorLight = sensorManager.getDefaultSensor((Sensor.TYPE_LIGHT));
            sensorLightPresent = true;
        }else{
            sensorLightPresent = false;
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) !=  null){
            sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorProximityPresent = true;
        }else{
            sensorProximityPresent = false;
        }
    }

    public void setFlashlightEnabled(boolean bool){
        flashlightEnabled = bool;
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
    
    public void turnOnFlashlight(){
        if(flashlightEnabled){
            Log.d(TAG, "turnOnFlashlight: ON");
            if(parameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                try {
                    cameraManager.setTorchMode(cameraID, true);
                 isFlashLightOn = true;
             } catch (CameraAccessException e) {
                     e.printStackTrace();
                 }
             }
        }
    }
    
    public void turnOffFlashlight(){
        if(!flashlightEnabled){
            Log.d(TAG, "turnOffFlashlight: OFF");
            if(parameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                try {
                    cameraManager.setTorchMode(cameraID, false);
                 isFlashLightOn = false;
              } catch (CameraAccessException e) {
                  e.printStackTrace();
               }
           }
        }
    }

    public boolean getFlashlightStatus(){
        return isFlashLightOn;
    }

    public void registerSensor(){
        if(sensorLightPresent)
            sensorManager.registerListener(sensorEventListener, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        if(sensorProximityPresent)
            sensorManager.registerListener(sensorEventListener, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "registerSensor: Registrerat");
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
        unregisterSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensor();
    }
}
