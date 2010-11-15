package procesamiento;

import java.awt.Color;
import java.awt.image.DataBuffer;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import objeto.Objeto;
import objeto.ObjetoUtil;
import objeto.Pixel;
import objeto.PixelComparator;

/**
 * Comando que detecta el contorno de 1 pixel exterior de los objetos de una
 * imagen binaria
 * 
 * @author oscar
 * 
 */
public class DetectarContorno extends AbstractImageCommand {
	private static final int maximoPuntos = 5000;

	/**
	 * Imagen original
	 */
	private PlanarImage originalImage;

	private int Matriz[][] = null;
	private TiledImage visitados = null;
	/**
	 * Color del fondo de la imagen
	 */
	private Color colorUmbralFondo;

	/**
	 * Color utilizado para marcar el contorno
	 */
	private Color colorContorno;

	/**
	 * Color utilizado para marcar el interior
	 */
	private Color colorInterior;

	/**
	 * Lista de objetos detectados
	 */
	private List<Objeto> objetos = new ArrayList<Objeto>();

	/**
	 * Marca de pixels visitados
	 */
	// private List<Pixel> visitados;

	/**
	 * Ancho de la imagen
	 */
	private int width;

	/**
	 * Alto de la imagen
	 */
	private int height;

	/**
	 * Bandas de la imagen
	 */
	private int nbands;

	/**
	 * Coordenada x del tile actualmente en proceso
	 */
	private int tileXActual;

	/**
	 * Coordenada y del tile actualmente en proceso
	 */
	private int tileYActual;

	/**
	 * Raster correspondiente al tile actualmente en proceso
	 */
	private WritableRaster rasterActual;

	public Color getColorInterior() {
		return colorInterior;
	}

	public void setColorInterior(Color colorInterior) {
		this.colorInterior = colorInterior;
	}

	public DetectarContorno(PlanarImage binaryImage, PlanarImage originalImage,
			Color colorUmbralFondo, Color colorContorno) {
		super(binaryImage);
		this.colorUmbralFondo = colorUmbralFondo;
		this.colorContorno = colorContorno;
		this.originalImage = originalImage;
	}

	public PlanarImage getOriginalImage() {
		return originalImage;
	}

	public void setOriginalImage(PlanarImage originalImage) {
		this.originalImage = originalImage;
	}

	public Color getColorUmbralFondo() {
		return colorUmbralFondo;
	}

	public void setColorUmbralFondo(Color colorUmbralFondo) {
		this.colorUmbralFondo = colorUmbralFondo;
	}

	/**
	 * Limpia los pixels visitados
	 */
	private void initVisitados() {
		// The image dimensions.
		int width = getImage().getWidth();
		int height = getImage().getHeight();
		// We need a sample model where the pixels are packed into one data
		// type.
		MultiPixelPackedSampleModel sampleModel = new MultiPixelPackedSampleModel(
				DataBuffer.TYPE_BYTE, ImageUtil.tileWidth,
				ImageUtil.tileHeight, 1); // one bit per pixel
		// Create a TiledImage using the SampleModel.
		visitados = new TiledImage(0, 0, width, height, ImageUtil.tileWidth,
				ImageUtil.tileHeight, sampleModel, null);

	}

	/**
	 * Acciones a realizar para liberar memoria
	 */
	private void liberarMemoria() {
		visitados = null;
		originalImage = null;
		// pixels = null;
		objetos = null;
		System.gc();
	}

	/**
	 * Retorna el objeto al que pertenece un pixel
	 * 
	 * @param pixel
	 * @param objetos
	 * @return
	 */
	public Objeto getObjetoContenedor(Pixel pixel, List<Objeto> objetos) {
		for (Objeto obj : objetos)
			if (obj.isPertenece(pixel))
				return obj;
		return null;
	}

	/**
	 * Retorna si un pixel pertenece a algunos de los objetos
	 * 
	 * @param pixel
	 * @param objetos
	 * @return
	 */
	public boolean perteneceAAlgunObjeto(Pixel pixel, List<Objeto> objetos) {
		for (Objeto obj : objetos)
			if (obj.isPertenece(pixel))
				return true;
		return false;
	}

	/**
	 * Completa los objetos con los pixels que pertenecen a cada uno
	 * 
	 * @param objetos
	 * @return
	 */
	public void completarObjeto(Objeto objeto) {
		if (objeto.getContorno().size() > 0) {
			List<Pixel> interior = new ArrayList<Pixel>();
			getPixelsInterior(objeto.getContorno().get(0),
					objeto.getContorno(), interior, objeto, visitados);
			objeto.setPuntos(interior);
		}
	}

	/**
	 * Pinta el contorno de los objetos detectados
	 * 
	 * @param objetos
	 * @return
	 */
	public PlanarImage pintarContorno(List<Objeto> objetos) {
		if (getOriginalImage() != null) {
			TiledImage ti = ImageUtil.createTiledImage(getOriginalImage(),
					ImageUtil.tileWidth, ImageUtil.tileHeight);

			for (int i = 0; i < objetos.size(); i++) {
				for (Pixel p : objetos.get(i).getContorno()) {

					pintarPixel(ti, p.getX(), p.getY(), getColorContorno());
				}
			}
			return ti;
		}
		return null;

	}

	/**
	 * Pinta el interior de los objetos detectados
	 * 
	 * @param objetos
	 * @return
	 */
	public PlanarImage pintarInterior(List<Objeto> objetos) {
		Color interior = new Color(50, 50, 50);
		if (getOriginalImage() != null) {
			TiledImage ti = ImageUtil.createTiledImage(getOriginalImage(),
					ImageUtil.tileWidth, ImageUtil.tileHeight);

			for (int i = 0; i < objetos.size(); i++) {
				for (Pixel p : objetos.get(i).getPuntos()) {

					pintarPixel(ti, p.getX(), p.getY(), interior);
				}
			}
			return ti;
		}
		return null;

	}

	public Color getColorPunto(Pixel pixel, PlanarImage ti) {
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

	/**
	 * Retorna los pixels que forman el contorno a partir de un pixel blanco
	 * dado
	 * 
	 * @param pixel
	 *            Pixel blanco
	 * @param offset
	 * @return Pixels que forman el contorno de un objeto
	 */
	public List<Pixel> getPixelsContorno(Pixel pixel, PlanarImage ti) {
		List<Pixel> contorno = new ArrayList<Pixel>();
		/**/
		pixel.setCol(getColorPunto(pixel, getOriginalImage()));

		Pixel nextContorno = pixel;
		Pixel pixelAnt = pixel.getAdyacente(Pixel.DIR_O, width, height);
		contorno.add(nextContorno);
		setVisitado(nextContorno);
		Pixel next = getNextContorno(nextContorno, pixelAnt, pixel);
		pixelAnt = nextContorno;
		nextContorno = next;

		while (nextContorno != null && !pixel.equals(nextContorno)) {
			/**/
			nextContorno.setCol(getColorPunto(pixel, ti));
			contorno.add(nextContorno);
			setVisitado(nextContorno);
			next = getNextContorno(nextContorno, pixelAnt, pixel);
			pixelAnt = nextContorno;
			nextContorno = next;
		}
		return contorno;
	}

	/**
	 * Retorna los pixels que forman el interior del objeto a partir de un pixel
	 * blanco dado
	 * 
	 * @param pixel
	 *            Pixel blanco
	 * @param offset
	 * @return Pixels que forman el contorno de un objeto
	 */
	public void getPixelsInterior(Pixel pixel, List<Pixel> contorno,
			List<Pixel> interior, Objeto o, PlanarImage ti) {
		for (int dir = 0; dir < 8; dir++) {
			Pixel actual = getAdyacenteNuevo(pixel, dir, getOriginalImage());
			if (actual != null)
				if (!contorno.contains(actual))
					if (!isVisitado(actual)) {
						setVisitado(actual);
						// if (o.isPerteneceTriangulo(actual)) {
						/**/
						actual.setCol(getColorPunto(pixel, getOriginalImage()));
						interior.add(actual);
						Pixel[] all = new Pixel[maximoPuntos];
						int cantidad = 0;
						all[cantidad] = actual;
						cantidad++;
						getPixelsAllInternal(all, cantidad, interior, o, ti);
						// }
					}
		}

	}

	private void getPixelsAllInternal(Pixel[] all, int cantidad,
			List<Pixel> interior, Objeto o, PlanarImage ti) {

		for (int i = 0; i < cantidad && i < maximoPuntos; i++) {
			Pixel pixel = all[i];
			if (pixel != null)
				for (int dir = 0; dir < 8; dir = dir + 2) {
					int direccion = adyacentes(dir);
					if (direccion != -1) {
						Pixel actual = getAdyacenteNuevo(pixel, direccion,
								getOriginalImage());
						if (actual != null)
							if ((actual != null) && (!isVisitado(actual))) {
								setVisitado(actual);
								if (o.isPerteneceTriangulo(actual)) {
									/**/
									actual.setCol(getColorPunto(pixel, getOriginalImage()));
									interior.add(actual);
									if (cantidad < maximoPuntos) {
										all[cantidad] = actual;
										cantidad++;
									}
								}
							}
					}
				}
		}

	}

	private int adyacentes(int ady) {
		if (ady == Pixel.DIR_E)
			return Pixel.DIR_E;
		if (ady == Pixel.DIR_N)
			return Pixel.DIR_N;
		if (ady == Pixel.DIR_O)
			return Pixel.DIR_O;
		if (ady == Pixel.DIR_S)
			return Pixel.DIR_S;
		return -1;

	}

	/**
	 * Retorna si un pixel fue visitado
	 * 
	 * @param pixel
	 * @return
	 */
	private boolean isVisitado(Pixel pixel) {
		/*
		 * if (Matriz[pixel.getY()][pixel.getX()] == 1) return true; return
		 * false;
		 */
		int[] value = ImageUtil
				.readPixel(pixel.getX(), pixel.getY(), visitados);
		if (value != null && value[0] == 1)
			return true;
		return false;

	}

	/**
	 * Setea como visitado un pixel
	 * 
	 * @param pixel
	 */
	private void setVisitado(Pixel pixel) {
		/*
		 * Matriz[pixel.getY()][pixel.getX()] = 1;
		 */
		int[] value = new int[1];
		value[0] = 1;
		ImageUtil.writePixel(pixel.getX(), pixel.getY(), value, visitados);
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
	public Pixel getPixel(int x, int y, PlanarImage image) {
		int[] pixel = ImageUtil.readPixel(x, y, (TiledImage) image,
				getRasterActual(), getTileXActual(), getTileYActual());
		int r = pixel[0];
		int g = pixel[0];
		int b = pixel[0];

		if (pixel.length == 3) {
			g = pixel[1];
			b = pixel[2];
		}
		Color colorPixel = new Color(r, g, b);
		return new Pixel(x, y, colorPixel);
	}

	/**
	 * Pinta el pixel (x,y) de la imagen con el color pasado como parámetro
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param color
	 */
	public void pintarPixel(TiledImage image, int x, int y, Color color) {
		int pixel[] = { color.getRed(), color.getGreen(), color.getBlue() };
		ImageUtil.writePixel(x, y, pixel, image);
	}

	/**
	 * Retorna el próximo pixel que forma el contorno de un objeto
	 * 
	 * @param pixel
	 *            Pixel actual que es contorno
	 * @param pixelAnt
	 *            Pixel anterior al actual que es contorno
	 * @param origen
	 *            Pixel desde el cuál se partió
	 * @param offset
	 *            Offset de la imagen
	 * @return
	 */
	public Pixel getNextContorno(Pixel pixel, Pixel pixelAnt, Pixel origen) {
		if (pixel.getX() == 781 && pixel.getY() == 645)
			System.out.println("entro");

		List<Pixel> posibles = new ArrayList<Pixel>();
		for (int dir = 0; dir < 8; dir++) {
			Pixel actual = getAdyacente(pixel, dir, getImage());
			if (pixelAnt != null && !pixelAnt.equals(origen)
					&& origen.equals(actual))
				return actual;
			if (actual != null && !pixel.equals(actual) && !isVisitado(actual)
					&& isContorno(actual)) {
				double lado = Pixel.lado(pixel, pixelAnt, actual);
				actual.setPeso(lado);
				posibles.add(actual);
			}
		}
		Collections.sort(posibles, new PixelComparator());
		if (posibles.size() > 0)
			return posibles.get(0);
		return null;
	}

	/**
	 * Retorna el próximo pixel que forma el contorno de un objeto teniendo en
	 * cuenta una imagen que contiene los bordes del objeto
	 * 
	 * @param pixel
	 *            Pixel actual que es contorno
	 * @param pixelAnt
	 *            Pixel anterior al actual que es contorno
	 * @param origen
	 *            Pixel desde el cuál se partió
	 * @param imageBordes
	 *            PlanarImage
	 * @return
	 */
	public Pixel getNextContorno(Pixel pixel, Pixel pixelAnt, Pixel origen,
			PlanarImage imageBordes) {
		List<Pixel> posibles = new ArrayList<Pixel>();
		for (int dir = 0; dir < 8; dir++) {
			Pixel actual = getAdyacente(pixel, dir, imageBordes);
			if (pixelAnt != null && !pixelAnt.equals(origen)
					&& origen.equals(actual))
				return actual;
			if (actual != null && !pixel.equals(actual) && !isVisitado(actual)
					&& isContorno(actual, imageBordes)) {
				double lado = Pixel.lado(pixel, pixelAnt, actual);
				if (lado >= 0) {
					actual.setPeso(lado);
					posibles.add(actual);
				}
			}
		}
		Collections.sort(posibles, new PixelComparator());
		if (posibles.size() > 0)
			return posibles.get(0);
		return null;
	}

	/**
	 * Retorna si un pixel es fondo de la imagen
	 * 
	 * @param pixel
	 * @return
	 */
	private boolean isFondo(Pixel pixel) {
		int umbralFondo = getColorUmbralFondo().getRed();
		return pixel.getCol().getRed() < umbralFondo;
	}

	/**
	 * Retorna el pixel adyacente a uno dado en una dirección determinada
	 * 
	 * @param pixel
	 *            Pixel actual
	 * @param direccion
	 *            Dirección para recuperar el adyacente
	 * @return Pixel adyacente
	 */
	public Pixel getAdyacente(Pixel pixel, int direccion, PlanarImage image) {
		Pixel ady = pixel.getAdyacente(direccion, width, height);
		if (ady != null) {
			return getPixel(ady.getX(), ady.getY(), image);
		}
		return null;
	}

	/**
	 * Retorna el pixel adyacente a uno dado en una dirección determinada
	 * 
	 * @param pixel
	 *            Pixel actual
	 * @param direccion
	 *            Dirección para recuperar el adyacente
	 * @return Pixel adyacente
	 */
	public Pixel getAdyacenteNuevo(Pixel pixel, int direccion, PlanarImage image) {
		Pixel ady = pixel.getAdyacente(direccion, width, height);
		if (ady != null) {
			return ady;
		}
		return null;
	}

	/**
	 * Devuelve el lado con el cuál pixel es contorno: si tiene un vecino que es
	 * fondo
	 * 
	 * @param pixel
	 * @param offset
	 * @param width
	 * @param nbands
	 * @param pixels
	 * @param umbralFondo
	 * @return
	 */
	public int getLadoContorno(Pixel pixel, int umbralFondo) {
		Pixel actual = null;
		actual = getAdyacente(pixel, Pixel.DIR_N, getImage());

		if (actual != null) {
			// actual = getPixel(actual.getX(), actual.getY(), offset);
			if (isFondo(actual))
				return Pixel.DIR_N;

		}
		actual = getAdyacente(pixel, Pixel.DIR_E, getImage());
		if (actual != null) {
			// actual = getPixel(actual.getX(), actual.getY(), offset);
			if (isFondo(actual))
				return Pixel.DIR_E;
		}
		actual = getAdyacente(pixel, Pixel.DIR_S, getImage());
		if (actual != null) {
			// actual = getPixel(actual.getX(), actual.getY(), offset);
			if (isFondo(actual))
				return Pixel.DIR_S;

		}
		actual = getAdyacente(pixel, Pixel.DIR_O, getImage());
		if (actual != null) {
			// actual = getPixel(actual.getX(), actual.getY(), offset);
			if (isFondo(actual))
				return Pixel.DIR_O;

		}
		return -1;
	}

	/**
	 * Devuelve si un pixel es contorno: si tiene un vecino que es fondo
	 * 
	 * @param pixel
	 * @param width
	 * @param nbands
	 * @param pixels
	 * @param umbralFondo
	 * @return
	 */
	public boolean isContorno(Pixel pixel) {
		int x = pixel.getX();
		int y = pixel.getY();

		if (isFondo(pixel))
			return false;
		Pixel actual = null;

		if (x - 1 < 0 || y - 1 < 0 || x + 1 >= width || y + 1 >= height)
			return true;

		if (x - 1 >= 0) {

			actual = getPixel(x - 1, y, getImage());
			if (isFondo(actual))
				return true;
		}
		if (y - 1 >= 0) {
			actual = getPixel(x, y - 1, getImage());
			if (isFondo(actual))
				return true;
		}

		if (y + 1 < height) {
			actual = getPixel(x, y + 1, getImage());
			if (isFondo(actual))
				return true;
		}

		if (x + 1 < width) {
			actual = getPixel(x + 1, y, getImage());
			if (isFondo(actual))
				return true;
		}

		return false;

	}

	/**
	 * Devuelve si un pixel es contorno fijandose solamente si el color del
	 * pixel no es el del fondo en la imagen de contornos
	 * 
	 * @param pixel
	 * @param width
	 * @param nbands
	 * @param pixels
	 * @param umbralFondo
	 * @param image
	 *            Imagen de contornos
	 * @return
	 */
	public boolean isContorno(Pixel pixel, PlanarImage image) {
		pixel = getPixel(pixel.getX(), pixel.getY(), image);
		if (isFondo(pixel))
			return false;
		return true;

	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int widht) {
		this.width = widht;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getBands() {
		return nbands;
	}

	public void setBands(int bands) {
		this.nbands = bands;
	}

	/*
	 * public List<Pixel> getVisitados() { return visitados; }
	 * 
	 * public void setVisitados(List<Pixel> visitados) { this.visitados =
	 * visitados; }
	 */

	public Color getColorContorno() {
		return colorContorno;
	}

	public void setColorContorno(Color colorContorno) {
		this.colorContorno = colorContorno;
	}

	public List<Objeto> getObjetos() {
		return objetos;
	}

	public void setObjetos(List<Objeto> objetos) {
		this.objetos = objetos;
	}

	public static void main(String[] args) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see procesamiento.ImageComand#execute()
	 */
	public PlanarImage execute() {
		if (getImage() != null) {
			try {

				// PlanarImage im0 = ImageUtil.createTiledImage(getImage(),
				// ImageUtil.tileWidth, ImageUtil.tileHeight);
				// PlanarImage im0 = ImageUtil.reformatImage(getImage(), new
				// Dimension(ImageUtil.tileWidth, ImageUtil.tileHeight));
				setImage(ImageUtil.createTiledImage(getImage(),ImageUtil.tileWidth, ImageUtil.tileHeight));

				List<Objeto> objetos = detectarObjetos();
				SepararObjetos separarObjetos = new SepararObjetos(getImage(),
						objetos, this);
				separarObjetos.execute();
				objetos = separarObjetos.getObjetos();

				setObjetos(objetos);
				for (Objeto obj : objetos) {
					ObjetoUtil.save(obj);
				}

				// PlanarImage output = pintarContorno(objetos);
				// completarObjetos(objetos);
				// clasificarObjetos();
				objetos = null;
				// JAI.create("filestore", output, "contorno.tif", "TIFF");
				return getImage();

			} catch (OutOfMemoryError e) {
				System.gc();
				System.err.println("Error al pintar el contorno");
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Detectar contorno Modificado";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		liberarMemoria();
	}

	public void limpiarVisitados() {
		/*
		 * for(int i = 0; i< getImage().getHeight(); i++) for(int j = 0; j<
		 * getImage().getHeight(); j++) this.Matriz[i][j] = 0;
		 */
		initVisitados();
	}

	/**
	 * Método encargado de detectar los objetos de la imagen
	 */
	public List<Objeto> detectarObjetos() {
		List<Objeto> objetos = new ArrayList<Objeto>();
		// initVisitados();
		int nombreObjeto = 1;
		if (getImage() != null) {
			// this.Matriz = new
			// int[getImage().getHeight()][getImage().getWidth()];
			setWidth(getImage().getWidth());
			setHeight(getImage().getHeight());
			TiledImage ti = (TiledImage) getImage();// ImageUtil.createTiledImage(getImage(),
													// ImageUtil.tileWidth,
													// ImageUtil.tileHeight);
			initVisitados();
			Color nuevo = new Color(0, 0, 0);

			PlanarImage tiOriginal = getOriginalImage();/*ImageUtil.createTiledImage(DetectarObjetos.getOriginalImage(), ImageUtil.tileWidth,
					ImageUtil.tileHeight);*/
			int tWidth = ImageUtil.tileWidth;
			int tHeight = ImageUtil.tileHeight;
			for (int tw = ti.getMinTileX(); tw <= ti.getMaxTileX(); tw++)
				for (int th = ti.getMinTileY(); th <= ti.getMaxTileY(); th++) {
					// Get a raster for that tile.
					WritableRaster wr = ti.getWritableTile(tw, th);
					setRasterActual(wr);
					setTileXActual(tw);
					setTileYActual(th);

					for (int w = 0; w < tWidth; w++)
						for (int h = 0; h < tHeight; h++) {
							int x = tw * tWidth + w;
							int y = th * tHeight + h;
							if (x < width && y < height) {

								Pixel pixelVerificar = new Pixel(x, y, nuevo);
								if (!isVisitado(pixelVerificar)) {
									int[] pixel2 = null;
									int[] pix = wr.getPixel(x, y, pixel2);

									int r = pix[0];
									int g = pix[0];
									int b = pix[0];
									if (pix.length == 3) {
										g = pix[1];
										b = pix[2];
									}

									Color colorPixel = new Color(r, g, b);
									Pixel pixel = new Pixel(x, y, colorPixel);
									if (!isFondo(pixel)
											&& !isVisitado(pixel)
											&& isContorno(pixel)
											//&& !perteneceAAlgunObjeto(pixel,
											//		objetos)
											){

										List<Pixel> pixelsContorno = getPixelsContorno(pixel, tiOriginal);
										if (pixelsContorno.size() > 0) {
											Objeto o = new Objeto();
											o.setContorno(pixelsContorno);
											if (o.validarContorno()) {
												o.setName("Maiz"+ nombreObjeto);
												nombreObjeto++;
												List<Pixel> interior = new ArrayList<Pixel>();
												getPixelsInterior(pixel,pixelsContorno,interior, o, tiOriginal);
												o.setPuntos(interior);
												objetos.add(o);
												System.out
														.println("Objeto catalogado: "
																+ o.getName()
																+ " - Puntos detectados: "
																+ interior.size());
											}
										}
									}
								}
							}
						}

				}
		}
		return objetos;
	}

	private int getTileXActual() {
		return tileXActual;
	}

	private void setTileXActual(int tileXActual) {
		this.tileXActual = tileXActual;
	}

	private int getTileYActual() {
		return tileYActual;
	}

	private void setTileYActual(int tileYActual) {
		this.tileYActual = tileYActual;
	}

	private WritableRaster getRasterActual() {
		return rasterActual;
	}

	private void setRasterActual(WritableRaster rasterActual) {
		this.rasterActual = rasterActual;
	}

}
