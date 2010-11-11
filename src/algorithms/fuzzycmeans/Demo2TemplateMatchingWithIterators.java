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
package algorithms.fuzzycmeans;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import algorithms.templatematching.TemplateMatchingWithIterators;

/**
 * This class demonstrates the usage of the TemplateMatchingWithIterators class,
 * running two instances concurrently, each in its thread.
 */
public class Demo2TemplateMatchingWithIterators
  {
  /**
   * This is the application's entry point.
   * @param args the input image and template
   */
  public static void main(String[] args)
    {
    // We need four arguments: the image and template filenames for the first task
    // and the image and template filenames for the second task.
    if (args.length != 4)
      {
      System.err.println("Usage: java algorithms.common.templatematching.Demo2TemplateMatchingWithIterators "+
                         "image1 template1 image2 template2");
      System.exit(0);
      }
    // Open the images and templates 
    PlanarImage image1 = JAI.create("fileload", args[0]);
    PlanarImage template1 = JAI.create("fileload", args[1]);
    PlanarImage image2 = JAI.create("fileload", args[2]);
    PlanarImage template2 = JAI.create("fileload", args[3]);
    // Create the TemplateMatchingWithIterators instances.

    TemplateMatchingWithIterators tm1 = new TemplateMatchingWithIterators(image1,template1);
    TemplateMatchingWithIterators tm2 = new TemplateMatchingWithIterators(image2,template2);
    // Run them until they both finish.
    tm1.start();
    tm2.start();
    while(!tm1.isFinished() || !tm2.isFinished())
      {
      // Uncomment the following lines if you're a big fan of scrolling messages.
      // System.out.println("First task:"+tm1.getPosition()+" of "+tm1.getSize()+"  "+
      //                    "Second task:"+tm2.getPosition()+" of "+tm2.getSize());   
      }
    // Just dump the results in local files. Assume they are tiffs.
    JAI.create("filestore",tm1.getOutput(),"TM1_"+args[0],"TIFF");
    JAI.create("filestore",tm2.getOutput(),"TM2_"+args[0],"TIFF");
    }
  }