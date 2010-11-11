package jai;

import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileRequest;

import procesamiento.ConvertEscalaGrises;

public class SobelFilter {
	public static void main(String[] args) {
		// Read the image. Assume args[0] points to its filename.
		if (args.length > 0) {
			PlanarImage input = JAI.create("fileload", args[0]);
			ConvertEscalaGrises c = new ConvertEscalaGrises(input);
			input = c.execute();
			
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
			PlanarImage output = sumImage(output1, output3);
			output = sumImage(output1, output2);
			output = sumImage(output, output3);
			output = sumImage(output, output4);
			output = sumImage(output, output5);
			output = sumImage(output, output6);
			output = sumImage(output, output7);
			output = sumImage(output, output8);

			JAI.create("filestore", output, "sobel.tif", "TIFF");
			
		}

	}
	private static PlanarImage createEmptyImage(int width, int height){
		// Create the ParameterBlock.
	     Byte[] bandValues = new Byte[1];
	     bandValues[0] = new Integer(0).byteValue();
	     ParameterBlock pb = new ParameterBlock();
	     pb.add(new Float(width));   // The width
	     pb.add(new Float(height));  // The height
	     pb.add(bandValues);                   // The band values

	     // Create the constant operation.
	     PlanarImage afa1 = (PlanarImage)JAI.create("constant", pb);
	     return afa1;
	}
	
	public static PlanarImage sumImage(PlanarImage image1, PlanarImage image2){
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image1);
		pb.addSource(image2);
		return JAI.create("add", pb);
	}
}
