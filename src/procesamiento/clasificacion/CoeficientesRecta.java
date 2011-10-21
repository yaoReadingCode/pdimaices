package procesamiento.clasificacion;

import objeto.Pixel;

/**
 * Clase que contiene los coeficientes a, b y c de la recta a * x + b * y + c = 0
 * @author oscar
 *
 */
public class CoeficientesRecta {
	public double a;
	public double b;
	public double c;
	/**
	 * Proyecta un punto sobre la recta retornando otro punto que pertenece a la recta
	 * @param punto
	 * @return
	 */
	public Pixel proyectarPunto(Pixel punto){
		Pixel result = punto.clonar();
		if (this.b != 0){
			double y = -1 * (this.a * punto.getX() + this.c) / this.b;
			result.setYDouble(y);
			return result;
		}
		else{
			double x = -1 * this.c / this.a;
			result.setXDouble(x);
			return result;
		}
	}

}
