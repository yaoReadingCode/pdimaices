package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Objeto;
import objeto.Pixel;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

public class SumaAngulos extends EvaluadorRasgo {
	/**
	 * Cantidad de pixeles del contorno a utilizar para ver si un pixel se desvía 
	 * demasiado del contorno. Lo que indicaría que pertenece a otro objeto
	 */
	private int ventanaPixeles = 10;
	
	private static int anguloDesvio = 70;
	
	public SumaAngulos() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SumaAngulos(RasgoClase rasgo, Double valor, Double desvioEstandar) {
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
	
	public double distancia(Pixel p, CoeficientesRecta recta){
		double distancia = Math.abs((-1 * recta.a * p.getXDouble() - p.getYDouble() - recta.c) / Math.sqrt(Math.pow(recta.a,2) + 1));
		return distancia;
	}
	/**
	 * 
	 * @param contorno
	 * @param posInicio
	 * @param posFin
	 * @return
	 */
	public boolean isLineaRecta(List<Pixel> contorno, int posInicio, int posFin){
		double umbral = 1;
		double distanciaPromedio = 0;
		if (posInicio < 0)
			posInicio = contorno.size() - posInicio;
		Pixel inicio = contorno.get(posInicio % contorno.size());
		Pixel fin = contorno.get(posFin % contorno.size());
		Double pendiente = (fin.getYDouble() - inicio.getYDouble()) / (fin.getXDouble() - inicio.getXDouble()); 
		CoeficientesRecta recta = new CoeficientesRecta();
		coeficientesRecta(pendiente, inicio, recta);
		
		int i = 0;
		int cantPixeles = Math.abs(posFin - posInicio);
		while (i <= cantPixeles){
			Pixel p = contorno.get((posInicio + i) % contorno.size());
			double dist = distancia(p, recta);
			distanciaPromedio += dist ;
			i++;
		}
		distanciaPromedio = distanciaPromedio / cantPixeles;
		if (distanciaPromedio < umbral){
			return true;
		}
		return false;
	}
	
	public Double calcularAngulo(Pixel pInicio, Pixel pMedio, Pixel pFin){
		Double angulo = null;
		Double pendiente1 = (pMedio.getYDouble() - pInicio.getYDouble()) / (pMedio.getXDouble() - pInicio.getXDouble()); 
		Double pendiente2 = -1 / pendiente1;
		CoeficientesRecta coefR1 = new CoeficientesRecta();
		CoeficientesRecta coefR2 = new CoeficientesRecta();
		coeficientesRecta(pendiente1, pInicio, coefR1);
		coeficientesRecta(pendiente2, pFin, coefR2);
		if (pMedio.getX() == 67 && pMedio.getY() == 32)
			System.out.println("");
		Pixel pInterseccion = calcularInterseccionRectas(coefR1.a, coefR1.b, coefR1.c, coefR2.a, coefR2.b, coefR2.c);
		if (pInterseccion != null){
			pInterseccion.setMaxX(pInicio.getMaxX());
			pInterseccion.setMaxY(pInicio.getMaxY());
			if (!pInterseccion.equals(pMedio)){
				double ladoA = pFin.distancia(pMedio);
				double ladoB = pFin.distancia(pInterseccion);
				if (ladoA != 0)
					angulo = Math.toDegrees(Math.asin(ladoB / ladoA));

				double ladoPuntoMedio = Pixel.lado2(pInicio, pMedio, pFin);
				double ladoPuntoInterseccion = Pixel.lado2(pMedio, pInterseccion, pFin);
				if (ladoPuntoInterseccion > 0 && angulo != null && ladoPuntoMedio < 0)
					angulo = 180 - angulo;

			}else
				angulo = 90.0;
		
		}
		return angulo;
	}

	
	/**
	 * Retorna 1 si el Maiz esta quebrado, 0 si no lo está.
	 */
	public RasgoObjeto calcularValor(Objeto objeto) {
		System.out.println("******");
		System.out.println(objeto.getName());
		List<Pixel> contorno = objeto.getContorno();
		int tamanioSegmento = getVentanaPixeles();
		double sumaAngulos = 0;
		
		if (tamanioSegmento == 0)
			tamanioSegmento = 1;
		
		if (contorno.size() > tamanioSegmento * 5){
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
			List<Pixel> esquinas = new ArrayList<Pixel>();
			while (!parar && contorno.size() > tamanioSegmento){
				
				Pixel p = contorno.get(i % contorno.size());
				
				if (p.getX() == 86 && p.getY() == 42)
					System.out.println("");

				Pixel finVentana2 = contorno.get((i + tamanioSegmento) % contorno.size());

				Double angulo = calcularAngulo(iniVentana, finVentana, finVentana2);
				double lado = Pixel.lado2(iniVentana, p, finVentana2);

				if (lado < 0 && angulo != null && angulo >= anguloDesvio){

					if (angulo > anguloCandidato){
						anguloCandidato = angulo;
						posCandidato = i;
					}
					
				}else{
					if (posCandidato != null){
						Pixel punto = contorno.get(posCandidato % contorno.size());
						esquinas.add(punto);
						/*
						if (isLineaRecta(contorno, i - longitudRecta, i)||
							isLineaRecta(contorno, i, i + longitudRecta)){*/
							posIniVentana = posCandidato;
							i = posCandidato + tamanioSegmento;
							posFinVentana = i;
							posCandidato = null;
							angulo = null;

							System.out.println(punto);
							System.out.println(anguloCandidato);
							
							sumaAngulos += anguloCandidato;
							anguloCandidato = 0;
						/*}*/
						
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
			
			if (posCandidato != null){
				Pixel punto = contorno.get(posCandidato % contorno.size());
				esquinas.add(punto);
				/*
				if (isLineaRecta(contorno, i - longitudRecta, i)||
					isLineaRecta(contorno, i, i + longitudRecta)){*/
					sumaAngulos += anguloCandidato;
					System.out.println(punto);
					System.out.println(anguloCandidato);
				/*}*/
			}
		}
		
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),sumaAngulos);
	}
	
	public static void main(String[] args) {
		SumaAngulos c = new SumaAngulos();
		Pixel pInicio = new Pixel(77,25,null,84,68);
		Pixel pMedio = new Pixel(67,32,null,84,68);
		Pixel pFin = new Pixel(65,42,null,84,68);
		
		double lado = Pixel.lado2(pInicio, pMedio, pFin);
		/*
		CoeficientesRecta recta = new CoeficientesRecta();
		double pendiente = (pFin.getYDouble() - pInicio.getYDouble()) / (pFin.getXDouble() - pInicio.getXDouble());
		c.coeficientesRecta(pendiente, pInicio, recta);
		double distancia = c.distancia(pMedio, recta);
		System.out.println(distancia);*/
		Double angulo = c.calcularAngulo(pInicio, pMedio, pFin);
		
		
	}

}
