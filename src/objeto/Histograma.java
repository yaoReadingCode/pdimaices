package objeto;

import java.util.ArrayList;
import java.util.List;


public class Histograma extends RasgoObjeto {
	/**
	 * Tipo de histograma: R, G, V, H, S, V, etc.
	 */
	private String tipo;
	
	/**
	 * Cantidad maxima de valores que se pueden cargar para un tipo de histograma dado.
	 * Ej: para los tipos R, G y B el numero maximo de valores sera 128, 256, etc.
	 */
	private Integer maxValores;
	
	/**
	 * Lista que contiene la cantidad de pixeles para cada valor del histograma
	 */
	private List<ValorHistograma> valores = new ArrayList<ValorHistograma>(); 

	public Histograma() {
		// TODO Auto-generated constructor stub
	}

	public Histograma(Rasgo rasgo, Double valor) {
		super(rasgo, valor);
		// TODO Auto-generated constructor stub
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getMaxValores() {
		return maxValores;
	}

	public void setMaxValores(Integer maxValores) {
		this.maxValores = maxValores;
	}

	public List<ValorHistograma> getValores() {
		return valores;
	}

	public void setValores(List<ValorHistograma> valores) {
		this.valores = valores;
	}

	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (!(obj instanceof Histograma))
			return false;
		Histograma r = (Histograma) obj;
		
		if (this.getRasgo() != null)
			return this.getRasgo().equals(r.getRasgo()) && this.getTipo().equals(r.getTipo());
		return false;
	}
	
	public String toString(){
		if (getRasgo() != null)
			return getRasgo().getNombre() + " - " + getTipo();
		return "";
	}

	

}
