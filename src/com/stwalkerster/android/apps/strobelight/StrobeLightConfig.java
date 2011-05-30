package com.stwalkerster.android.apps.strobelight;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;

public class StrobeLightConfig extends Activity {
	
	Camera cam;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        cam = Camera.open();
        if(cam==null)
        {
        	//no camera;
        }
        else
        {
        	Camera.Parameters p = cam.getParameters();
        	p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        	cam.setParameters(p);
        }
    }
}