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
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;
import java.awt.AWTEvent;
import java.util.prefs.Preferences;


public class SpotIntensityAnalysis implements PlugIn, DialogListener {
   private SpotIntensityParameters parms_;
   private final Preferences myPrefs_;
   private final String TIMEINTERVAL = "TimeIntervalMs";
   private final String ELECTRONSPERADU = "ElectronsPerADU";
   private final String CHECKFIRSTNFRAMES = "CheckFirstNFrames";
   private final String SPOTRADIUS = "SpotRadius";
   private final String NOISETOLERANCE = "NoiseTolerance";
   
   public SpotIntensityAnalysis() {
      myPrefs_ = Preferences.userNodeForPackage(this.getClass());
   }
   
   @Override
   public void run(String arg) {
      final NonBlockingGenericDialog gd = new NonBlockingGenericDialog( 
              "Spot Intensity Analysis" );
      gd.addHelp("http://imagej.net/Spot_Intensity_Analysis");
      gd.addNumericField("Time Interval (s)", 
              myPrefs_.getDouble(TIMEINTERVAL, 1.0), 3);
      gd.addNumericField("Electrons per ADU",
              myPrefs_.getDouble(ELECTRONSPERADU, 1.0), 3);
      gd.addNumericField("Check First n Frames", 
              myPrefs_.getInt(CHECKFIRSTNFRAMES, 10), 0);
      gd.addNumericField("Spot Radius (pixels)", 
              myPrefs_.getInt(SPOTRADIUS, 3), 0);
      gd.addNumericField("Noise tolerance", 
              myPrefs_.getInt(NOISETOLERANCE, 500), 0);
      
      gd.addPreviewCheckbox(null, "Preview");
      
      gd.addDialogListener(this);
      
      parms_ = getParams(gd);
      
      gd.showDialog();
      
      if (gd.wasOKed()) {
         RunAnalysis ra = new RunAnalysis(ij.IJ.getImage(), parms_, gd);
         ra.start ();
      }
      
   }

   @Override
   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
      parms_ = getParams(gd);
      ImagePlus img = WindowManager.getCurrentImage();
      if (img != null) {
         if (!gd.isPreviewActive()) {
            img.setOverlay(null);
         } else {
            RunAnalysis ra = new RunAnalysis(img, parms_, gd);
            ra.preview();
         }
      }
      return true;
   }

   private SpotIntensityParameters getParams(GenericDialog gd) {
      SpotIntensityParameters parms = new SpotIntensityParameters();
      parms.intervalS_ = gd.getNextNumber();
      myPrefs_.putDouble(TIMEINTERVAL, parms.intervalS_);
      parms.ePerADU_ = gd.getNextNumber();
      myPrefs_.putDouble(ELECTRONSPERADU, parms.ePerADU_);
      parms.nFrames_ = (int) gd.getNextNumber();
      myPrefs_.putInt(CHECKFIRSTNFRAMES, parms.nFrames_);
      parms.radius_ = (int) gd.getNextNumber();
      myPrefs_.putInt(SPOTRADIUS, parms.radius_);
      parms.noiseTolerance_ = (int) gd.getNextNumber();
      myPrefs_.putInt(NOISETOLERANCE, parms.noiseTolerance_);
      
      return parms;
   }
}

