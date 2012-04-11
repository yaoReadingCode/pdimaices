/*
 * Created by JFormDesigner on Sun Apr 18 10:32:30 VET 2010
 */

package aplicarFiltros;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.Dialog.ModalExclusionType;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author seba
 */
public class Progress extends JPanel {
	static private String newline = "\n";
	
	private Component focusOwner;
	
	public Progress(Window owner) {
		this.owner = owner;
		setFocusOwner(owner);
		initComponents();
	}
	public void aumentar(int valor, String texto){
		int v = valor + progressBar1.getValue();
		if(v > 100){
			window1.setVisible(false);
		}else{
			progressBar1.setValue(v);
			label1.setText("Tarea: " + texto);
			//textAreaInfo.setText("");
			progressBar1.invalidate();
			progressBar1.repaint();

		}
		System.out.println("Es visible: " +window1.isVisible());
	}

	public void inicializarProgressBar(){
		progressBar1.setValue(0);
		label1.setText("Tarea:");
		textAreaInfo.setText("");
		owner.setEnabled(false);
	}
	
	public void dibujar(){
		window1.setVisible(true);
		window1.setEnabled(false);
		this.updateUI();
	}
	
	public void finalizar(){
		window1.setVisible(false);
		owner.setEnabled(true);
		if (getFocusOwner() != null)
			getFocusOwner().requestFocus();
	}
	
	/**
	 * Agrega un texto a visualizar en el log
	 */
	public void addLogInfo(String texto){
		textAreaInfo.append(texto + newline);
		textAreaInfo.setCaretPosition(textAreaInfo.getText().length());

	}
	
	public Component getFocusOwner() {
		return focusOwner;
	}
	public void setFocusOwner(Component focusOwner) {
		this.focusOwner = focusOwner;
	}
	
	public void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
		window1 = new JDialog(owner);
		
		progressBar1 = new JProgressBar();
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		textAreaInfo = new JTextArea();

		//======== window1 ========
		{
			//window1.setAlwaysOnTop(true);
			window1.setTitle("Progreso");
			//window1.setModal(true);
			window1.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
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

			//======== scrollPane1 ========
			{

				//---- textAreaInfo ----
				textAreaInfo.setRows(2);
				textAreaInfo.setEditable(false);
				scrollPane1.setViewportView(textAreaInfo);
			}
			window1ContentPane.add(scrollPane1);
			scrollPane1.setBounds(20, 80, 495, scrollPane1.getPreferredSize().height);

			window1ContentPane.setPreferredSize(new Dimension(535, 130));
			window1.pack();
			window1.setLocationRelativeTo(window1.getOwner());
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
	private JDialog window1;
	private JProgressBar progressBar1;
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JTextArea textAreaInfo;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	private Window owner;
}
