package jai.histogram;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;

public class Histrogram {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 PlanarImage image = JAI.create("fileload", "maiz.tif");
		 int[] bins = {256, 256, 256};             // The number of bins.
	     double[] low = {0.0D, 0.0D, 0.0D};        // The low value.
	     double[] high = {256.0D, 256.0D, 256.0D}; // The high value.

	     // Construct the Histogram object.
	     Histogram h = new Histogram(bins, low, high);

	     // Create the parameter block.
	     ParameterBlock pb = new ParameterBlock();
	     pb.addSource(image);               // Specify the source image
	     pb.add(null);                      // No ROI
	     pb.add(1);                         // periods
	     pb.add(1);                         // Sampling
	     pb.add(bins);
	     pb.add(low);
	     pb.add(high);
	     pb.add(h);                      // Specify the histogram

	     // Perform the histogram operation.
	     PlanarImage dst = (PlanarImage)JAI.create("histogram", pb, null);

	     // Retrieve the histogram data.
	     h = (Histogram) dst.getProperty("histogram");

	     JFrame frame = new JFrame("R,G,B Histograms");
			Container cp = frame.getContentPane();
			cp.setLayout(new GridLayout(3, 1));
			DisplayHistogramComponent cRed = new DisplayHistogramComponent(h, 0,
					"Red");
			DisplayHistogramComponent cGreen = new DisplayHistogramComponent(h, 1,
					"Green");
			DisplayHistogramComponent cBlue = new DisplayHistogramComponent(h, 2,
					"Blue");
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
