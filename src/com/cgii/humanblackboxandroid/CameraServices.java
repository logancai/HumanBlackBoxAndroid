package com.cgii.humanblackboxandroid;

import java.io.File;

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
import android.widget.VideoView;

public class CameraServices extends Activity implements OnInfoListener, SurfaceHolder.Callback, OnErrorListener{
	/** TextView*/
    TextView textView;
    
    private VideoView videoView = null;
    private SurfaceHolder holder = null;
	
	/** MediaRecorder*/
    private MediaRecorder recorder = null;
    private long recordingDuration = 17000; // 15 seconds
    private Camera camera = null;
    
    int count = 0;
	
	public CameraServices (){
		
		if (!initCamera()){
			finish();
		}
		initRecorder();
		
		
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
		
		//Setup MediaRecorder
//		recorder = new MediaRecorder();
//		
//		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//	    recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
////								    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//		recorder.setOutputFile(filePath);
//	    CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//	    recorder.setProfile(cpHigh);
////								    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//	    recorder.setMaxDuration((int) recordingDuration);
//	    try {
//			recorder.prepare();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    recorder.start();
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
			Log.v(MainActivity.TAG, "Could not initialize the camera");
			re.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
			recorder.stop();
		}
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
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
