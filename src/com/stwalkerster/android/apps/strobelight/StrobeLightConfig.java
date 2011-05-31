package com.stwalkerster.android.apps.strobelight;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class StrobeLightConfig extends Activity {
	
	Camera cam;
	StrobeRunner runner;
	Thread bw;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.ToggleButton01);
        
        runner = StrobeRunner.getInstance();
        if(runner.isRunning)
        {	
        	
        }
        else
        {
	        cam = Camera.open();
	        
	        if(cam==null)
	        {
	        	togglebutton.setEnabled(false);
	        	TextView t = (TextView)findViewById(R.id.TextView01);
	        	t.setText(R.string.nocamera);
	        	return;
	        }
	        
	        cam.release();
        } 
        
        
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
        
        final SeekBar skbar = (SeekBar)findViewById(R.id.SeekBar01);
        skbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				runner.delay=progress;
				
			}
		});
        
        final SeekBar skbaroff = (SeekBar)findViewById(R.id.SeekBar02);
        skbaroff.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				runner.delayoff=progress;
				
			}
		});

        
    }

    @Override
    protected void onStop() {
    	runner.requestStop=true;
        ToggleButton togglebutton = (ToggleButton) findViewById(R.id.ToggleButton01);
        togglebutton.setChecked(false);
    	
    	super.onStop();
    }
}