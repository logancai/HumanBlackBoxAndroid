package com.cgii.humanblackboxandroid;

import java.util.Date;

import android.content.Intent;
import android.util.Log;


public class AsyncCalculation extends Thread{
	
	public static String TAG = "com.cgii.humanblackbox";
	
	//Warning: you cannot edit the view. Only the original thread that created a view can touch its view.
	
	@Override
	public void run(){
		try{
			Date before;
			Date now;
			while(SensorServices.getTracking() == true){
				before = new Date();
				now = new Date();
				if (MainActivity.getSensorEvent() != null){
					//Math formula goes here
					
				}
				else{
					String values = "Sensor is null";
					Log.v(TAG, values);
				}
				while((now.getTime() - before.getTime()) < MainActivity.getDelay()){
					now = new Date();
				}
			}
		}
		catch (NullPointerException e){
			e.printStackTrace();
			Log.e(TAG, "An Nullpointer exception has occured.");
		}
		catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, "An exception has occured.");
		}
	}
	
}
