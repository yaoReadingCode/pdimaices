/*
 * Created on Jun 27, 2005
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
 * This application demonstrates the usage of the cluster validity measures that
 * can be calculated by the FuzzyCMeansImageClustering algorithm. It attempts to
 * find an optimal number of clusters for a particular image.
 */
public class AutoFuzzyCMeansClustering
  {
 /**
  * The application entry point.
  */
  public static void main(String[] args)
    {
    if (args.length != 1) // Check command line arguments.
      {
      System.err.println("Usage: java algorithms.fuzzycmeans.AutoFuzzyCMeansClustering "+
                         "inputImage");
      System.exit(0);
      }
    // Load the input image.
    PlanarImage inputImage = JAI.create("fileload", args[0]);
    // Create several tasks, each with a different number of clusters.
    double partitionCoefficient,partitionEntropy,compactnessAndSeparation;
    double bestPartitionCoefficient = Double.MIN_VALUE;
    double bestPartitionEntropy = Double.MAX_VALUE;
    double bestCompactnessAndSeparation = Double.MAX_VALUE;
    int bestByPartitionCoefficient=1,bestByPartitionEntropy=1,bestByCompactnessAndSeparation=1;
    System.out.println("+--------+-------------+-------------+-------------+");
    System.out.println("|Clusters| Part.Coeff. |Part.Entropy |Compact.&Sep.|");
    for(int c=2;c<10;c++)
      {
      // Create the task.
      FuzzyCMeansImageClustering task = new FuzzyCMeansImageClustering(inputImage,c,100,2,0.005);
      task.run(); // Run it (without threading).
      // Get the resulting validity measures.
      partitionCoefficient = task.getPartitionCoefficient();
      partitionEntropy = task.getPartitionEntropy();
      compactnessAndSeparation = task.getCompactnessAndSeparation();
      // See which is the best so far.
      if (partitionCoefficient > bestPartitionCoefficient)
        {
        bestPartitionCoefficient = partitionCoefficient;
        bestByPartitionCoefficient = c;
        }
      if (partitionEntropy < bestPartitionEntropy)
        {
        bestPartitionEntropy = partitionEntropy;
        bestByPartitionEntropy = c;
        }
      if (compactnessAndSeparation < bestCompactnessAndSeparation)
        {
        bestCompactnessAndSeparation = compactnessAndSeparation;
        bestByCompactnessAndSeparation = c;
        }
      // Print a simple report.
      System.out.println("|   "+String.format("%2d",new Object[]{new Integer(c)})+
                         "   |"+String.format("%13.6f|%13.6f|%13.6f|",
                                             new Object[]{new Double(partitionCoefficient),
                                             new Double(partitionEntropy),
                                             new Double(compactnessAndSeparation)}));
      }
    System.out.println("+--------+-------------+-------------+-------------+");
    System.out.println("Best number of clusters:");
    System.out.println("  according to Partition Coefficient:"+bestByPartitionCoefficient);
    System.out.println("  according to Partition Entropy:"+bestByPartitionEntropy);
    System.out.println("  according to Compactness and Separation:"+bestByCompactnessAndSeparation);
    }
  
  }