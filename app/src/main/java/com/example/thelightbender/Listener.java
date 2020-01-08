package com.example.thelightbender;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class Listener implements SensorEventListener {
    private static final String TAG = "Listener";
    private MainActivity main;
    private float luxValue, distanceFromPhone;

    public Listener(MainActivity main){
        this.main = main;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: Inside onsensor");
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            luxValue = event.values[0];
            Log.d(TAG, "onSensorChanged: " + luxValue);
            if(luxValue > 0 && luxValue < 100){
                main.changeScreenBrightness(1/luxValue);
                main.setFlashlightEnabled(true);
            }else if(luxValue > 1000) {
                main.setFlashlightEnabled(false);
                main.changeScreenBrightness(255);
            }else if(luxValue > 2000) {
                main.changeScreenBrightness(10);
                main.setFlashlightEnabled(false);
            }
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            distanceFromPhone = event.values[0];
            if(distanceFromPhone < 1.0 && !main.getFlashlightStatus())
                main.turnOnFlashlight();
            else
                main.turnOffFlashlight();
            Log.d(TAG, "onSensorChanged: Distance = " + distanceFromPhone);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
