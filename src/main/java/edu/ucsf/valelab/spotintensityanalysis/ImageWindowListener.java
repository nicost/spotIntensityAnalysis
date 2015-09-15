
package edu.ucsf.valelab.spotintensityanalysis;

import edu.ucsf.valelab.spotintensityanalysis.algorithm.NearestPoint2D;
import edu.ucsf.valelab.spotintensityanalysis.algorithm.Utils;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.text.TextWindow;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

/**
 *
 * @author nico
 */
public class ImageWindowListener implements MouseListener {
   private final ImagePlus iPlus_;
   private final ResultsTable res_;
   private final TextWindow twin_;
   private final NearestPoint2D np2D_;
   private final ResultsTableListener rtl_;
   
   public ImageWindowListener (ImagePlus iPlus, ResultsTable res, 
           ResultsTableListener rtl, TextWindow twin,  Polygon maxima)  {
      iPlus_ = iPlus;
      res_ = res;
      rtl_ = rtl;
      twin_ = twin;
      np2D_ = new NearestPoint2D(Utils.PolygonToList(maxima), 100);
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
      int tx = e.getX();
      int ty = e.getY();
      int offscreenX = iPlus_.getWindow().getCanvas().offScreenX(tx);
		int offscreenY = iPlus_.getWindow().getCanvas().offScreenY(ty);
      
      Point2D.Double closestPoint = np2D_.findKDWSE(
              new Point2D.Double(offscreenX, offscreenY));
      if (closestPoint != null) {
         for (int i = 0; i < res_.size(); i++) {
            final int x = (int) res_.getValueAsDouble(0, i);
            final int y = (int) res_.getValueAsDouble(1, i);
            if (x == closestPoint.x && y == closestPoint.y) {
               twin_.getTextPanel().setSelection(i, i);
               rtl_.keyPressed(null);
               return;
            }
         }
      }
      
      
   }
   
}
