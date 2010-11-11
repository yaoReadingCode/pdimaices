package procesamiento;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

public class GradientMagnitud extends AbstractImageCommand {

	public GradientMagnitud(PlanarImage image) {
		super(image);
	}

	public PlanarImage execute() {
		if (getImage() != null){
			
			KernelJAI kern_h = KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
		    KernelJAI kern_v = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;
		    
		    RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();

		    // Create the Gradient operation.
		    PlanarImage im1 = JAI.create("gradientmagnitude", getImage(), kern_h, kern_v);
		    //PlanarImage im1 = JAI.create("convolve", getImage(), kern_h, null);
		    //im1 = JAI.create("gradientmagnitude", im1, kern_h, kern_v);
		    //return im1;
		    
		    ParameterBlock pb = new ParameterBlock();
		    pb.addSource(im1);
		    pb.addSource(getImage());
		    
			PlanarImage output = JAI.create("subtract", pb, hints);
			return output;

		}
		return null;
	}

	public String getCommandName() {
		return "GradientMagnitud";
	}

	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
