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
	private Double valor;
	private Double desvioEstandar = 0.0;
	private Double maximo;
	private Double minimo;
	private ObjetoReferencia objetoReferencia;
	
	public EvaluadorRasgo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EvaluadorRasgo(RasgoClase rasgo, Double valor, Double desvioEstandar) {
		super();
		this.desvioEstandar = desvioEstandar;
		this.rasgoClase = rasgo;
		this.valor = valor;
		this.maximo = valor + desvioEstandar;
		this.minimo = valor - desvioEstandar;
	}

	public RasgoClase getRasgoClase() {
		return rasgoClase;
	}

	public void setRasgoClase(RasgoClase rasgo) {
		this.rasgoClase = rasgo;
		
		if (rasgo != null){
			if (rasgo.getMedia() != null){
				this.setValor(rasgo.getMedia());
			}
			else{
				this.setValor(rasgo.getMediaDefault());
			}
			if (rasgo.getDesvioEstandar() != null){
				this.setDesvioEstandar(rasgo.getDesvioEstandar());
			}
			else{
				this.setDesvioEstandar(rasgo.getDesvioEstandarDefault());
			}

			if (rasgo.getMedia() != null){
				if (rasgo.getCalcularValorMedio()){
					this.setMinimo(rasgo.getMedia() - rasgo.getDesvioEstandar());
					this.setMaximo(rasgo.getMedia() + rasgo.getDesvioEstandar());
				}
				else{
					this.setMinimo(rasgo.getMinimo());
					this.setMaximo(rasgo.getMaximo());
				}
			}
			else{
				if (rasgo.getMediaDefault() != null && rasgo.getDesvioEstandarDefault() != null){
					this.setMinimo(rasgo.getMediaDefault() - rasgo.getDesvioEstandarDefault());
					this.setMaximo(rasgo.getMediaDefault() + rasgo.getDesvioEstandarDefault());
				}
			}
			
		}
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

	public Double calcularValor(Objeto objeto){
		return getValor();
	}

	public ObjetoReferencia getObjetoReferencia() {
		return objetoReferencia;
	}

	public void setObjetoReferencia(ObjetoReferencia objetoReferencia) {
		this.objetoReferencia = objetoReferencia;
	}

	public boolean isEnRango(Objeto objeto, boolean addRasgoToObject) {
		Double valor = calcularValor(objeto);
		if (addRasgoToObject){
			RasgoObjeto rasgoObjeto = new RasgoObjeto(this.getRasgoClase().getRasgo(),valor);
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
		if (rasgoClase == null) {
			if (other.rasgoClase != null)
				return false;
		} else if (!rasgoClase.equals(other.rasgoClase))
			return false;
		return true;
	}

}
