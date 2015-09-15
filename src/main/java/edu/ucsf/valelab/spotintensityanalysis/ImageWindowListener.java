
package edu.ucsf.valelab.spotintensityanalysis;

import edu.ucsf.valelab.spotintensityanalysis.data.SpotIntensityParameters;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author nico
 */
public class ImageWindowListener implements MouseListener {
   private final ImagePlus iPlus_;
   private final ResultsTable res_;
   private final Polygon maxima_;
   private final SpotIntensityParameters parms_;
   
   public ImageWindowListener (ImagePlus iPlus, ResultsTable res, Polygon maxima,
           SpotIntensityParameters parms)  {
      iPlus_ = iPlus;
      res_ = res;
      maxima_ = maxima;
      parms_ = parms;
   }
   
   @Override
   public void mouseClicked(MouseEvent e) {
      update (e);
   }

   @Override
   public void mousePressed(MouseEvent e) {
      
   }

   @Override
   public void mouseReleased(MouseEvent e) {
      
   }

   @Override
   public void mouseEntered(MouseEvent e) {
      
   }

   @Override
   public void mouseExited(MouseEvent e) {
      
   }
   
   private void update(MouseEvent e) {
      if (iPlus_ == null) {
         return;
      }
      int x = e.getX();
      int y = e.getY();
      //ij.IJ.log ("Mouse clicked at " + x + ", " + y);
      boolean found = false;
      for (int i = 0; i < maxima_.npoints && !found; i++) {
         // TODO: check if this is our spot
      }
      
      
   }
   
}
