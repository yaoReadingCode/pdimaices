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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.Timer;

/**
 * This applications shows a rather complex usage of the KMeansImageClustering class, 
 * creating a user interface that allows the user to select some clustering 
 * parameters and rerun the cluster task. The original and clustered image are 
 * shown, side-by-side and synchronized. Input data is read from a file and at 
 * the end of the clustering process the data is saved in another file.
 */
public class DemoKMeansImageClustering extends JFrame implements ActionListener
  {
  // The input and output images.
  private PlanarImage input,output;
  // The display for the input and output images.
  private jai.binarize.DisplayTwoSynchronizedImages display;
  // The start (clustering) button.
  private JButton start;
  // A JSlider for the number of clusters.
  private JSlider numClusters;
  // A JSlider for the maximum number of iterations.
  private JSlider maxIterationsSlider;
  // The possible values for the maximum number of iterations.
  private int[] maxIterationsValues = {2,5,10,20,50,100,200,500,1000};
  // A JSlider for the epsilon value.
  private JSlider epsilonSlider;
  // The possible values for the epsilon.
  private int[] epsilonValues = {1,5,10,50,100,200,500,1000};
  // A smaller font for the sliders.
  private Font labelsFont = new Font("Dialog",0,9);
  // A combo box with the initialization methods.
  private JComboBox initMethods;
  // A progress bar to indicate the state of the task.
  private JProgressBar progressBar;
  // A JLabel with information about the process.
  private JLabel infoLabel;
  // A timer, which will act on the user interface.
  private Timer timer;
  // The clustering task instance.
  private KMeansImageClustering clusterer;
  // The output image file name.
  private String ofile;
  
 /**
  * The constructor for the application, which will set its user interface.
  * @param ifile the input file name.
  * @param ofile the output file name.
  */
  public DemoKMeansImageClustering(String ifile,String ofile)
    {
    super("K-Means Clustering Application");
    // Read the input image from the file.
    input = JAI.create("fileload", ifile);
    this.ofile = ofile;
    // Create a compatible (empty) output image.
    output = new TiledImage(input.getMinX(),input.getMinY(),
                            input.getWidth(),input.getHeight(),
                            input.getMinX(),input.getMinY(),
                            input.getSampleModel(),input.getColorModel());
    // Create an instance of DisplayTwoSynchronizedImages and add it to the
    // center of the JFrame.
    display = new jai.binarize.DisplayTwoSynchronizedImages(input,output);
    getContentPane().add(display,BorderLayout.CENTER);
    // Let's create the start button and set its maximum size so it will appear
    // wide enough in the control panel.
    start = new JButton("Start Clustering");
    start.addActionListener(this);
    start.setAlignmentX(Component.CENTER_ALIGNMENT);
    start.setMaximumSize(new Dimension(250,25));
    // A JSlider for the number of clusters.
    numClusters = new JSlider(2,50,8);
    Hashtable<Integer,JLabel> labels = new Hashtable<Integer,JLabel>();
    for(int label=2;label<=50;label+=8)
      {
      JLabel aLabel = new JLabel(""+label);
      aLabel.setFont(labelsFont);
      labels.put(new Integer(label),aLabel);
      }
    numClusters.setMajorTickSpacing(8);
    numClusters.setMinorTickSpacing(1);
    numClusters.setSnapToTicks(true);
    numClusters.setPaintTicks(true);
    numClusters.setLabelTable(labels);
    numClusters.setPaintLabels(true);
    numClusters.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE),"Number of clusters"));
    // A JSlider for the maximum number of iterations. This JSlider will have
    // sort of a log scale. Real values will be from 2 to 1000.
    maxIterationsSlider = new JSlider(0,8,5);
    labels = new Hashtable<Integer,JLabel>();
    for(int label=0;label<9;label++)
      {
      JLabel aLabel = new JLabel(""+maxIterationsValues[label]);
      aLabel.setFont(labelsFont);
      labels.put(new Integer(label),aLabel);
      }
    maxIterationsSlider.setMajorTickSpacing(1);
    maxIterationsSlider.setSnapToTicks(true);
    maxIterationsSlider.setPaintTicks(true);
    maxIterationsSlider.setLabelTable(labels);
    maxIterationsSlider.setPaintLabels(true);
    maxIterationsSlider.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE),"Max. iterations (approx.)"));
    // A JSlider for the epsilon value. This JSlider will have
    // sort of a log scale. Real values will be from 1 to 1000.
    epsilonSlider = new JSlider(0,7,3);
    labels = new Hashtable<Integer,JLabel>();
    for(int label=0;label<8;label++)
      {
      JLabel aLabel = new JLabel(""+epsilonValues[label]);
      aLabel.setFont(labelsFont);
      labels.put(new Integer(label),aLabel);
      }
    epsilonSlider.setMajorTickSpacing(1);
    epsilonSlider.setSnapToTicks(true);
    epsilonSlider.setPaintTicks(true);
    epsilonSlider.setLabelTable(labels);
    epsilonSlider.setPaintLabels(true);
    epsilonSlider.setBorder(
          BorderFactory.createTitledBorder(
              BorderFactory.createLineBorder(Color.BLUE),"Epsilon"));
    // The initialization methods combo box.
    String[] initMethodsLabels = { "Evenly spaced values",
                                   "Random data samples",
                                   "Random values"};
    initMethods = new JComboBox(initMethodsLabels);
    initMethods.setMaximumSize(new Dimension(250,50));
    initMethods.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE),"Initialization method"));
    // A JProgressBar for the progress bar (duh!).
    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    // The information label.
    infoLabel = new JLabel(" ");
    infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    infoLabel.setMaximumSize(new Dimension(200,25));
    // Add the control components to the control panel.
    Box controlPanel = Box.createVerticalBox();
    controlPanel.setPreferredSize(new Dimension(250,350));
    controlPanel.add(numClusters);
    controlPanel.add(Box.createRigidArea(new Dimension(0,10)));
    controlPanel.add(maxIterationsSlider);
    controlPanel.add(Box.createRigidArea(new Dimension(0,10)));
    controlPanel.add(epsilonSlider);
    controlPanel.add(Box.createRigidArea(new Dimension(0,10)));
    controlPanel.add(initMethods);
    controlPanel.add(Box.createRigidArea(new Dimension(0,10)));
    controlPanel.add(start);
    controlPanel.add(Box.createRigidArea(new Dimension(0,10)));
    controlPanel.add(progressBar);
    controlPanel.add(infoLabel);
    // Add the control panel to the content pane.
    getContentPane().add(controlPanel,BorderLayout.EAST);
    // Create a timer monitor, which will cause an ActionEvent every second.
    timer = new Timer(1000,this);
    // Set the closing operation so the application is finished.
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack(); // Adjust the frame size using preferred dimensions.
    setVisible(true); // Show the frame.
    }

 /**
  * This method will be called when an action event occurs. In this class, it
  * either means that the user has clicked on the Start button or that the
  * timer has fired.
  */
  public void actionPerformed(ActionEvent e)
    {
    // If the user clicks on the start button...
    if (e.getSource() == start)
      {
      // Start the monitor, which will cause another action event every second.
      timer.start();
      // Gets the clustering task arguments from the user interface.
      char iMethod = "SDR".charAt(initMethods.getSelectedIndex());
      int nClusters = numClusters.getValue();
      int maxIter = maxIterationsValues[maxIterationsSlider.getValue()];
      int epsilon = epsilonValues[epsilonSlider.getValue()];
      // Create the clustering task and starts it.
      clusterer = new KMeansImageClustering(input,nClusters,maxIter,epsilon,iMethod);
      progressBar.setMaximum(100); // maximum value is 100 percent.
      // Start the clustering task.
      clusterer.start();
      // Turn "off" the Start button.
      start.setEnabled(false);
      start.setText("Clustering, please wait...");
      // Turn "off" the cluster number selection and max iterations widget thingies.
      numClusters.setEnabled(false);
      maxIterationsSlider.setEnabled(false);
      epsilonSlider.setEnabled(false);
      initMethods.setEnabled(false);
      }
    // This will happen when the timer fires.
    else if (e.getSource() == timer)
      {
      // Change the second image on the display to be the result image so far.
      display.setImage2(clusterer.getOutput());
      // Which percentage of the task is completed ?
      int percentage = (int)(100*clusterer.getPosition()/clusterer.getSize());
      progressBar.setValue(percentage);
      // Update the information about the task.
      infoLabel.setText(clusterer.getInfo());
      // If the task has finished...
      if (clusterer.isFinished())
        {
        // Enable the start button and the cluster number/max iterations
        // selection slider.
        start.setEnabled(true);
        start.setText("Start clustering");
                      numClusters.setEnabled(true);
                      maxIterationsSlider.setEnabled(true);
                      epsilonSlider.setEnabled(true);
                      initMethods.setEnabled(true);
        // Stops the monitor.
        timer.stop();
        // Save the image on a file.
        JAI.create("filestore",clusterer.getOutput(),ofile,"TIFF");
        }
      }
    }

 /**
  * The application entry point, which will need an input image file name and
  * an output image file name.
  */
  public static void main(String[] args)
    {
    new DemoKMeansImageClustering(args[0],args[1]);
    }
  
  }