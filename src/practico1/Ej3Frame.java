/*
 * Created by JFormDesigner on Sat May 30 23:43:38 GMT 2009
 */

package practico1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author User #3 oscar Gay
 */
public class Ej3Frame extends JFrame {
	public Ej3Frame() {
		initComponents();
	}

	private void thisWindowClosed(WindowEvent e) {
		System.exit(0);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();

		//======== this ========
		setTitle("Practico 1 - Ej 3");
		addWindowListener(new WindowAdapter() {
			 
			public void windowClosed(WindowEvent e) {
				thisWindowClosed(e);
			}
			 
			public void windowClosing(WindowEvent e) {
				thisWindowClosed(e);
			}
		});
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FlowLayout());
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
		Ej3Panel panel = new Ej3Panel();
		this.add(panel);
		this.setBackground(Color.WHITE);
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}


