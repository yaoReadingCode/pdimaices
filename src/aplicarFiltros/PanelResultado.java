/*
 * Created by JFormDesigner on Mon Jan 31 22:55:17 ART 2011
 */

package aplicarFiltros;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * @author seba cola
 */
public class PanelResultado extends JPanel {
	public void addFila(String nombre, int cantidad, float porcentaje){
		DefaultTableModel model = (DefaultTableModel) tableRasgos2.getModel();
		model.addRow(new Object[]{nombre, cantidad, (porcentaje+"%")});
	}
	public PanelResultado() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - seba cola
		scrollPaneRasgos2 = new JScrollPane();
		tableRasgos2 = new JTable();

		//======== this ========

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		setLayout(null);

		//======== scrollPaneRasgos2 ========
		{

			//---- tableRasgos2 ----
			tableRasgos2.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Clasificaciones", "Cantidad de Objetos", "Porcentaje"
				}
			) {
				Class[] columnTypes = new Class[] {
					String.class, String.class, Object.class
				};
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
			});
			tableRasgos2.setPreferredScrollableViewportSize(new Dimension(200, 100));
			tableRasgos2.setBackground(UIManager.getColor("RadioButton.light"));
			tableRasgos2.setCellSelectionEnabled(true);
			scrollPaneRasgos2.setViewportView(tableRasgos2);
		}
		add(scrollPaneRasgos2);
		scrollPaneRasgos2.setBounds(15, 145, 655, 160);

		{ // compute preferred size
			Dimension preferredSize = new Dimension();
			for(int i = 0; i < getComponentCount(); i++) {
				Rectangle bounds = getComponent(i).getBounds();
				preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
				preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
			}
			Insets insets = getInsets();
			preferredSize.width += insets.right;
			preferredSize.height += insets.bottom;
			setMinimumSize(preferredSize);
			setPreferredSize(preferredSize);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - seba cola
	private JScrollPane scrollPaneRasgos2;
	private JTable tableRasgos2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
