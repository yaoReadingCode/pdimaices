/*
 * Created by JFormDesigner on Sun Dec 05 20:47:07 GMT-03:00 2010
 */

package aplicarFiltros;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.sun.media.jai.widget.DisplayJAI;

import objeto.Clase;
import objeto.ClaseObjeto;
import objeto.Objeto;
import objeto.RasgoObjeto;
import procesamiento.clasificacion.Clasificador;

/**
 * @author User #3
 */
public class ObjetoPanel extends JPanel {
	private Objeto objeto = null;
	private Clasificador clasificador = null;
	public ObjetoPanel(Objeto objeto, int nroPanel, Clasificador clasificador) {
		initComponents();
		this.objeto = objeto;
		this.clasificador = clasificador;
		PlanarImage image = JAI.create("fileload", objeto.getPathImage());
		
		this.panelImagen.add(new DisplayJAI(image),BorderLayout.CENTER);
		
		this.labelNombre.setText(objeto.getName());
		this.labelNro.setText(Integer.toString(nroPanel));
		
		DefaultTableModel model = (DefaultTableModel) tableRasgos.getModel();
		
		for(int i=0;i<objeto.getRasgos().size();i++){
			RasgoObjeto rasgo = objeto.getRasgos().get(i);
			/*
			model.setValueAt(rasgo.getRasgo().getNombre(), i, 0);
			model.setValueAt(rasgo.getValor(), i, 1);*/
			
			model.addRow(new Object[]{rasgo.getRasgo().getNombre(),rasgo.getValor()});
		}
		
		//---- comboBoxClase ----
		DefaultComboBoxModel claseModel = new DefaultComboBoxModel();  
		if (clasificador != null){
			List<Clase> clases = clasificador.getClases();
			for(Clase c:clases){
				claseModel.addElement(c);
			}
		}

		comboBoxClase.setModel(claseModel);

		ClaseObjeto clase = objeto.getClases().get(0);
		this.comboBoxClase.setSelectedItem(clase.getClase());
	}

	public Objeto getObjeto() {
		return objeto;
	}

	public void setObjeto(Objeto objeto) {
		this.objeto = objeto;
	}

	public Clasificador getClasificador() {
		return clasificador;
	}

	public void setClasificador(Clasificador clasificador) {
		this.clasificador = clasificador;
	}

	private void buttonGuardarActionPerformed(ActionEvent e) {
		Clase clase = (Clase) this.comboBoxClase.getSelectedItem();
		ClaseObjeto claseObjeto = this.objeto.getClases().get(0);
		claseObjeto.setClase(clase);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - oscar giorgetti
		panel1 = new JPanel();
		labelNro = new JLabel();
		labelNombre = new JLabel();
		panelRasgos = new JPanel();
		scrollPaneRasgos = new JScrollPane();
		tableRasgos = new JTable();
		panelImagen = new JPanel();
		panel2 = new JPanel();
		labelClase = new JLabel();
		comboBoxClase = new JComboBox();
		buttonGuardar = new JButton();

		//======== this ========
		setBorder(new LineBorder(new Color(153, 204, 255)));

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		setLayout(new BorderLayout());

		//======== panel1 ========
		{
			panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

			//---- labelNro ----
			labelNro.setText("1");
			panel1.add(labelNro);

			//---- labelNombre ----
			labelNombre.setText("Nombre");
			labelNombre.setFont(labelNombre.getFont().deriveFont(labelNombre.getFont().getStyle() | Font.BOLD));
			panel1.add(labelNombre);
		}
		add(panel1, BorderLayout.NORTH);

		//======== panelRasgos ========
		{
			panelRasgos.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
			panelRasgos.setLayout(new BoxLayout(panelRasgos, BoxLayout.Y_AXIS));

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
			panelRasgos.add(scrollPaneRasgos);
		}
		add(panelRasgos, BorderLayout.EAST);

		//======== panelImagen ========
		{
			panelImagen.setBorder(UIManager.getBorder("ProgressBar.border"));
			panelImagen.setLayout(new BoxLayout(panelImagen, BoxLayout.X_AXIS));
		}
		add(panelImagen, BorderLayout.CENTER);

		//======== panel2 ========
		{
			panel2.setLayout(new FlowLayout(FlowLayout.LEFT));

			//---- labelClase ----
			labelClase.setText("Clase:");
			labelClase.setFont(labelClase.getFont().deriveFont(labelClase.getFont().getStyle() | Font.BOLD));
			panel2.add(labelClase);
			panel2.add(comboBoxClase);

			//---- buttonGuardar ----
			buttonGuardar.setText("Guardar");
			buttonGuardar.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					buttonGuardarActionPerformed(e);
				}
			});
			panel2.add(buttonGuardar);
		}
		add(panel2, BorderLayout.SOUTH);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - oscar giorgetti
	private JPanel panel1;
	private JLabel labelNro;
	private JLabel labelNombre;
	private JPanel panelRasgos;
	private JScrollPane scrollPaneRasgos;
	private JTable tableRasgos;
	private JPanel panelImagen;
	private JPanel panel2;
	private JLabel labelClase;
	private JComboBox comboBoxClase;
	private JButton buttonGuardar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
