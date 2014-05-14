package com.hilifes.hirecordmp3.free;

import com.hilifes.record.GlobalParameters;
import com.hilifes.record.HiRecordMP3Activity;

public class FreeActivity_noads extends HiRecordMP3Activity{
	
	protected final String TAG = FreeActivity.class.toString();
	
	public FreeActivity_noads(){
		GlobalParameters.serviceClass=HiRecordService.class;
		GlobalParameters.activityClass=FreeActivity_noads.class;
	}
}
