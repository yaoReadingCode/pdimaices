package objeto;

import java.util.ArrayList;
import java.util.List;

public class Clase implements HistogramaContainer{

	private Long id;
	
	/**
	 * Nombre de la clase
	 */
	private String nombre;
	/**
	 * Descripcion de la clase
	 */
	private String descripcion;
	
	/**
	 * Color en formato int
	 */
	private Integer colorRgb;
	
	/**
	 * Orden de evaluación entre las clases
	 */
	private Integer ordenEvaluacion;
	
	private List<RasgoClase> rasgos;
	
	/**
	 * Rubro al que pertenece la clase
	 */
	private RubroCalidad rubroCalidad;
	
	/**
	 * Indica si es la clase que se asigna cuando un objeto no pertenece a ninguna otra clase
	 */
	private boolean indeterminado = false;
	
	/**
	 * Indica si el la clase correspondiente al objeto de referencia
	 */
	private boolean objetoReferencia = false;
	
	/**
	 * Cantidad de objetos que pertenecen a la clase
	 */
	private int cantidadObjetos = 0;
	
	private List<Histograma> histogramas = new ArrayList<Histograma>();
	
	public Clase(String nombre) {
		super();
		this.nombre = nombre;
	}

	public Clase(String nombre, String descripcion) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
	}


	public Clase() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<RasgoClase> getRasgos() {
		return rasgos;
	}

	public void setRasgos(List<RasgoClase> rasgos) {
		this.rasgos = rasgos;
	}

	public List<RasgoClase> getRasgosDeterminantes() {
		List<RasgoClase> rasgos = new ArrayList<RasgoClase>();
		if (getRasgos() != null){
			for(RasgoClase r:getRasgos()){
				if (r != null && r.getDeterminante()){
					rasgos.add(r);
				}
			}
			return rasgos;
		}
		return rasgos;
	}
	public Integer getColorRgb() {
		return colorRgb;
	}

	public void setColorRgb(Integer colorRgb) {
		this.colorRgb = colorRgb;
	}

	public Integer getOrdenEvaluacion() {
		return ordenEvaluacion;
	}

	public void setOrdenEvaluacion(Integer ordenEvaluacion) {
		this.ordenEvaluacion = ordenEvaluacion;
	}
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Clase))
			return false;
		Clase c = (Clase) o;
		if (getNombre() != null)
			return getNombre().equals(c.getNombre());
		return false;
	}

	 
	public String toString() {
		return getDescripcion();
	}
	
	/**
	 * Recupera el rasgo de un nombre  dado
	 * @param rasgo
	 * @return
	 */
	public RasgoClase getRasgo(Rasgo rasgo) {
		RasgoClase aux = new RasgoClase();
		aux.setRasgo(rasgo);
		int index = getRasgos().indexOf(aux);
		if (index != -1)
			return getRasgos().get(index);
		return null;
	}
	
	/**
	 * Calcula la distancia promedio de un objeto a una clase.
	 * Promedio de las distancias de cada rasgo del objeto a la media de la clase.
	 * @param objeto
	 * @return
	 */
	public Double distanciaPromedio(Objeto objeto){
		double acumulador = 0.0;
		double cantidad = 0;
		
		for(RasgoClase r:this.getRasgos()){
			if (r != null && r.getDeterminante()){
				RasgoObjeto ro = objeto.getRasgo(r.getRasgo(),this);
				if(ro != null && ro.getValor() != null){
					Double media = r.getMedia();
					if (media == null){
						double min = (r.getMinimo() != null) ? r.getMinimo():Double.MIN_VALUE;
						double max = (r.getMaximo() != null) ? r.getMaximo():Double.MAX_VALUE;
						media = (min + max) / 2;
					}
					double distancia = 0.0;
					distancia = Math.abs(media- ro.getValor());
					if (r.getPeso() != null && r.getPeso() != 0){
						distancia = distancia * Math.exp(r.getPeso());
					}
					acumulador += distancia;
					cantidad++;
				}
			}
		}
		if (cantidad != 0)
			return acumulador / cantidad;
		return null;
	}

	public RubroCalidad getRubroCalidad() {
		return rubroCalidad;
	}

	public void setRubroCalidad(RubroCalidad rubroCalidad) {
		this.rubroCalidad = rubroCalidad;
	}

	public boolean isIndeterminado() {
		return indeterminado;
	}

	public void setIndeterminado(boolean indeterminado) {
		this.indeterminado = indeterminado;
	}

	public boolean isObjetoReferencia() {
		return objetoReferencia;
	}

	public void setObjetoReferencia(boolean objetoReferencia) {
		this.objetoReferencia = objetoReferencia;
	}

	public int getCantidadObjetos() {
		return cantidadObjetos;
	}

	public void setCantidadObjetos(int cantidadObjetos) {
		this.cantidadObjetos = cantidadObjetos;
	}

	public List<Histograma> getHistogramas() {
		return histogramas;
	}

	public void setHistogramas(List<Histograma> histogramas) {
		this.histogramas = histogramas;
	}
	
	/**
	 * Retornan el Histograma de un dado tipo
	 * @param tipo
	 * @return
	 */
	public Histograma getHistograma(String tipo){
		Histograma h = new Histograma();
		h.setTipo(tipo);
		int index = getHistogramas().indexOf(h);
		if (index != -1)
			return getHistogramas().get(index);
		return null;
	}
}
