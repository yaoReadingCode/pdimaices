/*
 * Created by JFormDesigner on Sun Apr 18 10:32:30 VET 2010
 */

package aplicarFiltros;

import java.awt.*;
import javax.swing.*;

/**
 * @author seba
 */
public class Progress extends JPanel {
	public Progress() {
		initComponents();
	}
	public void aumentar(int valor, String texto){
		int v = valor + progressBar1.getValue();
		if(v > 100){
			window1.setVisible(false);
		}else{
			progressBar1.setValue(v);
			label1.setText("Tarea: " + texto);
			
			progressBar1.invalidate();
			progressBar1.repaint();

		}
		System.out.println("Es visible: " +window1.isVisible());
	}

	public void inicializarProgressBar(){
		progressBar1.setValue(0);
		label1.setText("Tarea:");
	}
	
	public void dibujar(){
		window1.setVisible(true);
		window1.setEnabled(false);
		this.updateUI();
	}
	
	public void finalizar(){
		window1.setVisible(false);
	}

	public void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		window1 = new JWindow();
		progressBar1 = new JProgressBar();
		label1 = new JLabel();

		//======== window1 ========
		{
			window1.setAlwaysOnTop(true);
			window1.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
			Container window1ContentPane = window1.getContentPane();
			window1ContentPane.setLayout(null);

			//---- progressBar1 ----
			progressBar1.setStringPainted(true);
			window1ContentPane.add(progressBar1);
			progressBar1.setBounds(15, 20, 500, 25);

			//---- label1 ----
			label1.setText("Tarea:");
			window1ContentPane.add(label1);
			label1.setBounds(20, 55, 495, 25);

			window1ContentPane.setPreferredSize(new Dimension(535, 95));
			window1.pack();
			window1.setLocationRelativeTo(window1.getOwner());
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JWindow window1;
	private JProgressBar progressBar1;
	private JLabel label1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
