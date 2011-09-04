package procesamiento;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.WritableRaster;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import objeto.Pixel;


/**
 * Comando que elimina el fondo de una imagen
 * @author oscar
 *
 */
public class Binarizar extends AbstractImageCommand {

	private static final int umbralFondo = 100;

	/**
	 * Rango de valores HSV para determinar el fondo de la imagen
	 */
	private HSVRange rangeFondo = null;
	
	/**
	 * Rango de valores HSV para determinar que es un objeto y que no.
	 * Si está seteado se usa este rango para binarizar la imagen
	 */
	private HSVRange rangeObjeto = null;

	/**
	 * Coordenada x del tile actualmente en proceso
	 */
	private int tileXActual;

	/**
	 * Coordenada y del tile actualmente en proceso
	 */
	private int tileYActual;
	
	/**
	 * Ancho de la imagen
	 */
	private int width;

	/**
	 * Alto de la imagen
	 */
	private int height;


	/**
	 * Raster correspondiente al tile actualmente en proceso
	 */
	private WritableRaster rasterActual;
	
	private int Matriz[][] = null;
	
	private int maxMatrixW = 512;
	private int maxMatrixH = 512;
	
	private static int blanco[] = {255,255,255};
	
	private static int negro[] = {0,0,0};


	public Binarizar(PlanarImage image, HSVRange rangeFondo) {
		super(image);
		this.rangeFondo = rangeFondo;
	}
	
	public Binarizar(PlanarImage image, HSVRange rangeFondo, HSVRange rangoObjeto) {
		super(image);
		this.rangeFondo = rangeFondo;
		this.rangeObjeto = rangoObjeto;
	}
	
	/**
	 * Convierte la imagen a blanco y negro
	 * @param image
	 * @return
	 */
	private TiledImage binarizar(PlanarImage image){
		if (getImage() != null) {
			TiledImage tiledImage = ImageUtil.createTiledImage(getImage(), ImageUtil.tileWidth, ImageUtil.tileHeight);
			int width = getImage().getWidth();
			int height = getImage().getHeight();
			setWidth(width);
			setHeight(height);
			int tWidth = ImageUtil.tileWidth;
			int tHeight =  ImageUtil.tileHeight;
			
			int maxX = 0;
			int maxY = 0;

			// We must process all tiles.
			for (int th = tiledImage.getMinTileY(); th <= tiledImage.getMaxTileY(); th++) 
				for (int tw = tiledImage.getMinTileX(); tw <= tiledImage.getMaxTileX(); tw++){
					// Get a raster for that tile.
					WritableRaster wr = tiledImage.getWritableTile(tw, th);
					setRasterActual(wr);
					setTileXActual(tw);
					setTileYActual(tw);

					for (int w = 0; w < tWidth; w++)
						for (int h = 0; h < tHeight; h++) {
							int x = tw * tWidth + w;
							int y = th * tHeight + h;
							if (x <= image.getMaxX() && y <= image.getMaxY()) {
								maxX = Math.max(maxX, x);
								maxY = Math.max(maxY, y);

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
											wr.setPixel(x, y, negro);
										} 
										else {
											wr.setPixel(x, y, blanco);
										}
									}
									else{
										if (rangeObjeto.isEnRango(hsv[0], hsv[1], hsv[2])) {
											wr.setPixel(x, y, blanco);
										} 
										else {
											wr.setPixel(x, y, negro);
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
			System.out.println("Maximo: " + maxX + " - " + maxY);
			return tiledImage;
		}
		return null;

	}
	
	/**
	 * Convierte la imagen a blanco y negro
	 * @param image
	 * @return
	 */
	private TiledImage eliminarPuntosAislados(TiledImage image){
		if (getImage() != null) {
			TiledImage tiledImage = image;
			int width = image.getWidth();
			int height = image.getHeight();
			setWidth(width);
			setHeight(height);
			int tWidth = ImageUtil.tileWidth;
			int tHeight =  ImageUtil.tileHeight;

			// We must process all tiles.
			for (int th = tiledImage.getMinTileY(); th <= tiledImage.getMaxTileY(); th++) 
				for (int tw = tiledImage.getMinTileX(); tw <= tiledImage.getMaxTileX(); tw++){
					// Get a raster for that tile.
					WritableRaster wr = tiledImage.getWritableTile(tw, th);
					setRasterActual(wr);
					setTileXActual(tw);
					setTileYActual(tw);

					for (int w = 0; w < tWidth; w++)
						for (int h = 0; h < tHeight; h++) {
							int x = tw * tWidth + w;
							int y = th * tHeight + h;
							if (y == 186)
								System.out.println("");
							if (x <= image.getMaxX() && y <= image.getMaxY()) {
								
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
									Pixel aux = new Pixel(x,y,new Color(r,g,b), width, height);
									
									if (isPuntoAislado(aux, tiledImage) || isBordeImagen(aux)){
										wr.setPixel(x, y, negro);
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
	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#execute()
	 */
	public PlanarImage execute() {
		if (getImage() != null) {
			TiledImage image = binarizar(getImage());
			return eliminarPuntosAislados(image);
		}
		return null;

	}
	
	/**
	 * Recupera el pixel (x,y) de la imagen
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param nbands
	 * @param pixels
	 * @return
	 */
	public Pixel getPixel(Pixel p, PlanarImage image) {
		if (p != null){
			int[] pixel = ImageUtil.readPixel(p.getX(), p.getY(), (TiledImage) image);
			int r = pixel[0];
			int g = pixel[0];
			int b = pixel[0];

			if (pixel.length == 3) {
				g = pixel[1];
				b = pixel[2];
			}
			Color colorPixel = new Color(r, g, b);
			return new Pixel(p.getX(), p.getY(), colorPixel,getImage().getMaxX(),getImage().getMaxY());
		}
		return null;
	}
	
	/**
	 * Retorna si un pixel es fondo de la imagen
	 * 
	 * @param pixel
	 * @return
	 */
	public boolean isFondo(Pixel pixel) {
		if (pixel.getCol() != null)
			return pixel.getCol().getRed() < umbralFondo;
		return false;
	}

	private boolean isPuntoAislado(Pixel pixel, PlanarImage image) {
		int countAdyacentes = 0;
		Pixel ady = getPixel(pixel.getAdyacente(Pixel.DIR_N, width, height),image);
		if (ady != null && !isFondo(ady)) {
			countAdyacentes++;
		}
		ady = getPixel(pixel.getAdyacente(Pixel.DIR_E, width, height),image);
		if (ady != null && !isFondo(ady)) {
			countAdyacentes++;
		}
		ady = getPixel(pixel.getAdyacente(Pixel.DIR_S, width, height),image);
		if (ady != null && !isFondo(ady)) {
			countAdyacentes++;
		}
		ady = getPixel(pixel.getAdyacente(Pixel.DIR_O, width, height),image);
		if (ady != null && !isFondo(ady)) {
			countAdyacentes++;
		}
		if (countAdyacentes < 2)
			return true;
		return false;

	}

	private boolean isBordeImagen(Pixel pixel) {
		if (pixel.getY() == 186)
			System.out.println("");
		if (pixel.getX() <= 0 || pixel.getY() <= 0 || pixel.getX() >= getImage().getMaxX() || pixel.getY() >= getImage().getMaxY())
			return true;
		return false;
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

	public int getTileXActual() {
		return tileXActual;
	}

	public void setTileXActual(int tileXActual) {
		this.tileXActual = tileXActual;
	}

	public int getTileYActual() {
		return tileYActual;
	}

	public void setTileYActual(int tileYActual) {
		this.tileYActual = tileYActual;
	}

	public WritableRaster getRasterActual() {
		return rasterActual;
	}

	public void setRasterActual(WritableRaster rasterActual) {
		this.rasterActual = rasterActual;
	}
	private Pixel convertirPixel(Pixel p){
		Pixel pixel = new Pixel((p.getX() - getTileXActual() * ImageUtil.tileWidth),(p.getY() - getTileYActual() * ImageUtil.tileHeight), p.getCol(), width, height);
		if ((pixel.getY() >= maxMatrixH) || (pixel.getX() >= maxMatrixW))
			return null;
		if ((pixel.getY() < 0) || (pixel.getX() < 0))
			return null;
		return pixel;
	}
	/**
	 * Limpia los pixels visitados
	 */

	private void initVisitados() {
		this.Matriz  = null;
		this.Matriz = new int[maxMatrixW+1][maxMatrixH+1];
	}

	
	/**
	 * Setea como visitado un pixel
	 * 
	 * @param pixel
	 */
	private void setVisitado(Pixel p) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null){
			Matriz[pixel.getX()][pixel.getY()] = 1;
		}
	}
	
	/**
	 * Retorna si un pixel fue visitado
	 * 
	 * @param pixel
	 * @return
	 */
	private boolean isVisitado(Pixel p) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null)
			if (Matriz[pixel.getX()][pixel.getY()] == 1){
				return true;
			}else{ 
				return false;
			}	
		return true;
	}
	

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	

}
