package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.Rasgo;
import objeto.RasgoObjeto;

/**
 * Representa un rasgo medible de una clase
 * 
 * @author oscar
 * 
 */
public abstract class EvaluadorRasgo {
	private Rasgo rasgo;
	private Double valor;
	private Double desvioEstandar = 0.0;
	private Double maximo;
	private Double minimo;
	
	public EvaluadorRasgo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EvaluadorRasgo(Rasgo rasgo, Double valor, Double desvioEstandar) {
		super();
		this.desvioEstandar = desvioEstandar;
		this.rasgo = rasgo;
		this.valor = valor;
		this.maximo = valor + desvioEstandar;
		this.minimo = valor - desvioEstandar;
	}

	public Rasgo getRasgo() {
		return rasgo;
	}

	public void setRasgo(Rasgo rasgo) {
		this.rasgo = rasgo;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
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

	public Double getMinimo() {
		return minimo;
	}

	public void setMinimo(Double minimo) {
		this.minimo = minimo;
	}

	public abstract Double calcularValor(Objeto objeto);

	public boolean isEnRango(Objeto objeto, boolean addRasgoToObject) {
		Double valor = calcularValor(objeto);
		if (addRasgoToObject){
			RasgoObjeto rasgoObjeto = new RasgoObjeto(this.getRasgo(),valor);
			objeto.addRasgo(rasgoObjeto);
		}
		if (valor != null) {
			//if (getValor() - getDesvioEstandar() <= valor && getValor() + getDesvioEstandar() >= valor)
			if (getMinimo() == null && getMaximo() == null)
				return true;
			
			if (getMinimo() == null && getMaximo() != null){
				if (getMaximo() >= valor)
					return true;
				else
					return false;
			}
			
			if (getMinimo() != null && getMaximo() == null){
				if (getMinimo() <= valor)
					return true;
				else
					return false;
			}

			if (getMinimo() != null && getMaximo() != null){
				if (getMinimo() <= valor && getMaximo() >= valor)
					return true;
				else
					return false;
			}
			
		}
		return false;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EvaluadorRasgo other = (EvaluadorRasgo) obj;
		if (rasgo == null) {
			if (other.rasgo != null)
				return false;
		} else if (!rasgo.equals(other.rasgo))
			return false;
		return true;
	}

}
