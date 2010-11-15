package procesamiento;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

public class SobelFilter extends AbstractImageCommand {

	public SobelFilter(PlanarImage image) {
		super(image);
		// TODO Auto-generated constructor stub
	}

	public PlanarImage execute() {
		if (getImage() != null){
			/*
			KernelJAI kern_h = KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
		    KernelJAI kern_v = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;

		    // Create the Gradient operation.
		    PlanarImage im1 = JAI.create("convolve", getImage(), kern_h, null);
		    PlanarImage im2 = JAI.create("convolve", getImage(), kern_v, null);
		    return ImageUtil.ImageUtil.sumImage(im1, im2);*/
			PlanarImage input = getImage();
			float[] filtroE = { 
					1, 1, -1, 
					1, -2, -1, 
					1, 1, -1 };
			KernelJAI kernelE = new KernelJAI(3, 3, filtroE);
			float[] filtroNE = { 
					1, -1, -1, 
					1, -2, -1, 
					1, 1, 1 };
			KernelJAI kernelNE = new KernelJAI(3, 3, filtroNE);
			float[] filtroN = { 
					-1, -1, -1, 
					1, -2, 1, 
					1, 1, 1 };
			KernelJAI kernelN= new KernelJAI(3, 3, filtroN);
			float[] filtroNO = { 
					-1, 1, 1, 
					-1, -2, 1, 
					-1, 1, 1 };
			KernelJAI kernelNO = new KernelJAI(3, 3, filtroNO);
			float[] filtroO = { 
					-1, 1, 1, 
					-1, -2, 1, 
					-1, 1, 1 };
			KernelJAI kernelO = new KernelJAI(3, 3, filtroO);
			float[] filtroSO = { 
					1, 1, 1, 
					-1, -2, 1, 
					-1, -1, 1 };
			KernelJAI kernelSO = new KernelJAI(3, 3, filtroSO);
			float[] filtroS = { 
					1, 1, 1, 
					1, -2, 1, 
					-1, -1, -1 };
			KernelJAI kernelS = new KernelJAI(3, 3, filtroS);
			float[] filtroSE = { 
					1, 1, 1, 
					1, -2, -1, 
					1, -1, -1 };
			KernelJAI kernelSE = new KernelJAI(3, 3, filtroSE);
			PlanarImage output1 = JAI.create("convolve", input, kernelE);
			PlanarImage output2 = JAI.create("convolve", input, kernelNE);
			PlanarImage output3 = JAI.create("convolve", input, kernelN);
			PlanarImage output4 = JAI.create("convolve", input, kernelNO);
			PlanarImage output5 = JAI.create("convolve", input, kernelO);
			PlanarImage output6 = JAI.create("convolve", input, kernelSO);
			PlanarImage output7 = JAI.create("convolve", input, kernelS);
			PlanarImage output8 = JAI.create("convolve", input, kernelSE);
			PlanarImage output = ImageUtil.sumImage(output1, output3);
			output = ImageUtil.sumImage(output1, output2);
			output = ImageUtil.sumImage(output, output3);
			output = ImageUtil.sumImage(output, output4);
			output = ImageUtil.sumImage(output, output5);
			output = ImageUtil.sumImage(output, output6);
			output = ImageUtil.sumImage(output, output7);
			output = ImageUtil.sumImage(output, output8);
			return output;
		}
		
		return null;
	}

	public String getCommandName() {
		return "SobelFilter";
	}

	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
