package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Objeto;
import objeto.Pixel;

public class Curvatura extends EvaluadorRasgo {
	/**
	 * Porcentaje de la longitud del contorno de objeto con el cuál se define el tamaño del segmento
	 */
	private int porcTamanioSegmento = 10;
	
	/**
	 * Angulo de variación más allá del cuál se concidera que la dirección del contorno cambia
	 */
	private int anguloDesvio = 10;

	public Curvatura() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public int getPorcTamanioSegmento() {
		return porcTamanioSegmento;
	}

	public void setPorcTamanioSegmento(int porcTamanioSegmento) {
		this.porcTamanioSegmento = porcTamanioSegmento;
	}

	public int getAnguloDesvio() {
		return anguloDesvio;
	}

	public void setAnguloDesvio(int anguloDesvio) {
		this.anguloDesvio = anguloDesvio;
	}

	public Double calcularValor(Objeto objeto) {
		List<Pixel> contorno = objeto.getContorno();
		int tamanioSegmento = (int)((double) getPorcTamanioSegmento() * contorno.size() / 100);
		double cantCambiosDireccion = 0;
		
		if (tamanioSegmento == 0)
			tamanioSegmento = 1;
		
		int posIniVentana = 0;
		int posFinVentana = tamanioSegmento - 1;
		Pixel iniVentana = contorno.get(posIniVentana);
		Pixel finVentana = contorno.get(posFinVentana);
		//Para la ecuacion de la recta
		double pendiente1 = 0;
		if (finVentana.getXDouble() - iniVentana.getXDouble() != 0)
			pendiente1 = (finVentana.getYDouble() - iniVentana.getYDouble()) / (finVentana.getXDouble() - iniVentana.getXDouble());
		
		int inicio = tamanioSegmento+ 1;
		boolean parar = false;

		while (!parar && contorno.size() > tamanioSegmento){
			
			Pixel p = contorno.get((posFinVentana + tamanioSegmento) % contorno.size());
			
			double pendiente2 = 0;
			if (p.getXDouble() - finVentana.getXDouble() != 0){
				pendiente2 = (p.getYDouble() - finVentana.getYDouble()) / (p.getXDouble() - finVentana.getXDouble());
				double tgAngulo = Math.abs((pendiente2 - pendiente1) / (1 + pendiente2 * pendiente1));
				double angulo = Math.toDegrees(Math.atan(tgAngulo));
				if (Math.abs(angulo) > anguloDesvio ){
					cantCambiosDireccion++;
				}
			}
			else{
				if (iniVentana.getXDouble() != p.getXDouble())
					cantCambiosDireccion++;
			}
			
			if (posFinVentana + tamanioSegmento >= contorno.size())
				parar = true;
			else{
				posIniVentana = posFinVentana;
				posFinVentana = (posFinVentana + tamanioSegmento) % contorno.size();
				iniVentana = contorno.get(posIniVentana);
				finVentana = contorno.get(posFinVentana);

				if (finVentana.getXDouble() - iniVentana.getXDouble() != 0)
					pendiente1 = (finVentana.getYDouble() - iniVentana.getYDouble()) / (finVentana.getXDouble() - iniVentana.getXDouble());
							
			}

		}
		
		if (cantCambiosDireccion != 0)
			return (double) contorno.size() / cantCambiosDireccion;
		
		return 0.0;
	}
	public static void main(String[] args) {
		Objeto o = new Objeto();
		List<Pixel> contorno = new ArrayList<Pixel>();
		contorno.add(new Pixel(10,10,null));
		contorno.add(new Pixel(11,10,null));
		contorno.add(new Pixel(12,10,null));
		contorno.add(new Pixel(13,10,null));
		contorno.add(new Pixel(14,10,null));
		contorno.add(new Pixel(15,10,null));
		contorno.add(new Pixel(15,11,null));
		contorno.add(new Pixel(15,12,null));
		contorno.add(new Pixel(15,13,null));
		contorno.add(new Pixel(15,14,null));
		contorno.add(new Pixel(15,15,null));
		contorno.add(new Pixel(14,15,null));
		contorno.add(new Pixel(13,15,null));
		contorno.add(new Pixel(12,15,null));
		contorno.add(new Pixel(11,15,null));
		contorno.add(new Pixel(10,15,null));
		contorno.add(new Pixel(10,14,null));
		contorno.add(new Pixel(10,13,null));
		contorno.add(new Pixel(10,12,null));
		contorno.add(new Pixel(10,11,null));
		contorno.add(new Pixel(10,10,null));
		o.setContorno(contorno);
		
		Curvatura c = new Curvatura();
		double valor = c.calcularValor(o);
	}
}
