package procesamiento.clasificacion;

import java.util.ArrayList;
import java.util.List;

import objeto.Objeto;
import objeto.Pixel;

public class Curvatura extends EvaluadorRasgo {
	/**
	 * Porcentaje de la longitud del contorno de objeto con el cuál se define el tamaño del segmento
	 */
	private int porcTamanioSegmento = 2;
	
	/**
	 * Angulo de variación más allá del cuál se concidera que la dirección del contorno cambia
	 */
	private int anguloDesvio = 20;

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
		int posFinVentana = tamanioSegmento;
		Pixel iniVentana = contorno.get(posIniVentana);
		Pixel finVentana = contorno.get(posFinVentana);
		//Para la ecuacion de la recta
		
		boolean parar = false;
		int i = posFinVentana;
		int inicio = i;
		while (!parar && contorno.size() > tamanioSegmento){
			
			Pixel p = contorno.get(i % contorno.size());
			Pixel finVentana2 = contorno.get((i + tamanioSegmento) % contorno.size());

			double pendiente1 = 0;
			if (finVentana.getXDouble() - iniVentana.getXDouble() != 0)
				pendiente1 = (finVentana.getYDouble() - iniVentana.getYDouble()) / (finVentana.getXDouble() - iniVentana.getXDouble());
			else 
				pendiente1 = 1;
			
			double pendiente2 = 0;
			if (finVentana2.getXDouble() - p.getXDouble() != 0)
				pendiente2 = (finVentana2.getYDouble() - p.getYDouble()) / (finVentana2.getXDouble() - p.getXDouble());
			else
				pendiente2 = 1;
			
			double tgAngulo = Math.abs((pendiente2 - pendiente1) / (1 + pendiente2 * pendiente1));
			double angulo = Math.toDegrees(Math.atan(tgAngulo));
			if (Math.abs(angulo) > anguloDesvio ){
				cantCambiosDireccion++;
			}

			posIniVentana = (posIniVentana + 1) % contorno.size();
			posFinVentana = (posFinVentana + 1) % contorno.size();
			
			iniVentana = contorno.get(posIniVentana);
			finVentana = contorno.get(posFinVentana);
		
			i = (i + 1) % contorno.size();
			
			if (i == inicio)
				parar = true;
		}
		
		if (cantCambiosDireccion != 0)
			return (double) contorno.size() / cantCambiosDireccion;
		
		return 0.0;
	}
	public static void main(String[] args) {
		Objeto o = new Objeto();
		List<Pixel> contorno = new ArrayList<Pixel>();
		contorno.add(new Pixel(10,10,null));
		contorno.add(new Pixel(15,10,null));
		contorno.add(new Pixel(15,11,null));
		contorno.add(new Pixel(15,15,null));
		contorno.add(new Pixel(14,15,null));
		contorno.add(new Pixel(10,15,null));
		contorno.add(new Pixel(10,14,null));
		contorno.add(new Pixel(10,10,null));
		o.setContorno(contorno);
		
		Curvatura c = new Curvatura();
		double valor = c.calcularValor(o);
		System.out.println(valor);
	}
}
