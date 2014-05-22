package com.cgii.humanblackboxandroid;

import java.util.Date;

import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;


public class AsyncCalculation extends Thread{
	
	public static String TAG = "com.cgii.humanblackbox";
	
	private boolean isRecording;
	
	//Warning: you cannot edit the view. Only the original thread that created a view can touch its view.
	
	@Override
	public void run(){
		isRecording = false;
		try{
			Date before;
			Date now;
			while(SensorServices.getTracking() == true){
				before = new Date();
				now = new Date();
				if (MainActivity.getSensorEvent() != null){
//					String values = "Sensor is not null";
//					Log.v(TAG, values);
					
					//Math formula goes here
					SensorEvent event = MainActivity.getSensorEvent();
					double vector = Math.sqrt(event.values[0]*event.values[0]+
							event.values[1]*event.values[1]+
							event.values[2]*event.values[2]);
					
					if (vector > 20){
						isRecording = true;
						beginRecording();
					}
					
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
	
	private void beginRecording() {
		Message msgObj = MainActivity.getHandler().obtainMessage();
		Bundle b = new Bundle();
		b.putBoolean("RECORDING", isRecording);
		msgObj.setData(b);
		MainActivity.getHandler().sendMessage(msgObj);
    }
	
}
