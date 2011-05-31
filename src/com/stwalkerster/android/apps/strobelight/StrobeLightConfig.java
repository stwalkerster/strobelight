package com.stwalkerster.android.apps.strobelight;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class StrobeLightConfig extends Activity {
	
	Camera cam;
	
	Thread bw;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.ToggleButton01);
        cam = Camera.open();
        
        if(cam==null)
        {
        	togglebutton.setEnabled(false);
        	TextView t = (TextView)findViewById(R.id.TextView01);
        	t.setText(R.string.nocamera);
        	return;
        }
        
        cam.release();
        
        final StrobeRunner runner =new StrobeRunner();
        
        
        
        togglebutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                if (togglebutton.isChecked()) {
                    Toast.makeText(StrobeLightConfig.this, "Checked", Toast.LENGTH_SHORT).show();
                    bw = new Thread(runner);
                    bw.start();
                } else {
                    Toast.makeText(StrobeLightConfig.this, "Not checked", Toast.LENGTH_SHORT).show();
                    runner.requestStop = true;
                    
                }
            }

        });
        
        
        


        
    }
}