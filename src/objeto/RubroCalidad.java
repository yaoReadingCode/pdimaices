package objeto;

public class RubroCalidad {
	
	private Long id;
	
	/**
	 * Nombre
	 */
	private String nombre;
	
	/**
	 * Descripción
	 */
	private String descripcion;
	
	/**
	 * Identificador del sistema al que pertenece
	 */
	private String sistema;
	
	/**
	 * Clase utilizada para evaluar el valor del rubro para un determinado Grado. La clase debe
	 * extender de procesamiento.descuento.EvaluadorRubro.
	 */
	private String claseEvaluadorValorRubro;
	
	/**
	 * Clase utilizada para evaluar el descuento a aplicar cuando se exede el valor determinado para el grado.
	 * La clase debe extender de procesamiento.descuento.AplicarDescuento.
	 */
	private String claseEvaluadorDescuento;

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

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}
	
	public String getClaseEvaluadorValorRubro() {
		return claseEvaluadorValorRubro;
	}

	public void setClaseEvaluadorValorRubro(String claseEvaluadorValorRubro) {
		this.claseEvaluadorValorRubro = claseEvaluadorValorRubro;
	}

	public String getClaseEvaluadorDescuento() {
		return claseEvaluadorDescuento;
	}

	public void setClaseEvaluadorDescuento(String claseEvaluadorDescuento) {
		this.claseEvaluadorDescuento = claseEvaluadorDescuento;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof RubroCalidad))
			return false;
		RubroCalidad c = (RubroCalidad) o;
		if (getSistema()!= null && getNombre() != null)
			return getSistema().equals(c.getSistema()) && getNombre().equals(c.getNombre());
		return false;
	}

	 
	public String toString() {
		return getSistema() + " - " + getNombre();
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	
	
}
