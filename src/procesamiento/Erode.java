package procesamiento;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

/**
 * Comando que ejecuta la operación Erosión sobre una imagen binaria
 * @author oscar
 *
 */
public class Erode extends AbstractImageCommand {
	private float[] kernelMatrix = { 
		0, 0, 1, 1, 1, 0, 0, 
		0, 1, 1, 1,	1, 1, 0, 
		1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 
		1, 1, 1, 1, 1, 1, 1, 
		0, 1, 1, 1, 1, 1, 0, 
		0, 0, 1, 1, 1, 0, 0 };


	public Erode(PlanarImage image) {
		super(image);

	}
	
	public Erode(PlanarImage image, float[] kernel) {
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
		return JAI.create("erode", p, hints);
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Erosión";
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		// TODO Auto-generated method stub
		
	}

}
