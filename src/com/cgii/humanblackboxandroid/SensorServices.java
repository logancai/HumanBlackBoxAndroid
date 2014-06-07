package com.cgii.humanblackboxandroid;

import java.util.concurrent.TimeUnit;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SensorServices extends Services implements SensorEventListener{
	
//	private final SensorManager mSensorManager;
	
	public static boolean mTracking;
	
	/**
     * The sensors used by the compass are mounted in the movable arm on Glass. Depending on how
     * this arm is rotated, it may produce a displacement ranging anywhere from 0 to about 12
     * degrees. Since there is no way to know exactly how far the arm is rotated, we just split the
     * difference.
     */
    private static final int ARM_DISPLACEMENT_DEGREES = 6;
	
	/**
     * The maximum age of a location retrieved from the passive location provider before it is
     * considered too old to use when the compass first starts up.
     */
    private static final long MAX_LOCATION_AGE_MILLIS = TimeUnit.MINUTES.toMillis(30);
    
    /**
     * The minimum elapsed time desired between location notifications.
     */
    private static final long MILLIS_BETWEEN_LOCATIONS = TimeUnit.SECONDS.toMillis(3);
    
    /**
     * The minimum distance desired between location notifications.
     */
    private static final long METERS_BETWEEN_LOCATIONS = 2;
	
	public SensorServices(SensorManager sensorManager) {
		mSensorManager = sensorManager;
		mTracking = false;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
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
		Services.mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                5000*2);
		
		
//		Services.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location lastLocation = Services.mLocationManager
                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		
        if (lastLocation != null) {
            long locationAge = lastLocation.getTime() - System.currentTimeMillis();
            if (locationAge < MAX_LOCATION_AGE_MILLIS) {
                Services.mLocation = lastLocation;
            }
        }
        else{
        	Log.v(Services.TAG, "lastLocation is null");
        }

        Services.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
        		MILLIS_BETWEEN_LOCATIONS, METERS_BETWEEN_LOCATIONS, mLocationListener,
                Looper.getMainLooper());
		
		
		mTracking = true;
		
		MainActivity.mAsyncCalculation = new AsyncCalculation();
		MainActivity.mAsyncCalculation.start();
		
	}
	
	public void stop(){
		if (mTracking) {
			mSensorManager.unregisterListener(this);
			mTracking = false;
			MainActivity.mAsyncCalculation.interrupt();
		}
	}
	public static boolean getTracking(){
		return mTracking;
	}
	
	public static void getAddress(){
		Message msgObj = MainActivity.locationHandler.obtainMessage();
		MainActivity.locationHandler.sendMessage(msgObj);
	}
	
	LocationListener mLocationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			Services.mLocation = location;
			//geocoder will convert latitude/longitude to address
			Log.v(Services.TAG, "SensorServices onLocationChanged");
			Services.speed = location.getSpeed();
			getAddress();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("Latitude + Longitude","status");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d("Latitude + Longitude","enable");
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d("Latitude + Longitude","disable");
		}
		
	};
	
}
