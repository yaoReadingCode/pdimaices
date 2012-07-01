package objeto;

import procesamiento.descuento.AplicarDescuento;
import procesamiento.descuento.EvaluadorRubro;

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
	
	private EvaluadorRubro evaluadorValorRubro;
	
	private AplicarDescuento evaluadorDescuento;
	
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
	
	public EvaluadorRubro getEvaluadorValorRubro() {
		if (evaluadorValorRubro == null){
			evaluadorValorRubro = createEvaluadorValorRubro();
		}
		return evaluadorValorRubro;
	}

	private EvaluadorRubro createEvaluadorValorRubro() {
		try{
			if (getRubro().getClaseEvaluadorValorRubro() != null){
				EvaluadorRubro evaluadorRubro = (EvaluadorRubro) Class.forName(getRubro().getClaseEvaluadorValorRubro()).newInstance();
				evaluadorRubro.setValor(getValor());
				return evaluadorRubro;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setEvaluadorValorRubro(EvaluadorRubro evaluadorValorRubro) {
		this.evaluadorValorRubro = evaluadorValorRubro;
	}

	public AplicarDescuento getEvaluadorDescuento() {
		if (evaluadorDescuento == null){
			evaluadorDescuento = createEvaluadorDescuento();
		}
		return evaluadorDescuento;
	}

	private AplicarDescuento createEvaluadorDescuento() {
		try{
			if (getRubro().getClaseEvaluadorDescuento() != null){
				AplicarDescuento evaluadorDescuento = (AplicarDescuento) Class.forName(getRubro().getClaseEvaluadorDescuento()).newInstance();
				evaluadorDescuento.setDescuento(getDescuento());
				return evaluadorDescuento;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setEvaluadorDescuento(AplicarDescuento evaluadorDescuento) {
		this.evaluadorDescuento = evaluadorDescuento;
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
