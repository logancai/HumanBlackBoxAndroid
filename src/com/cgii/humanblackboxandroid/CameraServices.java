package com.cgii.humanblackboxandroid;

//https://www.youtube.com/watch?v=ZScE1aXS1Rs

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class CameraServices extends Activity implements OnInfoListener, SurfaceHolder.Callback, OnErrorListener{
	/** TextView*/
    TextView textView;
    
    private VideoView videoView = null;
    private SurfaceHolder holder = null;
	
	/** MediaRecorder*/
    private MediaRecorder recorder = null;
    private long recordingDuration = 15000; // 15 seconds
    private Camera camera = null;
    
    int count = 0;
	
	public CameraServices (){
		//Do nothing
	}
	
	public void launchCameraService(){
//		Toast.makeText(this, "Launched Camera", Toast.LENGTH_SHORT).show();
		Log.v(MainActivity.TAG, "CameraServices created");
		if (!initCamera()){
			finish();
		}
		
//		videoView = (VideoView) findViewById(R.id.videoView1);
		
//		initRecorder();
//		beginRecording();
	}
	
	private void beginRecording() {
		recorder.setOnInfoListener(this);
		recorder.setOnErrorListener(this);
		recorder.start();
		
	}

	private void releaseCamera(){
		if (camera != null){
			try{
				camera.reconnect();
			}
			catch (IOException e){
				e.printStackTrace();
			}
			camera.release();
			camera = null;
		}
	}
	private void  releaseRecorder(){
		if(recorder != null){
			recorder.release();
			recorder = null;
		}
	}
	
	private void initRecorder() {
		if (recorder != null) return;
		
		//setup information about recording
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String date = today.year + "_" + (today.month+1) + "_" + today.monthDay + "_" + 
				today.hour + ":" + today.minute + ":" + today.second;
		File path = Environment.getExternalStorageDirectory(); //Returns something like "/mnt/sdcard"
		String pathToSDCard = path.toString();
		String filePath = pathToSDCard + "/DCIM/Camera/" + date + ".mp4";
		
		try{
			camera.stopPreview();
			camera.unlock();
			
			recorder = new MediaRecorder();
			recorder.setCamera(camera);
			
			recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			recorder.setVideoFrameRate(30); //we could change that later
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setMaxDuration((int)recordingDuration);
			recorder.setPreviewDisplay(holder.getSurface());
			recorder.setOutputFile(filePath);
			
			recorder.prepare();
			Log.v(MainActivity.TAG, "MediaRecorder initialized");
		}
		catch(Exception e){
			Log.v(MainActivity.TAG, "MediaRecorder failed to initialize");
			e.printStackTrace();
		}
	}

	private boolean initCamera(){
		try{
			camera = Camera.open();
			Camera.Parameters camParams = camera.getParameters();
			camera.lock();
			holder = videoView.getHolder();
			holder.addCallback(this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		catch(RuntimeException re){
			Toast.makeText(this, "Camera could not initialize", Toast.LENGTH_SHORT).show();
			Log.v(MainActivity.TAG, "Could not initialize the camera");
			re.printStackTrace();
			return false;
		}
		System.out.println(true);
		Toast.makeText(this, "Camera initialized", Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		Log.i(MainActivity.TAG, "got a recording event");
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
			Log.i(MainActivity.TAG, "max duration reached");
			stopRecording();
			
		}
	}

	private void stopRecording() {
		if (recorder != null){
			recorder.setOnErrorListener(null);
			recorder.setOnInfoListener(null);
			try{
				recorder.stop();
			}
			catch(IllegalStateException e){
				Log.e(MainActivity.TAG, "IllegalStateEcep in stopRecording");
			}
			releaseRecorder();
			releaseCamera();
		}
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		Log.e(MainActivity.TAG, "got a recording error");
		stopRecording();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(MainActivity.TAG, "surfaceCreated");
		try{
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		}
		catch (IOException e){
			Log.v(MainActivity.TAG, "Could not start the preview");
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}
