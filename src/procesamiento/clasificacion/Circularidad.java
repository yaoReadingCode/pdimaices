package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class Circularidad extends EvaluadorRasgo {

	public Circularidad() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Circularidad(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
	}

	 
	public RasgoObjeto calcularValor(Objeto objeto) {
		Double circularidad = null;
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
		
		
		//Sumatoria de distancia de los puntos al centro del objeto
		/*
		double sumDisCentro = 0;
		double radio = objeto.getLongitudPerimetro() / (2 * Math.PI);
		double radio2 = objeto.getRadio();
		
		System.out.println("Radio: " + radio);
		System.out.println("Radio2: " + radio2);
		for(Pixel p:objeto.getContorno()){
			sumDisCentro += p.distancia(objeto.getPixelMedio());
		}
		double circularidad = sumDisCentro / (objeto.getLongitudPerimetro() * Math.max(radio,radio2));*/
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),circularidad);
	}

}
