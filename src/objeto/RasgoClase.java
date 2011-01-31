package objeto;


public class RasgoClase {
	
	private Long id;
	
	private Rasgo rasgo;
	
	private Clase clase;
	
	/**
	 * Valor medio del rasgo para los objetos de la clase.
	 */
	private Double media;
 
	/**
	 * Desvío estándar del rasgo para los objetos de la clase.
	 */
	private Double desvioEstandar = 0.0;
	
	/**
	 * Valor medio default utilizado inicialmente cuando no hay
	 * datos previos de los objetos de una clase.
	 */
	private Double mediaDefault;

	/**
	 * Desvio estandar default utilizado inicialmente cuando no hay
	 * datos previos de los objetos de una clase.
	 */
	private Double desvioEstandarDefault;
	
	/**
	 * Valor máximo del rasgo para los objetos de la clase.
	 */
	private Double maximo;

	/**
	 * Valor mínimo del rasgo para los objetos de la clase.
	 */
	private Double minimo;
	
	/**
	 * Determina el peso de un rasgo para determinar la pertenencia o no de un objeto a una clase.<br>
	 * Los rasgos seran evaluados en orden decreciente de peso. Primero los de mayor peso hasta llegar 
	 * a los de menor peso.<br> 
	 * Si el peso es 0 indica que el rasgo no es determinante para indicar la pertenencia del objeto a la clase.   
	 */
	private Double peso;
	
	/**
	 * Determina si se debe utilizar el valor medio y el desvio estandar para validar
	 * si un objeto pertenece a una clase. De lo contrario se utilizan los valores maximos
	 * y minimos para cada par <rasgo,clase>
	 */
	private Boolean calcularValorMedio;
	
	/**
	 * Indica si el rasgo es determinante para analizar la pertenecia de un objeto a la clase
	 */
	private Boolean determinante = true;

	public RasgoClase() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Rasgo getRasgo() {
		return rasgo;
	}

	public void setRasgo(Rasgo rasgo) {
		this.rasgo = rasgo;
	}

	public Clase getClase() {
		return clase;
	}

	public void setClase(Clase clase) {
		this.clase = clase;
	}

	public Double getMedia() {
		return media;
	}

	public void setMedia(Double media) {
		this.media = media;
	}

	public Double getDesvioEstandar() {
		return desvioEstandar;
	}

	public void setDesvioEstandar(Double desvioEstandar) {
		this.desvioEstandar = desvioEstandar;
	}

	public Double getMediaDefault() {
		return mediaDefault;
	}

	public void setMediaDefault(Double mediaDefault) {
		this.mediaDefault = mediaDefault;
	}

	public Double getDesvioEstandarDefault() {
		return desvioEstandarDefault;
	}

	public void setDesvioEstandarDefault(Double desvioEstandarDefault) {
		this.desvioEstandarDefault = desvioEstandarDefault;
	}

	public Double getMaximo() {
		return maximo;
	}

	public void setMaximo(Double maximo) {
		this.maximo = maximo;
	}

	public Double getMinimo() {
		return minimo;
	}

	public void setMinimo(Double minimo) {
		this.minimo = minimo;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}

	public Boolean getCalcularValorMedio() {
		return calcularValorMedio;
	}

	public void setCalcularValorMedio(Boolean calcularValorMedio) {
		this.calcularValorMedio = calcularValorMedio;
	}

	public Boolean getDeterminante() {
		return determinante;
	}

	public void setDeterminante(Boolean determinante) {
		this.determinante = determinante;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof RasgoClase))
			return false;
		RasgoClase r = (RasgoClase) o;
		if (getClase() != null && getRasgo() != null)
			return getClase().equals(r.getClase()) && getRasgo().equals(r.getRasgo());
		return false;
	}

	 
	public String toString() {
		if (getClase() != null && getRasgo() != null)
			return getClase().getNombre() + " - " + getRasgo().getNombre();
		return "";
	}

}
