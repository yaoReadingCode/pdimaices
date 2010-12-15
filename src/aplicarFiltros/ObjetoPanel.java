/*
 * Created by JFormDesigner on Sun Dec 05 20:47:07 GMT-03:00 2010
 */

package aplicarFiltros;

import java.awt.*;
import java.awt.BorderLayout;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.sun.media.jai.widget.DisplayJAI;

import objeto.Objeto;
import objeto.RasgoObjeto;

/**
 * @author User #3
 */
public class ObjetoPanel extends JPanel {
	Objeto objeto = null;
	public ObjetoPanel(Objeto objeto) {
		initComponents();
		this.objeto = objeto;
		PlanarImage image = JAI.create("fileload", objeto.getPathImage());
		
		this.panelImagen.add(new DisplayJAI(image),BorderLayout.CENTER);
		
		this.labelNombre.setText(objeto.getName());
		
		DefaultTableModel model = (DefaultTableModel) tableRasgos.getModel();
		
		for(int i=0;i<objeto.getRasgos().size();i++){
			RasgoObjeto rasgo = objeto.getRasgos().get(i);
			/*
			model.setValueAt(rasgo.getRasgo().getNombre(), i, 0);
			model.setValueAt(rasgo.getValor(), i, 1);*/
			
			model.addRow(new Object[]{rasgo.getRasgo().getNombre(),rasgo.getValor()});
		}
	}

	public Objeto getObjeto() {
		return objeto;
	}

	public void setObjeto(Objeto objeto) {
		this.objeto = objeto;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
		panel1 = new JPanel();
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
		setBorder(new MatteBorder(0, 0, 2, 0, new Color(153, 204, 255)));

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		setLayout(new BorderLayout());

		//======== panel1 ========
		{
			panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

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
				tableRasgos.setPreferredScrollableViewportSize(new Dimension(150, 100));
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
			panel2.add(labelClase);

			//---- comboBoxClase ----
			comboBoxClase.setModel(new DefaultComboBoxModel(new String[] {
				"MAIZ AMARILLO",
				"INDETERMINADO"
			}));
			panel2.add(comboBoxClase);

			//---- buttonGuardar ----
			buttonGuardar.setText("Guardar");
			panel2.add(buttonGuardar);
		}
		add(panel2, BorderLayout.SOUTH);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
	private JPanel panel1;
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
