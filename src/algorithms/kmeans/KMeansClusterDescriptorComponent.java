/*
 * Created on Jun 10, 2005
 * @author Rafael Santos (rafael.santos@lac.inpe.br)
 * 
 * Part of the Java Advanced Imaging Stuff site
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI)
 * 
 * STATUS: Complete.
 * 
 * Redistribution and usage conditions must be done under the
 * Creative Commons license:
 * English: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.en
 * Portuguese: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.pt
 * More information on design and applications are on the projects' page
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI).
 */
package algorithms.kmeans;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 * This class represents a KMeansClusterDescriptor as a component for visualization. 
 */
public class KMeansClusterDescriptorComponent extends JComponent
  {
  // The instance of KMeansClusterDescriptor.
  private KMeansClusterDescriptor descriptor;
  // The width and height of the component.
  private int width,height;
  // A font to draw some text.
  private Font font = new Font("Monospaced",0,12);
  
 /**
  * The constructor for this class, which sets the fields' values.
  * @param width the width of the component.
  * @param height the height of the component.
  * @param descriptor the instance of KMeansClusterDescriptor that will be shown.
  */
  public KMeansClusterDescriptorComponent(int width,int height,KMeansClusterDescriptor descriptor)
    {
    this.width = width;
    this.height = height;
    this.descriptor = descriptor;
    }

 /**
  * This method informs the maximum size of this component, which will be the same as the preferred size.
  */
  public Dimension getMaximumSize()
    {
    return getPreferredSize();
    }

 /**
  * This method informs the minimum size of this component, which will be the same as the preferred size.
  */
  public Dimension getMinimumSize()
    {
    return getPreferredSize();
    }

 /**
  * This method informs the preferred size of this component, which will be constant.
  */
  public Dimension getPreferredSize()
    {
    return new Dimension(width,height);
    }

 /**
  * Paints the component with information from the cluster descriptor. 
  */
  protected void paintComponent(Graphics g)
    {
    Graphics2D g2d = (Graphics2D)g;
    float[] centroid = descriptor.getCentroid();
    Color background;
    // If there are three dimensions on the cluster descriptors, we assume they represent
    // a RGB color coordinate, otherwise we use the first dimension as the gray level.
    if (centroid.length == 3)
      background = new Color((int)centroid[0],(int)centroid[1],(int)centroid[2]);
    else    
      background = new Color((int)centroid[0],(int)centroid[0],(int)centroid[0]);
    // Fill the background.
    g2d.setColor(background);
    g2d.fillRect(0,0,width,height);
    // Set the font and get some attributes.
    g2d.setFont(font);
    FontMetrics m = g2d.getFontMetrics();
    // Draw the assignment count value.
    String data = ""+descriptor.getNumberOfAssignedPoints();
    int x = width-3-m.stringWidth(data);
    int y = m.getHeight()+(height-m.getHeight())/2;
    g2d.setColor(Color.BLACK);
    g2d.drawString(data,x+1,y);
    g2d.setColor(Color.BLACK);
    g2d.drawString(data,x,y+1);
    g2d.setColor(Color.WHITE);
    g2d.drawString(data,x,y);
    }
  
  }