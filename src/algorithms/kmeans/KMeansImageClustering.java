/*
 * Created on Jun 8, 2005
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

import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import algorithms.common.ImageProcessingTask;

/**
 * This class implements a basic K-Means clustering algorithm as an image
 * processing task. This implementation tries to speed things up, but needs
 * to keep all image data on memory.
 * This implementation deals only with integer-like pixel data but can
 * cluster N-dimensional data.
 * One thing to remember about K-Means: it is possible that some clusters
 * will become "empty". This implementation does not deal with any solution
 * to this case, the clusters will simply become empty and will not be
 * further processed.
 * IMPORTANT: This implementation does not lock access to the data while the
 * clustering is being done.
 */
public class KMeansImageClustering extends ImageProcessingTask
  {
  // The output (clustered) image.
  private TiledImage pOutput;
  // The input image dimensions.
  private int width,height,numBands;
  // Some clustering parameters.
  private int maxIterations,numClusters;
  // The iteration counter will be global so we can get its value on the
  // middle of the clustering process.
  private int iteration;
  // A metric of clustering "quality".
  private double sumOfDistances;
  // A small value, if the difference of the cluster "quality" does not
  // changes beyond this value, we consider the clustering converged.
  private double epsilon;
  // Flags and counters.
  private boolean finished = false;
  private long position;
  // The cluster centers.
  private float[][] clusterCenters;
  // The cluster assignment counter.
  private int[] clusterAssignmentCount;
  // A big array with all the input data and a small one for a pixel.
  private int[] inputData,aPixel;
  // A big array with the output data (cluster indexes).
  private short[][] outputData;
  
  /**
   * The constructor for the class, which sets the input image, the number of
   * desired clusters, the maximum number of iterations, a value that will be
   * used to decide whether the convergence has stopped and the initial
   * clusters centers initialization mode. It also allocates needed memory.
   * @param pInput the input planar image.
   * @param numClusters the desired number of clusters.
   * @param maxIterations the maximum number of iterations.
   * @param epsilon a small value used to verify if clustering has converged.
   * @param initMode the initialization mode: 'R' for random values, 'S' for
   *        evenly spaced values, 'D' for randomly sampled data values.
   */
  public KMeansImageClustering(PlanarImage pInput,int numClusters,int maxIterations,
                          double epsilon,char initMode)
    {
    // Get the image dimensions.
    width = pInput.getWidth();
    height = pInput.getHeight();
    numBands = pInput.getSampleModel().getNumBands();
    // Create the output image based on the input image's dimensions.
    pOutput = new TiledImage(pInput.getMinX(),pInput.getMinY(),width,height,
                             pInput.getMinX(),pInput.getMinY(),pInput.getSampleModel(),pInput.getColorModel());
    // Get some clustering parameters.
    this.numClusters = numClusters;
    this.maxIterations = maxIterations;
    this.epsilon = epsilon;
    iteration = 0;
    // We need arrays to store the clusters' centers and assignment counts.
    clusterCenters = new float[numClusters][numBands];
    clusterAssignmentCount = new int[numClusters];
    // Gets the raster for the input image.
    Raster raster = pInput.getData();
    // Gets the whole image data on memory. Get memory for a single pixel too.
    inputData = new int[width*height*numBands];
    aPixel = new int[numBands];
    // Gets memory for the output data (cluster indexes).
    outputData = new short[width][height];
    raster.getPixels(0,0,width,height,inputData);
    // Initialize the clusters centers, depending on the initialization mode.
    // Evenly spaced values (over main diagonal).
    if ((initMode == 's') || (initMode == 'S'))
      {
      // Discover the extrema value for the image data.
      double[] extrema = getExtrema(pInput);
      double delta = (extrema[1]-extrema[0])/(numClusters-1);
      for(int cluster=0;cluster<numClusters;cluster++)
        {
        for(int band=0;band<numBands;band++)
          clusterCenters[cluster][band] = (float)extrema[0];
        extrema[0] += delta;
        }
      }
    // Randomly sampled data values.
    else if ((initMode == 'd') || (initMode == 'D'))
      {
      // Create a Iterator to get the samples.
      RandomIter iterator = RandomIterFactory.create(pInput,null);
      for(int cluster=0;cluster<numClusters;cluster++)
        {
        int rx = (int)(Math.random()*width);
        int ry = (int)(Math.random()*height);
        iterator.getPixel(rx,ry,aPixel);
        for(int band=0;band<numBands;band++)
          clusterCenters[cluster][band] = aPixel[band];
        }  
      }
    // Random values, using the maximum of the data values as limit.
    else // if ((initMode == 'r') || (initMode == 'R'))
      {
      // Discover the maximum value for the image data.
      double[] extrema = getExtrema(pInput);
      for(int cluster=0;cluster<numClusters;cluster++)
        for(int band=0;band<numBands;band++)
           clusterCenters[cluster][band] = (float)(extrema[1]*Math.random());
      }
    }

 /**
  * This method returns a two-element array with the minimum and maximum
  * values found in a PlanarImage.
  * @param image the input PlanarImage.
  * @return an array with two positions: the first is the minima of the image
  *         and the second is the maxima.
  */
  private double[] getExtrema(PlanarImage image)
    {
    ParameterBlock pbMaxMin = new ParameterBlock();
    pbMaxMin.addSource(image);
    RenderedOp extremaOp = JAI.create("extrema", pbMaxMin);
    // Must get the extrema of all bands !
    double[] allMins = (double[])extremaOp.getProperty("minimum");
    double[] allMaxs = (double[])extremaOp.getProperty("maximum");
    double minValue = allMins[0];
    double maxValue = allMaxs[0];
    for(int v=1;v<allMins.length;v++)
      {
      if (allMins[v] < minValue) minValue = allMins[v];
      if (allMaxs[v] > maxValue) maxValue = allMaxs[v];
      }
    return new double[] {minValue,maxValue};
    }
 
 /**
  * This method performs the bulk of the processing. It runs the classic
  * K-Means clustering algorithm:
  * 1 - Scan the image and calculate the assignment vector.
  * 2 - Scan the assignment vector and recalculate the cluster centers.
  * 3 - Calculate statistics and repeat from 1 if needed.
  */
  public void run()
    {
    double lastSumOfDistances=0;
    iterations: // Label for main loop.
      for(iteration=0;iteration<maxIterations;iteration++)
        {
        // 0 - Clean the cluster assignment vector.
        Arrays.fill(clusterAssignmentCount,0);
        // 1 - Scan the image and calculate the assignment vector.
        for(int h=0;h<height;h++)
          for(int w=0;w<width;w++)
            {
            // Where does the individual pixel data start ?
            int index = (h*width+w)*numBands;
            // Gets the class for this pixel.
            for(int band=0;band<numBands;band++) aPixel[band] = inputData[index+band];
            short aClass = getClassFor(aPixel);
            // Update the position index.
            position += numBands + numClusters*numBands;
            outputData[w][h] = aClass;
            clusterAssignmentCount[aClass]++;
            }
        // 2 - Scan the assignment vector and recalculate the cluster centers.
        for(int cluster=0;cluster<numClusters;cluster++) Arrays.fill(clusterCenters[cluster],0f);
        // Update the position index.
        position += numClusters;
        for(int h=0;h<height;h++)
          for(int w=0;w<width;w++)
            {
            int theCluster = outputData[w][h];
            for(int band=0;band<numBands;band++)
              {
              int index = (h*width+w)*numBands;
              clusterCenters[theCluster][band] += inputData[index+band];
              }
            // Update the position index.
            position += numBands;
            }
        // 2a - Recalculate the centers.
        for(int cluster=0;cluster<numClusters;cluster++)
          if (clusterAssignmentCount[cluster] > 0)
            for(int band=0;band<numBands;band++)
              clusterCenters[cluster][band]/=clusterAssignmentCount[cluster];
        // Update the position index.
        position += numClusters*numBands;
        // 3 - Calculate statistics and repeat from 1 if needed.
        sumOfDistances = 0;
        for(int h=0;h<height;h++)
          for(int w=0;w<width;w++)
            {
            // To which class does this pixel belong ?
            short pixelsClass = outputData[w][h];
            // Calculate the distance between this pixel's values and its
            // assigned cluster center values.
            double distance = 0;
            int index = (h*width+w)*numBands;
            for(int band=0;band<numBands;band++)
              {
              double e1 = inputData[index+band];
              double e2 = clusterCenters[pixelsClass][band];
              double diff = (e1-e2)*(e1-e2);
              distance += diff;
              }
            distance = Math.sqrt(distance);
            sumOfDistances += distance;
            // Update the position index.
            position += numBands;
            }
        // Is it converging ?
        if (iteration > 0)
          if (Math.abs(lastSumOfDistances-sumOfDistances) < epsilon) break iterations;
        lastSumOfDistances = sumOfDistances;
        } // end of the iterations loop.
    finished = true;
    // Means that all calculations are done, too.
    position = getSize();
    }

 /**
  * This auxiliary method gets the class for a pixel vector.
  * @return the class (cluster index) for a pixel.
  */
  private short getClassFor(int[] pixel)
    {
    // Let's compare this pixel data with all the cluster centers.
    float closestSoFar = Float.MAX_VALUE;
    short classSoFar = 0;
    for(short cluster=0;cluster<numClusters;cluster++)
      {
      // Calculate the (quick) distance from this pixel to that cluster center.
      float distance = 0f;
      for(int band=0;band<numBands;band++)
        distance += Math.abs(clusterCenters[cluster][band]-pixel[band]);
      if (distance < closestSoFar)
        {
        closestSoFar = distance;
        classSoFar = cluster;
        }
      }
    return classSoFar;
    }

 /**
  * This method returns the output image. It can return an image which is
  * being processed while this method run, so results are not guaranteed
  * to be final (and can be very strange if the image is large).
  * @return the (possibly temporary) clustering results.
  */
  public TiledImage getOutput()
    {
    // We must dump the crossed contents of the cluster centers and the
    // cluster indexes into the resulting image.
    // Create a SampleModel for the output data.
    SampleModel sampleModel =
      RasterFactory.createBandedSampleModel(DataBuffer.TYPE_INT,width,height,numBands);
    // Create a WritableRaster using that sample model.
    WritableRaster raster = RasterFactory.createWritableRaster(sampleModel,new Point(0,0));
    // A pixel array will contain all bands for a specific x,y.
    int[] pixelArray = new int[numBands];
    // For all pixels in the image...
    for(int h=0;h<height;h++)
      for(int w=0;w<width;w++)
        {
        // Get the class (cluster center) for that pixel.
        short aClass = outputData[w][h];
        // Fill the array with the cluster center.
        for(int band=0;band<numBands;band++) pixelArray[band] = (int)clusterCenters[aClass][band];
        // Put it on the raster.
        raster.setPixel(w,h,pixelArray);
        }
    // Set the raster on the output image.
    pOutput.setData(raster);
    return pOutput;
    }

 /**
  * This method returns the estimated size (steps) for this task.
  * The value is, of course, an approximation, just so we will be able to
  * give the user a feedback on the processing time. In this case, the
  * value is calculated as the number of loops in the run() method.
  */
  public long getSize()
    {
    // Return the estimated size for this task:
    return (long)maxIterations* // The maximum number of iterations times
      (
       (width*height*(numBands+numClusters*numBands))+ // Step 1 of run()
       (numClusters+width*height*numBands)+ // Step 2 of method run()
       (numClusters*numBands)+  // Step 2a of method run()
       (width*height*numBands) // Step 3 of the method run()
      );
    }

 /**
  * This method returns a measure of the progress of the algorithm.
  */
  public long getPosition()
    {
    return position;
    }

 /**
  * This method returns true if the clustering has finished.
  */
  public boolean isFinished()
    {
    return finished;
    }

 /**
  * This method returns some information about the task (its progress).
  * @return some information about the task
  */
  public String getInfo()
    {
    if (!finished) return "Iteration "+(iteration+1)+" of "+maxIterations;
    else return "Clustering converged.";
    }

  }