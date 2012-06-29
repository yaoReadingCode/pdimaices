/*
 * Created by JFormDesigner on Tue Dec 07 12:32:06 GMT-03:00 2010
 */

package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import objeto.Clase;
import objeto.Objeto;
import procesamiento.clasificacion.ClaseComparator;
import procesamiento.clasificacion.Clasificador;
import procesamiento.clasificacion.EvaluadorClase;

/**
 * @author Oscar Giorgetti
 */
public class ClasePanel extends JPanel {
	
	public Clase clase;
	private FrameResultado contenedor = null;
	private List<Objeto> seleccionados = new ArrayList<Objeto>();
	private Clasificador clasificador = null;
	private int cantObjPage = 4;
	
	private JPanel panelOpciones;
	private JComboBox comboBoxClase;
	private JCheckBox checkSelectAll;
	private JButton buttonEliminar;
	
	JPanel panelObjetos;

	public ClasePanel(Clase clase, FrameResultado contenedor, Clasificador clasificador, int cantObjetosPage) {
		super();
		this.clase = clase;
		this.contenedor = contenedor;
		this.clasificador = clasificador;
		this.cantObjPage = cantObjetosPage;
		initComponents();
	}
	
	private void initComponents(){
		this.setLayout(new BorderLayout());
		
		panelObjetos = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		int cant = 0;
		List<Objeto> objetosClase = getObjetosClase();

		panelObjetos.setLayout(gbl);
		//frame.getContentPane().add(new JScrollPane(container),BorderLayout.CENTER);
		long cantidadPixeles = 0;
		for (Objeto obj: objetosClase) {
			// ObjetoPanel jp = new ObjetoPanel(o);
			try{
				ObjetoPanel panel = new ObjetoPanel(obj,cant + 1, clasificador, this);
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
				panelObjetos.add(panel);
				cant++;
				cantidadPixeles = cantidadPixeles + (obj.getPuntos().size() + obj.getContorno().size());
			}
			catch (Exception e) {
				System.err.println("Error creando panel objeto");
				e.printStackTrace();
			}
		}
		if (!getClase().isObjetoReferencia()){
			String agrupador = (getClase().getRubroCalidad() != null) ? getClase().getRubroCalidad().getDescripcion(): getClase().getDescripcion();
			getContenedor().getPanelResultado().addValuePixel(agrupador, cantidadPixeles, cant);
		}
		JScrollPane scrollPanelObjetos = new JScrollPane(panelObjetos);
		scrollPanelObjetos.setPreferredSize(computePreferredSize(panelObjetos));
		
		this.add(createPanelOpciones(), BorderLayout.NORTH);
		this.add(scrollPanelObjetos, BorderLayout.CENTER);
	}
	
	/**
	 * Calcula el preferred size de un panel
	 * @param panel
	 * @return
	 */
	private Dimension computePreferredSize(JPanel panel){
		// compute preferred size
		Dimension preferredSize = new Dimension();
		for(int i = 0; i < panel.getComponentCount(); i++) {
			Rectangle bounds = panel.getComponent(i).getBounds();
			preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
			preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
		}
		Insets insets = panel.getInsets();
		preferredSize.width += insets.right;
		preferredSize.height += insets.bottom;
		return preferredSize;
	}
	/**
	 * Crea el panel con las opciones seleccionar todos, eliminar, etc.
	 */
	private JPanel createPanelOpciones(){
		panelOpciones = new JPanel();
		panelOpciones.setBorder(BorderFactory.createLineBorder(SystemColor.textHighlight, 1));
		panelOpciones.setLayout(new BoxLayout(panelOpciones,BoxLayout.X_AXIS));
		
		JPanel panelRight = new JPanel();
		panelRight.setLayout(new FlowLayout(FlowLayout.RIGHT,5,5));
		
		checkSelectAll = new JCheckBox();
		checkSelectAll.setText("Seleccionar Todos");
		checkSelectAll.setToolTipText("Marcar/Desmarcar Todos");
		checkSelectAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionarTodos(e);
			}
		});

		buttonEliminar = new JButton();
		buttonEliminar.setToolTipText("Eliminar Seleccionados");
		buttonEliminar.setIcon(new ImageIcon("img\\eliminar.png"));
		buttonEliminar.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonEliminarActionPerformed(e);
			}
		});
		
		panelRight.add(checkSelectAll);
		panelRight.add(buttonEliminar);
		panelRight.add(getComboBoxClase());
		
		panelOpciones.add(panelRight);
		return panelOpciones;
	}
	
	/**
	 * Selecciona/Deselecciona todos los objetos de una clase
	 */
	private void seleccionarTodos(ActionEvent e) {
		boolean selected = checkSelectAll.isSelected();
		seleccionados.clear();
		if (selected)
			seleccionados.addAll(getObjetosClase());
		
		for(Objeto obj: getObjetosClase()){
			obj.setSelected(selected);
		}
			
		for(Component panelObjeto: panelObjetos.getComponents()){
			panelObjeto.invalidate();
			panelObjeto.repaint();
		}
	}
	
	/**
	 * Retorna los objetos asignados a la clase
	 * @return
	 */
	private List<Objeto> getObjetosClase(){
		EvaluadorClase key = new EvaluadorClase(this.clase,null);
		List<Objeto> objetosClase =this.clasificador.getClasificacion().get(key);
		if (objetosClase != null)
			return objetosClase;
		return new ArrayList<Objeto>();
	}
	
	private void comboBoxClaseActionPerformed(ActionEvent e) {
		Clase selectedClase = (Clase)this.comboBoxClase.getSelectedItem();
		if (selectedClase != null && selectedClase.getId() != null){
			int n = JOptionPane.showConfirmDialog(this.getContenedor(),
					"Esta seguro que desea asignar los objetos seleccionados a la clase: " + selectedClase.getDescripcion() + "?",
				    "Confirmación",
				    JOptionPane.OK_CANCEL_OPTION);
			if (n == 0)
				this.contenedor.changeObjeto(getSeleccionados(), selectedClase);
			
		}
	}
	
	private void buttonEliminarActionPerformed(ActionEvent e) {
		int n = JOptionPane.showConfirmDialog(this.getContenedor(),
				"Esta seguro que desea eliminar los objetos seleccionados?",
			    "Confirmación",
			    JOptionPane.OK_CANCEL_OPTION);
		if (n == 0){
			List<Objeto> objetosClase = getObjetosClase();
			objetosClase.removeAll(getSeleccionados());
			this.getContenedor().actualizarPanel();
		}
	}
	
	public JComboBox getComboBoxClase() {
		if (comboBoxClase == null){
			comboBoxClase = new JComboBox();
			List<Clase> clases;
			Clase noSelect = new Clase("Modificar Clasificación","Modificar Clasificación");

			//---- comboBoxClase ----
			DefaultComboBoxModel claseModel = new DefaultComboBoxModel();
			claseModel.addElement(noSelect);
			if (clasificador != null){
				clases = new ArrayList<Clase>(clasificador.getClases());
				Collections.sort(clases, new ClaseComparator());
				for(Clase c:clases){
					if(!getClase().equals(c))
						claseModel.addElement(c);
				}
			}
			comboBoxClase.setModel(claseModel);
			this.comboBoxClase.setSelectedItem(noSelect);
			
			comboBoxClase.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					comboBoxClaseActionPerformed(e);
				}
			});
		}
		return comboBoxClase;
	}

	public Clase getClase() {
		return clase;
	}

	public void setClase(Clase clase) {
		this.clase = clase;
	}

	public FrameResultado getContenedor() {
		return contenedor;
	}

	public void setContenedor(FrameResultado contenedor) {
		this.contenedor = contenedor;
	}

	public List<Objeto> getSeleccionados() {
		return seleccionados;
	}

	public void setSeleccionados(List<Objeto> seleccionados) {
		this.seleccionados = seleccionados;
	}
	
	/**
	 * Agrega un objeto a la lista de seleccionados si es que ya no esta agregado
	 * @param obj
	 */
	public void agregarSeleccionado(Objeto obj){
		obj.setSelected(true);
		if (!getSeleccionados().contains(obj))
			getSeleccionados().add(obj);
	}
	
	/**
	 * Eliminar un objeto de la lista de seleccionados si es que esta agregado
	 * @param obj
	 */
	public void eliminarSeleccionado(Objeto obj){
		obj.setSelected(false);
		if (getSeleccionados().contains(obj))
			getSeleccionados().remove(obj);
	}
}
