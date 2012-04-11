package procesamiento;

import java.awt.Color;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import com.sun.xml.internal.ws.api.pipe.NextAction;

import objeto.Objeto;
import objeto.ObjetoUtil;
import objeto.Pixel;
import objeto.PixelComparator;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.ObjetoReferencia;
import aplicarFiltros.Visualizador;

/**
 * Comando que detecta el contorno de 1 pixel exterior de los objetos de una
 * imagen binaria
 * 
 * @author oscar
 * 
 */
public class DetectarContorno extends AbstractImageCommand {
//	private static final int maximoPuntos = 100000;
	private static final int ventanaPixel = 10;

	public static final String NOMBRE_DEFAULT = "";

	/**
	 * Imagen original
	 */
	private PlanarImage originalImage;
	
	/**
	 * Imagen Binaria resultado del proceso de binarizacio
	 */
	private PlanarImage binaryImage;
	
	private int Matriz[][] = null;
	private int MatrizContorno[][] = null;
	private int maxMatrixW = 1024;
	private int maxMatrixH = 1024;
	//private TiledImage visitados = null;
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

	private int tWidth;

	private int tHeight;

	/**
	 * Rango HSV del color del fondo
	 */
	private HSVRange rangeFondo = null;
	
	private EvaluadorClase evaluadorObjetoReferencia;
	private Objeto valor_MM_Pixel;
	//private double aspectRadioMejor;
	//private double circularidadMejor;
	private double areaMejor;
	
	/**
	 * Flag que indica se se debe tratar de separar objetos pegados
	 */
	private boolean separarObjetos = true;
	
	/**
	 * Flag que indica si se debe mostrar informacion de log
	 */
	private boolean visualizarInfoLog = true;
	
	/**
	 * Flag que indica si se debe asignar nombre a los objetos detectados
	 */
	private boolean asignarNombreObjeto = true;
	
	/**
	 * Flag que indica si se debe buscar el objeto de referencia
	 */
	private boolean buscarObjetoReferencia = true;
	
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
		this.Matriz  = null;
		this.Matriz = new int[maxMatrixW+1][maxMatrixH+1];
		this.MatrizContorno = new int[maxMatrixW+1][maxMatrixH+1];

	}

	

	public int getMaxMatrixW() {
		return maxMatrixW;
	}

	public void setMaxMatrixW(int maxMatrixW) {
		this.maxMatrixW = maxMatrixW;
	}

	

	public int getMaxMatrixH() {
		return maxMatrixH;
	}

	public void setMaxMatrixH(int maxMatrixH) {
		this.maxMatrixH = maxMatrixH;
	}

	/**
	 * Acciones a realizar para liberar memoria
	 */
	private void liberarMemoria() {
		//visitados = null;
		originalImage = null;
		// pixels = null;
		objetos = null;
		//System.gc();
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
			/*
			List<Pixel> interior = new ArrayList<Pixel>();
			getPixelsInterior(objeto.getContorno().get(0),
					objeto.getContorno(), interior, objeto, getOriginalImage());
			objeto.setPuntos(interior);
			*/
			List<Pixel> interior = new ArrayList<Pixel>();
			getPixelsInterior(objeto.getContorno(), interior, objeto, getOriginalImage());
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
	public Color getColorPunto(Pixel pixel) {
		/**/
		int[] pix = ImageUtil.readPixel(pixel.getX(), pixel.getY(), getImage());

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
		Pixel pixelAntPrimero = pixelAnt;
		contorno.add(nextContorno);
		//setVisitado(nextContorno, true);
		List<Pixel> posibles = getNextContorno(nextContorno, pixelAnt, pixel, true);
		Pixel next = null;
		if (posibles != null && posibles.size() > 0){
 			next = posibles.get(0);
 		}
		
		pixelAnt = nextContorno;
		nextContorno = next;
		List<Pixel> removeVisitados = new ArrayList<Pixel>();

		while (nextContorno != null && !pixel.equals(nextContorno)) {
			/**/
			nextContorno.setCol(getColorPunto(nextContorno, getOriginalImage()));
			contorno.add(nextContorno);
			setVisitado(nextContorno, true);
			List<Pixel> posiblesNext = getNextContorno(nextContorno, pixelAnt, pixel, true);
			next = null;
			if (nextContorno.getX()== 3017 && nextContorno.getY() == 436)
				System.out.println(nextContorno);
			if (posiblesNext != null && posiblesNext.size() > 0){
	 			next = posiblesNext.get(0);
	 		}

			if (next == null){
				contorno.remove(nextContorno);
				contorno.remove(pixelAnt);
				removeVisitados.add(nextContorno);
				next = pixelAnt;
				if (contorno.size() > 0){
					nextContorno = contorno.get(contorno.size() - 1);
				}
					
			}

			pixelAnt = nextContorno;
			nextContorno = next;
		}
		setVisitado(pixel, true);
		desmarcarVisitados(removeVisitados);
		return contorno;
	}
	
	/**
	 * Retorna los pixeles adyacentes a uno dado como parametro que no sean fondo
	 * @param pixel
	 * @return
	 */
	private List<Pixel> getAdyacentesNoFondo(Pixel pixel) {
		List<Pixel> posibles = new ArrayList<Pixel>();
		for (int dir = 0; dir < 8; dir++) {
			Pixel actual = getAdyacente(pixel, dir, getImage());
			if (!isFondo(actual)) { 
				posibles.add(actual);
			}
		}
		return posibles;
	}

	/**
	 * Indica si la lista1 contiene alguno de los elementos de la lista2 
	 * @param lista1
	 * @param lista2
	 * @return
	 */
	private boolean containsAny(List<Pixel> lista1,	List<Pixel> lista2) {
		if (lista1.size() == 0 || lista2.size() == 0)
			return false;
		for(Pixel p:lista2){
			if (lista1.contains(p))
				return true;
		}
		return false;
	}

//	/**
//	 * Retorna los pixels que forman el interior del objeto a partir de un pixel
//	 * blanco dado
//	 * 
//	 * @param pixel
//	 *            Pixel blanco
//	 * @param offset
//	 * @return Pixels que forman el contorno de un objeto
//	 */
//	private void getPixelsInterior(Pixel pixel, List<Pixel> contorno,
//			List<Pixel> interior, Objeto o, PlanarImage ti) {
//		for (int dir = 0; dir < 8; dir++) {
//
//			Pixel actual = getAdyacenteNuevo(pixel, dir, getOriginalImage());
//			/*Para que complete los objetos cuando se divide un objeto en dos o mas*/
//			twGlobal = ti.XToTileX(actual.getX());
//			thGlobal = ti.YToTileY(actual.getY());
//			
//			if (actual != null)
//				if (!contorno.contains(actual))
//					/*if (!isVisitado(actual)) {
//						setVisitado(actual);*/
//						// if (o.isPerteneceTriangulo(actual)) {
//						/**/
//						actual.setCol(getColorPunto(pixel, getOriginalImage()));
//						interior.add(actual);
//						Pixel[] all = new Pixel[maximoPuntos];
//						int cantidad = 0;
//						all[cantidad] = actual;
//						cantidad++;
//						getPixelsAllInternal(all, cantidad, interior, o, ti);
//						// }
//					/*}*/
//		}
//		
//	}

	/**
	 * Retorna los pixels que forman el interior del objeto a partir de un pixel
	 * blanco dado
	 * 
	 * @param pixel
	 *            Pixel blanco
	 * @param offset
	 * @return Pixels que forman el contorno de un objeto
	 */
	private void getPixelsInterior(List<Pixel> contorno,
			List<Pixel> interior, Objeto o, PlanarImage ti) {
		int minX = (int) o.getBoundingBox().getMinX();
		int minY = (int) o.getBoundingBox().getMinY();
		int maxX = (int) o.getBoundingBox().getMaxX();
		int maxY = (int) o.getBoundingBox().getMaxY();

		for(int x = minX; x <= maxX; x++){
			for(int y = minY; y <= maxY; y++){
				Pixel actual = getPixel(new Pixel(x, y, null), getOriginalImage());
				if (actual != null && o.isPertenece(actual)){
					if (actual.getCol() == null)
						actual.setCol(getColorPunto(actual, getOriginalImage()));
					interior.add(actual);
				}	
			}
		}
	}	
	
//	private void getPixelsAllInternal(Pixel[] all, int cantidad,
//			List<Pixel> interior, Objeto o, PlanarImage ti) {
//
//		for (int i = 0; i < cantidad && i < maximoPuntos; i++) {
//			Pixel pixel = all[i];
//			if (pixel != null)
//				for (int dir = 0; dir < 8; dir = dir + 2) {
//					int direccion = adyacentes(dir);
//					if (direccion != -1) {
//						Pixel actual = getAdyacenteNuevo(pixel, direccion,
//								getOriginalImage());
//						if (actual != null)
//							if ((actual != null) && (!isVisitado(actual))) {
//								
//								if (o.isPertenece(actual)) {
//									/**/
//									setVisitado(actual, false);
//									actual.setCol(getColorPunto(pixel,
//											getOriginalImage()));
//									interior.add(actual);
//									if (cantidad < maximoPuntos) {
//										all[cantidad] = actual;
//										cantidad++;
//									}
//								}
//							}
//					}
//				}
//		}
//
//	}

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

		return false;

		/*
		 * int[] value = ImageUtil .readPixel(pixel.getX(), pixel.getY(),
		 * visitados); if (value != null && value[0] == 1) return true; return
		 * false;
		 */

	}
	
	/**
	 * Retorna si el pixel es un contorno visitado
	 * 
	 * @param pixel
	 * @return
	 */
	private boolean isContornoVisitado(Pixel p) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null)
			if (MatrizContorno[pixel.getX()][pixel.getY()] == 1){
				return true;
			}else{ 
				return false;
			}	

		return false;

		/*
		 * int[] value = ImageUtil .readPixel(pixel.getX(), pixel.getY(),
		 * visitados); if (value != null && value[0] == 1) return true; return
		 * false;
		 */

	}

	private Pixel convertirPixel(Pixel p){
		//int tileX = getImage().XToTileX(p.getX());
		//int tileY = getImage().YToTileY(p.getY());
		int newX = p.getX() % maxMatrixW;
		int newY = p.getY() % maxMatrixH;
		Pixel pixel = new Pixel(newX,newY, p.getCol(), getImage().getMaxX(), getImage().getMaxY());
		if ((pixel.getY() >= maxMatrixH) || (pixel.getX() >= maxMatrixW))
			return null;
		if ((pixel.getY() < 0) || (pixel.getX() < 0))
			return null;
		return pixel;
	}

	/**
	 * Setea como visitado un pixel
	 * 
	 * @param pixel
	 */
	private void setVisitado(Pixel p, boolean contorno) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null){
			Matriz[pixel.getX()][pixel.getY()] = 1;
			if (contorno)
				MatrizContorno[pixel.getX()][pixel.getY()] = 1;
		}
	}
	
	protected void desmarcarVisitados(List<Pixel> lista){
		for(Pixel p:lista)
			unsetVisitado(p, true);
	}

	protected void marcarVisitados(List<Pixel> lista){
		for(Pixel p:lista)
			setVisitado(p, true);
	}

	protected void desmarcarObjeto(Objeto o){
		if (o.getContorno() != null){
			for(Pixel p:o.getContorno())
				unsetVisitado(p, true);
			for(Pixel p:o.getPuntos())
				unsetVisitado(p, false);
		}
	}

	/**
	 * Desmarca como visitado un pixel
	 * 
	 * @param pixel
	 */
	private void unsetVisitado(Pixel p, boolean contorno) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null){
			Matriz[pixel.getX()][pixel.getY()] = 0;
			if (contorno){
				MatrizContorno[pixel.getX()][pixel.getY()] = 0;
			}
		}
			
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
		return new Pixel(x, y, colorPixel,getImage().getMaxX(),getImage().getMaxY());
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
		if (p.getX() >= image.getMinX() &&
			p.getX() <= image.getMaxX() &&
			p.getY() >= image.getMinY() &&
			p.getY() <= image.getMaxY()){
			int[] pixel = ImageUtil.readPixel(p.getX(), p.getY(), image);
			if (pixel != null){
				int r = pixel[0];
				int g = pixel[0];
				int b = pixel[0];

				if (pixel.length == 3) {
					g = pixel[1];
					b = pixel[2];
				}
				Color colorPixel = new Color(r, g, b);
				p.setMaxX(image.getWidth());
				p.setMaxY(image.getHeight());
				p.setCol(colorPixel);

			}
			return p;
		}
		return null;
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
	public List<Pixel> getNextContorno(Pixel pixel, Pixel pixelAnt, Pixel origen, boolean horario) {
		List<Pixel> posibles = new ArrayList<Pixel>();
		int dirActual = pixel.getDireccion(pixelAnt);
		int[] recorrido = null;
		if (horario)
			recorrido = Pixel.getRecorridoHorarioAdayacentes(dirActual,3);
		else
			recorrido = Pixel.getRecorridoAntiHorarioAdayacentes(dirActual,3);
		int peso = recorrido.length;
		for (int dir : recorrido) {
			Pixel actual = getAdyacente(pixel, dir, getImage());
			if (pixelAnt != null && !pixelAnt.equals(origen)
					&& origen.equals(actual)){
				posibles.clear();
				posibles.add(actual);
				return posibles;
			}
				
			if (actual != null && !pixel.equals(actual) && !isVisitado(actual)
					&& isContorno(actual) && !Pixel.isOpuestoLado(dirActual, dir)) { 
				actual.setPeso(peso);
				posibles.add(actual);
			}
			peso--;
		}
		Collections.sort(posibles, new PixelComparator());
		if (posibles.size() == 0)
			System.out.println("No hay posibles");
		return posibles;
	}

	/**
	 * Retorna el próximo pixel que forma el contorno interno de un objeto
	 * 
	 * @param pixel
	 *            Pixel actual que es contorno
	 * @param pixelAnt
	 *            Pixel anterior al actual que es contorno
	 * @param origen
	 *            Pixel desde el cuál se partió
	 * @return
	 */
	public List<Pixel> getNextContornoInterno(Pixel pixel, Pixel pixelAnt, Pixel origen, boolean horario, Pixel origenExterior, Pixel finExterior) {
		List<Pixel> posibles = new ArrayList<Pixel>();
		int dirActual = pixel.getDireccion(pixelAnt);
		int[] recorrido = null;
		if (horario)
			recorrido = Pixel.getRecorridoHorarioAdayacentes(dirActual,2);
		else
			recorrido = Pixel.getRecorridoAntiHorarioAdayacentes(dirActual,2);
		int peso = recorrido.length;
		for (int dir : recorrido) {
			Pixel actual = getAdyacente(pixel, dir, getImage());
			if (pixelAnt != null && !pixelAnt.equals(origen) && origen.equals(actual)){
				posibles.clear();
				posibles.add(actual);
				return posibles;
			}
				
			if (actual != null && !pixel.equals(actual) && origenExterior.isAdyacente(actual)) {
				double lado = Pixel.lado(origenExterior, finExterior, actual);
				if (lado > 0 && !isVisitado(actual) && !Pixel.isOpuestoLado(dirActual, dir)){			
					actual.setPeso(peso);
					posibles.add(actual);
				}
			}
			peso--;
		}
		Collections.sort(posibles, new PixelComparator());
		return posibles;
	}

	/**
	 * Retorna si un pixel es fondo de la imagen
	 * 
	 * @param pixel
	 * @return
	 */
	public boolean isFondo(Pixel pixel) {
		int umbralFondo = getColorUmbralFondo().getRed();
		if (pixel.getCol() == null){
			pixel = getPixel(pixel.getX(), pixel.getY(), getImage());
		}
		
		if (pixel.getCol() != null)
			return pixel.getCol().getRed() < umbralFondo;
		return false;
	}
	
	/**
	 * Retorna si un pixel es fondo de la imagen
	 * 
	 * @param pixel
	 * @return
	 */
	public boolean isFondo(Pixel pixel, PlanarImage image) {
		int umbralFondo = getColorUmbralFondo().getRed();
		pixel = getPixel(pixel, image);
		if (pixel.getCol() != null)
			return pixel.getCol().getRed() < umbralFondo;
		return false;
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
	 * Devuelve si el pixel es un punto final de una linea
	 * 
	 * @param pixel
	 *            Pixel actual
	 * @param direccion
	 *            Dirección para recuperar el adyacente
	 * @return Pixel adyacente
	 */
	public boolean isPuntoFinalLinea(Pixel pixel, PlanarImage image) {
		int countAdyacentes = 0;
		Pixel ady = pixel.getAdyacente(Pixel.DIR_N, width, height);
		if (ady != null && !isFondo(ady)) {
			countAdyacentes++;
		}
		ady = pixel.getAdyacente(Pixel.DIR_E, width, height);
		if (ady != null && !isFondo(ady)) {
			countAdyacentes++;
		}
		ady = pixel.getAdyacente(Pixel.DIR_S, width, height);
		if (ady != null && !isFondo(ady)) {
			countAdyacentes++;
		}
		ady = pixel.getAdyacente(Pixel.DIR_O, width, height);
		if (ady != null && !isFondo(ady)) {
			countAdyacentes++;
		}
		if (countAdyacentes < 2)
			return true;
		return false;
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

		if (x <= getImage().getMinX() || y <= getImage().getMinY() || x >= getImage().getMaxX() || y >= getImage().getMaxY()
				){
			pixel.setCol(Color.WHITE);
			int[] newPixel = { 255, 255, 255 };
			TiledImage image = (TiledImage) getImage();
			ImageUtil.writePixel(pixel.getX(), pixel.getY(), newPixel,image);
			return true;
		}

		if (x - 1 >= 0) {

			actual = getPixel(x - 1, y, getImage());
			if (isFondo(actual) || isContornoVisitado(actual))
				return true;
		}
		if (y - 1 >= 0) {
			actual = getPixel(x, y - 1, getImage());
			if (isFondo(actual) || isContornoVisitado(actual))
				return true;
		}

		if (y + 1 < height) {
			actual = getPixel(x, y + 1, getImage());
			if (isFondo(actual) || isContornoVisitado(actual))
				return true;
		}

		if (x + 1 < width) {
			actual = getPixel(x + 1, y, getImage());
			if (isFondo(actual) || isContornoVisitado(actual))
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
				//JAI.create("filestore", getImage(), "pre_detectar_contorno.tif", "TIFF");

				setImage(ImageUtil.createTiledImage(getImage(),
						ImageUtil.tileWidth, ImageUtil.tileHeight));
				setWidth(getImage().getWidth());
				setHeight(getImage().getHeight());
				List<Objeto> objetos = detectarObjetos();
				if (this.isSepararObjetos()){
					SepararObjetos separarObjetos = new SepararObjetos(getImage(), objetos, this);
					separarObjetos.setOriginalImage(getOriginalImage());
					separarObjetos.setClasificador(getClasificador());
					separarObjetos.execute();
					objetos = separarObjetos.getObjetos();
				}
				
				if (isBuscarObjetoReferencia() && valor_MM_Pixel != null){
					objetos.remove(valor_MM_Pixel);
					System.out.println("**************************************************");
					System.out.println("Objeto Referencia: " + valor_MM_Pixel.getName()
							+ " - diametro en MM: " + ObjetoReferencia.mayorDiametroEnMM(valor_MM_Pixel));
					
					for (Objeto o : objetos) {
						System.out
						.println("**************************************************");
						System.out.println("Objeto: " + o.getName()
								+ " - diametro en MM: " + ObjetoReferencia.mayorDiametroEnMM(o));
						System.out.println("Es maiz quebrado: " + ObjetoReferencia.isGranoQuebrado(o));
						
					}
					System.out.println("Error en MM de la imagen: " + ObjetoReferencia.getErrorCalculos());
					System.out
							.println("**************************************************");
				}
				
				setObjetos(objetos);
				for (Objeto obj : objetos) {
					ObjetoUtil.save(obj, getRangeFondo().getColorMedio());
				}
				if (isBuscarObjetoReferencia() && ObjetoReferencia.getReferencia() != null){
					ObjetoUtil.save(ObjetoReferencia.getReferencia(), getRangeFondo().getColorMedio());
				}

				objetos = null;
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
	
	public void isObjSpecial(Objeto objeto){
		if ( isBuscarObjetoReferencia() && getEvaluadorObjetoReferencia().pertenece(objeto, false)){
			if (areaMejor < objeto.getArea()){
				areaMejor = objeto.getArea();
				valor_MM_Pixel = objeto;
				ObjetoReferencia.setObjetoReferencia(objeto);
			}
		}
	}

	/**
	 * Método encargado de detectar los objetos de la imagen
	 */
	public List<Objeto> detectarObjetos() {
		List<Objeto> objetos = new ArrayList<Objeto>();
		//int nombreObjeto = getClasificador().getCantidadObjetos() + 1;
		JAI.create("filestore", getImage(), "contorno.tif", "TIFF");
		if (getImage() != null) {
			setWidth(getImage().getWidth());
			setHeight(getImage().getHeight());
			TiledImage ti = (TiledImage) getImage();
			
			Color nuevo = new Color(0, 0, 0);

			PlanarImage tiOriginal = getOriginalImage();
			
			tWidth = ImageUtil.tileWidth;
			tHeight = ImageUtil.tileHeight;
			
			//setMaxMatrixH(tHeight*2);
			//setMaxMatrixW(tWidth*2);
			//initVisitados();
			
			for (int th = ti.getMinTileY(); th <= ti.getMaxTileY(); th++) 
				for (int tw = ti.getMinTileX(); tw <= ti.getMaxTileX(); tw++){
					// Get a raster for that tile.
					WritableRaster wr = ti.getWritableTile(tw, th);
					setRasterActual(wr);
					setTileXActual(tw);
					setTileYActual(th);
					/**
					 * Parametros para setVisitados
					 */
					initVisitados();
					
					for (int h = 0; h < tHeight; h++) 
						for (int w = 0; w < tWidth; w++){
							int x = tw * tWidth + w;
							int y = th * tHeight + h;
							if (x < width && y < height) {

								Pixel pixelVerificar = new Pixel(x, y, nuevo, getImage().getMaxX(), getImage().getMaxY());
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
									Pixel pixel = new Pixel(x, y, colorPixel, getImage().getMaxX(), getImage().getMaxY());
									if (!isFondo(pixel) && isContorno(pixel)) {
										List<Pixel> pixelsContorno = getPixelsContorno(pixel, tiOriginal);
										Objeto o = new Objeto();
										o.setOriginalImage(getOriginalImage());
										o.setContorno(pixelsContorno);
										if (validarObjeto(o)){
											completarObjeto(o);
											if (isAsignarNombreObjeto()){
												int nombreObjeto = getClasificador().getCantidadObjetos() + 1;
												getClasificador().aumentarCantidadObjetos();
												o.setName(NOMBRE_DEFAULT+ nombreObjeto);
											}
											borrarObjeto((TiledImage)getImage(), o, Color.black);
											if (isBuscarObjetoReferencia()){
												this.isObjSpecial(o);
											}
											initVisitados();
											if (o.getPuntos().size() > 0){
												objetos.add(o);
												if (isVisualizarInfoLog()){
													String info = "Objeto catalogado: "
														+ o.getName()
														+ " - Puntos detectados: "
														+ o.getPuntos().size();
													System.out.println(info);
													Visualizador.addLogInfo(info);
												}
											}
										}
									}
									setVisitado(pixelVerificar, true);
								}
							}
						}

				}
		}
		return objetos;
	}

	private boolean validarObjeto(Objeto o) {
		if (o.getContorno() != null && o.getContorno().size() > ventanaPixel && o.validarContorno()){
			o.calcularMRC();
			//if (getEvalObjetoCircular().pertenece(o, false))
				return true;
		}
		return false;
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
	
	/**
	 * Borra el objeto de la imagen
	 * @param objeto
	 */
	public void borrarObjeto(TiledImage image, Objeto objeto, Color fondo){
		//JAI.create("filestore", image, "parcial_antes_"+objeto.getName()+".tif", "TIFF");
		int[] newPixel = { fondo.getRed(), fondo.getGreen(), fondo.getBlue()};
		for(Pixel p:objeto.getContorno()){
			ImageUtil.writePixel(p.getX(), p.getY(), newPixel,image);
		}
		for(Pixel p:objeto.getPuntos()){
			ImageUtil.writePixel(p.getX(), p.getY(), newPixel,image);
		}
		//JAI.create("filestore", image, "parcial_desp_"+objeto.getName()+".tif", "TIFF");
		//ObjetoUtil.save(objeto, Color.black);
	}

	public PlanarImage getBinaryImage() {
		return binaryImage;
	}

	public void setBinaryImage(PlanarImage binaryImage) {
		this.binaryImage = binaryImage;
	}

	public HSVRange getRangeFondo() {
		return rangeFondo;
	}

	public void setRangeFondo(HSVRange rangeFondo) {
		this.rangeFondo = rangeFondo;
	}

	public boolean isSepararObjetos() {
		return separarObjetos;
	}

	public void setSepararObjetos(boolean separarObjetos) {
		this.separarObjetos = separarObjetos;
	}

	public boolean isVisualizarInfoLog() {
		return visualizarInfoLog;
	}

	public void setVisualizarInfoLog(boolean visualizarInfoLog) {
		this.visualizarInfoLog = visualizarInfoLog;
	}

	public boolean isAsignarNombreObjeto() {
		return asignarNombreObjeto;
	}

	public void setAsignarNombreObjeto(boolean asignarNombreObjeto) {
		this.asignarNombreObjeto = asignarNombreObjeto;
	}

	public boolean isBuscarObjetoReferencia() {
		return buscarObjetoReferencia;
	}

	public void setBuscarObjetoReferencia(boolean buscarObjetoReferencia) {
		this.buscarObjetoReferencia = buscarObjetoReferencia;
	}

	public EvaluadorClase getEvaluadorObjetoReferencia() {
		if (evaluadorObjetoReferencia == null){
			evaluadorObjetoReferencia = getClasificador().getEvaluadorClaseObjetoReferencia();
		}
		return evaluadorObjetoReferencia;
	}

	public void setEvaluadorObjetoReferencia(EvaluadorClase evaluadorObjetoReferencia) {
		this.evaluadorObjetoReferencia = evaluadorObjetoReferencia;
	}

}


