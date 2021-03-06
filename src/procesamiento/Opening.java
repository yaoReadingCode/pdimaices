package procesamiento;



import jai.display.DisplayTwoSynchronizedImages;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;


/**
 * Comando que ejecuta la operación Opening sobre una imagen binaria
 */
public class Opening extends AbstractImageCommand {
	private float[] kernelMatrix = { 
			0, 0, 1, 1, 1, 0, 0, 
			0, 1, 1, 1,	1, 1, 0, 
			1, 1, 1, 1, 1, 1, 1, 
			1, 1, 1, 1, 1, 1, 1, 
			1, 1, 1, 1, 1, 1, 1, 
			0, 1, 1, 1, 1, 1, 0, 
			0, 0, 1, 1, 1, 0, 0 };
	

	public Opening(PlanarImage image) {
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
		Opening o = new Opening(input);
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
			RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
			// Create the kernel using the array.
			KernelJAI kernel = new KernelJAI(7, 7, kernelMatrix);
			// Create a ParameterBlock with that kernel and image.
			ParameterBlock p = new ParameterBlock();
			p.addSource(getImage());
			p.add(kernel);
			// Erode the image.
			PlanarImage output = JAI.create("erode", p, hints);
			// Now dilate the image with the same kernel.
			p = new ParameterBlock();
			p.addSource(output);
			p.add(kernel);
			output = JAI.create("dilate", p, hints);
			return output;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Opening";
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		// TODO Auto-generated method stub
		
	}

}