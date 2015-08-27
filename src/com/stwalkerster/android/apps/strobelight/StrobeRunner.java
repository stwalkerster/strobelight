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
	public volatile int delay = 40;
	public volatile int delayoff = 40;
	public volatile StrobeLightConfig controller;
	public volatile String errorMessage = "";

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
    		catch(RuntimeException ex)
    		{
    			requestStop = true;
    			errorMessage = "Error setting camera flash status. Your device may be unsupported.";
    		}
    	}
    	
    	cam.release();
    	
    	isRunning = false;
    	requestStop=false;
    	
    	controller.mHandler.post(controller.mShowToastRunnable);
    }

}
