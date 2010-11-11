package jai;

import java.awt.Point;
import java.awt.image.*;
import javax.media.jai.*;

public class CreateGrayImage {
	public static void main(String[] args) {
		int width = 1024;
		int height = 1024; // Dimensions of the image.
		float[] imageData = new float[width * height];
		int count = 0; // Auxiliary counter.
		for (int w = 0; w < width; w++)
			// Fill the array with a degradé pattern.
			for (int h = 0; h < height; h++)
				imageData[count++] = (float) (Math.sqrt(w + h));
		// Create a DataBuffer from the values on the image array.
		javax.media.jai.DataBufferFloat dbuffer = new javax.media.jai.DataBufferFloat(
				imageData, width * height);
		// Create a float data sample model.
		SampleModel sampleModel = RasterFactory.createBandedSampleModel(
				DataBuffer.TYPE_FLOAT, width, height, 1);
		// Create a compatible ColorModel.
		ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
		// Create a WritableRaster.
		Raster raster = RasterFactory.createWritableRaster(sampleModel,
				dbuffer, new Point(0, 0));
		// Create a TiledImage using the float SampleModel.
		TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0,
				sampleModel, colorModel);
		// Set the data of the tiled image to be the raster.
		tiledImage.setData(raster);
		// Save the image on a file.
		JAI.create("filestore", tiledImage, "floatpattern.tif", "TIFF");
	}
}