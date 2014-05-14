package com.hilifes.record;

import com.hilifes.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HiRecordPreferenceActivity extends PreferenceActivity{

	@Override
	protected void onStop() {
		super.onStop();
		GlobalParameters.loadParams(getApplicationContext());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
