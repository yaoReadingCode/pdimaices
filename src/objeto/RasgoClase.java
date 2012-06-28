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
	 * Sumatoria del valor del rasgo para todos los objetos que pertenecen a la clase
	 */
	private Double sumValor;

	/**
	 * Sumatoria del valor al cuadrado del rasgo para todos los objetos que pertenecen a la clase
	 */
	private Double sumValorCuadrado;
	
	/**
	 * Cantidad de valores del rasgo para todos los objetos que pertenecen a la clase
	 */
	private Integer cantValores;


	/**
	 * Valor máximo del rasgo para los objetos de la clase.
	 */
	private Double maximo;

	/**
	 * Valor máximo real del rasgo para los objetos de la clase.
	 * El atributo máximo puede no ser el máximo real de los objetos clasificados, esto depende de si
	 * el rango es variable o no y del valor por defecto inicial para el valor minimo. 
	 */
	private Double maximoReal;

	/**
	 * Valor mínimo del rasgo para los objetos de la clase.
	 */
	private Double minimo;
	
	/**
	 * Valor mínimo real del rasgo para los objetos de la clase.
	 * El atributo minimo puede no ser el minimo real de los objetos clasificados, esto depende de si
	 * el rango es variable o no y del valor por defecto inicial para el valor minimo. 
	 */
	private Double minimoReal;
	
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

	/**
	 *  Indicar si el valor maximo y minimo de un rasgo puede variar despues de realizar una clasificacion
	 */
	private Boolean rangoVariable = true;

	public RasgoClase() {
		// TODO Auto-generated constructor stub
	}

	public RasgoClase(Clase clase) {
		this.clase = clase;
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

	public Double getMaximo() {
		return maximo;
	}

	public void setMaximo(Double maximo) {
		this.maximo = maximo;
	}
	
	public Double getMaximoReal() {
		return maximoReal;
	}

	public void setMaximoReal(Double maximoReal) {
		this.maximoReal = maximoReal;
	}

	public Double getMinimo() {
		return minimo;
	}

	public void setMinimo(Double minimo) {
		this.minimo = minimo;
	}
	
	public Double getMinimoReal() {
		return minimoReal;
	}

	public void setMinimoReal(Double minimoReal) {
		this.minimoReal = minimoReal;
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

	public Double getSumValor() {
		return sumValor;
	}

	public void setSumValor(Double sumValor) {
		this.sumValor = sumValor;
	}

	public Double getSumValorCuadrado() {
		return sumValorCuadrado;
	}

	public void setSumValorCuadrado(Double sumValorCuadrado) {
		this.sumValorCuadrado = sumValorCuadrado;
	}

	public Integer getCantValores() {
		return cantValores;
	}

	public void setCantValores(Integer cantValores) {
		this.cantValores = cantValores;
	}

	public Boolean getRangoVariable() {
		return rangoVariable;
	}

	public void setRangoVariable(Boolean rangoVariable) {
		this.rangoVariable = rangoVariable;
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
