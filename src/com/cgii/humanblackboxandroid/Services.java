package com.cgii.humanblackboxandroid;

import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.TextView;

public class Services extends Service implements SensorEventListener{

	/** The refresh rate, in frames per second, of the compass. */
    private static final int REFRESH_RATE_FPS = 45;
	
	/** The duration, in milliseconds, of one frame. */
    private static final long FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;
	
	/** Sensors*/
    private SensorManager mSensorManager;
    private Sensor mSensor;
    
    /** TextView*/
    TextView textView;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate(){
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//Register Listener
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.print("onStartCommand called");
		return START_STICKY;
    }
	
	/*
	 * onSensorChanged and onAccuracyChanged are implemented from
	 * SensorEventListener
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		float vector = (float) Math.sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1] + event.values[2]*event.values[2]);
		
		//!!!!!!!!
		//Put your math calls or methods here...
		//!!!!!
		
		textView = MainActivity.textView;
		textView.setText("X: " + event.values[0] + 
				"\nY: " + event.values[1] + 
				"\nZ: " + event.values[2] +
				"\nVector: " + vector);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
}
