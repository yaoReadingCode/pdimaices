package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import objeto.Clase;
import objeto.ClaseObjeto;
import objeto.Objeto;
import objeto.Pixel;
import objeto.Rasgo;
import objeto.RasgoObjeto;

public class Test {
	public static void main(String[] args) {
		Pixel iniVentana1 = new Pixel(0,0,null);
		Pixel finVentana1 = new Pixel(-3,-3,null);
		
		Pixel iniVentana2 = new Pixel(0,0,null);
		Pixel finVentana2 = new Pixel(1,2,null);
		
		Pixel p1 = finVentana1.clonar();
		p1.restar(iniVentana1);

		Pixel p2 = finVentana2.clonar();
		p2.restar(iniVentana2);
		
		double cosAngulo = (p1.getX()* p2.getX() + p1.getY() * p2.getY())/(p1.modulo()*p2.modulo());
		double angulo = Math.acos(cosAngulo);
		
		System.out.println(Math.toDegrees(angulo));
	}
}
