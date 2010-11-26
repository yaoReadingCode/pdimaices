package procesamiento.clasificacion;

public class Configuracion {

	private Long id;
	
	/**
	 * Nombre del sistema
	 */
	private String nombreSistema;
	
	/**
	 * Valor H mínimo
	 */
	private Float fondoHMin;
	
	/**
	 * Valor H máximo
	 */
	private Float fondoHMax;
	
	/**
	 * Valor S mínimo
	 */
	private Float fondoSMin;
	
	/**
	 * Valor S máximo
	 */
	private Float fondoSMax;
	
	/**
	 * Valor V mínimo
	 */
	private Float fondoVMin;
	
	/**
	 * Valor V máximo
	 */
	private Float fondoVMax;
	
	
	/**
	 * Area en cm2 del objeto de referencia
	 */
	private Double areaObjetoRefCm = 10.0;
	
	/**
	 * Color del objeto de referencia en formato int utilizado para encontrarlo en la imagen
	 */
	private Integer colorObjetoRefRgb;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNombreSistema() {
		return nombreSistema;
	}

	public void setNombreSistema(String nombreSistema) {
		this.nombreSistema = nombreSistema;
	}

	public Float getFondoHMin() {
		return fondoHMin;
	}

	public void setFondoHMin(Float fondoHMin) {
		this.fondoHMin = fondoHMin;
	}

	public Float getFondoHMax() {
		return fondoHMax;
	}

	public void setFondoHMax(Float fondoHMax) {
		this.fondoHMax = fondoHMax;
	}

	public Float getFondoSMin() {
		return fondoSMin;
	}

	public void setFondoSMin(Float fondoSMin) {
		this.fondoSMin = fondoSMin;
	}

	public Float getFondoSMax() {
		return fondoSMax;
	}

	public void setFondoSMax(Float fondoSMax) {
		this.fondoSMax = fondoSMax;
	}

	public Float getFondoVMin() {
		return fondoVMin;
	}

	public void setFondoVMin(Float fondoVMin) {
		this.fondoVMin = fondoVMin;
	}

	public Float getFondoVMax() {
		return fondoVMax;
	}

	public void setFondoVMax(Float fondoVMax) {
		this.fondoVMax = fondoVMax;
	}

	public Double getAreaObjetoRefCm() {
		return areaObjetoRefCm;
	}

	public void setAreaObjetoRefCm(Double areaObjetoRefCm) {
		this.areaObjetoRefCm = areaObjetoRefCm;
	}

	public Integer getColorObjetoRefRgb() {
		return colorObjetoRefRgb;
	}

	public void setColorObjetoRefRgb(Integer colorObjetoRefRgb) {
		this.colorObjetoRefRgb = colorObjetoRefRgb;
	}

}
