package aplicarFiltros;


public class Agrupador {
	private String nombre;
	
	private Integer cantidad = 0;
	
	private Long area = 0l;
	
	private Float porcentaje = 0f;
	
	private boolean graficar = true;
	
	public Agrupador() {
		super();
	}

	public Agrupador(String nombre, Integer cantidad, Long area,
			Float porcentaje, boolean graficar) {
		super();
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.area = area;
		this.porcentaje = porcentaje;
		this.graficar = graficar;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Long getArea() {
		return area;
	}

	public void setArea(Long area) {
		this.area = area;
	}
	
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Agrupador))
			return false;
		Agrupador c = (Agrupador) o;
		if (getNombre() != null)
			return getNombre().equals(c.getNombre());
		return false;
	}

	 
	public String toString() {
		return getNombre();
	}

	public Float getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(Float porcentaje) {
		this.porcentaje = porcentaje;
	}

	public boolean isGraficar() {
		return graficar;
	}

	public void setGraficar(boolean graficar) {
		this.graficar = graficar;
	}

	
}
