
package edu.ucsf.valelab.spotintensityanalysis;

import edu.ucsf.valelab.spotintensityanalysis.data.SpotIntensityParameters;
import edu.ucsf.valelab.spotintensityanalysis.plot.PlotUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.text.TextPanel;
import ij.text.TextWindow;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.prefs.Preferences;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author nico
 */
/**
 * KeyListener and MouseListenerclass for ResultsTable
 * 
 */
public class ResultsTableListener implements KeyListener, MouseListener{
   ImagePlus siPlus_;
   final ResultsTable res_;
   final TextWindow win_;
   final TextPanel tp_; 
   final SpotIntensityParameters parms_;
   final PlotUtils pu_;
   
   public ResultsTableListener(ImagePlus siPlus, ResultsTable res, 
           TextWindow win, SpotIntensityParameters parms) {
      siPlus_ = siPlus;
      res_ = res;
      win_ = win;
      tp_ = win.getTextPanel();
      parms_ = parms;
      
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      pu_ = new PlotUtils(prefs);
   }
   
   @Override
   public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();
      int row = tp_.getSelectionStart();
      if (key == KeyEvent.VK_J) {
         if (row > 0) {
            row--;
            tp_.setSelection(row, row);
         }
      } else if (key == KeyEvent.VK_K) {
         if  (row < tp_.getLineCount() - 1) {
            row++;
            tp_.setSelection(row, row);
         }
      }
      update();
   }
   @Override
   public void keyReleased(KeyEvent e) {}
   @Override
   public void keyTyped(KeyEvent e) {}

   @Override
   public void mouseReleased(MouseEvent e) {
      update();
   }
   @Override
   public void mousePressed(MouseEvent e) {}
   @Override
   public void mouseClicked(MouseEvent e) {
      update();
   }
   @Override
   public void mouseEntered(MouseEvent e) {};
   @Override
   public void mouseExited(MouseEvent e) {};

   private void update() {
      if (siPlus_ == null) {
         return;
      }
      int row = tp_.getSelectionStart();
      if (row >= 0 && row < tp_.getLineCount()) {
         if (siPlus_.getWindow() != null) {
            if (siPlus_ != IJ.getImage()) {
               siPlus_.getWindow().toFront();
               win_.toFront();
            }
         } else {
            siPlus_ = null;
            return;
         }
         int diam = 2 * parms_.radius_;
        
         int x = (int) res_.getValue("x", row);
         int y = (int) res_.getValue("y", row);
         Roi roi = new Roi(x - diam, y - diam, 2 * diam, 2 * diam, 
                 2 * diam);
         roi.setStrokeColor(Color.BLUE);
         siPlus_.setRoi(roi);
         
         XYSeries[] plots = new XYSeries[1];
         boolean[] showShapes = new boolean[1]; 
         
         XYSeries data = new XYSeries("" + x + "," + y, false, false);
         for (int col = 3; col < res_.getLastColumn(); col++) {
            data.add(Double.parseDouble(res_.getHeadings()[col]), 
                    res_.getValueAsDouble(col, row) );
         }
         plots[0] = data;
         pu_.plotDataN("Spot Intensity Profile", plots, "Time (s)",
           "Intensity", showShapes, "");
         
      }
   }
}
