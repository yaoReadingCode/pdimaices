/*
 * Created by JFormDesigner on Fri Dec 17 11:07:47 GMT-03:00 2010
 */

package aplicarFiltros;

import java.awt.*;
import javax.swing.*;

/**
 * @author Oscar Giorgetti
 */
public class PreviewObjetoPanel extends JPanel {
	public PreviewObjetoPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
		panelImagen = new JPanel();
		panelDescripcion = new JPanel();
		panelClase = new JPanel();
		labelClase = new JLabel();
		label1 = new JLabel();
		panelCabecera = new JPanel();
		labelNombre = new JLabel();

		//======== this ========

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		setLayout(new BorderLayout());

		//======== panelImagen ========
		{
			panelImagen.setLayout(new BoxLayout(panelImagen, BoxLayout.Y_AXIS));
		}
		add(panelImagen, BorderLayout.CENTER);

		//======== panelDescripcion ========
		{
			panelDescripcion.setLayout(new BoxLayout(panelDescripcion, BoxLayout.Y_AXIS));

			//======== panelClase ========
			{
				panelClase.setLayout(new FlowLayout(FlowLayout.LEFT));

				//---- labelClase ----
				labelClase.setText("Clase:");
				labelClase.setFont(labelClase.getFont().deriveFont(labelClase.getFont().getStyle() | Font.BOLD));
				panelClase.add(labelClase);

				//---- label1 ----
				label1.setText("Nombre Clase");
				panelClase.add(label1);
			}
			panelDescripcion.add(panelClase);
		}
		add(panelDescripcion, BorderLayout.WEST);

		//======== panelCabecera ========
		{
			panelCabecera.setLayout(new FlowLayout(FlowLayout.LEFT));

			//---- labelNombre ----
			labelNombre.setText("Nombre");
			labelNombre.setFont(new Font("Tahoma", Font.BOLD, 11));
			panelCabecera.add(labelNombre);
		}
		add(panelCabecera, BorderLayout.NORTH);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
	private JPanel panelImagen;
	private JPanel panelDescripcion;
	private JPanel panelClase;
	private JLabel labelClase;
	private JLabel label1;
	private JPanel panelCabecera;
	private JLabel labelNombre;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
