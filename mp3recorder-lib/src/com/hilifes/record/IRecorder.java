package com.hilifes.record;

import java.io.IOException;

public interface IRecorder {

	/**
	 * Starts a new recording.
	 */
	public abstract void start(String filename) throws IOException;
	
	public abstract void pause();
	public abstract void resume();

	/**
	 * Stops a recording that has been previously started.
	 */
	public abstract void stop() throws IOException;

	public abstract int getMaxAmplitude();
	
	public abstract boolean getRecordingState();
	public abstract boolean getPauseState();
	public abstract long	getFileSize();
}