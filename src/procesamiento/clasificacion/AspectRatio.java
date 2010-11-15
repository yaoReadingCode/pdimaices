package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Objeto;
import objeto.Pixel;
import objeto.Rasgo;

public class AspectRatio extends EvaluadorRasgo {

	public AspectRatio() {
		super();
		// TODO Auto-generated constructor stub
	}


	public AspectRatio(Rasgo rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	 
	public Double calcularValor(Objeto objeto) {
		Objeto objAux = objeto.clonar();
		double altoMRC = objeto.getAlto();
		double anchoMRC = objeto.getAncho();
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
		
		double aspectRatio = 0;
		if (anchoMRC < altoMRC)
			aspectRatio = anchoMRC / altoMRC;
		else
			aspectRatio = altoMRC / anchoMRC;
		//System.out.println(objeto.getName() + " - Apect Radio: " + aspectRatio);
		return aspectRatio;
	}

	public static void main(String[] args) {
		System.out.println("Test");
		Objeto obj = new Objeto();
		List<Pixel> contorno = new ArrayList<Pixel>();
		contorno.add(new Pixel(-10, -5, null));
		contorno.add(new Pixel(-10, 5, null));
		contorno.add(new Pixel(10, 5, null));
		contorno.add(new Pixel(10, -5, null));
		obj.setContorno(contorno);
		
		Rasgo r = new Rasgo();
		r.setNombre("Aspect Ratio");
		AspectRatio aspectRatio = new AspectRatio(r, 1.0, 0.2);
		Double valor = aspectRatio.calcularValor(obj);
		System.out.println(valor);

	}
}
