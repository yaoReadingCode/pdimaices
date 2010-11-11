/*
 * Created on May 24, 2005
 * @author Rafael Santos (rafael.santos@lac.inpe.br)
 * 
 * Part of the Java Advanced Imaging Stuff site
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI)
 * 
 * STATUS: Complete but could be enhanced.
 * Some possible enhancements:
 *   - Load network structure from a file. Put it on a new constructor?
 * 
 * Redistribution and usage conditions must be done under the
 * Creative Commons license:
 * English: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.en
 * Portuguese: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.pt
 * More information on design and applications are on the projects' page
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI).
 */
package algorithms.som;

import java.awt.Point;
import java.awt.image.Raster;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import javax.media.jai.PlanarImage;
import algorithms.common.ImageProcessingTask;

/**
 * This class implements the basic Self-Organizing Map algorithm, twisted to work
 * with images. It extends the ImageProcessingTask so we can have some (approximate)
 * idea on the algorithms.common' progress.
 *
 * Some of the members of this class are used to store values which are not related
 * with the SOM itself but will be very useful for visualization/algorithm analysis.
 *
 * Main references:
 * - http://www.arch.usyd.edu.au/~rob/study/publications/thesis/index.html
 * - Self-Organizing Maps, Teuvo Kohonen, Springer.
 * - Pattern Recognition using Neural Networks, Carl G. Looney, Oxford Press.
 * - Fundamentals of Neural Networks: Architectures, Algorithms and Applications,
 *   Laurene Fausett, Prentice-Hall.
 */
public class SOMForImages extends ImageProcessingTask implements Serializable
  {
  // The input image - this is the source of our data points. We will represent it 
  // as an array of floats.
  private PlanarImage inputImage;
  private float[] imageData;
  private int imageWidth,imageHeight,imageBands;
  // The random sampling factor - how many percent of the pixels will be used,
  // in each epoch, to train the network.
  private float samplingRate;
  // The dimensions of the SOM (w*h*d, d = number of bands on the input image).
  private int somWidth,somHeight;
  private int somDimensions;
  // The weights of this SOM.
  private float[][][] weights;
  // Additional SOM parameters.
  // SOM neighborhood type:
  private int neighborhoodType;
  public static final int NEIGHBORHOOD_GAUSSIAN    = 1;
  public static final int NEIGHBORHOOD_CIRCULAR    = 2;
  public static final int NEIGHBORHOOD_ONLY_WINNER = 3;
  // Learning rate parameters.
  private float learningRate;
  private float minimumLearningRate;
  private float learningRateDecay;
  // Update radius parameters.
  private float updateRadius;
  private float minimumUpdateRadius;
  private float updateRadiusDecay;
  // Global counters, indexes and references to values we'll need later.
  private int trainingStepsCounter;
  private float initialLearningRate,initialUpdateRadius;
  // An auxiliary matrix which says which neurons were updated in the last iteration.
  private boolean[][] updatedNeurons;
  // Another auxiliary matrix which says how many data points were assigned
  // (by the BMU rule) to a particular neuron.
  private int[][] assignmentCount;
  // This represents the last winning neuron.
  private Point lastWinningNeuron;
  
 /**
  * Constructor for the class, which gets memory for the data structures and set
  * some basic SOM parameters.
  * @param image the PlanarImage which will be used as data source.
  * @param w the width of the SOM
  * @param h the height of the SOM
  */
  public SOMForImages(PlanarImage image,int w,int h)
    {
    inputImage = image;
    imageWidth = inputImage.getWidth();
    imageHeight = inputImage.getHeight();
    imageBands = inputImage.getSampleModel().getNumBands();
    imageData = new float[imageWidth*imageHeight*imageBands];
    // Get all pixels into a floating point array.
    Raster inputRaster = inputImage.getData();
    inputRaster.getPixels(0,0,imageWidth,imageHeight,imageData);
    // Set the SOM parameters.
    somWidth = w;
    somHeight = h;
    somDimensions = imageBands;
    samplingRate = 1;
    weights = new float[somWidth][somHeight][somDimensions];
    updatedNeurons = new boolean[somWidth][somHeight];
    trainingStepsCounter = 0;
    assignmentCount = new int[somWidth][somHeight];
    }
  
 /**
  * Factory method for the class, which creates and returns a new instance of itself
  * through deserialization.
  * @param filename the stored SOM file name.
  */
  public static SOMForImages deserialize(String filename) throws IOException,ClassNotFoundException
    {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
    SOMForImages som = (SOMForImages)ois.readObject();
    ois.close();
    // I am not sure of what should I do here. I guess I should work harder
    // on this class serialization issues. Perharps check SerializableRenderedImage?
    return som;
    }
  
 /**
  * Initializes the weights of this SOM with random values between 0 and 255.
  */
  public void initWeights()
    {
    for(int h=0;h<somHeight;h++)
      for(int w=0;w<somWidth;w++)
        for(int d=0;d<somDimensions;d++)
          weights[w][h][d] = (float)Math.random()*256;
    }

 /**
  * Initializes the weights of this SOM with random values between the
  * values passed as arguments.
  * @param min the minimum value which will be used for the random weights.
  * @param max the maximum value which will be used for the random weights.
  */
  public void initWeights(float min,float max)
    {
    for(int h=0;h<somHeight;h++)
      for(int w=0;w<somWidth;w++)
        for(int d=0;d<somDimensions;d++)
          {
          weights[w][h][d] = (float)(min+Math.random()*(max-min));
          }
    }  

 /**
  * Execute (in a thread) the network training.
  */
  public void run() 
    {
    while(!isFinished())
      train();
    }  
  
 /*
  * Train the network with a pass through the image data. The training steps will
  * be: 1) determine the winning neuron; 2) update the winner neurons'
  * neighborhood and 3) update the learning rate and update radius.
  */
  private synchronized void train()
    {
    // Do a training epoch stuff thingie.
    resetAssignmentCounts();
    float[] data = new float[somDimensions];
System.out.println(trainingStepsCounter);    
    // Scan all image, respecting the sampling rate.
    for(int h=0;h<imageHeight;h++)
      {
      for(int w=0;w<imageWidth;w++)
        {
        // Should we use this pixel for sampling?
        if (Math.random() > samplingRate) continue;
        int offset = h*imageWidth*imageBands+w*imageBands;
        for(int band=0;band<imageBands;band++) data[band] = imageData[offset+band];
        // Find the winner neuron.
        Point winner = findWinner(data);
        increaseAssignmentCountAt(winner);
        // Update the network.
        if (neighborhoodType == NEIGHBORHOOD_GAUSSIAN)
          updateGaussian(winner,data);
        else if (neighborhoodType == NEIGHBORHOOD_CIRCULAR)
          updateCircular(winner,data);
        else if (neighborhoodType == NEIGHBORHOOD_ONLY_WINNER)
          updateOnlyWinner(winner,data);
        }
      }
    // After training the network with ALL image data (considering sampling),  
    // update the learning rate and update radius.
    learningRate *= (1f-learningRateDecay);
    if (learningRate < minimumLearningRate) learningRate = minimumLearningRate;
    updateRadius *= (1f-updateRadiusDecay);
    if (updateRadius < minimumUpdateRadius) updateRadius = minimumUpdateRadius;
    trainingStepsCounter++;
    } // end method train
  
 /*
  * This private method is used for debugging (it will format a vector of floats as a String)
  */
  private String printVector(float[] v) 
    {
    String r = "";
    for(int vv=0;vv<v.length;vv++) r += v[vv]+" ";
    return r;
    }
  
 /**
  * Finds the location of the winner neuron for a particular data vector, which
  * must be passed as an argument.
  * @param data the input data vector.
  * @return an instance of Point with the location of the winning neuron.
  */
  public Point findWinner(float[] data)
    {
    Point winner = new Point(0,0); // whatever
    float winnerDistance = Float.MAX_VALUE;
    // For each neuron, check which is closest to the data vector being considered.
    for(int h=0;h<somHeight;h++)
      for(int w=0;w<somWidth;w++)
        {
        float euclidDistance = 0f;
        for(int d=0;d<somDimensions;d++)
          euclidDistance += (data[d]-weights[w][h][d])*(data[d]-weights[w][h][d]);
        if (euclidDistance < winnerDistance) 
          {
          winnerDistance = euclidDistance;
          winner.x = w;
          winner.y = h;
          }
        }    
    lastWinningNeuron = winner;
    return winner;
    }
  
 /*
  * Updates the weights of the SOM using the winning neuron and the presented data.
  * This update method uses a Gaussian ("mexican hat") updating rule.
  * @param winner the location of the winner neuron.
  * @param data the data vector which was presented to the SOM.
  */
  private void updateGaussian(Point winner,float[] data)
    {
    // "Erase" the updated neurons matrix.
    for(int h=0;h<somHeight;h++) Arrays.fill(updatedNeurons[h],false);
    // Check all weights to see which must be updated.
    for(int h=0;h<somHeight;h++)
      for(int w=0;w<somWidth;w++)
        {
        // Calculate the distance between this neuron and the winner.
        // Note that the distance is measured using the lattice and not the weights values.
        float distance = 
          (float)Math.sqrt((w-winner.x)*(w-winner.x)+(h-winner.y)*(h-winner.y));
        if (distance < updateRadius)
          {
          // Calculate the gaussian gain. Note that it is a function of the distance.
          float gain = (float)Math.exp(-distance/(2.0*updateRadius*updateRadius));
          // Update the weights for the winning neurons.
          for(int d=0;d<somDimensions;d++)
            weights[w][h][d] += learningRate*gain*(data[d]-weights[w][h][d]);
          // Mark this neuron as updated.
          updatedNeurons[w][h] = true;
          }
        }
    }
  
 /*
  * Updates the weights of the SOM using the winning neuron and the presented data.
  * This update method uses a circular ("top hat") updating rule.
  * @param winner the location of the winner neuron.
  * @param data the data vector which was presented to the SOM.
  */
  private void updateCircular(Point winner,float[] data)
    {
    // "Erase" the updated neurons matrix.
    for(int w=0;w<somWidth;w++) Arrays.fill(updatedNeurons[w],false);
    // Check all weights to see which must be updated.
    for(int h=0;h<somHeight;h++)
      for(int w=0;w<somWidth;w++)
        {
        // Calculate the distance between this neuron and the winner.
        // Note that the distance is measured using the lattice and not the
        // weights values.
        float distance = 
          (float)Math.sqrt((w-winner.x)*(w-winner.x)+(h-winner.y)*(h-winner.y));
        if (distance < updateRadius)
          {
          // Calculate the circular gain.
          float gain = (updateRadius-distance)/updateRadius;
          // Update the weights for the winning neurons.
          for(int d=0;d<somDimensions;d++)
            weights[w][h][d] += learningRate*gain*(data[d]-weights[w][h][d]);
          // Mark this neuron as updated.
          updatedNeurons[w][h] = true;
          }
        }
    }
  
 /*
  * Updates the weights of the SOM using the winning neuron and the presented data.
  * This update method uses only the winning neuron.
  * @param winner the location of the winner neuron.
  * @param data the data vector which was presented to the SOM.
  */
  private void updateOnlyWinner(Point center,float[] data)
    {
    // "Erase" the updated neurons matrix.
    for(int h=0;h<somHeight;h++) Arrays.fill(updatedNeurons[h],false);
    // Update only the winner neuron.
    for(int d=0;d<somDimensions;d++)
      weights[center.x][center.y][d] += 
        learningRate * (data[d]-weights[center.x][center.y][d]);
    updatedNeurons[center.x][center.y] = true;
    }

 /**
  * Reset the assignment counts.
  */
  public void resetAssignmentCounts()
    {
    // "Erase" the assignment counts matrix.
    for(int h=0;h<somHeight;h++) Arrays.fill(assignmentCount[h],0);
    }

 /**
  * Get the assignment count for a specific neuron.
  * @param theOne the coordinates of the neuron which assignment count we want to get.
  * @return the assignment count for that neuron.
  */
  public int getAssignmentCountAt(Point theOne)
    {
    if ((theOne.x >= 0) && (theOne.y >= 0) &&
        (theOne.x < somWidth) && (theOne.y < somHeight))
         return assignmentCount[theOne.x][theOne.y];
    else return -1;
    }
  
 /*
  * Increase the assignment count for a specific neuron.
  * @param theOne the coordinates of the neuron which assignment count will be
  *        increased.
  */
  private void increaseAssignmentCountAt(Point theOne)
    {
    if ((theOne.x >= 0) && (theOne.y >= 0) &&
        (theOne.x < somWidth) && (theOne.y < somHeight))
       assignmentCount[theOne.x][theOne.y]++;
    }
  
 /*
  * Returns the estimated number of iterations (i.e. times the method run will be executed).
  * Since I am too lazy to calculate the number of iterations, I will simulate it.
  */
  public long getSize()
    {
    int _numberOfIterations = 0;
    float _learningRate = initialLearningRate;
    float _learningRateDecay = learningRateDecay;
    float _minimumLearningRate = minimumLearningRate;
    float _updateRadius = initialUpdateRadius;
    float _updateRadiusDecay = updateRadiusDecay;
    float _minimumUpdateRadius = minimumUpdateRadius;
    boolean doLR = true, doUR = true;
    while(doLR || doUR)
      {
      // This part does the same that the method run, but with the _ variables.
      _learningRate *= (1f-_learningRateDecay);
      if (_learningRate < _minimumLearningRate) 
        {
        _learningRate = _minimumLearningRate;
        doLR = false;
        }
      _updateRadius *= (1f-_updateRadiusDecay);
      if (_updateRadius < _minimumUpdateRadius) 
        {
        _updateRadius = _minimumUpdateRadius;
        doUR = false;
        }
      _numberOfIterations++;
      }
    return _numberOfIterations;
    }

 /**
  * This method returns true if the network is assumed to be converged, i.e. if both
  * the stop conditions are true.
  * @return true if the network has converged.
  */
  public boolean isFinished()
    {
    return ((learningRate <= minimumLearningRate) &&
            (updateRadius <= minimumUpdateRadius));
    }  
  
 /**
  * Dumps the SOMForImages attributes in a String.
  */
  public String toString()
    {
    StringBuffer sb = new StringBuffer(1024);
    // Dump SOM information.
    sb.append("SOM dimensions: "+somWidth+"x"+somHeight+"x"+somDimensions+"\n");
    sb.append("Learning rate: initial:"+format106f(initialLearningRate)+
              " final:"+format106f(minimumLearningRate)+" decay:"+format106f(learningRateDecay)+"\n");
    sb.append("Update radius: initial:"+format106f(initialUpdateRadius)+
              " final:"+format106f(minimumUpdateRadius)+" decay:"+format106f(updateRadiusDecay)+"\n");
    sb.append("Training steps:"+trainingStepsCounter+"\n");
    sb.append("Weights and neuron assignment counts:\n");
    for(int h=0;h<somHeight;h++)
      for(int w=0;w<somWidth;w++)
        {
        // The neuron position.
        sb.append("  h:"+format3i(h)+" w:"+format3i(w)+" weights:[");
        // The neuron weights.
        for(int d=0;d<somDimensions;d++)
          sb.append(format62f(weights[w][h][d])+",");
        sb.deleteCharAt(sb.length()-1); // erase last comma
        sb.append("] ");
        // The neuron assignment count.
        sb.append(format7i(assignmentCount[w][h]));
        sb.append("\n");
        }
    return sb.toString();
    }

 /**
  * Saves the SOMForImages attributes in a text file. Basically this method does
  * the same that toString, but in a format that is easier to parse.
  */
  public void save(String file) throws IOException
    {
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    // Dump SOM information (data only, no labels or text).
    bw.write(somWidth+" "+somHeight+" "+somDimensions); bw.newLine();
    bw.write(initialLearningRate+" "+minimumLearningRate+" "+learningRateDecay); bw.newLine();
    bw.write(initialUpdateRadius+" "+minimumUpdateRadius+" "+updateRadiusDecay); bw.newLine();
    bw.write(""+trainingStepsCounter); bw.newLine();
    for(int h=0;h<somHeight;h++)
      for(int w=0;w<somWidth;w++)
        {
        // The neuron position.
        bw.write(h+" "+w+" ");
        // The neuron weights.
        for(int d=0;d<somDimensions;d++)
          bw.write(weights[w][h][d]+" ");
        // The neuron assignment count.
        bw.write(""+assignmentCount[w][h]);
        bw.newLine();
        }
    bw.close();
    }

 /**
  * Serializes the SOMForImages attributes in a binary file.
  */
  public void serialize(String filename) throws IOException
    {
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
    oos.writeObject(this);
    oos.close();
    }
  
 /*
  * Format an integer using the %3d format.
  */
  private String format3i(int i)
    {
    return String.format("%3d",new Object[]{new Integer(i)});
    }
    
 /*
  * Format an integer using the %7d format.
  */
  private String format7i(int i)
    {
    return String.format("%7d",new Object[]{new Integer(i)});
    }
     
 /*
  * Format a floating point value using the %6.2f format.
  */
  private String format62f(float f)
    {
    return String.format("%6.2f",new Object[]{new Float(f)});
    }
    
 /*
  * Format a floating point value using the %10.6f format.
  */
  private String format106f(float f)
    {
    return String.format("%10.6f",new Object[]{new Float(f)});
    }
     
  
//                                                           #
//    ####  ###### ##### ##### ###### #####   ####          #
//   #    # #        #     #   #      #    # #             #
//   #      #####    #     #   #####  #    #  ####        #
//   #  ### #        #     #   #      #####       #      #
//   #    # #        #     #   #      #   #  #    #     #
//    ####  ######   #     #   ###### #    #  ####     #
//
//
//    ####  ###### ##### ##### ###### #####   ####
//   #      #        #     #   #      #    # #
//    ####  #####    #     #   #####  #    #  ####
//        # #        #     #   #      #####       #
//   #    # #        #     #   #      #   #  #    #
//    ####  ######   #     #   ###### #    #  ####
  
 /**
  * Get the neighborhood type.
  * @return the neighborhood type.
  */
  public int getNeighborhoodType()
    {
    return neighborhoodType;
    }

 /**
  * Set the neighborhood type.
  * @param the neighborhood type.
  */
  public void setNeighborhoodType(int neighborhoodType)
    {
    this.neighborhoodType = neighborhoodType;
    }

 /**
  * Get the dimensions (dimensionality of the weight vectors) of the SOM.
  * @return the dimensions of the SOM.
  */
  public int getSOMDimensions()
    {
    return somDimensions;
    }

 /**
  * Get the height of the SOM.
  * @return the height of the SOM.
  */
  public int getSOMHeight()
    {
    return somHeight;
    }

 /**
  * Get the width of the SOM.
  * @return the width of the SOM.
  */
  public int getSOMWidth()
    {
    return somWidth;
    }

 /**
  * Get the present learning rate.
  * @return the present learning rate.
  */
  public float getLearningRate()
    {
    return learningRate;
    }
  
 /**
  * Get the present update radius.
  * @return the present update radius.
  */
  public float getUpdateRadius()
    {
    return updateRadius;
    }

 /**
  * Get all the weights vectors.
  */ 
  public synchronized float[][][] getWeights()
    {
    return weights;
    }
  
 /*
  * Get the weights vector at a specific point.
  * @param theOne the coordinates of the neuron we're interested on.
  * @return the weights vector at that position or null if the position is not
  *         inside the SOM.
  */
  private float[] getWeightsAt(Point theOne)
    {
    if ((theOne.x >= 0) && (theOne.y >= 0) &&
        (theOne.x < somWidth) && (theOne.y < somHeight))
      return weights[theOne.x][theOne.y];
    else return null;
    }

 /**
  * See if a particular neuron was updated in the last iteration.
  * @param theOne the coordinates for which we want the update status.
  * @return true if the neuron at that position was updated on the last
  *         iteration, false otherwise.
  */
  public boolean neuronWasUpdated(Point theOne)
    {
    return updatedNeurons[theOne.x][theOne.y];
    }

 /**
  * Get the last known winner neurons weights.
  * @return Returns the weights of the last known winner neuron.
  */
  public  float[] getLastWinnerData()
    {
    float[] data = new float[somDimensions];
    data = weights[lastWinningNeuron.x][lastWinningNeuron.y];
    return data;
    }
    
 /**
  * Get the training steps counter.
  * @return the training steps counter.
  */
  public int getTrainingStepsCounter()
    {
    return trainingStepsCounter;
    }
  
 /**
  * Return the position on the image processing task, which will be the number
  * of training steps.
  * @return the position on the image processing task.
  */ 
  public long getPosition()
    {
    return getTrainingStepsCounter();
    }
  
 /**
  * Set the update radius.
  * @param updateRadius the new update radius.
  */
  public void setUpdateRadius(float updateRadius)
    {
    this.updateRadius = updateRadius;
    this.initialUpdateRadius = updateRadius;
    }
  
 /**
  * Set the update radius decay.
  * @param updateRadiusDecay the new update radius decay.
  */
  public void setUpdateRadiusDecay(float updateRadiusDecay)
    {
    this.updateRadiusDecay = updateRadiusDecay;
    }
  
 /**
  * Set the minimum update radius.
  * @param minimumUpdateRadius the new minimum update radius.
  */
  public void setMinimumUpdateRadius(float minimumUpdateRadius)
    {
    this.minimumUpdateRadius = minimumUpdateRadius;
    }

 /**
  * Set the learning rate.
  * @param learningRate the new learning rate.
  */
  public void setLearningRate(float learningRate)
    {
    this.learningRate = learningRate;
    this.initialLearningRate = learningRate;
    }
  
 /**
  * Set the learning rate decay.
  * @param learningRateDecay the new learning rate decay.
  */
  public void setLearningRateDecay(float learningRateDecay)
    {
    this.learningRateDecay = learningRateDecay;
    }

 /**
  * Set the minimum learnin rate.
  * @param minimumLearningRate the new minimum learning rate.
  */
  public void setMinimumLearningRate(float minimumLearningRate)
    {
    this.minimumLearningRate = minimumLearningRate;
    }

 /** 
  * Get the height of the image used as data source.
  * @return the input image height.
  */ 
  public int getImageHeight()
    {
    return imageHeight;
    }
  
 /** 
  * Get the width of the image used as data source.
  * @return the input image width.
  */ 
  public int getImageWidth()
    {
    return imageWidth;
    }
      
 /** 
  * Get the image used as data source.
  * @return the input image.
  */ 
  public PlanarImage getInputImage()
    {
    return inputImage;
    }
      
 /**
  * Set the sampling rate (defines how many pixels will be randomically sampled for 
  * training the network)
  * @param samplingH the new sampling rate.
  */
  public void setSamplingRate(int samplingR)
    {
    this.samplingRate = samplingR;
    }

  } // end class