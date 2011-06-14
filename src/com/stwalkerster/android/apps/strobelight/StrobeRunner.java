package com.stwalkerster.android.apps.strobelight;

import android.content.Context;
import android.hardware.Camera;
import android.widget.Toast;

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
	public volatile Context context;
	
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
        		throw new RuntimeException();
    		}
    		catch(InterruptedException ex)
    		{
    			
    		}
    		catch(RuntimeException ex)
    		{
    			requestStop = true;
    			Toast t = Toast.makeText(context, "Error setting status of camera flash.", Toast.LENGTH_LONG);
    		}
    	}
    	
    	cam.release();
    	
    	isRunning = false;
    	requestStop=false;
    }

}
