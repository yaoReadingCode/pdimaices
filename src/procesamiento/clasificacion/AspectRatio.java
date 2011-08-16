package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Objeto;
import objeto.Pixel;
import objeto.Rasgo;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class AspectRatio extends EvaluadorRasgo {

	public AspectRatio() {
		super();
		// TODO Auto-generated constructor stub
	}


	public AspectRatio(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	 
	public RasgoObjeto calcularValor(Objeto objeto) {
		double altoMRC = objeto.getAlto();
		double anchoMRC = objeto.getAncho();
	
		double aspectRatio = 0;
		if (anchoMRC < altoMRC)
			aspectRatio = anchoMRC / altoMRC;
		else
			aspectRatio = altoMRC / anchoMRC;
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),aspectRatio);
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
		RasgoClase rc = new RasgoClase();
		rc.setRasgo(r);
		AspectRatio aspectRatio = new AspectRatio(rc, 1.0, 0.2);
		Double valor = aspectRatio.calcularValor(obj).getValor();
		System.out.println(valor);

	}
}
