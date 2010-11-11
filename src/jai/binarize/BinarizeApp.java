package jai.binarize;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.renderable.ParameterBlock;
import java.util.Hashtable;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * This class demonstrates the use of the binarize operator. It allows the user
 * to set a threshold interactively but can also calculate it automatically.
 */
public class BinarizeApp extends JFrame implements ChangeListener,
		ActionListener {
	private JSlider slider; // A slider to set the threshold
	private JCheckBox interactive; // Will binarization be done interactively?
	private JComboBox modeList; // The threshold method combo box.
	private PlanarImage image; // The original image.
	private DisplayJAI display; // The display component.

	/**
	 * The constructor of the class creates the user interface and registers the
	 * event listeners.
	 * 
	 * @param filename
	 *            the file name of the image (we'll use it on the title bar)
	 * @param image
	 *            the PlanarImage to be rendered/binarized
	 */
	public BinarizeApp(String filename, PlanarImage image) {
		super("Interactive/Automatic binarization of image " + filename);
		this.image = image;
		// Set the content pane's layout
		getContentPane().setLayout(new BorderLayout());
		// Create and set the image display component
		display = new DisplayJAI(image);
		getContentPane().add(new JScrollPane(display), BorderLayout.CENTER);
		// Create a small control panel with a checkbox and the slider
		JPanel controlPanel = new JPanel(new BorderLayout());
		// Create and set the checkbox for interactivity.
		interactive = new JCheckBox("Interactive", false);
		controlPanel.add(interactive, BorderLayout.WEST);
		// Create the slider, and set its labels using a label table
		slider = new JSlider(0, 255, 0);
		Hashtable<Integer, JLabel> sliderLabels = new Hashtable<Integer, JLabel>();
		for (int label = 0; label <= 255; label += 32)
			sliderLabels.put(new Integer(label), new JLabel("" + label));
		sliderLabels.put(new Integer(255), new JLabel("255"));
		slider.setLabelTable(sliderLabels);
		slider.setPaintLabels(true);
		// Registers the change listener for the slider and add it to the
		// control panel
		slider.addChangeListener(this);
		controlPanel.add(slider, BorderLayout.CENTER);
		// Create a combobox with the automatic threshold methods on the
		// Histogram class.
		modeList = new JComboBox(new String[] { "Iterative Bisection",
				"Maximum Entropy", "Maximum Variance", "Minimum Error",
				"Minimum Fuzziness", "Mode (1.0)", "Mode (0.5)", "Mode (0.2)",
				"Mode (0.1)", "P-Tile (0.75)", "P-Tile (0.50)",
				"P-Tile (0.25)", "P-Tile (0.10)" });
		modeList.addActionListener(this);
		controlPanel.add(modeList, BorderLayout.EAST);
		// Add the control panel to the frame
		getContentPane().add(controlPanel, BorderLayout.NORTH);
		// Set the closing operation so the application is finished.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack(); // adjust the frame size using preferred dimensions.
		setVisible(true); // show the frame.
	}

	/**
	 * This method will be executed when the "automatic" button is pushed or the
	 * threshold selection mode is changed.
	 */
	public void actionPerformed(ActionEvent e) {
		// We get the threshold using the image histogram, let's calculate it.
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);
		pb.add(null); // The ROI
		pb.add(1);
		pb.add(1);
		pb.add(new int[] { 256 });
		pb.add(new double[] { 0 });
		pb.add(new double[] { 256 });
		// Calculate the histogram of the image.
		PlanarImage dummyImage = JAI.create("histogram", pb);
		Histogram h = (Histogram) dummyImage.getProperty("histogram");
		// Calculate the thresholds based on the selected method.
		double[] thresholds = null;
		switch (modeList.getSelectedIndex()) {
		case 0: // Iterative Bisection
			thresholds = h.getIterativeThreshold();
			break;
		case 1: // Maximum Entropy
			thresholds = h.getMaxEntropyThreshold();
			break;
		case 2: // Maximum Variance
			thresholds = h.getMaxVarianceThreshold();
			break;
		case 3: // Minimum Error
			thresholds = h.getMinErrorThreshold();
			break;
		case 4: // Minimum Fuzziness
			thresholds = h.getMinFuzzinessThreshold();
			break;
		case 5: // Mode
			thresholds = h.getModeThreshold(1.0);
			break;
		case 6: // Mode
			thresholds = h.getModeThreshold(0.5);
			break;
		case 7: // Mode
			thresholds = h.getModeThreshold(0.2);
			break;
		case 8: // Mode
			thresholds = h.getModeThreshold(0.1);
			break;
		case 9: // "P-Tile"
			thresholds = h.getPTileThreshold(0.75);
			break;
		case 10: // "P-Tile"
			thresholds = h.getPTileThreshold(0.50);
			break;
		case 11: // "P-Tile"
			thresholds = h.getPTileThreshold(0.25);
			break;
		case 12: // "P-Tile"
			thresholds = h.getPTileThreshold(0.10);
			break;
		}
		int threshold = (int) thresholds[0];
		// Change the UI to use the new threshold.
		slider.setValue(threshold);
		binarize(threshold);
	}

	/**
	 * 1* This method will be executed when the slider position changes. 1
	 */
	public void stateChanged(ChangeEvent e) {
		// If interactivity is off and we're still adjusting, return.
		if (slider.getValueIsAdjusting() && !interactive.isSelected())
			return;
		// Gets the threshold value.
		int threshold = slider.getValue();
		// Modify/display the image.
		binarize(threshold);
	}

	/*
	 * 1 This methods applies the binarization and display the binarized image.
	 * 1
	 */
	private void binarize(int threshold) {
		// Binarizes the original image.
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);
		pb.add(1.0 * threshold);
		// Creates a new, thresholded image and uses it on the DisplayJAI
		// component
		PlanarImage thresholdedImage = JAI.create("binarize", pb);
		display.set(thresholdedImage);
	}

	/**
	 * 1* The application entry point. 1* @param args the command line
	 * arguments. 1
	 */
	public static void main(String[] args) {
		// We need one argument: the image filename.
		if (args.length != 1) {
			System.err.println("Usage: java operators.point.BinarizeApp image");
			System.exit(0);
		}
		// Read the image.
		PlanarImage image = JAI.create("fileload", args[0]);
		// Convert color images to one-band using weights for the three bands if
		// required.
		if (image.getNumBands() == 3) {
			double[][] matrix = { { 0.114, 0.587, 0.299, 0 } };
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(image);
			pb.add(matrix);
			image = JAI.create("bandcombine", pb, null);
		}
		// If at this point the image has more than one band (e.g if
		// it was a 2- or 4+- band image), then bail out.
		if (image.getNumBands() != 1) {
			System.err.println("Cannot work with " + image.getNumBands()
					+ "-banded images, sorry");
			System.exit(0);
		}
		// Create the GUI and start the application.
		new BinarizeApp(args[0], image);
	}

}