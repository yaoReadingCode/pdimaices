package jai;

/*
 * Part of the Java Image Processing Cookbook, please see
 * http://www.lac.inpe.br/~rafael.santos/JIPCookbook/index.jsp
 * for information on usage and distribution.
 * Rafael Santos (rafael.santos@lac.inpe.br)
 */

import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;

import javax.media.jai.JAI;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;

/**
 * This application creates a volume image, represented and stored as a
 * two-dimensional image with a large number of bands (the third dimension). The
 * image's pixels will be floating point values. To avoid Out of Memory errors,
 * you should run this application with an increased heap space (e.g. -Xmx256M).
 */
public class CreateGLVolume {
	/**
	 * The application entry point. No parameters are required.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// The image dimensions.
		int width = 2024;
		int height = 2024;
		// We need a sample model. The most appropriate is created as shown:
		SampleModel sampleModel = RasterFactory.createBandedSampleModel(
				DataBuffer.TYPE_BYTE, width, height, 1); // One band.
		// Create a TiledImage using the SampleModel.
		TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0,
				sampleModel, null);
		// Get a raster for the single tile.
		WritableRaster wr = tiledImage.getWritableTile(0, 0);
		// Paint it with squares in random gray levels
		for (int h = 0; h < height / 32; h++)
			for (int w = 0; w < width / 32; w++) {
				int[] fill = new int[32 * 32]; // A block of pixels...
				Arrays.fill(fill, (int) (Math.random() * 256)); // .. filled a
				// random gray
				// level.
				wr.setSamples(w * 32, h * 32, 32, 32, 0, fill);
			}
		// Save the image on a file.
		JAI.create("filestore", tiledImage, "jaigl.png", "PNG");
	}

}
