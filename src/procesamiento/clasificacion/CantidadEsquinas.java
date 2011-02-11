package procesamiento.clasificacion;

import java.util.List;

import objeto.Objeto;
import objeto.Pixel;
import objeto.RasgoClase;

public class CantidadEsquinas extends EvaluadorRasgo {
	/**
	 * Cantidad de pixeles del contorno a utilizar para ver si un pixel se desvía 
	 * demasiado del contorno. Lo que indicaría que pertenece a otro objeto
	 */
	private int ventanaPixeles = 10;

	private static int anguloDesvio = 90;

	public CantidadEsquinas() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CantidadEsquinas(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super(rasgo, valor, desvioEstandar);
		// TODO Auto-generated constructor stub
	}
	public int getVentanaPixeles() {
		return ventanaPixeles * getObjetoReferencia().getRelacionPixelCm().intValue();
	}


	public void setVentanaPixeles(int ventanaPixeles) {
		this.ventanaPixeles = ventanaPixeles;
	}
	
	/***
	 * Calcula los coeficientes a, b y c de la recta a * x + b * y + c = 0 formada por el vector director
	 * de la recta y un punto de la misma
	 * @param vectorDirector Vector director de la recta
	 * @param puntoRecta Punto que pertenece a la recta
	 * @param a
	 * @param b
	 * @param c
	 */
	public void coeficientesRecta(Pixel vectorDirector, Pixel puntoRecta, Double a, Double b, Double c){
		a = vectorDirector.getXDouble();
		b = vectorDirector.getYDouble();
		c = b * puntoRecta.getYDouble() - puntoRecta.getYDouble() * a;
	}
	/**
	 * Calcula el punto de intersección de dos rectas: <a * x + b * y + c = 0> y <d * x + e * y + f = 0> 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param e
	 * @param f
	 * @return
	 */
	public Pixel calcularInterseccionRectas(double a, double b, double c, double d, double e, double f){
		Double x = null, y = null;
		Double c1 = 0.0, c2 = 0.0;
		if (a - d != 0){
			c1 = a * (e - b)/(a - d);
			c2 = a * (f - c)/(a - d);
		}
		if (c1 + b != 0)
			y = (-c2 - c)/(c1 + b);
		if (d != 0 && y != null)
			x = (f - e * y)/ d;
		return new Pixel(x,y,null);
	}
	
	public Double calcularAngulo(Pixel pInicio, Pixel pMedio, Pixel pFin){
		Double angulo = null;
		Pixel vDirector1 = pMedio.clonar();
		vDirector1.restar(pInicio);
		
		/**
		 * Vector perpendicular a vDirector1
		 */
		Pixel vDirector2 = new Pixel(-1 * vDirector1.getYDouble(), vDirector1.getXDouble(), null);
		double a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;
		
		coeficientesRecta(vDirector1, pInicio, a, b, c);
		coeficientesRecta(vDirector2, pInicio, d, e, f);
		
		Pixel pInterseccion = calcularInterseccionRectas(a, b, c, d, e, f);
		if (pInterseccion != null){
			double ladoA = 0;
			double ladoB = 0;
			
			if (!pInterseccion.equals(pMedio)){
				ladoA = pFin.distancia(pMedio);
				ladoB = pFin.distancia(pInterseccion);
			}else{
				ladoB = pFin.distancia(pMedio);
				ladoA = pFin.distancia(pInterseccion);
			}
			
			if (ladoA != 0)
				angulo = Math.toDegrees(Math.asin(ladoB / ladoA));
		}
		return angulo;
	}

	
	@Override
	public Double calcularValor(Objeto objeto) {
		System.out.println("******");
		System.out.println(objeto.getName());
		List<Pixel> contorno = objeto.getContorno();
		int tamanioSegmento = getVentanaPixeles();
		double cantEsquinas = 0;
		
		if (tamanioSegmento == 0)
			tamanioSegmento = 1;
		
		if (contorno.size() > tamanioSegmento*2){
			int posIniVentana = 0;
			int posFinVentana = tamanioSegmento;
			Pixel iniVentana = contorno.get(posIniVentana);
			Pixel finVentana = contorno.get(posFinVentana);
			//Para la ecuacion de la recta
			
			boolean parar = false;
			int i = posFinVentana;
			int inicio = i;
			
			Integer posCandidato = null;
			double anguloCandidato = 0;
			
			while (!parar && contorno.size() > tamanioSegmento){
				
				Pixel p = contorno.get(i % contorno.size());
				Pixel finVentana2 = contorno.get((i + tamanioSegmento) % contorno.size());

				double angulo = calcularAngulo(iniVentana, finVentana, finVentana2);
				double lado = Pixel.lado(iniVentana, finVentana2, p);

				if (lado < 0 && angulo < anguloDesvio){
					posCandidato = i;
					anguloCandidato = angulo;
					
					if (angulo > anguloCandidato){
						anguloCandidato = angulo;
						posCandidato = i;
					}
					
				}else{
					if (posCandidato != null){
						Pixel punto = contorno.get(posCandidato % contorno.size());
						
						posIniVentana = posCandidato;
						i = posCandidato + tamanioSegmento;
						posFinVentana = i;
						posCandidato = null;
						
						cantEsquinas++;
						System.out.println(punto);
						System.out.println(anguloCandidato);
					}
				}

				posIniVentana = posIniVentana + 1;
				posFinVentana = posFinVentana + 1;
				
				iniVentana = contorno.get(posIniVentana % contorno.size());
				finVentana = contorno.get(posFinVentana % contorno.size());
			
				i++;
				
				if (posFinVentana > contorno.size() && i > inicio)
					parar = true;
			}
		}
		
		return cantEsquinas;
	}

}
