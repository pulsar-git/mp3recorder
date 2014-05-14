package com.hilifes.hirecordmp3.free;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hilifes.Utils;
import com.hilifes.record.HiRecordMP3Activity;

public class LauncherActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent myIntent;
		if(Utils.isRegistered(getApplicationContext()))	{
			myIntent = new Intent(this, HiRecordMP3Activity.class);
		}
		else {
			myIntent = new Intent(this, FreeActivity.class);
		}
		startActivity(myIntent);
		finish();
	}
}
