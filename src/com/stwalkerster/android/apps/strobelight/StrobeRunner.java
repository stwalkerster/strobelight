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

import android.hardware.Camera;

public class StrobeRunner implements Runnable {
    public static StrobeRunner getInstance()
    {
        return instance == null ? instance = new StrobeRunner() : instance;
    }
    
    private static StrobeRunner instance;
    
    
    public volatile boolean requestStop = false;
    public volatile boolean isRunning = false;
    public volatile double delayOn = 40;
    public volatile double delayOff = 40;
    public volatile StrobeLightConfig controller;
    public volatile String errorMessage = "";

    @Override
    public void run() {
        if(isRunning)
            return;
        
        requestStop = false;
        isRunning = true;
        
        Camera cam = Camera.open();
        
        Camera.Parameters pon = cam.getParameters(), poff = cam.getParameters();
        
        pon.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        poff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        
        while(!requestStop) {
            try {
                cam.setParameters(pon);
                Thread.sleep(Math.round(delayOn));

                cam.setParameters(poff);
                Thread.sleep(Math.round(delayOff));
            }
            catch(InterruptedException ex)
            {}
            catch(RuntimeException ex)
            {
                requestStop = true;
                errorMessage = "Error setting camera flash status. Your device may be unsupported.";
            }
        }

        cam.release();
        
        isRunning = false;
        requestStop = false;
        
        controller.mHandler.post(controller.mShowToastRunnable);
    }
}
