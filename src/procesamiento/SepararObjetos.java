package procesamiento;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import objeto.BoundingBox;
import objeto.Clase;
import objeto.Objeto;
import objeto.ObjetoUtil;
import objeto.Pixel;
import objeto.PixelComparator;
import objeto.Rasgo;
import objeto.RasgoClase;
import procesamiento.clasificacion.Circularidad;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.EvaluadorRasgo;
import aplicarFiltros.Visualizador;

public class SepararObjetos extends AbstractImageCommand {

	/**
	 * Cantidad de pixeles del contorno a utilizar para ver si un pixel se desvía 
	 * demasiado del contorno. Lo que indicaría que pertenece a otro objeto
	 */
	private int ventanaPixeles = 10;
	private static final double anguloDesvio = 40; 
	
	private PlanarImage originalImage;
	
	List<Objeto> objetos = null;
	
	DetectarContorno detectarContorno = null;
	
	PlanarImage imagenBordes = null;
	BoundingBox boundingBoxObjetoInicial = null;
	
	private int maxMatrixW = 1024;
	private int maxMatrixH = 1024;

	private int Matriz[][] = null;
	
	private EvaluadorClase evaluadorObjetoCircular = null;
	
	public SepararObjetos(PlanarImage image, List<Objeto> objetos, DetectarContorno padre) {
		super(image);
		this.objetos = objetos;
		this.detectarContorno = padre;
		createEvaluadorObjetoCircular();
	}


	public SepararObjetos(PlanarImage image, List<Objeto> objetos, int ventanaPixeles) {
		super(image);
		this.objetos = objetos;
		this.ventanaPixeles = ventanaPixeles;
		createEvaluadorObjetoCircular();
	}
	
	/**
	 * Crea y asigna el atributo evaluadorObjetoCircular 
	 */
	private void createEvaluadorObjetoCircular(){
		RasgoClase rcCircularidad = new RasgoClase();
		rcCircularidad.setRasgo(new Rasgo("Circularidad"));
		
		RasgoClase rcAspectRadio = new RasgoClase();
		rcAspectRadio.setRasgo(new Rasgo("AspectRadio"));
		
	
		Circularidad circularidad = new Circularidad(rcCircularidad, 0.875, 0.125);
		//AspectRatio aspectRadio = new AspectRatio(rcAspectRadio, 0.6, 0.4);
		//Area area = new Area("Area", 3000.0,2000.0);

		List<EvaluadorRasgo> rasgos = new ArrayList<EvaluadorRasgo>();
		rasgos.add(circularidad);
		//rasgos.add(aspectRadio);
		
		Clase claseObjetoCircular = new Clase();
		claseObjetoCircular.setNombre("Objeto circular");
		
		EvaluadorClase objetoCircular = new EvaluadorClase(claseObjetoCircular, rasgos);
		setEvaluadorObjetoCircular(objetoCircular);
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
		if (getObjetos() != null){
			List<Objeto> nuevos = new ArrayList<Objeto>();
			int cantObjetos = getObjetos().size() + getClasificador().getCantidadObjetos();
			for (Objeto obj : getObjetos()) {

				if (necesitaDivision(obj)){
					setImagenBordes(getImagenBordes(obj));
					JAI.create("filestore", getImagenBordes(), "sobel.tif", "TIFF");
					initVisitados();
					List<Pixel> puntosDivisionVisitados = new ArrayList<Pixel>();
					List<Objeto> nuevosObjetos = separarObjetosImagenBordes(obj, puntosDivisionVisitados, 0, cantObjetos);
					nuevos.addAll(nuevosObjetos);
					if (nuevosObjetos.size() > 1)
						cantObjetos += nuevosObjetos.size() - 1;
				}
				else{
					nuevos.add(obj);	
				}
					
			}

			setObjetos(nuevos);
		}
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
	private void dividirObjeto(Objeto obj, Pixel puntoConflicto1, Pixel puntoConflicto2, List<Pixel> camino, Objeto objeto1, Objeto objeto2){
		if (camino.size() > 0){
			List<Pixel> contorno = obj.getContorno(); 
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
			
			List<Pixel> contorno1 = objeto1.getContorno();
			contorno1.clear();
			List<Pixel> contorno2 = objeto2.getContorno();
			contorno2.clear();
			
			if (indexP1 != -1 && indexP2 != -1){
				List<Pixel> list1 = new ArrayList<Pixel>(contorno.subList(0, indexP1 + 1));
				List<Pixel> list2 = new ArrayList<Pixel>(contorno.subList(indexP1, indexP2 + 1));
				List<Pixel> list3 = new ArrayList<Pixel>(contorno.subList(indexP2, contorno.size()));
				
				Pixel primeroCamino = camino.get(0);
				camino.remove(puntoConflicto1);
				camino.remove(puntoConflicto2);
				if (puntoConflicto1.equals(primeroCamino)){
					//construimos el contorno 1
					contorno1.addAll(list1);
					contorno1.addAll(camino);
					contorno1.addAll(list3);
					
					//contruimos el contorno 2
					contorno2.addAll(list2);
					for(int j = camino.size() -1; j >= 0 ; j--)
						contorno2.add(camino.get(j));
				}
				else{
					//construimos el contorno 1
					contorno1.addAll(list1);
					for(int j = camino.size() -1; j >= 0 ; j--)
						contorno1.add(camino.get(j));
					contorno1.addAll(list3);
					
					//contruimos el contorno 2
					contorno2.addAll(list2);
					contorno2.addAll(camino);
				}
				
				objeto1.setContorno(contorno1);
				objeto2.setContorno(contorno2);
			}			
		}
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
	private List<Pixel> obtenerPuntosDeConflictoNuevo(List<Pixel> contorno){
		List<Pixel> puntosConflicto = new ArrayList<Pixel>();
		
		int posPuntoConflicto = 0;
		int posPixelInicial = 2 * getVentanaPixeles();
		boolean parar = false;
		int i = posPixelInicial;
		double anguloMejor = 0;
		Pixel puntoConflicto = null;
		int countPixelesDesvio = 0;
		int countPixeles = 0;
		while (!parar && contorno.size() > 2 * getVentanaPixeles()){
			
			Pixel p = contorno.get(i % contorno.size());
			int posInicio = i - 2 * getVentanaPixeles();
			if (posInicio < 0)
				posInicio = contorno.size() + posInicio;
			int posMedio = i - getVentanaPixeles();
			if (posMedio < 0)
				posMedio = contorno.size() + posMedio;
			Pixel inicio = contorno.get(posInicio % contorno.size() );
			Pixel medio = contorno.get(posMedio % contorno.size());
			Pixel fin = p;
			double lado = Pixel.lado(inicio, medio, fin);
			if (lado < 0){
				double angulo = ObjetoUtil.calcularAngulo(inicio, medio, fin);
				if (angulo > anguloDesvio && (puntoConflicto == null || angulo > anguloMejor)){
					puntoConflicto = medio;
					anguloMejor = angulo;
					posPuntoConflicto = i;
				}
				countPixelesDesvio++;
				if(puntoConflicto != null && countPixelesDesvio > getVentanaPixeles()){
					puntosConflicto.add(puntoConflicto);
					puntoConflicto = null;
					countPixelesDesvio = 0;
					i = posPuntoConflicto + getVentanaPixeles();
				}
			}
			else{
				puntoConflicto = null;
				countPixelesDesvio = 0;
			}
					
						
			i = i + 1;
			countPixeles ++;
			if (countPixeles > contorno.size())
				parar = true;
		}
		return puntosConflicto;
	}

	
	private PlanarImage getImagenBordes(Objeto obj){
		BoundingBox bb = obj.getBoundingBox();
		setBoundingBoxObjetoInicial(bb);
		Rectangle rectangle = new Rectangle((int)bb.getMinX(), (int)bb.getMinY(), (int)(bb.getMaxX() - bb.getMinX()), (int)(bb.getMaxY() - bb.getMinY()));
		BufferedImage image = getOriginalImage().getAsBufferedImage(rectangle,null);
		PlanarImage ti = TiledImage.wrapRenderedImage(image);
		
		EliminarFondo ef = new EliminarFondo(ti, detectarContorno.getRangeFondo());
		PlanarImage output = ef.execute();
		
		ConvertEscalaGrises dcg = new ConvertEscalaGrises(output);
		output = dcg.execute();
		
		SobelFilter so = new SobelFilter(output);
		so.setClasificador(getClasificador());
		output = so.execute();
		
		HSVRange range = new HSVRange();
		range.setVMax(60f);
		EliminarFondo ef2 = new EliminarFondo(output, range);
		output = ef2.execute();
		return output;
	}
	
	/**
	 * Obtiene el pixel dentro de un contorno
	 * @param p
	 * @param contorno
	 * @return
	 */
	private Pixel getAnterior(Pixel p,List<Pixel> contorno){
		int index = contorno.indexOf(p);
		int indexAnterior = index - getVentanaPixeles();
		if (indexAnterior < 0)
			indexAnterior = contorno.size() + indexAnterior;
		Pixel anterior = contorno.get(indexAnterior);
		return anterior;
	}
	
	/**
	 * Obtiene el pixel dentro de un contorno
	 * @param p
	 * @param contorno
	 * @return
	 */
	private Pixel getPosterior(Pixel p,List<Pixel> contorno){
		int index = contorno.indexOf(p);
		int indexPosterior = index + getVentanaPixeles();
		Pixel posterior = contorno.get(indexPosterior);
		return posterior;
	}
	
	/**
	 * Retorna si algun pixel del contorno intersecta a la recta formada por los puntos p1 y p2
	 * @return
	 */
	private boolean intersectaAlgunPuntoRecta(Pixel p1, Pixel p2, List<Pixel> contorno){
		if (contorno.size() > 1) {
			
			int sizeLado = getVentanaPixeles();
			Pixel inicio = null;
			Pixel fin = null;
			
			BoundingBox bb1 = new BoundingBox(p1,p2);
			
			for (int i = sizeLado; i < contorno.size(); i= i + sizeLado ) {
				inicio = contorno.get(i - sizeLado);
				fin = contorno.get(i);
				BoundingBox bb2 = new BoundingBox(inicio,fin);
				Pixel interseccion = ObjetoUtil.calcularPuntoInterseccion(p1, p2, inicio, fin);
				if (interseccion != null && bb1.isPertenece(interseccion) && bb2.isPertenece(interseccion)
						&& p1.distancia(interseccion) > getVentanaPixeles() && p2.distancia(interseccion) > getVentanaPixeles())
					return true;
				
			}
			if (fin != null){
				inicio = fin;
				fin = contorno.get(0);
				BoundingBox bb2 = new BoundingBox(inicio,fin);
				Pixel interseccion = ObjetoUtil.calcularPuntoInterseccion(p1, p2, inicio, fin);
				if (interseccion != null && bb1.isPertenece(interseccion) && bb2.isPertenece(interseccion)
						&& p1.distancia(interseccion) > getVentanaPixeles() && p2.distancia(interseccion) > getVentanaPixeles())
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Reordena los puntos de division en sentido antihorario con respecto a un punto
	 * @param desvioHorario
	 * @param puntosDivision
	 * @param puntosDivisionVisitados  Pares de puntos de division ya evaluados
	 * @param contornoObjeto Contorno del objeto
	 * @return
	 */
	private List<Pixel> getPosiblesDesviosAntihorarios(Pixel desvioHorario, List<Pixel> puntosDivision, List<Pixel> puntosDivisionVisitados, List<Pixel> contornoObjeto){
		List<Pixel> desviosAntihorarios = new ArrayList<Pixel>();
		desviosAntihorarios.remove(desvioHorario);
		Pixel anteriorDH = getAnterior(desvioHorario, contornoObjeto);
		int direccionDH = desvioHorario.getDireccion(anteriorDH);
		for(Pixel p:puntosDivision){
			//Pixel parPuntoDivision = desvioHorario.clonar();
			//parPuntoDivision.sumar(p);
			if (!desvioHorario.equals(p) && !desviosAntihorarios.contains(p) 
					&& !puntosDivisionVisitados.contains(p)){
				double lado = Pixel.lado(anteriorDH, desvioHorario, p);
				int direccion = p.getDireccion(desvioHorario);
				int distanciaDireccion = Pixel.distanciaLado(direccionDH, direccion);
				if (lado >= 0 && distanciaDireccion <= 2 && !intersectaAlgunPuntoRecta(desvioHorario, p, contornoObjeto)){
					double peso = desvioHorario.distancia(p);
					p.setPeso(peso);
					desviosAntihorarios.add(p);
				}
				
			}
		}
		Collections.sort(desviosAntihorarios, new PixelComparator(true));
		List<Pixel> result = new ArrayList<Pixel>(); 
		if (desviosAntihorarios.size() > 0)
			result.add(desviosAntihorarios.get(0)) ;
		return result;
		//return desviosAntihorarios;
	}
	
	/**
	 * Separa un objetos utilizando la imagen que contiene los bordes
	 * @param obj
	 * @param objetoCircular
	 * @param nivel
	 * @param cantObjetos
	 * @return
	 */
	private List<Objeto> separarObjetosImagenBordes(Objeto obj, List<Pixel> puntosDivisionVisitados, int nivel, int cantObjetos) {
		List<Pixel> contornoObjeto = obj.getContorno();
		List<Pixel> puntosDivision = obtenerPuntosDeConflictoNuevo(contornoObjeto);
		List<Objeto> objetosResult = new ArrayList<Objeto>();
		if (puntosDivision.size() > 1){
			initVisitados();
			List<Division> divisionesPosibles = new ArrayList<Division>();
			List<Pixel> posiblesVisitados = new ArrayList<Pixel>(puntosDivisionVisitados); 
			for(int i = 0; i < puntosDivision.size() ; i++){
				Pixel desvioHorario = puntosDivision.get(i);
				List<Pixel> desviosAntihorarios = getPosiblesDesviosAntihorarios(desvioHorario , puntosDivision, posiblesVisitados, contornoObjeto);
				for(int j = 0; j < desviosAntihorarios.size(); j++){
					Pixel desvioAntihorario = desviosAntihorarios.get(j);
					Division division = new Division(desvioHorario, desvioAntihorario, null);
					if (!divisionesPosibles.contains(division)){
						//posiblesVisitados.add(desvioHorario);
						//posiblesVisitados.add(desvioAntihorario);
						
						Pixel desvioHorarioAux = getBoundingBoxObjetoInicial().getPixelRelativo(desvioHorario);
						Pixel desvioAntihorarioAux = getBoundingBoxObjetoInicial().getPixelRelativo(desvioAntihorario);
						//Marco el par de puntos de division procesado 

						Division caminoRelativo = findCamino(desvioAntihorarioAux , desvioHorarioAux);
						if (caminoRelativo == null)
							caminoRelativo = findCamino(desvioHorarioAux, desvioAntihorarioAux);
						if (caminoRelativo == null){
							List<Pixel> camino = ObjetoUtil.crearLinea(desvioHorario, desvioAntihorario, getImage().getWidth(), getImage().getHeight());
							caminoRelativo = new Division(desvioHorario, desvioAntihorario, camino);
							caminoRelativo.setEvaluacion(0);
						}
						divisionesPosibles.add(caminoRelativo);						
					}
				}
				
			}
			
			if (divisionesPosibles.size() > 0){
				Collections.sort(divisionesPosibles);
				boolean huboDivision = false;
				for(int i = 0; i < divisionesPosibles.size() && !huboDivision; i++){
					Division mejorDivision = divisionesPosibles.get(i);
					Objeto obj1 = new Objeto();
					obj1.setOriginalImage(getOriginalImage());
					Objeto obj2 = new Objeto();
					obj2.setOriginalImage(getOriginalImage());

					dividirObjeto(obj, mejorDivision.origen, mejorDivision.fin, mejorDivision.puntos, obj1, obj2);
					if (obj1.validarContorno() && obj2.validarContorno()){
						boolean isCircularObj1 = getEvaluadorObjetoCircular().pertenece(obj1, false);
						boolean isCircularObj2 = getEvaluadorObjetoCircular().pertenece(obj2, false);
						if (isCircularObj1 || isCircularObj2){
							huboDivision = true;
							obj1.setName(obj.getName());
							obj2.setName("Objeto" + (cantObjetos + 1));
							
							cantObjetos++;
							puntosDivisionVisitados.add(mejorDivision.origen);
							puntosDivisionVisitados.add(mejorDivision.fin);
							List<Objeto> nuevosObjetos = new ArrayList<Objeto>();
							if (!necesitaDivision(obj1) && isCircularObj1){
								completarObjeto(obj1);
								objetosResult.add(obj1);
							}
							else{
								nuevosObjetos = separarObjetosImagenBordes(obj1, puntosDivisionVisitados, nivel + 1, cantObjetos + 1);
								if (nuevosObjetos.size() == 1){
									completarObjeto(obj1);
								}
								objetosResult.addAll(nuevosObjetos);
								
							}
							if (!necesitaDivision(obj2) && isCircularObj2){
								completarObjeto(obj2);
								objetosResult.add(obj2);
							}
							else{
								cantObjetos += nuevosObjetos.size() - 1;
								nuevosObjetos = separarObjetosImagenBordes(obj2, puntosDivisionVisitados, nivel + 1, cantObjetos + 1);
								if (nuevosObjetos.size() == 1){
									completarObjeto(obj2);
								}
								objetosResult.addAll(nuevosObjetos);
							}
						}
					}
				}
			}
		}
		if (objetosResult.size() == 0){
			objetosResult.add(obj);
		}
		return objetosResult;
	}
	
	/**
	 * Evalua si un objeto necesita ser dividido
	 * @param obj1
	 * @return
	 */
	private boolean necesitaDivision(Objeto obj1) {
		List<Pixel> puntosDivision = obtenerPuntosDeConflictoNuevo(obj1.getContorno());
		if (puntosDivision.size() > 1){
			return true;
		}
		return false;
	}


	private void completarObjeto(Objeto obj){
		detectarContorno.limpiarVisitados();
		detectarContorno.completarObjeto(obj);
		obj.calcularMRC();
		
		String info = "Objeto catalogado: "
			+ obj.getName()
			+ " - Puntos detectados: "
			+ obj.getPuntos().size();

		Visualizador.addLogInfo(info);
	}
	
	/* 
	 * @param obj
	 * @param objetoCircular
	 * @return
	 */
	private List<Objeto> separarObjetos(Objeto obj, EvaluadorClase objetoCircular, int nivel, int cantObjetos) {	
		
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

	public String getCommandName() {
		return this.getClass().getName();
	}

	
	public void postExecute() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Retorna el próximo pixel que forma el contorno de un objeto en un direccin dada
	 * 
	 * @param pixel
	 *            Pixel actual que es contorno
	 * @param pixelAnt
	 *            Pixel anterior al actual que es contorno
	 * @param origen
	 *            Pixel desde el cuál se partió
	 * @param offset
	 *            Offset de la imagen
	 * @return
	 */
	public List<Pixel> getNextContornoDireccion(Pixel pixel, Pixel pixelAnt, Pixel origen, int direccion, boolean horario) {
		List<Pixel> posibles = new ArrayList<Pixel>();
		int[] recorrido = null;
		if (horario)
			recorrido = Pixel.getRecorridoHorarioAdayacentes(direccion, 2);
		else
			recorrido = Pixel.getRecorridoAntiHorarioAdayacentes(direccion, 2);
		int peso = recorrido.length;
		for (int dir : recorrido) {
			Pixel actual = getAdyacente(pixel, dir, getImagenBordes());
			if (pixelAnt != null && !pixelAnt.equals(origen)
					&& origen.equals(actual)){
				posibles.clear();
				posibles.add(actual);
				return posibles;
			}
			double lado = Pixel.lado(pixelAnt, pixel, actual);
			if (lado >= 0){
				if (actual != null && !pixel.equals(actual) && !isVisitado(actual)
						&& isContorno(actual, getImagenBordes()) && !Pixel.isOpuestoLado(direccion, dir)) { 
					actual.setPeso(lado);
					posibles.add(actual);
				}
			}
			peso--;
		}
		Collections.sort(posibles, new PixelComparator(true));
		return posibles;
	}

	/**
	 * Busca el borde mas cercano en una direccion dada
	 * @param dir Direccion
	 * @param inicio Punto inicial
	 * @param fin Punto final
	 * @return
	 */
	private Pixel findBordeMasCercano(int dir, Pixel inicio, Pixel fin, Integer cantMaxima) {
		double distanciaInicial = Double.MAX_VALUE; 
		if (fin != null)
			distanciaInicial = inicio.distancia(fin);
		double distanciaActual = distanciaInicial - 1;
		
		int direcciones[] = Pixel.getRecorridoHorarioAdayacentes(dir, 1);
		List<Pixel> puntosContorno = new ArrayList<Pixel>();
		for(int direccion: direcciones){
			Pixel actual = getAdyacente(inicio, direccion, getImage());
			Pixel anterior = inicio;
			int i = 0;
			distanciaActual = actual.distancia(fin);
			while (actual != null && !actual.equals(fin) && isFondo(actual, getImagenBordes()) 
					&& !isVisitado(actual) && distanciaActual < distanciaInicial && (cantMaxima == null || i < cantMaxima )){
				anterior = actual;
				actual = getAdyacente(actual, direccion, getImage());
				if (actual != null && fin != null){
					if (isFondo(actual, getImagenBordes())){
						List<Pixel> posibles = getNextContornoDireccion(actual, anterior, inicio, direccion, true);
						if (posibles != null && posibles.size() > 0){
							puntosContorno.addAll(posibles);
							actual = null;
						}
					}
					if (actual != null)
						distanciaActual = actual.distancia(fin);	
				}
				i++;
			}
			if (actual != null && !isVisitado(actual) && !isFondo(actual, getImagenBordes())){
				actual.setPeso(actual.distancia(fin));
				puntosContorno.add(actual);
			}
		}
		Collections.sort(puntosContorno, new PixelComparator(true));
		if (puntosContorno.size() > 0)
			return puntosContorno.get(0);
		return null;
	}

	/**
	 * Busca un camino de borde entre los puntos inicio y fin
	 * @param inicio
	 * @param fin
	 * @return
	 */
	private Division findCamino(Pixel inicio, Pixel fin) {
		List<Pixel> contorno = new ArrayList<Pixel>();
		List<Pixel> visitados = new ArrayList<Pixel>();
		Pixel anterior = inicio;
		Pixel actual = inicio;
		//double distanciaInicial = inicio.distancia(fin);
		//double distancia = actual.distancia(fin);
		boolean seguir = true;
		while(actual != null && !actual.equals(fin) && seguir){
			Pixel nextContorno = null;
			int dirFin = fin.getDireccion(actual);
			int dirActual = actual.getDireccion(anterior);
			if (dirActual == -1)
				dirActual = dirFin;
			if (Pixel.distanciaLado(dirFin, dirActual) < 3){
				int direccion = (dirActual + dirFin) / 2;
				/*
				List<Pixel> posibles = getNextContornoDireccion(actual, anterior, inicio, direccion, false);
				Pixel posible = null;
				if (posibles != null && posibles.size() > 0)
					posible = fin.getPixelMasCercano(posibles);
				else{
					posible = getAdyacente(actual, dirFin, getImage());
				}
				if (fin.equals(posible) || (posible != null && !isVisitado(posible) && !isFondo(posible, getImagenBordes()))){
					nextContorno = posible;
				}
				else{*/
				Pixel posible = null;
				double mejorDist = Double.MAX_VALUE;
				int[] recorrido = Pixel.getRecorridoHorarioAdayacentes(direccion, 2);
				for(int pos : recorrido){
					posible = getAdyacente(actual, pos, getImage());
					if (fin.equals(posible) || (posible != null && !isVisitado(posible) && !isFondo(posible, getImagenBordes()))){
						double dist = posible.distancia(fin);
						if (dist < mejorDist){
							nextContorno = posible;
							mejorDist = dist;
						}
					}
				}
				/*}*/

				if (nextContorno == null){
					Pixel bordeMascercano = findBordeMasCercano(direccion,actual,fin, getVentanaPixeles());
					if (bordeMascercano != null && !isFondo(bordeMascercano, getImagenBordes())){
						List<Pixel> lineaPixeles = ObjetoUtil.crearLinea(actual, bordeMascercano, getImagenBordes().getWidth(), getImagenBordes().getHeight());
						contorno.addAll(lineaPixeles);
						for(Pixel p:lineaPixeles){
							contorno.add(p);
							visitados.add(p);
							setVisitado(p, true);
						}
						nextContorno = bordeMascercano;
					}
				}
			}
			
			if (nextContorno != null){
				anterior = actual;
				actual = nextContorno;
				if(!nextContorno.equals(fin)){
					actual.setCol(getColorPunto(actual, getOriginalImage()));
					contorno.add(actual);
					visitados.add(actual);
					setVisitado(actual, true);
				}
			}
			else{
				if (contorno.size() < 1)
					seguir = false;
				if (seguir){
					contorno.remove(contorno.size() - 1);
					actual = anterior;
					if (contorno.size() > 1)
						anterior = contorno.get(contorno.size() - 2);
					else
						anterior = inicio;
				}
			}

		}
		
		if (actual != null && actual.equals(fin)){
			int countBorde = 0;
			List<Pixel> camino = new ArrayList<Pixel>();
			for(Pixel p:contorno){
				if (!isFondo(p, getImagenBordes()))
					countBorde++;
				Pixel aux = getBoundingBoxObjetoInicial().getPixelOriginal(p);
				aux.setMaxX(getImage().getWidth());
				aux.setMaxY(getImage().getHeight());
				camino.add(aux);
			}
			Pixel iniDivision = getBoundingBoxObjetoInicial().getPixelOriginal(inicio);
			iniDivision.setMaxX(getImage().getWidth());
			iniDivision.setMaxY(getImage().getHeight());

			Pixel finDivision = getBoundingBoxObjetoInicial().getPixelOriginal(fin);
			finDivision.setMaxX(getImage().getWidth());
			finDivision.setMaxY(getImage().getHeight());

			Division division = new Division(iniDivision, finDivision, camino);

			division.setEvaluacion(countBorde);
			
			return division;
		}

		desmarcarVisitados(visitados);
		return null;
	}
	
	/**
	 * Retorna el pixel adyacente a uno dado en una dirección determinada
	 * 
	 * @param pixel
	 *            Pixel actual
	 * @param direccion
	 *            Dirección para recuperar el adyacente
	 * @return Pixel adyacente
	 */
	public Pixel getAdyacente(Pixel pixel, int direccion, PlanarImage image) {
		Pixel ady = pixel.getAdyacente(direccion, image.getMaxX(), image.getMaxY());
		if (ady != null) {
			return getPixel(ady, image);
		}
		return null;
	}

	
	public Color getColorPunto(Pixel pixel, PlanarImage ti) {
		/**/
		int[] pix = ImageUtil.readPixel(pixel.getX(), pixel.getY(), ti);

		int r = pix[0];
		int g = pix[0];
		int b = pix[0];
		if (pix.length == 3) {
			g = pix[1];
			b = pix[2];
		}
		return new Color(r, g, b);
	}

	private Pixel convertirPixel(Pixel p){
		Pixel pixel = new Pixel((p.getX() % maxMatrixW),(p.getY() % maxMatrixH), p.getCol(), p.getMaxX(), p.getMaxY());
		if ((pixel.getY() >= maxMatrixH) || (pixel.getX() >= maxMatrixW))
			return null;
		if ((pixel.getY() < 0) || (pixel.getX() < 0))
			return null;
		return pixel;
	}

	/**
	 * Setea como visitado un pixel
	 * 
	 * @param pixel
	 */
	private void setVisitado(Pixel p, boolean contorno) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null){
			Matriz[pixel.getX()][pixel.getY()] = 1;
		}
	}

	/**
	 * Devuelve si un pixel es contorno: si tiene un vecino que es fondo
	 * 
	 * @param pixel
	 * @param width
	 * @param nbands
	 * @param pixels
	 * @param umbralFondo
	 * @return
	 */
	public boolean isContorno(Pixel pixel, PlanarImage image) {
		int x = pixel.getX();
		int y = pixel.getY();

		if (isFondo(pixel, image))
			return false;
		Pixel actual = null;

		if (x <= image.getMinX() || y <= image.getMinY() || x >= image.getMaxX() || y >= image.getMaxY()
				){
			pixel.setCol(Color.WHITE);
			return true;
		}

		if (x - 1 >= 0) {

			actual = new Pixel(x - 1, y, null, pixel.getMaxX(), pixel.getMaxY());
			if (isFondo(actual, image) || isVisitado(actual))
				return true;
		}
		if (y - 1 >= 0) {
			actual = new Pixel(x, y - 1, null, pixel.getMaxX(), pixel.getMaxY());
			if (isFondo(actual, image) || isVisitado(actual))
				return true;
		}

		if (y + 1 < image.getHeight()) {
			actual = new Pixel(x, y + 1, null, pixel.getMaxX(), pixel.getMaxY());
			if (isFondo(actual, image) || isVisitado(actual))
				return true;
		}

		if (x + 1 < image.getWidth()) {
			actual = new Pixel(x + 1, y, null, pixel.getMaxX(), pixel.getMaxY());
			if (isFondo(actual, image) || isVisitado(actual))
				return true;
		}

		return false;

	}

	/**
	 * Retorna si un pixel fue visitado
	 * 
	 * @param pixel
	 * @return
	 */
	private boolean isVisitado(Pixel p) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null)
			if (Matriz[pixel.getX()][pixel.getY()] == 1){
				return true;
			}else{ 
				return false;
			}	

		return true;
	}
	
	/**
	 * Desmarca como visitado un pixel
	 * 
	 * @param pixel
	 */
	private void unsetVisitado(Pixel p) {
		Pixel pixel =  convertirPixel(p);
		if (pixel != null){
			Matriz[pixel.getX()][pixel.getY()] = 0;
		}
			
	}

	
	private void desmarcarVisitados(List<Pixel> lista){
		for(Pixel p:lista)
			unsetVisitado(p);
	}


	/**
	 * Limpia los pixels visitados
	 */
	private void initVisitados() {
		this.Matriz  = null;
		this.Matriz = new int[maxMatrixW+1][maxMatrixH+1];
	}

	/**
	 * Retorna si un pixel es fondo de la imagen
	 * 
	 * @param pixel
	 * @return
	 */
	public boolean isFondo(Pixel pixel, PlanarImage image) {
		int umbralFondo = detectarContorno.getColorUmbralFondo().getRed();
		pixel = getPixel(pixel, image);
		if (pixel.getCol() != null)
			return pixel.getCol().getRed() < umbralFondo;
		return false;
	}

	/**
	 * Recupera el pixel (x,y) de la imagen
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param nbands
	 * @param pixels
	 * @return
	 */
	public Pixel getPixel(Pixel p, PlanarImage image) {
		int[] pixel = ImageUtil.readPixel(p.getX(), p.getY(), image);
		if (pixel != null){
			int r = pixel[0];
			int g = pixel[0];
			int b = pixel[0];

			if (pixel.length == 3) {
				g = pixel[1];
				b = pixel[2];
			}
			Color colorPixel = new Color(r, g, b);
			p.setCol(colorPixel);

		}
		return p;
	}

	
	public PlanarImage getOriginalImage() {
		return originalImage;
	}


	public void setOriginalImage(PlanarImage originalImage) {
		this.originalImage = originalImage;
	}


	public PlanarImage getImagenBordes() {
		return imagenBordes;
	}


	public void setImagenBordes(PlanarImage imagenBordes) {
		this.imagenBordes = imagenBordes;
	}


	public BoundingBox getBoundingBoxObjetoInicial() {
		return boundingBoxObjetoInicial;
	}


	public void setBoundingBoxObjetoInicial(BoundingBox boundingBoxObjetoInicial) {
		this.boundingBoxObjetoInicial = boundingBoxObjetoInicial;
	}


	public EvaluadorClase getEvaluadorObjetoCircular() {
		return evaluadorObjetoCircular;
	}


	public void setEvaluadorObjetoCircular(EvaluadorClase evaluadorObjetoCircular) {
		this.evaluadorObjetoCircular = evaluadorObjetoCircular;
	}
	
	private class Division implements Comparable<Division>{
		/**
		 * El porcentaje de los puntos que son borde en la division
		 */
		private Double evaluacion;
		
		Pixel origen;
		
		Pixel fin;
		
		List<Pixel> puntos;

		public Division(Pixel origen, Pixel fin, List<Pixel> puntos) {
			super();
			this.origen = origen;
			this.fin = fin;
			if (puntos != null && puntos.size() > 0){
				List<Pixel> camino = puntos; 
				camino.add(0, origen); 
				camino.add(fin);
				this.puntos = camino;
			}
			
		}

		@Override
		public int compareTo(Division o) {
			return this.evaluacion.compareTo(o.evaluacion) * -1;
		}
		
		public void setEvaluacion(int countBorde) {
			if (this.puntos != null && this.puntos.size() > 0)
				this.evaluacion = ((double)1 / this.puntos.size()) * ((double) countBorde / this.puntos.size());
			else
				this.evaluacion = 0.0;
		}
		
		
		public Double getEvaluacion(){
			return this.evaluacion;
		}

		public boolean equals(Object obj){
			if (obj == null)
				return false;
			if (!(obj instanceof Division))
				return false;
			Division d = (Division) obj;
			if (this.origen.equals(d.origen) && this.fin.equals(d.fin))
				return true;
			if (this.origen.equals(d.fin) && this.fin.equals(d.origen))
				return true;
			return false;
		}
	}

}
