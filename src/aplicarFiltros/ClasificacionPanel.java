/*
 * Created by JFormDesigner on Tue Dec 07 11:59:08 GMT-03:00 2010
 */

package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import objeto.Clase;
import objeto.Objeto;
import objeto.Rasgo;
import objeto.RasgoObjeto;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.EvaluadorRasgo;

/**
 * @author Oscar Giorgetti
 */
public class ClasificacionPanel extends JPanel {
	
	private Map<EvaluadorClase, List<Objeto>> clasificacion = new HashMap<EvaluadorClase, List<Objeto>>();
	
	public ClasificacionPanel(Map<EvaluadorClase, List<Objeto>> clasificacion) {
		initComponents();
		
		setClasificacion(clasificacion);
		
		Set<EvaluadorClase> clases = clasificacion.keySet();
		for(EvaluadorClase c: clases){
			Clase clase = c.getClase();
			List<Objeto> objetosClase = clasificacion.get(c);
			
			ClasePanel panel = new ClasePanel(clase,objetosClase);
			panel.setPreferredSize(new Dimension(500, 200));
			panel.invalidate();
			this.panelClases.add(panel);
		}		

	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
		scrollPaneClases = new JScrollPane();
		panelClases = new JPanel();

		//======== this ========

		setLayout(new BorderLayout());

		//======== scrollPaneClases ========
		{
			scrollPaneClases.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//======== panelClases ========
			{
				panelClases.setLayout(new BoxLayout(panelClases, BoxLayout.Y_AXIS));
			}
			scrollPaneClases.setViewportView(panelClases);
		}
		add(scrollPaneClases, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	public Map<EvaluadorClase, List<Objeto>> getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(Map<EvaluadorClase, List<Objeto>> clasificacion) {
		this.clasificacion = clasificacion;
	}

	 public static void main(String[] args) {
		   JFrame frame = new JFrame();
		   String path = "image/";
		   File directorio = new File(path);
		   String [] ficheros = directorio.list();
		   List<Objeto> objetos = new ArrayList<Objeto>();
		   for (int i = 0; i < ficheros.length; i++) {
			   if (ficheros[i].endsWith(".tif")){
				   Objeto o = new Objeto();
				   o.setPathImage(path+ficheros[i]);
				   o.setName(ficheros[i]);
				   List<RasgoObjeto> rasgos = new ArrayList<RasgoObjeto>();
				   rasgos.add(new RasgoObjeto(new Rasgo("AREA"),1.0));
				   o.setRasgos(rasgos);
				   objetos.add(o);
			   }
		   }

		   EvaluadorClase ec = new EvaluadorClase(new Clase("MAIZ"),new ArrayList<EvaluadorRasgo>());
		   
		   Map<EvaluadorClase, List<Objeto>> clasificacion = new HashMap<EvaluadorClase, List<Objeto>>();
		   clasificacion.put(ec,objetos);
		   
		   ClasificacionPanel panel = new ClasificacionPanel(clasificacion);
		   frame.add(panel);
		   
		   //frame.getContentPane().add(new JLabel(new ImageIcon("Lighthouse.jpg")));
		   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		   frame.setSize(700,600);
		   
		   //frame.pack();
		   frame.setVisible(true);
	 }
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
	private JScrollPane scrollPaneClases;
	private JPanel panelClases;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	
}
