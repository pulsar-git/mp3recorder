package com.hilifes.hirecordmp3.free;

import android.util.Log;
import android.widget.Toast;

import com.hilifes.record.GlobalParameters;
import com.hilifes.record.HiRecordService;

public class HiRecordServiceFree extends HiRecordService{
	

	private final static int TIME_LIMIT_MIN=10;
	private final static String TAG=HiRecordServiceFree.class.toString();
	
	public HiRecordServiceFree(){
		GlobalParameters.activityClass = FreeActivity.class;
	}

	private Runnable mFreeTimer = new Runnable() {
		@Override
		public void run() {
			pauseRecord();
			Toast.makeText(HiRecordServiceFree.this,"Unregistered version limited to "+TIME_LIMIT_MIN+" mn of recording", Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected void startRecord(String filename) {
		super.startRecord(filename);
		mHandler.postDelayed(mFreeTimer, 1000*60*TIME_LIMIT_MIN);
		// TODO remove me!
		Log.d(TAG,"start Record");
	}

	@Override
	protected void resumeRecord() {
		super.resumeRecord();
		mHandler.postDelayed(mFreeTimer, 1000*60*TIME_LIMIT_MIN);
	}
	
	@Override
	protected void stopRecord() {
		super.stopRecord();
		mHandler.removeCallbacks(mFreeTimer);
	}

	@Override
	protected void pauseRecord() {
		super.pauseRecord();
		mHandler.removeCallbacks(mFreeTimer);
	}
	
}
