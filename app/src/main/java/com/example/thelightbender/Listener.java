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
            if(luxValue > 0 && luxValue < 100){
                main.changeScreenBrightness(1/luxValue);
            }else if(luxValue > 1000)
                main.changeScreenBrightness(255);
            else if(luxValue > 2000)
                main.changeScreenBrightness(10);
        }else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            distanceFromPhone = event.values[0];
            Log.d(TAG, "onSensorChanged: Distance = " + distanceFromPhone);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
