package com.hilifes.record;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LameWrapper {
	
	static {
	    System.loadLibrary("mp3lame");
	  }
	
	public static native String getVersion();
	public static native boolean Init(int channel,int samplerate,int brate,int mode,int quality);
	public static native byte[] EncodeByte(byte[] buffer,int size);
	public static native byte[] EncodeShort(short[] buffer,int size);
	public static native byte[] Flush();
	public static native void	 Close();
	
	
	public static void EncodeFile(String filename) throws IOException
	{
		FileInputStream is = new FileInputStream(filename);
		FileOutputStream os = new FileOutputStream(filename+".mp3");
		
		byte [] buffer = new byte[4096];
		byte [] res=null;
		Init(1,44100,16,3,5);
		
		int size=0;
		
		while(true)
		{
			size=is.read(buffer, 0, 4096);
			if(size==-1)
				break;
			
			res=EncodeByte(buffer,size);
			os.write(res);
		}
		
		res=Flush();
		os.write(res);
		Close();		
	}
}
