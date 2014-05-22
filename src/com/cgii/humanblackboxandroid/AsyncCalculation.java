package com.cgii.humanblackboxandroid;

import android.os.AsyncTask;

public class AsyncCalculation extends AsyncTask<Void, Void, Void>{

	@Override
	protected Void doInBackground(Void... params) {
		do{
			if (MainActivity.mSensorEvent != null){
				MainActivity.textView.setText("X: "+ MainActivity.mSensorEvent.values[0] +
						"\nY: "+ MainActivity.mSensorEvent.values[1] +
						"\nX: "+ MainActivity.mSensorEvent.values[2]);
			}
			else{
				MainActivity.textView.setText("Sensor is null");
			}
		}while(SensorServices.mTracking == true);
		return null;
	}
	
}
