package jai.binarize;

/*
 * Part of the Java Image Processing Cookbook, please see
 * http://www.lac.inpe.br/~rafael.santos/JIPCookbook/index.jsp
 * for information on usage and distribution.
 * Rafael Santos (rafael.santos@lac.inpe.br)
 */

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;

/**
 * This class demonstrates the use of the binarize operator. An input image is
 * binarized with a fixed threshold value.
 */
public class Binarize {
	/**
	 * The application entry point.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	public static void main(String[] args) {
		// We need one argument: the image filename.
		if (args.length != 1) {
			System.err.println("Usage: java operators.point.Binarize image");
			System.exit(0);
		}
		// Read the image.
		PlanarImage input = JAI.create("fileload", args[0]);
		// Binarize the image.
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(input);
		pb.add(100.0);
		PlanarImage thresholdedImage = JAI.create("binarize", pb);
		// Create a JFrame for displaying the results.
		JFrame frame = new JFrame();
		frame.setTitle("Invert image " + args[0]);
		// Add to the JFrame's ContentPane an instance of
		// DisplayTwoSynchronizedImages, which
		// will contain the original and processed image.
		frame.getContentPane().add(
				new DisplayTwoSynchronizedImages(input, thresholdedImage));
		// Set the closing operation so the application is finished.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack(); // Adjust the frame size using preferred dimensions.
		frame.setVisible(true); // Show the frame.
	}

}