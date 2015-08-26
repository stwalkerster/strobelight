package com.stwalkerster.android.apps.strobelight;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class StrobeLightConfig extends Activity {
	
	Camera cam;
	StrobeRunner runner;
	Thread bw;
	private TextView textViewOn;
	private TextView textViewOff;

	public final Handler mHandler = new Handler();
	
    public final Runnable mShowToastRunnable = new Runnable() {
        public void run() {
            showMessage();
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.ToggleButtonStrobe);
		textViewOn = (TextView) findViewById(R.id.TextViewOn);
		textViewOff = (TextView) findViewById(R.id.TextViewOff);

        runner = StrobeRunner.getInstance();
        runner.controller = this;
        
        if(runner.isRunning)
        {	
        	
        }
        else
        {
        	try
        	{
        		
		        cam = Camera.open();
		        
		        if(cam==null)
		        {
		        	togglebutton.setEnabled(false);
		        	TextView t = (TextView)findViewById(R.id.TextViewOn);
		        	t.setText(R.string.nocamera);
		        	return;
		        }
		        
		        cam.release();
        	}
        	catch(RuntimeException ex)
        	{
	        	togglebutton.setEnabled(false);
	        	TextView t = (TextView)findViewById(R.id.TextViewOn);
	        	t.setText(R.string.nocamera);
	        	Toast.makeText(getApplicationContext(), "Error connecting to camera flash.", Toast.LENGTH_LONG).show();
	        	return;
        	}
        } 
        
        
        togglebutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks
				if (togglebutton.isChecked()) {
					bw = new Thread(runner);
					bw.start();
				} else {
					runner.requestStop = true;
				}
			}
		});
        
        final SeekBar skbar = (SeekBar)findViewById(R.id.SeekBarOn);
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
				runner.delay = progress;
				textViewOn.setText(getResources().getString(R.string.speed) + " (" + progress + " ms)");
			}
		});
		skbar.setProgress(runner.delay);
        
        final SeekBar skbaroff = (SeekBar)findViewById(R.id.SeekBarOff);
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
				textViewOff.setText(getResources().getString(R.string.speedoff) + " (" + progress + " ms)");
			}
		});
		skbaroff.setProgress(runner.delayoff);

        
    }

    @Override
    protected void onStop() {
    	runner.requestStop=true;
        ToggleButton togglebutton = (ToggleButton) findViewById(R.id.ToggleButtonStrobe);
        togglebutton.setChecked(false);
    	
    	super.onStop();
    }

    public void showMessage()
    {
    	String err = runner.errorMessage;
    	runner.errorMessage="";
    	if(!err.equals(""))
    	{
	    	Context context = getApplicationContext();
	    	int duration = Toast.LENGTH_SHORT;
	
	    	Toast toast = Toast.makeText(context, err, duration);
	    	toast.show();
    	}
    	
        ToggleButton togglebutton = (ToggleButton) findViewById(R.id.ToggleButtonStrobe);
        togglebutton.setChecked(false);
    }
}