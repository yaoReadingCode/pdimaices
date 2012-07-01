package aplicarFiltros;


public class Agrupador {
	private String nombre;
	
	private Double cantidad = 0.0;
	
	private Double area = 0.0;
	
	private Double porcentaje = 0.0;
	
	private boolean graficar = true;
	
	public Agrupador() {
		super();
	}

	public Agrupador(String nombre, Double cantidad, Double area,
			Double porcentaje, boolean graficar) {
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

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
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

	public Double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(Double porcentaje) {
		this.porcentaje = porcentaje;
	}

	public boolean isGraficar() {
		return graficar;
	}

	public void setGraficar(boolean graficar) {
		this.graficar = graficar;
	}

	
}
