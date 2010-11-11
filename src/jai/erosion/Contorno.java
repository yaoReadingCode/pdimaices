package jai.erosion;

/*
 * Part of the Java Image Processing Cookbook, please see
 * http://www.lac.inpe.br/~rafael.santos/JIPCookbook/index.jsp
 * for information on usage and distribution.
 * Rafael Santos (rafael.santos@lac.inpe.br)
 */

import jai.binarize.DisplayTwoSynchronizedImages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import procesamiento.ImageUtil;




/**
 * This class demonstrates the use of the erode and dilate (mathematical
 * morphology) operators.
 */
public class Contorno extends JFrame implements ActionListener {
	// The original image and the one that will be processed.
	private PlanarImage image, processedImage;
	// The display component.
	private DisplayTwoSynchronizedImages display;
	// Some graphical user interface components.
	private JRadioButton erode, dilate;
	private Font font = new Font("default", 0, 28);
	private JButton kern1Button, kern2Button, kern3Button, kern4Button,
			kern5Button, kern6Button, kern7Button;
	// The kernels for the operations.
	private static int kernelW = 7;
	private static int kernelH = 7;
	private static float[] kernel1Matrix = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	private static float[] kernel2Matrix = { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0,
			0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 };
	private static float[] kernel3Matrix = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private static float[] kernel4Matrix = { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0,
			0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
			0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1 };
	private static float[] kernel5Matrix = { 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0,
			0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1,
			0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1 };
	private static float[] kernel6Matrix = { 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1,
			1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0 };
	private static float[] kernel7Matrix = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1,
			0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	/**
	 * The constructor for this class, which will set its user interface.
	 */
	public Contorno(PlanarImage i) {
		super("Mathematical morphology operators");
		image = i;
		// We need a temporary image for the display. Let's create a constant
		// one.
		ParameterBlock p = new ParameterBlock();
		p.add((float) image.getWidth()); // Width and height must be floats...
		p.add((float) image.getHeight());
		Byte[] bandValues = new Byte[image.getNumBands()];
		for (int band = 0; band < bandValues.length; band++)
			bandValues[band] = new Byte((byte) 1);
		p.add(bandValues);
		processedImage = JAI.create("constant", p, null);
		// Create an instance of DisplayTwoSynchronizedImages to hold both
		// images.
		display = new DisplayTwoSynchronizedImages(image, processedImage);
		getContentPane().add(display);
		// Let's create the UI. First a panel for the controls.
		JPanel controlPanel = new JPanel(new GridLayout(1, 7));
		// Then, two radio buttons for the operations.
		erode = new JRadioButton("Erode");
		erode.setFont(font);
		erode.setSelected(true);
		dilate = new JRadioButton("Dilate");
		dilate.setFont(font);
		ButtonGroup group = new ButtonGroup();
		group.add(erode);
		group.add(dilate);
		controlPanel.add(erode);
		controlPanel.add(dilate);
		// Five icon buttons with the operators.
		kern1Button = new JButton(new ImageIcon(createIcon(kernel1Matrix,
				kernelW, kernelH)));
		controlPanel.add(kern1Button);
		kern1Button.addActionListener(this);
		kern2Button = new JButton(new ImageIcon(createIcon(kernel2Matrix,
				kernelW, kernelH)));
		controlPanel.add(kern2Button);
		kern2Button.addActionListener(this);
		kern3Button = new JButton(new ImageIcon(createIcon(kernel3Matrix,
				kernelW, kernelH)));
		controlPanel.add(kern3Button);
		kern3Button.addActionListener(this);
		kern4Button = new JButton(new ImageIcon(createIcon(kernel4Matrix,
				kernelW, kernelH)));
		controlPanel.add(kern4Button);
		kern4Button.addActionListener(this);
		kern5Button = new JButton(new ImageIcon(createIcon(kernel5Matrix,
				kernelW, kernelH)));
		controlPanel.add(kern5Button);
		kern5Button.addActionListener(this);
		kern6Button = new JButton(new ImageIcon(createIcon(kernel6Matrix,
				kernelW, kernelH)));
		controlPanel.add(kern6Button);
		kern6Button.addActionListener(this);
		kern7Button = new JButton(new ImageIcon(createIcon(kernel7Matrix,
				kernelW, kernelH)));
		controlPanel.add(kern7Button);
		kern7Button.addActionListener(this);
		// Add the control panel to the UI.
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		// Set the closing operation so the application is finished.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack(); // Adjust the frame size using preferred dimensions.
		setVisible(true); // Show the frame.
	}

	/**
	 * This method creates an Image (icon) based on a kernel. This image will be
	 * used in the application GUI's button.
	 */
	private static Image createIcon(float[] kernel, int kw, int kh) {
		int scale = 6;
		int[] whites = new int[scale * scale];
		Arrays.fill(whites, 1); // Whites in a binary image are ones.
		// Create a binary image for that kernel (scaled).
		BufferedImage i = new BufferedImage(kw * scale, kh * scale,
				BufferedImage.TYPE_BYTE_BINARY);
		// We need its raster to set the pixels' values.
		WritableRaster raster = i.getRaster();
		for (int h = 0; h < kernelH; h++)
			for (int w = 0; w < kernelW; w++)
				if (kernel[h * kw + w] == 0)
					raster.setSamples(w * scale, h * scale, scale, scale, 0,
							whites);
		return i;
	}

	/**
	 * This method will be called when the user selects a button.
	 */
	public void actionPerformed(ActionEvent e) {
		PlanarImage grayImage = ImageUtil.binarize(image, new Color(0, 0, 0),
				new Color(255, 255, 255));
		/*
		 * if (grayImage.getNumBands() == 3) { double[][] matrix = { { 0.114,
		 * 0.587, 0.299, 0 } }; ParameterBlock pb = new ParameterBlock();
		 * pb.addSource(grayImage); pb.add(matrix); grayImage =
		 * JAI.create("bandcombine", pb, null); }
		 */
		ParameterBlock p = new ParameterBlock();
		p.addSource(grayImage);
		if (e.getSource() == kern1Button)
			p.add(new KernelJAI(kernelW, kernelH, 3, 3, kernel1Matrix));
		else if (e.getSource() == kern2Button)
			p.add(new KernelJAI(kernelW, kernelH, 3, 3, kernel2Matrix));
		else if (e.getSource() == kern3Button)
			p.add(new KernelJAI(kernelW, kernelH, 3, 3, kernel3Matrix));
		else if (e.getSource() == kern4Button)
			p.add(new KernelJAI(kernelW, kernelH, 3, 3, kernel4Matrix));
		else if (e.getSource() == kern5Button)
			p.add(new KernelJAI(kernelW, kernelH, 3, 3, kernel5Matrix));
		else if (e.getSource() == kern6Button)
			p.add(new KernelJAI(kernelW, kernelH, 3, 3, kernel6Matrix));
		else if (e.getSource() == kern7Button)
			p.add(new KernelJAI(kernelW, kernelH, 3, 3, kernel7Matrix));
		PlanarImage image2;
		if (erode.isSelected())
			image2 = JAI.create("erode", p, null);
		else
			image2 = JAI.create("dilate", p, null);

		ParameterBlock pb = new ParameterBlock();
		if (erode.isSelected()) {
			pb.addSource(grayImage);
			pb.addSource(image2);
		} else {
			pb.addSource(image2);
			pb.addSource(grayImage);
		}

		processedImage = JAI.create("subtract", pb);

		display.setImage2(processedImage);
	}

	/**
	 * The application entry point.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	public static void main(String[] args) {
		// We need one argument: the image filename.
		if (args.length != 1) {
			System.err.println("Usage: java jai.operators.area.MMorph image");
			System.exit(0);
		}
		// Read the image.
		PlanarImage input = JAI.create("fileload", args[0]);
		// Create an instance of this class to process and display the results.
		new Contorno(input);
	}
}