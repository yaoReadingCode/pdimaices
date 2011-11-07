package procesamiento;

import java.awt.RenderingHints;
import java.awt.image.Kernel;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

public class GaussianFilter extends AbstractImageCommand {
	
	protected float radius;
	protected float[] matrix;

	public GaussianFilter(PlanarImage image, float radius) {
		super(image);
		this.radius = radius;
		this.matrix = GaussianFilter.makeKernel(radius);
	}
	
	/**
	 * Make a Gaussian blur kernel.
	 */
	public static float[] makeKernel(float radius) {
		int r = (int)Math.ceil(radius);
		int rows = r*2+1;
		float[] matrix = new float[rows];
		float sigma = radius/3;
		float sigma22 = 2*sigma*sigma;
		float sigmaPi2 = 2* (float) Math.PI * sigma;
		float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
		float radius2 = radius*radius;
		float total = 0;
		int index = 0;
		for (int row = -r; row <= r; row++) {
			float distance = row*row;
			if (distance > radius2)
				matrix[index] = 0;
			else
				matrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
			total += matrix[index];
			index++;
		}
		for (int i = 0; i < rows; i++)
			matrix[i] /= total;

		return matrix;
	}

	@Override
	public PlanarImage execute() {
		RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
		KernelJAI horizontal = new KernelJAI(matrix.length, 1, matrix);
		KernelJAI vertical = new KernelJAI(1, matrix.length, matrix);
		PlanarImage output1 = JAI.create("convolve", getImage(), horizontal, hints);
		PlanarImage result = JAI.create("convolve", output1, vertical, hints);
		return result;
	}

	@Override
	public String getCommandName() {
		return "GaussianFilter";
	}

	@Override
	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
