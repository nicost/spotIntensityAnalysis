 ///////////////////////////////////////////////////////////////////////////////
 //FILE:          
 //PROJECT:       
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

package edu.ucsf.valelab.spotintensityanalysis.algorithm;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

/**
 *
 * @author nico
 */
public class StackUtils {
   
   public static ImagePlus Average(ImagePlus inPlus) {
      ImageStack stack = inPlus.getImageStack();
      // TODO: return error when the stack does not contain shortProcessors
      ImageProcessor ip = stack.getProcessor(0);
      final int width = ip.getWidth();
      final int height = ip.getHeight();
      final int dimension = width * height;
      
      long[] summedPixels = new long[dimension];
      
      for (int n = 0; n < stack.getSize(); n++) {
         ip = stack.getProcessor(n);
         short[] pixels = (short[]) ip.getPixels();
         for (int i = 0; i < dimension; i++) {
            summedPixels[i] += pixels[i] & 0xffff;
         }
      }
      
      for (int i = 0; i < dimension; i++) {
            summedPixels[i] = summedPixels[i] / stack.getSize();
         }

      ImageProcessor outProc = new ShortProcessor(width, height);
      outProc.setPixels(summedPixels);
      ImagePlus outPlus = new ImagePlus("Average", outProc);  
      outPlus.copyScale(inPlus);
      
      return outPlus;
   }
}
