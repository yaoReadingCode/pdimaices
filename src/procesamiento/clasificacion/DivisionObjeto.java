package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.Pixel;
/**
 * Clase que contiene la division de un objeto en dos
 * @author oscar
 *
 */
public class DivisionObjeto implements Comparable<DivisionObjeto> {
	/**
	 * Valoracion de la division
	 */
	private Double valoracion;
	
	/**
	 * Objeto1
	 */
	private Objeto objeto1;
	
	/**
	 * Objeto2
	 */
	private Objeto objeto2;
	
	/**
	 * Pixel origen de la division 
	 */
	private Pixel origen;
	
	/**
	 * Pixel destino de la division
	 */
	private Pixel fin;
	
	private boolean circularObjeto1 = false;
	
	private boolean circularObjeto2 = false;


	public DivisionObjeto() {
		super();
	}

	public DivisionObjeto(Double valoracion, Objeto objeto1, Objeto objeto2) {
		super();
		this.valoracion = valoracion;
		this.objeto1 = objeto1;
		this.objeto2 = objeto2;
	}

	public Double getValoracion() {
		return valoracion;
	}

	public void setValoracion(Double valoracion) {
		this.valoracion = valoracion;
	}

	public Objeto getObjeto1() {
		return objeto1;
	}

	public void setObjeto1(Objeto objeto1) {
		this.objeto1 = objeto1;
	}

	public Objeto getObjeto2() {
		return objeto2;
	}

	public void setObjeto2(Objeto objeto2) {
		this.objeto2 = objeto2;
	}

	@Override
	public int compareTo(DivisionObjeto o) {
		return this.valoracion.compareTo(o.valoracion) * -1;
	}

	public Pixel getOrigen() {
		return origen;
	}

	public void setOrigen(Pixel origen) {
		this.origen = origen;
	}

	public Pixel getFin() {
		return fin;
	}

	public void setFin(Pixel fin) {
		this.fin = fin;
	}

	public boolean isCircularObjeto1() {
		return circularObjeto1;
	}

	public void setCircularObjeto1(boolean circularObjeto1) {
		this.circularObjeto1 = circularObjeto1;
	}

	public boolean isCircularObjeto2() {
		return circularObjeto2;
	}

	public void setCircularObjeto2(boolean circularObjeto2) {
		this.circularObjeto2 = circularObjeto2;
	}

}
