package objeto;


public class RasgoClase {
	
	private Long id;
	
	private Rasgo rasgo;
	
	private Clase clase;
	
	private Double media;
 
	private Double desvioEstandar = 0.0;
	
	private Double mediaDefault;

	private Double desvioEstandarDefault;
	
	private Double maximo;
	
	private Double minimo;
	
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
