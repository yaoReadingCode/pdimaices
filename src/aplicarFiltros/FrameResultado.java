package aplicarFiltros;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
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
		resultado.setContenedor(this);
		this.addPanel(resultado,"Resultado");
		
		for(EvaluadorClase c: clases){
			List<Objeto> objetosClase = getClasificador().getClasificacion().get(c);
			JPanel container = new JPanel();
			GridBagLayout gbl = new GridBagLayout();
			int cant = 0;
			float porcentaje = (objetosClase.size()*100)/getClasificador().countObject();
			resultado.addValueCount(c.getClase().getNombre(), objetosClase.size(),porcentaje);
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
				cantidadPixeles = cantidadPixeles + (obj.getPuntos().size() + obj.getContorno().size()); 
			}
			resultado.addValuePixel(c.getClase().getNombre(), cantidadPixeles);
			
			this.addPanel(container, c.getClase().getNombre());
		}
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
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		menuItem1 = new JMenuItem();
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

		//======== menuBar1 ========
		{

			//======== menu1 ========
			{
				menu1.setText("Archivo");

				//---- menuItem1 ----
				menuItem1.setText("Salir");
				menu1.add(menuItem1);
			}
			menuBar1.add(menu1);
		}
		setJMenuBar(menuBar1);

		//======== panel1 ========
		{
			panel1.setBorder(new BevelBorder(BevelBorder.RAISED));
			panel1.setBackground(new Color(204, 204, 255));

			// JFormDesigner evaluation mark
//			panel1.setBorder(new javax.swing.border.CompoundBorder(
//				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
//					"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
//					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
//					java.awt.Color.red), panel1.getBorder())); panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			panel1.setLayout(null);

			//======== panel3 ========
			{
				panel3.setBackground(Color.blue);
				panel3.setLayout(null);
				panel3.add(tabbedPane1);
				tabbedPane1.setBounds(5, 15, 750, 745);

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
			panel3.setBounds(10, 30, 795, 780);

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
				label1.setBounds(7, 15, 220, label1.getPreferredSize().height);

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
			panel2.setBounds(820, 30, 235, 275);

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
		panel1.setBounds(5, 10, 1080, 800);

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
	private JMenuBar menuBar1;
	private JMenu menu1;
	private JMenuItem menuItem1;
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
}
