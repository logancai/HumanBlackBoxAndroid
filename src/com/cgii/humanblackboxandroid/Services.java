package com.cgii.humanblackboxandroid;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Services extends Service implements 
							SensorEventListener,
							OnInfoListener, 
							OnErrorListener{

	/** The refresh rate, in frames per second, of the compass. */
    private static final int REFRESH_RATE_FPS = 45;
	
	/** The duration, in milliseconds, of one frame. */
    private static final long FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;
	
	/** Sensors*/
    private SensorManager mSensorManager;
    private Sensor mSensor;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    
    /** MediaRecorder*/
    private Date startTime;
    private boolean isRecording = false;
    private long recordingDuration = 15000; // 15 seconds
    
    /** TextView*/
    TextView textView;
    
    /** Video Views*/
    private SurfaceHolder holder = null;
    
    /** For math calculations*/
    private double X,Y,Z,mag;
	private double jerk;
	private double jerkx;
	private double jerky;
	private double jerkz;
	private int count = 0;
	private double time;
	private double now;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(){
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//Register Listener
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
		
//		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
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
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			float vector = (float) Math.sqrt(event.values[0]*event.values[0] 
					+ event.values[1]*event.values[1] 
					+ event.values[2]*event.values[2]);
			
			textView = MainActivity.textView;
			textView.setText("X: " + event.values[0] + 
					"\nY: " + event.values[1] + 
					"\nZ: " + event.values[2] +
					"\nVector: " + vector);
			
			if (vector > 15){
//				Toast.makeText(this, "Greater than 15", Toast.LENGTH_SHORT).show();
				beginRecording();
			}
			
//			count=1; 
//			/* Wont count always be 1 ? -Logan
//			 * When onSensorChanged get's called it is always going to set count = 1
//			*/
//			// Gives you your change in time from now to the last time 
//			Time late =new Time(Time.getCurrentTimezone());	
//			long latetime=late.toMillis(true);
//
//			if(count==1){ // for the first iteration when we dont have any now data 
//				time=latetime;
//				jerkx=X-X/time;
//				jerky=Y-Y/time;
//				jerkz=Z-Z/time;
//				jerk=0/time;
//				count++;
//			}
//			else{
//				 time=(latetime-now)/1000;
//				jerkx=(event.values[0]-X)/time;
//				jerky=(event.values[1]-Y)/time;
//				jerkz=(event.values[2]-Z)/time;
//				jerk=(vector-mag)/time;
//			}
//			now=latetime;// then set late to now
//
//			double g=9.8;
//			if (event.values[0] > 2*g|| event.values[1]>2*g||event.values[2]>2*g|| vector>2*g){
//				if(jerk<0||jerk>0){
//				beginRecording();
//				}
//			}
//
//			 X=event.values[0];
//			 Y=event.values[1];
//			 Z=event.values[2];
//			 mag=(double)vector;
		}
		
		
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
	/*
	 * Camera stuff
	 */
	private void beginRecording(){
		if (isRecording == false){
			Toast.makeText(this, "beginrecording == false", Toast.LENGTH_SHORT).show();
			isRecording = true;
			startTime = new Date();
			
			//Just for debugging
			count += 1;
			textView = MainActivity.countView;
			String num = Integer.toString(count);
			textView.setText(num);
			
			initRecorder();
			
		}
		else{
			
			Date now = new Date();
			long difference = now.getTime() - startTime.getTime();
			
			//If the difference of time from (NOW-START) > DURATION
			//reset is recording
			if (difference > recordingDuration){
				isRecording =false;
				beginRecording();
			}
		}
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		Log.i(MainActivity.TAG, "got a recording event");
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
			Log.i(MainActivity.TAG, "max duration reached");
			Toast.makeText(this, "max duration reached", Toast.LENGTH_SHORT).show();
			stopRecording();
			
		}
	}
	
	private void stopRecording() {
		if (MainActivity.recorder != null){
			MainActivity.recorder.setOnErrorListener(null);
			MainActivity.recorder.setOnInfoListener(null);
			try{
				MainActivity.recorder.stop();
			}
			catch(IllegalStateException e){
				Log.e(MainActivity.TAG, "IllegalStateEcep in stopRecording");
			}
			releaseRecorder();
			releaseCamera();
		}
	}
	
	private void releaseCamera(){
		if (MainActivity.camera != null){
			try{
				MainActivity.camera.reconnect();
			}
			catch (IOException e){
				e.printStackTrace();
			}
			MainActivity.camera.release();
			MainActivity.camera = null;
		}
	}
	private void  releaseRecorder(){
		if(MainActivity.recorder != null){
			MainActivity.recorder.release();
			MainActivity.recorder = null;
		}
	}
	
	private void initRecorder() {
//		if (recorder != null) {
//			return;
//		}
		
		//setup information about recording
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String date = today.year + "_" + (today.month+1) + "_" + today.monthDay + "_" + 
				today.hour + ":" + today.minute + ":" + today.second;
		File path = Environment.getExternalStorageDirectory(); //Returns something like "/mnt/sdcard"
		String pathToSDCard = path.toString();
		String filePath = pathToSDCard + "/DCIM/Camera/" + date + ".mp4";
		
		try{
			MainActivity.camera.stopPreview();
			MainActivity.camera.unlock();
			
			MainActivity.recorder = new MediaRecorder();
			MainActivity.recorder.setCamera(MainActivity.camera);
			
			MainActivity.recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			MainActivity.recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			MainActivity.recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//			MainActivity.recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
//			CamcorderProfile cam = new CamcorderProfile();
//			MainActivity.recorder.setProfile();
			MainActivity.recorder.setVideoFrameRate(30); //we could change that later
			MainActivity.recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			MainActivity.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			MainActivity.recorder.setMaxDuration((int)recordingDuration);
			MainActivity.recorder.setPreviewDisplay(MainActivity.holder.getSurface());
			MainActivity.recorder.setOutputFile(filePath);
			
			MainActivity.recorder.prepare();
			Log.v(MainActivity.TAG, "MediaRecorder initialized");
			
			MainActivity.recorder.start();
		}
		catch(Exception e){
			Log.v(MainActivity.TAG, "MediaRecorder failed to initialize");
			e.printStackTrace();
		}
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		Log.e(MainActivity.TAG, "got a recording error");
		stopRecording();
	}
	
	
}
