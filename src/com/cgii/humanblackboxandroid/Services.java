package com.cgii.humanblackboxandroid;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

public class Services extends Service {
	
	public static String TAG = "com.cgii.humanblackboxandroid";
	public static LocationManager mLocationManager;
	public static SensorManager mSensorManager;
	public static Location mLocation;
	public static float speed;
	public static float lastHeading;
	public static float mHeading;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate(){
		if (mLocationManager == null){
			mLocationManager = 
					(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		MainActivity.mSensorServices = new SensorServices(mSensorManager);
		MainActivity.mSensorServices.start();
		
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.print("onStartCommand called");
		return START_STICKY;
    }
	
	@Override
	public void onDestroy(){
		MainActivity.mSensorServices.stop();
		
	}
	
	
}
