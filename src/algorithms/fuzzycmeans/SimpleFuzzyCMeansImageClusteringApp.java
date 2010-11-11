/*
 * Created on Jun 24, 2005
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
package algorithms.fuzzycmeans;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

/**
 * This applications shows a simple usage of the FuzzyCMeansImageClustering
 * class, clustering an image with parameters passed through the command
 * line. It does not use the threaded feature of FuzzyCMeansImageClustering.
 */
public class SimpleFuzzyCMeansImageClusteringApp
  {
  /**
   * The application entry point, which will need some parameters, described
   * below, and which must be passed in the command line:
   * - The input file name (string, existing file)
   * - The output file name (string, file will be created/overwriten)
   * - The desired number of classes (integer)
   * - The maximum number of iterations (integer)
   * - The fuzziness factor (floating point)
   * - The epsilon value (floating point)
   */
   public static void main(String[] args)
     {
     if (args.length != 6) // Check command line arguments.
       {
       System.err.println("Usage: java algorithms.fuzzycmeans.SimpleFuzzyCMeansImageClusteringApp "+
                          "inputImage outputImage numberOfClusters "+
                          "maxIterations fuzziness epsilon");
       System.exit(0);
       }
     // Load the input image.
     PlanarImage inputImage = JAI.create("fileload", args[0]);
     // Create the task.
     FuzzyCMeansImageClustering task =
        new FuzzyCMeansImageClustering(inputImage,
                                       Integer.parseInt(args[2]),
                                       Integer.parseInt(args[3]),
                                       Float.parseFloat(args[4]),
                                       Float.parseFloat(args[5]));
     task.run(); // Run it (without threading).
     // Get the resulting image.
     PlanarImage outputImage = task.getRankedImage(0);
     // Save the image on a file.
     JAI.create("filestore",outputImage,args[1],"TIFF");
     }

  }