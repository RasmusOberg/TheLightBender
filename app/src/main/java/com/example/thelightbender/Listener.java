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
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            luxValue = event.values[0];
            Log.d(TAG, "onSensorChanged: " + luxValue);
            float value;
            if(luxValue > 0 && luxValue < 1000){
                value = 1-(1/luxValue);
                main.changeScreenBrightness(value);
                main.setFlashlightEnabled(true);
            }else if(luxValue > 1000) {
                main.setFlashlightEnabled(false);
                value = 1-(1/luxValue);
                main.changeScreenBrightness(value);
                main.turnOffFlashlight();
            }else if(luxValue > 2000) {
                value = 1-(1/luxValue);
                main.changeScreenBrightness(value);
                main.setFlashlightEnabled(false);
                main.turnOffFlashlight();
            }
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            distanceFromPhone = event.values[0];
            if(distanceFromPhone < 1.0)
                main.turnOnFlashlight();
            else {
                main.setFlashlightEnabled(false);
                main.turnOffFlashlight();
            }
            Log.d(TAG, "onSensorChanged: Distance = " + distanceFromPhone);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
