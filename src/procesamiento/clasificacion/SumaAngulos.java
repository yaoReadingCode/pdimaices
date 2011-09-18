package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Objeto;
import objeto.ObjetoUtil;
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
		ObjetoUtil.coeficientesRecta(pendiente, inicio, recta);
		
		int i = 0;
		int cantPixeles = Math.abs(posFin - posInicio);
		while (i <= cantPixeles){
			Pixel p = contorno.get((posInicio + i) % contorno.size());
			double dist = ObjetoUtil.distancia(p, recta);
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
		ObjetoUtil.coeficientesRecta(pendiente1, pInicio, coefR1);
		ObjetoUtil.coeficientesRecta(pendiente2, pFin, coefR2);
		Pixel pInterseccion = ObjetoUtil.calcularInterseccionRectas(coefR1.a, coefR1.b, coefR1.c, coefR2.a, coefR2.b, coefR2.c);
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
