package com.hilifes.record;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioRecord;
import android.util.Log;

import com.hilifes.Utils;

public class LowLevelAudioRecorder implements IRecorder {
	private boolean recording=false;
	private boolean paused=false;

	private AudioRecord audio=null;
	private final static int BUFFER_SIZE=4096*7;
	private int amplitude = 100;
	private long size =0;



	private final static String TAG=LowLevelAudioRecorder.class.toString();

	private String filename;

	@Override
	public void pause()
	{
		paused=true;
	}

	@Override
	public void resume()
	{
		paused=false;
	}

	@Override
	public void start(String filename) throws IOException
	{
		if(recording)
			return;

		this.filename=filename;

		paused=false;

		Log.d(TAG,"Min BufferSize = "+AudioRecord.getMinBufferSize(GlobalParameters.audioFrequency,GlobalParameters.audioChannelConfig, GlobalParameters.audioFormat	));
		Log.d(TAG,"new AudioRecord ("+GlobalParameters.audioSource+","+GlobalParameters.audioFrequency+","+GlobalParameters.audioChannelConfig+","+GlobalParameters.audioFormat+");");
		
		if(audio!=null){
			audio.stop();
			audio.release();
			audio=null;
		}
			
		try {
			audio = new AudioRecord(GlobalParameters.audioSource, GlobalParameters.audioFrequency, GlobalParameters.audioChannelConfig, GlobalParameters.audioFormat,BUFFER_SIZE);

			if (audio.getState() != AudioRecord.STATE_INITIALIZED){
				Log.e(TAG,"Error: Bad parameters!");
				//Todo notify user!
				return;
			}
		}
		catch(Exception e){
			Log.e(TAG,"Exception: Cannot initialise AudioRecorder!");
			return;
		}

		audio.startRecording();

		Thread recordingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				writeData();
			}
		},"AudioRecorder Thread");

		recordingThread.start();

		recording=true;
	}
	public static short max(short[] values) {
		short max = Short.MIN_VALUE;
		for(short value : values) {
			if(value > max)
				max = value;
		}
		return max;
	}

	public void writeData()
	{
		short [] temp = new short[BUFFER_SIZE];
		byte [] mp3_buff=null;

		Log.d(TAG,"LameWrapper.Init("+GlobalParameters.mp3NumChannel+","+GlobalParameters.mp3SampleRate+","+GlobalParameters.mp3BitRate+","+GlobalParameters.mp3Quality+","+GlobalParameters.mp3Mode+");");
		if(LameWrapper.Init(GlobalParameters.mp3NumChannel,GlobalParameters.mp3SampleRate,GlobalParameters.mp3BitRate,GlobalParameters.mp3Quality,GlobalParameters.mp3Mode)==false) //1 channel 44100 48Bps mode Mono quality 3
			Log.d(TAG, "Error calling Init");

		FileOutputStream os = null;

		if(filename==null)
			filename=Utils.getOutputMediaFilePathName("REC", ".mp3");

		File file=new File(filename);
		GlobalParameters.filename=file.getName();

		try {
			os=new FileOutputStream(filename,true); //append if filename is specified!
		} catch (FileNotFoundException e) {
			Log.d(TAG, "Cannot open mp3 file:"+e.getMessage());
			return;
		}

		try {

			this.size=os.getChannel().size();
		} catch (IOException e1) {
			this.size=0;
		}

		while(recording){
			int size=audio.read(temp, 0,BUFFER_SIZE);

			if(AudioRecord.ERROR_INVALID_OPERATION != size && size != 0)
			{
				if(!paused)
				{
					amplitude=max(temp);

					// Send to Lame for MP3 compression
					mp3_buff = LameWrapper.EncodeShort(temp, size);
					try {
						// WRITE data to file
						os.write(mp3_buff);
						this.size+=mp3_buff.length;

					} catch (IOException e) {
						Log.d(TAG,"write error:"+e.getMessage());
					}
				}
			}
		}

		//Finalize MP3 and write file
		mp3_buff=LameWrapper.Flush();
		try {
			os.write(mp3_buff);
		} catch (IOException e) {
			Log.d(TAG,"write error:"+e.getMessage());
		}
		LameWrapper.Close();
	}


	@Override
	public void stop() throws IOException
	{
		if(!recording)
			return;

		audio.stop();
		audio.release();
		audio=null;
		recording=false;
	}

	@Override
	public int getMaxAmplitude(){
		return amplitude;
	}

	@Override
	public boolean getRecordingState() {
		return recording;
	}

	@Override
	public boolean getPauseState() {
		return paused;
	}

	@Override
	public long getFileSize() {
		return size;
	}
}
