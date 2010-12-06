package objeto;

public class Rasgo {

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
	 * Nombre de la subclase de EvaluadorClase que se utiliza para evaluar el rasgo
	 */
	private String nombreEvaluadorRasgo;
	

	public Rasgo() {
		// TODO Auto-generated constructor stub
	}

	public Rasgo(String nombre) {
		super();
		this.nombre = nombre;
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
	
	public String getNombreEvaluadorRasgo() {
		return nombreEvaluadorRasgo;
	}

	public void setNombreEvaluadorRasgo(String nombreEvaluadorRasgo) {
		this.nombreEvaluadorRasgo = nombreEvaluadorRasgo;
	}

	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (!(obj instanceof Rasgo))
			return false;
		Rasgo r = (Rasgo) obj;
		
		if (this.getNombre() != null)
			return this.getNombre().equals(r.getNombre());
		return false;
	}
	
	public String toString(){
		return getNombre();
	}

}
