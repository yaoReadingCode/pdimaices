package procesamiento.clasificacion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import objeto.Clase;
import objeto.Objeto;

/**
 * Clase que define la interfaz necesaria para saber si un objeto
 * pertenece a una clase o no.
 * 
 * @author oscar
 * 
 */
public class EvaluadorClase {
	
	private Long id;
	
	/**
	 * Clase a evaluar
	 */
	private Clase clase;

	private List<EvaluadorRasgo> rasgos = new ArrayList<EvaluadorRasgo>();

	public EvaluadorClase(Clase clase, List<EvaluadorRasgo> rasgos) {
		super();
		this.clase = clase;
		this.rasgos = rasgos;
	}

	public Color getColor() {
		if (getClase().getColorRgb() != null)
			return new Color(getClase().getColorRgb());
		return null;
	}

	public void setColor(Color color) {
		getClase().setColorRgb(color.getRGB());
	}

	/**
	 * Indica si un objeto pertenece a la clase
	 * 
	 * @param objeto
	 * @return
	 */
	public boolean pertenece(Objeto objeto,boolean addRasgoToObject) {
		boolean pertenece = true;
		for (EvaluadorRasgo rasgo : getRasgos())
			if (!rasgo.isEnRango(objeto,addRasgoToObject))
				pertenece = false;
		return pertenece;
	}

	public Clase getClase() {
		return clase;
	}

	public void setClase(Clase clase) {
		this.clase = clase;
	}

	public List<EvaluadorRasgo> getRasgos() {
		return rasgos;
	}

	public void setRasgos(List<EvaluadorRasgo> rasgos) {
		this.rasgos = rasgos;
	}

	 
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof EvaluadorClase))
			return false;
		EvaluadorClase c = (EvaluadorClase) o;
		if (getClase() != null)
			return getClase().equals(c.getClase());
		return false;
	}

	 
	public String toString() {
		if (getClase() != null)
			return getClase().getNombre();
		return "";
	}

	public Long getId(){
		return id;
	}
	
	public void setId(Long id){
		this.id = id;
	}
	
}
