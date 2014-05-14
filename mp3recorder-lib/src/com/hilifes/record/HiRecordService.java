package com.hilifes.record;

import java.io.IOException;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.hilifes.R;

public class HiRecordService extends Service {
	private static final String TAG=HiRecordService.class.toString();
	public static final String ACTION_RECORD 		= "com.hilifes.hirecord.action.RECORD";
	public static final String ACTION_STOP_RECORD 	= "com.hilifes.hirecord.action.STOP_RECORD";
	public static final String ACTION_PAUSE_RECORD 	= "com.hilifes.hirecord.action.PAUSE_RECORD";
	public static final String ACTION_RESUME_RECORD = "com.hilifes.hirecord.action.RESUME_RECORD";

	public static final String ACTION_PLAY 			= "com.hilifes.hirecord.action.PLAY";
	public static final String ACTION_STOP_PLAY 	= "com.hilifes.hirecord.action.STOP_PLAY";
	public static final String ACTION_START_UPDATE 	= "com.hilifes.hirecord.action.START_UPDATE";
	public static final String ACTION_STOP_UPDATE 	= "com.hilifes.hirecord.action.STOP_UPDATE";

	public static final String ACTION_GET_STATE 	= "com.hilifes.hirecord.action.GET_STATE";

	//Do not broadcast amplitude message when not needed!!
	public static final String UPDATE_SERVICE_STATE 	= "com.hilifes.hirecord.action.UPDATE_AMPLITUDE";
	public static final String PARAM_AMPLITUDE 		= "PARAM_AMPLITUDE";
	public static final String PARAM_SIZE		 	= "PARAM_SIZE";
	public static final String PARAM_FILENAME 		= "PARAM_FILENAME";
	
	public static final String UPDATE_STATE 		= "com.hilifes.hirecord.action.UPDATE_STATE";
	public static final String RECORD_STATE			= "PLAY";
	public static final String PAUSE_STATE			= "PAUSE";
	

	private PowerManager.WakeLock wl=null;

	private static final int NOTIFICATION_ID = 1;

	protected Handler mHandler = new Handler();	
	private Runnable mUpdateTime = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(UPDATE_SERVICE_STATE);
			intent.putExtra(PARAM_AMPLITUDE, rec.getMaxAmplitude());
			intent.putExtra(PARAM_SIZE, rec.getFileSize());

			sendBroadcast(intent);
			mHandler.postDelayed(this, 200);
		}
	};
	
	public HiRecordService(){
		GlobalParameters.activityClass = HiRecordMP3Activity.class;
	}

	private LowLevelAudioRecorder rec = new LowLevelAudioRecorder();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onHandleIntent(intent);
		return START_STICKY;
	}



	protected void goForeground()
	{
		//GO FOREGROUND!
		//The intent to launch when the user clicks the expanded notification
		Intent intent_temp = new Intent(this, GlobalParameters.activityClass);
		intent_temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent_temp, 0);

		//This constructor is deprecated. Use Notification.Builder instead
		Notification notice = new Notification(R.drawable.ic_micro, "HiRecordMP3", System.currentTimeMillis());

		//This method is deprecated. Use Notification.Builder instead.
		notice.setLatestEventInfo(this, "HiRecordMP3", "Currently recording audio", pendIntent);

		notice.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(NOTIFICATION_ID, notice);
	}

	protected void startRecord(String filename)
	{
		try
		{
			rec.start(filename);
			startUpdate();
			wl = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HiRecordServiceMP3 WL");
			//Acquire PARTIAL_WAKE_LOCK in order to prevent CPU from shutting
			wl.acquire();

		} catch (IOException e) {
			Log.d(TAG,"Error:" +e.getMessage());
		}
		sendState();
	}
	
	protected void stopRecord()
	{
		try {
			rec.stop();
			stopUpdate();
			stopForeground(true);
			//Release PARTIAL_WAKE_LOCK lock
			if(wl!=null)
			{
				wl.release();
				wl=null; //If you dont put it to null, stopSelf() will destroy the service and that ll call another time release on it!
			}

			stopSelf();
		} catch (IOException e) {
			Log.d(TAG,"Error:" +e.getMessage());
		}
		sendState();
	}
	
	protected void pauseRecord()
	{
		stopUpdate();
		rec.pause();
		sendState();
		stopUpdate();
	}
	
	protected void resumeRecord()
	{
		rec.resume();
		sendState();
		startUpdate();
	}

	protected void onHandleIntent(Intent intent) {
		if(intent == null)
			return;

		if(intent.getAction().equals(ACTION_RECORD)){			
			goForeground();
			startRecord(intent.getStringExtra(PARAM_FILENAME));
			return;
		}

		if(intent.getAction().equals(ACTION_STOP_RECORD)){
			stopRecord();
			return;
		}

		if(intent.getAction().equals(ACTION_PAUSE_RECORD)){
			pauseRecord();
			return;
		}

		if(intent.getAction().equals(ACTION_RESUME_RECORD)){
			resumeRecord();
			return;
		}


		if(intent.getAction().equals(ACTION_START_UPDATE)){
			if(rec.getRecordingState() && !rec.getPauseState()) // If we are recording and !paused then start update!
			{
				startUpdate();
			}
			return;
		}

		if(intent.getAction().equals(ACTION_STOP_UPDATE)){
			stopUpdate();
			return;
		}

		if(intent.getAction().equals(ACTION_GET_STATE)){
			sendState();
			return;
		}
	}

	protected void 	sendState()
	{
		Intent sintent = new Intent(UPDATE_STATE);
		sintent.putExtra(RECORD_STATE, rec.getRecordingState());
		sintent.putExtra(PAUSE_STATE, rec.getPauseState());
		sendBroadcast(sintent);
	}

	public	void	startUpdate()
	{
		mUpdateTime.run();
	}

	public void 	stopUpdate()
	{
		mHandler.removeCallbacks(mUpdateTime);
	}

	@Override
	public void onDestroy(){
		try {
			rec.stop();
			if(wl!=null)
				wl.release();
		} catch (IOException e) {
			Log.d(TAG,"Error: " +e.getMessage());
		}
	}
}
