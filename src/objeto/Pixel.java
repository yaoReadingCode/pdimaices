package objeto;

import java.awt.Color;
import java.util.List;

public class Pixel implements Cloneable{
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

	public Pixel(double i, double j, int R, int G, int B) {
		x = (int) i;
		y = (int) j;
		xDouble = i;
		yDouble = j;
		Color c = new Color(R, G, B);
		col = c;
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
			return adyacente;
		}
		if (ady == DIR_NE && y - 1 >= 0 && x + 1 < maxX) {
			adyacente.setX(x + 1);
			adyacente.setY(y - 1);
			return adyacente;
		}

		if (ady == DIR_E && x + 1 < maxX) {
			adyacente.setX(x + 1);
			adyacente.setY(y);
			return adyacente;
		}

		if (ady == DIR_SE && x + 1 < maxX && y + 1 < maxY) {
			adyacente.setX(x + 1);
			adyacente.setY(y + 1);
			return adyacente;
		}

		if (ady == DIR_S && y + 1 < maxY) {
			adyacente.setX(x);
			adyacente.setY(y + 1);
			return adyacente;
		}

		if (ady == DIR_SO && y + 1 < maxY && x - 1 >= 0) {
			adyacente.setX(x - 1);
			adyacente.setY(y + 1);
			return adyacente;
		}
		if (ady == DIR_O && x - 1 >= 0) {
			adyacente.setX(x - 1);
			adyacente.setY(y);
			return adyacente;
		}

		if (ady == DIR_NO && x - 1 >= 0 && y - 1 >= 0) {
			adyacente.setX(x - 1);
			adyacente.setY(y - 1);
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
			int d = (b.getY() - a.getY())* c.getX() + (a.getX() - b.getX())*c.getY() + b.getX()* a.getY() - b.getY()*a.getX(); 
			return d;*/
			
			Pixel P21 = new Pixel(b.getX() - a.getX(), b.getY() - a.getY(), null);
			Pixel P32 = new Pixel(c.getX() - a.getX(), c.getY() - a.getY(), null);
			double prodV = P21.productoVectorial(P32);
			return prodV;
		}
		return 0;
	};

	/**
	 * Calcula el producto vectorial entre dos pixels
	 * 
	 * @param pixel
	 * @return
	 */
	public double productoVectorial(Pixel pixel) {
		return x * pixel.y - y * pixel.x;
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
		return Math.sqrt(distX * distX + distY * distY);
	}

	public void restar(Pixel pixel) {
		double PX = xDouble - pixel.xDouble;
		double PY = yDouble - pixel.yDouble;
		xDouble = PX;
		yDouble = PY;
		x = (int) xDouble;
		y = (int) yDouble;
	};

	public void sumar(Pixel pixel) {
		double PX = xDouble + pixel.xDouble;
		double PY = yDouble + pixel.yDouble;
		xDouble = PX;
		yDouble = PY;
		x = (int) xDouble;
		y = (int) yDouble;
	};

	public void rotar(double angulo) {
		double coseno = Math.cos(Math.toRadians(angulo));
		double seno = Math.sin(angulo);
		double PX = xDouble * coseno + yDouble * seno;
		double PY = (-1) * xDouble * seno + yDouble * coseno;
		xDouble = PX;
		yDouble = PY;
		x = (int) xDouble;
		y = (int) yDouble;
	};

	/**
	 * Retorna el pixel mas cercano de una lista de pixeles 
	 * @param lista
	 * @return
	 */
	public Pixel getPixelMasCercano(List<Pixel> lista){
		if (lista != null && lista.size() > 0){
			
			Pixel masCercano = null;
			double mejorDistancia = Double.MAX_VALUE;
			for(int i = 0; i< lista.size(); i++){
				Pixel p = lista.get(i);
				if (!this.equals(p)){
					double distancia = this.distancia(p);
					if (mejorDistancia > distancia){
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
		return new Pixel(xDouble, yDouble, col);
	}

	protected Object clone() throws CloneNotSupportedException {
		return clonar();
	}
	
	public double modulo(){
		return Math.sqrt(Math.pow(this.getXDouble(), 2) + Math.pow(this.getYDouble(), 2));
	}
	
}
