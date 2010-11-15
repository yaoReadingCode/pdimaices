package procesamiento;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

public class PreProcess extends AbstractImageCommand {

	public PreProcess(PlanarImage image) {
		super(image);
	}

	public PlanarImage execute() {
		if (getImage() != null) {
			PlanarImage input = getImage();


			// Let's get rid of some annoying noise and small regions. We will
			// do an closing
			// then an opening on the image.
			// The kernels for the operations.
		    float[] kernelMatrix =  { 
		    		0, 0, 0, 0, 0, 
                    0, 1, 1, 1, 0, 
                    0, 1, 1, 1, 0, 
                    0, 1, 1, 1, 0, 
                    0, 0, 0, 0, 0};
			// Create the kernel using the array.
			KernelJAI kernel = new KernelJAI(5, 5, kernelMatrix);
			// Create a ParameterBlock with that kernel and image.
			ParameterBlock p = new ParameterBlock();
			p.addSource(input);
			p.add(kernel);
			// Dilate the image.
			input = JAI.create("dilate", p, null);
			// Now erode the image with the same kernel.
			p = new ParameterBlock();
			p.addSource(input);
			p.add(kernel);
			input = JAI.create("erode", p, null);
			// Do the opening, which is a erode+dilate.
			p = new ParameterBlock();
			p.addSource(input);
			p.add(kernel);
			input = JAI.create("erode", p, null);
			p = new ParameterBlock();
			p.addSource(input);
			p.add(kernel);
			input = JAI.create("dilate", p, null);
			return input;
		}
		return null;
	}

	public String getCommandName() {
		return "PreProcess";
	}

	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
