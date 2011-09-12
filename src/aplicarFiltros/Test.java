package aplicarFiltros;

import objeto.Pixel;
import objeto.Triangulo;


public class Test {
	public static void main(String[] args) {
		Pixel p1 = new Pixel(56,56,null,500,500);
		Pixel p2 = new Pixel(51,90,null,500,500);
		Pixel p3 = new Pixel(46,88,null,500,500);
		Triangulo t = new Triangulo(p1,p2,p3);
		boolean pertenece = t.isPertenece(new Pixel(51,101,null,500,500));		
	}
}
