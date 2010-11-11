package objeto;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;

import procesamiento.ImageUtil;

public class ObjetoUtil {
	//private static PlanarImage inputImage = JAI.create("fileload", "limpia.tif");
	private static int desplazamiento = 50;
	/*
	public PlanarImage getInputImage() {
		return inputImage;
	}

	public static void setInputImage(PlanarImage inputImageSet) {
		inputImage = inputImageSet;
	}*/

	public static void save(Objeto o) {
		int width = 206;
		int height = 174;
		byte[] data = new byte[width * height* 3]; // Image data array.
		int count = 0; // Temporary counter.
		// Create a Data Buffer from the values on the single image array.
		DataBufferByte dbuffer = new DataBufferByte(data, width * height * 3);
		// Create an pixel interleaved data sample model.
		SampleModel sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width,
						height, 3);
		// Create a compatible ColorModel.
		ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
		// Create a WritableRaster.
		Raster raster = RasterFactory.createWritableRaster(sampleModel,
				dbuffer, new Point(0, 0));
		// Create a TiledImage using the SampleModel.
		TiledImage ti = new TiledImage(0, 0, width, height, 0, 0,
				sampleModel, colorModel);
		// Set the data of the tiled image to be the raster.
		ti.setData(raster);
		//TiledImage ti = ImageUtil.createTiledImage(inputImage, 206, 174);
		if (o != null) {
			o.calcularMaximosMinimos();
			for (Pixel p : o.getPuntos()) {
				pintarPunto(p, o, ti);
			}
			for (Pixel p : o.getContorno()) {
				pintarPunto(p, o, ti);
			}
		}

		JAI.create("filestore", ti, "image\\" + o.getName() + ".tif", "TIFF");
	}
	private static void pintarPunto(Pixel p, Objeto o, TiledImage ti){
		Color interior = p.getCol();
		if (interior != null) {
			int pixel[] = { interior.getRed(), interior.getGreen(),
					interior.getBlue() };

			int x = (p.getX() - o.getxMin()) + desplazamiento;
			int y = (p.getY() - o.getyMin()) + desplazamiento;
			ImageUtil.writePixel(x, y, pixel, ti);
		}else{
			int pixel[] = {50, 50,	50};

			int x = (p.getX() - o.getxMin()) + desplazamiento;
			int y = (p.getY() - o.getyMin()) + desplazamiento;
			ImageUtil.writePixel(x, y, pixel, ti);
			
		}
		
	}

}
