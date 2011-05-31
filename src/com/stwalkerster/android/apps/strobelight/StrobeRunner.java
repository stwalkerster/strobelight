package com.stwalkerster.android.apps.strobelight;

import android.hardware.Camera;

public class StrobeRunner implements Runnable {

	public volatile boolean requestStop = false;
	public volatile boolean isRunning = false;
	
	
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
        		Thread.sleep(500);
        		cam.setParameters(poff);
        		Thread.sleep(500);
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
