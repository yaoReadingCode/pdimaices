package jai.display;

/*
 * Part of the Java Image Processing Cookbook, please see
 * http://www.lac.inpe.br/~rafael.santos/JIPCookbook/index.jsp
 * for information on usage and distribution.
 * Rafael Santos (rafael.santos@lac.inpe.br)
 */
import java.awt.BorderLayout;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;

/**
 * This class uses the DisplayHistogramComponent component in an application.
 */
public class DisplayHistogramApp extends JFrame {
	private DisplayHistogramComponent dh; // An instance of the component.

	/**
	 * The constructor for the class, which will create the user interface and
	 * register the mouse motion events.
	 */
	public DisplayHistogramApp(String name, Histogram histo) {
		setTitle("Histogram of " + name);
		getContentPane().setLayout(new BorderLayout());
		// Add to this ContentPane an instance of DisplayHistogramComponent,
		// which will
		// contain the histogram plot for the first band of the image.
		dh = new DisplayHistogramComponent(histo, 0, "Histogram of " + name);
		getContentPane().add(dh, BorderLayout.CENTER);
		// Set the closing operation so the application is finished.
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack(); // Adjust the frame size using preferred dimensions.
		setVisible(true); // Show the frame.
	}

	/**
	 * The application entry point.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	public static void main(String[] args) {
		PlanarImage image = JAI.create("fileload", args[0]);
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);
		pb.add(null); // The ROI.
		pb.add(1); // Samplings.
		pb.add(1);
		pb.add(new int[] { 256 }); // Num. bins.
		pb.add(new double[] { 0 }); // Min value to be considered.
		pb.add(new double[] { 256 }); // Max value to be considered.
		// Creates the histogram.
		PlanarImage temp = JAI.create("histogram", pb);
		Histogram h = (Histogram) temp.getProperty("histogram");
		new DisplayHistogramApp(args[0], h);
	}

}