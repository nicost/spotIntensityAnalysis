 ///////////////////////////////////////////////////////////////////////////////
 //FILE:          RunAnalysis.java
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

import edu.ucsf.valelab.spotintensityanalysis.algorithm.FindLocalMaxima;
import edu.ucsf.valelab.spotintensityanalysis.algorithm.Utils;
import edu.ucsf.valelab.spotintensityanalysis.data.SpotIntensityParameters;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import java.awt.Color;
import java.awt.Polygon;

/**
 *
 * @author nico
 */
public class RunAnalysis extends Thread {
   final ImagePlus iPlus_;
   final SpotIntensityParameters parms_;
   final GenericDialog gd_;
   
   public RunAnalysis(ImagePlus iPlus,  
           SpotIntensityParameters parms, GenericDialog gd){
      iPlus_ = iPlus;
      parms_ = parms;
      gd_ = gd;
   }
   
   @Override
   public void run() {
      // first calculate the mean of the first n images
      final ImageStack is = iPlus_.getImageStack();
      final int width = iPlus_.getWidth();
      final int height = iPlus_.getHeight();
      
      final ImageStack firstImagesStack = is.crop(0, 0, 0, width, height, 
              parms_.nFrames_);
      ImagePlus ip = new ImagePlus("tmp", firstImagesStack);
      ip.copyScale(iPlus_);
      ImagePlus avgImP = Utils.Average(ip);
      
      Polygon maxima = FindLocalMaxima.FindMax(avgImP.getProcessor(), 
              parms_.radius_, parms_.noiseTolerance_, 
              FindLocalMaxima.FilterType.NONE);
      
      Overlay ovl = Utils.GetSpotOverlay(maxima, parms_.radius_, Color.red);
      
      iPlus_.setOverlay(ovl);
      
   }
   
   
}
