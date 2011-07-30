package objeto;

import java.awt.Color;
import java.util.List;

public class Pixel implements Cloneable {
	public static final int DIR_E = 2;
	public static final int DIR_SE = 3;
	public static final int DIR_S = 4;
	public static final int DIR_SO = 5;
	public static final int DIR_O = 6;
	public static final int DIR_NO = 7;
	public static final int DIR_N = 0;
	public static final int DIR_NE = 1;

	public static final int LADO_IZQ = -1;
	public static final int LADO_DER = 1;
	public static final int LADO_MISMO_SEGMENTO = 0;

	private int x = 0;
	private int y = 0;

	private double xDouble = 0;
	private double yDouble = 0;

	private Integer maxX = null;
	private Integer maxY = null;

	private Color col = null;

	/**
	 * Atributo utilizado para ordenar pixeles
	 */
	private double peso = 0;

	public Pixel() {

	}

	public Pixel(int i, int j, Color c) {
		x = i;
		y = j;
		xDouble = x;
		yDouble = y;
		col = c;
	}

	public Pixel(int i, int j, int R, int G, int B) {
		x = i;
		y = j;
		xDouble = x;
		yDouble = y;
		Color c = new Color(R, G, B);
		col = c;
	}

	public Pixel(double i, double j, Color c) {
		x = (int) i;
		y = (int) j;
		xDouble = i;
		yDouble = j;
		col = c;
	}

	public Pixel(double i, double j, Color c, Integer maxX, Integer maxY) {
		x = (int) i;
		y = (int) j;
		xDouble = i;
		yDouble = j;
		col = c;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public Pixel(double i, double j, int R, int G, int B) {
		x = (int) i;
		y = (int) j;
		xDouble = i;
		yDouble = j;
		Color c = new Color(R, G, B);
		col = c;
	}

	public Pixel(int x, int y, Color c, int maxX, int maxY) {
		this(x, y, c);
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public Color getCol() {
		return col;
	}

	public void setCol(Color col) {
		this.col = col;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		xDouble = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		yDouble = y;
	}

	public double getXDouble() {
		return xDouble;
	}

	public void setXDouble(double double1) {
		xDouble = double1;
		x = (int) xDouble;
	}

	public double getYDouble() {
		return yDouble;
	}

	public void setYDouble(double double1) {
		yDouble = double1;
		y = (int) yDouble;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	public boolean isColorFondo() {
		if ((col.getBlue() <= col.getGreen())
				&& (col.getBlue() <= col.getRed()))
			return false;
		if (col.getBlue() > 220 && col.getRed() > 220 && col.getGreen() > 220)
			return false;
		return true;
	}

	public boolean isAdyacente(Pixel pixel) {
		if (this.x == pixel.getX() && this.y == pixel.getY())
			return false;

		if (this.x - 1 == pixel.x && this.y - 1 == pixel.y)
			return true;
		if (this.x - 1 == pixel.x && this.y == pixel.y)
			return true;
		if (this.x - 1 == pixel.x && this.y + 1 == pixel.y)
			return true;

		if (this.x == pixel.x && this.y - 1 == pixel.y)
			return true;
		if (this.x == pixel.x && this.y + 1 == pixel.y)
			return true;

		if (this.x + 1 == pixel.x && this.y - 1 == pixel.y)
			return true;
		if (this.x + 1 == pixel.x && this.y == pixel.y)
			return true;
		if (this.x + 1 == pixel.x && this.y + 1 == pixel.y)
			return true;

		/*
		 * if (this.x - 1 <= pixel.getX() && pixel.getX() <= this.x + 1 &&
		 * this.y - 1 <= pixel.getY() && pixel.getY() <= this.y + 1) return
		 * true;
		 */
		return false;
	}

	/**
	 * Devuelve el lado del pixel de acuerdo a otro pixel de referencia
	 * 
	 * @param p
	 * @return
	 */
	public int getLado(Pixel p) {
		if (x == p.x - 1 && y == p.y)
			return DIR_N;
		if (x == p.x - 1 && y == p.y + 1)
			return DIR_NE;
		if (x == p.x && y == p.y + 1)
			return DIR_E;
		if (x == p.x + 1 && y == p.y + 1)
			return DIR_SE;
		if (x == p.x + 1 && y == p.y)
			return DIR_S;
		if (x == p.x + 1 && y == p.y - 1)
			return DIR_SO;
		if (x == p.x && y == p.y - 1)
			return DIR_O;
		if (x == p.x - 1 && y == p.y - 1)
			return DIR_NO;
		return -1;
	}

	/**
	 * Devuelve la direccion al pixel dado como referencia
	 * 
	 * @param p
	 * @return
	 */
	public int getDireccion(Pixel p) {
		if (x == p.x && y < p.y)
			return DIR_N;
		if (x > p.x && y < p.y)
			return DIR_NE;
		if (x < p.x && y < p.y)
			return DIR_NO;
		if (x == p.x && y > p.y)
			return DIR_S;
		if (x > p.x && y > p.y)
			return DIR_SE;
		if (x < p.x && y > p.y)
			return DIR_SO;
		if (x > p.x && y == p.y)
			return DIR_E;
		if (x < p.x && y == p.y)
			return DIR_O;
		return -1;
	}

	public Pixel getAdyacente(int ady, int maxX, int maxY) {
		Pixel adyacente = new Pixel();
		if (ady == DIR_N && y - 1 >= 0) {
			adyacente.setX(x);
			adyacente.setY(y - 1);
			adyacente.setMaxX(maxX);
			adyacente.setMaxY(maxY);
			return adyacente;
		}
		if (ady == DIR_NE && y - 1 >= 0 && x + 1 < maxX) {
			adyacente.setX(x + 1);
			adyacente.setY(y - 1);
			adyacente.setMaxX(maxX);
			adyacente.setMaxY(maxY);
			return adyacente;
		}

		if (ady == DIR_E && x + 1 < maxX) {
			adyacente.setX(x + 1);
			adyacente.setY(y);
			adyacente.setMaxX(maxX);
			adyacente.setMaxY(maxY);
			return adyacente;
		}

		if (ady == DIR_SE && x + 1 < maxX && y + 1 < maxY) {
			adyacente.setX(x + 1);
			adyacente.setY(y + 1);
			adyacente.setMaxX(maxX);
			adyacente.setMaxY(maxY);
			return adyacente;
		}

		if (ady == DIR_S && y + 1 < maxY) {
			adyacente.setX(x);
			adyacente.setY(y + 1);
			adyacente.setMaxX(maxX);
			adyacente.setMaxY(maxY);
			return adyacente;
		}

		if (ady == DIR_SO && y + 1 < maxY && x - 1 >= 0) {
			adyacente.setX(x - 1);
			adyacente.setY(y + 1);
			adyacente.setMaxX(maxX);
			adyacente.setMaxY(maxY);
			return adyacente;
		}
		if (ady == DIR_O && x - 1 >= 0) {
			adyacente.setX(x - 1);
			adyacente.setY(y);
			adyacente.setMaxX(maxX);
			adyacente.setMaxY(maxY);
			return adyacente;
		}

		if (ady == DIR_NO && x - 1 >= 0 && y - 1 >= 0) {
			adyacente.setX(x - 1);
			adyacente.setY(y - 1);
			adyacente.setMaxX(maxX);
			adyacente.setMaxY(maxY);
			return adyacente;
		}

		return null;
	}

	/**
	 * Retorna de que lado esta un Pixel c con respecto a una recta formada
	 * entre el Pixel a y b.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return -1 si esta a la izquierda, 1 si esta a la derercha y 0 si
	 *         pertenece al segmento
	 */
	public static double lado(Pixel a, Pixel b, Pixel c) {

		if (a != null && b != null && c != null) {
			/*
			 * int d = (b.getY() - a.getY()) c.getX() + (a.getX() -
			 * b.getX())c.getY() + b.getX() a.getY() - b.getY()a.getX(); return
			 * d;
			 */

			Pixel P21 = new Pixel(b.getX() - a.getX(), b.getY() - a.getY(),
					null, null, null);
			Pixel P32 = new Pixel(c.getX() - a.getX(), c.getY() - a.getY(),
					null, null, null);
			double prodV = P21.productoVectorial(P32);
			return prodV;
		}
		return 0;
	}

	public static double lado2(Pixel a, Pixel b, Pixel c) {
		Pixel a2 = a.clonar();
		a2.setY(a2.maxY - a2.y);
		Pixel b2 = b.clonar();
		b2.setY(b2.maxY - b2.y);
		Pixel c2 = c.clonar();
		c2.setY(c2.maxY - c2.y);
		Pixel ab = b2.clonar();
		ab.restar(a2);
		Pixel bc = c2.clonar();
		bc.restar(b2);
		double pv = ab.productoVectorial2(bc);
		return pv;
	}

	/**
	 * Calcula el producto vectorial entre dos pixels
	 * 
	 * @param pixel
	 * @return
	 */
	public double productoVectorial(Pixel pixel) {
		return getXDouble() * pixel.getYDouble() - getYDouble() * pixel.getXDouble();
		// return getXCartesiano() * pixel.getYCartesiano() - getYCartesiano() *
		// pixel.getXCartesiano();
	}

	
	/**
	 * Calcula el producto vectorial entre dos pixels
	 * 
	 * @param pixel
	 * @return
	 */
	public double productoVectorial2(Pixel pixel) {
		return getX() * pixel.getY() - getY() * pixel.getX();
		 //return getXCartesiano() * pixel.getYCartesiano() - getYCartesiano() * pixel.getXCartesiano();
	}

	/**
	 * Calcula la distancia a otro pixel
	 * 
	 * @param pixel
	 * @return
	 */
	public double distancia(Pixel pixel) {
		double distX = this.xDouble - pixel.xDouble;
		double distY = this.yDouble - pixel.yDouble;
		double val = distX * distX + distY * distY;
		if (val != 0)
			return Math.sqrt(val);
		return 0;
	}

	public void restar(Pixel pixel) {
		double PX = x - pixel.x;
		double PY = y - pixel.y;
		xDouble = PX;
		yDouble = PY;
		x = (int) xDouble;
		y = (int) yDouble;
	};

	public void sumar(Pixel pixel) {
		double PX = x + pixel.x;
		double PY = y + pixel.y;
		xDouble = PX;
		yDouble = PY;
		x = (int) xDouble;
		y = (int) yDouble;
	};

	public void rotar(double angulo) {
		double coseno = Math.cos(Math.toRadians(angulo));
		double seno = Math.sin(Math.toRadians(angulo));
		double PX = x * coseno - y * seno;
		double PY = x * seno + y * coseno;
		xDouble = PX;
		yDouble = PY;
		x = (int) Math.rint(xDouble);
		y = (int) Math.rint(yDouble);
	};

	/**
	 * Retorna el pixel mas cercano de una lista de pixeles
	 * 
	 * @param lista
	 * @return
	 */
	public Pixel getPixelMasCercano(List<Pixel> lista) {
		if (lista != null && lista.size() > 0) {

			Pixel masCercano = null;
			double mejorDistancia = Double.MAX_VALUE;
			for (int i = 0; i < lista.size(); i++) {
				Pixel p = lista.get(i);
				if (!this.equals(p)) {
					double distancia = this.distancia(p);
					if (mejorDistancia > distancia) {
						mejorDistancia = distancia;
						masCercano = p;
					}
				}
			}
			return masCercano;
		}
		return null;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Pixel))
			return false;
		Pixel p = (Pixel) obj;
		return x == p.x && y == p.y;
	}

	public String toString() {
		return x + " - " + y;
	}

	public Pixel clonar() {
		return new Pixel(xDouble, yDouble, col, maxX, maxY);
	}

	protected Object clone() throws CloneNotSupportedException {
		return clonar();
	}

	public double modulo() {
		return Math.sqrt(Math.pow(this.getXDouble(), 2)
				+ Math.pow(this.getYDouble(), 2));
	}

	public double getXCartesiano() {
		return this.xDouble;
	}

	public double getYCartesiano() {
		if (this.maxY != null)
			return this.maxY - this.getYDouble();
		return this.getYDouble();
	}

	public Integer getMaxX() {
		return maxX;
	}

	public void setMaxX(Integer maxX) {
		this.maxX = maxX;
	}

	public Integer getMaxY() {
		return maxY;
	}

	public void setMaxY(Integer maxY) {
		this.maxY = maxY;
	}

	public Double getAnguloPolar(){
		if (x > 0 && y >= 0){
			return Math.toDegrees(Math.atan(yDouble/xDouble));
		}
		if (x > 0 && y < 0){
			return Math.toDegrees(Math.atan(yDouble/xDouble)) + 360;
		}
		if (x < 0){
			return Math.toDegrees(Math.atan(yDouble/xDouble)) + 180;
		}
		if (x == 0 && y > 0){
			return 90.0;
		}
		if (x == 0 && y < 0){
			return 270.0;
		}
		return null;


	}
	
	/**
	 * Transforma el punto a coordenadas cartesianas
	 * @return
	 */
	public Pixel getCoordenadasCartesianas(){
		Pixel p = new Pixel();
		p.setXDouble(this.getXCartesiano());
		p.setYDouble(this.getYCartesiano());
		p.setCol(this.getCol());
		return p;
	}
	
	public Pixel getPuntoMedio(Pixel punto){
		Pixel p = new Pixel();
		p.setXDouble((this.xDouble + punto.xDouble) / 2);
		p.setYDouble((this.yDouble + punto.yDouble) / 2);
		return p;
	}
	
	public Pixel trasladar(Pixel punto){
		Pixel aux = this.clonar();
		aux.setXDouble(this.xDouble + punto.xDouble);
		aux.setYDouble(this.yDouble + punto.yDouble);
		return aux;
	}
}
