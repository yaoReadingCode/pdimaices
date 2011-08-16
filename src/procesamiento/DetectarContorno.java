package procesamiento;

import java.awt.Color;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import objeto.Clase;
import objeto.Objeto;
import objeto.ObjetoUtil;
import objeto.Pixel;
import objeto.PixelComparator;
import objeto.Rasgo;
import objeto.RasgoClase;
import procesamiento.clasificacion.AspectRatio;
import procesamiento.clasificacion.Circularidad;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.EvaluadorRasgo;
import aplicarFiltros.Visualizador;
import dataAcces.ObjectDao;

/**
 * Comando que detecta el contorno de 1 pixel exterior de los objetos de una
 * imagen binaria
 * 
 * @author oscar
 * 
 */
public class DetectarContorno extends AbstractImageCommand {
	private static final int maximoPuntos = 10000;
	private static final int ventanaPixel = 10;
	private static final double anguloDesvio = 45; 
	/**
	 * Imagen original
	 */
	private PlanarImage originalImage;
	
	private int Matriz[][] = null;
	private int MatrizContorno[][] = null;
	private int maxMatrixW = 0;
	private int maxMatrixH = 0;
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

	private int tWidth;

	private int tHeight;

	private int twGlobal;

	private int thGlobal;
	
	private EvaluadorClase evalObjetoCircular = null;

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
		
		RasgoClase rcCircularidad = new RasgoClase();
		rcCircularidad.setRasgo(new Rasgo("Circularidad"));
		
		RasgoClase rcAspectRadio = new RasgoClase();
		rcAspectRadio.setRasgo(new Rasgo("AspectRadio"));
		
	
		Circularidad circularidad = new Circularidad(rcCircularidad, 1.0, 0.7);
		AspectRatio aspectRadio = new AspectRatio(rcAspectRadio, 1.0, 0.6);

		List<EvaluadorRasgo> rasgos = new ArrayList<EvaluadorRasgo>();
		rasgos.add(circularidad);
		rasgos.add(aspectRadio);
		
		Clase claseObjetoCircular = new Clase();
		claseObjetoCircular.setNombre("Objeto circular");
		
		Clase claseEvaluador = new Clase();
		claseEvaluador.setNombre("Objeto circular");

		EvaluadorClase objetoCircular = new EvaluadorClase(claseObjetoCircular, rasgos);
		setEvalObjetoCircular(objetoCircular);
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
		/*
		 * // The image dimensions. int width = getImage().getWidth(); int
		 * height = getImage().getHeight(); // We need a sample model where the
		 * pixels are packed into one data // type. MultiPixelPackedSampleModel
		 * sampleModel = new MultiPixelPackedSampleModel( DataBuffer.TYPE_BYTE,
		 * ImageUtil.tileWidth, ImageUtil.tileHeight, 1); // one bit per pixel
		 * // Create a TiledImage using the SampleModel. visitados = new
		 * TiledImage(0, 0, width, height, ImageUtil.tileWidth,
		 * ImageUtil.tileHeight, sampleModel, null);
		 */
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
			List<Pixel> interior = new ArrayList<Pixel>();
			getPixelsInterior(objeto.getContorno().get(0),
					objeto.getContorno(), interior, objeto, getOriginalImage());
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
		contorno.add(nextContorno);
		setVisitado(nextContorno,true);
		List<Pixel> posibles = getNextContorno(nextContorno, pixelAnt, pixel,true);
		Pixel next = null;
		if (posibles != null && posibles.size() > 0)
			next = posibles.get(0);
		pixelAnt = nextContorno;
		nextContorno = next;
		Pixel inicio = null;
		Pixel medio = null;
		Pixel fin= null;
		int countPixel = 0;
		double anguloMejor = 0;
		Pixel puntoConflicto = null;
		int countPixelesDesvio = 0;
		while (nextContorno != null && !pixel.equals(nextContorno)) {
			nextContorno.setCol(getColorPunto(nextContorno, getOriginalImage()));
			contorno.add(nextContorno);
			setVisitado(nextContorno, true);
			posibles = getNextContorno(nextContorno, pixelAnt, pixel,true);
			next = null;
			if (posibles != null && posibles.size() > 0){
				next = posibles.get(0);
				if (next.getX() == 103 && next.getY() == 50)
					System.out.println("");
			}
			pixelAnt = nextContorno;
			nextContorno = next;
			
			if (countPixel > 2 * ventanaPixel){
				int posInicio = countPixel - 2 * ventanaPixel;
				int posMedio = countPixel - ventanaPixel;
				inicio = contorno.get(posInicio);
				medio = contorno.get(posMedio);
				fin = pixelAnt;
				double lado = Pixel.lado(inicio, medio, fin);
				if (lado < 0){
					double angulo = ObjetoUtil.calcularAngulo(inicio, medio, fin);
					if (angulo > anguloDesvio && (puntoConflicto == null || angulo > anguloMejor)){
						puntoConflicto = medio;
						anguloMejor = angulo;
					}
					countPixelesDesvio++;
					if(puntoConflicto != null && countPixelesDesvio > ventanaPixel){
						Pixel desvioHorario = puntoConflicto;
						int posDesvio = contorno.indexOf(desvioHorario);
						List<Pixel> resto = new ArrayList<Pixel>();
						if (posDesvio < contorno.size() - 1){
							for(int i = posDesvio + 1; i < contorno.size(); i++){
								Pixel p = contorno.get(i);
								resto.add(p);
							}
							for(Pixel p:resto){
								contorno.remove(p);
							}
						}
						List<Pixel> contornoAntihorario = new ArrayList<Pixel>();
						Pixel anterior = contorno.get(1); 
						
						Pixel desvioAntihorario = buscarDesvioAntihorario(pixel, anterior, desvioHorario, ti, contornoAntihorario);
						if (desvioAntihorario != null){
							List<Pixel> contornoResult = new ArrayList<Pixel>();
							List<Pixel> contornoDesmarcar = new ArrayList<Pixel>();
							contornoDesmarcar.addAll(resto);
							if (desvioAntihorario.equals(desvioHorario)){
								contornoResult.addAll(contorno);
								contornoAntihorario.remove(desvioHorario);
								for(int i = contornoAntihorario.size() - 1; i > -1; i--)
									contornoResult.add(contornoAntihorario.get(i));
							}
							else{
								contornoResult.addAll(contornoAntihorario);
								contornoResult.add(pixel);
								contornoDesmarcar.addAll(resto);
							}
							Objeto obj = new Objeto();
							obj.setOriginalImage(getOriginalImage());
							obj.setContorno(contornoResult);
							if (getEvalObjetoCircular().pertenece(obj, false)){
								desmarcarVisitados(resto);
								return contornoResult;
							}
						}
						if (resto != null)
							contorno.addAll(resto);
						puntoConflicto = null;
						countPixelesDesvio = 0;	
					}
				}
				else{
					puntoConflicto = null;
					countPixelesDesvio = 0;
				}
					
			}
			countPixel++;
		}
		return contorno;
	}
	
	/**
	 * Busca un camino de borde entre los puntos inicio y fin
	 * @param inicio
	 * @param fin
	 * @return
	 */
	private List<Pixel> findCamino(Pixel inicio, Pixel fin) {
		List<Pixel> contorno = new ArrayList<Pixel>();
		Pixel actual = inicio;
		double distanciaInicial = inicio.distancia(fin);
		while(actual != null && !actual.equals(fin)){
			int dir = fin.getDireccion(actual);
			double distancia = actual.distancia(fin);
			Pixel posible = getAdyacente(actual, dir, getImage());
			if (posible == null || (isVisitado(posible)&& !posible.equals(fin)))
				return null;
			Pixel nextContorno = null;
			if (!isFondo(posible)){
				nextContorno = posible;
			}
			else{
				double mejorDist = Double.MAX_VALUE;
				int[] recorrido = Pixel.getRecorridoHorarioAdayacentes(dir, 2);
				for(int pos : recorrido){
					posible = getAdyacente(actual, pos, getImage());
					if (posible != null && !isVisitado(posible) && !isFondo(posible)){
						double dist = posible.distancia(fin);
						if (dist < mejorDist){
							nextContorno = posible;
							mejorDist = dist;
						}
					}
				}
			}

			if (nextContorno == null){
				// Si la distancia es mayoy que la inicial entonces me desvie
				if (distancia > distanciaInicial){
					return null;
				}
				Pixel bordeMascercano = findBordeMasCercano(dir,actual,fin, null);
				if (bordeMascercano != null && !isFondo(bordeMascercano)){
					List<Pixel> lineaPixeles = ObjetoUtil.crearLinea(actual, bordeMascercano, width, height);
					contorno.addAll(lineaPixeles);
					for(Pixel p:lineaPixeles){
						contorno.add(p);
						setVisitado(p, true);
					}
					nextContorno = bordeMascercano;
				}
			}
			actual = nextContorno;
			if (actual != null && !actual.equals(fin)){
				actual.setCol(getColorPunto(actual, getOriginalImage()));
				contorno.add(actual);
				setVisitado(actual, true);
			}

		}
		if (actual != null){
			return contorno;
		}
		desmarcarVisitados(contorno);
		return null;
	}
	/**
	 * Busca el borde mas cercano en una direccion dada
	 * @param dir Direccion
	 * @param inicio Punto inicial
	 * @param fin Punto final
	 * @return
	 */
	private Pixel findBordeMasCercano(int dir, Pixel inicio, Pixel fin, Integer cantMaxima) {
		double distancia = Double.MAX_VALUE; 
		if (fin != null)
			distancia = inicio.distancia(fin);
		double distanciaActual = distancia - 1;
		Pixel actual = getAdyacente(inicio, dir, getImage());
		int i = 0;
		while (actual != null && !actual.equals(fin) && isFondo(actual) 
				&& !isVisitado(actual) && distanciaActual < distancia && (cantMaxima == null || i < cantMaxima )){
			distancia = distanciaActual;
			dir = fin.getDireccion(actual);
			actual = getAdyacente(actual, dir, getImage());
			if (actual != null && fin != null)
				distanciaActual = actual.distancia(fin);
			i++;
		}
		if (actual != null && !isFondo(actual))
			return actual;
		return null;
	}

	private Pixel buscarDesvioAntihorario(Pixel origen, Pixel anterior,Pixel desvioHorario, PlanarImage ti,
			List<Pixel> contornoAntihorario) {
		Pixel nextContorno = origen;
		Pixel pixelAnt = anterior;
		List<Pixel> posibles = getNextContorno(nextContorno, pixelAnt, origen,false);
		Pixel next = null;
		if (posibles != null && posibles.size() > 0)
			next = posibles.get(0);
		pixelAnt = nextContorno;
		nextContorno = next;
		Pixel inicio = null;
		Pixel medio = null;
		Pixel fin= null;
		int countPixel = 0;
		Pixel desvioAntihorario = null;
		Pixel puntoConflicto = null;
		double anguloMejor = 0;
		int countPixelesDesvio = 0;
		while (nextContorno != null && !origen.equals(nextContorno) && !desvioHorario.equals(nextContorno))/*(!contornoHorario.contains(nextContorno)))*/ {
			nextContorno.setCol(getColorPunto(nextContorno, getOriginalImage()));
			contornoAntihorario.add(nextContorno);
			setVisitado(nextContorno, true);
			posibles = getNextContorno(nextContorno, pixelAnt, origen,false);
			next = null;
			if (posibles != null && posibles.size() > 0)
				next = posibles.get(0);
			pixelAnt = nextContorno;
			nextContorno = next;
			
			if (countPixel > 2 * ventanaPixel){
				int posInicio = countPixel - 2 * ventanaPixel;
				int posMedio = countPixel - ventanaPixel;
				inicio = contornoAntihorario.get(posInicio);
				medio = contornoAntihorario.get(posMedio);
				fin = pixelAnt;
				if (medio.getX() == 153 && medio.getY() == 51)
					System.out.println("");

				double lado = Pixel.lado(inicio, medio, fin);
				if (lado > 0){
					double angulo = ObjetoUtil.calcularAngulo(inicio, medio, fin);
					if (angulo > anguloDesvio && (puntoConflicto == null || angulo > anguloMejor)){
						puntoConflicto = medio;
						anguloMejor = angulo;
					}
					countPixelesDesvio++;
					if (puntoConflicto != null && countPixelesDesvio > ventanaPixel){
						desvioAntihorario = puntoConflicto;
						int posDesvio = contornoAntihorario.indexOf(desvioAntihorario);
						List<Pixel> resto = new ArrayList<Pixel>();
						if (posDesvio < contornoAntihorario.size() - 1){
							for(int i = posDesvio + 1; i < contornoAntihorario.size(); i++){
								Pixel p = contornoAntihorario.get(i);
								resto.add(p);
							}
							for(Pixel p:resto){
								contornoAntihorario.remove(p);
							}

						}
						
						List<Pixel> camino = findCamino(desvioAntihorario, desvioHorario);
						if (camino != null && camino.size() > 0){
							if (resto != null && resto.size() > 0){
								marcarNuevoBorde(desvioAntihorario, desvioHorario, resto.get(0), camino);
								desmarcarVisitados(resto);
							}
							contornoAntihorario.addAll(camino);
							return desvioHorario;
						}
						if (resto != null)
							contornoAntihorario.addAll(resto);
						puntoConflicto = null;
						countPixelesDesvio = 0;
					}
				}
				else{
					puntoConflicto = null;
					countPixelesDesvio = 0;
				}
			}
			countPixel++;
		}
		if (nextContorno == null){
			desmarcarVisitados(contornoAntihorario);
		}
		return nextContorno;
	}
	
	/**
	 * Marca el nuevo borde resultante de dividir un objeto
	 * @param inicio
	 * @param fin
	 * @param inicioNuevoBorde
	 * @param camino
	 */
	private void marcarNuevoBorde(Pixel inicio, Pixel fin, Pixel inicioNuevoBorde, List<Pixel> camino) {
		//Pixel anterior = inicioNuevoBorde;
		List<Pixel> borde = new ArrayList<Pixel>();
		for(Pixel p:camino){
			//int dir = p.getDireccion(anterior) + 2;
			for (int dir = 0; dir < 8; dir ++){
				Pixel pBorde = p.getAdyacente(dir, width, height);
				if (pBorde != null && borde.contains(pBorde));
					borde.add(pBorde);
			}
			//anterior = p;
		}
		int[] newPixel = { 255, 255, 255 };
		TiledImage image = (TiledImage) getImage();
		for(Pixel p: borde){
			ImageUtil.writePixel(p.getX(), p.getY(), newPixel,image);
			unsetVisitado(p, true);
		}
		int[] pixelNegro = { 0, 0, 0 };
		//Eliminar puntos finales
		for(int i = borde.size() - 1; i > - 1; i--){
			Pixel p = borde.get(i);
			if (isPuntoFinalLinea(p, image))
				ImageUtil.writePixel(p.getX(), p.getY(), pixelNegro,image);
		}
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
			/*Para que complete los objetos cuando se divide un objeto en dos o mas*/
			twGlobal = ti.XToTileX(actual.getX());
			thGlobal = ti.YToTileY(actual.getY());
			
			if (actual != null)
				if (!contorno.contains(actual))
					/*if (!isVisitado(actual)) {
						setVisitado(actual);*/
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
					/*}*/
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
								
								if (o.isPerteneceTriangulo(actual)) {
									/**/
									setVisitado(actual, false);
									actual.setCol(getColorPunto(pixel,
											getOriginalImage()));
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
	private boolean isVisitado(Pixel p) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null)
			if (Matriz[pixel.getX()][pixel.getY()] == 1){
				return true;
			}else{ 
				return false;
			}	

		return true;

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

		return true;

		/*
		 * int[] value = ImageUtil .readPixel(pixel.getX(), pixel.getY(),
		 * visitados); if (value != null && value[0] == 1) return true; return
		 * false;
		 */

	}

	private Pixel convertirPixel(Pixel p){
		Pixel pixel = new Pixel((p.getX() - twGlobal * tWidth),(p.getY() - thGlobal * tHeight), p.getCol(), getImage().getMaxX(), getImage().getMaxY());
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
			if (pixel.getX() == 104 && pixel.getY() == 49)
				System.out.println("");
			Matriz[pixel.getX()][pixel.getY()] = 1;
			if (contorno)
				MatrizContorno[pixel.getX()][pixel.getY()] = 1;
		}
	}
	
	private void desmarcarVisitados(List<Pixel> lista){
		for(Pixel p:lista)
			unsetVisitado(p, true);
	}

	private void desmarcarObjeto(Objeto o){
		for(Pixel p:o.getContorno())
			unsetVisitado(p, true);
		for(Pixel p:o.getPuntos())
			unsetVisitado(p, false);
	}

	/**
	 * Desmarca como visitado un pixel
	 * 
	 * @param pixel
	 */
	private void unsetVisitado(Pixel p, boolean contorno) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null){
			if (pixel.getX() == 104 && pixel.getY() == 49)
				System.out.println("");
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
			recorrido = Pixel.getRecorridoHorarioAdayacentes(dirActual,2);
		else
			recorrido = Pixel.getRecorridoAntiHorarioAdayacentes(dirActual,2);
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
					&& isContorno(actual) && Pixel.distanciaLado(dirActual, dir) != 4) { 
				actual.setPeso(peso);
				posibles.add(actual);
			}
			peso--;
		}
		Collections.sort(posibles, new PixelComparator());
		if (posibles.size() > 0)
			return posibles;
		else{
			
			Pixel borde = findBordeMasCercano(dirActual, pixel, null, 5);
			if (borde != null){
				List<Pixel> conectarBorde = ObjetoUtil.crearLinea(pixel, borde, width, height);
				int[] newPixel = { 255, 255, 255 };
				TiledImage image = (TiledImage) getImage();
				for(Pixel p: conectarBorde){
					ImageUtil.writePixel(p.getX(), p.getY(), newPixel,image);
				}
				posibles.clear();
				if (conectarBorde.size() > 0){
					posibles.add(conectarBorde.get(0));
				}
				else
					posibles.add(borde);	
				return posibles;
			}
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
		int umbralFondo = getColorUmbralFondo().getRed();
		if (pixel.getCol() == null){
			pixel = getPixel(pixel.getX(), pixel.getY(), getImage());
		}
		
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

		if (x - 1 < 0 || y - 1 < 0 || x + 1 >= width || y + 1 >= height){
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

				setImage(ImageUtil.createTiledImage(getImage(),
						ImageUtil.tileWidth, ImageUtil.tileHeight));

				List<Objeto> objetos = detectarObjetos();
				SepararObjetos separarObjetos = new SepararObjetos(getImage(),
						objetos, this);
				separarObjetos.setOriginalImage(getOriginalImage());
				separarObjetos.setClasificador(getClasificador());
				separarObjetos.execute();
				objetos = separarObjetos.getObjetos();

				setObjetos(objetos);
				for (Objeto obj : objetos) {
					ObjetoUtil.save(obj);
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

	/**
	 * Método encargado de detectar los objetos de la imagen
	 */
	public List<Objeto> detectarObjetos() {
		List<Objeto> objetos = new ArrayList<Objeto>();
		int nombreObjeto = ObjectDao.getInstance().getCantidadObjetos() + 1;
		if (getImage() != null) {
			setWidth(getImage().getWidth());
			setHeight(getImage().getHeight());
			TiledImage ti = (TiledImage) getImage();
			
			Color nuevo = new Color(0, 0, 0);

			PlanarImage tiOriginal = getOriginalImage();
			
			tWidth = ImageUtil.tileWidth;
			tHeight = ImageUtil.tileHeight;
			
			setMaxMatrixH(tHeight*2);
			setMaxMatrixW(tWidth*2);
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
					twGlobal = tw;
					thGlobal = th;
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
									if (!isFondo(pixel) /*&& !isVisitado(pixel)*/
											&& isContorno(pixel)
									// && !perteneceAAlgunObjeto(pixel,
									// objetos)
									) {

										List<Pixel> pixelsContorno = getPixelsContorno(
												pixel, tiOriginal);
										if (pixelsContorno != null && pixelsContorno.size() > ventanaPixel) {
											Objeto o = new Objeto();
											o.setOriginalImage(getOriginalImage());
											o.setContorno(pixelsContorno);
											if (nombreObjeto == 143)
												System.out.println("");
											if (o.validarContorno()) {
												o.setName("Objeto"+ nombreObjeto);
												nombreObjeto++;
												List<Pixel> interior = new ArrayList<Pixel>();
												getPixelsInterior(pixel,
														pixelsContorno,
														interior, o, tiOriginal);
												o.setPuntos(interior);
												o.calcularMRC();
												
												if (getEvalObjetoCircular().pertenece(o, false)){
													borrarObjeto(o);
													initVisitados();
													
													objetos.add(o);
													String info = "Objeto catalogado: "
														+ o.getName()
														+ " - Puntos detectados: "
														+ interior.size();
													System.out.println(info);
													
													Visualizador.addLogInfo(info);
												}
												else{
													desmarcarObjeto(o);
												}
											}
											else{
												desmarcarVisitados(pixelsContorno);
											}
										}
										else{
											if (pixelsContorno != null)
												desmarcarVisitados(pixelsContorno);
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
	private void borrarObjeto(Objeto objeto){
		int[] newPixel = { 0, 0, 0 };
		TiledImage image = (TiledImage) getImage();
		for(Pixel p:objeto.getContorno()){
			ImageUtil.writePixel(p.getX(), p.getY(), newPixel,image);
		}
		for(Pixel p:objeto.getPuntos()){
			ImageUtil.writePixel(p.getX(), p.getY(), newPixel,image);
		}
		JAI.create("filestore", getImage(), objeto.getName()+"contorno.tif", "TIFF");		
	}

	public EvaluadorClase getEvalObjetoCircular() {
		return evalObjetoCircular;
	}

	public void setEvalObjetoCircular(EvaluadorClase evalObjetoCircular) {
		this.evalObjetoCircular = evalObjetoCircular;
	}
}
