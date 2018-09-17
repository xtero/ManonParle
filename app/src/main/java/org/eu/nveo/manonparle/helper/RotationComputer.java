package org.eu.nveo.manonparle.helper;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public abstract class RotationComputer implements SensorEventListener {

    private SensorManager mSensorManager;

    private final String tag = "RotationComputer";

    private float[] mGravity;


    public abstract void onUpdate( float rotation );

    public RotationComputer( SensorManager sensorManager ){
        mSensorManager = sensorManager;
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
            float ax = mGravity[0];
            float ay = mGravity[1];
            float z = (float) ( ( Math.atan2( ax, ay ) * 180 ) / Math.PI );
            Log.v(tag, Float.toString(z) );
            onUpdate( z );
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stop(){
        mSensorManager.unregisterListener( this );

    }
}
