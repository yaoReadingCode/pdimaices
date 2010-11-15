package procesamiento;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.PlanarImage;

import objeto.Clase;
import objeto.Objeto;
import objeto.Pixel;
import objeto.Rasgo;
import procesamiento.clasificacion.AspectRatio;
import procesamiento.clasificacion.Circularidad;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.EvaluadorRasgo;

public class SepararObjetos extends AbstractImageCommand {

	/**
	 * Cantidad de pixeles del contorno a utilizar para ver si un pixel se desvía 
	 * demasiado del contorno. Lo que indicaría que pertenece a otro objeto
	 */
	private int ventanaPixeles = 10;
	private static int anguloDesvio = 35; 
	
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
		return ventanaPixeles;
	}


	public void setVentanaPixeles(int ventanaPixeles) {
		this.ventanaPixeles = ventanaPixeles;
	}


	public PlanarImage execute() {
		if (getObjetos() != null){
			Circularidad circularidad = new Circularidad(new Rasgo("Circularidad"), 1.0, 0.2);
			AspectRatio aspectRadio = new AspectRatio(new Rasgo("AspectRadio"), 1.0, 0.4);
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
	 * 
	 * @param obj
	 * @param objetoCircular
	 * @return
	 */
	private List<Objeto> separarObjetos(Objeto obj, EvaluadorClase objetoCircular) {	
		List<Pixel> contorno = obj.getContorno();
		
		if (contorno.size() >= getVentanaPixeles()){
			List<Integer> posPuntosConflicto = new ArrayList<Integer>();
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
				List<Pixel> puntosConflicto = new ArrayList<Pixel>();
				for(i = 0; i < posPuntosConflicto.size(); i++)
					puntosConflicto.add(contorno.get(posPuntosConflicto.get(i)));
					
				List<Objeto> objetos = new ArrayList<Objeto>();
				Objeto objetoActual = obj;
				List<Pixel> nuevoContorno = new ArrayList<Pixel>();
				
				contorno = objetoActual.getContorno();
				
				List<Pixel> puntosConflictoVisitados = new ArrayList<Pixel>();
				for(Pixel puntoConflicto: puntosConflicto){
					parar = false;
					i = contorno.indexOf(puntoConflicto);
					nuevoContorno.clear();
					inicio = i;
					puntosConflictoVisitados.add(puntoConflicto);
					while (i != -1 && !parar && contorno.size() > getVentanaPixeles()){
						
						Pixel p = contorno.get(i % contorno.size());
						
						if (!puntosConflicto.contains(p) || puntosConflictoVisitados.contains(p))
							nuevoContorno.add(p);
						else{
							List<Pixel> contornoAux = new ArrayList<Pixel>(nuevoContorno);
							
							List<Pixel> lineaPixeles = crearLinea(p, contornoAux.get(0));
							//Linea de pixeles ordenada
							List<Pixel> lineaPixelesOrd = new ArrayList<Pixel>();
							contornoAux.add(p);
							
							lineaPixeles.remove(p);
							Pixel proximo = p;
							while(lineaPixeles.size() > 0){
								 proximo = proximo.getPixelMasCercano(lineaPixeles);
								 if (!proximo.equals(contornoAux.get(0)))
									 lineaPixelesOrd.add(proximo);
								 lineaPixeles.remove(proximo);
								 
							}
							
							contornoAux.addAll(lineaPixelesOrd);

							Objeto nuevoObj = new Objeto();
							nuevoObj.setContorno(contornoAux);
							if (nuevoObj.validarContorno()){
								if (obj.getName().equals("64"))
									System.out.println("");
								
								if (objetoCircular.pertenece(nuevoObj,false)){
									detectarContorno.limpiarVisitados();
									detectarContorno.completarObjeto(nuevoObj);
									objetos.add(nuevoObj);
									nuevoObj.setName(obj.getName()+"_"+objetos.size());
									parar = true;
									nuevoContorno.clear();
									
									//Elimino del contorno el objeto separado
									int indexP1 = contorno.indexOf(puntoConflicto);
									int indexP2 = i  % contorno.size();
									List<Pixel> contorno1 = new ArrayList<Pixel>(contorno.subList(0, indexP1 + 1));
									List<Pixel> contorno2 = new ArrayList<Pixel>(contorno.subList(indexP2, contorno.size()));
									
									contorno.clear();
									contorno.addAll(contorno1);
									for(int j = lineaPixelesOrd.size() -1; j >= 0 ; j--)
										contorno.add(lineaPixelesOrd.get(j));
									contorno.addAll(contorno2);
									
									puntosConflictoVisitados.add(p);
								}
								else 
									nuevoContorno.add(p);
							}
							else 
								nuevoContorno.add(p);
						}
						i = i + 1;
						if (i  % contorno.size() == inicio)
							parar = true;
					}
				}
				if (contorno.size() > 0){
					Objeto nuevoObj = new Objeto();
					nuevoObj.setContorno(contorno);
					if (nuevoObj.validarContorno()){
						detectarContorno.limpiarVisitados();
						detectarContorno.completarObjeto(nuevoObj);
						objetos.add(nuevoObj);
						nuevoObj.setName(obj.getName()+"_"+objetos.size());
					}
				}
				return objetos;
			}
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
