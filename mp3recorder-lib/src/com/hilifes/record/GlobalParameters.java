package com.hilifes.record;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.preference.PreferenceManager;
import android.util.Log;

public class GlobalParameters {

	private static final String TAG = GlobalParameters.class.toString();

	public static int mp3NumChannel = 1;
	public static final int audioSourceDefault = MediaRecorder.AudioSource.MIC;
	public static int audioSource = MediaRecorder.AudioSource.MIC;
	public static int audioFrequency = 44100;
	public static int audioChannelConfig = AudioFormat.CHANNEL_IN_DEFAULT;
	public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	public static final int audioFormatDefault = AudioFormat.ENCODING_PCM_16BIT;

	public static int mp3SampleRate = 44100;
	public static int mp3BitRate = 48;
	public static final int mp3BitRateDefault = 48;
	public static int mp3Quality = 3;
	public static final int mp3QualityDefault = 3;
	public static int mp3Mode = 3;

	public static boolean adsDisabled;

	public static Class<?> activityClass = HiRecordService.class;
	public static Class<?> serviceClass = HiRecordMP3Activity.class;

	public static final String PARAM_MP3_BRATE = "MP3_BRATE";
	public static final String PARAM_MP3_SAMPLE_RATE = "MP3_SAMPLE_BITRATE";
	public static final String PARAM_MP3_QUALITY = "MP3_QUALITY";

	public static final String PARAM_AUDIO_SOURCE = "AUDIO_SOURCE";
	public static final String PARAM_AUDIO_FORMAT = "AUDIO_FORMAT";
	public static final String PARAM_AUDIO_CONFIG = "AUDIO_CONFIG";
	public static final String PARAM_AUDIO_FREQ = "AUDIO_FREQ";
	public static final String PARAM_ADS_DISABLE_DATE = "ADS_DISABLED_DATE";
	public static String filename; // Current Filename

	public static Integer tryLoadParams(SharedPreferences settings,
			String param, int defValue) {
		int res = defValue;

		try {
			res = Integer.valueOf(settings.getString(param, ""));
		} catch (NumberFormatException e) {
			Log.d(GlobalParameters.class.toString(), "Error in loading params!");
		}
		return res;
	}

	public static void checkDate(SharedPreferences settings) {
		SharedPreferences.Editor editor = settings.edit();
		
		if (adsDisabled) {
			Long date;

			date = settings.getLong(PARAM_ADS_DISABLE_DATE, 0);

			Calendar now = Calendar.getInstance();
			if (date != 0) {
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(date);
				c.add(Calendar.DAY_OF_MONTH, 3);
				if (now.compareTo(c) == 1) { // now is after c
					Log.d(TAG, "Time is after: restauring ads");
					editor.putBoolean("adsDisabled", false);
					editor.remove(PARAM_ADS_DISABLE_DATE);
					adsDisabled=false;
				}else
				{
					Log.d(TAG, "Time is before: "+c.getTimeInMillis()+" vs "+now.getTimeInMillis());
				}
			} else {
				date = now.getTimeInMillis();
				Log.d(TAG, "Time is NULL: initiating to " + date);
				editor.putLong(PARAM_ADS_DISABLE_DATE, date);
			}
		} else {
			Log.d(TAG, "Removing"+PARAM_ADS_DISABLE_DATE);
			editor.remove(PARAM_ADS_DISABLE_DATE);
		}
		editor.commit();
	}

	public static void loadParams(Context ctx) {

		ctx = ctx.getApplicationContext();
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(ctx);

		mp3BitRate = tryLoadParams(settings, PARAM_MP3_BRATE, mp3BitRateDefault);
		mp3Quality = tryLoadParams(settings, PARAM_MP3_QUALITY,
				mp3QualityDefault);
		audioSource = tryLoadParams(settings, PARAM_AUDIO_SOURCE,
				audioSourceDefault);
		audioFormat = tryLoadParams(settings, PARAM_AUDIO_FORMAT,
				audioFormatDefault);
		adsDisabled = settings.getBoolean("adsDisabled", false);
		checkDate(settings);
	}
}
