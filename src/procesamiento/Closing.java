package procesamiento;

/*
 * Part of the Java Image Processing Cookbook, please see
 * http://www.lac.inpe.br/~rafael.santos/JIPCookbook/index.jsp
 * for information on usage and distribution.
 * Rafael Santos (rafael.santos@lac.inpe.br)
 */

import jai.binarize.DisplayTwoSynchronizedImages;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;


/**
 * Comando que realiza la operación Closing sobre una imagen binaria
 */
public class Closing extends AbstractImageCommand {
	private float[] kernelMatrix = { 
		0, 0, 1, 1, 1, 0, 0, 
		0, 1, 1, 1,	1, 1, 0, 
		1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 
		0, 1, 1, 1, 1, 1, 0, 
		0, 0, 1, 1, 1, 0, 0 };

	/**
	 * Constructor
	 * @param image Imagen a procesar
	 */
	public Closing(PlanarImage image) {
		super(image);
	}

	/**
	 * The application entry point.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	public static void main(String[] args) {
		// We need one argument - the image file name.
		if (args.length != 1) {
			System.err.println("Usage: java jai.operators.area.Open image");
			System.exit(0);
		}
		PlanarImage input = JAI.create("fileload", args[0]);
		Closing o = new Closing(input);
		PlanarImage output = o.execute();
		// Read the image.
		// Create a JFrame for displaying the results.
		JFrame frame = new JFrame();
		frame.setTitle("Opening of the image " + args[0]);
		// Add to the JFrame's ContentPane an instance of
		// DisplayTwoSynchronizedImages, which will
		// contain the original and processed image.
		frame.getContentPane().add(
				new DisplayTwoSynchronizedImages(input, output));
		// Set the closing operation so the application is finished.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack(); // adjust the frame size using preferred dimensions.
		frame.setVisible(true); // show the frame.
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#execute()
	 */
	public PlanarImage execute() {
		if (getImage() != null) {
			// The kernels for the operations.

			RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
			// Create the kernel using the array.
			KernelJAI kernel = new KernelJAI(7, 7, kernelMatrix);
			// Create a ParameterBlock with that kernel and image.
			ParameterBlock p = new ParameterBlock();
			p.addSource(getImage());
			p.add(kernel);
			// Erode the image.
			PlanarImage output = JAI.create("dilate", p, hints);
			// Now dilate the image with the same kernel.
			p = new ParameterBlock();
			p.addSource(output);
			p.add(kernel);
			output = JAI.create("erode", p, hints);
			return output;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Closing";
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		// TODO Auto-generated method stub
		
	}

}