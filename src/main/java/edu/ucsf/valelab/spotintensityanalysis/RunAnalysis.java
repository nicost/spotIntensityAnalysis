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

import edu.ucsf.valelab.spotintensityanalysis.algorithm.StackUtils;
import ij.ImagePlus;
import ij.ImageStack;

/**
 *
 * @author nico
 */
public class RunAnalysis extends Thread {
   final ImagePlus iPlus_;
   final int nrImagesToAverage_;
   
   public RunAnalysis(ImagePlus iPlus, int nrImagesToAverage){
      iPlus_ = iPlus;
      nrImagesToAverage_ = nrImagesToAverage;
   }
   
   @Override
   public void run() {
      // first calculate the mean of the first n images
      final ImageStack is = iPlus_.getImageStack();
      final int width = iPlus_.getWidth();
      final int height = iPlus_.getHeight();
      
      final ImageStack firstImagesStack = is.crop(0, 0, 0, width, height, nrImagesToAverage_);
      ImagePlus ip = new ImagePlus("tmp", firstImagesStack);
      ip.copyScale(iPlus_);
      ImagePlus avgImP = StackUtils.Average(ip);
      
      avgImP.show();
      
   }
   
   
}
