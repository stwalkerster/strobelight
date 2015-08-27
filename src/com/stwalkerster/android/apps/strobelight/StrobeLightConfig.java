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
    private TextView textViewFreq;
    private int frequency;

	public final Handler mHandler = new Handler();
	
    public final Runnable mShowToastRunnable = new Runnable() {
        public void run() {
            showMessage();
        }
    };

	public int freqFromDelay(int off, int on) {
		return 1000 / (off + on);
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.ToggleButtonStrobe);
		textViewOn = (TextView) findViewById(R.id.TextViewOn);
        textViewOff = (TextView) findViewById(R.id.TextViewOff);
        textViewFreq = (TextView) findViewById(R.id.textViewFreq);

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
		final SeekBar skbaroff = (SeekBar)findViewById(R.id.SeekBarOff);
		final SeekBar skbarFreq = (SeekBar)findViewById(R.id.SeekBarFreq);

        skbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				runner.delay = progress;
				textViewOn.setText(getResources().getString(R.string.speed) + ": " + progress + " ms");
				skbarFreq.setProgress(freqFromDelay(runner.delayoff, runner.delay));
			}
		});
		skbar.setProgress(runner.delay);
        
        skbaroff.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				runner.delayoff = progress;
				textViewOff.setText(getResources().getString(R.string.speedoff) + ": " + progress + " ms");
				skbarFreq.setProgress(freqFromDelay(runner.delayoff, runner.delay));
			}
		});
		skbaroff.setProgress(runner.delayoff);

		skbarFreq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				frequency = progress;
                if (fromUser) {
                    float ratio = runner.delayoff / runner.delay;
                    skbaroff.setProgress(progress);
                }
                textViewFreq.setText(getResources().getString(R.string.frequency) + ": " + frequency + " Hz");
			}
		});
        skbarFreq.setProgress(0);
		skbarFreq.setProgress(freqFromDelay(runner.delayoff, runner.delay));

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