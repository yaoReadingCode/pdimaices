/*
 * Created on Jun 5, 2005
 * @author Rafael Santos (rafael.santos@lac.inpe.br)
 * 
 * Part of the Java Advanced Imaging Stuff site
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI)
 * 
 * STATUS:
 * - Still alpha / working for some tasks or parameters / fully functional
 * - Needs documentation on the source code
 * - Needs documentation on the page
 * 
 * 
 * Redistribution and usage conditions must be done under the
 * Creative Commons license:
 * English: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.en
 * Portuguese: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.pt
 * More information on design and applications are on the projects' page
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI).
 */
package algorithms.templatematching;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.JFrame;

/**
 * This class demonstrates the usage of the TemplateMatchingWithIterators
 * but without running it as a separate thread.
 */
public class DemoTemplateMatchingWithIterators
  {
  /**
   * This is the applications' entry point.
   * @param args the input image and template
   */
  public static void main(String[] args)
    {
    // We need two arguments: the image and template filenames.
    if (args.length != 2)
      {
      System.err.println("Usage: java algorithms.templatematching.DemoTemplateMatchingWithIterators "+
                         "image template");
      System.exit(0);
      }
    // Open the image and template 
    PlanarImage image = JAI.create("fileload", args[0]);
    PlanarImage template = JAI.create("fileload", args[1]);
    // Create the TemplateMatchingWithIterators instance and run it (threadless)
    TemplateMatchingWithIterators tm = new TemplateMatchingWithIterators(image,template);
    tm.run(); // don't use start() here!
    TiledImage output = tm.getOutput();
    // Get the resulting image and show the input and output images in an instance of
    // DisplayTwoSynchronizedImages.
    // Create a JFrame for displaying the results.
    JFrame frame = new JFrame();
    frame.setTitle("Template matching of "+args[0]+" with "+args[1]);
    // Add to the JFrame's ContentPane an instance of DisplayJAI with the match image.
    frame.getContentPane().add(new jai.binarize.DisplayTwoSynchronizedImages(image,output));
    // Set the closing operation so the application is finished.
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack(); // adjust the frame size using preferred dimensions.
    frame.setVisible(true); // show the frame.
    }
  }