package procesamiento.jai.operators;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PointOpImage;

import procesamiento.HSVRange;
import procesamiento.RgbHsv;

public class RealzarImageOp extends PointOpImage {

	public RealzarImageOp(RenderedImage source, ImageLayout layout,
			Map configuration, boolean cobbleSources) {
		super(source, layout, configuration, cobbleSources);
	}

	public RealzarImageOp(RenderedImage source) {
		super(source, null, null, true);
	}

	/**
	 * Realiza la trasnformacion para realzar el color del pixel
	 * 
	 * @param wr
	 *            WritableRaster raster donde se va guardar el pixel
	 *            transformado
	 * @param x
	 *            Posicion x del pixel en la imagen
	 * @param y
	 *            Posicion y del pixel en la imagen
	 * @param hsv
	 *            Color del pixel en formato HSV. H: Tonalidad, S: Saturacion,
	 *            V: Brillo
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
		if (s >= sUmbral && v >= vUmbral) {
			// newS = 100;
			newV = vUmbral;
		} else if (s < sUmbral && v >= vUmbral) {
			newS = 0;
			newV = vUmbral;
		} else if (v < 20) {
			newV = 0;
		}
		Color newColor = new Color(RgbHsv.HSVtoRGB(newH / HSVRange.HMAX_VALUE,
				newS / HSVRange.SMAX_VALUE, newV / HSVRange.VMAX_VALUE));
		int[] pixel = { newColor.getRed(), newColor.getGreen(),
				newColor.getBlue() };
		wr.setPixel(x, y, pixel);
	}

	@Override
	public Raster[] getTiles(Point[] arg0) {
		// TODO Auto-generated method stub
		return super.getTiles(arg0);
	}

	@Override
	protected void computeRect(Raster[] sources, WritableRaster dest,
			Rectangle destRect) {
		try{
			
			Raster raster = sources[0];
			int tx = XToTileX(raster.getMinX());
			int ty = XToTileX(raster.getMinY());
			int maxX = raster.getMinX() + raster.getWidth();
			int maxY = raster.getMinY() + raster.getHeight();
			for (int w = 0; w < raster.getWidth(); w++)
				for (int h = 0; h < raster.getHeight(); h++) {
					int[] pixel = null;
					int x = tx * tileWidth + w;
					int y = ty * tileHeight + h;
					if (x < maxX && y < maxY){
						pixel = raster.getPixel(x, y, pixel);
						int r = pixel[0];
						int g = pixel[0];
						int b = pixel[0];
						
						if (pixel.length == 3) {
							g = pixel[1];
							b = pixel[2];
						}
						
						float[] hsv = RgbHsv.RGBtoHSV(r, g, b);
						try{
							transformarPixel(dest, x, y, hsv);
							
						}
						catch (Exception e) {
							throw e;
						}
					}
				}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Raster getData(Rectangle arg0) {
		// TODO Auto-generated method stub
		return super.getData(arg0);
	}

	@Override
	public Raster computeTile(int tileX, int tileY) {
		return super.computeTile(tileX, tileY);
	}
	
	
	
}
