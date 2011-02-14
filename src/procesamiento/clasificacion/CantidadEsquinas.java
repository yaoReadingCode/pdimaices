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
	
	private class CoeficientesRecta {
		public double a;
		public double b;
		public double c;
	}

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
	public void coeficientesRecta(double pendiente, Pixel punto, CoeficientesRecta coeficientes){
		coeficientes.a = -1 * pendiente;
		coeficientes.b = 1;
		coeficientes.c = pendiente * punto.getXDouble() - punto.getYDouble();
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
			x = (-f - e * y)/ d;
		if (x != null && y != null)
			return new Pixel(x,y,null);
		return null;
	}
	
	public Double calcularAngulo(Pixel pInicio, Pixel pMedio, Pixel pFin){
		Double angulo = null;
		Double pendiente1 = (pMedio.getYDouble() - pInicio.getYDouble()) / (pMedio.getXDouble() - pInicio.getXDouble()); 
		Double pendiente2 = -1 / pendiente1;
		CoeficientesRecta coefR1 = new CoeficientesRecta();
		CoeficientesRecta coefR2 = new CoeficientesRecta();
		coeficientesRecta(pendiente1, pInicio, coefR1);
		coeficientesRecta(pendiente2, pFin, coefR2);
		
		Pixel pInterseccion = calcularInterseccionRectas(coefR1.a, coefR1.b, coefR1.c, coefR2.a, coefR2.b, coefR2.c);
		if (pInterseccion != null){
			if (!pInterseccion.equals(pMedio)){
				double ladoA = pFin.distancia(pMedio);
				double ladoB = pFin.distancia(pInterseccion);
				if (ladoA != 0)
					angulo = Math.toDegrees(Math.asin(ladoB / ladoA));

				double ladoPuntoInterseccion = Pixel.lado2(pMedio, pFin, pInterseccion);
				if (ladoPuntoInterseccion > 0)
					angulo = 180 - angulo;

			}else
				angulo = 90.0;
		
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

				Double angulo = calcularAngulo(iniVentana, finVentana, finVentana2);
				double lado = Pixel.lado(iniVentana, finVentana2, p);

				if (lado < 0 && angulo != null && angulo >= anguloDesvio){
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
	
	public static void main(String[] args) {
		CantidadEsquinas c = new CantidadEsquinas();
		Pixel pInicio = new Pixel(-1,1,null);
		Pixel pMedio = new Pixel(0,2,null);
		Pixel pFin = new Pixel(1,0,null);
		
		Double angulo = c.calcularAngulo(pInicio, pMedio, pFin);
	}

}
