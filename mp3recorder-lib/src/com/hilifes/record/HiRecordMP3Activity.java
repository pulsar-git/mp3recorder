package com.hilifes.record;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hilifes.R;

public class HiRecordMP3Activity extends Activity {

	public HiRecordMP3Activity()
	{
		GlobalParameters.serviceClass=HiRecordService.class;
		GlobalParameters.activityClass=HiRecordMP3Activity.class;
	}
	
	/** Called when the activity is first created. */
	private ImageButton recButton;
	private ImageButton stopButton;
	private ImageButton pauseButton;
	private ImageButton prefButton;
	private ImageButton filesButton;
	private TextView	lblFileInfo;

	private HistogramView mView;

	protected final String TAG = HiRecordMP3Activity.class.toString();
	
	private boolean paused=false;
	public boolean recording=false;
	
	
	
	private void	setTextLabel(long size)
	{
		if(recording)
			if(paused)
				lblFileInfo.setText(getString(R.string.state_pause)+": "+GlobalParameters.filename+" | "+size/1024+"KB");
			else
				lblFileInfo.setText(getString(R.string.state_rec)+": "+GlobalParameters.filename+" | "+size/1024+"KB");
		else
			lblFileInfo.setText(getString(R.string.state_rdy));
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent.getAction().equals(HiRecordService.UPDATE_SERVICE_STATE))
			{
				int point = intent.getIntExtra(HiRecordService.PARAM_AMPLITUDE, 0);
				mView.addPoint(point);
				setTextLabel(intent.getLongExtra(HiRecordService.PARAM_SIZE, 0));
				return;
			}
			
			if(intent.getAction().equals(HiRecordService.UPDATE_STATE))
			{
				paused = intent.getBooleanExtra(HiRecordService.PAUSE_STATE, false);
				recording = intent.getBooleanExtra(HiRecordService.RECORD_STATE, false);
				if(!recording)
				{
					setTextLabel(0);
				}
				setButton();
			}
		}
	}; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		recButton = (ImageButton) findViewById(R.id.imageButtonRec);
		pauseButton = (ImageButton) findViewById(R.id.imageButtonPause);
		stopButton = (ImageButton) findViewById(R.id.imageButtonStop);
		prefButton = (ImageButton) findViewById(R.id.imageButton2);
		filesButton = (ImageButton) findViewById(R.id.imageButton1);
		
		lblFileInfo = (TextView ) findViewById(R.id.lblfileInfo);
		
		mView = (HistogramView) findViewById(R.id.viewHist);
		
		lblFileInfo.setText("");


		recButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnRecord();
			}
		});

		stopButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnStop();
			}
		});
		
		pauseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btnPause();
			}
		});
		
		prefButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btnPreferences();				
			}
		});
		
		filesButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				actionFileList();
			}
		});

		// Initialize default values!
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		GlobalParameters.loadParams(getApplicationContext());

		
		IntentFilter filter = new IntentFilter(HiRecordService.UPDATE_SERVICE_STATE);
		filter.addAction(HiRecordService.UPDATE_STATE);
		registerReceiver(mMessageReceiver, filter);
		
		Intent intent = new Intent(this, GlobalParameters.serviceClass);
		intent.setAction(HiRecordService.ACTION_GET_STATE);
		startService(intent);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}
	
	private void setButton(){
		recButton.setEnabled(!recording);
		stopButton.setEnabled(recording);
		pauseButton.setEnabled(recording);
		if(paused && recording)
			pauseButton.setImageResource(R.drawable.resume_btn);
		else
			pauseButton.setImageResource(R.drawable.pause_btn);
	}
	
	private void btnRecord(){
		if(!recording)
		{
			mView.clear();
			Intent intent = new Intent(this, GlobalParameters.serviceClass);
			intent.setAction(HiRecordService.ACTION_RECORD);
			startService(intent);
		}
	}

	protected void btnStop(){
		if(recording)
		{
			Intent intent = new Intent(this, GlobalParameters.serviceClass);
			intent.setAction(HiRecordService.ACTION_STOP_RECORD);
			startService(intent);
		}
	}

	
	protected void btnPause()
	{
		if(recording)
		{
			Intent intent = new Intent(this,GlobalParameters.serviceClass);
			if(paused)
			{
				intent.setAction(HiRecordService.ACTION_RESUME_RECORD);
			}
			else
			{
				intent.setAction(HiRecordService.ACTION_PAUSE_RECORD);			
			}
			startService(intent);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Intent intent = new Intent(this, GlobalParameters.serviceClass);
		intent.setAction(HiRecordService.ACTION_STOP_UPDATE);
		startService(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = new Intent(this, GlobalParameters.serviceClass);
		intent.setAction(HiRecordService.ACTION_START_UPDATE);
		startService(intent);
		
		intent = new Intent(this, GlobalParameters.serviceClass);
		intent.setAction(HiRecordService.ACTION_GET_STATE);
		startService(intent);
	}
	
	private void btnPreferences()
	{
		//Show PreferenceActivity
		//Intent myIntent = new Intent(this, HiRecordPrefActivity.class);
		Intent myIntent = new Intent(this, HiRecordPreferenceActivity.class);
		startActivity(myIntent);
	}
	
	private void actionFileList()
	{
		Intent myIntent = new Intent(this, HiRecordFileActivity.class);
		startActivity(myIntent);
	}
	
	

}