package procesamiento.clasificacion;

import objeto.Objeto;
import objeto.RasgoClase;
import objeto.RasgoObjeto;

/**
 * Representa un rasgo medible de una clase
 * 
 * @author oscar
 * 
 */
public class EvaluadorRasgo {
	private RasgoClase rasgoClase;
	private Double maximo;
	private Double minimo;
	
	public EvaluadorRasgo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EvaluadorRasgo(RasgoClase rasgo, Double minimo, Double maximo) {
		this.maximo = maximo;
		this.minimo = minimo;
		this.rasgoClase = rasgo;
	}

	public RasgoClase getRasgoClase() {
		return rasgoClase;
	}

	public void setRasgoClase(RasgoClase rasgo) {
		this.rasgoClase = rasgo;
		
		if (rasgo != null){
			if (rasgo.getMedia() != null){
				if (rasgo.getCalcularValorMedio()){
					this.setMinimo(rasgo.getMedia() - 2 * rasgo.getDesvioEstandar());
					this.setMaximo(rasgo.getMedia() + 2 * rasgo.getDesvioEstandar());
				}
				else{
					this.setMinimo(rasgo.getMinimo());
					this.setMaximo(rasgo.getMaximo());
				}
			}
			else{
				this.setMinimo(rasgo.getMinimo());
				this.setMaximo(rasgo.getMaximo());
			}
			
		}
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

	public RasgoObjeto calcularValor(Objeto objeto){
		Double valor = null;
		if (getMaximo() != null && getMinimo() != null)
			valor = (getMaximo() + getMinimo()) / 2;
		return new RasgoObjeto(this.getRasgoClase().getRasgo(),valor);
	}

	public boolean isEnRango(Objeto objeto, RasgoObjeto rasgoObjeto) {
		Double valor = rasgoObjeto.getValor();
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
		if (rasgoClase == null) {
			if (other.rasgoClase != null)
				return false;
		} else if (!rasgoClase.equals(other.rasgoClase))
			return false;
		return true;
	}

}
