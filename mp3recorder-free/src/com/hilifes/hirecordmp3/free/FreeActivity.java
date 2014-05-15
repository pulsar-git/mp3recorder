package com.hilifes.hirecordmp3.free;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hilifes.record.GlobalParameters;
import com.hilifes.record.HiRecordMP3Activity;
import com.hilifes.record.HiRecordService;

public class FreeActivity extends HiRecordMP3Activity {

	protected final String TAG = FreeActivity.class.toString();
	protected ProgressDialog pdialog=null;

	public FreeActivity(){
		GlobalParameters.serviceClass=HiRecordService.class; //Used to be HiRecordServiceFree.java
		GlobalParameters.activityClass=FreeActivity.class;
	}

	protected Handler mHandler = new Handler();	


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		if(!GlobalParameters.adsDisabled){
			LinearLayout l =(LinearLayout)findViewById(R.id.mainLayout);
			//l.addView(getLayoutInflater().inflate(R.layout.tapjoy, null),0);
			l.addView(getLayoutInflater().inflate(R.layout.ads, null),0);
		
			// Look up the AdView as a resource and load a request.*
			AdView adView = (AdView)this.findViewById(R.id.adView); 
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
		}
	}
}
