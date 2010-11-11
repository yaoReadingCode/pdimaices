/*
 * Part of the Java Image Processing Cookbook, please see
 * http://www.lac.inpe.br/~rafael.santos/JIPCookbook/index.jsp
 * for information on usage and distribution.
 * Rafael Santos (rafael.santos@lac.inpe.br)
 */
package jai.histogram;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;

/**
 * This class uses the DisplayHistogramComponent component to display the
 * histograms of the bands in a color image.
 */
public class DisplayRGBHistogramApp {
	/**
	 * The application entry point.
	 * 
	 * @param args
	 *            the command line arguments (a color image file name).
	 */
	public static void main(String[] args) {
		PlanarImage image = JAI.create("fileload", "maiz.tif");
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);
		pb.add(null); // The ROI.
		pb.add(1); // Samplings.
		pb.add(1);
		pb.add(new int[] { 128 }); // Num. bins.
		pb.add(new double[] { 0 }); // Min value to be considered.
		pb.add(new double[] { 256 }); // Max value to be considered.
		// Creates the histogram.
		PlanarImage temp = JAI.create("histogram", pb);
		Histogram h = (Histogram) temp.getProperty("histogram");
		// Creates the GUI to display three histogram components.
		JFrame frame = new JFrame("R,G,B Histograms");
		Container cp = frame.getContentPane();
		cp.setLayout(new GridLayout(3, 1));
		DisplayHistogramComponent cRed = new DisplayHistogramComponent(h, 0,"Red");
		DisplayHistogramComponent cGreen = new DisplayHistogramComponent(h, 1,"Green");
		DisplayHistogramComponent cBlue = new DisplayHistogramComponent(h, 2,"Blue");
		cRed.setBarColor(Color.RED);
		cRed.setMarksColor(Color.WHITE);
		cGreen.setBarColor(Color.GREEN);
		cGreen.setMarksColor(Color.WHITE);
		cBlue.setBarColor(Color.BLUE);
		cBlue.setMarksColor(Color.WHITE);
		// Use the same scale on the y-axis.
		int max = Math.max(cRed.getMaxCount(), Math.max(cGreen.getMaxCount(),
				cBlue.getMaxCount()));
		cRed.setMaxCount(max);
		cGreen.setMaxCount(max);
		cBlue.setMaxCount(max);
		cp.add(cRed);
		cp.add(cGreen);
		cp.add(cBlue);
		// Set the closing operation so the application is finished.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack(); // Adjust the frame size using preferred dimensions.
		frame.setVisible(true); // Show the frame.
	}

}