///*
// * What needs to be done
// * 
// * [x]Get ACCELEROMETER
// * [] Get other sensors
// * [x] Create background service
// * [] Math of when the camera is invoked
// * [] Create camera and store
// * [] Code to draw the stuff on the screen of last invoked
// */
//
//package com.cgii.humanblackboxandroid;
//
//import java.io.IOException;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.content.Intent;
//import android.hardware.Camera;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.VideoView;
//
//public class MainActivity extends Activity implements SurfaceHolder.Callback{
//    
//	public static String TAG = "com.cgii.humanblackbox";
//	
//	/** MediaRecorder Stuff*/
//	public static Camera camera = null;
//	public static MediaRecorder recorder = null;
//	
//    /** Layout stuff*/
//    public static TextView textView = null;
//	public static TextView countView = null;
//	
//	public static SurfaceHolder holder = null;
//    public static SurfaceView surfaceView = null;
//    public static VideoView videoView = null;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
//		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
//		
//		textView = (TextView) findViewById(R.id.debugTextView);
//		countView = (TextView) findViewById(R.id.count);
//		videoView = (VideoView) findViewById(R.id.videoView1);
//		
//		//Begin Camera services
//		Log.v(MainActivity.TAG, "CameraServices created");
//		
//		Intent intent = new Intent(this, Services.class);
//		startService(intent);
//		
//	}
//	
//	@Override
//	protected void onStart(){
//		super.onStart();
//		Log.v(MainActivity.TAG, "onResume");
//		if (!initCamera()){
//			finish();
//		}
//	}
//	
//	@Override
//	protected void onResume(){
//		super.onResume();
//	}
//	
//	@Override
//	protected void onDestroy(){
//		super.onDestroy();
//		releaseCamera();
//	}
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	/**
//	 * A placeholder fragment containing a simple view.
//	 */
//	public static class PlaceholderFragment extends Fragment {
//
//		public PlaceholderFragment() {
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_main, container,
//					false);
//			return rootView;
//		}
//	}
//	
//	@SuppressWarnings("deprecation")
//	boolean initCamera() {
//		try{
//			camera = Camera.open();
//			Camera.Parameters camParams = camera.getParameters();
//			/* Fix for Samsung Phones*/
//			camParams.set("cam_mode", 1);
//			camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
//			camera.setParameters(camParams);
//			
//			camera.lock();
//			holder = videoView.getHolder();
//			holder.addCallback(this);
//			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		}
//		catch(RuntimeException re){
//			Toast.makeText(this, "Camera could not initialize", Toast.LENGTH_SHORT).show();
//			Log.v(MainActivity.TAG, "Could not initialize the camera");
//			re.printStackTrace();
//			return false;
//		}
//		System.out.println(true);
//		Toast.makeText(this, "Camera initialized", Toast.LENGTH_SHORT).show();
//		return true;
//	}
//	
//	private void releaseCamera(){
//		if (camera != null){
//			try{
//				camera.reconnect();
//			}
//			catch (IOException e){
//				e.printStackTrace();
//			}
//			camera.release();
//			camera = null;
//		}
//	}
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		Log.v(MainActivity.TAG, "surfaceCreated");
//		try{
//			camera.setPreviewDisplay(holder);
//			camera.startPreview();
//		}
//		catch (IOException e){
//			Log.v(MainActivity.TAG, "Could not start the preview");
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width,
//			int height) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
