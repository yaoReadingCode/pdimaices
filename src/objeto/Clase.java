package objeto;

import java.util.List;

public class Clase {

	private Long id;
	
	/**
	 * Nombre de la clase
	 */
	private String nombre;
	
	public Clase(String nombre) {
		super();
		this.nombre = nombre;
	}

	public Clase(String nombre, String descripcion) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

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

}
