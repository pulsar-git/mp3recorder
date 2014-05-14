package com.hilifes;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class Utils {
	

	public static String getOutputMediaFilePath()
	{
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC), "HiRecordMP3");
      // This location works best if you want the created images to be shared
      // between applications and persist after your app has been uninstalled.

      // Create the storage directory if it does not exist
      if (! mediaStorageDir.exists()){
          if (! mediaStorageDir.mkdirs()){
              Log.d("Utils", "failed to create directory");
              return null;
          }
      }
      
      return mediaStorageDir.getPath();
	}
	
	public static String getOutputMediaFilePathName(String prefix,String suffix)
	{
      // Create a media file name
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      return getOutputMediaFilePath() + File.separator +prefix+"_"+ timeStamp + suffix;
	}
	
	public static File getOutputMediaFile(String prefix,String suffix){
        return  new File(getOutputMediaFilePathName(prefix,suffix));
    }
	
	
	public boolean testRoot()
	{
		Process p;

		try {
			p=Runtime.getRuntime().exec("su");

			DataOutputStream os = new DataOutputStream(p.getOutputStream());  

			// Close the terminal  
			os.writeBytes("exit\n");  
			os.flush();

			p.waitFor();
			if(p.exitValue() != 255)
			{
				return true;
			}
			else
			{
				return false;
			}

		} catch (IOException e) {
			
		} catch (InterruptedException e) {
			
		}
		return false;
	}
	
	
	private static final Class<?>[] mSetForegroundSignature = new Class[] {
	    boolean.class};
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
	    int.class, Notification.class};
	private static final Class<?>[] mStopForegroundSignature = new Class[] {
	    boolean.class};

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	void invokeMethod(Method method, Object[] args) {
	    try {
	        method.invoke(this, args);
	    } catch (InvocationTargetException e) {
	        // Should not happen.
	        Log.w("ApiDemos", "Unable to invoke method", e);
	    } catch (IllegalAccessException e) {
	        // Should not happen.
	        Log.w("ApiDemos", "Unable to invoke method", e);
	    }
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
	    // If we have the new startForeground API, then use it.
	    if (mStartForeground != null) {
	        mStartForegroundArgs[0] = Integer.valueOf(id);
	        mStartForegroundArgs[1] = notification;
	        invokeMethod(mStartForeground, mStartForegroundArgs);
	        return;
	    }

	    // Fall back on the old API.
	    mSetForegroundArgs[0] = Boolean.TRUE;
	    invokeMethod(mSetForeground, mSetForegroundArgs);
	    mNM.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
	    // If we have the new stopForeground API, then use it.
	    if (mStopForeground != null) {
	        mStopForegroundArgs[0] = Boolean.TRUE;
	        invokeMethod(mStopForeground, mStopForegroundArgs);
	        return;
	    }

	    // Fall back on the old API.  Note to cancel BEFORE changing the
	    // foreground state, since we could be killed at that point.
	    mNM.cancel(id);
	    mSetForegroundArgs[0] = Boolean.FALSE;
	    invokeMethod(mSetForeground, mSetForegroundArgs);
	}

	
	public void prepareForeground(Context ctx)
	{
		mNM = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
	    try {
	        mStartForeground = getClass().getMethod("startForeground",
	                mStartForegroundSignature);
	        mStopForeground = getClass().getMethod("stopForeground",
	                mStopForegroundSignature);
	        return;
	    } catch (NoSuchMethodException e) {
	        // Running on an older platform.
	        mStartForeground = mStopForeground = null;
	    }
	    try {
	        mSetForeground = getClass().getMethod("setForeground",
	                mSetForegroundSignature);
	    } catch (NoSuchMethodException e) {
	        throw new IllegalStateException(
	                "OS doesn't have Service.startForeground OR Service.setForeground!");
	    }

	}
	
	private final static String REG_FILE="RegisteredFile";
	private final static String REG_KEY="RegisteredKey";
	
	public static void registerApplication(Context ctx){
		SharedPreferences settings = ctx.getSharedPreferences(REG_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(REG_KEY, true);
		editor.commit();
	}

	public static boolean isRegistered(Context ctx)
	{
		SharedPreferences settings = ctx.getSharedPreferences(REG_FILE, 0);
		return settings.getBoolean(REG_KEY, false);
	}	

}
