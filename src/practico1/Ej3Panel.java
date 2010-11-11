/*
 * Created by JFormDesigner on Sat May 30 23:45:36 GMT 2009
 */

package practico1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import info.clearthought.layout.*;

/**
 * @author User #3
 */
public class Ej3Panel extends JPanel {
	public Ej3Panel() {
		initComponents();
	}

	private void nivelesGrisChanged(ActionEvent e) {
		JRadioButton rbSource = (JRadioButton) e.getSource();
		String strNivelGris = rbSource.getText();
		graphicComponent1.setNivelesGris(Integer.parseInt(strNivelGris));
		graphicComponent1.invalidate();
		graphicComponent1.repaint();
	}

	private void anchoChanged(ActionEvent e) {
		JRadioButton rbSource = (JRadioButton) e.getSource();
		String strAncho = rbSource.getText();
		graphicComponent1.setWidth(Integer.parseInt(strAncho));
		graphicComponent1.invalidate();
		graphicComponent1.repaint();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		splitPane1 = new JSplitPane();
		panel1 = new JPanel();
		panel3 = new JPanel();
		rbAncho1024 = new JRadioButton();
		rbAncho512 = new JRadioButton();
		rbAncho256 = new JRadioButton();
		rbancho128 = new JRadioButton();
		panel2 = new JPanel();
		rbGris256 = new JRadioButton();
		rbGris128 = new JRadioButton();
		rbGris32 = new JRadioButton();
		rbGris8 = new JRadioButton();
		rbGris2 = new JRadioButton();
		panel4 = new JPanel();
		graphicComponent1 = new GraphicComponent();

		//======== this ========
		setLayout(null);

		//======== splitPane1 ========
		{
			splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane1.setResizeWeight(0.2);
			splitPane1.setContinuousLayout(true);

			//======== panel1 ========
			{
				panel1.setBorder(null);
				panel1.setLayout(null);

				//======== panel3 ========
				{
					panel3.setBorder(new TitledBorder(null, "Ancho", TitledBorder.LEADING, TitledBorder.TOP, null, Color.blue));
					panel3.setLayout(null);

					//---- rbAncho1024 ----
					rbAncho1024.setText("1024");
					rbAncho1024.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							anchoChanged(e);
						}
					});
					panel3.add(rbAncho1024);
					rbAncho1024.setBounds(10, 20, 95, 23);

					//---- rbAncho512 ----
					rbAncho512.setText("512");
					rbAncho512.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							anchoChanged(e);
						}
					});
					panel3.add(rbAncho512);
					rbAncho512.setBounds(10, 40, 90, 23);

					//---- rbAncho256 ----
					rbAncho256.setText("256");
					rbAncho256.setSelected(true);
					rbAncho256.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							anchoChanged(e);
						}
					});
					panel3.add(rbAncho256);
					rbAncho256.setBounds(10, 60, 85, 23);

					//---- rbancho128 ----
					rbancho128.setText("128");
					rbancho128.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							anchoChanged(e);
						}
					});
					panel3.add(rbancho128);
					rbancho128.setBounds(10, 80, 85, 23);

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
						panel3.setPreferredSize( preferredSize );
					}
				}
				panel1.add(panel3);
				panel3.setBounds(220, 5, 205, 135);

				//======== panel2 ========
				{
					panel2.setBorder(new TitledBorder(null, "Niveles de gris", TitledBorder.LEADING, TitledBorder.TOP, null, Color.blue));
					panel2.setLayout(null);

					//---- rbGris256 ----
					rbGris256.setText("256");
					rbGris256.setSelected(true);
					rbGris256.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							nivelesGrisChanged(e);
						}
					});
					panel2.add(rbGris256);
					rbGris256.setBounds(10, 20, 80, 23);

					//---- rbGris128 ----
					rbGris128.setText("128");
					rbGris128.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							nivelesGrisChanged(e);
						}
					});
					panel2.add(rbGris128);
					rbGris128.setBounds(10, 40, 75, 23);

					//---- rbGris32 ----
					rbGris32.setText("32");
					rbGris32.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							nivelesGrisChanged(e);
						}
					});
					panel2.add(rbGris32);
					rbGris32.setBounds(10, 60, 75, 23);

					//---- rbGris8 ----
					rbGris8.setText("8");
					rbGris8.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							nivelesGrisChanged(e);
						}
					});
					panel2.add(rbGris8);
					rbGris8.setBounds(10, 80, 80, 23);

					//---- rbGris2 ----
					rbGris2.setText("2");
					rbGris2.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							nivelesGrisChanged(e);
						}
					});
					panel2.add(rbGris2);
					rbGris2.setBounds(10, 100, 75, 23);

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
						panel2.setPreferredSize( preferredSize );
					}
				}
				panel1.add(panel2);
				panel2.setBounds(5, 5, 210, 135);

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
					panel1.setPreferredSize( preferredSize );
				}
			}
			splitPane1.setTopComponent(panel1);

			//======== panel4 ========
			{
				panel4.setLayout(null);
				panel4.add(graphicComponent1);
				graphicComponent1.setBounds(new Rectangle(new Point(5, 5), graphicComponent1.getPreferredSize()));

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel4.getComponentCount(); i++) {
						Rectangle bounds = panel4.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel4.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel4.setPreferredSize( preferredSize );
				}
			}
			splitPane1.setBottomComponent(panel4);
		}
		add(splitPane1);
		splitPane1.setBounds(10, 40, 1005, 320);

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
			setPreferredSize( preferredSize );
		}

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(rbAncho1024);
		buttonGroup2.add(rbAncho512);
		buttonGroup2.add(rbAncho256);
		buttonGroup2.add(rbancho128);

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(rbGris256);
		buttonGroup1.add(rbGris128);
		buttonGroup1.add(rbGris32);
		buttonGroup1.add(rbGris8);
		buttonGroup1.add(rbGris2);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
		graphicComponent1.setNivelesGris(256);
		graphicComponent1.setWidth(256);
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JSplitPane splitPane1;
	private JPanel panel1;
	private JPanel panel3;
	private JRadioButton rbAncho1024;
	private JRadioButton rbAncho512;
	private JRadioButton rbAncho256;
	private JRadioButton rbancho128;
	private JPanel panel2;
	private JRadioButton rbGris256;
	private JRadioButton rbGris128;
	private JRadioButton rbGris32;
	private JRadioButton rbGris8;
	private JRadioButton rbGris2;
	private JPanel panel4;
	private GraphicComponent graphicComponent1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
