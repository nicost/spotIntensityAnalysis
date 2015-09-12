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
      
      gd.addPreviewCheckbox(null, "Analyze");
   }

   @Override
   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
       if (!gd.isPreviewActive()) {
          
       } else {
          RunAnalysis ra = new RunAnalysis(ij.IJ.getImage(), 10);
          ra.start();
       }
       return true;
   }
}

