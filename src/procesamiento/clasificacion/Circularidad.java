package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.Pixel;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class Circularidad extends EvaluadorRasgo {

	public Circularidad() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Circularidad(RasgoClase rasgo, Double minimo, Double maximo) {
		super(rasgo, minimo, maximo);
	}
	 
	public RasgoObjeto calcularValor(Objeto objeto) {
		/*Double circularidad = null;
		
		if (objeto.getPuntos().size() > 0){
			double radio = objeto.getRadio();
			double area = objeto.getArea();
			circularidad = null;
			if (radio != 0)
				circularidad = area / (Math.PI * Math.pow(radio,2));
		}
		else{
			double perimetro = objeto.getLongitudPerimetro();
			double radio = objeto.getRadio();
			circularidad = null;
			if (radio != 0)
				circularidad = perimetro / (2 * Math.PI* radio);
		}
		*/
		
		//Sumatoria de distancia de los puntos al centro del objeto
		
		double radio = objeto.getLongitudPerimetro() / (2 * Math.PI);
		
		//System.out.println("Radio: " + radio);
		double error = 0;
		for(Pixel p:objeto.getContorno()){
			error += Math.sqrt(Math.pow(p.distancia(objeto.getPixelMedio()) - radio, 2));
		}
		double circularidad = 1 - (error / (objeto.getLongitudPerimetro() * radio));
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),circularidad);
	}

}
