package procesamiento;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import objeto.Objeto;
import objeto.Pixel;

/**
 * Comando que detecta el contorno de 1 pixel exterior de los objetos de una
 * imagen binaria
 * 
 * @author oscar
 * 
 */
public class DetectarContornoViejo extends AbstractImageCommand {
	/**
	 * Imagen original
	 */
	private PlanarImage originalImage;
	/**
	 * Color del fondo de la imagen
	 */
	private Color colorUmbralFondo;

	/**
	 * Color utilizado para marcar el contorno
	 */
	private Color colorContorno;

	/**
	 * Lista de objetos detectados
	 */
	private List<Objeto> objetos = new ArrayList<Objeto>();

	/**
	 * Marca de pixels visitados
	 */
	private List<Pixel> visitados;

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
	 * Pixels blancos (que no son fondo) de la imagen
	 */
	private List<Pixel> pixelsBlancos = new ArrayList<Pixel>();

	public DetectarContornoViejo(PlanarImage binaryImage, PlanarImage originalImage,
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
		visitados = new ArrayList<Pixel>();
		/*
		visitados = new int[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				visitados[i][j] = 0;
		*/
	}

	/**
	 * Acciones a realizar para liberar memoria
	 */
	private void liberarMemoria() {
		visitados = null;
		originalImage = null;
		//pixels = null;
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
	public List<Objeto> completarObjetos(List<Objeto> objetos) {
		if (getImage() != null) {
			for (Pixel pixel : getPixelsBlancos()) {
				Objeto objeto = getObjetoContenedor(pixel, objetos);
				if (objeto != null)
					objeto.agregarPunto(pixel);
			}
		}
		return objetos;
	}

	/**
	 * Detecta el contorno de un objeto
	 * 
	 * @param obj
	 * @return
	 */
	public void detectarContorno(Objeto obj) {
		List<Pixel> contorno = new ArrayList<Pixel>();
		for (Pixel pixel : obj.getPuntos()) {
			if (isContorno(pixel))
				contorno.add(pixel);
		}
		obj.setContorno(contorno);
	}

	/**
	 * Detecta los objetos de la imagen
	 * 
	 * @return Lista de objetos detectados
	 */
	public List<Objeto> detectarObjetos() {
		List<Objeto> objetos = new ArrayList<Objeto>();
		if (getImage() != null) {
			initVisitados();
			for (Pixel pixel : getPixelsBlancos()) {

				if (!isVisitado(pixel) && isContorno(pixel)
						&& !perteneceAAlgunObjeto(pixel, objetos)) {
					List<Pixel> pixelsContorno = getPixelsContorno(pixel);
					if (pixelsContorno.size() > 0) {
						System.out.println("Objeto detectado:");
						System.out.println(pixelsContorno);
						Objeto o = new Objeto();
						o.setContorno(pixelsContorno);
						objetos.add(o);
					}
				}
			/*
			setWidth(getImage().getWidth());
			setHeight(getImage().getHeight());
			SampleModel sm = getImage().getSampleModel();
			setBands(sm.getNumBands());
			Raster inputRaster = getImage().getData();
			int[] pixels = new int[nbands * width * height];
			inputRaster.getPixels(0, 0, width, height, pixels);
			setPixels(pixels);
			initVisitados();

			for (Pixel pixel : getPixelsBlancos()) {
				int offset = pixel.getX() * width * nbands + pixel.getY()
						* nbands;
				if (!isVisitado(pixel) && isContorno(pixel, offset)
						&& !perteneceAAlgunObjeto(pixel, objetos)) {
					List<Pixel> pixelsContorno = getPixelsContorno(pixel,
							offset);
					if (pixelsContorno.size() > 0) {
						Objeto o = new Objeto();
						o.setContorno(pixelsContorno);
						objetos.add(o);
					}
				}*/

			}
		}

		return objetos;
	}

	/**
	 * Pinta el contorno de los objetos detectados
	 * 
	 * @param objetos
	 * @return
	 */
	public PlanarImage pintarContorno(List<Objeto> objetos) {
		if (getOriginalImage() != null){
			TiledImage ti = ImageUtil.createTiledImage(getOriginalImage(), ImageUtil.tileWidth, ImageUtil.tileHeight);
			
			for (int i = 0; i < objetos.size(); i++) {
				for (Pixel p : objetos.get(i).getContorno()) {
					
					pintarPixel(ti, p.getX(), p.getY(), getColorContorno());
				}
			}
			return ti;

			/*
			int width = getOriginalImage().getWidth();
			int height = getOriginalImage().getHeight();
			SampleModel sm = getOriginalImage().getSampleModel();
			int nbands = sm.getNumBands();

			TiledImage ti = new TiledImage(getOriginalImage().createSnapshot(),	false);
			Raster inputRaster = ti.getData();
			 WritableRaster outputRaster = inputRaster.createCompatibleWritableRaster();
			//WritableRaster outputRaster = ti.getWritableTile(0, 0);
			int[] pixels = new int[nbands * width * height];
			inputRaster.getPixels(0, 0, width, height, pixels);
			//outputRaster.getPixels(0, 0, width, height, pixels);
			
			int offset;

			for (int i = 0; i < objetos.size(); i++) {
				for (Pixel p : objetos.get(i).getContorno()) {
					offset = p.getX() * width * nbands + p.getY() * nbands;
					pintarPixel(pixels, p.getX(), p.getY(), offset, getColorContorno());
				}
			}

			outputRaster.setPixels(0, 0, width, height, pixels);
			//TiledImage image = new TiledImage(getOriginalImage(), 1, 1);
			// TiledImage ti = (TiledImage) getOriginalImage();
			ti.setData(outputRaster);
			return ti;*/
		}
		return null;

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
	private List<Pixel> getPixelsContorno(Pixel pixel) {
		List<Pixel> contorno = new ArrayList<Pixel>();

		Pixel nextContorno = pixel;
		Pixel pixelAnt = pixel.getAdyacente(Pixel.DIR_O, width, height);
		contorno.add(nextContorno);
		setVisitado(nextContorno);
		Pixel next = getNextContorno(nextContorno, pixelAnt, pixel);
		pixelAnt = nextContorno;
		nextContorno = next;

		while (nextContorno != null && !pixel.equals(nextContorno)) {
			contorno.add(nextContorno);
			setVisitado(nextContorno);
			next = getNextContorno(nextContorno, pixelAnt, pixel);
			pixelAnt = nextContorno;
			nextContorno = next;
		}
		return contorno;
	}

	/**
	 * Retorna si un pixel fue visitado
	 * 
	 * @param pixel
	 * @return
	 */
	private boolean isVisitado(Pixel pixel) {
		return visitados.contains(pixel);
		/*
		if (visitados[pixel.getY()][pixel.getX()] == 1)
			return true;
		return false;*/
	}

	/**
	 * Setea como visitado un pixel
	 * 
	 * @param pixel
	 */
	private void setVisitado(Pixel pixel) {
		if (!visitados.contains(pixel))
			visitados.add(pixel);
		//visitados[pixel.getY()][pixel.getX()] = 1;
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
	public Pixel getPixel(int x, int y) {
		int[] pixel = ImageUtil.readPixel(x, y, (TiledImage) getImage());
		int r = pixel[0];
		int g = pixel[1];
		int b = pixel[2];
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
		int pixel[] = {color.getRed(), color.getGreen(), color.getBlue()};
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

		List<Pixel> posiblesContornos = new ArrayList<Pixel>();
		for (int dir = 0; dir < 8; dir++) {
			Pixel actual = getAdyacente(pixel, dir);
			if (pixelAnt != null && !pixelAnt.equals(origen)
					&& origen.equals(actual))
				return actual;
			if (actual != null && !isVisitado(actual)
					&& isContorno(actual))
				return actual;
			/*
			 * if (actual != null && !isVisitado(actual) && isContorno(actual,
			 * offset)) posiblesContornos.add(actual);
			 */
		}
		Double mejorContorno = null;
		Pixel contorno = null;
		if (pixelAnt != null)
			for (Pixel p : posiblesContornos) {
				double lado = Pixel.lado(pixelAnt, pixel, p);
				if (mejorContorno != null) {
					if (lado > mejorContorno) {
						mejorContorno = lado;
						contorno = p;
					}
				} else {
					mejorContorno = lado;
					contorno = p;
				}

			}
		posiblesContornos = null;
		return contorno;
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
	public Pixel getAdyacente(Pixel pixel, int direccion) {
		Pixel ady = pixel.getAdyacente(direccion, width, height);
		if (ady != null) {
			return getPixel(ady.getX(), ady.getY());
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
		actual = getAdyacente(pixel, Pixel.DIR_N);

		if (actual != null) {
			// actual = getPixel(actual.getX(), actual.getY(), offset);
			if (isFondo(actual))
				return Pixel.DIR_N;

		}
		actual = getAdyacente(pixel, Pixel.DIR_E);
		if (actual != null) {
			// actual = getPixel(actual.getX(), actual.getY(), offset);
			if (isFondo(actual))
				return Pixel.DIR_E;
		}
		actual = getAdyacente(pixel, Pixel.DIR_S);
		if (actual != null) {
			// actual = getPixel(actual.getX(), actual.getY(), offset);
			if (isFondo(actual))
				return Pixel.DIR_S;

		}
		actual = getAdyacente(pixel, Pixel.DIR_O);
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

		if (x == 67 && y == 22)
			System.out.println("Entro");
		if (isFondo(pixel))
			return false;
		Pixel actual = null;
		if (x - 1 >= 0) {

			actual = getPixel(x - 1, y);
			if (isFondo(actual))
				return true;
		}
		if (y - 1 >= 0) {
			actual = getPixel(x, y - 1);
			if (isFondo(actual))
				return true;
		}

		if (y + 1 < height) {
			actual = getPixel(x, y + 1);
			if (isFondo(actual))
				return true;
		}

		if (x + 1 < width) {
			actual = getPixel(x + 1, y);
			if (isFondo(actual))
				return true;
		}

		return false;
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
	public int[][] getVisitados() {
		return visitados;
	}

	public void setVisitados(int[][] visitados) {
		this.visitados = visitados;
	}*/
	public List<Pixel> getVisitados() {
		return visitados;
	}

	public void setVisitados(List<Pixel> visitados) {
		this.visitados = visitados;
	}


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
				setImage(ImageUtil.createTiledImage(getImage(), ImageUtil.tileWidth, ImageUtil.tileHeight));
				eliminarFondo();
				List<Objeto> objetos = detectarObjetos();

				for (Objeto obj : objetos)
					getPixelsBlancos().removeAll(obj.getPuntos());

				setObjetos(objetos);
				PlanarImage output = pintarContorno(objetos);
		 		completarObjetos(objetos);
				objetos = null;
				return output;
			} catch (OutOfMemoryError e) {
				System.gc();
				System.out.println("Error al pintar el contorno");
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
		return "Detectar contorno";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		liberarMemoria();
	}

	public List<Pixel> getPixelsBlancos() {
		return pixelsBlancos;
	}

	public void setPixelsBlancos(List<Pixel> pixelsBlancos) {
		this.pixelsBlancos = pixelsBlancos;
	}

	/**
	 * Método encargado de eliminar el fondo , completa la lista de pixels
	 * blancos
	 */
	public void eliminarFondo() {
		List<Pixel> pixelBlancos = new ArrayList<Pixel>();
		if (getImage() != null) {
			setWidth(getImage().getWidth());
			setHeight(getImage().getHeight());
			initVisitados();
			
			TiledImage ti = (TiledImage) getImage();//ImageUtil.createTiledImage(getImage(), ImageUtil.tileWidth, ImageUtil.tileHeight);
			for (int h = 0; h < height; h++)
				for (int w = 0; w < width; w++) {
					int[] pix = ImageUtil.readPixel(w, h, ti);
					int r = pix[0];
					int g = pix[1];
					int b = pix[2];
					Color colorPixel = new Color(r, g, b);
					Pixel pixel = new Pixel(w, h, colorPixel);
					if (!isFondo(pixel))
						pixelBlancos.add(pixel);
				}

			/*
			SampleModel sm = getImage().getSampleModel();
			setBands(sm.getNumBands());
			Raster inputRaster = getImage().getData();
			int[] pixels = new int[nbands * width * height];
			inputRaster.getPixels(0, 0, width, height, pixels);
			setPixels(pixels);
			int offset;

			for (int h = 0; h < height; h++)
				for (int w = 0; w < width; w++) {
					offset = h * width * nbands + w * nbands;

					int r = pixels[offset + 0];
					int g = pixels[offset + 1];
					int b = pixels[offset + 2];

					Color colorPixel = new Color(r, g, b);
					Pixel pixel = new Pixel(h, w, colorPixel);
					if (!isFondo(pixel))
						pixelBlancos.add(pixel);
				}
			*/
		}
		setPixelsBlancos(pixelBlancos);
	}

}
