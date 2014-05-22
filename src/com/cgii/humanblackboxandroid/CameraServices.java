package com.cgii.humanblackboxandroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.cgii.humanblackboxandroid.MainActivity.PlaceholderFragment;

//https://www.youtube.com/watch?v=ZScE1aXS1Rs

public class CameraServices extends Activity {
	int recordingDuration = 15;
	
	public static final int TAKE_VIDEO_REQUEST = 1;
	private Uri cameraVideoURI;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		setContentView(R.layout.recordingview);
		MainActivity.setRecodringStatus(false);
	}
	
	public CameraServices(){
		
	}
	
	protected void OnActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {
			MainActivity.setRecodringStatus(false);
	        //The following line is for Google Glass only
			/*
			String picturePath = data.getStringExtra(
	                CameraManager.EXTRA_PICTURE_FILE_PATH);
	        */
			
//			String[] projection = 
//				   { MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE }; 
//			@SuppressWarnings("deprecation")
//			Cursor cursor = managedQuery(cameraVideoURI, projection, null, null, null); 
//			int column_index_data =
//			   cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA); 
//			int column_index_size =
//			   cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE); 
//			cursor.moveToFirst(); 
//			String recordedVideoFilePath = cursor.getString(column_index_data);
//			int recordedVideoFileSize = cursor.getInt(column_index_size);
	    }

	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void takeVideo(View View){
//		Time today = new Time(Time.getCurrentTimezone());
//		today.setToNow();
//		String date = today.year + "_" + (today.month + 1) + "_"
//				+ today.monthDay + "_" + today.hour + "h" + today.minute + "m"
//				+ today.second + "s";
//		String fileName = date + ".mp4";

		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//		ContentValues values = new ContentValues();
//		values.put(MediaStore.Video.Media.TITLE, fileName);
//		cameraVideoURI = getContentResolver().insert(
//				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingDuration);
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraVideoURI);
		startActivityForResult(intent, TAKE_VIDEO_REQUEST);
	}
}
