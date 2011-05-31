package com.stwalkerster.android.apps.strobelight;

import android.hardware.Camera;

public class StrobeRunner implements Runnable {

	protected StrobeRunner()
	{
		
	}
	
	public static StrobeRunner getInstance()
	{
		return ( instance == null ? instance = new StrobeRunner() : instance );
	}
	
	private static StrobeRunner instance;
	
	
	public volatile boolean requestStop = false;
	public volatile boolean isRunning = false;
	public volatile int delay = 10;
	public volatile int delayoff = 500;
	
    @Override
    public void run() {
    	if(isRunning)
    		return;
    	
    	requestStop=false;
    	isRunning = true;
    	
    	Camera cam = Camera.open();
    	
    	Camera.Parameters pon = cam.getParameters(), poff = cam.getParameters();
    	
    	pon.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
    	poff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
    	
    	while(!requestStop)
    	{
    		try{
        		cam.setParameters(pon);
        		Thread.sleep(delay);
        		cam.setParameters(poff);
        		Thread.sleep(delayoff);
    		}
    		catch(InterruptedException ex)
    		{
    			
    		}
    	}
    	
    	cam.release();
    	
    	isRunning = false;
    	requestStop=false;
    }

}
