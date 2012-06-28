package objeto;

public class ToleranciaRubro {
	
	private Long id;

	/**
	 * Rubro de Calidad
	 */
	private RubroCalidad rubro;
	
	/**
	 * Grado
	 */
	private Grado grado;
	
	/**
	 * Valor minimo o maximo para el grado
	 */
	private Double valor;
	
	/**
	 * Descuento a aplicar por excedente
	 */
	private Double descuento;
	
	/**
	 * Tipo de descuento: Porcentual o Directo
	 */
	private String tipoDescuento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RubroCalidad getRubro() {
		return rubro;
	}

	public void setRubro(RubroCalidad rubro) {
		this.rubro = rubro;
	}

	public Grado getGrado() {
		return grado;
	}

	public void setGrado(Grado grado) {
		this.grado = grado;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getDescuento() {
		return descuento;
	}

	public void setDescuento(Double descuento) {
		this.descuento = descuento;
	}

	public String getTipoDescuento() {
		return tipoDescuento;
	}

	public void setTipoDescuento(String tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
	}
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof ToleranciaRubro))
			return false;
		ToleranciaRubro c = (ToleranciaRubro) o;
		if (getGrado() != null && getRubro() != null)
			return getGrado().equals(c.getGrado()) && getRubro().equals(c.getRubro());
		return false;
	}

	 
	public String toString() {
		return getGrado() + " - " + getRubro();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
