package objeto;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.jai.PlanarImage;

import procesamiento.ImageUtil;
import procesamiento.RgbHsv;

public class Objeto {
	
	private Long id;
	
	/**
	 * Lista de rasgos que caracterizan al objeto
	 */
	private List<RasgoObjeto> rasgoObjetos = new ArrayList<RasgoObjeto>();
	
	/**
	 * Clases a la que pertenece el objeto
	 */
	private List<ClaseObjeto> claseObjetos = new ArrayList<ClaseObjeto>();
	
	/**
	 * Lista de pixeles del objeto
	 */
	private List<Pixel> puntos = new ArrayList<Pixel>();

	/**
	 * Lista de triángulos que forman el objeto. Se calculan a partir de la
	 * lista de pixeles del objeto
	 */
	private List<Triangulo> triangulosContenedores = new ArrayList<Triangulo>();

	/**
	 * Lista de pixeles del objeto
	 */
	private List<Pixel> contorno = new ArrayList<Pixel>();

	private double radio;

	private String name = "";

	private BoundingBox boundingBox;

	private Color colorPromedio = null;

	private Pixel pixelMedio = null;
	
	//Puntos maximos y minimos
	private int xMin=1000000;
	
	private int xMax=0;
	
	private int yMin=1000000;
	
	private int yMax=0;
	
	/**
	 * Path de la imagen en disco
	 */
	private String pathImage;

	private double[] acumuladorR = null;

	private double[] acumuladorG = null;

	private double[] acumuladorB = null;
	
	private Pixel pixelPunta1 = null;
	
	private Pixel pixelPunta2 = null;
	
	private PlanarImage originalImage;	
	
	public double[] getAcumuladorR() {
		if (acumuladorR == null) this.colorPromedio();
		return acumuladorR;
	}

	public double[] getAcumuladorG() {
		if (acumuladorR == null) this.colorPromedio();
		return acumuladorG;
	}

	public double[] getAcumuladorB() {
		if (acumuladorR == null) this.colorPromedio();
		return acumuladorB;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPuntos(List<Pixel> puntos) {
		this.puntos = puntos;
	}

	public Objeto() {
		puntos = new ArrayList<Pixel>();

	}

	public int medida() {
		return puntos.size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void agregarPunto(Pixel p) {
		puntos.add(p);

	}

	public void agregarPunto(int i, int j, Color c) {
		Pixel p = new Pixel(i, j, c);
		puntos.add(p);
	}

	public void agregarPunto(int i, int j, int R, int G, int B) {
		Pixel p = new Pixel(i, j, R, G, B);
		puntos.add(p);
	}

	public List<Triangulo> getTriangulosContenedores() {
		return triangulosContenedores;
	}

	public void setTriangulosContenedores(List<Triangulo> triangulosContenedores) {
		this.triangulosContenedores = triangulosContenedores;
	}

	public List<Pixel> getPuntos() {
		return puntos;
	}

	public int getxMin() {
		return xMin;
	}

	public void setxMin(int xMin) {
		this.xMin = xMin;
	}

	public int getxMax() {
		return xMax;
	}

	public void setxMax(int xMax) {
		this.xMax = xMax;
	}

	public int getyMin() {
		return yMin;
	}

	public void setyMin(int yMin) {
		this.yMin = yMin;
	}

	public int getyMax() {
		return yMax;
	}

	public void setyMax(int yMax) {
		this.yMax = yMax;
	}
	
	public String getPathImage() {
		return pathImage;
	}

	public void setPathImage(String pathImage) {
		this.pathImage = pathImage;
	}

	/**
	 * Retorna el color promedio del objeto
	 * 
	 * @return
	 */
	public Color colorPromedio() {
		if (colorPromedio == null) {
			int R = 0;
			int G = 0;
			int B = 0;
			/**Histograma*/
			double[] acumuladorR = new double[256];
			double[] acumuladorG = new double[256];
			double[] acumuladorB = new double[256];
			
			Iterator<Pixel> i = puntos.iterator();
			while (i.hasNext()) {
				Pixel p = (Pixel) i.next();
				R = R + p.getCol().getRed();
				G = G + p.getCol().getGreen();
				B = B + p.getCol().getBlue();
				acumuladorR[p.getCol().getRed()]++;
				acumuladorG[p.getCol().getGreen()]++;
				acumuladorB[p.getCol().getBlue()]++;
				
			}
			R = (R / puntos.size()) % 255;
			G = (G / puntos.size()) % 255;
			B = (B / puntos.size()) % 255;
			colorPromedio = new Color(R, G, B);
		}
		return colorPromedio;

	}

	/**
	 * Retorna si un pixel es adyacente a algunos de los pixeles del objeto
	 * 
	 * @param pixel
	 * @return
	 */
	public boolean isAdyacentePixel(Pixel pixel) {
		for (Pixel p : getPuntos()) {
			if (p.isAdyacente(pixel))
				return true;
		}

		return false;
	}

	public List<Pixel> getContorno() {
		return contorno;
	}
	
	public boolean validarContorno(){
		if (contorno != null && contorno.size() >= 3){
			Pixel inicio = contorno.get(0);
			Pixel fin = contorno.get(contorno.size() - 1);
			if (inicio.isAdyacente(fin))
				return true;
			else {
				System.err.println("Contorno de objeto invalido. Los pixels "+ inicio + " y " + fin + " no son adyacentes");
			}
		}
		return false;
	}

	public void setContorno(List<Pixel> contorno) {
		this.contorno = contorno;
		//validarContorno(contorno);
		if (contorno != null){
			calcularMedioYBoundingBox();
			calcularTriangulosContenedores();
		}
	}
	
	/**
	 * calcula el minimo rectangulo contenedor y lo asigna como bounding box
	 */
	public void calcularMRC(){
		Objeto objAux = this.clonar();
		double altoMRC = this.getAlto();
		double anchoMRC = this.getAncho();
		double areaMin = altoMRC * anchoMRC;
		double anguloRot = 3;
		for (double anguloTot = anguloRot; anguloTot < 360; anguloTot += anguloRot) {
			objAux.rotarContorno(anguloRot);
			double alto = objAux.getAlto();
			double ancho = objAux.getAncho();
			double area = alto * ancho;
			if (area < areaMin) {
				anchoMRC = ancho;
				altoMRC = alto;
				areaMin = ancho * alto;
			}
		}
		
		this.boundingBox = objAux.getBoundingBox();
	}

	/**
	 * Retorna si un pixel se encuentra dentro de un objeto (pertenece a un
	 * objeto)
	 * 
	 * @param p
	 * @return
	 */
	public boolean isPertenece(Pixel p) {
		if (validarContorno())
			if (isPerteneceTriangulo(p)) return true;
		return false;
	}
	
	public boolean isPerteneceTriangulo(Pixel p){
		for (Triangulo t : getTriangulosContenedores()) {
			if (t.isPertenece(p))
				return true;
		}
		return false;
	}

	/**
	 * Calcula el punto medio y el bounding box desde el contorno
	 */
	private void calcularMedioYBoundingBox() {
		List<Pixel> contorno = getContorno();
		double x = 0;
		double y = 0;
		double minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
		Integer maxXImage = null;
		Integer maxYImage = null;
		if (contorno.size() > 0){
			maxXImage = contorno.get(0).getMaxX();
			maxYImage = contorno.get(0).getMaxY();
		}
		for (Pixel p : contorno) {
			x += p.getXDouble();
			y += p.getYDouble();
			if (p.getXDouble() < minX)
				minX = p.getXDouble();
			if (p.getYDouble() < minY)
				minY = p.getYDouble();
			if (p.getXDouble() > maxX)
				maxX = p.getXDouble();
			if (p.getYDouble() > maxY)
				maxY = p.getYDouble();
		}
		setBoundingBox(new BoundingBox(minX, minY, maxX, maxY));
		Pixel medio = new Pixel( x / contorno.size(), y / contorno.size(), null,maxXImage, maxYImage);
		setPixelMedio(medio);
	}

	/**
	 * Calcula los triángulos que forman el objeto a partir de la lista de
	 * pixeles que forman el contorno del objeto
	 */
	public void calcularTriangulosContenedores() {
		if (getContorno() != null) {

			double radioMin = 0;
			double radioMax = 0;
			
			int paso = 5;

			if (contorno.size() > 1) {
				List<Triangulo> triangulos = new ArrayList<Triangulo>();
				Pixel pixeltrianguloAnt = contorno.get(0);
				Pixel primero = contorno.get(0);
				Pixel actual = contorno.get(1);
				//int ladoAnt = actual.getLado(pixeltrianguloAnt);
				radioMin = getPixelMedio().distancia(primero);
				radioMax = radioMin;
				for (int i = 2; i < contorno.size(); i++) {
					Pixel ant = contorno.get(i - 1);
					actual = contorno.get(i);
					//int lado = actual.getLado(ant);
					double dist = getPixelMedio().distancia(contorno.get(i - 1));

					if (dist > radioMax)
						radioMax = dist;
					if (dist < radioMin)
						radioMin = dist;
					Triangulo t = new Triangulo(getPixelMedio(), pixeltrianguloAnt, ant);
					if (t.validarTriangulo() /*(lado != ladoAnt*/) {
						triangulos.add(t);
						//ladoAnt = lado;
						pixeltrianguloAnt = ant;
					}
				}
				Triangulo t = new Triangulo(getPixelMedio(), pixeltrianguloAnt,	primero);
				triangulos.add(t);
				setTriangulosContenedores(triangulos);
				setRadio((radioMax + radioMin) / 2);
				//setRadio(radioMin);
			}
		}
	}

	/**
	 * calcula el area del objeto
	 * 
	 * @return
	 */
	public int getArea() {
		if (getPuntos() != null)
			return getPuntos().size();
		return 0;
	}

	/**
	 * Calcula la longitud del perímetro del objeto
	 * 
	 * @return
	 */
	public int getLongitudPerimetro() {
		if (getContorno() != null)
			return getContorno().size();
		return 0;
	}

	public double getRadio() {
		return this.radio;
	}

	public void setRadio(double radio) {
		this.radio = radio;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	public double getAlto() {
		if (getBoundingBox() != null)
			return getBoundingBox().getMaxX() - getBoundingBox().getMinX();
		return 0;
	}

	public double getAncho() {
		if (getBoundingBox() != null)
			return getBoundingBox().getMaxY() - getBoundingBox().getMinY();
		return 0;
	}

	/**
	 * Rota el contorno del objeto un angulo especificado
	 * @param angulo
	 */
	public void rotarContorno(double angulo) {
		rotarPixeles(getContorno(), angulo);
		calcularMedioYBoundingBox();
	}

	private void rotarPixeles(List<Pixel> lista, double angulo){
		for (Pixel p : lista) {
			p.restar(getPixelMedio());
			p.rotar(angulo);
			p.sumar(getPixelMedio());
		}
	}
	
	/**
	 * Rota el contorno del objeto un angulo especificado
	 * @param angulo
	 */
	public void rotar(double angulo) {
		rotarPixeles(getContorno(), angulo);
		rotarPixeles(getPuntos(), angulo);
		calcularMedioYBoundingBox();
	}	

	public Pixel getPixelMedio() {
		return pixelMedio;
	}

	public void setPixelMedio(Pixel pixelMedio) {
		this.pixelMedio = pixelMedio;
	}

	/**
	 * Cociente entre el área y la longitud del perímetro. Si es circular el
	 * cociente debe ser cercano a 4PI
	 * 
	 * @return
	 */
	public double getCircularidad() {
		int area = getArea();
		int perimetro = getLongitudPerimetro();
		if (perimetro != 0)
			return area / perimetro;
		return 0;
	}

	public Objeto clonar() {
		Objeto obj = new Objeto();
		obj.setOriginalImage(this.getOriginalImage());
		List<Pixel> contorno = new ArrayList<Pixel>();
		for (Pixel p : getContorno()) {
			contorno.add(p.clonar());
		}
		obj.setContorno(contorno);
		return obj;
	}
	
	public void calcularMaximosMinimos() {
			Iterator<Pixel> i = contorno.iterator();
			while (i.hasNext()) {
				Pixel p = (Pixel) i.next();
				if (p.getX() < xMin ) xMin = p.getX();
				if (p.getY() < yMin ) yMin = p.getY();
				
				if (p.getX() > xMax ) xMax = p.getX();
				if (p.getY() > yMax ) yMax = p.getY();
			}

	}
	
	public List<RasgoObjeto> getRasgos() {
		return rasgoObjetos;
	}

	public void setRasgos(List<RasgoObjeto> rasgoObjetos) {
		this.rasgoObjetos = rasgoObjetos;
	}

	public List<ClaseObjeto> getClases() {
		return claseObjetos;
	}

	public void setClases(List<ClaseObjeto> claseObjetos) {
		this.claseObjetos = claseObjetos;
	}
	
	/**
	 * Recupera el rasgo de un nombre  dado
	 * @param rasgo
	 * @return
	 */
	public RasgoObjeto getRasgo(Rasgo rasgo){
		RasgoObjeto aux = new RasgoObjeto();
		aux.setRasgo(rasgo);
		int index = getRasgos().indexOf(aux);
		if (index != -1)
			return getRasgos().get(index);
		return null;
		
	}
	/**
	 * Recupera el rasgo de un nombre  dado
	 * @param rasgo
	 * @return
	 */
	public RasgoObjeto getRasgo(RasgoObjeto rasgo){
		int index = getRasgos().indexOf(rasgo);
		if (index != -1)
			return getRasgos().get(index);
		return null;
		
	}

	
	/**
	 * Agrega un rasgo al objeto. Si ya existe un rasgo con el mismo nombre modifica el
	 * valor de este con el valor del rasgo pasado como parámetro.
	 * @param rasgoObjeto
	 */
	public void addRasgo(RasgoObjeto rasgoObjeto){
		RasgoObjeto r = getRasgo(rasgoObjeto);
		if (r != null){
			r.setValor(rasgoObjeto.getValor());
		}
		else{
			getRasgos().add(rasgoObjeto);
			rasgoObjeto.setObjeto(this);
		}
	}
	
	/**
	 * Elimina un rasgo al objeto.
	 * @param rasgoObjeto
	 */
	public void removeRasgo(RasgoObjeto rasgoObjeto){
		getRasgos().remove(rasgoObjeto);
		rasgoObjeto.setObjeto(null);
	}

	/**
	 * Recupera la clase de un nombre  dado
	 * @param clse
	 * @return Clase
	 */
	public ClaseObjeto getClase(Clase clase){ 
		ClaseObjeto aux = new ClaseObjeto(clase);
		int index = getRasgos().indexOf(aux);
		if (index != -1)
			return getClases().get(index);
		return null;
		
	}
	
	/**
	 * Agrega una clase al objeto.
	 * @param  claseObjeto
	 */
	public void addClase(ClaseObjeto claseObjeto){
		ClaseObjeto c = getClase(claseObjeto.getClase());
		if (c != null){
			getClases().remove(c);
		}
		getClases().add(claseObjeto);
		claseObjeto.setObjeto(this);
	}
	
	/**
	 * Agrega una clase al objeto.
	 * @param  claseObjeto
	 */
	public void addClase(Clase clase){
		ClaseObjeto c = new ClaseObjeto(clase);
		addClase(c);
	}

	/**
	 * Elimina una clase al objeto.
	 * @param claseObjeto
	 */
	public void removeClase(ClaseObjeto claseObjeto){
		getClases().remove(claseObjeto);
		claseObjeto.setObjeto(null);
	}

	/**
	 * Elimina una clase al objeto.
	 * @param claseObjeto
	 */
	public void removeClase(Clase clase){
		for(ClaseObjeto c: getClases()){
			if (c.getClase().equals(clase)){
				this.removeClase(c);
				break;
			}
		}
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public Pixel getPixelPunta(){
		if (getContorno() != null && getContorno().size() > 0){
			List<Pixel> contorno = getContorno();
			Pixel punto1 = null;
			Pixel punto2 = null;
			int index1 = 0;
			int index2 = 0;
			double mayorDistancia = 0;
			for(int i = 0; i < contorno.size(); i++){
				Pixel actual1 = contorno.get(i);
				for(int j = i + 1; j < contorno.size(); j++){
					Pixel actual2 = contorno.get(j);
					double distancia = actual1.distancia(actual2);
					if (punto1 == null || distancia > mayorDistancia){
						mayorDistancia = distancia;
						punto1 = actual1;
						punto2 = actual2;
						index1 = i;
						index2 = j;
					}
				}
			}
			if (punto2 != null){
				int ventanaPixel = 20;
				int indexInicio = index1 - ventanaPixel;
				if (indexInicio < 0)
					indexInicio = Math.abs(contorno.size() + indexInicio) % contorno.size();
				Pixel iniVentana = contorno.get(indexInicio);
				Pixel finVentana = contorno.get((index1 + ventanaPixel) % contorno.size());
				Double angulo1 = ObjetoUtil.calcularAngulo(iniVentana, punto1, finVentana);
				indexInicio = index2 - ventanaPixel;
				if (indexInicio < 0)
					indexInicio = Math.abs(contorno.size() + indexInicio) % contorno.size();
				iniVentana = contorno.get(indexInicio);
				finVentana = contorno.get((index2 + ventanaPixel) % contorno.size());
				Double angulo2 = ObjetoUtil.calcularAngulo(iniVentana, punto2, finVentana);
				if (angulo1 != null && angulo2 != null){
					
					if (punto1.getCol() != null && punto2.getCol() != null){
						Color color1 = calcularPromedioColorPunto(punto1, 6);
						Color color2 = calcularPromedioColorPunto(punto2, 6);
						float[] hsv1 = RgbHsv.RGBtoHSV(color1.getRed(), color1.getGreen(), color1.getBlue());
						float[] hsv2 = RgbHsv.RGBtoHSV(color2.getRed(), color2.getGreen(), color2.getBlue());
						if (hsv1[2] > 200 && hsv1[2] > hsv2[2]){
							setPixelPunta1(punto1);
							setPixelPunta2(punto2);
							return punto1;
						}
						if (hsv2[2] > 200 && hsv1[2] < hsv2[2]){
							setPixelPunta1(punto2);
							setPixelPunta2(punto1);
							return punto2;
						}
					}
					if (angulo1 > angulo2){
						setPixelPunta1(punto1);
						setPixelPunta2(punto2);
						return punto1;
					}
					else{
						setPixelPunta1(punto2);
						setPixelPunta2(punto1);
						return punto2;
					}
				}
				else if (angulo1 != null){
					setPixelPunta1(punto1);
					setPixelPunta2(punto2);
					return punto1;
				}
				setPixelPunta1(punto2);
				setPixelPunta2(punto2);
				return punto2;
			}
			setPixelPunta1(punto1);
			setPixelPunta1(punto1);
			return punto1;
		}
		return null;
	}

	public Pixel getPixelPunta2() {
		return pixelPunta2;
	}

	public void setPixelPunta2(Pixel pixelPunta2) {
		this.pixelPunta2 = pixelPunta2;
	}

	public Pixel getPixelPunta1() {
		return pixelPunta1;
	}

	public void setPixelPunta1(Pixel pixelPunta1) {
		this.pixelPunta1 = pixelPunta1;
	}
	/**
	 * Calcula el color promedio del objeto alrededor del punto pasado como parámetro.
	 * 
	 * @param punto Un punto del objeto
	 * @param tamañoRec Tamaño del rectángulo alrededor del punto que se tendrá en cuenta para evaluar el colot promedio
	 * @return Color promedio
	 */
	public Color calcularPromedioColorPunto(Pixel punto, int tamanioRec){
		Pixel aux = getPixelMedio().clonar();
		aux.restar(punto);
		int ancho = tamanioRec / 2;
		int r = 0;
		int g = 0;
		int b = 0;
		int cantPuntos = 0;
		Pixel direccion = new Pixel(ancho * aux.getXDouble()/aux.modulo(),ancho * aux.getYDouble()/aux.modulo(),null);
		Pixel centro = punto.trasladar(direccion);
		for(int x = centro.getX() - ancho; x < centro.getX() + ancho; x++)
			for(int y = centro.getY() - ancho; y < centro.getY() + ancho; y++){
				Pixel p = new Pixel(x,y,null);
				if (isPertenece(p)){
					Color color = ImageUtil.getColorPunto(p, getOriginalImage());
					if (color != null){
						r += color.getRed();
						g += color.getGreen();
						b += color.getBlue();
						cantPuntos++;
					}
				}
			}
		if (cantPuntos > 0){
			r = r / cantPuntos;
			g = g / cantPuntos;
			b = b / cantPuntos;
		}
		return new Color(r,g,b);
	}

	public PlanarImage getOriginalImage() {
		return originalImage;
	}

	public void setOriginalImage(PlanarImage originalImage) {
		this.originalImage = originalImage;
	}

}
