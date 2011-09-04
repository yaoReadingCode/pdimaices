package procesamiento;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageReadParam;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.TileCache;
import javax.media.jai.TiledImage;

import objeto.Objeto;
import objeto.Pixel;
import aplicarFiltros.Matriz;

import com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi;

public class ImageUtil {
	public static final int tileWidth = 256;
	public static final int tileHeight = 256;

	public static PlanarImage sumImage(PlanarImage image1, PlanarImage image2) {
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image1);
		pb.addSource(image2);
		return JAI.create("add", pb);
	}
	
	/**
	 * Aplica una lista de filtros a una imagen y retorna la suma de los resultados
	 * @param inputImage
	 * @param filtros
	 * @param operation Operación JAI a aplicar
	 * @return
	 */
	public static PlanarImage aplicarFiltros(PlanarImage inputImage, List<Matriz> filtros, String operation) {
		if (filtros != null && filtros.size() > 0) {
			Iterator<Matriz> iterator = filtros.iterator();

			Matriz m = null;
			boolean buscarFirst = true;
			while (iterator.hasNext() && buscarFirst) {
				Matriz m1 = iterator.next();
				if (m1.isHabilitado()) {
					m = m1;
					buscarFirst = false;
				}
			}
			if (m != null) {
				KernelJAI kernel1 = new KernelJAI(3, 3, m.getValues());
				PlanarImage output1 = JAI.create(operation, inputImage, kernel1);
				PlanarImage output = null;
				while (iterator.hasNext()) {
					m = iterator.next();
					if (m.isHabilitado()) {
						KernelJAI kernel2 = new KernelJAI(3, 3, m.getValues());
						PlanarImage output2 = JAI.create(operation, inputImage, kernel2);
						output = sumImage(output1, output2);
						output1 = output;
					}
				}
				if (output != null)
					return output;
				return output1;
			}
		}
		return inputImage;
	}

	/**
	 * Binariza la imagen pintando los objetos con el color pasado como parámetro
	 * @param image Imagen a binarizar
	 * @param fondo Color del fondo
	 * @param nuevoColor Nuevo color de los objetos
	 * @return
	 */
	public static PlanarImage binarize(PlanarImage image, Color fondo, Color nuevoColor) {
		TiledImage ti = ImageUtil.createTiledImage(image, tileWidth, tileHeight);
		int width = image.getWidth();
		int height = image.getHeight();

		int [] newPixel = {nuevoColor.getRed(), nuevoColor.getGreen(), nuevoColor.getBlue()};
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++) {
				int[] pixel = ImageUtil.readPixel(w, h, ti);
				int r = pixel[0];
				int g = pixel[1];
				int b = pixel[2];

				Color color = new Color(r, g, b);
				if (!color.equals(fondo)) {
					ImageUtil.writePixel(w, h, newPixel, ti);
				}
			}
		return ti;
	}

	/**
	 * Calcula el momento de inercia u(k,l) del contorno de un objeto
	 * 
	 * @param k
	 * @param l
	 * @return
	 */
	public static double momento(int k, int l, Objeto objeto) {
		if (objeto != null && objeto.getContorno() != null) {
			List<Pixel> contorno = objeto.getContorno();
			// double x_centro = objeto.getPixelMedio().getXDouble();
			// double y_centro = objeto.getPixelMedio().getYDouble();
			double suma = 0;
			for (Pixel pixel : contorno) {
				suma += Math.pow(pixel.getXDouble(), k)
						* Math.pow(pixel.getYDouble(), l);
			}
			return suma;
		}
		return 1;
	}

	/**
	 * Reformatea una imagen creando tiles de una dimension dada
	 * 
	 * @param img
	 *            Imagen a reformatear
	 * @param tileDim
	 *            Dimensiones de los tiles ej: 256 x 256
	 * @return
	 */
	public static RenderedOp reformatImage(PlanarImage img, Dimension tileDim) {
		int tileWidth = tileDim.width;
		int tileHeight = tileDim.height;
		ImageLayout tileLayout = new ImageLayout(img);
		tileLayout.setTileWidth(tileWidth);
		tileLayout.setTileHeight(tileHeight);

		HashMap map = new HashMap();
		map.put(JAI.KEY_IMAGE_LAYOUT, tileLayout);
		map.put(JAI.KEY_INTERPOLATION, Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
		RenderingHints tileHints = new RenderingHints(map);

		ParameterBlock pb = new ParameterBlock();
		pb.addSource(img);
		return JAI.create("format", pb, tileHints);
	}

	/**
	 * Crea un TiledImage de un PlanarImage
	 * 
	 * @param image
	 *            PlanarImage
	 * @return TiledImage
	 */
	public static TiledImage createDisplayImage(PlanarImage image) {
		SampleModel sampleModel = image.getSampleModel();
		ColorModel colorModel = image.getColorModel();

		TiledImage ti = new TiledImage(image.getMinX(), image.getMinY(), image
				.getWidth(), image.getHeight(), image.getTileGridXOffset(),
				image.getTileGridYOffset(), sampleModel, colorModel);
		ti.setData(image.copyData());
		return ti;
	}

	/**
	 * Setea el valor de un pixel de un TiledImage
	 * @param x
	 * @param y
	 * @param pixelValue
	 * @param image
	 */
	public static void writePixel(int x, int y, int[] pixelValue, TiledImage image) {
		int xIndex = image.XToTileX(x);
		int yIndex = image.YToTileY(y);
		
		WritableRaster tileRaster = image.getWritableTile(xIndex, yIndex);
		if (tileRaster != null)
			//System.out.println("pixel: " + x +" , " + y);
			tileRaster.setPixel(x, y, pixelValue);
		//image.releaseWritableTile(xIndex, yIndex);
		
	}
	
	/**
	 * Setea el valor de un pixel de un TiledImage
	 * @param x
	 * @param y
	 * @param pixelValue
	 * @param image
	 */
	public static void writePixel(int x, int y, int[] pixelValue, TiledImage image, WritableRaster rasterActual, int tileXActual, int tileYActual) {
		int xIndex = image.XToTileX(x);
		int yIndex = image.YToTileY(y);
		WritableRaster tileRaster = null;
		if (xIndex == tileXActual && yIndex == tileYActual)
			tileRaster = rasterActual; 
		else
			tileRaster = image.getWritableTile(xIndex, yIndex);
		
		if (tileRaster != null)
			//System.out.println("pixel: " + x +" , " + y);
			tileRaster.setPixel(x, y, pixelValue);
		image.releaseWritableTile(xIndex, yIndex);
	}
	
	/**
	 * Lee el valor de un pixel de un TiledImage
	 * @param x
	 * @param y
	 * @param image
	 */
	public static int[] readPixel(int x, int y, PlanarImage image) {
		int[] pixelValue = null;
		int xIndex = image.XToTileX(x);
		int yIndex = image.YToTileY(y);
		Raster tileRaster = image.getTile(xIndex, yIndex);
		if (tileRaster != null)
			pixelValue = tileRaster.getPixel(x, y, pixelValue);
		//image.releaseWritableTile(xIndex, yIndex);
		return pixelValue;
	}
	
	/**
	 * Lee el valor de un pixel de un TiledImage
	 * @param x
	 * @param y
	 * @param image
	 */
	public static int[] readPixel(int x, int y, TiledImage image, Raster rasterActual, int tileXActual, int tileYActual) {
		int[] pixelValue = null;
		int xIndex = image.XToTileX(x);
		int yIndex = image.YToTileY(y);
		JAI instance = JAI.getDefaultInstance();
		
		
		
		Raster tileRaster = null;
		if (xIndex == tileXActual && yIndex == tileYActual)
			tileRaster = rasterActual; 
		else{
			tileRaster = instance.getTileCache().getTile(image, xIndex, yIndex);  //image.getTile(xIndex, yIndex);
			if (tileRaster == null){
				tileRaster = image.getTile(xIndex, yIndex);
				instance.getTileCache().add(image, xIndex, yIndex, tileRaster);
			}
		}
			
		
		if (tileRaster != null)
			pixelValue = tileRaster.getPixel(x, y, pixelValue);
		//image.releaseWritableTile(xIndex, yIndex);
		return pixelValue;
	}
	
	/**
	 * Crea un TiledImage de un PlanarImage
	 * 
	 * @param image
	 *            PlanarImage
	 * @return TiledImage
	 */
	public static TiledImage createTiledImage(PlanarImage image, int tyleWidth, int tyleHeight) {
		TiledImage ti = new TiledImage(image,tyleWidth,tyleHeight);
		return ti;
	}
	
	public static PlanarImage loadImage(String absolutePath, int tileWidth, int tileHeight) throws Exception{
		
			//setting cache
			TileCache cache = JAI.getDefaultInstance().getTileCache();
			
			ImageLayout tileLayout = new ImageLayout();
			tileLayout.setTileWidth(tileWidth);
			tileLayout.setTileHeight(tileHeight);
	
			HashMap map = new HashMap();
			map.put(JAI.KEY_IMAGE_LAYOUT, tileLayout);
			map.put(JAI.KEY_TILE_CACHE, cache);
			map.put(JAI.KEY_CACHED_TILE_RECYCLING_ENABLED, true);
			RenderingHints tileHints = new RenderingHints(map);
			
			//JAI.getDefaultInstance().setRenderingHints(tileHints);
		
		
			 JPEGImageReaderSpi 	readerJPEGSpi = new JPEGImageReaderSpi();
			 final ImageReadParam readP = new ImageReadParam();
			
			 final ParameterBlock pbjRead = new ParameterBlock();
			 pbjRead.add(absolutePath);
			 pbjRead.add(0);
			 pbjRead.add(Boolean.FALSE);
			 pbjRead.add(Boolean.FALSE);
			 pbjRead.add(Boolean.FALSE);
			 pbjRead.add(null);
			 pbjRead.add(null);
			 pbjRead.add(readP);
			 pbjRead.add(readerJPEGSpi.createReaderInstance());
			 
			 PlanarImage image = JAI.create("imageread",pbjRead,tileHints);

			 return image;
	}
	
	public static Color getColorPunto(Pixel pixel, PlanarImage ti) {
		/**/
		int[] pix = ImageUtil.readPixel(pixel.getX(), pixel.getY(), ti);

		int r = pix[0];
		int g = pix[0];
		int b = pix[0];
		if (pix.length == 3) {
			g = pix[1];
			b = pix[2];
		}
		return new Color(r, g, b);

	}

}
