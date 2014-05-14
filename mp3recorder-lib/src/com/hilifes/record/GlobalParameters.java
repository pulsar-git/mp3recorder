package com.hilifes.record;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.preference.PreferenceManager;
import android.util.Log;

public class GlobalParameters {

	public static int mp3NumChannel=1;
	public static final int audioSourceDefault=MediaRecorder.AudioSource.MIC;
	public static int audioSource=MediaRecorder.AudioSource.MIC;
	public static int audioFrequency = 44100;
	public static int audioChannelConfig=AudioFormat.CHANNEL_IN_DEFAULT;
	public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	public static final int audioFormatDefault = AudioFormat.ENCODING_PCM_16BIT;

	public static int mp3SampleRate=44100;
	public static int mp3BitRate=48;
	public static final int mp3BitRateDefault=48;
	public static int mp3Quality=3;
	public static final int mp3QualityDefault=3;
	public static int mp3Mode=3;

	public static Class<?> activityClass=HiRecordService.class;
	public static Class<?> serviceClass = HiRecordMP3Activity.class;
	
	public static final String PARAM_MP3_BRATE		="MP3_BRATE";
	public static final String PARAM_MP3_SAMPLE_RATE="MP3_SAMPLE_BITRATE";
	public static final String PARAM_MP3_QUALITY	="MP3_QUALITY";
	
	public static final String PARAM_AUDIO_SOURCE	="AUDIO_SOURCE";
	public static final String PARAM_AUDIO_FORMAT	="AUDIO_FORMAT";
	public static final String PARAM_AUDIO_CONFIG	="AUDIO_CONFIG";
	public static final String PARAM_AUDIO_FREQ		="AUDIO_FREQ";
	public static String filename; // Current Filename


	public static Integer tryLoadParams(SharedPreferences settings,String param,int defValue){
		int res = defValue;

		try {
			res = Integer.valueOf(settings.getString(param,""));
		}
		catch (NumberFormatException e){
			Log.d(GlobalParameters.class.toString(),"Error in loading params!");
		}
		return res;
	}

	public static void loadParams(Context ctx){

		ctx=ctx.getApplicationContext();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
	
		mp3BitRate = tryLoadParams(settings,PARAM_MP3_BRATE, mp3BitRateDefault);
		mp3Quality = tryLoadParams(settings,PARAM_MP3_QUALITY, mp3QualityDefault);
		audioSource = tryLoadParams(settings,PARAM_AUDIO_SOURCE,audioSourceDefault);
		audioFormat = tryLoadParams(settings,PARAM_AUDIO_FORMAT,audioFormatDefault);
	}
}
