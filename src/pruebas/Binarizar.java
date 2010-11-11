package pruebas;

import java.awt.Dimension;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import com.sun.media.jai.codec.JPEGDecodeParam;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;

import procesamiento.AbstractImageCommand;
import procesamiento.HSVRange;
import procesamiento.ImageUtil;
import procesamiento.RgbHsv;

/**
 * Comando que elimina el fondo de una imagen
 * 
 * @author oscar
 * 
 */
public class Binarizar extends AbstractImageCommand {

	/**
	 * Rango de valores HSV para determinar el fondo de la imagen
	 */
	private HSVRange range = null;

	public Binarizar(PlanarImage image, HSVRange range) {
		super(image);
		this.range = range;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see procesamiento.ImageComand#execute()
	 */
	public PlanarImage execute() {
		if (getImage() != null) {
			
			TiledImage tiledImage = ImageUtil.createTiledImage(getImage(), ImageUtil.tileWidth, ImageUtil.tileHeight);
			int width = getImage().getWidth();
			int height = getImage().getHeight();
			int tWidth = ImageUtil.tileWidth;
			int tHeight =  ImageUtil.tileHeight;

			// We must process all tiles.
			for (int tw = tiledImage.getMinTileX(); tw <= tiledImage.getMaxTileX(); tw++)
				for (int th = tiledImage.getMinTileY(); th <= tiledImage.getMaxTileY(); th++) {
					// Get a raster for that tile.
					WritableRaster wr = tiledImage.getWritableTile(tw, th);

					for (int w = 0; w < tWidth; w++)
						for (int h = 0; h < tHeight; h++) {
							int x = tw * tWidth + w;
							int y = th * tHeight + h;
							if (x < width && y < height) {
								
								try {
									int[] pixel = null;
									
									pixel = wr.getPixel(x, y, pixel);
									int r = pixel[0];
									int g = pixel[0];
									int b = pixel[0];

									if (pixel.length == 3) {
										g = pixel[1];
										b = pixel[2];
									}

									float[] hsv = RgbHsv.RGBtoHSV(r, g, b);
									if (((range.getHMin() != null && range.getHMin() <= hsv[0]) || range.getHMin() == null)
											&& ((range.getHMax() != null && range.getHMax() >= hsv[0]) || range.getHMax() == null)
											&& ((range.getSMin() != null && range.getSMin() <= hsv[1]) || range.getSMin() == null)
											&& ((range.getSMax() != null && range.getSMax() >= hsv[1]) || range.getSMax() == null)
											&& ((range.getVMin() != null && range.getVMin() <= hsv[2]) || range.getVMin() == null)
											&& ((range.getVMax() != null && range.getVMax() >= hsv[2]) || range.getVMax() == null)) {

										int[] newPixel = { 0, 0, 0 };
										wr.setPixel(x, y, newPixel);
									} else {
										int[] newPixel = { 255, 255, 255 };
										wr.setPixel(x, y, newPixel);
									}
								} catch (Exception e) {
									System.out.println("x: "+x + ", y: "+ y);
									e.printStackTrace();
									return null;
								}
							}
						}
					tiledImage.releaseWritableTile(tw, th);
				}

			/*
			 * int countPixelsBlanco = 0; for (int h = 0; h < height; h++) for
			 * (int w = 0; w < width; w++) { int[] pixel =
			 * ImageUtil.readPixel(w, h, ti); // System.out.print("at (" + w +
			 * "," + h + "): "); int r = pixel[0]; int g = pixel[0]; int b =
			 * pixel[0];
			 * 
			 * if (pixel.length == 3) { g = pixel[1]; b = pixel[2]; } if (r >
			 * 250 ){ countPixelsBlanco ++; }
			 * 
			 * float[] hsv = RgbHsv.RGBtoHSV(r, g, b); if (((range.getHMin() !=
			 * null && range.getHMin() <= hsv[0]) || range .getHMin() == null)
			 * && ((range.getHMax() != null && range.getHMax() >= hsv[0]) ||
			 * range .getHMax() == null) && ((range.getSMin() != null &&
			 * range.getSMin() <= hsv[1]) || range .getSMin() == null) &&
			 * ((range.getSMax() != null && range.getSMax() >= hsv[1]) || range
			 * .getSMax() == null) && ((range.getVMin() != null &&
			 * range.getVMin() <= hsv[2]) || range .getVMin() == null) &&
			 * ((range.getVMax() != null && range.getVMax() >= hsv[2]) || range
			 * .getVMax() == null)) {
			 * 
			 * int [] newPixel = {0, 0, 0}; ImageUtil.writePixel(w, h, newPixel,
			 * ti); } else { int [] newPixel = {255, 255, 255};
			 * ImageUtil.writePixel(w, h, newPixel, ti); }
			 * 
			 * }
			 */

			return tiledImage;
		}
		return null;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PlanarImage pi = JAI.create("fileload", args[0]);
		TIFFEncodeParam tep = new TIFFEncodeParam();
	    tep.setWriteTiled(true);
	    tep.setTileSize(ImageUtil.tileWidth, ImageUtil.tileHeight);
		
		pi = JAI.create("filestore", pi, "tiledImage.tif", "TIFF", tep);
		
		pi = ImageUtil.reformatImage(pi, new Dimension(256, 256));
		HSVRange range = new HSVRange();
		range.setHMin(40f);
		range.setHMax(340f);
		range.setSMin(9f);
		Binarizar e = new Binarizar(pi, range);
		PlanarImage tiledImage = e.execute();

		JAI.create("filestore", tiledImage, "imagenGris2.tif", "TIFF", tep);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Binarizar";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
