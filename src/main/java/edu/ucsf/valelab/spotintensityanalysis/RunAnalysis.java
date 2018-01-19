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
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Overlay;
import ij.measure.ResultsTable;
import ij.plugin.ImageCalculator;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.text.TextPanel;
import ij.text.TextWindow;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Polygon;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

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
      ImagePlus avgIP = Utils.Average(ip);
      
      Polygon maxima = FindLocalMaxima.FindMax(avgIP.getProcessor(), 
              parms_.radius_, parms_.noiseTolerance_, 
              FindLocalMaxima.FilterType.NONE);
      Overlay ovl = Utils.GetSpotOverlay(maxima, parms_.radius_, Color.red);
      
      iPlus_.setOverlay(ovl);

      ResultsTable res = new ResultsTable();
      res.setPrecision(0);
      
      for (int i = 0; i < maxima.npoints; i++) {
         res.incrementCounter();
         res.addValue("x", maxima.xpoints[i]);
         res.addValue("y", maxima.ypoints[i]);
      }
    
      
      ImagePlus backgroundIP = avgIP.duplicate();
      if (parms_.backgroundMethod_.equals(SpotIntensityAnalysis.GAUSSIAN100)) {
         IJ.run(backgroundIP,"Gaussian Blur...", "sigma=100");
      } else if (parms_.backgroundMethod_.equals(SpotIntensityAnalysis.MEDIAN40)) {
         IJ.run(backgroundIP, "Median...", "radius=40");
      }
      
      ImageCalculator iCalc = new ImageCalculator();
      for (int frame = 1; frame <= iPlus_.getNFrames(); frame++) {
         IJ.showProgress(frame, iPlus_.getNFrames());
         ImageProcessor frameProcessor = is.getProcessor(frame);
         ImagePlus sub = iCalc.run("Subtract create 32-bit",  
                 new ImagePlus("t", frameProcessor), backgroundIP );
         for (int i = 0; i < maxima.npoints; i++) {
            int x = maxima.xpoints[i];
            int y = maxima.ypoints[i];
            float intensity = Utils.GetIntensity((FloatProcessor) sub.getProcessor(), x, y, parms_.radius_);
            res.setValue("" + (frame - 1) * parms_.intervalS_, i , intensity * parms_.ePerADU_);
         }
      }
       
      String name = "Spot analysis of " + iPlus_.getShortTitle();
      res.show(name);
      
      // Attach listener to TextPanel
      Frame frame = WindowManager.getFrame(name);
      if (frame != null && frame instanceof TextWindow) {
         TextWindow twin = (TextWindow) frame;
         TextPanel tp = twin.getTextPanel();

         for (MouseListener ms : tp.getMouseListeners()) {
            tp.removeMouseListener(ms);
         }
         for (KeyListener ks : tp.getKeyListeners()) {
            tp.removeKeyListener(ks);
         }

         ResultsTableListener myk = new ResultsTableListener(iPlus_, res, twin, parms_);
         tp.addKeyListener(myk);
         tp.addMouseListener(myk);
         frame.toFront();

         // atach listener to ImageWindow
         if (iPlus_ != null) {
            ImageWindow iWin = iPlus_.getWindow();
            ImageCanvas canvas = iWin.getCanvas();
            for (MouseListener ms : canvas.getMouseListeners()) {
               twin.removeMouseListener(canvas);
            }
            ImageWindowListener iwl = new ImageWindowListener(iPlus_, res, myk, 
                    twin, maxima);
            canvas.addMouseListener(iwl);
         }
      }

   }

   
   public void preview () {
      
      // first calculate the mean of the first n images
      final ImageStack is = iPlus_.getImageStack();
      final int width = iPlus_.getWidth();
      final int height = iPlus_.getHeight();
      
      final ImageStack firstImagesStack = is.crop(0, 0, 0, width, height, 
              parms_.nFrames_);
      ImagePlus ip = new ImagePlus("tmp", firstImagesStack);
      ip.copyScale(iPlus_);
      ImagePlus avgIP = Utils.Average(ip);
      
      Polygon maxima = FindLocalMaxima.FindMax(avgIP.getProcessor(), 
              parms_.radius_, parms_.noiseTolerance_, 
              FindLocalMaxima.FilterType.NONE);
      Overlay ovl = Utils.GetSpotOverlay(maxima, parms_.radius_, Color.red);
      
      iPlus_.setOverlay(ovl);
   }
   
   
}
