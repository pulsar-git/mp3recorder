package com.hilifes.record;

import java.io.IOException;

import com.hilifes.Utils;

import android.media.MediaRecorder;

public class AudioRecorder implements IRecorder {

	private boolean recording = false;
	public final MediaRecorder recorder = new MediaRecorder();
	private boolean paused=false;
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

	/* (non-Javadoc)
	 * @see com.hilifes.hirecord.IRecorder#start()
	 */
	@Override
	public void start(String filename) throws IOException {
		if(!recording)
		{
			this.filename=filename;
			paused=false;
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			if(filename==null)
				recorder.setOutputFile(Utils.getOutputMediaFilePathName("REC", ".3GP"));
			else
				recorder.setOutputFile(filename);
			recorder.prepare();
			recorder.start();
			recording=true;
		}
	}

	/* (non-Javadoc)
	 * @see com.hilifes.hirecord.IRecorder#stop()
	 */
	@Override
	public void stop() throws IOException {
		if(recording)
		{
			recorder.stop();
			recorder.release();
			recording=false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.hilifes.hirecord.IRecorder#getMaxAmplitude()
	 */
	@Override
	public int getMaxAmplitude()
	{
		return recorder.getMaxAmplitude();
	}

	@Override
	public boolean getRecordingState() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getPauseState() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getFileSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}