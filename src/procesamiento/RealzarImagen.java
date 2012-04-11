package procesamiento;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.WritableRaster;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileScheduler;
import javax.media.jai.TiledImage;

public class RealzarImagen extends AbstractImageCommand {

	public RealzarImagen(PlanarImage image) {
		super(image);
	}

	@Override
	public PlanarImage execute() {
		if (getImage() != null) {
			TiledImage tiledImage = ImageUtil.createTiledImage(getImage(), ImageUtil.tileWidth, ImageUtil.tileHeight);
			int tWidth = ImageUtil.tileWidth;
			int tHeight =  ImageUtil.tileHeight;
			
			TileScheduler scheduler = JAI.getDefaultInstance().getTileScheduler();
			Point[] tileIndices = tiledImage.getTileIndices(null);
			scheduler.scheduleTiles(tiledImage, tileIndices, null);

			// We must process all tiles.
			for (int th = tiledImage.getMinTileY(); th <= tiledImage.getMaxTileY(); th++) 
				for (int tw = tiledImage.getMinTileX(); tw <= tiledImage.getMaxTileX(); tw++){
					// Get a raster for that tile.
					WritableRaster wr = tiledImage.getWritableTile(tw, th);

					for (int w = 0; w < tWidth; w++)
						for (int h = 0; h < tHeight; h++) {
							int x = tw * tWidth + w;
							int y = th * tHeight + h;
							if (x <= tiledImage.getMaxX() && y <= tiledImage.getMaxY()) {

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
									transformarPixel(wr, x, y, hsv);

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
	 * Realiza la trasnformacion para realzar el color del pixel
	 * @param wr WritableRaster raster donde se va guardar el pixel transformado
	 * @param x Posicion x del pixel en la imagen
	 * @param y Posicion y del pixel en la imagen
	 * @param hsv Color del pixel en formato HSV. H: Tonalidad, S: Saturacion, V: Brillo
	 */
	private void transformarPixel(WritableRaster wr, int x, int y, float[] hsv) {
		float h = hsv[0];
		float s = hsv[1];
		float v = hsv[2];
		float newH = h;
		float newS = s;
		float newV = v;
		float sUmbral = 30;
		float vUmbral = 70;
		if (s >= sUmbral && v >= vUmbral){
			//newS = 100;
			newV = vUmbral;
		}
		else if (s < sUmbral && v >= vUmbral){
			newS = 0;
			newV = vUmbral;
		}
		else if(v < 20){
			newV = 0;
		}
		Color newColor = new Color(RgbHsv.HSVtoRGB(newH / HSVRange.HMAX_VALUE, newS / HSVRange.SMAX_VALUE, newV / HSVRange.VMAX_VALUE));
		int[] pixel = {newColor.getRed(),newColor.getGreen(),newColor.getBlue()};
		wr.setPixel(x, y, pixel);
	}

	@Override
	public String getCommandName() {
		return this.getClass().getName();
	}

	@Override
	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
