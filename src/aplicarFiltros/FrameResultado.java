package aplicarFiltros;

import java.awt.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import objeto.Clase;
import objeto.Objeto;
import objeto.RasgoObjeto;
import procesamiento.clasificacion.Clasificador;
import procesamiento.clasificacion.EvaluadorClase;

public class FrameResultado extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Clasificador clasificador;
	private int cantidadPaneles = 1;	
	public void setResultados(){
		Set<EvaluadorClase> clases = getClasificador().getClasificacion().keySet();
		
		PanelResultado resultado = new PanelResultado();
		this.addPanel(resultado,"Resultado");
		
		for(EvaluadorClase c: clases){
			List<Objeto> objetosClase = getClasificador().getClasificacion().get(c);
			JPanel container = new JPanel();
			GridBagLayout gbl = new GridBagLayout();
			int cant = 0;
			resultado.addFila(c.getClase().getNombre(), objetosClase.size());
			container.setLayout(gbl);
			//frame.getContentPane().add(new JScrollPane(container),BorderLayout.CENTER);
			for (Objeto obj: objetosClase) {
				// ObjetoPanel jp = new ObjetoPanel(o);
				ObjetoPanel panel = new ObjetoPanel(obj,cant + 1, getClasificador(), this );
				//panel.setSize(200, 100);
				// JButton panel = new JButton("Boton");

				// Place a component at cell location (1,1)
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridy = cant / 3;// GridBagConstraints.RELATIVE;
				gbc.gridx = cant % 3;// GridBagConstraints.RELATIVE;
				gbc.gridheight = 1;
				gbc.gridwidth = 1;
				gbc.fill = GridBagConstraints.BOTH;

				// Associate the gridbag constraints with the component
				gbl.setConstraints(panel, gbc);

				// Add the component to the container
				container.add(panel);
				cant++;
			}
			
			
			this.addPanel(container, c.getClase().getNombre());
		}	
	}
	
	public void changeObjeto(Objeto obj, Clase claseNew){
		Set<EvaluadorClase> clases = getClasificador().getClasificacion().keySet();
		for(EvaluadorClase c: clases){
			if(c.getClase().equals(obj.getClases().get(0).getClase()))
				getClasificador().getClasificacion().get(c).remove(obj);
			else if(c.getClase().equals(claseNew))
				getClasificador().getClasificacion().get(c).add(obj);
			
		}
		tabbedPane1.removeAll();
		cantidadPaneles = 1;
		this.setResultados();
	}
	
	public Clasificador getClasificador() {
		return clasificador;
	}

	public void setClasificador(Clasificador clasificador) {
		this.clasificador = clasificador;
	}

	public void setNombreObjetSeleccionado(String nombre){
		this.label1.setText("Nombre: "+nombre);
	}
	
	
	
	public void presentarResultados(Objeto objeto){ 
		
		tableRasgos.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Rasgo", "Valor"
				}
			) {
				Class[] columnTypes = new Class[] {
					String.class, Double.class
				};
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
			});
		
		DefaultTableModel model = (DefaultTableModel) tableRasgos.getModel();
		
		for(int i=0;i<objeto.getRasgos().size();i++){
			RasgoObjeto rasgo = objeto.getRasgos().get(i);
			model.addRow(new Object[]{rasgo.getRasgo().getNombre(),rasgo.getValor()});
		}
		tableRasgos.setModel(model);
		this.setNombreObjetSeleccionado(objeto.getName());
	}
	
	
	public FrameResultado() {
		initComponents();
	}
	

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - seba cola
		tabbedPane1 = new JTabbedPane();
		panel2 = new JPanel();
		scrollPaneRasgos = new JScrollPane();
		tableRasgos = new JTable();
		label1 = new JLabel();
		panel3 = new JPanel();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(null);
		contentPane.add(tabbedPane1);
		tabbedPane1.setBounds(35, 25, 795, 520);

		//======== panel2 ========
		{
			panel2.setBackground(Color.blue);

			// JFormDesigner evaluation mark
			panel2.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), panel2.getBorder())); panel2.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			panel2.setLayout(null);

			//======== scrollPaneRasgos ========
			{

				//---- tableRasgos ----
				tableRasgos.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"Rasgo", "Valor"
					}
				) {
					Class[] columnTypes = new Class[] {
						String.class, Double.class
					};
					@Override
					public Class<?> getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});
				tableRasgos.setPreferredScrollableViewportSize(new Dimension(200, 100));
				tableRasgos.setBackground(UIManager.getColor("RadioButton.light"));
				tableRasgos.setCellSelectionEnabled(true);
				scrollPaneRasgos.setViewportView(tableRasgos);
			}
			panel2.add(scrollPaneRasgos);
			scrollPaneRasgos.setBounds(5, 35, 225, 235);

			//---- label1 ----
			label1.setText("Nombre: No se ha seleccionado maiz");
			label1.setForeground(Color.white);
			label1.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel2.add(label1);
			label1.setBounds(8, 15, 220, label1.getPreferredSize().height);

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for(int i = 0; i < panel2.getComponentCount(); i++) {
					Rectangle bounds = panel2.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				Insets insets = panel2.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				panel2.setMinimumSize(preferredSize);
				panel2.setPreferredSize(preferredSize);
			}
		}
		contentPane.add(panel2);
		panel2.setBounds(845, 20, 235, 275);

		//======== panel3 ========
		{
			panel3.setBackground(Color.blue);
			panel3.setLayout(null);

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for(int i = 0; i < panel3.getComponentCount(); i++) {
					Rectangle bounds = panel3.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				Insets insets = panel3.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				panel3.setMinimumSize(preferredSize);
				panel3.setPreferredSize(preferredSize);
			}
		}
		contentPane.add(panel3);
		panel3.setBounds(20, 20, 820, 535);

		{ // compute preferred size
			Dimension preferredSize = new Dimension();
			for(int i = 0; i < contentPane.getComponentCount(); i++) {
				Rectangle bounds = contentPane.getComponent(i).getBounds();
				preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
				preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
			}
			Insets insets = contentPane.getInsets();
			preferredSize.width += insets.right;
			preferredSize.height += insets.bottom;
			contentPane.setMinimumSize(preferredSize);
			contentPane.setPreferredSize(preferredSize);
		}
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - seba cola
	private JTabbedPane tabbedPane1;
	private JPanel panel2;
	private JScrollPane scrollPaneRasgos;
	private JTable tableRasgos;
	private JLabel label1;
	private JPanel panel3;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	private boolean tableIniciada=false;
	public void addPanel(JPanel panel, String name){
//		if(tableIniciada){
			JScrollPane scrollPanel = new JScrollPane(panel);
			scrollPanel.setPreferredSize(new Dimension(512,512));
			tabbedPane1.addTab(name, scrollPanel);
			cantidadPaneles++;
//		}
//		else{
//			tabbedPane1.removeAll();
//			JScrollPane scrollPanel = new JScrollPane(panel);
//			scrollPanel.setPreferredSize(new Dimension(512,512));
//			tabbedPane1.addTab(name, scrollPanel);
//			tableIniciada=true;
//			
//		}
	}

	public void setTableIniciada(boolean tableIniciada) {
		this.tableIniciada = tableIniciada;
	}

	public boolean isTableIniciada() {
		return tableIniciada;
	}
}
