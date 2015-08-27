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

public class StrobeLightConfig extends Activity
{
	private Camera cam;
	private StrobeRunner runner;
	private Thread thread;
	private TextView textViewOn;
    private TextView textViewOff;
    private TextView textViewFreq;
	private SeekBar seekbarOn;
	private SeekBar seekbarOff;
	private SeekBar seekbarFreq;
	private ToggleButton togglebutton;
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
        
        togglebutton = (ToggleButton) findViewById(R.id.ToggleButtonStrobe);
		textViewOn = (TextView) findViewById(R.id.TextViewOn);
        textViewOff = (TextView) findViewById(R.id.TextViewOff);
        textViewFreq = (TextView) findViewById(R.id.textViewFreq);
		seekbarOn = (SeekBar) findViewById(R.id.SeekBarOn);
		seekbarOff = (SeekBar) findViewById(R.id.SeekBarOff);
		seekbarFreq = (SeekBar) findViewById(R.id.SeekBarFreq);

        runner = StrobeRunner.getInstance();
        runner.controller = this;
        
        if(!runner.isRunning) {
        	try {
		        cam = Camera.open();
		        if (cam == null) {
		        	togglebutton.setEnabled(false);
					textViewOn.setText(R.string.nocamera);
		        	return;
		        }
		        
		        cam.release();
        	}
        	catch(RuntimeException ex)
        	{
	        	togglebutton.setEnabled(false);
				textViewOn.setText(R.string.nocamera);
	        	Toast.makeText(getApplicationContext(), "Error connecting to camera flash.", Toast.LENGTH_LONG).show();
	        	return;
        	}
        } 
        
        
        togglebutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                if (togglebutton.isChecked()) {
                    thread = new Thread(runner);
                    thread.start();
                } else {
                    runner.requestStop = true;
                }
            }
        });


        seekbarOn.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                runner.delayOn = progress;
                textViewOn.setText(getResources().getString(R.string.speed) + ": " + progress + " ms");
                seekbarFreq.setProgress(freqFromDelay(runner.delayOff, runner.delayOn));
            }
        });
		seekbarOn.setProgress(runner.delayOn);


        seekbarOff.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                runner.delayOff = progress;
                textViewOff.setText(getResources().getString(R.string.speedoff) + ": " + progress + " ms");
                seekbarFreq.setProgress(freqFromDelay(runner.delayOff, runner.delayOn));
            }
        });
		seekbarOff.setProgress(runner.delayOff);


		seekbarFreq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequency = progress;
                if (fromUser) {
                    final float ratio = runner.delayOff / runner.delayOn;
                    float avgTime = (int) (((float) 1 / (float) frequency) * 1000) / 2;
                    seekbarOff.setProgress((int) (avgTime * ratio));
                    seekbarOn.setProgress((int) (avgTime * (1 / ratio)));
                }
                textViewFreq.setText(getResources().getString(R.string.frequency) + ": " + frequency + " Hz");
            }
        });
        seekbarFreq.setProgress(0);
		seekbarFreq.setProgress(freqFromDelay(runner.delayOff, runner.delayOn));
    }

    @Override
    protected void onStop() {
    	runner.requestStop = true;
        togglebutton.setChecked(false);

    	super.onStop();
    }

    public void showMessage()
    {
    	String err = runner.errorMessage;
    	runner.errorMessage = "";
    	if(!err.equals(""))	{
	    	Context context = getApplicationContext();
	    	int duration = Toast.LENGTH_SHORT;
	
	    	Toast toast = Toast.makeText(context, err, duration);
	    	toast.show();
    	}
    	
        togglebutton.setChecked(false);
    }
}