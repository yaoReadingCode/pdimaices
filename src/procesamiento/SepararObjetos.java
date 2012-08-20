package procesamiento;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import objeto.RasgoObjeto;
import objeto.Triangulo;
import procesamiento.clasificacion.AspectRatio;
import procesamiento.clasificacion.Circularidad;
import procesamiento.clasificacion.CoeficientesRecta;
import procesamiento.clasificacion.DivisionObjeto;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.EvaluadorRasgo;
import procesamiento.clasificacion.ObjetoReferencia;
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
	private EvaluadorClase evaluadorObjetoRectangular = null;
	
	public SepararObjetos(PlanarImage image, List<Objeto> objetos, DetectarContorno padre) {
		super(image);
		this.objetos = objetos;
		this.detectarContorno = padre;
		createEvaluadoresObjeto();
	}


	public SepararObjetos(PlanarImage image, List<Objeto> objetos, int ventanaPixeles) {
		super(image);
		this.objetos = objetos;
		this.ventanaPixeles = ventanaPixeles;
		createEvaluadoresObjeto();
	}
	
	/**
	 * Crea y asigna el atributo evaluadorObjetoCircular 
	 */
	private void createEvaluadoresObjeto(){
		RasgoClase rcCircularidad = new RasgoClase();
		rcCircularidad.setRasgo(new Rasgo("Circularidad"));
		RasgoClase rcAspectRadio = new RasgoClase();
		rcAspectRadio.setRasgo(new Rasgo("AspectRadio"));
		
		Circularidad circularidad = new Circularidad(rcCircularidad, 0.7, 1.0);
		//AspectRatio aspectRadio = new AspectRatio(rcAspectRadio, 0.6, 1.0);
		
		List<EvaluadorRasgo> rasgos = new ArrayList<EvaluadorRasgo>();
		rasgos.add(circularidad);
		//rasgos.add(aspectRadio);
		
		Clase claseObjetoCircular = new Clase();
		claseObjetoCircular.setNombre("Objeto circular");
		
		EvaluadorClase objetoCircular = new EvaluadorClase(claseObjetoCircular, rasgos);
		setEvaluadorObjetoCircular(objetoCircular);


		AspectRatio aspectRadio2 = new AspectRatio(rcAspectRadio, 0.4, 1.0);
		
		List<EvaluadorRasgo> rasgos2 = new ArrayList<EvaluadorRasgo>();
		rasgos2.add(aspectRadio2);
		//rasgos.add(aspectRadio);
		
		Clase claseObjetoRecatangular = new Clase();
		claseObjetoCircular.setNombre("Objeto rectangular");
		
		EvaluadorClase objetoRectangular = new EvaluadorClase(claseObjetoRecatangular, rasgos2);
		setEvaluadorObjetoRectangular(objetoRectangular);
		
	}


	public List<Objeto> getObjetos() {
		return objetos;
	}


	public void setObjetos(List<Objeto> objetos) {
		this.objetos = objetos;
	}


	public int getVentanaPixeles() {
		return ventanaPixeles /** getClasificador().getObjetoReferencia().getRelacionPixelCm().intValue()*/;
	}


	public void setVentanaPixeles(int ventanaPixeles) {
		this.ventanaPixeles = ventanaPixeles;
	}
	

	public PlanarImage execute() {
		if (getObjetos() != null){
			List<Objeto> nuevos = new ArrayList<Objeto>();
			for (Objeto obj : getObjetos()) {
				if (necesitaDivision(obj) && !obj.equals(ObjetoReferencia.getReferencia())){
					setImagenBordes(getImagenBordes(obj));
					JAI.create("filestore", getImagenBordes(), "sobel.tif", "TIFF");
					initVisitados();
					List<Pixel> puntosDivisionVisitados = new ArrayList<Pixel>();
					Map<Pixel, Pixel> puntoConflictoOpuestos = new HashMap<Pixel, Pixel>();
					List<Objeto> nuevosObjetos = separarObjetosImagenBordes(obj, puntosDivisionVisitados, 0, puntoConflictoOpuestos);
					nuevos.addAll(nuevosObjetos);
					if (detectarContorno.isBuscarObjetoReferencia()){
						for(Objeto o:nuevosObjetos){
							detectarContorno.isObjSpecial(o);
							
							for(Pixel p:o.getContorno()){
								if (p.getCol() == null || p.getCol().equals(Color.BLACK)){
									Pixel pColor = getPixel(p, getOriginalImage());
									if (pColor != null)
										p.setCol(pColor.getCol());
								}
							}
						}
					}
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
	private void dividirObjeto(Objeto obj, Pixel puntoConflicto1, Pixel puntoConflicto2, List<Pixel> camino, Objeto objeto1, Objeto objeto2, boolean ajustarContorno){
		if (camino.size() > 0){
			List<Pixel> contorno = obj.getContorno(); 
			int indexP1 = contorno.indexOf(puntoConflicto1);
			int indexP2 = contorno.indexOf(puntoConflicto2);
			Objeto objDivision = objeto2;
			Objeto objResto = objeto1;
			
			if (indexP1 > indexP2){
				int aux = indexP1;
				indexP1 = indexP2;
				indexP2 = aux;
				Pixel auxP = puntoConflicto1.clonar();
				puntoConflicto1 = puntoConflicto2;
				puntoConflicto2 = auxP;
				objDivision = objeto1;
				objResto = objeto2;
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
				List<Pixel> caminoInverso = new ArrayList<Pixel>(camino);
				Collections.reverse(caminoInverso);
				List<Pixel> camino1 = null;
				List<Pixel> camino2 = null;
				if (puntoConflicto1.equals(primeroCamino)){
					camino1 = camino;
					camino2 = caminoInverso;
				}
				else{
					camino1 = caminoInverso;
					camino2 = camino;
				}
				//construimos el contorno 1
				contorno1.addAll(list1);
				contorno1.addAll(camino1);
				contorno1.addAll(list3);
				
				//contruimos el contorno 2
				contorno2.addAll(list2);
				contorno2.addAll(camino2);
				
				if (ajustarContorno){
					int indexPInicio1 = contorno1.indexOf(puntoConflicto1) - getVentanaPixeles();
					if (indexPInicio1 < 0)
						indexPInicio1 = contorno1.size() + indexPInicio1;
					int indexPFin1 = (contorno1.indexOf(puntoConflicto2) + getVentanaPixeles()) % contorno1.size();
					List<Pixel> nuevoContorno1 = ajustarContorno(contorno1, indexPInicio1, indexPFin1,puntoConflicto1, puntoConflicto2);
					
					int indexPInicio2 = contorno2.indexOf(puntoConflicto2) - getVentanaPixeles();
					if (indexPInicio2 < 0)
						indexPInicio2 = contorno2.size() + indexPInicio2;
					int indexPFin2 = getVentanaPixeles() % contorno2.size();
					List<Pixel> nuevoContorno2 = ajustarContorno(contorno2, indexPInicio2, indexPFin2,puntoConflicto1, puntoConflicto2);
					contorno1 = nuevoContorno1;
					contorno2 = nuevoContorno2;
				}
				
				objResto.setContorno(contorno1);
				objDivision.setContorno(contorno2);
			}			
		}
	}
	
	
	/**
	 * Recorre el contorno y retorna los puntos en los que la direccion del contorno cambia significativamente.
	 * Con esos puntos se evalua si se puede dividir el objeto.  
	 * @param contorno
	 * @return
	 */
	private List<Pixel> obtenerPuntosDeConflicto(Objeto obj){
		if (obj.getPuntosDivisionContorno() == null){
			List<Pixel> puntosConflicto = new ArrayList<Pixel>();
			List<Pixel> contorno = obj.getContorno();
			int posPuntoConflicto = 0;
			int posPixelInicial = 2 * getVentanaPixeles();
			int posPixelFinal = posPixelInicial + contorno.size();
			boolean parar = false;
			int i = posPixelInicial;
			double anguloMejor = 0;
			Pixel puntoConflicto = null;
			int countPixelesDesvio = 0;
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
				double lado = Pixel.lado2(inicio, medio, fin);
				if (lado < 0){
					double angulo = ObjetoUtil.calcularAngulo(inicio, medio, fin);
					if (angulo > anguloDesvio && (puntoConflicto == null || angulo > anguloMejor)){
						puntoConflicto = medio;
						anguloMejor = angulo;
						posPuntoConflicto = i;
					}
					countPixelesDesvio++;
					if(puntoConflicto != null && countPixelesDesvio > getVentanaPixeles() && !puntosConflicto.contains(puntoConflicto)){
						puntosConflicto.add(puntoConflicto);
						puntoConflicto = null;
						countPixelesDesvio = 0;
						i = posPuntoConflicto + getVentanaPixeles();
					}
				}
				else{
					if (puntoConflicto != null && countPixelesDesvio > getVentanaPixeles()){
						puntoConflicto = null;
						countPixelesDesvio = 0;
					}
				}
				i = i + 1;
				if (i >= posPixelFinal)
					parar = true;
			}
			/*
			if (puntosConflicto.size() == 1){
				Pixel opuestoMasCercano = findPuntoOpuestoMasCercano(puntosConflicto.get(0), obj.getContorno());
				if (opuestoMasCercano != null){
					puntosConflicto.add(opuestoMasCercano);
				}
			}
			*/
			obj.setPuntosDivisionContorno(puntosConflicto);
		}
		return obj.getPuntosDivisionContorno();
	}

	
	private Pixel findPuntoOpuestoMasCercano(Pixel pixel, List<Pixel> contorno) {
		
		int posPixelInicial = contorno.indexOf(pixel);
		int posPixelFinal = posPixelInicial + contorno.size();
		boolean parar = false;
		int i = posPixelInicial + getVentanaPixeles();
		int ventana = getVentanaPixeles() / 2;
		int posIniA = posPixelInicial - ventana;
		if (posIniA < 0)
			posIniA = contorno.size() + posIniA;
		Pixel pixelMasCercano = null;
		Pixel inicioR1 = contorno.get(posIniA);
		Pixel finR1 = pixel;
		Pixel dirB = finR1.clonar();
		dirB.restar(inicioR1);

		double prodEscalarMenor = Double.MAX_VALUE;
		Pixel anteriorDH = getAnterior(pixel, contorno);
		while (!parar && contorno.size() > 2 * ventana){
			
			Pixel inicioR2 = contorno.get(i % contorno.size());
			Pixel finR2 = contorno.get((i + ventana) % contorno.size());
			Pixel dirA = finR2.clonar();
			dirA.restar(inicioR2);
			double productoEscalar = Math.abs(dirA.productoEscalar(dirB));
			
			BoundingBox bbR2 = new BoundingBox(inicioR2,finR2);
			Pixel interseccion = ObjetoUtil.calcularPuntoInterseccion(inicioR1, finR1, inicioR2, finR2);
			if (interseccion != null){
				int posInterseccion = contorno.indexOf(interseccion);
				double lado = Pixel.lado(anteriorDH, pixel, finR2);
				if (lado >= 0 && Math.abs(posPixelInicial - posInterseccion)> getVentanaPixeles() && bbR2.isPertenece(interseccion) && productoEscalar <= 10 && prodEscalarMenor > productoEscalar){
					prodEscalarMenor = productoEscalar;
					pixelMasCercano = finR2;
				}
			}
			i = i + 1;
			if (i >= posPixelFinal)
				parar = true;
		}
		return pixelMasCercano;
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
		int indexAnterior = index - getVentanaPixeles() / 2;
		if (indexAnterior < 0)
			indexAnterior = contorno.size() + indexAnterior;
		Pixel anterior = contorno.get(indexAnterior);
		return anterior;
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
	private List<Pixel> getPosiblesDesviosAntihorarios(Objeto obj, Pixel desvioHorario, List<Pixel> puntosDivision, List<Pixel> puntosDivisionVisitados, List<Pixel> contornoObjeto){
		List<Pixel> desviosAntihorarios = new ArrayList<Pixel>();
		Pixel anteriorDH = getAnterior(desvioHorario, contornoObjeto);
		int direccionDH = desvioHorario.getDireccion(anteriorDH);
		for(Pixel p:puntosDivision){
			if (!desvioHorario.equals(p) && !puntosDivisionVisitados.contains(p)){
				
				Pixel anteriorDAH =  getAnterior(p, contornoObjeto);
				double lado = Pixel.lado(anteriorDH, desvioHorario, p);
				double lado2 = Pixel.lado(anteriorDAH, p, desvioHorario);
				int direccionDAH = p.getDireccion(anteriorDAH);
				int distanciaDireccion = Pixel.distanciaLado(direccionDH, direccionDAH);
				double distancia = desvioHorario.distancia(p);
				if (/*distancia <= 2 * getVentanaPixeles() || */((lado > -10 || lado2 > -10) && distanciaDireccion >= 2 && !intersectaAlgunPuntoRecta(desvioHorario, p, contornoObjeto))){
					List<Pixel> camino = ObjetoUtil.crearLinea(desvioHorario, p, getImage().getWidth(), getImage().getHeight());
					if (camino != null && camino.size() > 0){
						Pixel primero = camino.get(0);
						if (desvioHorario.isAdyacente(primero)){
							camino.add(0, desvioHorario); 
							camino.add(p);
						}
						else{
							camino.add(0, p); 
							camino.add(desvioHorario);
						}
					}
					Objeto obj1 = new Objeto();
					obj1.setOriginalImage(getOriginalImage());
					Objeto obj2 = new Objeto();
					obj2.setOriginalImage(getOriginalImage());

					dividirObjeto(obj, desvioHorario, p, camino, obj1, obj2, false);
					//obj1.calcularMRC();
					obj2.calcularMRC();

					if (getEvaluadorObjetoRectangular().pertenece(obj2, false) /*&&
							getEvaluadorObjetoRectangular().pertenece(obj2, false)*/){
						double peso = distancia;
						p.setPeso(peso);
						desviosAntihorarios.add(p);
					}

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
	private List<Objeto> separarObjetosImagenBordes(Objeto obj, List<Pixel> puntosDivisionVisitados, int nivel, Map<Pixel, Pixel> puntoConflictoOpuestos) {
		List<Pixel> puntosDivision = obtenerPuntosDeConflicto(obj);
		puntosDivision.remove(puntosDivisionVisitados);
		agregarPuntosConflictoOpuestos(obj, puntosDivision, puntosDivisionVisitados, puntoConflictoOpuestos);
		List<Objeto> objetosResult = new ArrayList<Objeto>();
		if (puntosDivision.size() > 1){
			initVisitados();
			List<Division> divisionesPosibles = new ArrayList<Division>();
			List<DivisionObjeto> divisiones = new ArrayList<DivisionObjeto>();
			for(int i = 0; i < puntosDivision.size() ; i++){
				Pixel desvioHorario = puntosDivision.get(i);
				Pixel desvioAntihorario = puntoConflictoOpuestos.get(desvioHorario);
				if (desvioAntihorario != null){
					Division division = new Division(desvioHorario, desvioAntihorario, null);
					if (!divisionesPosibles.contains(division)){
						Pixel desvioHorarioAux = getBoundingBoxObjetoInicial().getPixelRelativo(desvioHorario);
						Pixel desvioAntihorarioAux = getBoundingBoxObjetoInicial().getPixelRelativo(desvioAntihorario);
						//Marco el par de puntos de division procesado 

						Division mejorDivision = null;
						Division caminoRelativo1 = findCamino(desvioAntihorarioAux , desvioHorarioAux);
						Division caminoRelativo2 = findCamino(desvioHorarioAux , desvioAntihorarioAux);
						if (caminoRelativo1 == null && caminoRelativo2 == null){
							List<Pixel> camino = ObjetoUtil.crearLinea(desvioHorario, desvioAntihorario, getImage().getWidth(), getImage().getHeight());
							if (camino != null){
								mejorDivision = new Division(desvioHorario, desvioAntihorario, camino);
								mejorDivision.setEvaluacion(0);
								if (mejorDivision.getEvaluacion() < 0.01)
									mejorDivision = null;
							}
						}
						else if (caminoRelativo1 != null && caminoRelativo2 != null){
							mejorDivision = caminoRelativo1;
							if (caminoRelativo1.evaluacion < caminoRelativo2.evaluacion){
								mejorDivision = caminoRelativo2;
							}
						}
						else {
							if (caminoRelativo1 != null)
								mejorDivision = caminoRelativo1;
							else
								mejorDivision = caminoRelativo2;
						}
						if (mejorDivision != null && mejorDivision.puntos != null){
							Objeto obj1 = new Objeto(obj);
							obj1.setOriginalImage(getOriginalImage());
							Objeto obj2 = new Objeto(obj);
							obj2.setOriginalImage(getOriginalImage());

							dividirObjeto(obj, mejorDivision.origen, mejorDivision.fin, mejorDivision.puntos, obj1, obj2, true);
							if (obj1.validarContorno() && obj2.validarContorno() &&
								(!obj1.getBoundingBox().isIncluido(obj2.getBoundingBox()) ||
								!obj2.getBoundingBox().isIncluido(obj1.getBoundingBox()))){
								List<RasgoObjeto> rasgosOb1 = new ArrayList<RasgoObjeto>();
								List<RasgoObjeto> rasgosOb2 = new ArrayList<RasgoObjeto>();
								getEvaluadorObjetoRectangular().pertenece(obj1, false);
								boolean isCircularObj1 = getEvaluadorObjetoCircular().pertenece(obj1, false, rasgosOb1);
								boolean isCircularObj2 = getEvaluadorObjetoCircular().pertenece(obj2, false, rasgosOb2);
								if (isCircularObj1 || isCircularObj2){
									Double circularidad1 = rasgosOb1.get(0).getValor();
									Double circularidad2 = rasgosOb2.get(0).getValor();
									Double valoracion = /*mejorDivision.getEvaluacion();*/circularidad1 * circularidad2 * mejorDivision.getEvaluacion();
									DivisionObjeto div = new DivisionObjeto(valoracion,obj1, obj2);
									div.setOrigen(mejorDivision.origen);
									div.setFin(mejorDivision.fin);
									div.setCircularObjeto1(isCircularObj1);
									div.setCircularObjeto2(isCircularObj2);
									divisiones.add(div);
									divisionesPosibles.add(mejorDivision);
								}
							}
						}
					}
				}
				
			}
			
			if (divisiones.size() > 0){
				Collections.sort(divisiones);
				boolean huboDivision = false;
				for(int i = 0; i < divisiones.size() && !huboDivision; i++){
					
					DivisionObjeto mejorDivision = divisiones.get(i);
					Objeto obj1 = mejorDivision.getObjeto1();
					Objeto obj2 = mejorDivision.getObjeto2();

					obj1.setName(obj.getName());
					int cantObjetos = getClasificador().getCantidadObjetos() + 1;
					getClasificador().aumentarCantidadObjetos();
					obj2.setName(DetectarContorno.NOMBRE_DEFAULT+cantObjetos);
					
					if (isNecesarioAjustarContorno(obj1, mejorDivision))
						ajustarContornoObjeto(obj1);
					if (isNecesarioAjustarContorno(obj2, mejorDivision))
						ajustarContornoObjeto(obj2);
					
					List<Objeto> nuevos = new ArrayList<Objeto>();

					puntosDivisionVisitados.add(mejorDivision.getOrigen());
					puntosDivisionVisitados.add(mejorDivision.getFin());
					List<Objeto> nuevosObjetos = new ArrayList<Objeto>();
					if (!necesitaDivision(obj1) && mejorDivision.isCircularObjeto1() && obj1.getPuntos().size() > 0){
						completarObjeto(obj1);
						nuevos.add(obj1);
					}
					else{
						nuevosObjetos = separarObjetosImagenBordes(obj1, puntosDivisionVisitados, nivel + 1, puntoConflictoOpuestos);
						nuevos.addAll(nuevosObjetos);
					}
					if (!necesitaDivision(obj2) && mejorDivision.isCircularObjeto2() && obj2.getPuntos().size() > 0){
						completarObjeto(obj2);
						nuevos.add(obj2);
					}
					else{
						nuevosObjetos = separarObjetosImagenBordes(obj2, puntosDivisionVisitados, nivel + 1, puntoConflictoOpuestos);
						nuevos.addAll(nuevosObjetos);
					}
					objetosResult.addAll(nuevos);
					String info = "Objeto catalogado: "
						+ obj2.getName()
						+ " - Puntos detectados: "
						+ obj2.getPuntos().size();

					Visualizador.addLogInfo(info);
					huboDivision = true;
				}
			}
		}
		if (objetosResult.size() == 0){
			if (nivel > 0 && obj.getPuntos().size() == 0){
				completarObjeto(obj);
			}
			if (obj.getPuntos().size() > 0){
				objetosResult.add(obj);
			}
		}
		return objetosResult;
	}
	
	/**
	 * Calculos los puntos de conflicto opuestos de cada puntos de division si ya no fue calculado antes
	 * @param puntosDivision Nuevos posibles puntos de division del objeto 
	 * @param puntosDivisionVisitados Puntos de division por los cuales ya se realizo una division
	 * @param puntoConflictoOpuestos Mapa que contiene para cada punto de division del objeto el punto de conflicto opuesto
	 */
	private void agregarPuntosConflictoOpuestos(Objeto obj, List<Pixel> puntosDivision, List<Pixel> puntosDivisionVisitados, Map<Pixel, Pixel> puntoConflictoOpuestos) {
		for (Pixel p:puntosDivision){
			if (puntoConflictoOpuestos.containsKey(p)){
				Pixel opuesto = puntoConflictoOpuestos.get(p);
				if (puntosDivisionVisitados.contains(opuesto)){
					Pixel nuevoOpuesto = null;
					List<Pixel> opuestos = getPosiblesDesviosAntihorarios(obj, p, puntosDivision, puntosDivisionVisitados, obj.getContorno());
					if (opuestos.size() > 0)
						nuevoOpuesto = opuestos.get(0);
					puntoConflictoOpuestos.put(p, nuevoOpuesto);
				}
			}
			else{
				Pixel nuevoOpuesto = null;
				List<Pixel> opuestos = getPosiblesDesviosAntihorarios(obj, p, puntosDivision, puntosDivisionVisitados, obj.getContorno());
				if (opuestos.size() > 0)
					nuevoOpuesto = opuestos.get(0);
				puntoConflictoOpuestos.put(p, nuevoOpuesto);
			}
		}
		
	}


	/**
	 * Evalua si un objeto necesita ser dividido
	 * @param obj1
	 * @return
	 */
	private boolean necesitaDivision(Objeto obj1) {
		List<Pixel> puntosDivision = obtenerPuntosDeConflicto(obj1);
		if (puntosDivision.size() > 1){
			return true;
		}
		return false;
	}


	private void completarObjeto(Objeto obj){
		detectarContorno.limpiarVisitados();
		detectarContorno.completarObjeto(obj);
		obj.calcularMRC();
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
				int dirActual = direccion;
				if (direccion == Pixel.DIR_NE || direccion == Pixel.DIR_NO || direccion == Pixel.DIR_SE || direccion == Pixel.DIR_SO){
					if (i % 2 == 0)
						dirActual = dir;
				}
				actual = getAdyacente(actual, dirActual, getImage());
				if (actual != null && fin != null){
					if (isFondo(actual, getImagenBordes())){
						List<Pixel> posibles = getNextContornoDireccion(actual, anterior, inicio, dirActual, true);
						if (posibles != null && posibles.size() > 0){
							puntosContorno.add(posibles.get(0));
							actual = posibles.get(0);
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
			if (Pixel.distanciaLado(dirFin, dirActual) < 2){
				int direccion = dirFin;/*(dirActual + dirFin) / 2;*/
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
				if (nextContorno == null){
					Pixel bordeMascercano = findBordeMasCercano(dirFin,actual,fin, getVentanaPixeles());
					if (bordeMascercano != null && !isFondo(bordeMascercano, getImagenBordes())){
						List<Pixel> lineaPixeles = ObjetoUtil.crearLinea(actual, bordeMascercano, getImagenBordes().getWidth(), getImagenBordes().getHeight());
						if (lineaPixeles.size() > 0){
							Pixel primero = lineaPixeles.get(0);
							if (!actual.isAdyacente(primero))
								Collections.reverse(lineaPixeles);
							contorno.addAll(lineaPixeles);
							for(Pixel p:lineaPixeles){
								visitados.add(p);
								setVisitado(p, true);
							}
							actual = lineaPixeles.get(lineaPixeles.size() - 1);

						}
						nextContorno = bordeMascercano;
					}
				}
			}
			
			if (nextContorno != null && !contorno.contains(nextContorno)){
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
			
			//List<Pixel> caminoAjustado = ajustarCamino(camino);
			Division division = new Division(iniDivision, finDivision, camino);

			division.setEvaluacion(countBorde);
			
			desmarcarVisitados(visitados);
			
			return division;
		}

		desmarcarVisitados(visitados);
		return null;
	}
	
	/**
	 * Ajusta el camino de pixeles encontrado entre dos puntos de forma que no haya puntos a la derecha en el contorno.
	 * Se lo mas circular posible.
	 * @param mejorDivision 
	 * @param camino
	 * @return
	 */
	private List<Pixel> ajustarContorno(List<Pixel> contorno, int indexP1, int indexP2, Pixel puntoConflicto1 ,Pixel puntoConflicto2) {
		if (contorno.size() > 2 * getVentanaPixeles()){
			int ventana = getVentanaPixeles() / 2;
			List<Pixel> result = new ArrayList<Pixel>();
			List<Pixel> resto = new ArrayList<Pixel>();
			
			int posPixelInicial = indexP1;
			int posPixelFinal = indexP2;
			int i = posPixelInicial;
			
			int cantPixelesRecorridos = 0;
			int cantPixelesARecorrer = 0;
			if (posPixelInicial < posPixelFinal % contorno.size()){
				cantPixelesARecorrer = posPixelFinal - posPixelInicial + 1;
				result.addAll(contorno.subList(0, posPixelInicial));
				resto.addAll(contorno.subList(posPixelFinal % contorno.size(), contorno.size()));				
			}
			else{
				cantPixelesARecorrer = contorno.size() - posPixelInicial + posPixelFinal % contorno.size() + 2;
				result.addAll(contorno.subList(posPixelFinal % contorno.size() , posPixelInicial));
			}
			boolean parar = false;
			while (cantPixelesRecorridos < cantPixelesARecorrer && !parar){
				int posInicio = i;
				int posFin = i + 2 * ventana;
				if (cantPixelesRecorridos + 2 * ventana > cantPixelesARecorrer)
					posFin = posPixelFinal;
				int posMedio = (posInicio + posFin)/ 2;

				Pixel inicio = contorno.get(posInicio % contorno.size());
				Pixel medio = contorno.get(posMedio % contorno.size());
				Pixel fin = contorno.get(posFin % contorno.size());
				
				double distancia = ObjetoUtil.distanciaPuntoARecta(medio, inicio, fin);
				//double lado = Pixel.lado(inicio, fin, medio);
				int salto = 0; 
				if (distancia > ventana / 4 /*lado < 0*/){
					List<Pixel> linea = ObjetoUtil.crearLinea(inicio, fin, getImage().getWidth(), getImage().getHeight());
					if (linea.size() > 0){
						result.add(inicio);
						if (inicio.isAdyacente(linea.get(0))){
							result.addAll(linea);
						}
						else{
							for(int index = linea.size() - 1; index > -1; index-- )
								result.add(linea.get(index));
						}
						result.add(fin);
						i = posFin;
						salto = Math.abs(posFin - posInicio);
					}
				}
				else{
					result.add(inicio);
				}
				
				i = i + 1;
				cantPixelesRecorridos = cantPixelesRecorridos + salto + 1;
			}
			
			result.addAll(resto);
			if (result.get(0).equals(result.get(result.size() - 1)))
				result.remove(result.size() - 1);
			Pixel inicio = result.get(0);
			Pixel fin = result.get(result.size() - 1);
			if (!(inicio.isAdyacente(fin) || inicio.equals(fin))){
				List<Pixel> linea = ObjetoUtil.crearLinea(inicio, fin, getImage().getWidth(), getImage().getHeight());
				if (linea != null){
					if (inicio.isAdyacente(linea.get(0))){
						Collections.reverse(linea);
					}
					for(Pixel p:linea)
						result.add(p);
				}
			}
				
			return result;
		}
		return contorno;
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
	
	public EvaluadorClase getEvaluadorObjetoRectangular() {
		return evaluadorObjetoRectangular;
	}

	public void setEvaluadorObjetoRectangular(EvaluadorClase evaluadorObjetoRectangular) {
		this.evaluadorObjetoRectangular = evaluadorObjetoRectangular;
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
				Pixel primero = puntos.get(0);
				if (origen.isAdyacente(primero)){
					camino.add(0, origen); 
					camino.add(fin);
				}
				else{
					camino.add(0, fin); 
					camino.add(origen);
				}
				this.puntos = camino;
			}
			
		}

		@Override
		public int compareTo(Division o) {
			return this.evaluacion.compareTo(o.evaluacion) * -1;
		}
		
		public void setEvaluacion(int countBorde) {
			if (this.puntos != null && this.puntos.size() > 0)
				this.evaluacion = ((double) 1 / this.puntos.size()) * 0.7 + ((double) (countBorde) / (this.puntos.size())) * 0.3;
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
	
	/**
	 * Ajusta el contorno del objeto borrando de la imagen el objeto resultante de la division y aplicando
	 * deteccion de bordes a la imagen resultante.
	 * @param objeto Objeto a ajustar el contorno 
	 * @param objetoAEliminar Objeto a eliminar de la imagen
	 */
	private void ajustarContornoObjeto(Objeto objeto){
		Color fondo = detectarContorno.getRangeFondo().getColorMedio();
		
		BoundingBox bb = objeto.getBoundingBox();
		Rectangle rectangle = new Rectangle((int)bb.getMinX(), (int)bb.getMinY(), (int)(bb.getMaxX() - bb.getMinX()), (int)(bb.getMaxY() - bb.getMinY()));
		BufferedImage image = getOriginalImage().getAsBufferedImage(rectangle,null);
		PlanarImage pi = TiledImage.wrapRenderedImage(image);
		TiledImage ti = ImageUtil.createTiledImage(pi, ImageUtil.tileWidth, ImageUtil.tileHeight);
		ImageUtil.inicializarImagen(ti, fondo);
		
		marcarObjeto(ti, objeto, fondo);
		//JAI.create("filestore", ti, objeto.getName() + "_aux.tif", "TIFF");
		
		Binarizar ef = new Binarizar(ti, detectarContorno.getRangeFondo());
		PlanarImage binaryImage = ef.execute();
		PlanarImage output = binaryImage;
		
		//JAI.create("filestore", output, objeto.getName() + "binary_aux.tif", "TIFF");
		
		Opening op = new Opening(output);
		output = op.execute();
		//JAI.create("filestore", output, objeto.getName() + "opening_aux.tif", "TIFF");
		
		DetectarContornoGrueso dcg = new DetectarContornoGrueso(output);
		output = dcg.execute();
		
		//JAI.create("filestore", output, objeto.getName() + "contornogrueso_aux.tif", "TIFF");
		
		DetectarContorno dc = new DetectarContorno(output, ti, new Color(100, 100, 100), Color.RED);
		dc.setBinaryImage(binaryImage);
		dc.setClasificador(getClasificador());
		dc.setRangeFondo(detectarContorno.getRangeFondo());
		dc.setSepararObjetos(false);
		dc.setVisualizarInfoLog(false);
		dc.setAsignarNombreObjeto(false);
		dc.setBuscarObjetoReferencia(false);
		output = dc.execute();
		
		List<Objeto> objetos = dc.getObjetos();
		if (objetos != null && objetos.size() > 0){
			objeto.setContorno(bb.getPixelsOriginales(objetos.get(0).getContorno(),getImage().getWidth(), getImage().getHeight()));
			objeto.setPuntos(bb.getPixelsOriginales(objetos.get(0).getPuntos(),getImage().getWidth(), getImage().getHeight()));
		}

	}

	/**
	 * Marca el objeto en la imagen pasada como parametro. Lo que no pertenece al objeto se le asigna el
	 * color de fondo
	 * @param ti TiledImage image
	 * @param objeto Objeto
	 * @param fondo Color de fondo
	 */
	private void marcarObjeto(TiledImage ti, Objeto objeto, Color fondo) {
		BoundingBox bb = objeto.getBoundingBox();
		/*
		int[] newPixel = { fondo.getRed(), fondo.getGreen(), fondo.getBlue()};
		for(int x = (int) bb.getMinX(); x <= bb.getMaxX(); x++)
			for(int y = (int) bb.getMinY(); y <= bb.getMaxY(); y++){
				Pixel p = new Pixel(x, y, fondo, getImage().getWidth(), getImage().getHeight());
				if (!objeto.isPertenece(p)){
					Pixel pixel = bb.getPixelRelativo(p);
					ImageUtil.writePixel(pixel.getX(), pixel.getY(), newPixel,ti);
				}
			}
		*/
		for(Pixel pixel: objeto.getContorno()){
			Pixel p = bb.getPixelRelativo(pixel);
			ImageUtil.writePixel(p,ti);
		}
		for(Pixel pixel: objeto.getPuntos()){
			Pixel p = bb.getPixelRelativo(pixel);
			ImageUtil.writePixel(p,ti);
		}
		
	}
	
	/**
	 * Determina si es necesario ajustar el contorno del objeto despues de haber realizado una division
	 * @param obj1 Objeto
	 * @param division Puntos de division del contorno
	 * @return true si la cantidad de pixeles de color de fondo cercanos a la linea de division es mayor que un valor umbral.
	 */
	private boolean isNecesarioAjustarContorno(Objeto obj1, DivisionObjeto division){
		List<Pixel> puntosDivision = obtenerPuntosDeConflicto(obj1);
		if (puntosDivision.size() > 0){
			Pixel pMedioDivision = division.getOrigen().clonar();
			pMedioDivision.sumar(division.getFin());
			pMedioDivision.setX(pMedioDivision.getX() / 2);
			pMedioDivision.setY(pMedioDivision.getY() / 2);
			
			Pixel origen = division.getOrigen();
			Pixel fin = division.getFin();
			if (Pixel.lado(origen, fin, obj1.getPixelMedio()) < 0){
				origen = division.getFin();
				fin = division.getOrigen();
			}
			
			Double pendiente = ObjetoUtil.calcularPendienteRecta(origen, fin);
			Double pendientePerpendular = null;
			if (pendiente == null){
				pendientePerpendular = 0.0;
			}
			else if (pendiente != 0){
				pendientePerpendular = -1.0 / pendiente;
			}
			Pixel puntoMedio = obj1.getPixelMedio();
			CoeficientesRecta rectaPerpendicular = new CoeficientesRecta();
			ObjetoUtil.coeficientesRecta(pendientePerpendular, pMedioDivision, rectaPerpendicular);
			Pixel medioObjetoRecta = rectaPerpendicular.proyectarPunto(puntoMedio);
			
			Pixel dirMedio = medioObjetoRecta.clonar();
			dirMedio.restar(pMedioDivision);
			dirMedio.normalizar();
			double distanciaPuntos = Math.min(origen.distancia(fin), getVentanaPixeles()*2);
			dirMedio.escalar((double)distanciaPuntos);
			
			Pixel medio = pMedioDivision.clonar();
			medio.sumar(dirMedio);
			
			Triangulo t = new Triangulo(origen, fin, medio);
			int cantidadFondo = 0;
			int total = 0;
			BoundingBox bb = new BoundingBox(t);
			Rectangle rectangle = new Rectangle((int)bb.getMinX(), (int)bb.getMinY(), (int)(bb.getMaxX() - bb.getMinX()), (int)(bb.getMaxY() - bb.getMinY()));
			if (rectangle.getWidth() > 0 && rectangle.getHeight() > 0){
				BufferedImage image = getOriginalImage().getAsBufferedImage(rectangle,null);
				PlanarImage pi = TiledImage.wrapRenderedImage(image);
				for(int x = (int) bb.getMinX(); x <= bb.getMaxX(); x++)
					for(int y = (int) bb.getMinY(); y <= bb.getMaxY(); y++){
						Pixel p = new Pixel(x, y, null, getImage().getWidth(), getImage().getHeight());
						if (obj1.isPertenece(p)){
							Pixel pixel = bb.getPixelRelativo(p);
							Color color = ImageUtil.getColorPunto(pixel, pi);
							if (color != null){
								if (detectarContorno.getRangeFondo().isEnRango(color))
									cantidadFondo++;
							}
							total++;
						}
					}
				double porcentajeFondo = 0.0;
				if (total > getVentanaPixeles()*2)
					porcentajeFondo = (double)cantidadFondo / (double)total;
				if (porcentajeFondo > 0.20)
					return true;
			}
		}
		return false;
	}
}
