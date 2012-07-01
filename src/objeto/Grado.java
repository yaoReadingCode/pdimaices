package objeto;

import java.util.HashSet;
import java.util.Set;

public class Grado {

	private Long id;
	
	/**
	 * Nombre
	 */
	private String nombre;
	
	/**
	 * Rebaja a aplicar para el grado
	 */
	private Double rebaja;
	
	/**
	 * Indica si es el grado Fuera de Estandar
	 */
	private boolean fueraEstandar;
	
	/**
	 * Identificador del sistema 
	 */
	private String sistema;

	private Set<ToleranciaRubro> toleranciaRubros = new HashSet<ToleranciaRubro>();
	
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

	public Double getRebaja() {
		return rebaja;
	}

	public void setRebaja(Double rebaja) {
		this.rebaja = rebaja;
	}

	public boolean isFueraEstandar() {
		return fueraEstandar;
	}

	public void setFueraEstandar(boolean fueraEstandar) {
		this.fueraEstandar = fueraEstandar;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}
	
	public Set<ToleranciaRubro> getToleranciaRubros() {
		return toleranciaRubros;
	}

	public void setToleranciaRubros(Set<ToleranciaRubro> toleranciaRubros) {
		this.toleranciaRubros = toleranciaRubros;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Grado))
			return false;
		Grado c = (Grado) o;
		if (getSistema()!= null && getNombre() != null)
			return getSistema().equals(c.getSistema()) && getNombre().equals(c.getNombre());
		return false;
	}

	 
	public String toString() {
		return getSistema() + " - " + getNombre();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
}
