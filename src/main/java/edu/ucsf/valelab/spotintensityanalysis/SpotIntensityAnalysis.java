///////////////////////////////////////////////////////////////////////////////
//FILE:          SpotIntensityAnalysis.java
//PROJECT:       SpotIntensityAnalysis
//-----------------------------------------------------------------------------
//
// AUTHOR:       Nico Stuurman
//
// COPYRIGHT:    University of California, San Francisco 2015
//
// LICENSE:      This file is distributed under the BSD license.
//               License text is included with the source distribution.
//
//               This file is distributed in the hope that it will be useful,
//               but WITHOUT ANY WARRANTY; without even the implied warranty
//               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.


package edu.ucsf.valelab.spotintensityanalysis;

import edu.ucsf.valelab.spotintensityanalysis.data.SpotIntensityParameters;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;
import java.awt.AWTEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpotIntensityAnalysis implements PlugIn, DialogListener {
   
   
   AtomicBoolean isRunning_ = new AtomicBoolean(false);
   
   @Override
   public void run(String arg) {
      final NonBlockingGenericDialog gd = new NonBlockingGenericDialog( 
              "Spot Intensity Analysis" );
      gd.addNumericField("Time Interval (s)", 1.0, 3);
      gd.addNumericField("Check First n Frames", 10, 0);
      gd.addNumericField("Spot Radius (pixels)", 3, 0);
      gd.addNumericField("Noise tolerance", 500, 0);
      
      gd.addPreviewCheckbox(null, "Pre-view");
      
      gd.addDialogListener(this);
      
      gd.showDialog();
      
      if (gd.wasOKed()) {
         ij.IJ.log("OK Pressed");
      }
      
   }

   @Override
   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
      SpotIntensityParameters parms = getParams(gd);
      if (!gd.isPreviewActive()) {
         ij.IJ.getImage().setOverlay(null);
      } else {
          RunAnalysis ra = new RunAnalysis(ij.IJ.getImage(), parms, gd);
          ra.start();
      }
      return true;
   }
   
   private SpotIntensityParameters getParams(GenericDialog gd) {
      SpotIntensityParameters parms = new SpotIntensityParameters();
      parms.intervalS_ = gd.getNextNumber();
      parms.nFrames_ = (int) gd.getNextNumber();
      parms.radius_ = (int) gd.getNextNumber();
      parms.noiseTolerance_ = (int) gd.getNextNumber();
      
      return parms;
   }
}

