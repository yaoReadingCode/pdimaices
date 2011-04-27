package objeto;


public class ClaseObjeto {
	private Long id;
	
	/**
	 * Clase
	 */
	private Clase clase;
	
	/**
	 * Objeto al que pertenece
	 */
	private Objeto objeto;
	
	/**
	 * Mide la distancia del objeto a la clase
	 */
	private Double distanciaPromedio;
	
	
	public ClaseObjeto() {
		super();
	}

	public ClaseObjeto(Clase clase) {
		this.clase = clase;
	}

	public Objeto getObjeto() {
		return objeto;
	}

	public void setObjeto(Objeto objeto) {
		this.objeto = objeto;
	}

	public Clase getClase() {
		return clase;
	}

	public void setClase(Clase clase) {
		this.clase = clase;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof ClaseObjeto))
			return false;
		ClaseObjeto c = (ClaseObjeto) o;
		if (getClase()!= null )
			return getClase().equals(c.getClase());
		return false;
	}

	 
	public String toString() {
		if (getClase() != null)
			return getClase().getNombre();
		return "";
	}

	public Long getId(){
		return id;
	}
	
	public void setId(Long id){
		this.id = id;
	}

	public Double getDistanciaPromedio() {
		return distanciaPromedio;
	}

	public void setDistanciaPromedio(Double distanciaPromedio) {
		this.distanciaPromedio = distanciaPromedio;
	}
	
}
