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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Objeto objeto = null;
	private Clasificador clasificador = null;
	private FrameResultado contenedor = null;
	private List<Clase> clases;
	private Clase noSelect = new Clase("Modificar Clasificación");
 	public ObjetoPanel(Objeto objeto, int nroPanel, Clasificador clasificador, FrameResultado contenedor) {
		initComponents();
		this.contenedor = contenedor;
		
		this.objeto = objeto;
		this.clasificador = clasificador;
		PlanarImage image = JAI.create("fileload", objeto.getPathImage());
		
		this.panelImagen.add(new DisplayJAI(image),BorderLayout.CENTER);
		
		this.labelNombre.setText(objeto.getName());
		this.labelNro.setText(Integer.toString(nroPanel));
		/*
		DefaultTableModel model = (DefaultTableModel) tableRasgos.getModel();
		
		for(int i=0;i<objeto.getRasgos().size();i++){
			RasgoObjeto rasgo = objeto.getRasgos().get(i);
			/*
			model.setValueAt(rasgo.getRasgo().getNombre(), i, 0);
			model.setValueAt(rasgo.getValor(), i, 1);
			
			model.addRow(new Object[]{rasgo.getRasgo().getNombre(),rasgo.getValor()});
		}
		*/
		//---- comboBoxClase ----
		DefaultComboBoxModel claseModel = new DefaultComboBoxModel();
		claseModel.addElement(noSelect);
		ClaseObjeto clase = objeto.getClases().get(0);
		if (clasificador != null){
			clases = clasificador.getClases();
			for(Clase c:clases){
				if(!clase.getClase().equals(c))
					claseModel.addElement(c);
			}
		}

		comboBoxClase.setModel(claseModel);

		
		//this.comboBoxClase.setSelectedItem(noSelect);
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

	private void panelImagenMouseClicked(MouseEvent e) {
		this.contenedor.presentarResultados(objeto);
	}

	private void comboBoxClaseItemStateChanged(ItemEvent e) {
		//System.out.println("cambio:" + e.getID() + "-" +e.toString());
	}

	private void comboBoxClaseActionPerformed(ActionEvent e) {
		System.out.println("cambio2:" + e.getID() + "-" +((Clase)this.comboBoxClase.getSelectedItem()).getNombre());
		this.contenedor.changeObjeto(objeto, ((Clase)this.comboBoxClase.getSelectedItem()));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - seba cola
		panel1 = new JPanel();
		labelNro = new JLabel();
		labelNombre = new JLabel();
		panelImagen = new JPanel();
		panel2 = new JPanel();
		comboBoxClase = new JComboBox();

		//======== this ========
		setBorder(new LineBorder(new Color(153, 204, 255)));

		// JFormDesigner evaluation mark
//		setBorder(new javax.swing.border.CompoundBorder(
//			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
//				"", javax.swing.border.TitledBorder.CENTER,
//				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
//				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

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

		//======== panelImagen ========
		{
			panelImagen.setBorder(UIManager.getBorder("ProgressBar.border"));
			panelImagen.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					panelImagenMouseClicked(e);
				}
			});
			panelImagen.setLayout(new BoxLayout(panelImagen, BoxLayout.X_AXIS));
		}
		add(panelImagen, BorderLayout.CENTER);

		//======== panel2 ========
		{
			panel2.setLayout(new FlowLayout(FlowLayout.LEFT));

			//---- comboBoxClase ----
			comboBoxClase.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					comboBoxClaseItemStateChanged(e);
				}
			});
			comboBoxClase.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					comboBoxClaseActionPerformed(e);
				}
			});
			panel2.add(comboBoxClase);
		}
		add(panel2, BorderLayout.SOUTH);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - seba cola
	private JPanel panel1;
	private JLabel labelNro;
	private JLabel labelNombre;
	private JPanel panelImagen;
	private JPanel panel2;
	private JComboBox comboBoxClase;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
