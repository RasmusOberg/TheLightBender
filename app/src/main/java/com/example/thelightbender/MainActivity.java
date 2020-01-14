package com.example.thelightbender;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private CameraManager cameraManager;
    private Sensor sensorLight, sensorProximity;
    private boolean sensorLightPresent, sensorProximityPresent, isFlashLightOn, flashlightEnabled, windowBrightness;
    private Window window;
    private ContentResolver contentResolver;
    private float brightness;
    private String cameraID;
    private CameraCharacteristics parameters;
    private RadioGroup group1, group2;
    private RadioButton btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    private Button confirm;
//    private SeekBar seekBar;
    private double choice;
    private int add = 0;
    private ImageView flashlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        flashlight = findViewById(R.id.flashlight);
        group1 = findViewById(R.id.group1);
        group2 = findViewById(R.id.group2);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);
        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
//        seekBar = findViewById(R.id.seekbar);
//        seekBar.setMax(100);
//        seekBar.setMin(0);
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioID2 = group2.getCheckedRadioButtonId();
                int index = group2.indexOfChild(findViewById(group2.getCheckedRadioButtonId()));
                Log.d(TAG, "onClick: Val = " + index);
                if(radioID2 == 2131165223)
                    windowBrightness = true;
                else if(radioID2 == 2131165224){
                    windowBrightness = false;
                    Toast.makeText(getApplicationContext(),  "This is permanent!", Toast.LENGTH_SHORT).show();
                }
                int group1ID = group1.getCheckedRadioButtonId();
                Log.d(TAG, "onClick: " + group1ID);
                if(group1ID == 2131165218) {
                    choice = 0.05;
                }
                else if(group1ID == 2131165219) {
                    choice = 0.15;
                    add += 10;
                }
                else if(group1ID == 2131165220) {
                    choice = 0.30;
                    add += 30;
                }
                else if(group1ID == 2131165221) {
                    choice = 0.45;
                    add += 50;
                }
                else {
                    choice = 1.0;
                    add += 100;
                }

            }
        });
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                int value = seekBar.getProgress();
//                Log.d(TAG, "onProgressChanged: value = " + value);
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
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

    public void changeScreenBrightness(float brightness) {
        this.brightness = brightness;

        if (!windowBrightness) {
            if (!Settings.System.canWrite(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                startActivity(i);
            } else {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, (int) (this.brightness * 255 * choice) + add);
            }
        }else {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = this.brightness;
            window.setAttributes(layoutParams);
        }

        try {
            Log.d(TAG, "changeScreenBrightness: current value = " +
                    Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void turnOnFlashlight(){
        if(flashlightEnabled){
            flashlight.setVisibility(View.VISIBLE);
            Log.d(TAG, "turnOnFlashlight: ON");
            isFlashLightOn = true;

//            if(parameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
//                try {
//                    cameraManager.setTorchMode(cameraID, true);
//             } catch (CameraAccessException e) {
//                     e.printStackTrace();
//                 }
//             }
        }
    }
    
    public void turnOffFlashlight(){
        if(!flashlightEnabled){
            flashlight.setVisibility(View.INVISIBLE);
            isFlashLightOn = false;
            Log.d(TAG, "turnOffFlashlight: OFF");

//            if(parameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
//                try {
//                    cameraManager.setTorchMode(cameraID, false);
//              } catch (CameraAccessException e) {
//                  e.printStackTrace();
//               }
//           }
        }
    }

    public void setFlashlightEnabled(boolean bool){
        flashlightEnabled = bool;
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
