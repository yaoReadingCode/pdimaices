package procesamiento;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

/**
 * Comando que realiza la operación de dilatación sobre una imagen binaria
 * @author oscar
 *
 */
public class Dilate extends AbstractImageCommand {
	private float[] kernelMatrix = { 
		0, 0, 1, 1, 1, 0, 0, 
		0, 1, 1, 1,	1, 1, 0, 
		1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 
		0, 1, 1, 1, 1, 1, 0, 
		0, 0, 1, 1, 1, 0, 0 };

	public Dilate(PlanarImage image) {
		super(image);
	}

	public Dilate(PlanarImage image, float[] kernel) {
		super(image);
		this.kernelMatrix = kernel;
	}

	public float[] getKernel() {
		return kernelMatrix;
	}

	public void setKernel(float[] kernel) {
		this.kernelMatrix = kernel;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#execute()
	 */
	public PlanarImage execute() {
		RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
		// Create the kernel using the array.
		KernelJAI k = new KernelJAI(7, 7, kernelMatrix);
		// Create a ParameterBlock with that kernel and image.
		ParameterBlock p = new ParameterBlock();
		p.addSource(getImage());
		p.add(k);
		return JAI.create("dilate", p, hints);
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Dilatación";
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
				
	}

}
