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
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Grado))
			return false;
		Grado c = (Grado) o;
		if (getSistema()!= null && getNombre() != null)
			return getSistema().equals(c.getSistema()) && getNombre().equals(c.getNombre());
		return false;
	}

	 
	public String toString() {
		return getSistema() + " - " + getNombre();
	}
	
}
