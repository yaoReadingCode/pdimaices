/*
 * Created on Jun 30, 2005
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
package algorithms.segmentation.regiongrowing;

import jai.binarize.DisplayTwoSynchronizedImages;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * This class demonstrates the SimpleRegionGrowing class with an user interface.
 */
public class DemoSimpleRegionGrowing
  {
 /**
  * The application entry point. We will need to give a file name of an image to be
  * segmented.
  * @param args the command-line arguments.
  */
  public static void main(String[] args)
    {
    // We need the image filename. Additional arguments will force image preprocessing.
    if (args.length == 0)
      {
      System.err.println("Usage: java algorithms.segmentation.regiongrowing.DemoSimpleRegionGrowing image [preprocess]");
      System.exit(0);
      }
    // Read the image.
    PlanarImage image = JAI.create("fileload", args[0]);
    // Create the image processing task. 
    SimpleRegionGrowing task = new SimpleRegionGrowing(image,(args.length > 1));
    // Create a display for the original and binarized image.    
    DisplayTwoSynchronizedImages d = 
      new DisplayTwoSynchronizedImages(image,task.getInternalImage());
    // Display the original and binarized images in a JFrame.
    JFrame origFrame = new JFrame();
    origFrame.setTitle("Original Image and Binarized Image");
    origFrame.getContentPane().add(new JScrollPane(d));
    // Set the closing operation so the application is finished.
    origFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    origFrame.pack(); // adjust the frame size using preferred dimensions.
    origFrame.setVisible(true); // show the frame.
    // Create a ProgressBar in a JFrame.
    JProgressBar progressBar = new JProgressBar(0,(int)task.getSize());
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    JFrame progressFrame = new JFrame();
    progressFrame.setTitle("Progress");
    progressFrame.getContentPane().add(progressBar);
    // Set the closing operation so the application is finished.
    progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    progressFrame.pack(); // adjust the frame size using preferred dimensions.
    progressFrame.setVisible(true); // show the frame.
    // We're ready to go.
    task.start();
    // Change the progress bar while the segmentation is being performed.
    while(!task.isFinished())
      {
      progressBar.setValue((int)task.getPosition());  
      progressBar.repaint();
      }
    // Segmentation has finished.
    progressBar.setValue((int)task.getPosition());  
    // Create a new frame to show the results.
    JFrame resultsFrame = new JFrame();
    resultsFrame.setTitle("Segmentation results");
    // Add to the JFrame's ContentPane an instance of DisplayDEM with the processed image.
    resultsFrame.getContentPane().add(new JScrollPane(new DisplayJAI(task.getOutput())));
    // Set the closing operation so the application is finished.
    resultsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    resultsFrame.pack(); // adjust the frame size using preferred dimensions.
    resultsFrame.setVisible(true); // show the frame.
    // Let's see some textual data about the segmentation.
    System.out.println("Number of regions: "+task.getNumberOfRegions());
    for(int c=1;c<=task.getNumberOfRegions();c++)
      System.out.println("Region "+c+": "+task.getPixelCount(c)+" pixels");
    }

  }