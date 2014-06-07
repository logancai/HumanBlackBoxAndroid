/*
 * What needs to be done
 * 
 * [x]Get ACCELEROMETER
 * [] Get other sensors
 * [x] Create background service
 * [] Math of when the camera is invoked
 * [] Create camera and store
 * [] Code to draw the stuff on the screen of last invoked
 */

package com.cgii.humanblackboxandroid;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    
	public static String TAG = "com.cgii.humanblackbox";
	
	public static SensorServices mSensorServices;
	public static SensorEvent mSensorEvent;
	public static Thread mAsyncCalculation;
	public static Handler mHandler;
	private static boolean isRecording;
	private static Activity mActivity;
	public static String phoneNumber;
	public static String address;
	
	public final static int recordingTimeInSeconds = 15;
	public final static long recordingTimeInMilSec = recordingTimeInSeconds * 1000;
	public final static long REFRESH_RATE_FPS = 45;
	public final static long DELAY = 1/45*1000; //in milliseconds
	public static final int TAKE_VIDEO_REQUEST = 1;
	
    /** Layout stuff*/
    public static TextView textView = null;
	public static TextView countView = null;
	public static TextView addressView = null;
	public static EditText editText = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		mActivity = this;
		textView = (TextView) findViewById(R.id.debugTextView);
		addressView = (TextView) findViewById(R.id.currentAddress);
		editText = (EditText) findViewById(R.id.phoneNumber);
		editText.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//Do nothing
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				phoneNumber = s.toString();
				Log.v(TAG, phoneNumber);
			}

			@Override
			public void afterTextChanged(Editable s) {
				//Do nothing
			}
			
		});
		//Begin Camera services
		Log.v(MainActivity.TAG, "Main onCreate Called");
		
		/*
		 * What is Handler?
		 * Handler communicates between the async thread and this thread.
		 * The thread is not allowed to directly touch the user interface
		 * so we must use this if we are using a thread.
		 */
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				boolean isRecording = msg.getData().getBoolean("RECORDING");
				boolean requestUpdate = msg.getData().getBoolean("UPDATE");
				if(isRecording){
					calledCamera();
				}
				if (requestUpdate){
					debugSensorSetText();
				}
			}
		};
		Intent intent = new Intent(this, Services.class);
		startService(intent);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.v(MainActivity.TAG, "Main onStart Called");
		if (!(mSensorServices == null)){
			mSensorServices.start();
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.v(MainActivity.TAG, "Main onStop Called");
//		mSensorServices.stop(); //Need to remove later
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void stopServices(View view){
		mSensorServices.stop();
	}
	public void restartServices(View view){
		mSensorServices.stop();
		mSensorServices.start();
	}
	public void launchCamera(View view){
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, TAKE_VIDEO_REQUEST);
	}
	public void updateValues(View view){
		if (mSensorEvent != null){
			MainActivity.textView.setText("X: "+ mSensorEvent.values[0] +
					"\nY: "+ mSensorEvent.values[1] +
					"\nX: "+ mSensorEvent.values[2]);
		}
		else{
			MainActivity.textView.setText("Sensor is null");
		}
	}
	
	public static void changeTextInTextView(String text){
		MainActivity.textView.setText(text);
	}
	public static SensorEvent getSensorEvent(){
		return mSensorEvent;
	}
	public static long getDelay(){
		return DELAY;
	}
	public static Handler getHandler(){
		return mHandler;
	}
	public static boolean getRecodringStatus(){
		return isRecording;
	}
	public static boolean setRecodringStatus(boolean a){
		isRecording = a;
		return isRecording;
	}
	public static void debugSensorSetText(){
		MainActivity.textView.setText("X: "+ mSensorEvent.values[0] +
				"\nY: "+ mSensorEvent.values[1] +
				"\nZ: "+ mSensorEvent.values[2]);
	}
	public void calledCamera(){
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, TAKE_VIDEO_REQUEST);
		isRecording = false;
	}
	
	public static Handler locationHandler = new Handler(){
    	@Override
		public void handleMessage(Message msg){
    		Log.v(Services.TAG, "location handler called");
    		findAddress(MainActivity.mActivity);
    	}
    };
	
    public static void findAddress(Activity activity){
    	List<Address> addressses = null;
		try 
		{
			Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
			addressses = geocoder.getFromLocation(Services.mLocation.getLatitude(), Services.mLocation.getLongitude(), 1);
		}
		catch (IOException e){
			e.printStackTrace();
			Log.v(Services.TAG, "SensorServices unable to get location");
		}
		catch (Exception e){
			e.printStackTrace();
			Log.v(Services.TAG, "SensorServices an exception occured");
		}
		if (addressses != null){
			String address = addressses.get(0).getAddressLine(0);
//			Services.city = addressses.get(0).getAddressLine(1);
//			Services.country = addressses.get(0).getAddressLine(2);
			String zipCode = addressses.get(0).getPostalCode();
			MainActivity.address = address + " " + zipCode;
			MainActivity.addressView.setText(address + " " + zipCode);
		}
		else{
			Log.e(Services.TAG, "Address is null");
		}
    }
    
    public void sendAddressMsg(View view){
		SmsManager.getDefault().sendTextMessage(phoneNumber, null, address, null,null);
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e(Services.TAG, "onActivityResult Called");
		if (requestCode == TAKE_VIDEO_REQUEST) {
	        isRecording = false;
	        Log.e(Services.TAG, "onActivityResult takeVideoRequest");
	    }
	}
	
}
