/*
 * Created on Jun 5, 2005
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
package algorithms.templatematching;

import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import algorithms.common.ImageProcessingTask;

/**
 * This class demonstrates an implementation of a basic Template Matching
 * algorithm, using RandomIter(ators) to access the data. This class inherits from
 * ImageProcessingTask.
 * IMPORTANT: This implementation does not lock access to the data while the
 * template matching is being done.
 */
public class TemplateMatchingWithIterators extends ImageProcessingTask
  {
  // The input image, input template and output images.
  private PlanarImage input;  
  private PlanarImage template;
  private TiledImage output;
  // Dimensions on the image and mask.
  private int imageWidth,imageHeight,templateWidth,templateHeight;
  private int hotSpotX,hotSpotY;
  // Flags and counters.
  private boolean finished = false;
  private int position;
  
 /**
  * The constructor for this class, which gets the input images and set some
  * parameters.
  * @param input the input image.
  * @param mask the template matching mask.
  */
  public TemplateMatchingWithIterators(PlanarImage input,PlanarImage template)
    {
    this.input = input;
    this.template = template;
    // Get some info about the input and template images.
    imageWidth = input.getWidth();
    imageHeight = input.getHeight();
    templateWidth = template.getWidth();
    templateHeight = template.getHeight();
    hotSpotX = templateWidth/2;
    hotSpotY = templateHeight/2;
    // Create an output image with the same features as the input one.
    output = new TiledImage(0,0,imageWidth,imageHeight,0,0,
                            input.getSampleModel(),input.getColorModel());
    finished = false;
    position = 0;
    }

 /**
  * This method returns a measure of length for the algorithm. We will consider the
  * number of processed lines.
  */
  public long getSize()
    {
    return imageHeight-2*hotSpotY;
    }

 /**
  * This method returns a measure of the progress of the algorithm.
  */
  public long getPosition()
    {
    return position;
    }
  
/**
 * This method is the main driver of the algorithm, it will perform the template
 * matching.
 */  
  public void run()
    {
    position = 0;
    // Get an iterator for the input image and a array to hold the pixels.
    RandomIter inputIterator = RandomIterFactory.create(input,null);
    int[] pixelI = new int[1];
    // Get an iterator for the mask image and a array to hold the pixels.
    RandomIter templateIterator = RandomIterFactory.create(template,null);
    int[] pixelT = new int[1];
    // Create a WritableRaster for the output.
    WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_BYTE,
                                                             imageWidth,imageHeight,1,
                                                             new Point(0,0));
    // define some useful values.
    int maxValue = templateWidth*templateHeight;
    // Scan all pixels corresponding to the output image.
    for(int yi=hotSpotY;yi<imageHeight-hotSpotY;yi++)
      {
      for(int xi=hotSpotX;xi<imageWidth-hotSpotX;xi++)
        {
        int result = 0;
        // Get the sum of the absolute difference between the pixels on
        // the image and on the template.
        for(int xt=0;xt<templateWidth;xt++)
          for(int yt=0;yt<templateHeight;yt++)
            {
            inputIterator.getPixel(xi+xt-hotSpotX,yi+yt-hotSpotY,pixelI);
            templateIterator.getPixel(xt,yt,pixelT);
            result += Math.abs(pixelT[0]-pixelI[0]);
            } 
        // Normalize the result.
        result = result/maxValue;
        // Set it on the output raster.
        raster.setSample(xi,yi,0,result);
        }
      // Set the output raster on the output image. Do it every line, so we can
      // get some partial results.
      output.setData(raster);
      position++;
      }
    finished = true;
    }

 /**
  * This method tells whether the algorithm has finished.
  */
  public boolean isFinished()
    {
    return finished;
    }

 /**
  * This method returns the output image.
  * @return the template matching resulting image.
  */
  public TiledImage getOutput()
    {
    return output;
    }
  
  }