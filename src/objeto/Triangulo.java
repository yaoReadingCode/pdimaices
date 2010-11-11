package objeto;

public class Triangulo {
	private Pixel p1;
	private Pixel p2;
	private Pixel p3;

	public Triangulo(Pixel p1, Pixel p2, Pixel p3) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	public Pixel getP1() {
		return p1;
	}

	public void setP1(Pixel p1) {
		this.p1 = p1;
	}

	public Pixel getP2() {
		return p2;
	}

	public void setP2(Pixel p2) {
		this.p2 = p2;
	}

	public Pixel getP3() {
		return p3;
	}

	public void setP3(Pixel p3) {
		this.p3 = p3;
	}

	/**
	 * Retorna si un pixel se encuentra dentro del triángulo
	 * 
	 * @param p
	 * @return
	 */
	public boolean isPertenece(Pixel p) {
		double lado = Pixel.lado(getP1(), getP2(), p);
		if (lado < 0)
			return false;
		lado = Pixel.lado(getP2(), getP3(), p);
		if (lado < 0)
			return false;
		lado = Pixel.lado(getP3(), getP1(), p);
		if (lado < 0)
			return false;
		return true;
	}
	
	/**
	 * Valida que los puntos formen un triangulo. Que los 3 puntos no esten en la misma linea
	 * @return
	 */
	public boolean validarTriangulo(){
		double lado = Pixel.lado(getP1(), getP2(), getP3());
		if (lado == 0)
			return false;
		return true;
		
	}
	 
	public String toString() {
		return "(" + getP1().toString() + " , " + getP2().toString() + " , "
				+ getP3().toString() + ")";
	}

}
