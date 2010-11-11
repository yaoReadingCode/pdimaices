/*
 * Created on May 24, 2005
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
package algorithms.som;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

/**
 * This class demonstrates the usage of the SOMForImages class.
 */
public class DemoSOMForImages
  {
 /**
  * The application entry point.
  * @param args the command-line parameters.
  */
  public static void main(String[] args)
    {
    // We need one argument: the image filename.
    if (args.length != 1)
      {
      System.err.println("Usage: java algorithms.common.som.DemoSOMForImages image");
      System.exit(0);
      }
    // Open the image (using the name passed as a command line parameter)
    PlanarImage pi = JAI.create("fileload", args[0]);
    // Create the SOMForImages instance and set its parameters.
    SOMForImages som = new SOMForImages(pi,6,6);
    som.initWeights();
    som.setNeighborhoodType(SOMForImages.NEIGHBORHOOD_GAUSSIAN);
    som.setUpdateRadius(20);
    som.setMinimumUpdateRadius(1);
    som.setUpdateRadiusDecay(0.1f);
    som.setLearningRate(0.9f);
    som.setMinimumLearningRate(0.1f);
    som.setLearningRateDecay(0.1f);
    // Run it.
    System.out.println("Will do "+som.getSize()+" iterations.");
    som.run(); // no threading
    // Dump its results.
    System.out.println(som);
    }
  }