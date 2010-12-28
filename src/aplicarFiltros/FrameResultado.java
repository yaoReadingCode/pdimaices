package aplicarFiltros;

import java.awt.*;
import javax.swing.*;

public class FrameResultado extends JFrame {
	public FrameResultado() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - seba cola
		tabbedPane1 = new JTabbedPane();
		panel1 = new JPanel();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		//======== tabbedPane1 ========
		{

			//======== panel1 ========
			{

				// JFormDesigner evaluation mark
				panel1.setBorder(new javax.swing.border.CompoundBorder(
					new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
						"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
						javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
						java.awt.Color.red), panel1.getBorder())); panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

				panel1.setLayout(null);

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
			tabbedPane1.addTab("text", panel1);

		}
		contentPane.add(tabbedPane1);
		tabbedPane1.setBounds(35, 25, 1000, 525);

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
	private JPanel panel1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	private boolean tableIniciada=false;
	public void addPanel(JPanel panel, String name){
		if(tableIniciada)
			tabbedPane1.addTab(name, panel);
		else{
			tabbedPane1.removeAll();
			tabbedPane1.addTab(name, panel);
			tableIniciada=true;
			
		}
	}
}
