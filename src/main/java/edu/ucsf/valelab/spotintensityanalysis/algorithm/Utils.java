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
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.Color;
import java.awt.Polygon;

/**
 *
 * @author nico
 */
public class Utils {
   
   public static ImagePlus Average(ImagePlus inPlus) {
      ImageStack stack = inPlus.getImageStack();
      // TODO: return error when the stack does not contain shortProcessors
      ImageProcessor ip = stack.getProcessor(1);
      final int width = ip.getWidth();
      final int height = ip.getHeight();
      final int dimension = width * height;
      
      long[] summedPixels = new long[dimension];
      
      for (int n = 1; n <= stack.getSize(); n++) {
         ip = stack.getProcessor(n);
         short[] pixels = (short[]) ip.getPixels();
         for (int i = 0; i < dimension; i++) {
            summedPixels[i] += pixels[i] & 0xffff;
         }
      }
      
      short[] averagedPixels = new short[dimension];
      for (int i = 0; i < dimension; i++) {
            averagedPixels[i] = (short) (summedPixels[i] / stack.getSize());
         }

      ImageProcessor outProc = new ShortProcessor(width, height);
      outProc.setPixels(averagedPixels);
      ImagePlus outPlus = new ImagePlus("Average", outProc);  
      outPlus.copyScale(inPlus);
      
      return outPlus;
   }
   
   public static Overlay GetSpotOverlay (Polygon spots, int radius, 
           Color symbolColor) {
      Overlay ov = new Overlay();
      int diameter = 2 * radius;
      for (int i = 0; i < spots.npoints; i++) {
         int x = spots.xpoints[i];
         int y = spots.ypoints[i];
         Roi roi = new Roi(x - radius, y - radius, 
                 diameter, diameter, diameter);
         roi.setStrokeColor(symbolColor);
         ov.add(roi);
      }
      return ov;
   }
   
   public static long GetIntensity(ImageProcessor ip, int x, int y, int radius) {
      long results = 0;
      // use symmetry to avoid too many calculations
      for (int i = 0; i < radius; i++) {
         for (int j = 0; j < radius; j++) {
            if (i == 0 && j == 0) {
               results += ip.get(x, y);
            } else {
               int d = (int) Math.sqrt( (i-radius)*(i-radius) + (j-radius)* (j-radius) );
               if (d < radius) {
                  if (i == 0) {
                     results += ip.get(x, y + j);
                     results += ip.get(x, y - j);
                  } else if (j==0) {
                     results += ip.get(x + i, y);
                     results += ip.get(x - i, y);
                  } else {
                     results += ip.get(x - i, y - j);
                     results += ip.get(x - i, y + j);
                     results += ip.get(x + i, y - j);
                     results += ip.get(x + i, y + j);
                  }
               }
            }
         }
      }
      return results;
   }
   
}
