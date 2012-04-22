package aplicarFiltros;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import objeto.Clase;
import objeto.ClaseObjeto;
import objeto.Objeto;
import objeto.RasgoObjeto;
import procesamiento.clasificacion.Clasificador;
import procesamiento.clasificacion.EvaluadorClase;
import procesamiento.clasificacion.EvaluadorClaseComparator;

public class FrameResultado extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Clasificador clasificador;
	private int cantidadPaneles = 1;
	private JFrame frameHistograma = null;
	private JPanel panelHistograma = null;
	private Objeto objetoSeleccionado = null;
	//private int selectedPanel = 0;
	public void setResultados(){
		Set<EvaluadorClase> clases = getClasificador().getClasificacion().keySet();
		List<EvaluadorClase> lClases = new ArrayList<EvaluadorClase>(clases);
		Collections.sort(lClases,new EvaluadorClaseComparator());
		
		PanelResultado resultado = new PanelResultado(getClasificador());
		resultado.setContenedor(this);
		this.addPanel(resultado,"Resultado");
		
		resultado.initDataSetModels();
		int cantObjPage = 4;
		
		for(EvaluadorClase c: lClases){
			List<Objeto> objetosClase = getClasificador().getClasificacion().get(c);
			JPanel container = new JPanel();
			GridBagLayout gbl = new GridBagLayout();
			int cant = 0;
			if (!c.getClase().isObjetoReferencia())
				resultado.addValueCount(c.getClase().getAgrupador(), objetosClase.size());
			container.setLayout(gbl);
			//frame.getContentPane().add(new JScrollPane(container),BorderLayout.CENTER);
			long cantidadPixeles = 0;
			for (Objeto obj: objetosClase) {
				// ObjetoPanel jp = new ObjetoPanel(o);
				ObjetoPanel panel = new ObjetoPanel(obj,cant + 1, getClasificador(), this );
				//panel.setSize(200, 100);
				// JButton panel = new JButton("Boton");

				// Place a component at cell location (1,1)
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridy = cant / cantObjPage;// GridBagConstraints.RELATIVE;
				gbc.gridx = cant % cantObjPage;// GridBagConstraints.RELATIVE;
				gbc.gridheight = 1;
				gbc.gridwidth = 1;
				gbc.fill = GridBagConstraints.BOTH;

				// Associate the gridbag constraints with the component
				gbl.setConstraints(panel, gbc);

				// Add the component to the container
				container.add(panel);
				cant++;
				cantidadPixeles = cantidadPixeles + (obj.getPuntos().size() + obj.getContorno().size()); 
			}
			if (!c.getClase().isObjetoReferencia())
				resultado.addValuePixel(c.getClase().getAgrupador(), cantidadPixeles);
			
			this.addPanel(container, c.getClase().getDescripcion());
		}
		resultado.actualizarDataSetCount();
		resultado.actualizarDataSetPixel();
		resultado.graficar();
	}
	
	public void changeObjeto(Objeto obj, Clase claseNew){
		Set<EvaluadorClase> clases = getClasificador().getClasificacion().keySet();
		for(EvaluadorClase c: clases){
			if(obj.getClases().size() > 0 && c.getClase().equals(obj.getClases().get(0).getClase())){
				getClasificador().getClasificacion().get(c).remove(obj);
				obj.removeClase(c.getClase());
			}
			else if(c.getClase().equals(claseNew)){
				getClasificador().getClasificacion().get(c).add(obj);
				obj.addClase(c.getClase());
			}
		}
		
		getClasificador().modificarClasificacion(obj);
		
		int selectedPanel = tabbedPane1.getSelectedIndex();
		Component panel = tabbedPane1.getComponentAt(selectedPanel);
		Point scrollPos = null;

		if (panel instanceof JScrollPane){
			scrollPos = ((JScrollPane)panel).getViewport().getViewPosition();
		}
		
		tabbedPane1.removeAll();
		cantidadPaneles = 1;
		this.setResultados();
		
		tabbedPane1.setSelectedIndex(selectedPanel);
		panel = tabbedPane1.getComponentAt(selectedPanel);
		if (panel instanceof JScrollPane){
			((JScrollPane)panel).getViewport().setViewPosition(scrollPos);
		}
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
		setObjetoSeleccionado(objeto);
		tableRasgos.setModel(new DefaultTableModel(
				new Object[][] {},
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
				
				public boolean isCellEditable(int row, int column) {
			        return false;
			    }
			});
		
		DefaultTableModel model = (DefaultTableModel) tableRasgos.getModel();
		Clase clase = ((ClaseObjeto)objeto.getClases().get(0)).getClase();
		for(int i=0;i<objeto.getRasgos().size();i++){
			RasgoObjeto rasgo = objeto.getRasgos().get(i);
			if (rasgo.getRasgo().getVisible()){
				if (rasgo.getClase() == null || rasgo.getClase().equals(clase))
					model.addRow(new Object[]{rasgo.getRasgo().getDescripcion(),(rasgo.getValor()!= null) ? rasgo.getValor():""});
				
			}
		}
		tableRasgos.setModel(model);
		/*
		tableRasgos.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int first = e.getFirstIndex();
				int last = e.getLastIndex();
				if (first == last){
					int row = tableRasgos.getSelectedRow();
					String nombreRasgo = (String)tableRasgos.getModel().getValueAt(row, 0);
					if (nombreRasgo != null && nombreRasgo.toUpperCase().startsWith("HISTOGRAMA")){
						showPanelHistograma();
					}
				}
				
			}
			
			
		});*/

		tableRasgos.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2){
					int row = tableRasgos.getSelectedRow();
					String nombreRasgo = (String)tableRasgos.getModel().getValueAt(row, 0);
					if (nombreRasgo != null && nombreRasgo.toUpperCase().startsWith("HISTOGRAMA")){
						showPanelHistograma();
					}
				}
			}
		}); 
		if(objeto.getPadre() == null){
			this.setNombreObjetSeleccionado(objeto.getName());
		}
		else{
			this.setNombreObjetSeleccionado(objeto.getName() + " - Padre: " + objeto.getPadre().getName());
		}
	}
	
	private void showPanelHistograma(){
		if (getObjetoSeleccionado() != null){
			Objeto objeto = getObjetoSeleccionado();
			Clase clase = ((ClaseObjeto)objeto.getClases().get(0)).getClase();
			PanelHistogramaContainer panel = new PanelHistogramaContainer(objeto,clase,objeto.getName(), "Clase: " + clase.getDescripcion());
			if (panelHistograma != null){
				frameHistograma.remove(panelHistograma);
			}
			panelHistograma = panel;
			frameHistograma.add(panelHistograma);
			frameHistograma.pack();
			frameHistograma.setResizable(true);
			frameHistograma.setVisible(true);
		}
		
	}
	public FrameResultado() {
		initComponents();
	}

	private void tabbedPane1StateChanged(ChangeEvent e) {
		//JTabbedPane pane = (JTabbedPane)e.getSource();

        // Get current tab
        //selectedPanel = pane.getSelectedIndex();
	}
	

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Sebastian Colavita
		panel1 = new JPanel();
		panel3 = new JPanel();
		tabbedPane1 = new JTabbedPane();
		panel2 = new JPanel();
		scrollPaneRasgos = new JScrollPane();
		tableRasgos = new JTable();
		label1 = new JLabel();

		//======== this ========
		setBackground(new Color(5, 21, 64));
		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		//======== panel1 ========
		{
			panel1.setBorder(new BevelBorder(BevelBorder.RAISED));
			panel1.setBackground(new Color(204, 204, 255));

			// JFormDesigner evaluation mark
			panel1.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), panel1.getBorder())); panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			panel1.setLayout(null);

			//======== panel3 ========
			{
				panel3.setBackground(Color.blue);
				panel3.setLayout(null);

				//======== tabbedPane1 ========
				{
					tabbedPane1.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent e) {
							tabbedPane1StateChanged(e);
						}
					});
				}
				panel3.add(tabbedPane1);
				tabbedPane1.setBounds(5, 5, 910, 650);

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
			panel1.add(panel3);
			panel3.setBounds(5, 5, 920, 660);

			//======== panel2 ========
			{
				panel2.setBackground(Color.blue);
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
						Class<?>[] columnTypes = new Class<?>[] {
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
				scrollPaneRasgos.setBounds(5, 25, 235, 628);

				//---- label1 ----
				label1.setText("Nombre: ...");
				label1.setForeground(Color.white);
				label1.setFont(new Font("Tahoma", Font.BOLD, 12));
				panel2.add(label1);
				label1.setBounds(7, 5, 185, label1.getPreferredSize().height);

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
			panel1.add(panel2);
			panel2.setBounds(930, 5, 245, 660);

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for(int i = 0; i < panel1.getComponentCount(); i++) {
					Rectangle bounds = panel1.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				Insets insets = panel1.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				panel1.setMinimumSize(preferredSize);
				panel1.setPreferredSize(preferredSize);
			}
		}
		contentPane.add(panel1);
		panel1.setBounds(0, 0, 1175, 670);

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
		
		frameHistograma = new JFrame();
		frameHistograma.setSize(PanelHistogramaContainer.PANEL_WIDTH, PanelHistogramaContainer.PANEL_HEIGHT);
		frameHistograma.setTitle("Histrogramas");
		frameHistograma.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frameHistograma.setLocationRelativeTo(null);
	}
	
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Sebastian Colavita
	private JPanel panel1;
	private JPanel panel3;
	private JTabbedPane tabbedPane1;
	private JPanel panel2;
	private JScrollPane scrollPaneRasgos;
	private JTable tableRasgos;
	private JLabel label1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	private boolean tableIniciada=false;
	public void addPanel(JPanel panel, String name){
			JScrollPane scrollPanel = new JScrollPane(panel);
			scrollPanel.setPreferredSize(new Dimension(700,512));
			tabbedPane1.addTab(name, scrollPanel);
			cantidadPaneles++;
	}

	public void setTableIniciada(boolean tableIniciada) {
		this.tableIniciada = tableIniciada;
	}

	public boolean isTableIniciada() {
		return tableIniciada;
	}

	public Objeto getObjetoSeleccionado() {
		return objetoSeleccionado;
	}

	public void setObjetoSeleccionado(Objeto objetoSeleccionado) {
		this.objetoSeleccionado = objetoSeleccionado;
	}
	
}
