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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.sun.media.jai.widget.DisplayJAI;

import objeto.Objeto;
import objeto.RasgoObjeto;

/**
 * @author User #3
 */
public class ObjetoPanel extends JPanel {
	public ObjetoPanel(Objeto objeto) {
		initComponents();
		PlanarImage image = JAI.create("fileload", objeto.getPathImage());
		this.add(new DisplayJAI(image),BorderLayout.CENTER);
		
		this.labelNombre.setText(objeto.getName());
		
		TableModel model = tableRasgos.getModel();
		for(int i=0;i<objeto.getRasgos().size();i++){
			RasgoObjeto rasgo = objeto.getRasgos().get(i);
			model.setValueAt(rasgo.getRasgo().getNombre(), i, 0);
			model.setValueAt(rasgo.getValor(), i, 1);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel1 = new JPanel();
		labelNombre = new JLabel();
		panelRasgos = new JPanel();
		scrollPaneRasgos = new JScrollPane();
		tableRasgos = new JTable();

		//======== this ========
		setLayout(new BorderLayout());

		//======== panel1 ========
		{
			panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

			//---- labelNombre ----
			labelNombre.setText("Nombre");
			panel1.add(labelNombre);
		}
		add(panel1, BorderLayout.NORTH);

		//======== panelRasgos ========
		{
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
				));
				tableRasgos.setPreferredScrollableViewportSize(new Dimension(200, 150));
				scrollPaneRasgos.setViewportView(tableRasgos);
			}
			panelRasgos.add(scrollPaneRasgos);
		}
		add(panelRasgos, BorderLayout.EAST);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel1;
	private JLabel labelNombre;
	private JPanel panelRasgos;
	private JScrollPane scrollPaneRasgos;
	private JTable tableRasgos;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
