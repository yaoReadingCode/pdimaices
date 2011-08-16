package procesamiento;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.PlanarImage;

import objeto.Clase;
import objeto.Objeto;
import objeto.ObjetoUtil;
import objeto.Pixel;
import objeto.Rasgo;
import objeto.RasgoClase;
import procesamiento.clasificacion.AspectRatio;
import procesamiento.clasificacion.Circularidad;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.EvaluadorRasgo;
import procesamiento.clasificacion.SumaAngulos;
import aplicarFiltros.Visualizador;

public class SepararObjetos extends AbstractImageCommand {

	/**
	 * Cantidad de pixeles del contorno a utilizar para ver si un pixel se desvía 
	 * demasiado del contorno. Lo que indicaría que pertenece a otro objeto
	 */
	private int ventanaPixeles = 20;
	//private static int anguloDesvio = 70;
	//private int ventanaPixeles = 5;
	//private static int anguloDesvio = 45;
	private static int anguloDesvio = 35;
	
	private PlanarImage originalImage;
	
	List<Objeto> objetos = null;
	
	DetectarContorno detectarContorno = null;
	
	public SepararObjetos(PlanarImage image, List<Objeto> objetos, DetectarContorno padre) {
		super(image);
		this.objetos = objetos;
		this.detectarContorno = padre;
	}


	public SepararObjetos(PlanarImage image, List<Objeto> objetos, int ventanaPixeles) {
		super(image);
		this.objetos = objetos;
		this.ventanaPixeles = ventanaPixeles;
	}


	public List<Objeto> getObjetos() {
		return objetos;
	}


	public void setObjetos(List<Objeto> objetos) {
		this.objetos = objetos;
	}


	public int getVentanaPixeles() {
		return ventanaPixeles * getClasificador().getObjetoReferencia().getRelacionPixelCm().intValue();
	}


	public void setVentanaPixeles(int ventanaPixeles) {
		this.ventanaPixeles = ventanaPixeles;
	}
	

	public PlanarImage execute() {
		/*
		if (getObjetos() != null){
			RasgoClase rcCircularidad = new RasgoClase();
			rcCircularidad.setRasgo(new Rasgo("Circularidad"));
			
			RasgoClase rcAspectRadio = new RasgoClase();
			rcAspectRadio.setRasgo(new Rasgo("AspectRadio"));
			
		
			Circularidad circularidad = new Circularidad(rcCircularidad, 1.0, 0.4);
			AspectRatio aspectRadio = new AspectRatio(rcAspectRadio, 1.0, 0.3);
			//Area area = new Area("Area", 3000.0,2000.0);

			List<EvaluadorRasgo> rasgos = new ArrayList<EvaluadorRasgo>();
			rasgos.add(circularidad);
			rasgos.add(aspectRadio);
			
			Clase claseObjetoCircular = new Clase();
			claseObjetoCircular.setNombre("Objeto circular");
			
			List<EvaluadorRasgo> rasgosEvaluador = new ArrayList<EvaluadorRasgo>();
			
			SumaAngulos sumaAngulos = new SumaAngulos(new RasgoClase(new Clase("SumaAngulos")),180.0,180.0);
			sumaAngulos.setObjetoReferencia(getClasificador().getObjetoReferencia());
			rasgosEvaluador.add(sumaAngulos);

			Clase claseEvaluador = new Clase();
			claseEvaluador.setNombre("Objeto circular");

			EvaluadorClase objetoCircular = new EvaluadorClase(claseObjetoCircular, rasgos);
			EvaluadorClase evaluador = new EvaluadorClase(claseEvaluador, rasgosEvaluador);

			List<Objeto> nuevos = new ArrayList<Objeto>();
			int cantObjetos = getObjetos().size();
			for (Objeto obj : getObjetos()) {

				if (!objetoCircular.pertenece(obj,false)){
					if (obj.getName().endsWith("249"))
						System.out.println("");
					//System.out.println("Separar objeto: " + obj.getPixelMedio());
					List<Objeto> nuevosObjetos = separarObjetos(obj, objetoCircular, 0, cantObjetos);
					nuevos.addAll(nuevosObjetos);
					if (nuevosObjetos.size() > 1)
						cantObjetos += nuevosObjetos.size() - 1;
				}
				else{
					nuevos.add(obj);	
				}
					
			}

			setObjetos(nuevos);
		}*/
		return null;
	}
	
	/**
	 * Divide un contorno en dos utilizando los puntos pasados como parametro
	 * @param contorno Contorno original
	 * @param puntoConflicto1 Punto de division 1
	 * @param puntoConflicto2 Puntov de division 2
	 * @param contorno1 Contorno 1
	 * @param contorno2 Contorno 2
	 */
	private void dividirContorno(List<Pixel> contorno, Pixel puntoConflicto1, Pixel puntoConflicto2, List<Pixel> contorno1, List<Pixel> contorno2){
		
		int indexP1 = contorno.indexOf(puntoConflicto1);
		int indexP2 = contorno.indexOf(puntoConflicto2);
		
		if (indexP1 > indexP2){
			int aux = indexP1;
			indexP1 = indexP2;
			indexP2 = aux;
			Pixel auxP = puntoConflicto1.clonar();
			puntoConflicto1 = puntoConflicto2;
			puntoConflicto2 = auxP;
		}
		
		contorno1.clear();
		contorno2.clear();
		
		if (indexP1 != -1 && indexP2 != -1){
			List<Pixel> list1 = new ArrayList<Pixel>(contorno.subList(0, indexP1 + 1));
			List<Pixel> list2 = new ArrayList<Pixel>(contorno.subList(indexP1, indexP2 + 1));
			List<Pixel> list3 = new ArrayList<Pixel>(contorno.subList(indexP2, contorno.size()));
			
			//construimos el contorno 1
			contorno1.addAll(list1);

			List<Pixel> lineaPixeles = ObjetoUtil.crearLinea(puntoConflicto1, puntoConflicto2, getImage().getWidth(), getImage().getHeight());
			contorno1.addAll(lineaPixeles);
			contorno1.addAll(list3);
			
			//contruimos el contorno 2
			contorno2.addAll(list2);
			for(int j = lineaPixeles.size() -1; j >= 0 ; j--)
				contorno2.add(lineaPixeles.get(j));

		}
	}
	
	/**
	 * Recorre el contorno y retorna los puntos en los que la direccion del contorno cambia significativamente.
	 * Con esos puntos se evalua si se puede dividir el objeto.  
	 * @param contorno
	 * @return
	 */
	private List<Pixel> obtenerPuntosDeConflicto(List<Pixel> contorno){
		List<Integer> posPuntosConflicto = new ArrayList<Integer>();
		List<Pixel> puntosConflicto = new ArrayList<Pixel>();
		int posIniVentana = 0;
		int posFinVentana = getVentanaPixeles() - 1;
		Pixel iniVentana = contorno.get(posIniVentana);
		Pixel finVentana = contorno.get(posFinVentana);
		//Para la ecuacion de la recta
		double pendiente1 = 0;
		if (finVentana.getXDouble() - iniVentana.getXDouble() != 0)
			pendiente1 = (finVentana.getYDouble() - iniVentana.getYDouble()) / (finVentana.getXDouble() - iniVentana.getXDouble());
		
		int countPixelsIzquierda = 0;
		int posPuntoConflicto = 0;
		int inicio = getVentanaPixeles()+ 1;
		boolean parar = false;
		int i = inicio;
		Pixel puntoConflic = null;
		while (!parar && contorno.size() > getVentanaPixeles()){
			
			Pixel p = contorno.get(i % contorno.size());
			
			double lado = Pixel.lado(iniVentana, finVentana, p);
			if (lado < 0){
				if (countPixelsIzquierda == 0)
					countPixelsIzquierda++;
				else{
					if (countPixelsIzquierda > 3){
						double pendiente2 = 0;
						if (p.getXDouble() - puntoConflic.getXDouble() != 0){
							pendiente2 = (p.getYDouble() - puntoConflic.getYDouble()) / (p.getXDouble() - puntoConflic.getXDouble());
							double tgAngulo = Math.abs((pendiente2 - pendiente1) / (1 + pendiente2 * pendiente1));
							double angulo = Math.toDegrees(Math.atan(tgAngulo));
							if (angulo > anguloDesvio ){
								countPixelsIzquierda++;
							}
							else
								countPixelsIzquierda = 0;
						}	
					}
					else 
						countPixelsIzquierda++;
					
				}
				
			}
			else
				countPixelsIzquierda = 0;
			
			if (countPixelsIzquierda == 0){
				posIniVentana = (Math.abs(i - getVentanaPixeles()) + 1) % contorno.size();
				posFinVentana = i % contorno.size();
				iniVentana = contorno.get(posIniVentana);
				finVentana = contorno.get(posFinVentana);

				if (finVentana.getXDouble() - iniVentana.getXDouble() != 0)
					pendiente1 = (finVentana.getYDouble() - iniVentana.getYDouble()) / (finVentana.getXDouble() - iniVentana.getXDouble());
			}

			if (countPixelsIzquierda == 1){

				posPuntoConflicto = i;
				puntoConflic  = p;
			}
			
			if (puntoConflic != null && countPixelsIzquierda > getVentanaPixeles()){
				posPuntosConflicto.add(posPuntoConflicto);
				countPixelsIzquierda = 0;
				puntoConflic = null;
				//Empiezo a recorrer desde el ultimo punto de conflicto
				
				posIniVentana = posPuntoConflicto% contorno.size();
				posFinVentana = (posPuntoConflicto + getVentanaPixeles()) % contorno.size();
				i = posFinVentana;

				iniVentana = contorno.get(posIniVentana);
				finVentana = contorno.get(posFinVentana);

				if (finVentana.getXDouble() - iniVentana.getXDouble() != 0)
					pendiente1 = (finVentana.getYDouble() - iniVentana.getYDouble()) / (finVentana.getXDouble() - iniVentana.getXDouble());
			}

							
			i = i + 1;
			//posIniVentana = (posIniVentana + 1) % contorno.size();
			//posFinVentana = (posFinVentana + 1) % contorno.size();
			if (i  % contorno.size() == inicio)
				parar = true;
		}
		if (posPuntosConflicto.size() > 1){
			for(i = 0; i < posPuntosConflicto.size(); i++)
				puntosConflicto.add(contorno.get(posPuntosConflicto.get(i)));

		}
		return puntosConflicto;
	}

	/**
	 * Recorre el contorno y retorna los puntos en los que la direccion del contorno cambia significativamente.
	 * Con esos puntos se evalua si se puede dividir el objeto.  
	 * @param contorno
	 * @return
	 */
	private List<Pixel> obtenerPuntosDeConflicto2(List<Pixel> contorno){
		List<Pixel> puntosConflicto = new ArrayList<Pixel>();
		int posIni = 0;
		int posFin = (2 * getVentanaPixeles() - 1) % contorno.size();
		Pixel pInicio = contorno.get(posIni);
		Pixel pFin = contorno.get(posFin);
		int inicio = getVentanaPixeles() - 1;
		boolean parar = false;
		int i = inicio;
		Pixel puntoConflicto = null;
		Double anguloMejor = null;
		while (!parar && contorno.size() > getVentanaPixeles()){
			
			Pixel pMedio = contorno.get(i % contorno.size());
			double lado = Pixel.lado(pInicio, pMedio, pFin);
			boolean agregoPunto = false;
			if (lado < 0){
				if(pMedio.getX() == 72 && pMedio.getY() == 89)
					System.out.println("");
				double angulo = ObjetoUtil.calcularAngulo(pInicio, pMedio, pFin);
				if (angulo > anguloDesvio && (puntoConflicto == null || angulo > anguloMejor)){
					puntoConflicto = pMedio;
					anguloMejor = angulo;
					
				}
				else{
					if (puntoConflicto != null){
						puntosConflicto.add(puntoConflicto);
						agregoPunto = true;
					}
				}
			}
			if (agregoPunto){
				posIni = contorno.indexOf(puntoConflicto);
				i = (posIni + getVentanaPixeles()) % contorno.size();
				posFin = (i + getVentanaPixeles()) % contorno.size();
				pInicio = contorno.get(posIni);
				pFin = contorno.get(posFin);
				puntoConflicto = null;
				anguloMejor = null;						
			}
			else{
				posIni = (posIni + 1) % contorno.size();
				i = (i + 1) % contorno.size();
				posFin = (posFin + 1) % contorno.size();
				pInicio = contorno.get(posIni);
				pFin = contorno.get(posFin);
			}

			if (i  % contorno.size() <= getVentanaPixeles() - 1)
				parar = true;
		}
		return puntosConflicto;
	}

	 /* 
	 * @param obj
	 * @param objetoCircular
	 * @return
	 */
	private List<Objeto> separarObjetos(Objeto obj, EvaluadorClase objetoCircular, int nivel, int cantObjetos) {	
		if (obj.getName().endsWith("52"))
			System.out.println("");
		
		List<Pixel> contorno = obj.getContorno();
		/*
		int tamanioSegmento = (int)((double) porcTamanioSegmento * contorno.size() / 100);
		if (tamanioSegmento <= 10)
			tamanioSegmento = 10;
		setVentanaPixeles(tamanioSegmento);
		*/
		if (contorno.size() > getVentanaPixeles() && nivel < 50){
			
			boolean huboDivision = false;
			List<Pixel> puntosConflicto = obtenerPuntosDeConflicto(contorno);
			if (puntosConflicto.size() > 1){
		
				List<Objeto> objetos = new ArrayList<Objeto>();
				Objeto objetoActual = obj;
				
				contorno = objetoActual.getContorno();
				
				List<Pixel> puntosConflictoVisitados = new ArrayList<Pixel>();
				for(Pixel puntoConflicto: puntosConflicto){
					boolean parar = false;

					puntosConflictoVisitados.add(puntoConflicto);
					List<Pixel> puntosConflicAux = new ArrayList<Pixel>(puntosConflicto);
					
					while (!parar){
						Pixel nextPuntoConflic = puntoConflicto.getPixelMasCercano(puntosConflicAux);
						if (nextPuntoConflic == null)
							parar = true;
						else{
							puntosConflicAux.remove(nextPuntoConflic);
							List<Pixel> contorno1 = new ArrayList<Pixel>();
							List<Pixel> contorno2 = new ArrayList<Pixel>();
							
							dividirContorno(contorno, puntoConflicto, nextPuntoConflic, contorno1, contorno2);
							
							Objeto obj1 = new Objeto();
							obj1.setOriginalImage(getOriginalImage());
							obj1.setContorno(contorno1);
							Objeto obj2 = new Objeto();
							obj2.setOriginalImage(getOriginalImage());
							obj2.setContorno(contorno2);
							
							Objeto nuevoObjeto = null;
							Objeto objResto = null;
							
							if (obj2.validarContorno() && objetoCircular.pertenece(obj2, false)){
								nuevoObjeto = obj2;
								objResto = obj1;
							}
							
							else if (obj1.validarContorno() && objetoCircular.pertenece(obj1, false)){
								nuevoObjeto = obj1;
								objResto = obj2;
							}
							else{
								if (obj1.getContorno().size() > obj2.getContorno().size()){
									nuevoObjeto = obj1;
									objResto = obj2;
								}
								else{
									nuevoObjeto = obj2;
									objResto = obj1;
								}
							}
							
							if (nuevoObjeto != null && nuevoObjeto.getContorno().size() > 100){
								
								detectarContorno.limpiarVisitados();
								detectarContorno.completarObjeto(nuevoObjeto);
								nuevoObjeto.calcularMRC();
								objetos.add(nuevoObjeto);
								nuevoObjeto.setName(obj.getName());
								parar = true;
								
								String info = "Objeto catalogado: "
									+ nuevoObjeto.getName()
									+ " - Puntos detectados: "
									+ nuevoObjeto.getPuntos().size();
						
								Visualizador.addLogInfo(info);
								
								
								contorno = objResto.getContorno();
								objResto.setName("Maiz" + (cantObjetos + 1));
								List<Objeto> nuevosObjetos = separarObjetos(objResto, objetoCircular, nivel + 1, cantObjetos + 1);
								objetos.addAll(nuevosObjetos);
								
								huboDivision = true;
								
								puntosConflictoVisitados.add(nextPuntoConflic);
							}
						}
					}
					if (huboDivision)
						break;
				}
				if (!huboDivision && contorno.size() > 0){
					Objeto nuevoObj = new Objeto();
					nuevoObj.setOriginalImage(getOriginalImage());
					nuevoObj.setContorno(contorno);
					if (nuevoObj.validarContorno()){
						detectarContorno.limpiarVisitados();
						detectarContorno.completarObjeto(nuevoObj);
						nuevoObj.calcularMRC();
						objetos.add(nuevoObj);
						nuevoObj.setName(obj.getName());
						
						String info = "Objeto catalogado: "
							+ nuevoObj.getName()
							+ " - Puntos detectados: "
							+ nuevoObj.getPuntos().size();
				
						Visualizador.addLogInfo(info);
						
					}
				}
				return objetos;
			}
		}
		
		if (obj.validarContorno()){
			detectarContorno.limpiarVisitados();
			detectarContorno.completarObjeto(obj);
		}
		List<Objeto> list = new ArrayList<Objeto>();
		if (obj.getPuntos() == null || obj.getPuntos().size() == 0){
			detectarContorno.limpiarVisitados();
			detectarContorno.completarObjeto(obj);
			obj.calcularMRC();
		}
		list.add(obj);
		return list;
	}
	/**
	 * 
	 * @param obj
	 * @param objetoCircular
	 * @return
	 */
	private List<Objeto> separarObjetos2(Objeto obj, EvaluadorClase objetoCircular, int nivel, int cantObjetos) {	
	
		List<Pixel> contorno = obj.getContorno();

		if (contorno.size() > getVentanaPixeles() && nivel < 20){
			
			boolean huboDivision = false;
			List<Pixel> puntosConflicto = obtenerPuntosDeConflicto2(contorno);
			if (puntosConflicto.size() > 1){
		
				List<Objeto> objetos = new ArrayList<Objeto>();
				Objeto objetoActual = obj;
				
				contorno = objetoActual.getContorno();
				List<Pixel> contorno1 = new ArrayList<Pixel>();
				List<Pixel> contorno2 = new ArrayList<Pixel>();
				separarContorno(contorno, puntosConflicto,contorno1, contorno2);
				
				Objeto obj1 = new Objeto();
				obj1.setOriginalImage(getOriginalImage());
				obj1.setContorno(contorno1);
				Objeto obj2 = new Objeto();
				obj2.setOriginalImage(getOriginalImage());
				obj2.setContorno(contorno2);
				
				Objeto nuevoObjeto = null;
				Objeto objResto = null;
				
				if (obj2.validarContorno() && objetoCircular.pertenece(obj2, false)){
					nuevoObjeto = obj2;
					objResto = obj1;
				}
				
				else if (obj1.validarContorno() && objetoCircular.pertenece(obj1, false)){
					nuevoObjeto = obj1;
					objResto = obj2;
				}
				
				if (nuevoObjeto != null && nuevoObjeto.getContorno().size() > 100){
					
					detectarContorno.limpiarVisitados();
					detectarContorno.completarObjeto(nuevoObjeto);
					nuevoObjeto.calcularMRC();
					objetos.add(nuevoObjeto);
					nuevoObjeto.setName(obj.getName());
					
					String info = "Objeto catalogado: "
						+ nuevoObjeto.getName()
						+ " - Puntos detectados: "
						+ nuevoObjeto.getPuntos().size();
			
					Visualizador.addLogInfo(info);
					
					objResto.calcularMRC();
					contorno = objResto.getContorno();
					objResto.setName("Maiz" + (cantObjetos + 1));
					List<Objeto> nuevosObjetos = separarObjetos2(objResto, objetoCircular, nivel + 1, cantObjetos + 1);
					objetos.addAll(nuevosObjetos);
					
					huboDivision = true;
					
				}								

				if (!huboDivision && contorno.size() > 0){
					Objeto nuevoObj = new Objeto();
					nuevoObj.setOriginalImage(getOriginalImage());
					nuevoObj.setContorno(contorno);
					if (nuevoObj.validarContorno()){
						detectarContorno.limpiarVisitados();
						detectarContorno.completarObjeto(nuevoObj);
						nuevoObj.calcularMRC();
						objetos.add(nuevoObj);
						nuevoObj.setName(obj.getName());
						
						String info = "Objeto catalogado: "
							+ nuevoObj.getName()
							+ " - Puntos detectados: "
							+ nuevoObj.getPuntos().size();
				
						Visualizador.addLogInfo(info);
						
					}
				}
				return objetos;
			}
		}
		
		if (obj.validarContorno()){
			detectarContorno.limpiarVisitados();
			detectarContorno.completarObjeto(obj);
		}
		List<Objeto> list = new ArrayList<Objeto>();
		if (obj.getPuntos() == null || obj.getPuntos().size() == 0){
			detectarContorno.limpiarVisitados();
			detectarContorno.completarObjeto(obj);
			obj.calcularMRC();
		}
		list.add(obj);
		return list;
	}

	private void separarContorno(List<Pixel> contorno, List<Pixel> puntosConflicto, List<Pixel> objeto1, List<Pixel> objeto2) {
		Double mejor = null;
		List<Pixel> contorno1Mejor = new ArrayList<Pixel>();
		List<Pixel> contorno2Mejor = new ArrayList<Pixel>();
		for(Pixel puntoConflicto:puntosConflicto){
			int posPuntoConflicto = contorno.indexOf(puntoConflicto);
			int posPuntoAnterior = posPuntoConflicto - getVentanaPixeles();
			if (posPuntoAnterior < 0)
				posPuntoAnterior = (contorno.size() - posPuntoAnterior) % contorno.size();
			Pixel puntoAnterior = contorno.get(posPuntoAnterior);
			Pixel nextPuntoConflic = seleccionarPuntoConflicto(puntoAnterior, puntoConflicto,puntosConflicto);
			if (nextPuntoConflic == null)
				break;
			else{
				List<Pixel> contorno1 = new ArrayList<Pixel>();
				List<Pixel> contorno2 = new ArrayList<Pixel>();
				if (puntoConflicto.getX()== 74 && puntoConflicto.getY() == 91)
					System.out.println("");
				dividirContorno(contorno, puntoConflicto, nextPuntoConflic, contorno1, contorno2);
				
				Objeto obj1 = new Objeto();
				obj1.setOriginalImage(getOriginalImage());
				obj1.setContorno(contorno1);
				//obj1.calcularMRC();
				Objeto obj2 = new Objeto();
				obj2.setOriginalImage(getOriginalImage());
				obj2.setContorno(contorno2);
				//obj2.calcularMRC();
				
				double actual = puntoConflicto.distancia(nextPuntoConflic);
				if (mejor == null || actual < mejor){
					contorno1Mejor = contorno1;
					contorno2Mejor = contorno2;
					mejor = actual;
				}
			}
		}
		objeto1.clear();
		objeto2.clear();
		objeto1.addAll(contorno1Mejor);
		objeto2.addAll(contorno2Mejor);
	}


	/**
	 * Selecciona el punto que este a la derecha de la recta formada por los puntos puntoAnterior y puntoConflicto y cuyo
	 * angulo con la recta sea el menor posible  
	 * @param puntoAnterior
	 * @param puntoConflicto
	 * @param puntosConflicAux
	 * @return
	 */
	private Pixel seleccionarPuntoConflicto(Pixel puntoAnterior, Pixel puntoConflicto, List<Pixel> puntosConflicAux) {
		if (puntosConflicAux != null && puntosConflicAux.size() > 0){
			double anguloMenor = Double.MAX_VALUE;
			double distanciaMenor = Double.MAX_VALUE;
			Pixel mejor = null;
			for(Pixel p:puntosConflicAux){
				if (!p.equals(puntoConflicto)){
					double lado = Pixel.lado(puntoAnterior, puntoConflicto, p);
					if (lado > 0){
						/*
						double angulo = ObjetoUtil.calcularAngulo(puntoAnterior, puntoConflicto, p);
						if (angulo < anguloMenor){
							anguloMenor = angulo;
							mejor = p;
						}*/
						double distancia = p.distancia(puntoConflicto);
						if (distancia < distanciaMenor){
							distancia = distanciaMenor;
							mejor = p;
						}
						
					}
				}
			}
			return mejor;
		}
		return null;
	}



	public String getCommandName() {
		return this.getClass().getName();
	}

	
	public void postExecute() {
		// TODO Auto-generated method stub

	}


	public PlanarImage getOriginalImage() {
		return originalImage;
	}


	public void setOriginalImage(PlanarImage originalImage) {
		this.originalImage = originalImage;
	}

}
