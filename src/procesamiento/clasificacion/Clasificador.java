package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import objeto.Clase;
import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;
import dataAcces.ObjectDao;

public class Clasificador {
	public static String CLASE_INDETERMINADO = "INDETERMINADO";

	private Map<EvaluadorClase, List<Objeto>> clasificacion = new HashMap<EvaluadorClase, List<Objeto>>();
	
	private ObjetoReferencia objetoReferencia= null; 
	
	private Configuracion configuracion;
	
	private List<Clase> clases = null;

	private Integer cantidadObjetos = null;
	
	public Clasificador() {
		super();
		configuracion = ObjectDao.getInstance().findConfiguracion("MAICES");
	}

	public Map<EvaluadorClase, List<Objeto>> getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(Map<EvaluadorClase, List<Objeto>> clasificacion) {
		this.clasificacion = clasificacion;
	}
	
	public ObjetoReferencia getObjetoReferencia() {
		return objetoReferencia;
	}

	public void setObjetoReferencia(ObjetoReferencia objetoReferencia) {
		this.objetoReferencia = objetoReferencia;
	}

	public Configuracion getConfiguracion() {
		return configuracion;
	}

	public void setConfiguracion(Configuracion configuracion) {
		this.configuracion = configuracion;
	}

	/**
	 * Guarda la clasificacion en la base de datos
	 * @param objetos
	 */
	public void guardarClasificacion(){
		ObjectDao dao = ObjectDao.getInstance();
		Set<EvaluadorClase> clases = clasificacion.keySet();
		for(EvaluadorClase c: clases){
			List<Objeto> objetosClase = clasificacion.get(c);
			for(Objeto obj:objetosClase){
				dao.save(obj);
			}
			
			for(EvaluadorRasgo er: c.getRasgos()){
				if (er.getRasgoClase().getRangoVariable() == true)
					actualizarRasgoClase(er.getRasgoClase(), objetosClase);;
			}
		}
	}
	
	/**
	 * Actualiza los valores valor medio, desvio estandar, maximo y minimo del rasgo de una clase
	 * @param rasgoClase RasgoClase
	 * @param objetos Objetos pertenecientes a la clase
	 */
	private void actualizarRasgoClase(RasgoClase rasgoClase, List<Objeto> objetos){
		Double sumValor = 0.0;
		Double sumValorCuadrado = 0.0;
		Double maximo = rasgoClase.getMaximo();
		Double minimo = rasgoClase.getMinimo();
		Integer cantValores = 0;
		
		if(rasgoClase.getSumValor() != null)
			sumValor = rasgoClase.getSumValor();
		if(rasgoClase.getSumValorCuadrado() != null)
			sumValorCuadrado = rasgoClase.getSumValorCuadrado();
		if(rasgoClase.getCantValores() != null)
			cantValores = rasgoClase.getCantValores();

		for(Objeto o: objetos){
			RasgoObjeto ro = o.getRasgo(rasgoClase.getRasgo());
			if(ro != null && ro.getValor() != null){
				sumValor += ro.getValor();
				Double valorCuadrado = Math.pow(ro.getValor(), 2); 
				sumValorCuadrado += valorCuadrado;
				if ((maximo != null &&  ro.getValor() > maximo) || maximo == null)
					maximo = ro.getValor();
				if ((minimo != null &&  ro.getValor() < minimo) || minimo == null)
					minimo = ro.getValor();
			}
			cantValores++;
		}
		
		rasgoClase.setSumValor(sumValor);
		rasgoClase.setSumValorCuadrado(sumValorCuadrado);
		rasgoClase.setCantValores(cantValores);
		rasgoClase.setMinimo(minimo);
		rasgoClase.setMaximo(maximo);
		
		ObjectDao.getInstance().save(rasgoClase);		
	}
	
	/**
	 * Inicializa el hash de las clases
	 * @throws Exception 
	 */
	public void inicializarClasificacion() throws Exception{
		objetoReferencia = new ObjetoReferencia();
		
		ObjectDao dao = ObjectDao.getInstance();
		List<Clase> clases = dao.qryClases(null,false,false);
		clasificacion = new HashMap<EvaluadorClase, List<Objeto>>();
		
		this.cantidadObjetos = dao.getCantidadObjetos();
		
		for(Clase c: clases){
			EvaluadorClase ec = createEvaluadorClase(c);
			getClasificacion().put(ec, new ArrayList<Objeto>());
		}
		
	}
	/**
	 * Recupera el evaluador de una clase
	 * @param nombreClase
	 * @return
	 */
	public EvaluadorClase getEvaluadorClase(String nombreClase){
		Clase c = ObjectDao.getInstance().findClase(nombreClase,null,null);
		EvaluadorClase ec = createEvaluadorClase(c);
		return ec;
		
	}
	
	/**
	 * Recupera el evaluador de una clase
	 * @param nombreClase
	 * @return
	 */
	public EvaluadorClase getEvaluadorClaseIndeterminado(){
		Clase c = ObjectDao.getInstance().findClase(null,true,null);
		EvaluadorClase ec = createEvaluadorClase(c);
		return ec;
		
	}

	/**
	 * Recupera el evaluador de una clase
	 * @param nombreClase
	 * @return
	 */
	public EvaluadorClase getEvaluadorClaseObjetoReferencia(){
		Clase c = ObjectDao.getInstance().findClase(null,null,true);
		EvaluadorClase ec = createEvaluadorClase(c);
		return ec;
		
	}

	/**
	 * Recupera el evaluador de una clase
	 * @param nombreClase
	 * @return
	 */
	private EvaluadorClase createEvaluadorClase(Clase c){
		List<EvaluadorRasgo> rasgos = new ArrayList<EvaluadorRasgo>();
		for(RasgoClase r: c.getRasgos()){
			try {
				if (r != null){
					if (r.getRasgo().getNombreEvaluadorRasgo() != null){
						Class evaluadorClass = Class.forName(r.getRasgo().getNombreEvaluadorRasgo());
						EvaluadorRasgo er = (EvaluadorRasgo) evaluadorClass.newInstance();
						er.setObjetoReferencia(getObjetoReferencia());
						er.setRasgoClase(r);

						rasgos.add(er);
						System.out.println(r.getRasgo() + ", minimo: " + er.getMinimo() + ", maximo: " + er.getMaximo());
					}
					else{
						EvaluadorRasgo er = new EvaluadorRasgo();
						er.setObjetoReferencia(getObjetoReferencia());
						er.setRasgoClase(r);

						rasgos.add(er);
						System.out.println(r.getRasgo() + ", minimo: " + er.getMinimo() + ", maximo: " + er.getMaximo());
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		EvaluadorClase ec = new EvaluadorClase(c,rasgos);
		return ec;
		
	}

	public List<Clase> getClases() {
		if (clases == null){
			clases = ObjectDao.getInstance().qryAll(Clase.class.getName());
		}
		return clases;
	}
	
	public int countObject(){
		int count = 0;
		Set<EvaluadorClase> clases = getClasificacion().keySet();
		for(EvaluadorClase c: clases){
			List<Objeto> objetosClase = getClasificacion().get(c);
			count = count + objetosClase.size();
		}	
		return count;
	}
	
	/**
	 * Retorna los objetos asignados a una clase dada
	 * @param clase
	 * @return
	 */
	public List<Objeto> getObjetosClase(Clase clase){
		for(EvaluadorClase ec: getClasificacion().keySet()){
			if (ec.getClase().equals(clase))
				return getClasificacion().get(ec);
		}
		return null;
	}

	public Integer getCantidadObjetos() {
		return cantidadObjetos;
	}

	public void setCantidadObjetos(Integer cantidadObjetos) {
		this.cantidadObjetos = cantidadObjetos;
	}
	
	public void aumentarCantidadObjetos(){
		this.cantidadObjetos++;
	}
	
}
