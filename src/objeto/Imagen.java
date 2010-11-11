package objeto;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;



public class Imagen {
	private ArrayList<Pixel> puntos = new ArrayList<Pixel>();
	private String name="";
	
	
	public Imagen(){
		puntos = new ArrayList<Pixel>();
		
	}
	
	public int medida(){
		return puntos.size(); 
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	public void agregarPunto(Pixel p){
		puntos.add(p);

	}
	
	public void agregarPunto(int i, int j, Color c){
		Pixel p = new Pixel(i,j,c);
		puntos.add(p);
	}
	
	public void agregarPunto(int i, int j, int R, int G, int B){
		Pixel p = new Pixel(i,j,R,G,B);
		puntos.add(p);
	}

	public ArrayList<Pixel> getPuntos() {
		return puntos;
	}

	public Color colorPromedio(){
		int R = 0;
		int G = 0;
		int B = 0;
		Iterator<Pixel> i = puntos.iterator();
		while(i.hasNext()){
			Pixel p = (Pixel)i.next();
			R = R + p.getCol().getRed() ;
			G = G + p.getCol().getGreen();
			B = B + p.getCol().getBlue() ;
		}	
		R = (R / puntos.size()) % 255;
		G = (G / puntos.size()) % 255;
		B = (B / puntos.size()) % 255;
		Color c = new Color(R,G,B);
		return c;
		
	}
	

}
