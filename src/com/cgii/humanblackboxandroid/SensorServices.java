package com.cgii.humanblackboxandroid;

import java.util.Date;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorServices implements SensorEventListener{
	
	private final SensorManager mSensorManager;
	
	public static boolean mTracking;
	private boolean isRecording;
	
	public SensorServices(SensorManager sensorManager) {
		mSensorManager = sensorManager;
		mTracking = false;
		isRecording = false;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			
//			MainActivity.textView.setText("X: "+ event[0] +
//					"\nY: "+ event[1] +
//					"\nX: "+ event[2] +);
			
			MainActivity.mSensorEvent = event;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//Do nothing
	}
	
	public void start(){
		if (!mTracking) {
			mSensorManager.registerListener(this, 
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		mTracking = true;
		
		new AsyncCalculation().execute();
		
	}
	
	public void stop(){
		if (mTracking) {
			mSensorManager.unregisterListener(this);
			mTracking = false;
		}
	}
	
}
