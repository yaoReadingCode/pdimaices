package procesamiento;


import java.awt.Dimension;
import java.awt.image.WritableRaster;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;


/**
 * Comando que elimina el fondo de una imagen
 * @author oscar
 *
 */
public class Binarizar extends AbstractImageCommand {

	/**
	 * Rango de valores HSV para determinar el fondo de la imagen
	 */
	private HSVRange rangeFondo = null;
	
	/**
	 * Rango de valores HSV para determinar que es un objeto y que no.
	 * Si está seteado se usa este rango para binarizar la imagen
	 */
	private HSVRange rangeObjeto = null;

	public Binarizar(PlanarImage image, HSVRange rangeFondo) {
		super(image);
		this.rangeFondo = rangeFondo;
	}
	
	public Binarizar(PlanarImage image, HSVRange rangeFondo, HSVRange rangoObjeto) {
		super(image);
		this.rangeFondo = rangeFondo;
		this.rangeObjeto = rangoObjeto;
	}

	/*
	 * (non-Javadoc)
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
									if (rangeObjeto == null || rangeObjeto.isNulo()){
										if (rangeFondo.isEnRango(hsv[0], hsv[1], hsv[2])) {

											int[] newPixel = { 0, 0, 0 };
											wr.setPixel(x, y, newPixel);
										} 
										else {
											int[] newPixel = { 255, 255, 255 };
											wr.setPixel(x, y, newPixel);
										}
									}
									else{
										if (rangeObjeto.isEnRango(hsv[0], hsv[1], hsv[2])) {
											int[] newPixel = { 255, 255, 255 };
											wr.setPixel(x, y, newPixel);
											
										} 
										else {
											int[] newPixel = { 0, 0, 0 };
											wr.setPixel(x, y, newPixel);
										}

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

			return tiledImage;
		}
		return null;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PlanarImage pi = JAI.create("fileload", args[0]);
		pi = ImageUtil.reformatImage(pi, new Dimension(256, 256));
		HSVRange rangeFondo = new HSVRange();
		rangeFondo.setHMin(40f);
		rangeFondo.setHMax(340f);
		rangeFondo.setSMin(9f);
		Binarizar e = new Binarizar(pi, rangeFondo);
		PlanarImage tiledImage = e.execute();
		JAI.create("filestore", tiledImage, "imagenGris2.tif", "TIFF");

	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Binarizar";
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		// TODO Auto-generated method stub
		
	}

}
