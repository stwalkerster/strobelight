/*
 * Original work Copyright (c) 2011 Simon Walker
 * Modified work Copyright 2015 Mathieu Souchaud
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
    private ToggleButton toggleButton;
    private Button moreFreqButton;
    private Button lessFreqButton;
    private Button buttonOffLess;
    private Button buttonOffMore;
    private Button buttonOnLess;
    private Button buttonOnMore;
    private double frequency;
    private final int maxSeekDelay = 1090;
    private final int maxSeekFreq = 109;

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

        toggleButton = (ToggleButton) findViewById(R.id.ToggleButtonStrobe);
        moreFreqButton = (Button) findViewById(R.id.buttonFreqMore);
        lessFreqButton = (Button) findViewById(R.id.buttonFreqLess);

        buttonOffLess = (Button) findViewById(R.id.buttonOffLess);
        buttonOffMore = (Button) findViewById(R.id.buttonOffMore);
        buttonOnLess = (Button) findViewById(R.id.buttonOnLess);
        buttonOnMore = (Button) findViewById(R.id.buttonOnMore);

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
                    toggleButton.setEnabled(false);
                    textViewOn.setText(R.string.nocamera);
                    return;
                }

                cam.release();
            }
            catch(RuntimeException ex)
            {
                toggleButton.setEnabled(false);
                textViewOn.setText(R.string.nocamera);
                Toast.makeText(getApplicationContext(), "Error connecting to camera flash.", Toast.LENGTH_LONG).show();
                return;
            }
        }


        toggleButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                if (toggleButton.isChecked()) {
                    thread = new Thread(runner);
                    thread.start();
                } else {
                    runner.requestStop = true;
                }
            }
        });


        OnClickListener freqButtonListener = new OnClickListener() {
            public void onClick(View v) {
                int progress = seekbarFreq.getProgress();
                if (v == moreFreqButton)
                    progress += 1;
                else
                    progress -= 1;
                seekbarFreq.setProgress(progress);
                setSeekbarFreqProgress(progress);
            }
        };
        moreFreqButton.setOnClickListener(freqButtonListener);
        lessFreqButton.setOnClickListener(freqButtonListener);


        OnClickListener offButtonListener = new OnClickListener() {
            public void onClick(View v) {
                int progress = seekbarOff.getProgress();
                if (v == buttonOffMore)
                    progress += 1;
                else
                    progress -= 1;
                seekbarOff.setProgress(progress);
                setSeekbarOffProgress(progress);
            }
        };
        buttonOffLess.setOnClickListener(offButtonListener);
        buttonOffMore.setOnClickListener(offButtonListener);


        OnClickListener onButtonListener = new OnClickListener() {
            public void onClick(View v) {
                int progress = seekbarOn.getProgress();
                if (v == buttonOnMore)
                    progress += 1;
                else
                    progress -= 1;
                seekbarOn.setProgress(progress);
                setSeekbarOnProgress(progress);
            }
        };
        buttonOnLess.setOnClickListener(onButtonListener);
        buttonOnMore.setOnClickListener(onButtonListener);


        ////////////////////
        // delay on
        seekbarOn.setMax(maxSeekDelay);
        seekbarOn.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setSeekbarOnProgress(progress);
                }
            }
        });
        setTextSpeedOn(runner.delayOn);
        seekbarOn.setProgress(delayToSeek(runner.delayOn));


        ////////////////////
        // delay off
        seekbarOff.setMax(maxSeekDelay);
        seekbarOff.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setSeekbarOffProgress(progress);
                }
            }
        });
        setTextSpeedOff(runner.delayOff);
        seekbarOff.setProgress(delayToSeek(runner.delayOff));


        ////////////////////
        // frequency
        seekbarFreq.setMax(maxSeekFreq);
        seekbarFreq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setSeekbarFreqProgress(progress);
                }
            }
        });
        // init
        frequency = freqFromDelays((float) runner.delayOff, (float) runner.delayOn);
        setTextFreq(frequency);
        seekbarFreq.setProgress(freqToSeek(frequency));
    }

    private void setSeekbarFreqProgress(int progress) {
        frequency = seekToFreq(progress);

        // avoid divide by 0
        if (frequency <= 0)
            frequency = 1;
        if (runner.delayOn <= 0)
            runner.delayOn = 1;

        setTextFreq(frequency);

        final double prevRatio = runner.delayOff / runner.delayOn;
        final double newOffShare = (prevRatio / (prevRatio + 1));
        final double newOnShare = 1 - newOffShare;
        final double newTotalDelay = 1000 / frequency; // ms
        runner.delayOff = newTotalDelay * newOffShare;
        runner.delayOn = newTotalDelay * newOnShare;

        setTextSpeedOff(runner.delayOff);
        setTextSpeedOn(runner.delayOn);

        seekbarOff.setProgress(delayToSeek(runner.delayOff));
        seekbarOn.setProgress(delayToSeek(runner.delayOn));
    }

    private void setSeekbarOffProgress(int progress) {
        runner.delayOff = seekToDelay(progress);
        setTextSpeedOff(runner.delayOff);

        frequency = freqFromDelays(runner.delayOff, runner.delayOn);
        setTextFreq(frequency);
        seekbarFreq.setProgress(freqToSeek(frequency));
    }

    private void setSeekbarOnProgress(int progress) {
        runner.delayOn = seekToDelay(progress);
        setTextSpeedOn(runner.delayOn);

        frequency = freqFromDelays(runner.delayOff, runner.delayOn);
        setTextFreq(frequency);
        seekbarFreq.setProgress(freqToSeek(frequency));
    }

    private void setTextSpeedOff(double speed) {
        textViewOff.setText(getResources().getString(R.string.textSpeedOff) + String.format(": %.1f ms", speed));
    }

    private void setTextSpeedOn(double speed) {
        textViewOn.setText(getResources().getString(R.string.textSpeedOn) + String.format(": %.1f ms", speed));
    }

    private void setTextFreq(double freq) {
        textViewFreq.setText(getResources().getString(R.string.frequency) + String.format(": %.3f Hz", freq));
    }

    @Override
    protected void onStop() {
        runner.requestStop = true;
        toggleButton.setChecked(false);

        super.onStop();
    }

    public void showMessage()
    {
        String err = runner.errorMessage;
        runner.errorMessage = "";
        if(!err.equals(""))    {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
    
            Toast toast = Toast.makeText(context, err, duration);
            toast.show();
        }
        
        toggleButton.setChecked(false);
    }

    private double seekToFreq(int seek) {
        double freq;

        // check
        if (seek < 0)
            seek = 0;

        // input 0 to 9
        // output 0.1 to 1
        if (seek <= 9) {
            freq = 0.1 + ((double) seek / 10);
        }
        // input 10 to 109
        // output 1 to 100
        else {
            freq = 1 + ((double) seek - 10);
        }

        return freq;
    }

    private int freqToSeek(double freq) {
        int seek;

        // input 0 to 1
        // output 0 to 9
        if (freq <= 0.94)
            seek = (int) Math.round(freq) * 10;
            // input 1 to 100
            // output 10 to 109
        else
            seek = (int) Math.round(freq) + 9;

        // check
        if (seek < 0)
            seek = 0;
        if (seek > maxSeekFreq)
            seek = maxSeekFreq;

        return seek;
    }

    private double seekToDelay(int seek) {
        double delay;

        // check
        if (seek < 0)
            seek = 0;

        // input 0 to 999
        // output 0 to 999
        if (seek <= 1000) {
            delay = seek;
        }
        // input 1000 to 1090
        // output 1000 to 10000
        else {
            delay = 1000 + ((seek - 1000) * 100);
        }

        return delay;
    }

    private int delayToSeek(double delay) {
        int seek;

        // input 0 to 999
        // output 0 to 999
        if (delay <= 1000) {
            seek = (int) Math.round(delay);
        }
        // input 1000 to 10000
        // output 1000 to 1090
        else {
            seek = 1000 + (((int) Math.round(delay) - 1000) / 100);
        }

        // check
        if (seek < 0)
            seek = 0;
        if (seek > maxSeekDelay)
            seek = maxSeekDelay;

        return seek;
    }

    private double freqFromDelays(double delayOff, double delayOn) {
        double freq;
        if ((delayOff + delayOn) <= 0)
            freq = 0;
        else
            freq = 1000 / (delayOff + delayOn);
        return freq;
    }
}
