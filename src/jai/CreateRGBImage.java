package jai;

import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.*;

public class CreateRGBImage {
	public static void main(String[] args) {
		int width = 121;
		int height = 121; // Dimensions of the image
		byte[] data = new byte[width * height * 3]; // Image data array.
		int count = 0; // Temporary counter.
		for (int w = 0; w < width; w++)
			// Fill the array with a pattern.
			for (int h = 0; h < height; h++) {
				data[count + 0] = (count % 2 == 0) ? (byte) 0 : (byte) 255;
				data[count + 1] = 0;
				data[count + 2] = (count % 2 == 0) ? (byte) 255 : (byte) 0;
				count += 3;
			}
		// Create a Data Buffer from the values on the single image array.
		DataBufferByte dbuffer = new DataBufferByte(data, width * height * 3);
		// Create an pixel interleaved data sample model.
		SampleModel sampleModel = RasterFactory
				.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width,
						height, 3);
		// Create a compatible ColorModel.
		ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
		// Create a WritableRaster.
		Raster raster = RasterFactory.createWritableRaster(sampleModel,
				dbuffer, new Point(0, 0));
		// Create a TiledImage using the SampleModel.
		TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0,
				sampleModel, colorModel);
		// Set the data of the tiled image to be the raster.
		tiledImage.setData(raster);
		// Save the image on a file.
		JAI.create("filestore", tiledImage, "rgbpattern.tif", "TIFF");

		// Read the image. Assume args[0] points to its filename.
		PlanarImage input = JAI.create("fileload", "rgbpattern.tif");
		// Invert the image.
		PlanarImage output = JAI.create("invert", input);

		JAI.create("filestore", output, "rgbpattern2.tif", "TIFF");

		float angle = (float) Math.toRadians(45);
		float centerX = tiledImage.getWidth() / 2f;
		float centerY = tiledImage.getHeight() / 2f;
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(tiledImage);
		pb.add(centerX);
		pb.add(centerY);
		pb.add(angle);
		pb.add(new InterpolationBilinear());
		PlanarImage scaledImage = JAI.create("rotate", pb);
		JAI.create("filestore", scaledImage, "rotate.tif", "TIFF");

		int kernelSize = 15;
		float[] kernelMatrix = new float[kernelSize * kernelSize];
		for (int k = 0; k < kernelMatrix.length; k++)
			kernelMatrix[k] = 1.0f / (kernelSize * kernelSize);
		KernelJAI kernel = new KernelJAI(kernelSize, kernelSize, kernelMatrix);
		PlanarImage convolveImage = JAI.create("convolve", input, kernel);
		JAI.create("filestore", convolveImage, "convolve.tif", "TIFF");
	}
}
