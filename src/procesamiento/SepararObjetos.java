package procesamiento;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.PlanarImage;

import aplicarFiltros.Visualizador;

import objeto.Clase;
import objeto.Objeto;
import objeto.Pixel;
import objeto.Rasgo;
import objeto.RasgoClase;
import procesamiento.clasificacion.AspectRatio;
import procesamiento.clasificacion.Circularidad;
import procesamiento.clasificacion.Clasificador;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.EvaluadorRasgo;
import procesamiento.clasificacion.ObjetoReferencia;

public class SepararObjetos extends AbstractImageCommand {

	/**
	 * Cantidad de pixeles del contorno a utilizar para ver si un pixel se desvía 
	 * demasiado del contorno. Lo que indicaría que pertenece a otro objeto
	 */
	//private int ventanaPixeles = 20;
	//private static int anguloDesvio = 70;
	private int ventanaPixeles = 10;
	private static int anguloDesvio = 35;
	
	/**
	 * Porcentaje de la longitud del contorno de objeto con el cuál se define el tamaño del segmento
	 */
	private int porcTamanioSegmento = 1;

	
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
		if (getObjetos() != null){
			RasgoClase rcCircularidad = new RasgoClase();
			rcCircularidad.setRasgo(new Rasgo("Circularidad"));
			
			RasgoClase rcAspectRadio = new RasgoClase();
			rcAspectRadio.setRasgo(new Rasgo("AspectRadio"));
			
			Circularidad circularidad = new Circularidad(rcCircularidad, 1.0, 0.2);
			AspectRatio aspectRadio = new AspectRatio(rcAspectRadio, 1.0, 0.4);
			//Area area = new Area("Area", 3000.0,2000.0);

			List<EvaluadorRasgo> rasgos = new ArrayList<EvaluadorRasgo>();
			rasgos.add(circularidad);
			rasgos.add(aspectRadio);
			
			Clase claseObjetoCircular = new Clase();
			claseObjetoCircular.setNombre("Objeto circular");
			//rasgos.add(area);
			EvaluadorClase objetoCircular = new EvaluadorClase(claseObjetoCircular, rasgos);
			List<Objeto> nuevos = new ArrayList<Objeto>();

			for (Objeto obj : getObjetos()) {
				if (!objetoCircular.pertenece(obj,false)){
					//System.out.println("Separar objeto: " + obj.getPixelMedio());
					List<Objeto> nuevosObjetos = separarObjetos(obj, objetoCircular);
					
					nuevos.addAll(nuevosObjetos);
				}
				else 
					nuevos.add(obj);
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
	private void dividirContorno(List<Pixel> contorno, Pixel puntoConflicto1, Pixel puntoConflicto2, List<Pixel> contorno1, List<Pixel> contorno2){
		
		int indexP1 = contorno.indexOf(puntoConflicto1);
		int indexP2 = contorno.indexOf(puntoConflicto2);
		
		if (indexP1 > indexP2){
			int aux = indexP1;
			indexP1 = indexP2;
			indexP2 = aux;
		}
		
		contorno1.clear();
		contorno2.clear();
		
		if (indexP1 != -1 && indexP2 != -1){
			List<Pixel> list1 = new ArrayList<Pixel>(contorno.subList(0, indexP1 + 1));
			List<Pixel> list2 = new ArrayList<Pixel>(contorno.subList(indexP1, indexP2 + 1));
			List<Pixel> list3 = new ArrayList<Pixel>(contorno.subList(indexP2, contorno.size()));
			
			//construimos el contorno 1
			contorno1.addAll(list1);

			List<Pixel> lineaPixeles = crearLinea(puntoConflicto1, puntoConflicto2);
			//Linea de pixeles ordenada
			List<Pixel> lineaPixelesOrd = new ArrayList<Pixel>();
					
			lineaPixeles.remove(puntoConflicto1);
			Pixel proximo = puntoConflicto1;
			while(lineaPixeles.size() > 0){
				 proximo = proximo.getPixelMasCercano(lineaPixeles);
				 if (!proximo.equals(puntoConflicto2))
					 lineaPixelesOrd.add(proximo);
				 lineaPixeles.remove(proximo);
				 
			}
			
			contorno1.addAll(lineaPixelesOrd);
			contorno1.addAll(list3);
			
			//contruimos el contorno 2
			contorno2.addAll(list2);
			for(int j = lineaPixelesOrd.size() -1; j >= 0 ; j--)
				contorno2.add(lineaPixelesOrd.get(j));

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
		List<Integer> posPuntosConflicto = new ArrayList<Integer>();
		List<Pixel> puntosConflicto = new ArrayList<Pixel>();
		int tamanioSegmento = getVentanaPixeles();
		int posIniVentana = 0;
		int posFinVentana = tamanioSegmento;
		int posFinVentana2 = tamanioSegmento * 2;
		Pixel iniVentana = contorno.get(posIniVentana);
		Pixel finVentana = contorno.get(posFinVentana);
		Pixel finVentana2 = null;		
		//Para la ecuacion de la recta
		
		boolean parar = false;
		int i = posFinVentana;
		Integer posPuntoConflicto = i;
		int inicio = i;
		int cantPixelsIzquierda = 0;
		while (!parar && contorno.size() > tamanioSegmento){
			
			Pixel p = contorno.get(i % contorno.size());
			
			if (p.getX() == 47 && p.getY() == 79)
				System.out.println("");
			/*
			if (posPuntoConflicto == null){
				finVentana2 = contorno.get((i + tamanioSegmento) % contorno.size());	
			}
			else*/
			posFinVentana2 = (posFinVentana + tamanioSegmento) % contorno.size();
			finVentana2 = contorno.get(posFinVentana2);
				
			double pendiente1 = 0;
			if (finVentana.getXDouble() - iniVentana.getXDouble() != 0)
				pendiente1 = (finVentana.getYDouble() - iniVentana.getYDouble()) / (finVentana.getXDouble() - iniVentana.getXDouble());
			else 
				pendiente1 = 1;
			
			double pendiente2 = 0;
			if (finVentana2.getXDouble() - finVentana.getXDouble() != 0)
				pendiente2 = (finVentana2.getYDouble() - finVentana.getYDouble()) / (finVentana2.getXDouble() - finVentana.getXDouble());
			else
				pendiente2 = 1;
			
			double tgAngulo = (pendiente2 - pendiente1) / (1 + pendiente2 * pendiente1);
			double angulo = Math.abs(Math.toDegrees(Math.atan(tgAngulo)));
			double lado = Pixel.lado(iniVentana, finVentana, finVentana2);
			
			if (lado < 0 && Math.abs(angulo) > anguloDesvio ){
				cantPixelsIzquierda++;
				//posPuntosConflicto.add(i % contorno.size());
			}
			else{
				cantPixelsIzquierda = 0;
				posPuntoConflicto = null;
			}

			if (cantPixelsIzquierda == 1){
				posPuntoConflicto = i;
				
			}
			
			if (cantPixelsIzquierda > 0){
				posPuntosConflicto.add(posPuntoConflicto % contorno.size());
				i = posPuntoConflicto + tamanioSegmento;
				posIniVentana = (Math.abs(i - tamanioSegmento) + 1) % contorno.size();
				posFinVentana = i % contorno.size();
				cantPixelsIzquierda = 0;
				posPuntoConflicto = null;
			}
			else{
				posIniVentana = (posIniVentana + 1) % contorno.size();
				posFinVentana = (posFinVentana + 1) % contorno.size();
			}
			/*
			posIniVentana = (posIniVentana + 1) % contorno.size();
			posFinVentana = (posFinVentana + 1) % contorno.size();
			*/
			iniVentana = contorno.get(posIniVentana);
			finVentana = contorno.get(posFinVentana);
			
			i++;
			if (i > contorno.size())
				parar = true;
			//i = (i + 1) % contorno.size();
		}

		if (posPuntosConflicto.size() > 1){
			for(i = 0; i < posPuntosConflicto.size(); i++)
				puntosConflicto.add(contorno.get(posPuntosConflicto.get(i)));

		}
		return puntosConflicto;
	}

	/**
	 * 
	 * @param obj
	 * @param objetoCircular
	 * @return
	 */
	private List<Objeto> separarObjetos(Objeto obj, EvaluadorClase objetoCircular) {	
		if (obj.getName().equals(""))
			System.out.println("");
		
		List<Pixel> contorno = obj.getContorno();
		/*
		int tamanioSegmento = (int)((double) porcTamanioSegmento * contorno.size() / 100);
		if (tamanioSegmento <= 10)
			tamanioSegmento = 10;
		setVentanaPixeles(tamanioSegmento);
		*/
		if (contorno.size() > getVentanaPixeles()){
			
			boolean huboDivision = false;
			List<Pixel> puntosConflicto = obtenerPuntosDeConflicto(contorno);
			if (puntosConflicto.size() > 1){
		
				List<Objeto> objetos = new ArrayList<Objeto>();
				Objeto objetoActual = obj;
				List<Pixel> nuevoContorno = new ArrayList<Pixel>();
				
				contorno = objetoActual.getContorno();
				
				List<Pixel> puntosConflictoVisitados = new ArrayList<Pixel>();
				for(Pixel puntoConflicto: puntosConflicto){
					boolean parar = false;
					int i = contorno.indexOf(puntoConflicto);
					nuevoContorno.clear();
					int inicio = i;
					puntosConflictoVisitados.add(puntoConflicto);
					while (i != -1 && !parar && contorno.size() > getVentanaPixeles()){
						
						Pixel p = contorno.get(i % contorno.size());
						
						if (!puntosConflicto.contains(p) || puntoConflicto.equals(p))
							nuevoContorno.add(p);
						else{
							
							List<Pixel> contorno1 = new ArrayList<Pixel>();
							List<Pixel> contorno2 = new ArrayList<Pixel>();
							
							dividirContorno(contorno, puntoConflicto, p, contorno1, contorno2);
							Objeto obj1 = new Objeto();
							obj1.setContorno(contorno1);
							Objeto obj2 = new Objeto();
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
							
							if (nuevoObjeto != null){
								
								detectarContorno.limpiarVisitados();
								detectarContorno.completarObjeto(nuevoObjeto);
								objetos.add(nuevoObjeto);
								nuevoObjeto.setName(obj.getName()+"_"+objetos.size());
								parar = true;
								nuevoContorno.clear();
								
								String info = "Objeto catalogado: "
									+ nuevoObjeto.getName()
									+ " - Puntos detectados: "
									+ nuevoObjeto.getPuntos().size();
						
								Visualizador.addLogInfo(info);
								
								
								contorno = objResto.getContorno();
								objResto.setName(obj.getName()+"_"+(objetos.size() + 1));
								List<Objeto> nuevosObjetos = separarObjetos(objResto, objetoCircular);
								objetos.addAll(nuevosObjetos);
								
								huboDivision = true;
								
								puntosConflictoVisitados.add(p);
								

							}
							else
								nuevoContorno.add(p);
						}
						i = i + 1;
						if (contorno.size() == 0 || i  % contorno.size() == inicio)
							parar = true;
					}
					
					if (huboDivision)
						break;
				}
				if (!huboDivision && contorno.size() > 0){
					Objeto nuevoObj = new Objeto();
					nuevoObj.setContorno(contorno);
					if (nuevoObj.validarContorno()){
						detectarContorno.limpiarVisitados();
						detectarContorno.completarObjeto(nuevoObj);
						objetos.add(nuevoObj);
						nuevoObj.setName(obj.getName()+"_"+objetos.size());
						
						String info = "Objeto catalogado: "
							+ obj.getName()
							+ " - Puntos detectados: "
							+ obj.getPuntos().size();
				
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
		list.add(obj);
		return list;
	}

	/**
	 * Crea una linea de pixeles que unen dos puntos
	 * @param p1
	 * @param p2
	 * @return
	 */
	private List<Pixel> crearLinea(Pixel p1, Pixel p2){
		List<Pixel> linea = new ArrayList<Pixel>();
		Pixel inicio = p1;
		Pixel fin = p2;
		if (p2.getX() < p1.getX() && p1.getX() != p2.getX()){
			inicio = p2;
			fin = p1;
		}
		else{
			if (p2.getX() == p1.getX() && p2.getY() < p1.getY()){
				inicio = p2;
				fin = p1;
			}
		}
		//linea.add(inicio);
		Pixel anterior = inicio;
		if (fin.getX() != inicio.getX()){
			double a = (fin.getYDouble() - inicio.getYDouble()) / (fin.getXDouble() - inicio.getXDouble());
			double b = fin.getYDouble() - a * fin.getXDouble();
			for(int x = inicio.getX(); x <= fin.getX(); x++){
				int y = (int) Math.round(a * x + b);
				Pixel p = new Pixel(x, y, Color.WHITE);
				if (!anterior.isAdyacente(p)){
					
					for(int y2 = anterior.getY() + 1; anterior.getY()< p.getY() && y2 < p.getY(); y2++){
						Pixel pAux = new Pixel(p.getX(), y2, Color.WHITE);
						linea.add(pAux);
						anterior = pAux;
					}

					for(int y2 = anterior.getY() - 1; p.getY() < anterior.getY() && y2 > p.getY(); y2--){
						Pixel pAux = new Pixel(p.getX(), y2, Color.WHITE);
						linea.add(pAux);
						anterior = pAux;
					}
					
				}
				if (!linea.contains(p)){
					linea.add(p);
					anterior = p;
				}
					
			}
		}
		else{
			for(int y = inicio.getY(); y <= fin.getY(); y++){
				Pixel p = new Pixel(inicio.getX(), y, Color.WHITE);
				if (!linea.contains(p))
					linea.add(p);	
			}
			
		}
		//linea.add(fin);
		return linea;
	}

	public String getCommandName() {
		return this.getClass().getName();
	}

	
	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
